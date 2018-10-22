package com.avant.joao.avant.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avant.joao.avant.R;
import com.avant.joao.avant.utils.Step;

import java.util.ArrayList;
import java.util.List;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepsViewHolder> {

    ArrayList<Step> mStepList;
    private Context mContext;
    private int lastPosition = -1;

    public StepsAdapter(ArrayList<Step> steps, Context context)
    {
        this.mStepList = steps;
        this.mContext = context;
    }


    public void addStep(Step step){
        if(this.mStepList != null && !this.mStepList.contains(step)){
            this.mStepList.add(step);
        }
    }

    public ArrayList<Step> getSteps(){
        return this.mStepList;
    }

    @NonNull
    @Override
    public StepsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.step_item,viewGroup,false);
        StepsViewHolder plvh = new StepsViewHolder(view);
        return plvh;
    }

    @Override
    public void onBindViewHolder(@NonNull StepsViewHolder stepsViewHolder, int position) {
        if(this.mStepList != null){
            Step currentStep = this.mStepList.get(position);
            if(currentStep.getFoot() == 'D'){
                stepsViewHolder.leftStepBall.setVisibility(View.GONE);
            }else {
                stepsViewHolder.rightStepBall.setVisibility(View.GONE);

            }

            stepsViewHolder.mStepLenghtText.setText(String.valueOf(currentStep.getLenght())+" cm");
            stepsViewHolder.mStepTimeText.setText(String.valueOf(currentStep.getTime())+" s");
            setAnimation(stepsViewHolder.stepContainer,position);
        }
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
       if(this.mStepList != null){
           return mStepList.size();
       }else{
           return 0;
       }
    }



    public class StepsViewHolder extends RecyclerView.ViewHolder{

        private final TextView mStepLenghtText;
        private final TextView mStepTimeText;
        private final ImageView leftStepBall;
        private final ImageView rightStepBall;
        private final LinearLayout stepContainer;

        public StepsViewHolder(View itemView) {
            super(itemView);

            stepContainer = (LinearLayout) itemView.findViewById(R.id.step_container);
            mStepLenghtText = (TextView) itemView.findViewById(R.id.step_lenght);
            mStepTimeText = (TextView) itemView.findViewById(R.id.step_time);

            leftStepBall = (ImageView) itemView.findViewById(R.id.left_step_ball);
            rightStepBall = (ImageView) itemView.findViewById(R.id.right_step_ball);



        }

    }
}
