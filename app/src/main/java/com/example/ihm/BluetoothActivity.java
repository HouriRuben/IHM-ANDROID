package com.example.ihm;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;


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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Set;

public class BluetoothActivity extends AppCompatActivity {

    private interface MessageConstants {
        int MESSAGE_READ = 0;
        int MESSAGE_WRITE = 1;
        int MESSAGE_TOAST = 2;
    }

    public static String bundleReadMessageKey = "READ_MSG_KEY";

    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    ArrayList<BluetoothDevice> pairedDevices;
    static ArrayList<BluetoothDevice> btDevices;
    int ACTION_REQUEST_DISCOVERABLE = 2;
    int DISCOVERABILITY_DURATION = 300;
    Handler mHandler;
    AcceptBluetoothThread bluetoothServerThread;

    ProgressDialog loadingDialog;

    ListView mListView;

    ArrayList<MenuPlanified> menusPlanified = new ArrayList<>();

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference database = FirebaseDatabase.getInstance().getReference("repas");
    final Type listType = new TypeToken<ArrayList<MenuPlanified>>(){}.getType();
    final Gson gson = new GsonBuilder()
            .serializeNulls()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initiateMenusPlanifiedList();
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what == MessageConstants.MESSAGE_READ){
                    final String receivedMessage = msg.getData().getString(BluetoothActivity.bundleReadMessageKey);
                    ArrayList<MenuPlanified> menusList = BluetoothActivity.this.gson.fromJson(receivedMessage, BluetoothActivity.this.listType);
                    BluetoothActivity.this.menusPlanified.addAll(menusList);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BluetoothActivity.this);
                    alertDialogBuilder.setTitle("Réception de données")
                            .setMessage("Acceptez-vous les " + menusList.size() + " menus reçu(s) ?")
                            .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Vous avez maintenant accès aux menus partagés", Toast.LENGTH_SHORT);
                                    toast.show();
                                    if (user != null) {
                                        String jsonResult = BluetoothActivity.this.gson.toJson(BluetoothActivity.this.menusPlanified,BluetoothActivity.this.listType);
                                        BluetoothActivity.this.database.child(user.getUid()).setValue(jsonResult);
                                    }
                                }
                            })
                            .setNegativeButton("Non", null);
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else if(msg.what == MessageConstants.MESSAGE_WRITE){
                    BluetoothActivity.this.loadingDialog.dismiss();
                }

                if(msg.what == MessageConstants.MESSAGE_TOAST){
                    BluetoothActivity.this.loadingDialog.dismiss();
                    Toast toast = Toast.makeText(getApplicationContext(), msg.getData().getString("toast"), Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        };
        this.pairedDevices = new ArrayList<>(this.bluetoothAdapter.getBondedDevices());
        btDevices = this.pairedDevices;
        mListView = findViewById(R.id.bt_device_list);

        BluetoothDeviceAdapter arrayAdapter = new BluetoothDeviceAdapter(this, btDevices);
        mListView.setAdapter(arrayAdapter);
        discoverBluetoothDevice();
        acceptBluetoothConnexion();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int devicePosition = position;
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BluetoothActivity.this);
                        alertDialogBuilder.setTitle("Envoi de données")
                        .setMessage("Voulez-vous partager vos menus avec " + btDevices.get(position).getName() + " ?")
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(AcceptBluetoothThread.getServerSocket() != null){
                                    ConnectBluetoothThread connectThread = new ConnectBluetoothThread(btDevices.get(devicePosition), mHandler);
                                    connectThread.start();
                                    BluetoothActivity.this.loadingDialog = ProgressDialog.show(BluetoothActivity.this, "Partage des menus", "Envoi en cours ...", true);
                                } else {
                                    Toast errToast = Toast.makeText(getApplicationContext(), "Impossible de se connecter à l'appareil", Toast.LENGTH_SHORT);
                                    errToast.show();
                                }
                            }
                        })
                        .setNegativeButton("Non", null);
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(!BluetoothActivity.btDevices.contains(device)){
                    BluetoothActivity.btDevices.add(device);
                }
                mListView.invalidateViews();
            }
        }
    };

    public void discoverBluetoothDevice(){
        // Intent that triggers when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        bluetoothAdapter.startDiscovery();
    }

    public void acceptBluetoothConnexion(){
        // Turn on the Bluetooth Socket Server and sets the device as discoverable
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABILITY_DURATION);
        startActivityForResult(discoverableIntent, ACTION_REQUEST_DISCOVERABLE);
    }

    public void initiateMenusPlanifiedList(){
        if (user != null ) {
            database.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String json = dataSnapshot.getValue(String.class);
                    if (json != null) {
                        menusPlanified = gson.fromJson(json, listType);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("Firebase read ", "Failed to read value.", error.toException());
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == ACTION_REQUEST_DISCOVERABLE && resultCode == DISCOVERABILITY_DURATION){
            bluetoothServerThread = new AcceptBluetoothThread(bluetoothAdapter, mHandler);
            bluetoothServerThread.start();
        }
    }

    public void onDestroy(){
        super.onDestroy();
        if(bluetoothServerThread != null && bluetoothServerThread.isAlive()){
            bluetoothServerThread.cancel();
        }
        unregisterReceiver(receiver);
    }
}
