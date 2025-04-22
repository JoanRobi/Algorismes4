package Model;

/** Node intern de l'arbre de Huffman. */
public class Node implements Comparable<Node> {
    final byte value; // Només vàlid si és fulla
    final int freq;
    final Node left, right;

    public Node(byte value, int freq) {
        this.value = value;
        this.freq = freq;
        this.left = null;
        this.right = null;
    }

    public Node(Node left, Node right) {
        this.value = 0; // Dummy
        this.freq = left.freq + right.freq;
        this.left = left;
        this.right = right;
    }

    public byte getValue() {
        return value;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public boolean isLeaf() {
        return left == null && right == null;
    }

    @Override
    public int compareTo(Node o) {
        return Integer.compare(this.freq, o.freq);
    }
}