package com.example.ihm;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MenuAdapter extends ArrayAdapter<Menu> {


    public MenuAdapter(Context context, ArrayList<Menu> menus) {
        super(context, 0, menus);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_menu,parent, false);
        }

        MenuViewHolder viewHolder = (MenuViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new MenuViewHolder();
            viewHolder.menu = (TextView) convertView.findViewById(R.id.textmenu);
            viewHolder.entree = (TextView) convertView.findViewById(R.id.textentree);
            viewHolder.plat = (TextView) convertView.findViewById(R.id.textplat);
            viewHolder.dessert = (TextView) convertView.findViewById(R.id.textdessert);
            convertView.setTag(viewHolder);
        }

        Menu menu = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        viewHolder.menu.setText(menu.getNomMenu());
        viewHolder.entree.setText(menu.getEntree());
        viewHolder.plat.setText(menu.getPlat());
        viewHolder.dessert.setText(menu.getDessert());

        return convertView;
    }

    private class MenuViewHolder{
        public TextView menu;
        public TextView entree;
        public TextView plat;
        public TextView dessert;
    }

}