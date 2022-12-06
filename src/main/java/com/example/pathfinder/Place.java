package com.example.pathfinder;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;



public class Place extends Circle {

    private String city;

    public Place(String city, double x, double y){
        super(x, y, 10);
        this.city = city;
    }

    public void setColor(Color color ) {
        setFill(color);
    }

    public String getCity(){
        return city;
    }


    @Override
    public String toString(){
        return getCity();
    }
}