package Controlador;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import Main.Main;
import Model.Dades;
import Model.Node;

// Aquesta classe DESCOMPRIMEIX un fitxer .huff utilitzant Huffman
public class HuffmanDecompress implements Notificar {

    private final Dades dades; // Objecte amb les dades de la descompressió (arbre, etc.)
    private Main main;

    public HuffmanDecompress(Dades dades, Main main) {
        this.dades = dades;
        this.main = main;
    }

    // DESCOMPRIMEIX el fitxer d'entrada i escriu el resultat al fitxer de sortida
    public void decompress() throws IOException {
        double start = System.nanoTime();
        try (DataInputStream in = new DataInputStream(
                new BufferedInputStream(new FileInputStream(dades.getInput().toFile())));
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dades.getOutput().toFile()))) {

            // 1. Llegeix la CAPÇALERA del fitxer .huff
            int originalSize = in.readInt(); // Mida original del fitxer (en bytes)
            int symbolCount = in.readUnsignedByte(); // Nombre de símbols diferents

            boolean useShort = originalSize <= 0xFFFF; // Si el fitxer era petit, les freqüències s'han guardat amb
                                                       // short

            // Llegeix la taula de freqüències (per reconstruir l'arbre)
            Map<Byte, Integer> freq = new HashMap<>();
            for (int i = 0; i < symbolCount; i++) {
                byte symbol = in.readByte(); // Llegeix el símbol
                int f = useShort ? in.readUnsignedShort() : in.readInt(); // Llegeix la freqüència
                freq.put(symbol, f); // Desa-ho al mapa
            }

            // 2. RECONSTRUEIX l'arbre de Huffman a partir de les freqüències
            dades.buildTree();

            // 3. DECODIFICA els bits i escriu els bytes originals
            try (BitInputStream bitIn = new BitInputStream(in)) {
                int written = 0;
                while (written < originalSize) {
                    Node node = dades.getRoot(); // Comença des de l'arrel de l'arbre
                    // Recorre l'arbre fins a arribar a una fulla (un símbol)
                    while (!node.isLeaf()) {
                        boolean bit = bitIn.readBit(); // Llegeix un bit
                        node = bit ? node.getRight() : node.getLeft(); // Segueix a la dreta (1) o a l'esquerra (0)
                    }
                    out.write(node.getValue()); // Escriu el símbol recuperat
                    written++; // Comptem quants bytes hem escrit
                }
            }
        }
        double end = System.nanoTime();
        double durada = end - start;
        durada /= Math.pow(10, 9);
        dades.setTempsDescompresio(durada);

        main.notificar("descomprimit"); // Avisa al main que la descompressió ja està feta
    }

    @Override
    public void notificar(String s) {
        if (s.equals("descomprimir")) {
            try {
                this.decompress();
            } catch (IOException e) {
                e.toString();
            }
        }
    }
}
