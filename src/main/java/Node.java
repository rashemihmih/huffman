import java.io.Serializable;

public class Node implements Serializable {
    private Byte value;
    private int weight;
    private Node left = null;
    private Node right = null;

    public Node(Byte value, int weight) {
        this.value = value;
        this.weight = weight;
    }

    public Node(Byte value, int weight, Node left, Node right) {
        this.value = value;
        this.weight = weight;
        this.left = left;
        this.right = right;
    }

    public Byte getValue() {
        return value;
    }

    public void setValue(Byte value) {
        this.value = value;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public boolean isLeaf() {
        return left == null && right == null;
    }
}
