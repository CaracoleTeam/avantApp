package com.avant.joao.avant.viewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.avant.joao.avant.utils.Step;

import java.util.ArrayList;
import java.util.List;

public class GaitCollectViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Step>> steps = new MutableLiveData<ArrayList<Step>>();

    public MutableLiveData<ArrayList<Step>> getSteps(){

        return this.steps;
    }

    public void updateSteps(ArrayList<Step> currentList ){

        this.steps.postValue(currentList);
    }

}
