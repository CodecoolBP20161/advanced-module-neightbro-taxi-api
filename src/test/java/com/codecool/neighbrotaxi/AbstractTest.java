package com.codecool.neighbrotaxi;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.sql.DataSource;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = NeighBroTaxiApplication.class)
public abstract class AbstractTest {
}
