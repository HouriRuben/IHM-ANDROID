package com.example.ihm;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View.OnClickListener;
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


public class FragmentMenus extends Fragment implements OnClickListener {
    ListView mListView;
    ArrayList<Menu> list = new ArrayList<>();
    // Firebase
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference database = FirebaseDatabase.getInstance().getReference("menus");
    //Json
    final Type listType = new TypeToken<ArrayList<Menu>>(){}.getType();
    final Gson gson = new GsonBuilder()
            .serializeNulls()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View view = inflater.inflate(R.layout.fragment_menus, container, false);
        Button createmenubutton = (Button) view.findViewById(R.id.createmenubutton);
        if (user != null ) {
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
                                mListView = (ListView) view.findViewById(R.id.listmenus);
                                MenuAdapter menuAdapter = new MenuAdapter(getActivity(), list);
                                mListView.setAdapter(menuAdapter);
                                System.out.println("CA MARCHE : " + list);
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
        createmenubutton.setOnClickListener(this);
        return view;

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getContext(), CreateMenuActivity.class);
        startActivity(intent);
    }
}
