package com.avant.joao.avant.databases;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.avant.joao.avant.dao.GaitDao;
import com.avant.joao.avant.dao.PatientDao;
import com.avant.joao.avant.entities.Gait;
import com.avant.joao.avant.entities.PatientEntity;


@Database(entities = {PatientEntity.class,Gait.class},version = 3)
public abstract class PatientDatabase extends RoomDatabase{

    public abstract PatientDao patientDao();
    public abstract GaitDao gaitDao();

    private static volatile PatientDatabase INSTANCE;

    public static PatientDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (PatientDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),PatientDatabase.class,"patient_database").fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }

}
