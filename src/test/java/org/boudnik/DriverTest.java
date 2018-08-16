package org.boudnik;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Alexandre_Boudnik
 * @since 08/15/2018
 */
public class DriverTest {

    private Connection connection;

    @Before
    public void setUp() throws Exception {
        connection = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;database=test;integratedSecurity=true");
    }

    @After
    public void tearDown() throws Exception {
        connection.close();
    }

    @Test
    public void jdbc() throws Exception {
        try (PreparedStatement statement = connection.prepareStatement("select * from q1")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    System.out.println("object = " + resultSet.getObject(1));
                }
            }
        }
    }
}