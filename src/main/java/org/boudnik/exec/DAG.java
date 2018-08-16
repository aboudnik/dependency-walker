package org.boudnik.exec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.RecursiveTask;
import java.util.function.BiFunction;

/**
 * @author Alexandre_Boudnik
 * @since 08/14/2018
 */
public class DAG<T> extends RecursiveTask<T> {

    private final Node<T> node;
    private final Builder<T> function;
    private final BiFunction<T, T, T> combiner;
    private final BiFunction<Builder<T>, Node<T>, T> invoker;

    public DAG(Node<T> root, Builder<T> function, BiFunction<T, T, T> combiner, BiFunction<Builder<T>, Node<T>, T> invoker) {
        this.node = root;
        this.function = function;
        this.combiner = combiner;
        this.invoker = invoker;
    }

    public DAG(Node<T> root, Builder<T> function, BiFunction<T, T, T> combiner) {
        this(root, function, combiner, Builder::apply);
    }

    public DAG(Node<T> root, Builder<T> function) {
        this(root, function, (o1, o2) -> null, Builder::apply);
    }

    @Override
    protected T compute() {
        Collection<Node<T>> children = node.getChildren();
        List<DAG<T>> tasks = new ArrayList<>();

        // initiate dependencies calculation
        for (Node<T> child : children) {
            DAG<T> element = new DAG<>(child, function, combiner);
            tasks.add(element);
            element.fork();
        }

        // collect results from dependencies
        T s = null;
        for (DAG<T> task : tasks) {
            s = combiner.apply(s, task.join());
        }

        // apply the function and return combined result
        return combiner.apply(s, invoker.apply(function, node));
    }
}
