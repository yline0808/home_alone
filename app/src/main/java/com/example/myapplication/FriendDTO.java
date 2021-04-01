package com.example.myapplication;

public class FriendDTO {
    private String name;
    private String loc;
    private String fuid;
    private int walk;

    public FriendDTO() {
    }

    public FriendDTO(String name, String loc, int walk, String fuid) {
        this.name = name;
        this.loc = loc;
        this.walk = walk;
        this.fuid = fuid;
    }

    public String getFuid() {
        return fuid;
    }

    public void setFuid(String fuid) {
        this.fuid = fuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public int getWalk() {
        return walk;
    }

    public void setWalk(int walk) {
        this.walk = walk;
    }
}
