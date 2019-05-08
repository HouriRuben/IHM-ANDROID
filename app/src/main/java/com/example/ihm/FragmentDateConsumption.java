package com.example.ihm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.List;




public class FragmentDateConsumption extends Fragment {
    ArrayList<MenuPlanified> list = new ArrayList<>();
    Context context = getContext();

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
    private ArrayList<String> stringlist;
    private String clickedItem1;
    private String clickedItem2;


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState){
        final View view = inflater.inflate(R.layout.fragment_fragment_date_consumption, container, false);
        Button valider = (Button) view.findViewById(R.id.validerbutton);

        if (user != null ) {

        list = new ArrayList<>();
        database.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String json = dataSnapshot.getValue(String.class);
                if (json != null) {
                    // Make the great list
                    List<MenuPlanified> listtamp = gson.fromJson(json, listType);
                    list.addAll(listtamp);
                    Collections.sort(list, new dateComparator());
                    list = startListToday(list);
                    stringlist = new ArrayList<String>();
                    for (int i = 0; i<list.size();i++){
                        stringlist.add(list.get(i).dateToString());
                    }

                    Spinner spinner1 = (Spinner) view.findViewById(R.id.firstdatespinner);
                    Spinner spinner2 = (Spinner) view.findViewById(R.id.seconddatespinner);
                    if (getActivity() != null ){
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                getActivity(),
                                android.R.layout.simple_spinner_item,
                                stringlist
                        );
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner1.setAdapter(adapter);
                        spinner2.setAdapter(adapter);
                    }


                    spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            clickedItem1 = (String) parent.getItemAtPosition(position);
                            if(context != null){
                                Toast.makeText(context, clickedItem1 + " selected", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            clickedItem2 = (String) parent.getItemAtPosition(position);
                            if(context != null){
                                Toast.makeText(context, clickedItem2 + " selected", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

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
    valider.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), ConsumptionActivity.class);
            String[] tabdate1 = clickedItem1.split("/");
            String[] tabdate2 = clickedItem2.split("/");
            int comparedate1 = (Integer.parseInt(tabdate1[2])*2000)+(Integer.parseInt(tabdate1[1])*100)+Integer.parseInt(tabdate1[0]);
            int comparedate2 = (Integer.parseInt(tabdate2[2])*2000)+(Integer.parseInt(tabdate2[1])*100)+Integer.parseInt(tabdate2[0]);
            if (comparedate2 < comparedate1){
                if(context != null){
                    Toast.makeText(context, "Date 2 < Date 1 : Impossible ", Toast.LENGTH_SHORT).show();
                }
            } else {
                intent.putExtra("firstdate",comparedate1);
                intent.putExtra("seconddate",comparedate2);
                startActivity(intent);
            }


        }
    });
        return view;

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


}
