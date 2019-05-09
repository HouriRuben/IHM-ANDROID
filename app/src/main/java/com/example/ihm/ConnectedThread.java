package com.example.ihm;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private byte[] mmBuffer; // mmBuffer store for the stream
    public Handler mHandler;

    private interface MessageConstants {
        int MESSAGE_READ = 0;
        int MESSAGE_WRITE = 1;
        int MESSAGE_TOAST = 2;
    }

    public ConnectedThread(BluetoothSocket socket, Handler handler) {
        mmSocket = socket;
        mHandler = handler;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams; using temp objects because
        // member streams are final.
        try {
            tmpIn = socket.getInputStream();
        } catch (IOException e) {
            Log.e("CONNECTED_DEVICE_ERROR", "Error occurred when creating input stream", e);
        }
        try {
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e("CONNECTED_DEVICE_ERROR", "Error occurred when creating output stream", e);
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        mmBuffer = new byte[1024];
        int numBytes; // bytes returned from read()
        // Keep listening to the InputStream until an exception occurs.

        try {
            Looper currLooper = Looper.myLooper();
            if(currLooper == null){
                Looper.prepare();
            }
            // Read from the InputStream.
            numBytes = mmInStream.read(mmBuffer);
            byte[] readedMsgBytes = Arrays.copyOfRange(mmBuffer,0, numBytes);
            String readedMsg = new String(readedMsgBytes);
            // Send the obtained bytes to the UI activity.
            Message readMsg = mHandler.obtainMessage(
                    MessageConstants.MESSAGE_READ, numBytes, -1);
            Bundle bundle = new Bundle();
            bundle.putString(BluetoothActivity.bundleReadMessageKey, readedMsg);
            readMsg.setData(bundle);
            readMsg.sendToTarget();
        } catch (Exception e) {
            Log.d("DEVICE_READ_ERROR", "Input stream was disconnected", e);

        }
        Looper currLooper = Looper.myLooper();
        if(currLooper != null){
            currLooper.quit();
        }
        System.out.println("connected thread ended");
    }

    // Call this from the main activity to send data to the remote device.
    public void write(byte[] bytes, BluetoothDevice device) {
        try {
            Looper currLooper = Looper.myLooper();
            if(currLooper == null){
                Looper.prepare();
            }
            mmOutStream.write(bytes);

            // Share the sent message with the UI activity.
            Message writtenMsg = mHandler.obtainMessage(
                    MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
            writtenMsg.sendToTarget();
        } catch (IOException e) {
            Log.e("DEVICE_WRITE_ERROR", "Error occurred when sending data", e);

            // Send a failure message back to the activity.
            Message writeErrorMsg =
                    mHandler.obtainMessage(MessageConstants.MESSAGE_TOAST);
            Bundle bundle = new Bundle();
            bundle.putString("toast",
                    "Couldn't send data to the other device");
            writeErrorMsg.setData(bundle);
            mHandler.sendMessage(writeErrorMsg);

            Looper currLooper = Looper.myLooper();
            if(currLooper != null){
                currLooper.quit();
            }
        }
    }

    // Call this method from the main activity to shut down the connection.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e("CONNECTION_CANCELLED", "Could not close the connect socket", e);
        }
    }
}
