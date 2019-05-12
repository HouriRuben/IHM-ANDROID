package com.example.ihm;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class TwitterFragment extends Fragment {

    WebView twitterFlux;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_twitter, container, false);
        twitterFlux = (WebView) view.findViewById(R.id.twitterFlux);

        twitterFlux.loadUrl("https://twitter.com/recettesduqc");

        twitterFlux.setWebViewClient(new WebViewClient());

        WebSettings webSettings = twitterFlux.getSettings();

        webSettings.setJavaScriptEnabled(true);

        return view;
    }

}
