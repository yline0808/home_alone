package com.example.myapplication;

import java.io.Serializable;
import java.util.ArrayList;

public class UserDTO implements Serializable {
    private String uid;
    private String name;
    private int age;
    private String sex;
    private int height;
    private int weight;
    private String location;
    private int walk;

    public UserDTO() {
    }

    public UserDTO(String uid, String name, int age, String sex, int height, int weight, String location, int walk) {
        this.uid = uid;
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.height = height;
        this.weight = weight;
        this.location = location;
        this.walk = walk;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getWalk() {
        return walk;
    }

    public void setWalk(int walk) {
        this.walk = walk;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", sex='" + sex + '\'' +
                ", height=" + height +
                ", weight=" + weight +
                ", location='" + location + '\'' +
                ", walk=" + walk +
                '}';
    }
}
