package com.example.ihm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CreateMenuActivity extends AppCompatActivity {

    Button Valider;
    EditText Titre, Entrée, Plat, Dessert, Prix, Calories;

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

        Valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Titre.getText().toString().isEmpty() || Entrée.getText().toString().isEmpty() || Plat.getText().toString().isEmpty() || Dessert.getText().toString().isEmpty()
                        || Prix.getText().toString().isEmpty()|| Calories.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter the Data", Toast.LENGTH_SHORT).show();
                } else {
                    Menu menu = new Menu(Titre.getText().toString(),Entrée.getText().toString(),Plat.getText().toString(),Dessert.getText().toString(),Double.parseDouble(Calories.getText().toString()),Integer.parseInt(Prix.getText().toString()));
                    final Gson gson = new GsonBuilder().serializeNulls().create();
                    String result = gson.toJson(menu);
                    Toast.makeText(getApplicationContext(), "Json : " + result, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
