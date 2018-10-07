package com.avant.joao.avant.viewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.avant.joao.avant.entities.Gait;
import com.avant.joao.avant.repositories.PatientRepo;

import java.util.ArrayList;
import java.util.List;

public class GaitViewModel extends AndroidViewModel {

    private PatientRepo mPatientRepo;


    public GaitViewModel(@NonNull Application application ) {
        super(application);
        mPatientRepo = new PatientRepo(application);

    }

    public LiveData<List<Gait>> getPatientGaits(int pid){return mPatientRepo.getPatientGaits(pid);}

    public void insertGait(Gait gait){this.mPatientRepo.insertGait(gait);}


}
