package com.avant.joao.avant;

import android.app.AlertDialog;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
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

import com.avant.joao.avant.databases.PatientDatabase;
import com.avant.joao.avant.entities.PatientEntity;
import com.avant.joao.avant.fragments.BtFragment;
import com.avant.joao.avant.fragments.PatientFragment;
import com.avant.joao.avant.tools.Patient;
import com.avant.joao.avant.tools.User;
import com.avant.joao.avant.viewModels.PatientViewModel;

import org.w3c.dom.Text;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private User mUser;
    private DrawerLayout mDrawerLayout;
    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        Intent authenticationIntent  = getIntent();
        mUser = (User) authenticationIntent.getSerializableExtra("user");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);


        PatientViewModel patientViewModel = ViewModelProviders.of(this).get(PatientViewModel.class);

        patientViewModel.insertPatient(new PatientEntity("João",19));
        patientViewModel.getAllPatients().observe(this, new Observer<List<PatientEntity>>() {
            @Override
            public void onChanged(@Nullable List<PatientEntity> patientEntities) {

            }
        });





        NavigationView mNavigationView = findViewById(R.id.nav_view);

        setUpHeader(mUser,mNavigationView);

        mNavigationView.getMenu().getItem(0).setChecked(true);
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

    private void setUpHeader(User user,NavigationView navigationView){
        View header = navigationView.getHeaderView(0);
        TextView headerTitle = (TextView) header.findViewById(R.id.header_title);
        headerTitle.setText(user.getName());
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

        }
        return super.onOptionsItemSelected(item);
    }

}
