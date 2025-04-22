package Model;

/**
 * Aquesta classe representa un NODE de l'arbre de Huffman.
 * Cada node pot ser una FULLA (amb un símbol) o un NODE INTERN (que apunta a
 * altres nodes).
 */
public class Node implements Comparable<Node> {
    final byte value; // El símbol que representa (NOMÉS per a les fulles)
    final int freq; // La freqüència total (quantes vegades apareix aquest símbol o suma de les
                    // freqüències dels fills)
    final Node left, right; // Fills esquerra i dreta (si el node és intern)

    // CONSTRUCTOR per a NODES FULLA (amb un símbol i la seva freqüència)
    public Node(byte value, int freq) {
        this.value = value; // El símbol (per exemple, una lletra)
        this.freq = freq; // La freqüència del símbol
        this.left = null; // Una fulla no té fills
        this.right = null;
    }

    // CONSTRUCTOR per a NODES INTERNS (combina dos nodes fills)
    public Node(Node left, Node right) {
        this.value = 0; // No té símbol (només les fulles en tenen)
        this.freq = left.freq + right.freq; // La freqüència és la suma dels dos fills
        this.left = left; // Fill esquerre
        this.right = right; // Fill dret
    }

    // Getter: retorna el símbol (NOMÉS útil si és fulla)
    public byte getValue() {
        return value;
    }

    // Getter: retorna el fill esquerre
    public Node getLeft() {
        return left;
    }

    // Getter: retorna el fill dret
    public Node getRight() {
        return right;
    }

    // Comprova si aquest node és una FULLA (no té fills)
    public boolean isLeaf() {
        return left == null && right == null;
    }

    // Permet comparar nodes per la freqüència (necessari per la cua de prioritats)
    @Override
    public int compareTo(Node o) {
        return Integer.compare(this.freq, o.freq);
    }
}
