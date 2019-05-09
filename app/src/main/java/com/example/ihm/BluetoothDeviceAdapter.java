package com.example.ihm;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class BluetoothDeviceAdapter extends ArrayAdapter<BluetoothDevice> {

    public BluetoothDeviceAdapter(Context context, ArrayList<BluetoothDevice> devices) {
        super(context, 0, devices);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_bt_device,parent, false);
        }

        BluetoothDeviceAdapter.BluetoothDeviceViewHolder viewHolder = (BluetoothDeviceAdapter.BluetoothDeviceViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new BluetoothDeviceAdapter.BluetoothDeviceViewHolder();
            viewHolder.deviceName = (TextView) convertView.findViewById(R.id.device_name);
            convertView.setTag(viewHolder);
        }

        BluetoothDevice device = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        viewHolder.deviceName.setText(device.getName());

        return convertView;
    }

    private class BluetoothDeviceViewHolder{
        public TextView deviceName;
    }
}
