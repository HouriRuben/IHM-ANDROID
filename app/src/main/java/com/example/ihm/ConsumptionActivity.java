package com.example.ihm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsumptionActivity extends AppCompatActivity {
    Double Prixdb;
    int Kcalint;
    TextView Kcal;
    TextView Prix;
    int date1str;
    int date2str;

    ArrayList<MenuPlanified> list = new ArrayList<>();
    ArrayList<MenuPlanified> listinterval = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumption);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Kcal = (TextView) findViewById(R.id.textView);
        Prix = (TextView) findViewById(R.id.textView2);

        Intent intent = getIntent();
        date1str = intent.getIntExtra("firstdate",0);
        date2str = intent.getIntExtra("seconddate",0);



        // Firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("repas");

        //Json
        final Type listType = new TypeToken<ArrayList<MenuPlanified>>(){}.getType();
        final Gson gson = new GsonBuilder()
                .serializeNulls()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create();

            if (user != null ) {
                database.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String json = dataSnapshot.getValue(String.class);
                        if (json != null) {
                            list = gson.fromJson(json, listType);
                            Collections.sort(list, new dateComparator());
                            intervalDate();
                            CreateChartEntree();
                            CreateChartPlat();
                            CreateChartDesert();
                            SetKcal();
                            SetPrix();
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

    private void SetPrix() {
        Prixdb = 0.0;
        for (int i =0;i<listinterval.size();i++){
            Prixdb += listinterval.get(i).getPrix();
        }
        Prix.setText("Total des Coûts : "+ Double.toString(Prixdb) + " €");
    }

    private void SetKcal() {
        for (int i =0;i<listinterval.size();i++){
            Kcalint += listinterval.get(i).getCalories();
        }
        Kcal.setText("Total des Calories : "+ Integer.toString(Kcalint)+ " kcal");
    }

    private void intervalDate() {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).dateCompare() >= date1str && list.get(i).dateCompare() <= date2str) {
                listinterval.add(list.get(i));
            }
        }
    }

    private void CreateChartEntree() {
        if (listinterval != null) {
            Map<String, Integer> entreemap = new HashMap<>();
            for (int i = 0; i < listinterval.size(); i++) {

                if (entreemap.containsKey(listinterval.get(i).getEntree())) {
                    entreemap.put(listinterval.get(i).getEntree(), entreemap.get(listinterval.get(i).getEntree()) + 1);
                } else {
                    entreemap.put(listinterval.get(i).getEntree(), 1);
                }
            }

            List<PieEntry> pieEntries = new ArrayList<>();

            for (Map.Entry<String, Integer> entry : entreemap.entrySet()) {
                pieEntries.add(new PieEntry(entry.getValue(), entry.getKey()));
            }
            PieDataSet dataSet = new PieDataSet(pieEntries, "Consommation des Entrées");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            PieData data = new PieData(dataSet);


            PieChart chart = (PieChart) findViewById(R.id.chart);
            chart.setData(data);
            chart.invalidate();
        }
    }

        private void CreateChartPlat() {
            if (listinterval != null) {

                Map<String, Integer> platmap = new HashMap<>();
                for (int i = 0; i < listinterval.size(); i++) {

                    if (platmap.containsKey(listinterval.get(i).getPlat())) {
                        platmap.put(listinterval.get(i).getPlat(), platmap.get(listinterval.get(i).getPlat()) + 1);
                    } else {
                        platmap.put(listinterval.get(i).getPlat(), 1);
                    }
                }

                List<PieEntry> pieEntries = new ArrayList<>();

                for (Map.Entry<String, Integer> entry : platmap.entrySet()) {
                    pieEntries.add(new PieEntry(entry.getValue(), entry.getKey()));
                }
                PieDataSet dataSet = new PieDataSet(pieEntries, "Consommation des Plats");
                dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                PieData data = new PieData(dataSet);


                PieChart chart = (PieChart) findViewById(R.id.chart2);
                chart.setData(data);
                chart.invalidate();
            }
        }

        private void CreateChartDesert() {
            if (listinterval != null) {
                Map<String, Integer> dessertmap = new HashMap<>();
                for (int i = 0; i < listinterval.size(); i++) {

                    if (dessertmap.containsKey(listinterval.get(i).getDessert())) {
                        dessertmap.put(listinterval.get(i).getDessert(), dessertmap.get(listinterval.get(i).getDessert()) + 1);
                    } else {
                        dessertmap.put(listinterval.get(i).getDessert(), 1);
                    }
                }

                List<PieEntry> pieEntries = new ArrayList<>();

                for (Map.Entry<String, Integer> entry : dessertmap.entrySet()) {
                    pieEntries.add(new PieEntry(entry.getValue(), entry.getKey()));
                }
                PieDataSet dataSet = new PieDataSet(pieEntries, "Consommation des Desserts");
                dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
                PieData data = new PieData(dataSet);


                PieChart chart = (PieChart) findViewById(R.id.chart3);
                chart.setData(data);
                chart.invalidate();
            }
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


