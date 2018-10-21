package com.avant.joao.avant.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.avant.joao.avant.entities.Gait;
import com.avant.joao.avant.entities.PatientEntity;


import java.util.List;

@Dao
public interface PatientDao {

    @Query("SELECT * FROM patient")
    LiveData<List<PatientEntity>> getAll();


    @Query("SELECT * FROM patient WHERE pid =:pid")
    LiveData<PatientEntity> getPatient(int pid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertUsers(PatientEntity patient);

    @Query("DELETE FROM patient where pid=:pid")
    void removeUser(final long  pid);



}
