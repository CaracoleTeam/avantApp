package com.avant.joao.avant;

import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.avant.joao.avant.Observables.BluetoothStateObservable;
import com.avant.joao.avant.fragments.BtFragment;
import com.avant.joao.avant.fragments.PatientFragment;
import com.avant.joao.avant.utils.BluetoothStatus;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;


public class MainActivity extends AppCompatActivity implements Observer{





    boolean mConnected = true;

    private DrawerLayout mDrawerLayout;
    private ActionBar mActionBar;
    private FirebaseAuth mAuth;

    private final static int ADD_PATIENT_CODE = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();






        BluetoothStateObservable.getInstance().addObserver(this);
        BluetoothStatus status =  new BluetoothStatus();

        status.deviceName = BluetoothStateObservable.getBluetoothName();
        status.connectionStatus =  BluetoothStateObservable.getBluetothState();

        BluetoothStateObservable.getInstance().updateStatus(status);










        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> user = new HashMap<>();
        user.put("name", mUser.getDisplayName());
        user.put("email",mUser.getEmail());

        db.collection("users").document(mUser.getUid()).set(user);





        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);





        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    2
            );
        }else{

        }


        NavigationView mNavigationView = findViewById(R.id.nav_view);

        setUpHeader(mUser,mNavigationView);

        mNavigationView.getMenu().getItem(0).setChecked(true);
        PatientFragment patientFragment = new PatientFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragments_container,patientFragment).commit();


        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        item.setChecked(true);

                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();


                        switch (item.getItemId()){
                            case R.id.nav_pacientes:
                                PatientFragment patientFragment = new PatientFragment();
                                ft.replace(R.id.fragments_container,patientFragment);

                                break;
                            case R.id.nav_bluetooth:
                                BtFragment btFragment = new BtFragment();

                                ft.replace(R.id.fragments_container,btFragment);
                                break;
                        }
                        mActionBar.setTitle(item.getTitle());
                        ft.commit();
                        mDrawerLayout.closeDrawers();

                        return true;


                    }
                }
        );

    }



    @Override
    public void update(Observable o, Object arg) {
        Log.d("Atualizou","True");
        BluetoothStatus status = (BluetoothStatus) arg;
        if(status.deviceName != null){
            mActionBar.setSubtitle("Conectado:"+status.deviceName);
            mConnected = true;
        }else{
            mActionBar.setSubtitle("Desconectado");
            mConnected = false;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 2: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {



                } else {


                }
                return;
            }


        }
    }




    private void setUpHeader(FirebaseUser user,NavigationView navigationView){
        View header = navigationView.getHeaderView(0);
        TextView headerTitle = (TextView) header.findViewById(R.id.header_title);
        headerTitle.setText(user.getDisplayName());
        TextView headerSubtitle = (TextView) header.findViewById(R.id.header_subtitle);
        headerSubtitle.setText(user.getEmail());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.actionbar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.add_patient_option:
                Intent startAddPatientActivity = new Intent(this,AddPatientActivity.class);
                startActivityForResult(startAddPatientActivity,ADD_PATIENT_CODE);

        }
        return super.onOptionsItemSelected(item);
    }



}
