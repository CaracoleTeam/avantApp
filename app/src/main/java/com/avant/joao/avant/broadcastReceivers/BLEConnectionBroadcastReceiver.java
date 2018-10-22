package com.avant.joao.avant.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.avant.joao.avant.Observables.BluetoothStateObservable;
import com.avant.joao.avant.services.BluetoothLeService;
import com.avant.joao.avant.utils.BluetoothStatus;

public class BLEConnectionBroadcastReceiver extends BroadcastReceiver {
    private int CONNECTION_STATE;
    private String BLE_RECEIVER = "BLE State broadcast Receiver:";
    @Override
    public void onReceive(Context context, Intent intent) {
        this.CONNECTION_STATE = intent.getIntExtra("state",0);
        Log.d(BLE_RECEIVER,"Conexão alterada!");

        BluetoothStatus bs = new BluetoothStatus();


        if(CONNECTION_STATE == BluetoothLeService.STATE_CONNECTED) {
            //TODO passar a string de strings.xml sobre o estado da conexão e o nome do dispositivo
            bs.deviceName = intent.getStringExtra("deviceName");
        }
        bs.connectionStatus = CONNECTION_STATE;
        BluetoothStateObservable.getInstance().updateStatus(bs);
    }

}
