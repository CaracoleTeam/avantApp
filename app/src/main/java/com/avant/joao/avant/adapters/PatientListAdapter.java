package com.avant.joao.avant.adapters;

import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avant.joao.avant.GaitsActivity;
import com.avant.joao.avant.R;
import com.avant.joao.avant.entities.PatientEntity;
import com.avant.joao.avant.utils.ParcelablePatient;

import java.io.ByteArrayInputStream;
import java.util.List;

public class PatientListAdapter extends RecyclerView.Adapter<PatientListAdapter.PatientListViewHolder> {

    private List<PatientEntity> mPatientsList;

    private Context context;


    public PatientListAdapter(List<PatientEntity> mPatientsList, Context context) {
        this.mPatientsList = mPatientsList;
        this.context = context;
    }

    public void setPatients(List<PatientEntity> patients){
        this.mPatientsList = patients;
    }


    @Override
    public PatientListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_item,parent,false);
        PatientListViewHolder plvh = new PatientListViewHolder(view);
        return plvh;
    }

    @Override
    public void onBindViewHolder(PatientListViewHolder holder, final int position) {
        if(mPatientsList != null){
            holder.mPatientName.setText(mPatientsList.get(position).getName());
            holder.mPatientAge.setText(String.valueOf(mPatientsList.get(position).getAge())+" anos");

            holder.mProfile.setImageBitmap(BitmapFactory.decodeStream(new ByteArrayInputStream(mPatientsList.get(position).getProfile())));

            holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PatientEntity patient = mPatientsList.get(position);
                    Log.d("Patient id: ",String.valueOf(mPatientsList.get(position).getPid()));
                    Intent startGaitActivityIntent = new Intent(context,GaitsActivity.class);
                    startGaitActivityIntent.putExtra("patient",new ParcelablePatient(patient.getPid(),patient.getName()));
                    view.getContext().startActivity(startGaitActivityIntent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(mPatientsList != null){
            return mPatientsList.size();
        }else{
            return 0;
        }
    }

    public  class PatientListViewHolder extends RecyclerView.ViewHolder{

        private final TextView mPatientName;
        private final TextView mPatientAge;
        private final ImageView mProfile;

        private final LinearLayout mLinearLayout;

        public PatientListViewHolder(View itemView) {
            super(itemView);

            mPatientName = itemView.findViewById(R.id.patient_name);
            mPatientAge = itemView.findViewById(R.id.patient_age);
            mProfile = itemView.findViewById(R.id.profile_image_item);
            mLinearLayout = itemView.findViewById(R.id.patient_item_layout);
        }
    }
}
