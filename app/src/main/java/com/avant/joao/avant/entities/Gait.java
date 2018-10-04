package com.avant.joao.avant.entities;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "gait",foreignKeys = @ForeignKey(entity = PatientEntity.class,parentColumns = "pid",childColumns = "patientId"))


public class Gait {

    @PrimaryKey(autoGenerate = true)
    public int gid;

    public int time;
    public int lSteps;
    public int rSteps;
    public int totalSteps;
    public int cadence;

    public int patientId;

    public int gaitDay;
    public int gaitMonth;
    public int gaitYear;


    public int getTotalSteps() {
        return totalSteps;
    }

    public void setTotalSteps(int totalSteps) {
        this.totalSteps = totalSteps;
    }

    public int getGaitDay() {
        return gaitDay;
    }

    public void setGaitDay(int gaitDay) {
        this.gaitDay = gaitDay;
    }

    public int getGaitMonth() {
        return gaitMonth;
    }

    public void setGaitMonth(int gaitMonth) {
        this.gaitMonth = gaitMonth;
    }

    public int getGaitYear() {
        return gaitYear;
    }

    public void setGaitYear(int gaitYear) {
        this.gaitYear = gaitYear;
    }

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

    public Gait( int time, int lSteps, int rSteps, int totalSteps,int cadence, int patientId, int gaitDay, int gaitMonth, int gaitYear) {

        this.time = time;
        this.lSteps = lSteps;
        this.rSteps = rSteps;
        this.totalSteps = totalSteps;
        this.cadence = cadence;
        this.patientId = patientId;
        this.gaitDay = gaitDay;
        this.gaitMonth = gaitMonth;
        this.gaitYear = gaitYear;
    }

    public Gait(){

    }
}
