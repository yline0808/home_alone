package com.example.myapplication;

import java.io.Serializable;
import java.util.ArrayList;

//implements Serializable
public class WalkDTO implements Serializable {
    private int goal;
    private int now;
    private ArrayList<Integer> week;

    public WalkDTO() {
    }

    public WalkDTO(int goal, int now) {
        this.goal = goal;
        this.now = now;
        this.week = new ArrayList<Integer>();
        for(int i = 0; i < 7; i ++){
            week.add(0);
        }
    }

    public WalkDTO(int goal, int now, ArrayList<Integer> week) {
        this.goal = goal;
        this.now = now;
        this.week = week;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public int getNow() {
        return now;
    }

    public void setNow(int now) {
        this.now = now;
    }

    public ArrayList<Integer> getWeek() {
        return week;
    }

    public void setWeek(ArrayList<Integer> week) {
        this.week = week;
    }
}
