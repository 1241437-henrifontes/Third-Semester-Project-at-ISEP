package Model.Trees;

/**
 * AVL Tree implementation extending a basic Binary Search Tree.
 *
 * @param <E> type of elements stored in the tree
 */
public class AVLTree <E extends Comparable<E>> extends BST<E> {
    private int balanceFactor(Node<E> node) {
        if (node == null) {
            return 0;
        }
        return height(node.getLeft()) - height(node.getRight());
    }

    private Node<E> rightRotation(Node<E> node) {
        Node<E> leftson = node.getLeft();
        node.setLeft(leftson.getRight());
        leftson.setRight(node);
        node = leftson;
        return node;
    }

    private Node<E> leftRotation(Node<E> node) {
        Node<E> rightson = node.getRight();
        node.setRight(rightson.getLeft());
        rightson.setLeft(node);
        node = rightson;
        return node;
    }

    private Node<E> twoRotations(Node<E> node) {
        if (balanceFactor(node) < 0) {
            node.setLeft(leftRotation(node.getLeft()));
            node = rightRotation(node);
        }
        else {
            node.setRight(rightRotation(node.getRight()));
            node = leftRotation(node);
        }
        return node;
    }

    private Node<E> balanceNode(Node<E> node) {
        int bf = balanceFactor(node);

        if (bf > 1) {
            if (balanceFactor(node.getLeft()) < 0) {
                node.setLeft(leftRotation(node.getLeft()));
            }
            node = rightRotation(node);
        } else if (bf < -1) {
            if (balanceFactor(node.getRight()) > 0) {
                node.setRight(rightRotation(node.getRight()));
            }
            node = leftRotation(node);
        }

        return node;
    }

    /**
     * Searches for an element in the AVL tree.
     *
     * @param element element to search
     * @return the element if found, null otherwise
     */
    public E search(E element) {
        return search(root, element);
    }

    private E search(Node<E> node, E element) {
        if (node == null) return null;

        int cmp = element.compareTo(node.getElement());

        if (cmp == 0) {
            return node.getElement();
        } else {
            if (cmp < 0) {
                return search(node.getLeft(), element);
            } else {
                return search(node.getRight(), element);
            }
        }
    }

    /**
     * Inserts an element into the AVL tree and rebalances if necessary.
     *
     * @param element element to insert
     */
    @Override
    public void insert(E element) {
        root = insert(element, root);
    }

    private Node<E> insert(E element, Node<E> node) {
        if (node == null)
            return new Node<>(element, null, null);

        if (node.getElement() == element)
            node.setElement(element);
        else if (node.getElement().compareTo(element) > 0) {
            node.setLeft(insert(element, node.getLeft()));
            node = balanceNode(node);
        }
        else {
            node.setRight(insert(element, node.getRight()));
            node = balanceNode(node);
        }

        updateHeight(node);
        return node;
    }

    /**
     * Removes an element from the AVL tree and rebalances if necessary.
     *
     * @param element element to remove
     */
    @Override
    public void remove(E element) {
        root = remove(element, root());
    }

    private Node<E> remove(E element, Node<E> node) {
        if (node == null) {
            return null;
        }
        if (element.compareTo(node.getElement()) == 0) {
            if (node.getLeft() == null && node.getRight() == null) {
                return null;
            }
            if (node.getLeft() == null) {
                return node.getRight();
            }
            if (node.getRight() == null) {
                return node.getLeft();
            }
            E smallElem = smallestElement(node.getRight());
            node.setElement(smallElem);
            node.setRight(remove(smallElem, node.getRight()));
            node = balanceNode(node);
        } else if (element.compareTo(node.getElement()) < 0) {
            node.setLeft(remove(element, node.getLeft()));
            node = balanceNode(node);
        } else {
            node.setRight(remove(element, node.getRight()));
            node = balanceNode(node);
        }

        updateHeight(node);
        return node;
    }

    /**
     * Compares this AVL tree with another for structural and element equality.
     *
     * @param otherObj the other object
     * @return true if both trees are equal, false otherwise
     */
    public boolean equals(Object otherObj) {
        if (this == otherObj)
            return true;

        if (otherObj == null || this.getClass() != otherObj.getClass())
            return false;

        AVLTree<E> second = (AVLTree<E>) otherObj;
        return equals(root, second.root);
    }

    public boolean equals(Node<E> root1, Node<E> root2) {
        if (root1 == null && root2 == null)
            return true;
        else if (root1 != null && root2 != null) {
            if (root1.getElement().compareTo(root2.getElement()) == 0) {
                return equals(root1.getLeft(), root2.getLeft())
                        && equals(root1.getRight(), root2.getRight());
            } else
                return false;
        } else return false;
    }
}
