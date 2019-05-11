package com.example.ihm;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class GoogleCalendarUtils {
    public static final String[] EVENT_PROJECTION = new String[]{
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
    };
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

    private static int calID;
    private static final List<ContentValues> contentValuesList = new ArrayList<>();
    private static String displayName;
    private static String accountName;
    private static String ownerName;

    public static final String SHARED_PREFS = "sharedPrefs";
    private static List<Long> oldEventsIDs;
    private static List<Long> newEventsIDs;
    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;
    private static Context callerCtx;

    public static void getDataFromCalendarTable(View view, String email) {
        Cursor cur = null;
        ContentResolver cr = callerCtx.getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";
        String[] selectionArgs = new String[]{email, "com.google", email};

        cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);

        while (cur.moveToNext()) {
            calID = cur.getInt(PROJECTION_ID_INDEX);
            displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
            accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

            /*TextView tv2 = (TextView) view.findViewById(R.id.textView2);
            tv2.setText(tv2.getText() + " calId " + calID + "\n" +
                            "displayName " + displayName + "\n" +
                            "accountName " + accountName + "\n" +
                            "ownerName " + ownerName);*/
        }
        cur.close();
    }

    public static void createDataToSend(List<MenuPlanified> list) {
        long startMillis = 0;
        long endMillis = 0;
        Calendar beginTime;
        Calendar endTime;
        beginTime = Calendar.getInstance();
        endTime = Calendar.getInstance();


        if (list.size() > 0) {
            contentValuesList.clear();
            for (int i = 0; i < list.size(); i++) {
                Log.i("list", list.get(i).toString());
            }
            for (int i = 0; i < list.size(); i++) {
                MenuPlanified elt = list.get(i);

                beginTime.set(elt.getYear(), elt.getMonth()-1, elt.getDay(), 12, 0);
                startMillis = beginTime.getTimeInMillis();
                endTime.set(elt.getYear(), elt.getMonth()-1, elt.getDay(), 14, 0);
                endMillis = endTime.getTimeInMillis();
                ContentValues values = new ContentValues();
                values.put(CalendarContract.Events.DTSTART, startMillis);
                values.put(CalendarContract.Events.DTEND, endMillis);
                values.put(CalendarContract.Events.TITLE, elt.getNomMenu());
                values.put(CalendarContract.Events.DESCRIPTION,
                        "Entrée: " + elt.getEntree() + "\n" +
                                "Plat: " + elt.getPlat() + "\n" +
                                "Dessert: " + elt.getDessert() + "\n" +
                                "Prix: " + elt.getPrix() + "\n" +
                                "Calories: " + elt.getCalories());
                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/France");
                contentValuesList.add(values);
            }
            for (int i = 0; i < contentValuesList.size(); i++) {
                Log.i("contentValuesList", contentValuesList.get(i).toString());
            }
        }
    }

    public static void init(Context ctx) {
        callerCtx = ctx;
        prefs = callerCtx.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        editor = prefs.edit();
        retrieveOldEventsIDsFromSharedPrefs();
        newEventsIDs = new ArrayList<>();
    }

    public static void retrieveOldEventsIDsFromSharedPrefs() {
        oldEventsIDs = new ArrayList<Long>();
        try {
            Set<String> temp = prefs.getStringSet("oldEventsIDs", null);
            List<String> oldEventsString = new ArrayList<>();
            oldEventsString.addAll(temp);

            for (int i = 0; i < oldEventsString.size(); i++) {
                oldEventsIDs.add(Long.parseLong(oldEventsString.get(i)));
            }
            for (int i = 0; i < oldEventsIDs.size(); i++) {
                Log.i("##2 DEBUG", oldEventsIDs.get(i).toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void deleteOldEventsInCalendar() {
        String DEBUG_TAG = "##1deleteOldEvents";
        if (oldEventsIDs.size() > 0) {
            Uri deleteUri = null;
            /*ContentResolver cr = getContentResolver();
            ContentValues values = new ContentValues();*/

            for (int i = 0; i < oldEventsIDs.size(); i++) {
                deleteUri = ContentUris.withAppendedId(
                        CalendarContract.Events.CONTENT_URI,
                        oldEventsIDs.get(i));
                int rows = callerCtx.getContentResolver().delete(
                        deleteUri,
                        null,
                        null);
                Log.i(DEBUG_TAG, oldEventsIDs.get(i).toString());
            }

            Log.i(DEBUG_TAG, "Events deleted: " + oldEventsIDs.size());
            oldEventsIDs.clear();

            editor.putStringSet("oldEventsIDs", new HashSet<String>());
            editor.commit();
        } else
            Log.i(DEBUG_TAG, "Events deleted: " + oldEventsIDs.size());
    }

    public static void setNewValueInSharedPrefs() {
        Set<String> setToStore;
        List<String> temp = new ArrayList<>();

        for (int i = 0; i < newEventsIDs.size(); i++){
            temp.add(newEventsIDs.get(i).toString());
        }
        setToStore = new HashSet<>(temp);
        editor.putStringSet("oldEventsIDs", setToStore);
        editor.commit();
    }

    public static void mainProcess(View v) {
        Uri uri;
        Long eventID;

        //delete old events in calendar and sets list to empty list in shared preferences
        deleteOldEventsInCalendar();

        ContentResolver cr = callerCtx.getContentResolver();
        for (int i = 0; i < contentValuesList.size(); i++) {
            uri = cr.insert(
                    CalendarContract.Events.CONTENT_URI,
                    (ContentValues) contentValuesList.get(i));
            eventID = Long.parseLong(uri.getLastPathSegment());
            newEventsIDs.add(eventID);
            Log.i("##2 INSERTED:", eventID.toString());
        }

        //set list value to newEventsIDsList in shared preferences
        setNewValueInSharedPrefs();
        for (int i = 0; i < newEventsIDs.size(); i++){
            oldEventsIDs.add(newEventsIDs.get(i));
        }
        newEventsIDs.clear();



        ((TextView) v.findViewById(R.id.resultSendCalendar)).setText("Repas planifiés envoyés au Calendar :)");
    }
}
