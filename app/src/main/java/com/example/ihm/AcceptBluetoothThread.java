package com.example.ihm;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class AcceptBluetoothThread extends Thread {

    private static BluetoothServerSocket btServerSocket;
    private Handler mHandler;
    private final String NAME = "00007777-0000-1000-8000-00805F9B34FB";

    public AcceptBluetoothThread(BluetoothAdapter btAdapter, Handler handler){
        BluetoothServerSocket tmpServerSocket = null;
        mHandler = handler;
        try{
            UUID myUUID = UUID.fromString(NAME);
            tmpServerSocket = btAdapter.listenUsingRfcommWithServiceRecord(NAME, myUUID);
        } catch(IOException e){
            Log.e("ERROR_SERVER_SOCKET", "Error while getting server socket", e);
        }
        AcceptBluetoothThread.btServerSocket = tmpServerSocket;
    }

    public void run(){
        BluetoothSocket btSocket = null;
        while(true){
            try{
                btSocket = btServerSocket.accept();
            } catch(IOException e){
                Log.e("BT_ACCEPT_ERROR", "Socket accept method failed", e);
                break;
            }

            if(btSocket != null){
                ConnectedThread connectedThread = new ConnectedThread(btSocket, mHandler);
                connectedThread.start();
                try{
                    connectedThread.join();
                } catch(InterruptedException e){
                    Log.e("BT_ACCEPT_ERROR", "Thread join failed", e);
                }
            }
        }
    }

    public void cancel(){
        try{
            btServerSocket.close();
        } catch(IOException e){
            Log.e("BT_CLOSE_ERROR", "Socket close method failed", e);
        }
    }

    public static BluetoothServerSocket getServerSocket(){
        return btServerSocket;
    }
}
