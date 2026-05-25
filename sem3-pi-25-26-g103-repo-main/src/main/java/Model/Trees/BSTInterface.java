package Model.Trees;

import java.util.List;
import java.util.Map;

/**
 *
 * @author DEI-ESINF
 * @param <E>
 */
public interface BSTInterface<E> {
    boolean isEmpty();
    void insert(E element);
    void remove(E element);

    int size();
    int height();

    E smallestElement();
    Iterable<E> inOrder();
    Iterable<E> preOrder();
    Iterable<E> posOrder();
    Map<Integer,List<E>> nodesByLevel();
}
