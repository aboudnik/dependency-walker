package org.boudnik.exec;

import java.io.Serializable;

/**
 * @author Alexandre_Boudnik
 * @since 08/14/2018
 */
public interface Builder<R> extends java.util.function.Function<Node<R>, R>, Serializable {
}
