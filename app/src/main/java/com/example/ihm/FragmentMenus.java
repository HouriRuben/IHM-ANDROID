package com.example.ihm;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.ListView;
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

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import javax.xml.transform.Result;


public class FragmentMenus extends Fragment implements OnClickListener {
    View fragmentView;
    ListView mListView;
    ArrayList<Menu> list = new ArrayList<>();
    // Firebase
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference database = FirebaseDatabase.getInstance().getReference("menus");
    //Json
    final Type listType = new TypeToken<ArrayList<Menu>>(){}.getType();
    final Gson gson = new GsonBuilder()
            .serializeNulls()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        this.fragmentView = inflater.inflate(R.layout.fragment_menus, container, false);
        Button createmenubutton = (Button) fragmentView.findViewById(R.id.createmenubutton);
        if (user != null ) {
            database.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String json = dataSnapshot.getValue(String.class);
                    System.out.println("json received : " + json);
                    if (json != null) {
                        list = gson.fromJson(json, listType);
                        mListView = (ListView) fragmentView.findViewById(R.id.listmenus);
                        if (getActivity() != null){
                            MenuAdapter menuAdapter = new MenuAdapter(getActivity(), list);
                            mListView.setAdapter(menuAdapter);
                        }

                        registerForContextMenu(mListView);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("Firebase read ", "Failed to read value.", error.toException());
                }
            });
        }
        createmenubutton.setOnClickListener(this);
        return fragmentView;

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu_menus, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int selectedItemId = (int) info.id;
        switch (item.getItemId()) {
            case R.id.cancel:
                return false;
            case R.id.delete:
                Menu suppressingMenu = list.get(selectedItemId);
                list.remove(suppressingMenu);
                String jsonResult = gson.toJson(list, listType);
                if (user != null ) {
                    database.child(user.getUid()).setValue(jsonResult);
                }
                mListView.invalidateViews();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getContext(), CreateMenuActivity.class);
        startActivity(intent);
    }
}
