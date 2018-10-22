package com.avant.joao.avant;

import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avant.joao.avant.Observables.BluetoothStateObservable;
import com.avant.joao.avant.adapters.StepsAdapter;
import com.avant.joao.avant.broadcastReceivers.BLEConnectionBroadcastReceiver;
import com.avant.joao.avant.entities.Gait;
import com.avant.joao.avant.fragments.BtFragment;
import com.avant.joao.avant.services.BluetoothLeService;
import com.avant.joao.avant.utils.BluetoothStatus;
import com.avant.joao.avant.utils.ParcelablePatient;
import com.avant.joao.avant.utils.Step;
import com.avant.joao.avant.viewModels.GaitCollectViewModel;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

public class GaitActivity extends AppCompatActivity implements View.OnClickListener,Observer {

    boolean mConnected = true;
    
    TextView mGaitCounterText;
    Button mStartRunningButton;
    static final String IS_RUNNING_STATE = "running";
    private boolean isRunning = false;

    StepsAdapter mStepsAdapter;

    GaitCollectViewModel gaitViewModel;
    ArrayList<Step> mSteps;

    RecyclerView mStepsRecyclerView;


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IS_RUNNING_STATE,isRunning);
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isRunning = savedInstanceState.getBoolean(IS_RUNNING_STATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BluetoothStateObservable.getInstance().addObserver(this);

        BluetoothStatus status =  new BluetoothStatus();

        status.deviceName = BluetoothStateObservable.getBluetoothName();
        status.connectionStatus =  BluetoothStateObservable.getBluetothState();

        BluetoothStateObservable.getInstance().updateStatus(status);









        setContentView(R.layout.activity_gait);
        mSteps = new ArrayList<Step>();
        mStepsRecyclerView = (RecyclerView) findViewById(R.id.steps_recycler_view);
        mStepsRecyclerView.hasFixedSize();
        LinearLayoutManager layoutManager =  new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        mStepsRecyclerView.setLayoutManager(layoutManager);

        mStepsAdapter = new StepsAdapter(this.mSteps,getApplicationContext());

        mStepsRecyclerView.setAdapter(mStepsAdapter);

        mGaitCounterText = (TextView) findViewById(R.id.steps_counter);
        mStartRunningButton = (Button) findViewById(R.id.start_running_button);

        mStartRunningButton.setOnClickListener(this);

        gaitViewModel = ViewModelProviders.of(this).get(GaitCollectViewModel.class);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isRunning){
            mStartRunningButton.setText(R.string.start_gait_button_true);
        }else{
            mStartRunningButton.setText(R.string.start_gait_button_false);
        }

    }

    @Override
    public void onPause(){
        super.onPause();

    }


    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();
            if (BluetoothLeService.
                    ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the
                // user interface.

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {



                String rawData = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);

                Log.d("Valor vindo do service:",rawData);

                Step passo = getStepFromRawData(rawData);

                mStepsAdapter.addStep(passo);
                mStepsAdapter.notifyDataSetChanged();

                mGaitCounterText.setText(String.valueOf(mStepsAdapter.getItemCount()));


            }
        }
    };

    private Step getStepFromRawData(String rawData){
        Step passo = new Step();
        passo.setFoot(rawData.charAt(1));

        int start = 3;
        int end = 4;

        for(int i = 0;i<rawData.length();i++){
            if(rawData.charAt(i) == 'l' ) {
                end = i ;
            }
        }
        String rawTime = rawData.substring(start,end);
        Log.d("rawTime:",rawTime);

        String rawLenght = rawData.substring(end+1);
        Log.d("rawLenght:",rawLenght);

        passo.setTime(Float.valueOf(rawTime)/10);
        passo.setLenght(Integer.valueOf(rawLenght));
        return passo;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.start_running_button:
                mGaitCounterText.setText(String.valueOf(mStepsAdapter.getItemCount()));
                if(!isRunning){

                    mStartRunningButton.setText(R.string.start_gait_button_true);

                    //para testes

                    Step passo1 = getStepFromRawData("pEt10l60");
                    mStepsAdapter.addStep(passo1);
                    mStepsAdapter.notifyDataSetChanged();
                    Step passo2 = getStepFromRawData("pDt70l57");
                    mStepsAdapter.addStep(passo2);
                    mStepsAdapter.notifyDataSetChanged();
                    Step passo3 = getStepFromRawData("pDt21l43");
                    mStepsAdapter.addStep(passo3);
                    mStepsAdapter.notifyDataSetChanged();
                    mGaitCounterText.setText(String.valueOf(mStepsAdapter.getItemCount()));

                    //O que deve ser implementado
                    /*IntentFilter dataReceivedFilter = new IntentFilter(BluetoothLeService.ACTION_DATA_AVAILABLE);
                    LocalBroadcastManager.getInstance(this).registerReceiver(mGattUpdateReceiver,dataReceivedFilter);*/

                    isRunning = true;
                }else{
                    mStartRunningButton.setText(R.string.start_gait_button_false);
                    processData(mStepsAdapter.getSteps());
                    LocalBroadcastManager.getInstance(this).unregisterReceiver(mGattUpdateReceiver);
                    isRunning = false;
                }
        }
    }

    public void processData(ArrayList<Step> steps){


        float time = 0,cadence;
        int totalSteps = steps.size() ,rSteps, lSteps = rSteps = 0;



        for(Step step:steps){
           time += step.getTime();
           if(step.getFoot() == 'D'){
               rSteps ++;
           }else{
               lSteps++;
           }
        }

        cadence =  totalSteps/(time/60);
        Date c = Calendar.getInstance().getTime();

        Gait caminhada = new Gait(time,lSteps,rSteps,totalSteps,cadence,c.getDay(),c.getMonth(),c.getYear());

        Intent it = new Intent();
        it.putExtra("gait",caminhada);
        setResult(RESULT_OK,it);
        finish();
    }

    @Override
    public void update(Observable o, Object arg) {
        Log.d("Atualizou","True");
        BluetoothStatus status = (BluetoothStatus) arg;
        if(status.deviceName != null){
            getSupportActionBar().setSubtitle("Conectado:"+status.deviceName);
            mConnected = true;
        }else{
            getSupportActionBar().setSubtitle("Desconectado");
            mConnected = false;
        }
    }
}
