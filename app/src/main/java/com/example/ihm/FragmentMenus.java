package com.example.ihm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;


public class FragmentMenus extends Fragment implements OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_menus, container, false);
        Button createmenubutton = (Button) view.findViewById(R.id.createmenubutton);
        createmenubutton.setOnClickListener(this);
        return view;

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getContext(), CreateMenuActivity.class);
        startActivity(intent);
    }
}
