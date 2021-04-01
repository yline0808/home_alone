package com.example.myapplication;

public class CommentDTO {
    String name;
    String txt;
    String time;

    public CommentDTO() {
    }

    public CommentDTO(String name, String txt, String time) {
        this.name = name;
        this.txt = txt;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "CommentDTO{" +
                "name='" + name + '\'' +
                ", txt='" + txt + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
