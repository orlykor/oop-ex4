package oop.ex4.data_structures;

import java.util.Iterator;
import java.util.NoSuchElementException;

import oop.ex4.data_structures.AvlNode;

public class AvlTree implements Iterable<Integer> {

    /** No nodes in the tree */
    private static final int NO_ELEMENTS = 0;

    /** The counting of depth starts from that */
    private static final int MINIMAL_DEPTH = -1;

    /** The sub-left tree is not balanced */
    private static final int LEFT_NOT_BALANCED = -2;

    /** The sub-right tree is not balanced */
    private static final int RIGHT_NOT_BALANCED = 2;

    /** The amount of nodes in the tree */
    private int size;

    /** The root of the tree */
    private AvlNode root;

    /**
     * The default constructor.
     */
    public AvlTree() {

	this.root = null;
	size = NO_ELEMENTS;

    }

    /**
     * A constructor that builds the tree by adding the elements in the input array one by one. If a value
     * appears more than once in the list, only the first appearance is added.
     * 
     * @param data the values to add to tree.
     */
    public AvlTree(int[] data) {
	for (int item : data) {
	    Integer value = item;
	    if (value != null) {
		this.add(item);
	    }
	}
    }

    /**
     * copy constructor that creates a deep copy of the given AvlTree. This means that for every node or any
     * other internal object of the given tree, a new, identical object, is instantiated for the new tree
     * (the internal object is not simply referenced from it). The new tree must contain all the values of
     * the given tree, but not necessarily in the same structure.
     * 
     * @param avlTree an AVL tree.
     */
    public AvlTree(AvlTree avlTree) {
	for (Integer item : avlTree) {
	    this.add(item);
	}
    }

    /**
     * Add a new node with the given key to the tree.
     * 
     * @param newValue the value of the new node to add.
     * @return true if the value to add is not already in the tree and it was successfully added, false
     *         otherwise.
     */
    public boolean add(int newValue) {
	if (search(this.root, newValue) == null) {
	    AvlNode cur = new AvlNode(newValue);
	    insert(this.root, cur);
	    size++;
	    return true;
	}
	return false;
    }

    /**
     * 
     * Check whether the tree contains the given input value.
     * 
     * @param val the value to search for.
     * @return the depth of the node (0 for the root) with the given value if it was found in the tree, −1
     *         otherwise.
     */
    public int contains(int searchVal) {
	AvlNode cur = search(this.root, searchVal);
	if (cur == null) {
	    return -1;
	}
	int depth = MINIMAL_DEPTH;
	while (cur != null) {
	    depth++;
	    cur = cur.parent;
	}
	return depth;
    }

    /*
     * search recursively for the node of the input value.
     * 
     * @param t the root of the tree
     * @param searchVal the value of th enode we search for
     * @return if we found the node return the node itself, if not return null.
     */
    private AvlNode search(AvlNode r, int searchVal) {
	while (r != null) {
	    if (searchVal < r.data) {
		return search(r.left, searchVal);
	    } else if (searchVal > r.data) {
		return search(r.right, searchVal);
	    } else if (searchVal == r.data) {
		return r;
	    }
	}
	return null;

    }

    /**
     * Removes the node with the given value from the tree, if it exists.
     * if it has one or two child it uses the remove method to remove the node.
     * 
     * @param toDelete the value to remove from the tree.
     * @return true if the given value was found and deleted, false otherwise.
     */
    public boolean delete(int toDelete) {

	AvlNode x = search(this.root, toDelete);
	if (x == null) {
	    return false;
	}
	if (x.parent == null) {
	    this.root = null;
	    size--;
	} else if (x.left == null && x.right == null) {
	    removeNode(toDelete, x.parent, null);

	} else if (x.left != null && x.right == null) {
	    removeNode(toDelete, x.parent, x.left);

	} else if (x.left == null && x.right != null) {
	    removeNode(toDelete, x.parent, x.right);

	} else if (x.left != null && x.right != null) {
	    AvlNode xSucc = treeSuccessor(x);
	    int succData = xSucc.data;
	    xSucc.data = x.data;
	    x.data = succData;
	    delete(xSucc.data);
	    checkBalance(this.root);
	    size--;
	}

	return true;
    }

    /*
     * removes the given node from the tree
     * 
     * @param toDelete the node we want to delete
     * @param parent the parent of the node.
     * @param nodeChild the new node to connect the parent to.
     */
    private void removeNode(int toDelete, AvlNode parent, AvlNode nodeChild) {
	if (toDelete < parent.data) {
	    parent.left = nodeChild;
	} else {
	    parent.right = nodeChild;
	}
	size--;
	checkBalance(this.root);
    }

    /*
     * check the balance between the heights of the nodes, if it is 2 or -2 rotate in the given direction
     * to correct the avl tree.
     * @param cur the node we want to check if an interruption of the balanced accrued.
     */
    private void checkBalance(AvlNode cur) {

	setBalance(cur);
	int balance = cur.balance;

	if (balance == LEFT_NOT_BALANCED) {
	    if (height(cur.left.left) >= height(cur.left.right)) {
		cur = rotateRight(cur);
	    } else {
		cur = doubleRotateLeftRight(cur);
	    }
	} else if (balance == RIGHT_NOT_BALANCED) {
	    if (height(cur.right.right) >= height(cur.right.left)) {
		cur = rotateLeft(cur);
	    } else {
		cur = doubleRotateRightLeft(cur);
	    }
	}
	if (cur.parent != null) {
	    checkBalance(cur.parent);
	} else {
	    this.root = cur;
	}
    }

