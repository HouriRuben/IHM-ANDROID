package com.example.ihm;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;


public class TwitterFragment extends Fragment {

    WebView twitterFlux;
    ImageView noInternet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_twitter, container, false);
        if( haveInternetConnection()) {
        twitterFlux = (WebView) view.findViewById(R.id.twitterFlux);

        twitterFlux.loadUrl("https://twitter.com/Cuisineetmets");

        twitterFlux.setWebViewClient(new WebViewClient());

        WebSettings webSettings = twitterFlux.getSettings();

        webSettings.setJavaScriptEnabled(true);


    } else {

            noInternet = (ImageView) view.findViewById(R.id.noInternet);
            noInternet.setImageResource(R.drawable.pasinternet);

        }
        return view;
    }


    private boolean haveInternetConnection() {
        // Fonction haveInternetConnection : return true si connecté, return false dans le cas contraire
        NetworkInfo network = ((ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if (network == null || !network.isConnected()) {
            // Le périphérique n'est pas connecté à Internet
            return false;
        }

            // Le périphérique est connecté à Internet
            return true;
    }

}
