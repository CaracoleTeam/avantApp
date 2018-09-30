package com.avant.joao.avant.entities;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;


@Entity(tableName = "patient")
public class PatientEntity {

    @PrimaryKey(autoGenerate = true)
    public int pid;

    public String name;
    public int age;

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

    public PatientEntity(String name, int age) {


        this.name = name;
        this.age = age;
    }



}
