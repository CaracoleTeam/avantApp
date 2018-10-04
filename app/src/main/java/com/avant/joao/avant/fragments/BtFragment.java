package com.avant.joao.avant.fragments;

import android.Manifest;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avant.joao.avant.R;
import com.avant.joao.avant.adapters.BluetoothListAdapter;
import com.avant.joao.avant.interfaces.OnBluetoothItemClick;
import com.avant.joao.avant.services.BluetoothLeService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.support.constraint.Constraints.TAG;

public class BtFragment extends Fragment implements OnBluetoothItemClick {

    // BLUEOOTH
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private static final int  REQUEST_ENABLE_BT = 9100;
    private static final long SCAN_PERIOD = 5000;
    private BluetoothGatt mBluetoothGatt;

    //LISTA
    private RecyclerView mRecyclerView;
    private BluetoothListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private BluetoothDevice mBluetoothDevice;

    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 2;

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

        mAdapter = new BluetoothListAdapter(bluetoothDevicesList,getActivity().getApplicationContext(),this);
        mRecyclerView.setAdapter(mAdapter);
        mScanning = true;

        scanLeDevice(true);
        return rootView;

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent startBluetoothservice = new Intent(getActivity().getApplicationContext(),BluetoothLeService.class);
        getActivity().stopService(startBluetoothservice);
    }

    private void scanLeDevice(final boolean enable) {
        Log.d("Comecou scan","True");
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
                    Log.d("Dispositivo",device.getAddress());
                    if(getActivity() == null)
                        return;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("Dispositivo",device.getAddress());
                            mAdapter.addDevice(device);
                            mAdapter.notifyDataSetChanged();

                        }
                    });
                }
            };


    @Override
    public void OnBluetoothItemClick(String deviceAddr) {
        Log.d("Addr:",deviceAddr);

        Intent startBluetoothservice = new Intent(getActivity().getApplicationContext(),BluetoothLeService.class);
        startBluetoothservice.putExtra(BluetoothLeService.EXTRA_DATA,deviceAddr);
        getActivity().startService(startBluetoothservice);


        //Intent startBluetoothServiceIntent = new Intent(getContext(),BluetoothLeService.class);
        //startBluetoothServiceIntent.putExtra("deviceAddr",deviceAddr);
        //getActivity().startService(startBluetoothServiceIntent);



    }


}
