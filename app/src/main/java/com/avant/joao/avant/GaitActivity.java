package com.avant.joao.avant;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.avant.joao.avant.entities.Gait;
import com.avant.joao.avant.fragments.BtFragment;
import com.avant.joao.avant.services.BluetoothLeService;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class GaitActivity extends AppCompatActivity implements View.OnClickListener {
    boolean mConnected = true;

    TextView mGaitCounterText;
    Button mStartRunningButton;
    private boolean isRunning = false;
    Gait gait;
    ArrayList<Float> mStepsTime;
    int mSteps;
    BluetoothGatt gatt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gait);

        mGaitCounterText = (TextView) findViewById(R.id.steps_counter);

        mStartRunningButton = (Button) findViewById(R.id.start_running_button);
        gait = new Gait();




    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter dataReceivedFilter = new IntentFilter(BluetoothLeService.ACTION_DATA_AVAILABLE);

        LocalBroadcastManager.getInstance(this).registerReceiver(mGattUpdateReceiver,dataReceivedFilter);
    }

    @Override
    public void onPause(){
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mGattUpdateReceiver);
    }


    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                //updateConnectionState(R.string.connected);

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                //updateConnectionState(R.string.disconnected);

                //clearUI();
            } else if (BluetoothLeService.
                    ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the
                // user interface.

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                Bundle data = intent.getExtras();
                float stepTime = data.getFloat(BluetoothLeService.EXTRA_DATA);
                mStepsTime.add(stepTime);
                mSteps = mStepsTime.size();

            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.start_running_button:
                if(!isRunning){
                    mStepsTime.clear();
                    mStartRunningButton.setText(R.string.start_gait_button_true);
                    IntentFilter dataReceivedFilter = new IntentFilter(BluetoothLeService.ACTION_DATA_AVAILABLE);
                    LocalBroadcastManager.getInstance(this).registerReceiver(mGattUpdateReceiver,dataReceivedFilter);
                    isRunning = true;
                }else{
                    mStartRunningButton.setText(R.string.start_gait_button_false);

                    LocalBroadcastManager.getInstance(this).unregisterReceiver(mGattUpdateReceiver);
                    isRunning = false;
                }
        }
    }
}
