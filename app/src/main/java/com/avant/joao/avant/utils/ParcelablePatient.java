package com.avant.joao.avant.utils;

import java.io.Serializable;

public class ParcelablePatient implements Serializable {
    private int pid;
    private String patientName;

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public ParcelablePatient(int pid, String patientName) {
        this.pid = pid;
        this.patientName = patientName;
    }
}
