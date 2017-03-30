package com.codecool.neighbrotaxi.service.implementation;

import com.codecool.neighbrotaxi.model.GeoCoord;
import com.codecool.neighbrotaxi.model.entities.Route;
import com.codecool.neighbrotaxi.repository.RouteRepository;
import com.codecool.neighbrotaxi.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.Date;

@Service
public class RouteServiceImpl implements RouteService {
    @Autowired
    RouteRepository repository;


    /**
     * Check if the input parameters are valid or not. If not throws an error.
     * It occurs when the coords are bigger or smaller than +180 or -180. If the car field is null. If the departure date has already passed.
     * @param route An entity of the Route class. This will be saved into the database, if everything is OK.
     * @throws InvalidParameterException Throws when the input parameters are invalid
     */
    @Override
    public void saveNewRoute(Route route) throws InvalidParameterException {
        GeoCoord start = route.getStart();
        GeoCoord destination = route.getDestination();
        if (!GeoCoordValidation(start))
            throw new InvalidParameterException("Invalid start point longitude or latitude values!");
        if (!GeoCoordValidation(destination))
            throw new InvalidParameterException("Invalid destination point longitude or latitude values!");
        if (route.getCar() == null)
            throw new InvalidParameterException("There is no valid car!");
        System.out.println(route.getDeparture().toString());
        System.out.println(new Date().toString());
        if (route.getDeparture().before(new Date()))
            throw new InvalidParameterException("Given time has already passed!");

        repository.save(route);
    }

    /**
     * Validate the longitude and latitude coordinates of a geo point.
     * @param geoCoord A GeoCoord instance. This is the custom class we use for handling the coordinates.
     * @return false, if the coords are invalid, true if the coords are in the correct range.
     */
    private boolean GeoCoordValidation(GeoCoord geoCoord){
        return !(geoCoord.getLongitude() > 180.0 || geoCoord.getLongitude() < -180.0
                || geoCoord.getLatitude() > 180.0 || geoCoord.getLatitude() < -180.0);
    }
}
