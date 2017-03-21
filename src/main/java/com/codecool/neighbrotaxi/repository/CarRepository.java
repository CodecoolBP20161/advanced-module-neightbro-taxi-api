package com.codecool.neighbrotaxi.repository;

import com.codecool.neighbrotaxi.model.entities.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

@RepositoryRestResource(collectionResourceRel = "userCars", path = "user-cars")
@Transactional
public interface CarRepository extends JpaRepository<Car, Integer> {
}
