package com.resistthedevil5947.myapplication;


import org.json.JSONArray;

public class DataModel2 {


    String letter;
    int total;
    String title;


    public DataModel2(String letter, int total) {
        this.letter = letter;
        this.total = total;
    }

    public DataModel2(String title) {
        this.title = title;
    }



    public String getLetter() {
        return letter;
    }

    public int getTotal() {
        return total;
    }

    public String getTitle() {
        return title;
    }

}
