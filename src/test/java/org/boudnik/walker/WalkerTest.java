package org.boudnik.walker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Map;

/**
 * @author Alexandre_Boudnik
 * @since 08/15/2018
 */
public class WalkerTest {
    Walker walker;

    @Before
    public void setUp() throws Exception {
        walker = Walker.getInstance();
    }


    @Test
    public void build() throws SQLException {
        walker.build();
        System.out.println(walker.map);
    }

    @After
    public void tearDown() throws Exception {
        walker.close();
    }

}