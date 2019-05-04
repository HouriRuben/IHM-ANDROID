package com.example.ihm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class CreateMenuActivity extends AppCompatActivity {

    Button Valider;
    EditText Titre, Entrée, Plat, Dessert, Prix, Calories;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_menu);
        Titre = (EditText) findViewById(R.id.editTextTitle);
        Entrée = (EditText) findViewById(R.id.editTextEntree);
        Plat = (EditText) findViewById(R.id.editTextPlat);
        Dessert = (EditText) findViewById(R.id.editTextDessert);
        Prix = (EditText) findViewById(R.id.editTextPrix);
        Calories = (EditText) findViewById(R.id.editTextCalories);
        Valider = (Button) findViewById(R.id.button);

        if (user != null) {

            database.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String json = dataSnapshot.getValue(String.class);
                    if (json != null) {
                        list = gson.fromJson(json, listType);
                    }


                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("Firebase read ", "Failed to read value.", error.toException());
                }
            });
        }


        Valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Titre.getText().toString().isEmpty() || Entrée.getText().toString().isEmpty() || Plat.getText().toString().isEmpty() || Dessert.getText().toString().isEmpty()
                        || Prix.getText().toString().isEmpty()|| Calories.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter the Data", Toast.LENGTH_SHORT).show();
                } else {

                    Menu menu = new Menu(Titre.getText().toString(),Entrée.getText().toString(),Plat.getText().toString(),Dessert.getText().toString(),Double.parseDouble(Calories.getText().toString()),Integer.parseInt(Prix.getText().toString()));
                    list.add(menu);
                    String jsonResult = gson.toJson(list, listType);

                    if (user != null ) {
                        database.child(user.getUid()).setValue(jsonResult);

                    }

                    Toast.makeText(getApplicationContext(), "Json : " + jsonResult, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

}
