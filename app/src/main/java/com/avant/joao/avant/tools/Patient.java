package com.avant.joao.avant.tools;

import android.os.Parcelable;

import java.io.Serializable;

public class Patient implements Serializable {
    private String pacientName;
    private int numberOfTests;
    private int Age;

    public int getAge() {
        return Age;
    }

    public Patient(String pacientName, int numberOfTests, int age) {
        this.pacientName = pacientName;
        this.numberOfTests = numberOfTests;
        Age = age;
    }

    public void setAge(int age) {
        Age = age;
    }

    public String getPacientName() {
        return pacientName;
    }



    public void setPacientName(String pacientName) {
        this.pacientName = pacientName;
    }

    public int getNumberOfTests() {
        return numberOfTests;
    }

    public void setNumberOfTests(int numberOfTests) {
        this.numberOfTests = numberOfTests;
    }
}
