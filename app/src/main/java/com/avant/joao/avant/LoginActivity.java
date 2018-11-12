package com.avant.joao.avant;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.avant.joao.avant.databases.PatientDatabase;
import com.avant.joao.avant.entities.Gait;
import com.avant.joao.avant.entities.PatientEntity;
import com.avant.joao.avant.repositories.PatientRepo;
import com.avant.joao.avant.viewModels.GaitViewModel;
import com.avant.joao.avant.viewModels.PatientViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    GoogleSignInClient mGoogleSignInClient;
    public static int RC_LOGIN_CODE = 9200;
    private FirebaseAuth mAuth;

    @Override
    protected void onStart(){
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        Log.d("Entrou na activity","True");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Log.d("CLicou","em algo");
        switch (view.getId()){
            case R.id.sign_in_button:
                Log.d("Clicou no login","True");
                signIn();
                break;
        }
    }

    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_LOGIN_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_LOGIN_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("Tab", "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SignIn", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateData(user);

                            updateData(user);

                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("SignIn", "signInWithCredential:failure", task.getException());

                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    //TODO sincronizar os dados do banco ao fazer login pela primeira vez
    private void updateData(final FirebaseUser user)  {

        Log.d("entrou em updateData","true");
        final PatientViewModel viewModel = ViewModelProviders.of(this).get(PatientViewModel.class);
        final GaitViewModel pvm = ViewModelProviders.of(this).get(GaitViewModel.class);

        Log.d("UPDATE DATA","Atualiazando com firebase");
        Log.d("Usuário",user.getUid());
        PatientRepo.firebaseDb.collection("users").document(user.getUid()).collection("patients").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){
                    Log.d("UPDATE DATA","Contato com firebase bem sucedido");
                    for(final QueryDocumentSnapshot document : task.getResult()){

                        Map<String, Object> data = document.getData();


                        final int patientId =  Long.valueOf(document.getId()).intValue();
                        final Long patientAge =(Long) data.get("age");
                        final String patientName = (String)data.get("name");




                        FirebaseStorage storage = FirebaseStorage.getInstance();

                        String reference = String.valueOf(user.getUid())+"/patients/"+document.getId()+".jpg";
                        Log.d("Reference:",reference);

                        final StorageReference patientReference =  storage.getReference().child(reference);




                        final long ONE_MEGA = 1024*1024;
                        final Task<byte[]> task1 = patientReference.getBytes(ONE_MEGA).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                if (bytes == null) {
                                    Log.d("Imagem do firebase:", "NULL");
                                }

                                final PatientEntity patient = new PatientEntity(patientId, patientName, patientAge.intValue(), bytes);
                                viewModel.insertPatient(patient);


                                final Task<QuerySnapshot> querySnapshotTask = PatientRepo.firebaseDb.collection("users").document(user.getUid()).collection("patients").document(String.valueOf(patientId)).collection("gaits").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for(final QueryDocumentSnapshot gaitsDocument : task.getResult()){
                                                Map<String, Object> gaitData = gaitsDocument.getData();


                                                final Float cadence = Float.valueOf(gaitData.get("cadence").toString());

                                                final int gaitDay = Integer.valueOf( gaitData.get("gaitDay").toString());
                                                final int gaitMonth = Integer.valueOf( gaitData.get("gaitMonth").toString());
                                                final int gaitYear = Integer.valueOf( gaitData.get("gaitYear").toString());
                                                final int lSteps = Integer.valueOf( gaitData.get("lSteps").toString());
                                                final int rSteps = Integer.valueOf( gaitData.get("rSteps").toString());
                                                final Float time = Float.valueOf(((Double) gaitData.get("time")).toString());
                                                final int totalSteps = Integer.valueOf( gaitData.get("totalSteps").toString());



                                                pvm.insertGait(new Gait(time, lSteps, rSteps, totalSteps, cadence, patient.getPid(), gaitDay, gaitMonth, gaitYear));

                                            }

                                        }
                                    }
                                });
                            }
                        });


                    }
                }else{
                    Log.d("Error","nao conectiou");
                }
            }
        });

    }

    private void updateUI(FirebaseUser account){

        if(account != null){
            Intent startMainActivityIntent = new Intent(this,MainActivity.class);
            startActivity(startMainActivityIntent);
        }
    }
}
