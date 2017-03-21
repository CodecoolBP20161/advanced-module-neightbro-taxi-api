package com.codecool.neighbrotaxi.model.entities;

import com.codecool.neighbrotaxi.model.GeoCoord;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private GeoCoord start;
    private GeoCoord destination;
    private Date departure;

    @ManyToOne
    private Car car;
}