    /**
     * @return the number of nodes in the tree.
     */
    public int size() {
	return size;
    }

    /**
     * 
     * @return an iterator on the Avl Tree. The returned iterator iterates over the tree nodes in an
     *         ascending order, and does NOT implement the remove() method.
     */
    public Iterator<Integer> iterator() {
	class TreeIterator implements Iterator<Integer> {

	    AvlNode currMin = findMinElement(root);

	    @Override
	    public boolean hasNext() {
		return currMin != null;
	    }

	    @Override
	    public Integer next() {
		if (currMin == null)
		    throw new NoSuchElementException();
		int temp = currMin.data;
		currMin = treeSuccessor(currMin);
		return temp;
	    }

	    public void remove() {
		throw new UnsupportedOperationException();
	    }
	}
	return new TreeIterator();
    }

    /*
     * finds the minimum element in the tree nodes.
     * 
     * @param x the node we start searching from.
     * @return the minimum element.
     */
    private AvlNode findMinElement(AvlNode x) {
	while (x.left != null) {
	    x = x.left;
	}
	return x;
    }

    /*
     * finds the successor of the given node 
     * 
     * @param x the node we search its successor.
     * @return the successor
     */
    private AvlNode treeSuccessor(AvlNode x) {
	if (x.right != null) {
	    return findMinElement(x.right);
	}
	if (x.parent == null) {
	    this.root = x.parent;
	}
	AvlNode y = x.parent;
	while (y != null && y.right == x) {
	    x = y;
	    y = x.parent;
	}
	return y;
    }

    /**
     * Calculates the minimum number of nodes in an AVL tree of height h.
     * 
     * @param h the height of the tree (a non−negative number) in question.
     * @return the minimum number of nodes in an AVL tree of the given height.
     */
    public static int findMinNodes(int h) {
	switch (h) {
	case 0:
	    return 1;
	case 1:
	    return 2;
	default:
	    return h = findMinNodes(h - 1) + findMinNodes(h - 2) + 1;
	}
    }

    /*
     * Calculates the height of the nodes in the tree.
     * 
     * @param r the root of the tree
     * @return the height of the root
     */
    private int height(AvlNode r) {
	int heightRight, heightLeft;
	if (r == null) {
	    return -1;
	}
	heightLeft = height(r.left);
	heightRight = height(r.right);
	return r.height = Math.max(heightRight, heightLeft) + 1;
    }

    /*
     * insert the given node, update the parents node and the root if needed
     * 
     * 
     * @param r the root of the tree
     * @param x the node we insert
     * 
     */
    private void insert(AvlNode r, AvlNode x) {

	if (r == null) {
	    this.root = x;
	} else {
	    if (x.data < r.data) {
		if (r.left == null) {
		    r.left = x;
		    x.parent = r;
		    checkBalance(r);
		} else {
		    insert(r.left, x);
		}
	    } else if (x.data > r.data) {
		if (r.right == null) {
		    r.right = x;
		    x.parent = r;
		    checkBalance(r);
		} else {
		    insert(r.right, x);
		}
	    }
	}
    }

    /*
     * Right rotation using the given node. (if it was a "LL" case)
     * 
     * @param n the node for the rotation.
     * @return the root of the new rotated tree.
     */
    private AvlNode rotateRight(AvlNode n) {
	AvlNode v = n.left;
	v.parent = n.parent;
	n.left = v.right;

	if (n.left != null) {
	    n.left.parent = n;
	}
	v.right = n;
	n.parent = v;

	if (v.parent != null) {
	    if (v.parent.right == n) {
		v.parent.right = v;
	    } else if (v.parent.left == n) {
		v.parent.left = v;
	    }
	}
	setBalance(n);
	setBalance(v);
	return v;
    }

    /*
     * 
     * Left rotation using the given node. (if it was a "RR" case)
     * 
     * @param n the node for the rotation.
     * @return the root of the new rotated tree.
     */
    private AvlNode rotateLeft(AvlNode n) {
	AvlNode v = n.right;
	v.parent = n.parent;
	n.right = v.left;

	if (n.right != null) {
	    n.right.parent = n;
	}
	v.left = n;
	n.parent = v;

	if (v.parent != null) {
	    if (v.parent.right == n) {
		v.parent.right = v;
	    } else if (v.parent.left == n) {
		v.parent.left = v;
	    }
	}
	setBalance(n);
	setBalance(v);

	return v;
    }

    /*
     * rotation for the "Left Right" case
     * 
     * @param u the node for the rotation
     * @return the root after the double rotation.
     */
    private AvlNode doubleRotateLeftRight(AvlNode u) {
	u.left = rotateLeft(u.left);
	return rotateRight(u);
    }

    /*
     * rotation for the "Right Left" case
     * 
     * @param u the node for the rotation
     * @return the root after the double rotation.
     */
    private AvlNode doubleRotateRightLeft(AvlNode u) {
	u.right = rotateRight(u.right);
	return rotateLeft(u);
    }

    void setBalance(AvlNode cur) {
	cur.balance = height(cur.right) - height(cur.left);
    }
}
