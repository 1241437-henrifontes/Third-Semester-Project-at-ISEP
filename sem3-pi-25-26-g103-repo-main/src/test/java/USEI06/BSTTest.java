package USEI06;

import Model.Trees.BST;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BSTTest {

    private BST<Integer> bst;

    private static class TestBST<E extends Comparable<E>> extends BST<E> {
        public int h(Node<E> n) { return height(n); }
        public void upd(Node<E> n) { updateHeight(n); }
        public Node<E> makeNode(E e, Node<E> l, Node<E> r) { return new Node<>(e,l,r); }
    }

    @BeforeEach
    void setUp() {
        bst = new BST<>();
    }

    private void insertAll(Integer... vals) {
        for (Integer v : vals) bst.insert(v);
    }

    @Test
    void root() {
        assertNull(bst.root());
        bst.insert(5);
        assertNotNull(bst.root());
        assertEquals(5, bst.root().getElement());
    }

    @Test
    void isEmpty() {
        assertTrue(bst.isEmpty());
        bst.insert(1);
        assertFalse(bst.isEmpty());
    }

    @Test
    void find() {
        insertAll(5,3,7,2,4,6,8);
        assertEquals(7, bst.find(bst.root(), 7).getElement());
        assertNull(bst.find(bst.root(), 10));
    }

    @Test
    void insert() {
        insertAll(5,3,7,3);
        Iterable<Integer> in = bst.inOrder();
        assertArrayEquals(new Integer[]{3,5,7}, toArray(in));
        assertEquals(3, bst.size());
    }

    @Test
    void remove() {
        insertAll(5,3,7,2,4,6,8);
        bst.remove(2);
        assertNull(bst.find(bst.root(), 2));
        bst.remove(6);
        bst.remove(7);
        assertNull(bst.find(bst.root(), 7));
        bst.remove(3);
        assertNull(bst.find(bst.root(), 3));
        assertEquals(3, bst.size());
        assertArrayEquals(new Integer[]{4,5,8}, toArray(bst.inOrder()));
    }

    @Test
    void size() {
        assertEquals(0, bst.size());
        insertAll(1,2,3);
        assertEquals(3, bst.size());
        bst.remove(2);
        assertEquals(2, bst.size());
    }

    @Test
    void height() {
        assertEquals(-1, bst.height());
        insertAll(1);
        assertEquals(0, bst.height());
    }

    @Test
    void testHeight() {
        TestBST<Integer> t = new TestBST<>();
        BST.Node<Integer> n1 = t.makeNode(1, null, null);
        BST.Node<Integer> n2 = t.makeNode(2, n1, null);
        assertEquals(0, t.h(n1));
        assertEquals(0, t.h(n2));
    }

    @Test
    void updateHeight() {
        TestBST<Integer> t = new TestBST<>();
        BST.Node<Integer> left = t.makeNode(1, null, null);
        left.setHeight(0);
        BST.Node<Integer> right = t.makeNode(3, null, null);
        right.setHeight(2);
        BST.Node<Integer> root = t.makeNode(2, left, right);
        t.upd(root);
        assertEquals(1 + Math.max(left.getHeight(), right.getHeight()), root.getHeight());
    }

    @Test
    void smallestElement() {
        assertNull(bst.smallestElement());
        insertAll(5,3,7,2,4);
        assertEquals(2, bst.smallestElement());
    }

    @Test
    void testSmallestElement() {
        insertAll(10,5,1);
        assertEquals(1, bst.smallestElement());
    }

    @Test
    void inOrder() {
        insertAll(5,3,7,2,4,6,8);
        assertArrayEquals(new Integer[]{2,3,4,5,6,7,8}, toArray(bst.inOrder()));
    }

    @Test
    void preOrder() {
        insertAll(5,3,7,2,4,6,8);
        assertArrayEquals(new Integer[]{5,3,2,4,7,6,8}, toArray(bst.preOrder()));
    }

    @Test
    void posOrder() {
        insertAll(5,3,7,2,4,6,8);
        assertArrayEquals(new Integer[]{2,4,3,6,8,7,5}, toArray(bst.posOrder()));
    }

    @Test
    void nodesByLevel() {
        insertAll(5,3,7,2,4,6,8);
        Map<Integer, List<Integer>> map = bst.nodesByLevel();
        assertEquals(List.of(5), map.get(0));
        assertEquals(List.of(3,7), map.get(1));
        assertEquals(List.of(2,4,6,8), map.get(2));
    }

    private static Integer[] toArray(Iterable<Integer> it) {
        List<Integer> list = new ArrayList<>();
        for (Integer i : it) list.add(i);
        return list.toArray(new Integer[0]);
    }

    @Test
    void removeNonExistentKeepsTree() {
        insertAll(2,1,3);
        int before = bst.size();
        bst.remove(99);
        assertEquals(before, bst.size());
        assertArrayEquals(new Integer[]{1,2,3}, toArray(bst.inOrder()));
    }

    @Test
    void removeRootScenarios() {
        bst.insert(10);
        assertEquals(1, bst.size());
        bst.remove(10);
        assertTrue(bst.isEmpty());
        insertAll(10, 5);
        assertEquals(2, bst.size());
        bst.remove(10);
        assertArrayEquals(new Integer[]{5}, toArray(bst.inOrder()));
    }

    @Test
    void traversalsOnEmptyAndSingleNode() {
        assertArrayEquals(new Integer[]{}, toArray(bst.inOrder()));
        assertArrayEquals(new Integer[]{}, toArray(bst.preOrder()));
        assertArrayEquals(new Integer[]{}, toArray(bst.posOrder()));
        bst.insert(7);
        assertArrayEquals(new Integer[]{7}, toArray(bst.inOrder()));
        assertArrayEquals(new Integer[]{7}, toArray(bst.preOrder()));
        assertArrayEquals(new Integer[]{7}, toArray(bst.posOrder()));
    }

    @Test
    void nodesByLevelOnSkewedTree() {
        insertAll(1,2,3,4);
        Map<Integer, List<Integer>> byLevel = bst.nodesByLevel();
        assertEquals(List.of(1), byLevel.get(0));
        assertEquals(List.of(2), byLevel.get(1));
        assertEquals(List.of(3), byLevel.get(2));
        assertEquals(List.of(4), byLevel.get(3));
    }
}
