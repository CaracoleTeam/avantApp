package com.avant.joao.avant;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avant.joao.avant.entities.PatientEntity;
import com.avant.joao.avant.viewModels.PatientViewModel;
import com.google.android.gms.common.util.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class AddPatientActivity extends AppCompatActivity {

    private static final int GET_PROFILE_IMAGE_CODE = 8900;

    TextView patientNameInput;
    TextView patientAgeInput;
    ImageView profileImage;
    byte [] storagedProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);
        getSupportActionBar().setTitle(getResources().getString(R.string.add_patient_activity_title));
        patientNameInput = findViewById(R.id.patient_name_input);
        patientAgeInput = findViewById(R.id.patient_age_input);
        profileImage = findViewById(R.id.profile_input);

        updateImage(Uri.parse("android.resource://com.avant.joao.avant/" + R.drawable.no_profile),0,0);

        profileImage.setOnClickListener(onImageClickListener);



    }

    View.OnClickListener onImageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent getProfileImageIntent = new Intent();
            getProfileImageIntent.setType("image/*");
            getProfileImageIntent.setAction(Intent.ACTION_GET_CONTENT);
            getProfileImageIntent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(getProfileImageIntent,GET_PROFILE_IMAGE_CODE);

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == GET_PROFILE_IMAGE_CODE && resultCode == RESULT_OK){
            Uri uri =data.getData();
            updateImage(uri,100,100);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void updateImage(Uri uri , int width,int height){

        try{
            Bitmap rawImage = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

            int squareSide = 0;
            int imageWidth = rawImage.getWidth();
            int imageHeight = rawImage.getHeight();
            int widthMoviment = 0,heightMoviment = 0;

            if(imageWidth > imageHeight){
                squareSide = imageHeight;
                widthMoviment = (imageWidth-squareSide)/2;
            }else if(imageHeight>imageWidth){
                squareSide = imageWidth;
                heightMoviment = (imageHeight-squareSide)/2;
            }
            squareSide = ( imageWidth >= imageHeight) ? imageHeight:imageWidth;

            Bitmap dst = Bitmap.createBitmap(rawImage, widthMoviment, heightMoviment, squareSide, squareSide);

            profileImage.setImageBitmap(dst);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            dst.compress(Bitmap.CompressFormat.PNG, 100, stream);

            storagedProfile = stream.toByteArray();

            stream.close();

        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.add_patient_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.finish_add_patient:
                validate();

        }
        return super.onOptionsItemSelected(item);
    }

    private void validate(){
        if(patientNameInput.getText().toString().matches("") || patientAgeInput.getText().toString().matches("")){
            Toast.makeText(this,"Campos vazios",Toast.LENGTH_SHORT).show();
        }else{
            submitPatient();
        }
    }

    private void submitPatient(){
        PatientEntity patient = new PatientEntity(patientNameInput.getText().toString(), Integer.parseInt(patientAgeInput.getText().toString()),storagedProfile);

        PatientViewModel patientViewModel = ViewModelProviders.of(this).get(PatientViewModel.class);
        patientViewModel.insertPatient(patient);

        Intent it = new Intent();
        setResult(RESULT_OK,it);
        finish();
    }
}
