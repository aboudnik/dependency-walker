package org.boudnik.walker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Alexandre_Boudnik
 * @since 08/15/2018
 */
public class WalkerTest {
    public static final Walker.FQN ROOT = new Walker.FQN("test", "dbo", "M");
    public static final Walker.FQN BOTTOM = new Walker.FQN("test", "dbo", "P2");
    Walker walker;

    @Before
    public void setUp() throws Exception {
        walker = Walker.getInstance();
        walker.build();
    }

    @After
    public void tearDown() throws Exception {
        walker.close();
    }


    @Test
    public void delete() {
        walker.forEach(walker.delete);
    }

    @Test
    public void walkDown() {
        walker.walkDown(ROOT, Walker::PRINT);
    }

    @Test
    public void walkUp() {
        walker.walkUp(BOTTOM, Walker::PRINT);
    }

    @Test
    public void copy() {
        walker.copy();
    }
}