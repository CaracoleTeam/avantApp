package com.avant.joao.avant.fragments;

import android.support.v4.app.Fragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avant.joao.avant.R;
import com.avant.joao.avant.adapters.PatientListAdapter;
import com.avant.joao.avant.entities.PatientEntity;
import com.avant.joao.avant.tools.Patient;
import com.avant.joao.avant.viewModels.PatientViewModel;

import java.util.ArrayList;
import java.util.List;

public class PatientFragment extends Fragment {

    RecyclerView mRecyclerView;
    PatientListAdapter mPatientListAdapter;
    List<PatientEntity> patients;

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup parent, Bundle savedInstanceState){
        super.onCreateView(li,parent,savedInstanceState);
        View rootview = li.inflate(R.layout.patient_fragment_layout,parent,false);

        mRecyclerView = (RecyclerView) rootview.findViewById(R.id.patient_recycler_view);
        mRecyclerView.hasFixedSize();

        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(),2));


        mPatientListAdapter = new PatientListAdapter(patients,getActivity().getApplicationContext());
        mRecyclerView.setAdapter(mPatientListAdapter);
        PatientViewModel patientViewModel = ViewModelProviders.of(getActivity()).get(PatientViewModel.class);

        patientViewModel.insertPatient(new PatientEntity("João",19));
        patientViewModel.getAllPatients().observe(this, new Observer<List<PatientEntity>>() {
            @Override
            public void onChanged(@Nullable List<PatientEntity> patientEntities) {
                mPatientListAdapter.setPatients(patientEntities);
                mPatientListAdapter.notifyDataSetChanged();
            }
        });



        return rootview;
    }
}
