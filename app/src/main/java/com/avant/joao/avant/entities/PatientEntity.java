package com.avant.joao.avant.entities;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;

import java.io.Serializable;


@Entity(tableName = "patient")
public class PatientEntity  {

    @PrimaryKey(autoGenerate = true)
    public int pid;

    public String name;
    public int age;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    public byte [] profile;

    public byte [] getProfile() {
        return this.profile;
    }

    public void setProfile(byte [] profile) {
        this.profile = profile;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public PatientEntity(String name, int age,byte [] profile) {


        this.name = name;
        this.age = age;
        this.profile = profile;
    }

    public PatientEntity(int id,String name, int age,byte [] profile) {

        this.pid = id;
        this.name = name;
        this.age = age;
        this.profile = profile;
    }

    public PatientEntity(){

    }





}
