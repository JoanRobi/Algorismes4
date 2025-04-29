package Controlador;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import Main.Main;
import Model.Dades;

// Aquesta classe s'encarrega de COMPRIMIR un fitxer utilitzant Huffman.
public class HuffmanCompress implements Notificar {

    private Dades dades; // Objecte on guardem totes les dades de la compressió (freqüències, arbre,
                         // codis, etc.)
    private Main main;

    public HuffmanCompress(Dades dades, Main main) {
        this.dades = dades;
        this.main = main;
    }

    // COMPRIMEIX el fitxer d'entrada i el guarda al fitxer de sortida
    public void compress() throws IOException {
        double start = System.nanoTime();
        // 1. Llegeix TOT el fitxer d'entrada com un array de bytes
        byte[] data = readAllBytes(dades.getInput());

        // 2. Calcula la freqüència de cada byte (quantes vegades apareix cada símbol)
        for (byte b : data) {
            dades.getFreq().merge(b, 1, Integer::sum);
        }

        // 3. Construeix l'arbre de Huffman i genera els codis binaris per cada símbol
        dades.buildTree();
        // A partir de l'arbre, genera els codis binaris per cada símbol
        dades.buildCodeMap(dades.getRoot(), "");

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

        double end = System.nanoTime();
        double durada = end - start;
        durada /= Math.pow(10, 9);
        dades.setTempsCompresio(durada);

        main.notificar("comprimit"); // Avisa al main de que el fitxer ja s'ha comprimit
    }

    // Llegeix TOT el fitxer d'entrada com un array de bytes
    private byte[] readAllBytes(Path path) throws IOException {
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(path.toFile()))) {
            return in.readAllBytes();
        }
    }

    @Override
    public void notificar(String s) {
        if (s.equals("comprimir")) {
            try {
                this.compress();
            } catch (IOException e) {
                e.toString();
            }
        }
    }
}
