package com.example.ihm;

import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class DownloadMenusTask extends AsyncTask<String, Void, ArrayList<Menu>> {
    private FragmentMenus fragmentReference;

    DownloadMenusTask(FragmentMenus context){
        fragmentReference = context;
    }

    @Override
    protected ArrayList<Menu> doInBackground(String... params){
        return fragmentReference.gson.fromJson(params[0], fragmentReference.listType);
    }

    @Override
    protected void onPostExecute(ArrayList<Menu> result) {
        fragmentReference.list = result;
        fragmentReference.mListView = (ListView) fragmentReference.fragmentView.findViewById(R.id.listmenus);
        if(fragmentReference.getActivity() != null){
            MenuAdapter menuAdapter = new MenuAdapter(fragmentReference.getActivity(), fragmentReference.list);
            fragmentReference.mListView.setAdapter(menuAdapter);
        }
        fragmentReference.registerForContextMenu(fragmentReference.mListView);
    }
}