package com.codecool.neighbrotaxi.repository;

import com.codecool.neighbrotaxi.model.entities.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface for handling route table.
 */
@Repository
@Transactional
public interface RouteRepository extends JpaRepository<Route, Integer>{
}
