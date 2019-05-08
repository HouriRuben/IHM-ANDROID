package com.example.ihm;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.List;

import static android.app.Activity.RESULT_OK;



public class ReceveFragment extends Fragment {
    EditText Destinataire;
    EditText ID;
    Button Partager;
    Button Recevoir;
    List<String> list2 = new ArrayList<>();

    // Firebase
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference database = FirebaseDatabase.getInstance().getReference("partage");
    ArrayList<String> list;
    final Type listType = new TypeToken<ArrayList<String>>(){}.getType();
    final Gson gson = new GsonBuilder()
            .serializeNulls()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();
    private ListView mainListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        final View view = inflater.inflate(R.layout.fragment_receve, container, false);
        Destinataire = view.findViewById(R.id.destinataire);
        ID = view.findViewById(R.id.shareid);
        Partager = view.findViewById(R.id.sharebutton);

        if (user != null ) {
            database.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String json = dataSnapshot.getValue(String.class);
                    if (json != null) {
                        list = gson.fromJson(json, listType);

                        mainListView = (ListView) view.findViewById( R.id.sharelist );

                        if (view.getContext() != null){
                            ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, android.R.id.text1,list);
                            mainListView.setAdapter( listAdapter );
                        }
                        registerForContextMenu(mainListView);

                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("Firebase read ", "Failed to read value.", error.toException());
                }
            });
        }

        //Bouton Recevoir
        Recevoir = view.findViewById(R.id.getidbutton);
        Recevoir.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void  onClick(View v)
            {
                addIDToGetList(v);
            }       });






        return view;
    }

    private void addIDToGetList(View v) {

        if ( ID.getText().toString().length() == 28 && ID.getText().toString() != null) {
            String newId = ID.getText().toString();
            list2.add(newId);

            if(list != null) {
                list2.addAll(list);
            }

        } else {
            Toast.makeText(getContext()," Format Identifiant non reconnu ",Toast.LENGTH_SHORT).show();
            ID.getText().clear();
        }


        String jsonResult = gson.toJson(list2,listType);

        if (user != null ) {
            database.child(user.getUid()).setValue(jsonResult);

        }

        ID.getText().clear();


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
                String suppressingID = list.get(selectedItemId);
                list2.remove(suppressingID);
                String jsonResult = gson.toJson(list2, listType);
                if (user != null) {
                    database.child(user.getUid()).setValue(jsonResult);
                }
                mainListView.invalidateViews();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


}


