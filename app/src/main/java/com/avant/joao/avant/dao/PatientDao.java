package com.avant.joao.avant.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.avant.joao.avant.entities.PatientEntity;


import java.util.List;

@Dao
public interface PatientDao {

    @Query("SELECT * FROM patient")
    LiveData<List<PatientEntity>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertUsers(PatientEntity patient);

}
