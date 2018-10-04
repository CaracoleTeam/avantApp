package com.avant.joao.avant.dao;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.avant.joao.avant.entities.Gait;

import java.util.List;

@Dao
public interface GaitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertGait(Gait gait);

    @Query("SELECT * FROM gait WHERE patientId =:pid")
    LiveData<List<Gait>> getPatientGaits(final int pid);
}
