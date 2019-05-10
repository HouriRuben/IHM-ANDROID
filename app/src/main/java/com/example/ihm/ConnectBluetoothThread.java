package com.example.ihm;

import android.app.Activity;
import android.app.Notification;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;

public class ConnectBluetoothThread extends Thread {

    private int MESSAGE_TOAST = 2;
    private final int NB_RETRY = 5;
    private int nb_fail = 0;

    private final BluetoothSocket mSocket;
    private final BluetoothDevice mDevice;
    private final Handler mHandler;
    private final String NAME = "00007777-0000-1000-8000-00805F9B34FB";

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference("repas");

    public ConnectBluetoothThread(BluetoothDevice device, Handler handler){
        BluetoothSocket tmpSocket = null;
        mDevice = device;
        mHandler = handler;

        try{
            UUID myUUID = UUID.fromString(NAME);
            tmpSocket = device.createRfcommSocketToServiceRecord(myUUID);
        } catch(IOException e){
            Log.e("ERROR_CLIENT_SOCKET", "Socket create method failed", e);
        }
        mSocket = tmpSocket;
    }

    public void run(){
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        Looper.prepare();

        if(mSocket != null){
            for(int i=0; i<this.NB_RETRY; i++){
                try{
                    mSocket.connect();
                    break;
                } catch(IOException connectException){
                    Log.e("ERROR_CLIENT_SOCKET", "Could not connect sockets", connectException);
                    nb_fail++;
                    try{
                        if(nb_fail >= NB_RETRY){
                            mSocket.close();
                            Log.e("ERROR_CLIENT_SOCKET", "Tried to connect " + NB_RETRY + " times. Socket closed.");
                            Message writeErrorMsg =
                                    mHandler.obtainMessage(MESSAGE_TOAST);
                            Bundle bundle = new Bundle();
                            bundle.putString("toast",
                                    "Impossible de se connecter à l'appareil " + mDevice.getName() + ". Veuillez réessayer");
                            writeErrorMsg.setData(bundle);
                            mHandler.sendMessage(writeErrorMsg);
                            Looper currLooper = Looper.myLooper();
                            if(currLooper != null){
                                currLooper.quit();
                            }
                            return;
                        }
                    } catch(IOException closeException){
                        Log.e("ERROR_CLIENT_SOCKET", "Could not close the client socket", closeException);
                    }
                }
                try{
                    Thread.sleep(1000);
                } catch(InterruptedException e){
                    Log.e("ERROR_CLIENT_SOCKET", "Thread interrompu", e);
                }

            }
        }

        if (user != null ) {
            database.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String json = dataSnapshot.getValue(String.class);
                    if (json != null && mSocket != null && mSocket.isConnected()) {
                        ConnectedThread connectedThread = new ConnectedThread(mSocket, mHandler);
                        connectedThread.write(json.getBytes());
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("Firebase read ", "Failed to read value.", error.toException());
                }
            });
        } else {
            Message writeErrorMsg =
                    mHandler.obtainMessage(MESSAGE_TOAST);
            Bundle bundle = new Bundle();
            bundle.putString("toast",
                    "Veuillez vous connecter pour envoyer vos menus. (Fonctionnalité de transfert local en développement)");
            writeErrorMsg.setData(bundle);
            mHandler.sendMessage(writeErrorMsg);
        }
        Looper currLooper = Looper.myLooper();
        if(currLooper != null){
            currLooper.quit();
        }
    }
}
