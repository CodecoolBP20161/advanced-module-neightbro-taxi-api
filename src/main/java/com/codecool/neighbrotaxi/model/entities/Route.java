package com.codecool.neighbrotaxi.model.entities;

import com.codecool.neighbrotaxi.model.GeoCoord;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private GeoCoord start;
    private GeoCoord destination;
    private Date departure;

    @ManyToOne
    private Car car;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public GeoCoord getStart() {
        return start;
    }

    public void setStart(GeoCoord start) {
        this.start = start;
    }

    public GeoCoord getDestination() {
        return destination;
    }

    public void setDestination(GeoCoord destination) {
        this.destination = destination;
    }

    public Date getDeparture() {
        return departure;
    }

    public void setDeparture(Date departure) {
        this.departure = departure;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }
}
