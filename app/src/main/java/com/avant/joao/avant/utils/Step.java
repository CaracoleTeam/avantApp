package com.avant.joao.avant.utils;

public class Step {

    float time;
    float lenght;
    char foot;

    public char getFoot() {
        return foot;
    }

    public void setFoot(char foot) {
        this.foot = foot;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }


    public float getLenght() {
        return lenght;
    }

    public void setLenght(float lenght) {
        this.lenght = lenght;
    }

    public Step(float time, float lenght, char foot) {
        this.time = time;
        this.lenght = lenght;
        this.foot = foot;
    }

    public Step() {
    }
}
