package com.avant.joao.avant.repositories;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.avant.joao.avant.dao.GaitDao;
import com.avant.joao.avant.dao.PatientDao;
import com.avant.joao.avant.databases.PatientDatabase;
import com.avant.joao.avant.entities.Gait;
import com.avant.joao.avant.entities.PatientEntity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
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

    public void removePatient(PatientEntity patient){
        new removePatientAsyncTask(mPatientDao,mGaitDao).execute(patient);
    }

    public void updatePatients(FirebaseUser user){
        new updatePatientsAsyncTask(mPatientDao).execute(user);
    }


    public void insertGait(Gait gait){
        new insertGaitAsyncTask(mGaitDao).execute(gait);
    }


    private static class updatePatientsAsyncTask extends AsyncTask<FirebaseUser,Integer,Void>{
        private PatientDao asyncPatientDao;

        updatePatientsAsyncTask(PatientDao pd){
            this.asyncPatientDao = pd;
        }

        @Override
        protected Void doInBackground(final FirebaseUser... firebaseUsers) {
            Log.d("Usuário",firebaseUsers[0].getUid());
            PatientRepo.firebaseDb.collection("users").document(firebaseUsers[0].getUid()).collection("patients").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if(task.isSuccessful()){
                        Log.d("UPDATE DATA","Contato com firebase bem sucedido");
                        for(QueryDocumentSnapshot document : task.getResult()){
                            Map<String, Object> data = document.getData();
                            final PatientEntity patient = new PatientEntity();
                            patient.setName((String)data.get("name"));
                            patient.setAge(Integer.valueOf(data.get("age").toString()));

                            Log.d("Patient Name",(String)data.get("name"));
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReference();
                            final StorageReference patientReference = storageRef.child(String.valueOf(firebaseUsers[0].getUid())+"/patients/");

                            StorageReference profileReference = patientReference.child(document.getId());

                            final long ONE_MEGA = 1024*1024;
                            profileReference.getBytes(ONE_MEGA).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    if(bytes == null)
                                    patient.setProfile(bytes);
                                }
                            });

                            new insertPatientAsyncTask(asyncPatientDao).execute(patient);
                        }
                    }else{
                        Log.d("Error","nao conectiou");
                    }
                }
            });
            return null;
        }
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

    private static class removePatientAsyncTask extends AsyncTask<PatientEntity,Integer,Void>{
        private PatientDao mPatientDao;
        private GaitDao mGaitDao;

        removePatientAsyncTask(PatientDao patientDao, GaitDao gaitDao){
            this.mPatientDao = patientDao;
            mGaitDao = gaitDao;
        }
        @Override
        protected Void doInBackground(PatientEntity... patientEntities) {
            this.mGaitDao.removeGaitsFromPatient(patientEntities[0].getPid());
            this.mPatientDao.removeUser(patientEntities[0].getPid());

            PatientRepo.firebaseDb.collection("users").document(mFirebaseUser.getUid()).collection("patients").document(String.valueOf(patientEntities[0].getPid())).delete();
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

            long gaitId = gaitAsyncDao.insertGait(gaits[0]);

            Map<String, Object> gait = new HashMap<>();
            gait.put("cadence", gaits[0].getCadence());
            gait.put("totalSteps",gaits[0].getTotalSteps());
            gait.put("lSteps",gaits[0].getlSteps());
            gait.put("rSteps",gaits[0].getrSteps());
            gait.put("gaitDay",gaits[0].getGaitDay());
            gait.put("gaitMonth",gaits[0].getGaitMonth());
            gait.put("gaitYear",gaits[0].getGaitYear());
            gait.put("time",gaits[0].getTime());


            PatientRepo.firebaseDb.collection("users")
                    .document(mFirebaseUser.getUid())
                    .collection("patients")
                    .document(String.valueOf(gaits[0].getPatientId())).collection("gaits")
                    .document(String.valueOf(gaitId)).set(gait);


            return null;
        }
    }


}




