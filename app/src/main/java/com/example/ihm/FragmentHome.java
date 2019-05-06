package com.example.ihm;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentHome.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentHome#newInstance} factory method to
 * create an instance of this fragment.
 */

import android.content.Intent;
import android.os.AsyncTask;

import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.ListView;
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


public class FragmentHome extends Fragment implements OnClickListener {
    ListView mListView;
    ArrayList<MenuPlanified> list = new ArrayList<>();
    ArrayList<String> listshare = new ArrayList<>();

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        Button createrepasbutton = (Button) view.findViewById(R.id.createrepasbutton);
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
                                    if (getActivity() != null) {
                                        mListView = (ListView) view.findViewById(R.id.listrepas);
                                        MenuPlanifiedAdapter repasAdapter = new MenuPlanifiedAdapter(getActivity(), list);
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
        createrepasbutton.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getContext(), CreateRepasActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu_menus, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int selectedItemId = (int) info.id;
        switch (item.getItemId()) {
            case R.id.cancel:
                return false;
            case R.id.delete:
                MenuPlanified suppressingMenu = list.get(selectedItemId);
                list.remove(suppressingMenu);
                String jsonResult = gson.toJson(list, listType);
                if (user != null) {
                    database.child(user.getUid()).setValue(jsonResult);
                }
                mListView.invalidateViews();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}