package com.example.ihm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
                    Toast.makeText(getApplicationContext(), "Titre -  " + Titre.getText().toString() + " \n" + "Entrée -  " + Entrée.getText().toString()
                            + " \n" + "Plat -  " + Plat.getText().toString() + " \n" + "Dessert -  " + Dessert.getText().toString()
                            + " \n" + "Prix -  " + Prix.getText().toString() + " \n" + "Calories -  " + Calories.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
