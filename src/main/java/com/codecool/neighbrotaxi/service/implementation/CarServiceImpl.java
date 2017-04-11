package com.codecool.neighbrotaxi.service.implementation;

import com.codecool.neighbrotaxi.model.entities.Car;
import com.codecool.neighbrotaxi.repository.CarRepository;
import com.codecool.neighbrotaxi.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarServiceImpl implements CarService {

    @Autowired
    private CarRepository repository;

    /**
     * Call the CarRepository's findOne method and returns this method's returned value.
     * @param id The id of the car we want to find in the Database.
     * @return a Car object mapped from the database based on the given ID.
     */
    @Override
    public Car findOne(Integer id) {
        return repository.findOne(id);
    }
}
