package com.avant.joao.avant.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avant.joao.avant.R;

import java.util.ArrayList;

public class BluetoothListAdapter extends RecyclerView.Adapter<BluetoothListAdapter.BluetothListViewHolder> {



    private ArrayList<BluetoothDevice> mBluetoothNames;

    private Context mContext;

    public BluetoothListAdapter(ArrayList<BluetoothDevice> mBluetoothNames, Context mContext) {
        this.mBluetoothNames = mBluetoothNames;
        this.mContext = mContext;
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
        holder.mBluetoothDeviceName.setText(mBluetoothNames.get(position).getName());

        holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
