package Controlador;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import Model.Dades;
import Model.Node;

/**
 * Controlador que utilitza {@link HuffmanModel} per comprimir fitxers.
 */
public class Huffman {

    private Dades dades;

    public Huffman(Dades dades) {
        this.dades = dades;
    }

    /**
     * Comprimeix {@code input} i escriu el resultat a {@code output}.huff.
     */
    public void compress(Path input, Path output) throws IOException {
        byte[] data = readAllBytes(input);

        // 1. Freqüències dels bytes
        for (byte b : data) {
            dades.getFreq().merge(b, 1, Integer::sum);
        }

        // 2. Construeix l'arbre i els codis
        this.build(dades.getFreq());

        // 3. Escriu el fitxer comprimit
        try (DataOutputStream out = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(output.toFile())))) {
            // 3.1 Encapsament (mida original + taula de freq.)
            boolean useShort = data.length <= 0xFFFF;
            out.writeInt(data.length);
            out.writeByte(dades.getFreqSize());
            for (Map.Entry<Byte, Integer> e : dades.getFreqEntrySet()) {
                int f = e.getValue();
                if (useShort) {
                    out.writeShort(f); // 2 bytes
                } else {
                    out.writeInt(f); // 4 bytes
                }
            }
            // 3.2 Cos (bits codificats)
            try (BitOutputStream bitOut = new BitOutputStream(out)) {
                for (byte b : data) {
                    String code = dades.getCodeMap().get(b);
                    for (char c : code.toCharArray()) {
                        bitOut.writeBit(c == '1');
                    }
                }
                bitOut.flushRemaining();
            }
        }
    }

    // --- Helpers -----------------------------------------------------------
    private byte[] readAllBytes(Path path) throws IOException {
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(path.toFile()))) {
            return in.readAllBytes();
        }
    }

    /**
     * Construeix l'arbre de Huffman i genera els codis a partir de la taula de
     * freqüències.
     */
    private void build(Map<Byte, Integer> freq) {
        for (Map.Entry<Byte, Integer> e : freq.entrySet()) {
            dades.addToQueue(new Node(e.getKey(), e.getValue()));
        }
        // Cas especial: fitxer amb un sol símbol
        if (dades.getQueueSize() == 1) {
            dades.addToQueue(new Node((byte) 0, 0));
        }
        while (dades.getQueueSize() > 1) {
            Node a = dades.pollFromQueue();
            Node b = dades.pollFromQueue();
            dades.addToQueue(new Node(a, b));
        }
        dades.setRoot(dades.pollFromQueue());
        buildCodeMap(dades.getRoot(), "");
    }

    private void buildCodeMap(Node node, String prefix) {
        if (node.isLeaf()) {
            dades.getCodeMap().put(node.getValue(), prefix.isEmpty() ? "0" : prefix);
        } else {
            buildCodeMap(node.getLeft(), prefix + "0");
            buildCodeMap(node.getRight(), prefix + "1");
        }
    }

    // Main de prova ràpida (opcional)
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Ús: java CompressionController <input> <output(.huff)>");
            return;
        }
        Dades dades = new Dades();
        Huffman huff = new Huffman(dades);
        huff.compress(Path.of(args[0]), Path.of(args[1]));
        System.out.println("Compressió completada!");
    }
}
