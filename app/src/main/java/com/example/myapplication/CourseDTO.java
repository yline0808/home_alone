package com.example.myapplication;

import java.io.Serializable;
import java.util.ArrayList;

public class CourseDTO implements Serializable {
    private String title;
    private double km;
    private boolean flat;
    private ArrayList<String> wg;

    public CourseDTO() {
    }

    public CourseDTO(String title, double km, boolean flat, ArrayList<String> wg) {
        this.title = title;
        this.km = km;
        this.flat = flat;
        this.wg = wg;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getKm() {
        return km;
    }

    public void setKm(double km) {
        this.km = km;
    }

    public boolean getFlat() {
        return flat;
    }

    public void setFlat(boolean flat) {
        this.flat = flat;
    }

    public ArrayList<String> getWg() {
        return wg;
    }

    public void setWg(ArrayList<String> wg) {
        this.wg = wg;
    }

    @Override
    public String toString() {
        return "CourseDTO{\n" +
                "title=" + title + '\n' +
                ", km=" + km + '\n' +
                ", flat=" + flat + '\n' +
                ", wg=[" + wg + "]\n"+
                '}';
    }
}
