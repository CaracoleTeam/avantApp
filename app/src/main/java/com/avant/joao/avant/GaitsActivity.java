package com.avant.joao.avant;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.avant.joao.avant.adapters.GaitAdapter;
import com.avant.joao.avant.entities.Gait;
import com.avant.joao.avant.utils.ParcelablePatient;
import com.avant.joao.avant.viewModels.GaitViewModel;
import com.avant.joao.avant.viewModels.PatientViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GaitsActivity extends AppCompatActivity {

    GaitViewModel mPatientGaitsViewModel;
    RecyclerView mGaitRecyclerView;
    GaitAdapter mGaitAdapter;
    List<Gait> mGaits;

    public static final int GAIT_CODE = 8500;
    public static final String GAITS_EXTRA = "gaits";
    public static final String PATIENT_EXTRA = "patient";


    private ParcelablePatient mParcelablePatiente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gaits);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mParcelablePatiente = (ParcelablePatient) getIntent().getSerializableExtra("patient");

        getSupportActionBar().setTitle(mParcelablePatiente.getPatientName());
        mGaitRecyclerView = (RecyclerView) findViewById(R.id.gait_recycler_view);

        mGaitRecyclerView.hasFixedSize();
        mGaitRecyclerView.setLayoutManager(new LinearLayoutManager(this));



        mGaitAdapter = new GaitAdapter(this,mGaits);
        mGaitRecyclerView.setAdapter(mGaitAdapter);

        mPatientGaitsViewModel = ViewModelProviders.of(this).get(GaitViewModel.class);
        mPatientGaitsViewModel.getPatientGaits(mParcelablePatiente.getPid()).observe(this, new Observer<List<Gait>>() {
            @Override
            public void onChanged(@Nullable List<Gait> gaits) {
                mGaitAdapter.setGaits(gaits);
                mGaitAdapter.notifyDataSetChanged();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startGaitActivity = new Intent(getApplicationContext(),GaitActivity.class);
                startGaitActivity.putExtra("patient",mParcelablePatiente);
                startActivityForResult(startGaitActivity,GAIT_CODE);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == GAIT_CODE){

            Gait receivedGait = (Gait) data.getParcelableExtra("gait");

            //mPatientGaitsViewModel.insertGait(receivedGait);
            mPatientGaitsViewModel.insertGait(new Gait(10,1,3,4,Float.valueOf("4.5"),1,5,10,10));

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.graph_item:
                Intent startGrapthActivity = new Intent(this,GraphActivity.class);
                startGrapthActivity.putExtra(GAITS_EXTRA,(Serializable) mGaitAdapter.getGaits());
                startGrapthActivity.putExtra(PATIENT_EXTRA,mParcelablePatiente);
                startActivity(startGrapthActivity);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gaits_menu,menu);
        return true;
    }
}
