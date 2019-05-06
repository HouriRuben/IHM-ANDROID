package com.example.ihm;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FragmentShare extends Fragment {
    EditText Destinataire;
    EditText ID;
    Button Partager;
    Button Recevoir;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
       View view = inflater.inflate(R.layout.fragment_share, container, false);
       Destinataire = view.findViewById(R.id.destinataire);
       ID = view.findViewById(R.id.shareid);
       Partager = view.findViewById(R.id.sharebutton);

       //Bouton Partager
       Partager.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               sendMail();
           }
       });
       //Bouton Recevoir
       Recevoir = view.findViewById(R.id.getidbutton);
       Recevoir.setOnClickListener(new View.OnClickListener(){
           @Override
           public  void  onClick(View v)
           {
               addIDToGetList(v);
           }       });

        if (user != null ) {
            database.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String json = dataSnapshot.getValue(String.class);
                    if (json != null) {
                        new AsyncTask<Void, Void, ArrayList<String>>() {
                            @Override
                            protected ArrayList<String> doInBackground(Void... params) {
                                return gson.fromJson(json, listType);
                            }

                            @Override
                            protected void onPostExecute(ArrayList<String> result) {
                                list = result;

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

    private void addIDToGetList(View v) {
        String newId = ID.getText().toString();
        List<String> list2 = new ArrayList<>();
        if(list != null) {
            list2.addAll(list);
        }
        list2.add(newId);

        String jsonResult = gson.toJson(list2,listType);
        if (user != null ) {
            database.child(user.getUid()).setValue(jsonResult);

        }
        ID.getText().clear();

//        mainListView = (ListView) v.findViewById( R.id.sharelist );

//        if (getActivity() != null){
//            ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list2);

//            mainListView.setAdapter( listAdapter );
//        }



    }

    private void sendMail() {
        String NewLine = System.getProperty("line.separator");

        String destinataires = Destinataire.getText().toString();
        String[] destinatairesliste = destinataires.split(",");

        String Objet = " Je souhaite partager mes repas avec toi !";
        String Message = " Salut , J'utilise l'application Menu Management pour planifier mes repas."
                +NewLine+" Pour cela , il te suffit de copier mon ID et de le coller dans la section Recevoir de l'onglet Share "
                +NewLine+" Mon ID : " + user.getUid()
                +NewLine+NewLine+user.getDisplayName();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL,destinatairesliste);
        intent.putExtra(Intent.EXTRA_SUBJECT,Objet);
        intent.putExtra(Intent.EXTRA_TEXT,Message);

        intent.setType("message/rcf822");
        startActivity(Intent.createChooser(intent,"Choisir l'Application pour l'envoi :"));

    }

}
