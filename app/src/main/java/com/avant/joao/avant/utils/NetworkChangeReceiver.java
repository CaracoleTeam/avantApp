package com.avant.joao.avant.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int status = NetworkUtil.getConnectivityStatusString(context);
        if("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())){
            if(status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED){
                Log.d("STATUS ","Nao conectado");
            }else{
                Log.d("STATUS ","conectado");
            }
        }
    }
}
