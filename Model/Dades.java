package Model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Model que encapsula la lògica del compressor de Huffman.
 * <p>
 * Només conté les operacions per construir l'arbre i obtenir els codis
 * corresponents a cada símbol.
 */
public class Dades {

    private Node root;
    private final Map<Byte, String> codeMap = new HashMap<>();
    private PriorityQueue<Node> pq = new PriorityQueue<>();
    private Map<Byte, Integer> freq = new HashMap<>();

    public Dades() {
    }

    /** Retorna el mapa símbol → codi binari (com String de '0'/'1'). */
    public Map<Byte, String> getCodeMap() {
        return codeMap;
    }

    public PriorityQueue<Node> getPQ() {
        return pq;
    }

    public void setRoot(Node n) {
        root = n;
    }

    public int getQueueSize() {
        return pq.size();
    }

    public void addToQueue(Node n) {
        pq.add(n);
    }

    public Node pollFromQueue() {
        return pq.poll();
    }

    /** Retorna l'arrel de l'arbre (útil per a visualitzacions). */
    public Node getRoot() {
        return root;
    }

    public Map<Byte, Integer> getFreq() {
        return freq;
    }

    public int getFreqSize() {
        return freq.size();
    }

    public Set<Entry<Byte, Integer>> getFreqEntrySet() {
        return freq.entrySet();
    }
}
