package org.boudnik.walker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Alexandre_Boudnik
 * @since 08/15/2018
 */
public class WalkerTest {
    public static final String DATABASE = "PITEDRTEST";
    public static final Walker.Table ROOT = new Walker.Table(DATABASE,"dbo", "M");
    public static final Walker.Table BOTTOM = new Walker.Table(DATABASE, "dbo", "P2");
    Walker walker;

    @Before
    public void setUp() throws Exception {
        walker = new Walker("18.218.197.170", 1433, DATABASE);
        walker.build();
    }

    @After
    public void tearDown() throws Exception {
        walker.close();
    }


    @Test
    public void walkDown() {
        walker.walkDown(ROOT, walker.PRINT);
    }

    @Test
    public void walkUp() {
        walker.walkUp(BOTTOM, walker.PRINT);
    }

    @Test
    public void deleteFromCLAIM_BATCH_LOG() {
        walker.delete(new Walker.Table(DATABASE, "dbo", "CLAIM_BATCH_LOG"), "CLAIM_BATCH_LOG.etl_batch_id in (select * from @list)");
    }

    @Test
    public void deleteFromDIM_VA_CLAIM() {
        walker.delete(new Walker.Table(DATABASE, "dbo", "DIM_VA_CLAIM"), "DIM_VA_CLAIM.claim_key in (select * from @list)");
    }
}