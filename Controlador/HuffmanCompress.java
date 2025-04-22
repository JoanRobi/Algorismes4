package Controlador;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import Model.Dades;
import Model.Node;

// Aquesta classe s'encarrega de COMPRIMIR un fitxer utilitzant Huffman.
public class HuffmanCompress implements Notificar {

    private Dades dades; // Objecte on guardem totes les dades de la compressió (freqüències, arbre,
                         // codis, etc.)

    public HuffmanCompress(Dades dades) {
        this.dades = dades;
    }

    // COMPRIMEIX el fitxer d'entrada i el guarda al fitxer de sortida
    public void compress() throws IOException {
        // 1. Llegeix TOT el fitxer d'entrada com un array de bytes
        byte[] data = readAllBytes(dades.getInput());

        // 2. Calcula la freqüència de cada byte (quantes vegades apareix cada símbol)
        for (byte b : data) {
            dades.getFreq().merge(b, 1, Integer::sum);
        }

        // 3. Construeix l'arbre de Huffman i genera els codis binaris per cada símbol
        this.build(dades.getFreq());

        // 4. Escriu el fitxer comprimit
        try (DataOutputStream out = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(dades.getOutput().toFile())))) {

            // 4.1 Escriu la CAPÇALERA (mida original i taula de freqüències)
            boolean useShort = data.length <= 0xFFFF; // Si el fitxer és petit, usem short (2 bytes) per les freq.
            out.writeInt(data.length); // Escrivim la mida original (en bytes)
            out.writeByte(dades.getFreqSize()); // Nombre de símbols diferents

            // Escriu per cada símbol: el byte + la seva freqüència
            for (Map.Entry<Byte, Integer> e : dades.getFreqEntrySet()) {
                out.writeByte(e.getKey()); // El símbol (byte)
                int f = e.getValue();
                if (useShort) {
                    out.writeShort(f); // La freqüència (2 bytes si és petit)
                } else {
                    out.writeInt(f); // La freqüència (4 bytes si és gran)
                }
            }

            // 4.2 Escriu el COS (els bits codificats amb Huffman)
            try (BitOutputStream bitOut = new BitOutputStream(out)) {
                for (byte b : data) {
                    String code = dades.getCodeMap().get(b); // Per cada byte, agafem el seu codi binari
                    for (char c : code.toCharArray()) {
                        bitOut.writeBit(c == '1'); // Escrivim bit a bit
                    }
                }
                bitOut.flushRemaining(); // Assegurem que els últims bits es guardin correctament
            }
        }
    }

    // Llegeix TOT el fitxer d'entrada com un array de bytes
    private byte[] readAllBytes(Path path) throws IOException {
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(path.toFile()))) {
            return in.readAllBytes();
        }
    }

    // Construeix l'ARBRE DE HUFFMAN i el MAPA DE CODIS a partir de la taula de
    // freqüències
    private void build(Map<Byte, Integer> freq) {
        // Omple la cua de prioritats amb nodes fulla (un per cada símbol)
        for (Map.Entry<Byte, Integer> e : freq.entrySet()) {
            dades.addToQueue(new Node(e.getKey(), e.getValue()));
        }

        // Cas especial: si només hi ha un símbol, afegeix un node dummy
        if (dades.getQueueSize() == 1) {
            dades.addToQueue(new Node((byte) 0, 0));
        }

        // Combina els dos nodes amb menor freqüència fins a tenir un únic arbre
        while (dades.getQueueSize() > 1) {
            Node a = dades.pollFromQueue();
            Node b = dades.pollFromQueue();
            dades.addToQueue(new Node(a, b)); // Crea un nou node intern
        }

        // El node restant és l'arrel de l'arbre
        dades.setRoot(dades.pollFromQueue());

        // A partir de l'arbre, genera els codis binaris per cada símbol
        buildCodeMap(dades.getRoot(), "");
    }

    // Recursiu: assigna codis binaris a cada símbol (fulla)
    private void buildCodeMap(Node node, String prefix) {
        if (node.isLeaf()) {
            // Si és una fulla, assigna el codi corresponent
            dades.getCodeMap().put(node.getValue(), prefix.isEmpty() ? "0" : prefix);
        } else {
            // Si és un node intern, continua recorrent (0 cap a l'esquerra, 1 cap a la
            // dreta)
            buildCodeMap(node.getLeft(), prefix + "0");
            buildCodeMap(node.getRight(), prefix + "1");
        }
    }

    // MAIN de prova ràpida (per comprovar que la compressió funciona)
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Ús: java CompressionController <input> <output(.huff)>");
            return;
        }
        Dades dades = new Dades(); // Crea l'objecte amb les dades
        HuffmanCompress huff = new HuffmanCompress(dades);
        dades.setInput(Path.of(args[0])); // Defineix el fitxer d'entrada
        dades.setOutput(Path.of(args[1])); // Defineix el fitxer de sortida
        huff.compress(); // Comprimeix!
        System.out.println("Compressió completada!");
    }

    @Override
    public void notificar(String s) {
        // Aquesta funció està preparada per afegir notificacions (GUI), però de moment
        // no fa res
        throw new UnsupportedOperationException("Unimplemented method 'notificar'");
    }
}
