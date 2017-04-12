package com.codecool.neighbrotaxi.service.implementation;

import com.codecool.neighbrotaxi.AbstractTest;
import com.codecool.neighbrotaxi.model.entities.Car;
import com.codecool.neighbrotaxi.repository.CarRepository;
import com.codecool.neighbrotaxi.service.CarService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.transaction.Transactional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

@Transactional
@MockBean(CarRepository.class)
public class CarServiceImplTest extends AbstractTest {
    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarService carService;

    private Car car;

    @Before
    public void setUp() throws Exception {
        car = new Car();
    }

    @Test
    public void findOne_ReturnValidObject() throws Exception {
        when(carRepository.findOne(anyInt())).thenReturn(car);

        Object returnedObject = carService.findOne(anyInt());

        assertEquals(car, returnedObject);
    }

}