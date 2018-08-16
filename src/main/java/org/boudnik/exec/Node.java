package org.boudnik.exec;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author Alexandre_Boudnik
 * @since 08/14/2018
 */
public interface Node<P> extends Serializable {
    Collection<Node<P>> getChildren();

    P payload();
}

