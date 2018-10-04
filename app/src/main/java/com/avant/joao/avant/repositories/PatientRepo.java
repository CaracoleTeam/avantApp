package com.avant.joao.avant.repositories;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.avant.joao.avant.dao.GaitDao;
import com.avant.joao.avant.dao.PatientDao;
import com.avant.joao.avant.databases.PatientDatabase;
import com.avant.joao.avant.entities.Gait;
import com.avant.joao.avant.entities.PatientEntity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientRepo {

    private PatientDao mPatientDao;
    private GaitDao mGaitDao;
    private LiveData<List<PatientEntity>> mPatientList;

    public  static FirebaseStorage firebaseStorage;
    public  static FirebaseUser mFirebaseUser;
    public  static FirebaseFirestore firebaseDb;

    public PatientRepo(Application app) {
        PatientDatabase db = PatientDatabase.getDatabase(app);
        mPatientDao = db.patientDao();
        mGaitDao = db.gaitDao();
        mPatientList = mPatientDao.getAll();
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDb  = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
    }

    public LiveData<List<PatientEntity>> getPatients(){

        return this.mPatientList;
    }

    public LiveData<List<Gait>> getPatientGaits(int pid){
        return mGaitDao.getPatientGaits(pid);
    }

    public void insertPatient(PatientEntity patient){
        Log.d("Repo insert Patient",String.valueOf(patient.getPid()));
        new insertPatientAsyncTask(mPatientDao).execute(patient);
    }

    public void insertGait(Gait gait){
        new insertGaitAsyncTask(mGaitDao).execute(gait);
    }

    private static class insertPatientAsyncTask extends AsyncTask<PatientEntity,Integer,Void>{

        private PatientDao asyncPatientDao;

        insertPatientAsyncTask(PatientDao pd){

            this.asyncPatientDao = pd;
        }
        @Override

        public Void doInBackground(PatientEntity ... patients){

            long generatedPatientId = asyncPatientDao.insertUsers(patients[0]);

            Map<String, Object> patient = new HashMap<>();
            patient.put("name", patients[0].getName());
            patient.put("age",patients[0].getAge());


            PatientRepo.firebaseDb.collection("users")
                    .document(mFirebaseUser.getUid())
                    .collection("patients")
                    .document(String.valueOf(generatedPatientId)).set(patient);

            Log.d("ADDED PATIENT",String.valueOf(patients[0].getPid()));
            firebaseStorage.getReference().child("/"+mFirebaseUser.getUid()+"/"+"patients"+"/"+generatedPatientId+".jpg").putBytes(patients[0].getProfile());

            return null;
        }
    }

    private static class insertGaitAsyncTask extends AsyncTask<Gait,Integer,Void>{
        private GaitDao gaitAsyncDao;

        insertGaitAsyncTask(GaitDao gaitDao){
            this.gaitAsyncDao = gaitDao;
        }


        @Override
        public Void doInBackground(Gait ... gaits) {

            gaitAsyncDao.insertGait(gaits[0]);
            return null;
        }
    }


}




