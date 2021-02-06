package com.example.healthappdemo.DataClass;

public class FoodInfo {
    String food_id;
    int food_img;//食物图片
    String name;//食物名
    int energy;//食物能量  每百克
    int level_img;//食物能量程度图片，绿黄红三个等级

    public FoodInfo(String food_id, int food_img, String name, int energy, int level_img) {
        this.food_id = food_id;
        this.food_img = food_img;
        this.name = name;
        this.energy = energy;
        this.level_img = level_img;
    }

    public String getFood_id() {
        return food_id;
    }

    public void setFood_id(String food_id) {
        this.food_id = food_id;
    }

    public int getFood_img() {
        return food_img;
    }

    public void setFood_img(int food_img) {
        this.food_img = food_img;
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

    public int getLevel_img() {
        return level_img;
    }

    public void setLevel_img(int level_img) {
        this.level_img = level_img;
    }
}
