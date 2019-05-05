package com.example.ihm;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;


public class CreateRepasActivity extends AppCompatActivity implements View.OnClickListener {
    ArrayList<Menu> list = new ArrayList<>();
    ArrayList<MenuPlanified> repaslist = new ArrayList<>();
    // Firebase
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference database = FirebaseDatabase.getInstance().getReference("menus");
    DatabaseReference databaserepas = FirebaseDatabase.getInstance().getReference("repas");
    //Json
    final Type listType = new TypeToken<ArrayList<Menu>>(){}.getType();
    final Type repasListType = new TypeToken<ArrayList<MenuPlanified>>(){}.getType();
    final Gson gson = new GsonBuilder()
            .serializeNulls()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();
    EasyDate date;
    Menu clickedItem;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_repas);
        context = this;
        Button nouveaumenubutton = (Button) findViewById(R.id.nouveaumenubutton);
        Button validerbutton = (Button) findViewById(R.id.validerbutton);
        nouveaumenubutton.setOnClickListener(this);
        validerbutton.setOnClickListener(this);

        // Calendar
        Date tamp = new Date();
        date = new EasyDate(tamp.getDay(),tamp.getMonth(),tamp.getYear());
        CalendarView calendar = (CalendarView) findViewById(R.id.simpleCalendarView);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                date = new EasyDate(dayOfMonth,month+1, year);

            }
        });

        // FireBase
        if (user != null ) {

            // Récuperer les Menu
            database.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String json = dataSnapshot.getValue(String.class);

                    if (json != null) {

                        new AsyncTask<Void, Void, ArrayList<Menu>>() {
                            @Override
                            protected ArrayList<Menu> doInBackground(Void... params) {
                                return gson.fromJson(json, listType);
                            }

                            @Override
                            protected void onPostExecute(ArrayList<Menu> result) {
                                list = result;
                                System.out.println("CA MARCHE : " + list);
                                Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);
                                MenuAdapter menuAdapter = new MenuAdapter(context, list);
                                MenuItemSpinnerAdapter spinnerAdapter = new MenuItemSpinnerAdapter(context,list);
                                spinner1.setAdapter(spinnerAdapter);
                                spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        clickedItem = (Menu) parent.getItemAtPosition(position);
                                        String clickedCountryName = clickedItem.getNomMenu();
                                        Toast.makeText(context, clickedCountryName + " selected", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                            }

                            @Override
                            protected void onPreExecute() {
                            }

                            @Override
                            protected void onProgressUpdate(Void... values) {
                            }
                        }.execute();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("Firebase read ", "Failed to read value.", error.toException());
                }
            });

            // Récuperer les menus planifier ( repas )
            databaserepas.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String jsonrepas = dataSnapshot.getValue(String.class);
                    if (jsonrepas != null) {
                        repaslist = gson.fromJson(jsonrepas, repasListType);
                    }


                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("Firebase read ", "Failed to read value.", error.toException());
                }
            });
        }
        // fin du if ( user != null )


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.validerbutton:
                if (clickedItem == null) {
                    Toast.makeText(getApplicationContext(), "Choisisez le Menu ", Toast.LENGTH_SHORT).show();
                } else {
                    MenuPlanified menuPlanified = new MenuPlanified(clickedItem.getNomMenu(),
                            clickedItem.getEntree(),
                            clickedItem.getPlat(),
                            clickedItem.getDessert(),
                            clickedItem.getPrix(),
                            clickedItem.getCalories(),
                            date.getYear(),
                            date.getMonth(),
                            date.getDay());
                    repaslist.add(menuPlanified);
                    String jsonResult = gson.toJson(repaslist,repasListType);
                    if (user != null ) {
                        databaserepas.child(user.getUid()).setValue(jsonResult);

                    }

                    Toast.makeText(getApplicationContext(), "Json : " + jsonResult, Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;

            case R.id.nouveaumenubutton:
                Intent intent = new Intent(context, CreateMenuActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }
}
