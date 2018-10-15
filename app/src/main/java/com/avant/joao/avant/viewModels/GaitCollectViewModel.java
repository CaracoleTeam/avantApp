package com.avant.joao.avant.viewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.avant.joao.avant.utils.Step;

import java.util.List;

public class GaitCollectViewModel extends ViewModel {

    private MutableLiveData<List<Step>> mSteps;


    public LiveData<List<Step>> getSteps(){
        return this.mSteps;
    }

    public void addStep(Step step){
        List<Step> currentSteps = this.mSteps.getValue();
        currentSteps.add(step);
        this.mSteps.postValue(currentSteps);
    }


}
