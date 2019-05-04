package com.example.ihm;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.ihm.Menu;

import java.util.ArrayList;


public class MenuItemSpinnerAdapter extends ArrayAdapter<Menu> {

    public MenuItemSpinnerAdapter(Context context, ArrayList<Menu> menusList) {
        super(context, 0, menusList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.row_menu, parent, false
            );
        }
        TextView textViewName = convertView.findViewById(R.id.textmenu);
        TextView textViewEntree = convertView.findViewById(R.id.textentree);
        TextView textViewPlat = convertView.findViewById(R.id.textplat);
        TextView textViewDessert = convertView.findViewById(R.id.textdessert);

        Menu currentItem = getItem(position);

        if (currentItem != null) {
            textViewName.setText(currentItem.getNomMenu());
            textViewEntree.setText(currentItem.getEntree());
            textViewPlat.setText(currentItem.getPlat());
            textViewDessert.setText(currentItem.getDessert());
        }

        return convertView;
    }
}