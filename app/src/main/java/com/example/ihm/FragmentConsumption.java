package com.example.ihm;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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

public class FragmentConsumption extends Fragment {
    ArrayList<MenuPlanified> list = new ArrayList<>();

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View view = inflater.inflate(R.layout.fragment_consumption, container, false);
        if (user != null ) {
            database.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String json = dataSnapshot.getValue(String.class);
                    if (json != null) {
                        new AsyncTask<Void, Void, ArrayList<MenuPlanified>>() {
                            @Override
                            protected ArrayList<MenuPlanified> doInBackground(Void... params) {
                                return gson.fromJson(json, listType);
                            }

                            @Override
                            protected void onPostExecute(ArrayList<MenuPlanified> result) {
                                list = result;
                                Collections.sort(list, new dateComparator());
                                CreateChartEntree(view);
                                CreateChartPlat(view);
                                CreateChartDesert(view);
                                MenuPlanifiedAdapter repasAdapter = new MenuPlanifiedAdapter(getActivity(), list);

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
        }
        return view;
    }

    private void CreateChartEntree( View v) {
        Map<String,Integer> entreemap = new HashMap<>();
        for (int i = 0 ; i<list.size(); i++){

            if (entreemap.containsKey(list.get(i).getEntree())){
                entreemap.put(list.get(i).getEntree(),entreemap.get(list.get(i).getEntree())+1);
            } else {
                entreemap.put(list.get(i).getEntree(),1);
            }
        }

        List<PieEntry> pieEntries = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : entreemap.entrySet()) {
            pieEntries.add(new PieEntry(entry.getValue(),entry.getKey()));
        }
        PieDataSet dataSet = new PieDataSet(pieEntries, "Consommation des Entrées");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData data = new PieData(dataSet);


        PieChart chart = (PieChart) v.findViewById(R.id.chart);
        chart.setData(data);
        chart.invalidate();
    }
    private void CreateChartPlat( View v) {
        Map<String,Integer> platmap = new HashMap<>();
        for (int i = 0 ; i<list.size(); i++){

            if (platmap.containsKey(list.get(i).getPlat())){
                platmap.put(list.get(i).getPlat(),platmap.get(list.get(i).getPlat())+1);
            } else {
                platmap.put(list.get(i).getPlat(),1);
            }
        }

        List<PieEntry> pieEntries = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : platmap.entrySet()) {
            pieEntries.add(new PieEntry(entry.getValue(),entry.getKey()));
        }
        PieDataSet dataSet = new PieDataSet(pieEntries, "Consommation des Plats");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data = new PieData(dataSet);


        PieChart chart = (PieChart) v.findViewById(R.id.chart2);
        chart.setData(data);
        chart.invalidate();
    }

    private void CreateChartDesert( View v) {
        Map<String,Integer> dessertmap = new HashMap<>();
        for (int i = 0 ; i<list.size(); i++){

            if (dessertmap.containsKey(list.get(i).getDessert())){
                dessertmap.put(list.get(i).getDessert(),dessertmap.get(list.get(i).getDessert())+1);
            } else {
               dessertmap.put(list.get(i).getDessert(),1);
            }
        }

        List<PieEntry> pieEntries = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : dessertmap.entrySet()) {
            pieEntries.add(new PieEntry(entry.getValue(),entry.getKey()));
        }
        PieDataSet dataSet = new PieDataSet(pieEntries, "Consommation des Desserts");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        PieData data = new PieData(dataSet);


        PieChart chart = (PieChart) v.findViewById(R.id.chart3);
        chart.setData(data);
        chart.invalidate();
    }
}
