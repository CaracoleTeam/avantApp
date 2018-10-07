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
import com.avant.joao.avant.utils.ParcelablePatient;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GaitActivity extends AppCompatActivity implements View.OnClickListener {
    boolean mConnected = true;

    TextView mGaitCounterText;
    Button mStartRunningButton;
    private boolean isRunning = false;
    Gait mGait;
    ArrayList<Integer> mStepsTime;
    int mSteps;
    BluetoothGatt gatt;
    private ParcelablePatient mParcelablePatiente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gait);

        mParcelablePatiente = (ParcelablePatient) getIntent().getSerializableExtra("patient");
        mGaitCounterText = (TextView) findViewById(R.id.steps_counter);
        mStartRunningButton = (Button) findViewById(R.id.start_running_button);

        mStartRunningButton.setOnClickListener(this);
        mStepsTime = new ArrayList<Integer>();


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onPause(){
        super.onPause();

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

                int stepTime = intent.getIntExtra(BluetoothLeService.EXTRA_DATA,0);
                Log.d("Tempo da passada:",String.valueOf(stepTime));
                mStepsTime.add(stepTime);
                mSteps = mStepsTime.size();

                mGaitCounterText.setText(String.valueOf(mSteps));


            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.start_running_button:
                if(!isRunning){

                    mStartRunningButton.setText(R.string.start_gait_button_true);
                    IntentFilter dataReceivedFilter = new IntentFilter(BluetoothLeService.ACTION_DATA_AVAILABLE);
                    LocalBroadcastManager.getInstance(this).registerReceiver(mGattUpdateReceiver,dataReceivedFilter);
                    isRunning = true;
                }else{
                    mStartRunningButton.setText(R.string.start_gait_button_false);
                    processData();
                    LocalBroadcastManager.getInstance(this).unregisterReceiver(mGattUpdateReceiver);
                    isRunning = false;
                }
        }
    }

    public void processData(){
        float time = 0;
        for(float a: mStepsTime){
            Log.d("Tempo da passada:",String.valueOf(a));
            time +=time + (a/10);
        }

        Log.d("Tempo total:",String.valueOf(time));

        float cadencia =  mSteps/(time/60);
        Date c = Calendar.getInstance().getTime();

        mGait = new Gait(time,0,mSteps,mSteps,cadencia,mParcelablePatiente.getPid(),c.getDay(),c.getMonth(),c.getYear());

        Intent it = new Intent();
        it.putExtra("gait",mGait);
        setResult(RESULT_OK,it);
        finish();
    }
}
