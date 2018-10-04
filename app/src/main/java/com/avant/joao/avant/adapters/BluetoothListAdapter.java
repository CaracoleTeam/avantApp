package com.avant.joao.avant.adapters;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avant.joao.avant.R;
import com.avant.joao.avant.interfaces.OnBluetoothItemClick;
import com.avant.joao.avant.services.BluetoothLeService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Handler;

import static android.support.constraint.Constraints.TAG;
import static com.avant.joao.avant.services.BluetoothLeService.ACTION_DATA_AVAILABLE;

public class BluetoothListAdapter extends RecyclerView.Adapter<BluetoothListAdapter.BluetothListViewHolder> {




    public ArrayList<BluetoothDevice> mBluetoothNames;
    private Context mContext;
    private BluetoothLeService bluetoothService;
    private OnBluetoothItemClick mListener;


    public BluetoothListAdapter(ArrayList<BluetoothDevice> mBluetoothNames, Context mContext,OnBluetoothItemClick listener) {
        this.mBluetoothNames = mBluetoothNames;
        this.mContext = mContext;
        this.mListener = listener;
        bluetoothService = new BluetoothLeService();

    }

    public void addDevice(BluetoothDevice device){
        if(!mBluetoothNames.contains(device)){
            mBluetoothNames.add(device);
        }
    }

    @Override
    public BluetothListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bluetooth_list_item,parent,false);
        BluetothListViewHolder mViewHolder = new BluetothListViewHolder(view);

        return mViewHolder;
    }



    @Override
    public void onBindViewHolder(BluetothListViewHolder holder, int position) {
        final BluetoothDevice device = mBluetoothNames.get(position);
        String textToDisplay;

        if(device.getName() == null){
            textToDisplay = device.getAddress();
        }else{
            textToDisplay = device.getName();
        }

        holder.mBluetoothDeviceName.setText(textToDisplay);

        holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.OnBluetoothItemClick(device.getAddress());
                //mBluetoothGatt = device.connectGatt(mContext, true, mGattCallback);
            }
        });
    }





    @Override
    public int getItemCount() {
        return mBluetoothNames.size();
    }

    public class BluetothListViewHolder extends RecyclerView.ViewHolder {

        public TextView mBluetoothDeviceName;
        public LinearLayout mLinearLayout;

        public BluetothListViewHolder(View itemView) {
            super(itemView);
            mBluetoothDeviceName = itemView.findViewById(R.id.bluetooth_item_text);
            mLinearLayout = itemView.findViewById(R.id.bluetooth_name_container);
        }
    }


}
