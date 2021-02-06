package com.example.healthappdemo.DataClass;

public class EnergyInfo {
    String energyID;//能量ID，为食物或是运动
    int img;//图片
    String name;//名字
    int count;//数量
    int number;//能量

    public EnergyInfo(String energyID, int img, String name, int count, int number) {
        this.energyID = energyID;
        this.img = img;
        this.name = name;
        this.count = count;
        this.number = number;
    }

    public String getEnergyID() {
        return energyID;
    }

    public void setEnergyID(String energyID) {
        this.energyID = energyID;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
