package com.codecool.neighbrotaxi.service;


import com.codecool.neighbrotaxi.model.entities.Route;

import java.security.InvalidParameterException;

public interface RouteService {
    public void saveNewRoute(Route route) throws InvalidParameterException;
}
