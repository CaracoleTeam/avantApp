package com.avant.joao.avant.fragments;

import android.support.v4.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avant.joao.avant.R;
import com.avant.joao.avant.adapters.BluetoothListAdapter;

import java.util.ArrayList;

public class BtFragment extends Fragment {

    // BLUEOOTH
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private static final int  REQUEST_ENABLE_BT = 9100;
    private static final long SCAN_PERIOD = 10000;

    //LISTA
    private RecyclerView mRecyclerView;
    private BluetoothListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;



    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup parent, Bundle savedInstanceState){
        super.onCreateView(layoutInflater,parent,savedInstanceState);
        View rootView = layoutInflater.inflate(R.layout.bluetooth_fragment_layout , parent,false);

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);

        mBluetoothAdapter = bluetoothManager.getAdapter();
        mHandler = new Handler();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }




        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.bluetooth_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        ArrayList<BluetoothDevice> bluetoothDevicesList = new ArrayList<BluetoothDevice>();

        mAdapter = new BluetoothListAdapter(bluetoothDevicesList,getActivity().getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);
        mScanning = true;
        scanLeDevice(mScanning);

        return rootView;

    }



    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }

    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {

                    if(getActivity() == null)
                        return;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("Device",device.getAddress());
                            mAdapter.addDevice(device);
                            mAdapter.notifyDataSetChanged();

                        }
                    });
                }
            };
}
