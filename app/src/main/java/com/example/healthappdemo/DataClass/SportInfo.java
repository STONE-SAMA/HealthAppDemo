package com.example.healthappdemo.DataClass;

public class SportInfo {
    String sportID;
    int sport_img;//运动图片
    String name;//运动名称
    int energy;//能量

    public SportInfo(String sportID, int sport_img, String name, int energy) {
        this.sportID = sportID;
        this.sport_img = sport_img;
        this.name = name;
        this.energy = energy;
    }

    public String getSportID() {
        return sportID;
    }

    public void setSportID(String sportID) {
        this.sportID = sportID;
    }

    public int getSport_img() {
        return sport_img;
    }

    public void setSport_img(int sport_img) {
        this.sport_img = sport_img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }
}
