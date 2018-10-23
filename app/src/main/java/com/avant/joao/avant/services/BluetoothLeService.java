package com.avant.joao.avant.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.avant.joao.avant.broadcastReceivers.BLEConnectionBroadcastReceiver;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.UUID;

public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();


    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private String mBluetoothDeviceName;
    private BluetoothGatt mBluetoothGatt;

    public  int mConnectionState = STATE_DISCONNECTED;

    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTION_CHANGE =
            "com.avant.joao.avant.bluetooth.le.ACTION_GATT_CONNECTION_CHANGE";

    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.avant.joao.avant.  bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.avant.joao.avant.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.avant.joao.avant.bluetooth.le.EXTRA_DATA";

    public final static String DEVICE_ADDR_EXTRA = "DEVICE_ADDR_EXTRA";
    public final static String DEVICE_NAME_EXTRA = "DEVICE_NAME_EXTRA";



    // Various callback methods defined by the BLE API.
    public final  BluetoothGattCallback mGattCallback =
            new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                                    int newState) {
                    String intentAction = ACTION_GATT_CONNECTION_CHANGE;
                    if (newState == BluetoothProfile.STATE_CONNECTED) {

                        mConnectionState = STATE_CONNECTED;



                        Log.i(TAG, "Connected to GATT server.");
                        Log.i(TAG, "Attempting to start service discovery:" +
                                mBluetoothGatt.discoverServices());

                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {

                        mConnectionState = STATE_DISCONNECTED;

                        Log.i(TAG, "Disconnected from GATT server.");

                    }
                    broadcastUpdate(intentAction);
                }

                @Override
                // New services discovered
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    List<BluetoothGattService> services = gatt.getServices();
                    List<BluetoothGattCharacteristic> charas = services.get(2).getCharacteristics();
                    Log.d("SERVICO:",services.get(2).getUuid().toString());

                    gatt.readCharacteristic(charas.get(0));



                    broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);

                    mBluetoothGatt.setCharacteristicNotification(charas.get(0), true);
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                    } else {
                        Log.w(TAG, "onServicesDiscovered received: " + status);
                    }
                }

                public void onCharacteristicChanged(BluetoothGatt gatt,

                                                    BluetoothGattCharacteristic characteristic) {

                    broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);


                }

                @Override
                // Result of a characteristic read operation
                public void onCharacteristicRead(BluetoothGatt gatt,
                                                 BluetoothGattCharacteristic characteristic,
                                                 int status) {
                    mBluetoothGatt.setCharacteristicNotification(characteristic, true);
                    Log.d("Chegou no metodo","true");
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        broadcastUpdate(ACTION_DATA_AVAILABLE,characteristic);
                    }
                }

            };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(this,BLEConnectionBroadcastReceiver.class);
        Log.d("Ação",action);
        Log.d("broadcastUpdate","Mudando estado da conexao");
        intent.putExtra("state",mConnectionState);
        if(mConnectionState == BluetoothLeService.STATE_CONNECTED){
            intent.putExtra("deviceName",this.mBluetoothDeviceName);
        }
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        Log.d("action",action);


        String rawValue = characteristic.getStringValue(0);

        Log.d("rawValue:",rawValue);


        final Intent intent = new Intent(action);
        intent.putExtra(EXTRA_DATA,rawValue);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String deviceAddr = intent.getStringExtra(BluetoothLeService.DEVICE_ADDR_EXTRA);
        String bluetoothName = intent.getStringExtra(BluetoothLeService.DEVICE_NAME_EXTRA);
        this.mBluetoothDeviceAddress = deviceAddr;
        this.mBluetoothDeviceName = bluetoothName;

        Log.d("Dispositivo bluetooth:",this.mBluetoothDeviceName);
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        connect(deviceAddr);

        return super.onStartCommand(intent, flags, startId);

    }

    public class LocalBinder extends Binder {
        BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
        public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.

        return super.onUnbind(intent);
    }
    @Override
    public void onDestroy() {
        Log.d("Service off:","true");
    }


    private final IBinder mBinder = new LocalBinder();

    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);


    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }
}