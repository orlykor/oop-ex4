package oop.ex4.data_structures;
/**
 * 
 * This class creates a node for the treeAvl class.
 * 
 * 
 * @author orlykor12
 *
 */
class AvlNode {

    /** No nodes in the tree */
    private static final int NO_HEIGHT = 0;

    /** the tree is balanced with no nodes */
    private static final int EMPTY_BALANCE = 0;

    public AvlNode left, right, parent;

    int data, height;

    public int balance;

    public AvlNode(int n) {

	data = n;
	height = NO_HEIGHT;
	balance = EMPTY_BALANCE;
    }
}
