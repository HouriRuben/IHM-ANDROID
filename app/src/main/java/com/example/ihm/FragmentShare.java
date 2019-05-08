package com.example.ihm;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.app.Activity.RESULT_OK;

public class FragmentShare extends Fragment {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    EditText Destinataire;
    Button Partager;

    BluetoothAdapter bluetoothAdapter;
    int REQUEST_ENABLE_BT = 1;


    public void handleBluetoothActivation(){
        int REQUEST_ENABLE_BT = 0;
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

       final View view = inflater.inflate(R.layout.fragment_share, container, false);
       Destinataire = view.findViewById(R.id.destinataire);
       Partager = view.findViewById(R.id.sharebutton);

       //Bouton Partager
       Partager.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               sendMail();
           }
       });






       return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceBundle){
        super.onActivityCreated(savedInstanceBundle);
        Button bluetoothButton = getActivity().findViewById(R.id.buttonBluetoothShare);
        bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!bluetoothAdapter.isEnabled()){
                    handleBluetoothActivation();
                }
            }
        });
        if(this.bluetoothAdapter == null){
            bluetoothButton.setEnabled(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_ENABLE_BT){
            if(resultCode == RESULT_OK){
                System.out.println("OK");
            }
        }
    }


    private void sendMail() {
        String NewLine = System.getProperty("line.separator");

        String destinataires = Destinataire.getText().toString();
        String[] destinatairesliste = destinataires.split(",");

        String Objet = " Je souhaite partager mes repas avec toi !";
        String Message = " Salut , J'utilise l'application Menu Management pour planifier mes repas."
                +NewLine+" Pour cela , il te suffit de copier mon ID et de le coller dans l'Onglet Recevoir. "
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
