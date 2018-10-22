package com.avant.joao.avant.utils;

import android.app.Application;

public class Avant extends Application {

    public boolean isBluetoothConnected;

    public boolean isBluetoothConnected(){
        return isBluetoothConnected;
    }

    public void setBluetoothStatus(boolean connectionState){
        this.isBluetoothConnected = connectionState;
    }
}
