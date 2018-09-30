package com.avant.joao.avant.entities;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "gait",foreignKeys = @ForeignKey(entity = PatientEntity.class,parentColumns = "pid",childColumns = "patientId"))


public class Gait {

    @PrimaryKey
    public int gid;

    public int time;
    public int lSteps;
    public int rSteps;
    public int cadence;

    public int patientId;

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getlSteps() {
        return lSteps;
    }

    public void setlSteps(int lSteps) {
        this.lSteps = lSteps;
    }

    public int getrSteps() {
        return rSteps;
    }

    public void setrSteps(int rSteps) {
        this.rSteps = rSteps;
    }

    public int getCadence() {
        return cadence;
    }

    public void setCadence(int cadence) {
        this.cadence = cadence;
    }

    public Gait(int gid, int time, int lSteps, int rSteps, int cadence,int patientId) {

        this.gid = gid;
        this.time = time;
        this.lSteps = lSteps;
        this.rSteps = rSteps;
        this.cadence = cadence;
        this.patientId = patientId;
    }
}
