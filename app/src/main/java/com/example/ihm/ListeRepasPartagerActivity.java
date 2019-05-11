package com.example.ihm;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

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
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class ListeRepasPartagerActivity extends AppCompatActivity {
    ListView mListView;
    ArrayList<MenuPlanified> list = new ArrayList<>();
    ArrayList<String> listshare = new ArrayList<>();
    Context context;

    // Firebase
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference database = FirebaseDatabase.getInstance().getReference("repas");
    DatabaseReference databaseshare = FirebaseDatabase.getInstance().getReference("partage");

    //Json
    final Type listType = new TypeToken<ArrayList<MenuPlanified>>(){}.getType();
    final Type listTypeShare = new TypeToken<ArrayList<String>>(){}.getType();
    final Gson gson = new GsonBuilder()
            .serializeNulls()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_repas_partager);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;
        final View rootView = getWindow().getDecorView().getRootView();
        if (user != null ) {
            databaseshare.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String json = dataSnapshot.getValue(String.class);
                    if (json != null) {
                        // Make the great list
                        listshare = gson.fromJson(json, listTypeShare);
                    } else {
                        listshare = new ArrayList<>();
                    }
                    listshare.add(user.getUid());

                    list = new ArrayList<>();
                    for (int i = 0 ;i< listshare.size();i++) {
                        database.child(listshare.get(i)).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final String json = dataSnapshot.getValue(String.class);
                                if (json != null) {
                                    // Make the great list
                                    List<MenuPlanified> listtamp = gson.fromJson(json, listType);
                                    list.addAll(listtamp);
                                    Collections.sort(list, new dateComparator());
                                    list = startListToday(list);

                                    // Prompt the great list
                                    if (context != null) {
                                        mListView = (ListView)rootView.findViewById(R.id.listmeandthem);
                                        MenuPlanifiedAdapter repasAdapter = new MenuPlanifiedAdapter(context, list);
                                        mListView.setAdapter(repasAdapter);

                                    }
                                    registerForContextMenu(mListView);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                                Log.w("Firebase read ", "Failed to read value.", error.toException());
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("Firebase read ", "Failed to read value.", error.toException());
                }
            });





        }
    }

    private ArrayList<MenuPlanified> startListToday(ArrayList<MenuPlanified> list) {
        Calendar tamp = Calendar.getInstance();
        tamp.add(Calendar.MONTH,1);
        int year = tamp.get(Calendar.YEAR);
        int month = tamp.get(Calendar.MONTH);
        int day = tamp.get(Calendar.DAY_OF_MONTH);
        int Current = (year*2000)+(month*100)+day;


        ArrayList<MenuPlanified> newList = new ArrayList<>();
        for (int i = 0;i<list.size();i++){
            if (list.get(i).dateCompare()>= Current){
                newList.add(list.get(i));
            }
        }

        return newList;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
