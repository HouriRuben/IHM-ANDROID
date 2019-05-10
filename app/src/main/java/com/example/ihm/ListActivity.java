package com.example.ihm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    int date1str;
    int date2str;
    int tamp;

    //PDF



    ArrayList<MenuPlanified> list = new ArrayList<>();
    ArrayList<String> listshare = new ArrayList<>();
    Context context;

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
    private String dest;
    Button Valider;
    private static final int STORAGE_CODE = 1000;
    private ImageView LISTECOURSE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Valider = (Button) findViewById(R.id.button);

        LISTECOURSE = (findViewById(R.id.imageView4));
        LISTECOURSE.setImageResource(R.drawable.listedecourse);

        Intent intent = getIntent();
        date1str = intent.getIntExtra("firstdate", 0);
        date2str = intent.getIntExtra("seconddate", 0);


        Valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                        tamp = i ;
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
                                                    Log.d("LIST", list.toString());

                                                }
                                                if (tamp == listshare.size()-1){
                                                    generatePdf(tamp, intervalDate(list));
                                                    Toast.makeText(context,"List.pdf sauvegarder au chemin : "+dest,Toast.LENGTH_SHORT).show();
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
                }
        });
    }

    private void generatePdf(int tamp, ArrayList<MenuPlanified> menuPlanifieds) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions,STORAGE_CODE);
            } else {
                dest = Environment.getExternalStorageDirectory()+"/"+"list.pdf";
                Log.d("DOC",dest);


                try {
                    Document document = new Document();
                    PdfWriter.getInstance(document, new FileOutputStream(dest));
                    document.open();
                    document.addAuthor(user.getDisplayName());
                    document.add(new Paragraph("LISTE DE COURSE"));
                    document.add(new Paragraph("Nombre D'Utilisateurs : "+ Integer.toString(tamp+1)));
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph("Entrée :"));
                    for (int i =0;i<menuPlanifieds.size();i++){
                        document.add(new Paragraph(menuPlanifieds.get(i).getEntree()));
                    }
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph("Plats :"));
                    for (int i =0;i<menuPlanifieds.size();i++){
                        document.add(new Paragraph(menuPlanifieds.get(i).getPlat()));
                    }
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph("Desserts : "));
                    for (int i =0;i<menuPlanifieds.size();i++){
                        document.add(new Paragraph(menuPlanifieds.get(i).getDessert()));
                    }
                    Log.d("DOC",dest);
                    document.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
        } else {
            dest = Environment.getExternalStorageDirectory()+"/"+"list.pdf";

            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(dest));
                document.open();
                document.add(new Paragraph("LISTE DE COURSE"));
                document.addAuthor(user.getDisplayName());
                document.add(new Paragraph("Nombre D'Utilisateurs : "+ Integer.toString(tamp+1)));
                document.add(new Paragraph(" "));
                document.add(new Paragraph("Entrée :"));
                for (int i =0;i<menuPlanifieds.size();i++){
                    document.add(new Paragraph(menuPlanifieds.get(i).getEntree()));
                }
                document.add(new Paragraph(" "));
                document.add(new Paragraph("Plats :"));
                for (int i =0;i<menuPlanifieds.size();i++){
                    document.add(new Paragraph(menuPlanifieds.get(i).getPlat()));
                }
                document.add(new Paragraph(" "));
                document.add(new Paragraph("Desserts : "));
                for (int i =0;i<menuPlanifieds.size();i++){
                    document.add(new Paragraph(menuPlanifieds.get(i).getDessert()));
                }
                Log.d("DOC",dest);
                document.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }



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

    private ArrayList<MenuPlanified> intervalDate(ArrayList<MenuPlanified> list) {
        ArrayList<MenuPlanified> listinterval = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).dateCompare() >= date1str && list.get(i).dateCompare() <= date2str) {
                listinterval.add(list.get(i));
            }
        }
        return listinterval;
    }

    private void sendMail() {
        String NewLine = System.getProperty("line.separator");

        String destinataire = user.getEmail();

        String Objet = "Liste de Course";
        String Message = "Ci-Joint un pdf de la liste de course";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("vnd.android.cursor.dir/email");
        intent.putExtra(Intent.EXTRA_EMAIL,destinataire);
        intent.putExtra(Intent.EXTRA_SUBJECT,Objet);
        intent.putExtra(Intent.EXTRA_TEXT,Message);
        //intent.setType("message/rcf822");
        startActivity(Intent.createChooser(intent,"Choisir l'Application pour l'envoi :"));

    }



}