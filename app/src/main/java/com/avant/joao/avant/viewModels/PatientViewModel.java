package com.avant.joao.avant.viewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.avant.joao.avant.entities.Gait;
import com.avant.joao.avant.entities.PatientEntity;
import com.avant.joao.avant.repositories.PatientRepo;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class PatientViewModel extends AndroidViewModel {

    private PatientRepo mPatientRepo;
    private LiveData<List<PatientEntity>> mPatientList;

    public PatientViewModel(@NonNull Application application) {
        super(application);
        this.mPatientRepo = new PatientRepo(application);
        this.mPatientList = mPatientRepo.getPatients();
    }


    public LiveData<List<PatientEntity>> getAllPatients(){return this.mPatientList;}

    public void insertPatient(PatientEntity patient){this.mPatientRepo.insertPatient(patient);}

    public void removePatient(PatientEntity patient){this.mPatientRepo.removePatient(patient);}

    public void updatePatients(FirebaseUser user){this.mPatientRepo.updatePatients(user);}


}
