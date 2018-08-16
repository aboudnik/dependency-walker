package org.boudnik.walker;

import java.io.Serializable;

/**
 * @author Alexandre_Boudnik
 * @since 08/15/2018
 */
public class Metadata implements Serializable {
    int id;
    String tablename;
    int lvl;
    String ParentTable;
}
