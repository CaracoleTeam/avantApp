package com.avant.joao.avant.Observables;

import com.avant.joao.avant.entities.Gait;
import com.avant.joao.avant.services.BluetoothLeService;
import com.avant.joao.avant.utils.BluetoothStatus;

import java.util.Observable;

public class BluetoothStateObservable extends Observable {

    private static BluetoothStateObservable instance = new BluetoothStateObservable();
    private static int bluetoothState = BluetoothLeService.STATE_DISCONNECTED;
    private static String bluetoothName;

    public static BluetoothStateObservable getInstance(){
        return instance;
    }

    public static int getBluetothState(){
        return bluetoothState;
    }

    public static String getBluetoothName(){
        return bluetoothName;
    }

    private BluetoothStateObservable(){

    }

    public void updateStatus(Object data){
        synchronized (this){
            BluetoothStatus status = (BluetoothStatus) data;
            this.bluetoothState =  (status.deviceName == null)? BluetoothLeService.STATE_DISCONNECTED:  BluetoothLeService.STATE_CONNECTED;
            this.bluetoothName = status.deviceName;
            setChanged();
            notifyObservers(data);
        }
    }
}
