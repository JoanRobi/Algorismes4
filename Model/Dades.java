package Model;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;

// Aquesta classe guarda TOTES les dades que necessita el compressor i descompressor de Huffman.
// Serveix com a "magatzem" d'informació: arbre, codis, freqüències, fitxers...
public class Dades {

    private Node root; // L'arrel de l'arbre de Huffman
    private final Map<Byte, String> codeMap = new HashMap<>(); // Mapa de codis Huffman (símbol → codi binari)
    private PriorityQueue<Node> queue = new PriorityQueue<>(); // Cua de prioritats per construir l'arbre
    private Map<Byte, Integer> freq = new HashMap<>(); // Mapa de freqüències (símbol → nombre de vegades)
    private Path input; // Ruta del fitxer d'entrada
    private Path output; // Ruta del fitxer de sortida
    private double tempsCompresio;
    private double tempsDescompresio;

    public Dades() {
    }

    // ------- GETTERS i SETTERS pels temps de compressió i descompressió -------

    public void setTempsCompresio(double temps) {
        this.tempsCompresio = temps;
    }

    public void setTempsDescompresio(double t) {
        this.tempsDescompresio = t;
    }

    public double getTempsCompresio() {
        return tempsCompresio;
    }

    public double getTempsDescompresio() {
        return tempsDescompresio;
    }

    // ------- GETTERS i SETTERS per les RUTES dels fitxers -------

    // Defineix la ruta del fitxer d'entrada
    public void setInput(Path p) {
        input = p;
    }

    // Retorna la ruta del fitxer d'entrada
    public Path getInput() {
        return input;
    }

    // Defineix la ruta del fitxer de sortida
    public void setOutput(Path p) {
        output = p;
    }

    // Retorna la ruta del fitxer de sortida
    public Path getOutput() {
        return output;
    }

    // ------- DADES RELACIONADES AMB LA CODIFICACIÓ HUFFMAN -------

    // Retorna el mapa símbol → codi binari (ex: 'a' → "101")
    public Map<Byte, String> getCodeMap() {
        return codeMap;
    }

    // Retorna la cua de prioritats usada per construir l'arbre
    public PriorityQueue<Node> getQueue() {
        return queue;
    }

    // Defineix l'arrel de l'arbre de Huffman
    public void setRoot(Node n) {
        root = n;
    }

    // Retorna quants nodes hi ha actualment a la cua de prioritats
    public int getQueueSize() {
        return queue.size();
    }

    // Afegeix un node nou a la cua de prioritats
    public void addToQueue(Node n) {
        queue.add(n);
    }

    // Treu el node amb menor freqüència de la cua (el més prioritari)
    public Node pollFromQueue() {
        return queue.poll();
    }

    // Retorna l'arrel de l'arbre de Huffman
    public Node getRoot() {
        return root;
    }

    // ------- FREQÜÈNCIES DELS SÍMBOLS -------

    // Retorna el mapa de freqüències (símbol → quantes vegades apareix)
    public Map<Byte, Integer> getFreq() {
        return freq;
    }

    // Retorna quants símbols diferents hi ha (la mida del mapa de freqüències)
    public int getFreqSize() {
        return freq.size();
    }

    // Retorna les parelles símbol-freqüència
    public Set<Entry<Byte, Integer>> getFreqEntrySet() {
        return freq.entrySet();
    }

    // Construeix l'arbre de Huffman a partir del mapa de freqüències
    public void buildTree() {
        // Omple la cua de prioritats amb nodes fulla (un per cada símbol)
        for (Map.Entry<Byte, Integer> e : freq.entrySet()) {
            queue.add(new Node(e.getKey(), e.getValue()));
        }

        // Cas especial: si només hi ha un símbol, afegeix un node dummy
        if (queue.size() == 1) {
            queue.add(new Node((byte) 0, 0));
        }

        // Combina els dos nodes amb menor freqüència fins a tenir un únic arbre
        while (queue.size() > 1) {
            Node a = queue.poll();
            Node b = queue.poll();
            queue.add(new Node(a, b)); // Crea un nou node intern
        }

        // El node restant és l'arrel de l'arbre
        root = queue.poll();
    }

    // Recursiu: assigna codis binaris a cada símbol (fulla)
    public void buildCodeMap(Node node, String prefix) {
        if (node.isLeaf()) {
            // Si és una fulla, assigna el codi corresponent
            codeMap.put(node.getValue(), prefix.isEmpty() ? "0" : prefix);
        } else {
            // Si és un node intern, continua recorrent (0 cap a l'esquerra, 1 cap a la
            // dreta)
            buildCodeMap(node.getLeft(), prefix + "0");
            buildCodeMap(node.getRight(), prefix + "1");
        }
    }
}
