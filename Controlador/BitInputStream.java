package Controlador;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Classe que ens permet llegir bits d'un flux d'entrada (DataInputStream),
 * d'un en un, de forma fàcil.
 * 
 * Normalment, quan llegim d'un fitxer, ho fem byte a byte (8 bits junts).
 * Aquesta classe ens deixa llegir cada bit per separat.
 */
public class BitInputStream implements Closeable {
    private final DataInputStream in; // El flux d'entrada (el fitxer o el buffer d'on llegim)
    private int currentByte = 0; // Aquí guardem el byte actual del qual anem traient bits
    private int numBits = 0; // Quants bits ens queden per llegir del currentByte
    private boolean endOfStream = false; // Si hem arribat al final del flux

    // Constructor: rep el DataInputStream d'on llegirem
    BitInputStream(DataInputStream in) {
        this.in = in;
    }

    /**
     * Llegeix el següent bit del flux.
     * Retorna true si el bit és 1, false si és 0.
     * Si no queden bits o bytes per llegir, llança una excepció IOException.
     */
    boolean readBit() throws IOException {
        // Si ja hem gastat tots els bits del currentByte, n'hem de llegir un altre
        if (numBits == 0) {
            if (endOfStream) {
                // Si ja havíem detectat que s'ha acabat el flux, donem error
                throw new IOException("Final inesperat del flux");
            }
            int read = in.read(); // Llegim un byte (8 bits) del flux
            if (read == -1) {
                // Si no hem pogut llegir (arribat al final del fitxer), marquem-ho
                endOfStream = true;
                throw new IOException("S'ha acabat el fitxer abans d'hora");
            }
            currentByte = read & 0xFF; // Convertim el byte llegit en un valor positiu (0-255)
            numBits = 8; // Ara tenim 8 bits per gastar
        }
        // Agafem el bit més significatiu (l'esquerra del currentByte)
        boolean bit = (currentByte & 0x80) != 0; // 0x80 = 10000000 (en binari)
        currentByte <<= 1; // Desplacem els bits cap a l'esquerra per preparar el següent
        numBits--; // Hem consumit un bit
        return bit; // Retornem el bit que hem llegit (true per 1, false per 0)
    }

    @Override
    public void close() throws IOException {
        // Aquesta funció està aquí per si volem tancar el flux més endavant.
        // No fa res especial perquè DataInputStream es tanca des de fora normalment.
    }
}
