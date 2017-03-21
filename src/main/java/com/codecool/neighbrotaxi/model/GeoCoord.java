package com.codecool.neighbrotaxi.model;


import org.springframework.data.geo.Point;

public class GeoCoord extends Point {


    public GeoCoord(double longitude, double latitude) {
        super(longitude, latitude);
    }

    public GeoCoord(Point point) {
        super(point);
    }

    public double getLongitude() {
        return super.getX();
    }

    public double getLatitude() {
        return super.getY();
    }
}
