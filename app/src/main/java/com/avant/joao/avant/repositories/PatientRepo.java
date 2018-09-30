package com.avant.joao.avant.repositories;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.avant.joao.avant.dao.PatientDao;
import com.avant.joao.avant.databases.PatientDatabase;
import com.avant.joao.avant.entities.PatientEntity;

import java.util.List;

public class PatientRepo {

    private PatientDao mPatientDao;
    private LiveData<List<PatientEntity>> mPatientList ;

    public PatientRepo(Application app) {
        PatientDatabase db = PatientDatabase.getDatabase(app);
        mPatientDao = db.patientDao();
        mPatientList = mPatientDao.getAll();


    }

    public LiveData<List<PatientEntity>> getPatients(){

        return this.mPatientList;
    }

    public void insert(PatientEntity patient){
        new insertAsyncTask(mPatientDao).execute(patient);
    }

    private static class insertAsyncTask extends AsyncTask<PatientEntity,Integer,Void>{

        private PatientDao asyncPatientDao;

        insertAsyncTask(PatientDao pd){
            this.asyncPatientDao = pd;
        }
        @Override
        public Void doInBackground(PatientEntity ... patients){
            asyncPatientDao.insertUsers(patients[0]);
            return null;
        }
    }


}




