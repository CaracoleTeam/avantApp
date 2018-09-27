package com.avant.joao.avant.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avant.joao.avant.R;

public class PatientFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup parent, Bundle savedInstanceState){
        super.onCreateView(li,parent,savedInstanceState);
        View rootview = li.inflate(R.layout.patient_fragment_layout,parent,false);


        return rootview;
    }
}
