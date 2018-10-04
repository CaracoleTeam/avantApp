package com.avant.joao.avant.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avant.joao.avant.GaitsActivity;
import com.avant.joao.avant.R;
import com.avant.joao.avant.entities.Gait;

import java.util.List;

public class GaitAdapter extends RecyclerView.Adapter<GaitAdapter.GaitViewHolder> {

    private List<Gait> mGaits;
    private Context context;

    public GaitAdapter( Context context,List<Gait> mGaits) {
        this.context = context;
        this.mGaits = mGaits;

    }

    public void  setGaits(List<Gait> gaits){this.mGaits = gaits;}

    public void addGait(Gait gait){
        if(!mGaits.contains(gait)){
            mGaits.add(gait);
        }
    }

    @Override
    public GaitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gait_item,parent,false);
        GaitViewHolder viewHolder = new GaitViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(GaitViewHolder holder, int position) {
        if(mGaits != null){
            Gait gait = mGaits.get(position);

            holder.date.setText(gait.getGaitDay()+"/"+gait.getGaitMonth()+"/"+gait.getGaitYear());

            holder.cadence.setText(gait.getCadence()+"p/s");
            holder.time.setText(gait.getTime()+"s");
            holder.steps.setText(gait.getTotalSteps() + "passos");

        }
    }

    @Override
    public int getItemCount() {
        if(mGaits != null){
            return mGaits.size();
        }else{
            return 0;
        }
    }

    public class GaitViewHolder extends RecyclerView.ViewHolder{

        LinearLayout gaitContainer;
        TextView date;
        TextView cadence;
        TextView steps;
        TextView time;
        public GaitViewHolder(View itemView) {
            super(itemView);

            gaitContainer = itemView.findViewById(R.id.gait_item_container);
            date = itemView.findViewById(R.id.date_text);
            cadence = itemView.findViewById(R.id.cadence_text);
            steps = itemView.findViewById(R.id.steps_text);
            time = itemView.findViewById(R.id.gait_time_text);
        }
    }


}
