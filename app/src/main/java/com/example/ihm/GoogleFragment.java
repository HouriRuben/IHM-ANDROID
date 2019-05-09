package com.example.ihm;


import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.v4.app.Fragment;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;




public class GoogleFragment extends Fragment {
    ImageView Profil;
    TextView Name;
    Button Deconnecter;

    //firebase
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_google, container, false);
                Profil = (ImageView) view.findViewById(R.id.imageView3);
                Name = (TextView) view.findViewById(R.id.Name);
                assert user != null;
                Name.setText(user.getDisplayName());
                displayImage(user.getPhotoUrl(),view);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mAuth = FirebaseAuth.getInstance();
        if (getContext() != null ){
            mGoogleSignInClient = GoogleSignIn.getClient(getContext(),gso);
        }

        Deconnecter = (Button) view.findViewById(R.id.disconnectButton);
        Deconnecter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Firebase sign out
                mAuth.signOut();

                signOut();
            }
        });



        return view;
    }

    private void displayImage(Uri photoUrl, View view) {
        new DownloadImageTask( (ImageView) view.findViewById(R.id.imageView3) ).execute(photoUrl.toString().replace("s96-c", "s384-c"));
    }

    private void signOut() {
        if(getActivity() != null) {
            mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(),
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            updateUI(null);
                        }
                    });
        }
    }

    private void updateUI(Object o) {
        getActivity().finish();
    }



}
