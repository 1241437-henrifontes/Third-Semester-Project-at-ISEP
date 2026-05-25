package USEI06;

import Model.Trees.AVLTree;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AVLTreeTest {

    private AVLTree<Integer> avl;

    @BeforeEach
    void setUp() {
        avl = new AVLTree<>();
    }

    @Test
    void search() {
        avl.insert(3);
        avl.insert(1);
        avl.insert(2);
        assertEquals(2, avl.search(2));
        assertNull(avl.search(99));
    }

    @Test
    void insert() {
        avl.insert(30);
        avl.insert(20);
        avl.insert(10);
        assertEquals(20, avl.root().getElement());

        avl.insert(40);
        avl.insert(50);
        assertEquals(40, avl.root().getRight().getElement());

        avl.insert(35);
        avl.insert(25);
        avl.insert(27);

        Integer[] expected = {10,20,25,27,30,35,40,50};
        assertArrayEquals(expected, toArray(avl.inOrder()));

        assertTrue(avl.height() >= 0);
    }

    @Test
    void remove() {
        int[] vals = {30,20,10,40,50,35,25,27};
        for (int v : vals) avl.insert(v);
        avl.remove(10);
        avl.remove(25);
        avl.remove(40);

        Integer[] expected = {20,27,30,35,50};
        assertArrayEquals(expected, toArray(avl.inOrder()));
        assertTrue(avl.height() >= 0);
    }

    @Test
    void testEquals() {
        AVLTree<Integer> a = new AVLTree<>();
        AVLTree<Integer> b = new AVLTree<>();
        int[] vals = {3,1,2,5,4};
        for (int v : vals) { a.insert(v); b.insert(v); }
        assertEquals(a, b);
        assertNotEquals(a, null);
        assertNotEquals(a, new Object());
    }

    @Test
    void testEquals1() {
        AVLTree<Integer> a = new AVLTree<>();
        AVLTree<Integer> b = new AVLTree<>();
        for (int v : new int[]{3,1,2,5}) a.insert(v);
        for (int v : new int[]{3,1,2,6}) b.insert(v);
        assertNotEquals(a, b);
    }

    private static Integer[] toArray(Iterable<Integer> it) {
        java.util.List<Integer> list = new java.util.ArrayList<>();
        for (Integer i : it) list.add(i);
        return list.toArray(new Integer[0]);
    }

    @Test
    void searchAbsentAndRootHeightNonNegative() {
        assertNull(avl.search(10));
        avl.insert(1);
        avl.insert(2);
        avl.insert(3);
        assertNull(avl.search(99));
        assertTrue(avl.height() >= 0);
    }
    
    @Test
    void removeNonExistentDoesNothing() {
        for (int v : new int[]{10,20,30}) avl.insert(v);
        Integer[] before = toArray(avl.inOrder());
        avl.remove(999);
        assertArrayEquals(before, toArray(avl.inOrder()));
    }

    @Test
    void heightNotExcessiveAfterManyInserts() {
        for (int i = 1; i <= 100; i++) avl.insert(i);
        assertTrue(avl.height() < 100);
        assertEquals(100, toArray(avl.inOrder()).length);
    }
}
