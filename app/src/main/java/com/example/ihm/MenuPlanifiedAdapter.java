package com.example.ihm;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MenuPlanifiedAdapter extends ArrayAdapter<MenuPlanified> {


    public MenuPlanifiedAdapter(Context context, ArrayList<MenuPlanified> menus) {
        super(context, 0, menus);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_menu_planified,parent, false);
        }

        MenuViewHolder viewHolder = (MenuViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new MenuViewHolder();
            viewHolder.menu = (TextView) convertView.findViewById(R.id.repastextmenu);
            viewHolder.entree = (TextView) convertView.findViewById(R.id.repastextentree);
            viewHolder.plat = (TextView) convertView.findViewById(R.id.repastextplat);
            viewHolder.dessert = (TextView) convertView.findViewById(R.id.repastextdessert);
            viewHolder.date = (TextView) convertView.findViewById(R.id.repastextdate);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Tweet> tweets
        MenuPlanified menu = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        viewHolder.menu.setText(menu.getMenu().getNomMenu());
        viewHolder.entree.setText(menu.getMenu().getEntree());
        viewHolder.plat.setText(menu.getMenu().getPlat());
        viewHolder.dessert.setText(menu.getMenu().getDessert());
        viewHolder.date.setText(menu.dateToString());


        return convertView;
    }

    private class MenuViewHolder{
        public TextView menu;
        public TextView entree;
        public TextView plat;
        public TextView dessert;
        public TextView date;
    }
}