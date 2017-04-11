package com.codecool.neighbrotaxi.service.implementation;

import com.codecool.neighbrotaxi.AbstractTest;
import com.codecool.neighbrotaxi.model.GeoCoord;
import com.codecool.neighbrotaxi.model.entities.Car;
import com.codecool.neighbrotaxi.model.entities.Route;
import com.codecool.neighbrotaxi.repository.RouteRepository;
import com.codecool.neighbrotaxi.service.RouteService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.transaction.Transactional;

import java.security.InvalidParameterException;
import java.util.Date;

import static org.mockito.Mockito.*;

@Transactional
@MockBean(RouteRepository.class)
public class RouteServiceImplTest extends AbstractTest {
    private Route route;

    @Autowired
    private RouteRepository repository;

    @Autowired
    private RouteService service;


    @Before
    public void setUp() throws Exception {
        route = new Route();
        route.setStart(new GeoCoord(1.0, 2.0));
        route.setDestination(new GeoCoord(3.0, 4.0));
        route.setCar(new Car());
        Date date = new Date();
        date.setTime(date.getTime() + 10000000);
        route.setDeparture(date);

    }

    @Test
    public void saveNewRoute_CallTheSaveMethodOfTheRepository() throws Exception {

        service.saveNewRoute(route);

        verify(repository, times(1)).save(route);
    }

    @Test(expected = InvalidParameterException.class)
    public void saveNewRoute_InvalidStartPointLongCoordInNegative_ThrowInvalidParameterException() throws Exception {
        route.setStart(new GeoCoord(-181.0, 2.0));

        service.saveNewRoute(route);

        verify(repository, never()).save(any(Route.class));
    }

    @Test(expected = InvalidParameterException.class)
    public void saveNewRoute_InvalidStartPointLongCoordInPositive_ThrowInvalidParameterException() throws Exception {
        route.setStart(new GeoCoord(181.0, 2.0));

        service.saveNewRoute(route);

        verify(repository, never()).save(any(Route.class));
    }

    @Test(expected = InvalidParameterException.class)
    public void saveNewRoute_InvalidStartPointLatCoordInNegative_ThrowInvalidParameterException() throws Exception {
        route.setStart(new GeoCoord(1.0, -200.0));

        service.saveNewRoute(route);

        verify(repository, never()).save(any(Route.class));
    }

    @Test(expected = InvalidParameterException.class)
    public void saveNewRoute_InvalidStartPointLatCoordInPositive_ThrowInvalidParameterException() throws Exception {
        route.setStart(new GeoCoord(1.0, 200.0));

        service.saveNewRoute(route);

        verify(repository, never()).save(any(Route.class));
    }

    @Test(expected = InvalidParameterException.class)
    public void saveNewRoute_InvalidDestinationPointLongCoordInNegative_ThrowInvalidParameterException() throws Exception {
        route.setDestination(new GeoCoord(-181.0, 2.0));

        service.saveNewRoute(route);

        verify(repository, never()).save(any(Route.class));
    }

    @Test(expected = InvalidParameterException.class)
    public void saveNewRoute_InvalidDestinationPointLongCoordInPositive_ThrowInvalidParameterException() throws Exception {
        route.setDestination(new GeoCoord(181.0, 2.0));

        service.saveNewRoute(route);

        verify(repository, never()).save(any(Route.class));
    }

    @Test(expected = InvalidParameterException.class)
    public void saveNewRoute_InvalidDestinationPointLatCoordInNegative_ThrowInvalidParameterException() throws Exception {
        route.setDestination(new GeoCoord(1.0, -200.0));

        service.saveNewRoute(route);

        verify(repository, never()).save(any(Route.class));
    }

    @Test(expected = InvalidParameterException.class)
    public void saveNewRoute_InvalidDestinationPointLatCoordInPositive_ThrowInvalidParameterException() throws Exception {
        route.setDestination(new GeoCoord(1.0, 200.0));

        service.saveNewRoute(route);

        verify(repository, never()).save(any(Route.class));
    }

    @Test(expected = InvalidParameterException.class)
    public void saveNewRoute_ThereIsNoGivenCar_ThrowInvalidParameterException() throws Exception {
        route.setCar(null);

        service.saveNewRoute(route);

        verify(repository, never()).save(any(Route.class));
    }

    @Test(expected = InvalidParameterException.class)
    public void saveNewRoute_DepartureTimeHasAlreadyPassed_ThrowInvalidParameterException() throws Exception {
        Date date = new Date();
        date.setTime(date.getTime() - 10000);
        route.setDeparture(date);

        service.saveNewRoute(route);

        verify(repository, never()).save(any(Route.class));
    }
}