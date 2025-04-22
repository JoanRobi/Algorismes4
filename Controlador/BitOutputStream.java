package Controlador;

import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Classe que serveix per escriure bits (d'un en un) a un flux de sortida
 * (DataOutputStream).
 * 
 * Normalment, quan escrivim a un fitxer, ho fem per bytes (8 bits junts).
 * Aquesta classe ens deixa escriure cada bit per separat, i quan hem omplert un
 * byte sencer, l'escrivim.
 */
public class BitOutputStream implements Closeable {
    private final DataOutputStream out; // El flux de sortida (fitxer o buffer on escrivim)
    private int currentByte = 0; // Aquí anem guardant els bits que anem escrivint
    private int numBits = 0; // Quants bits tenim guardats (encara sense escriure)

    // Constructor: rep el DataOutputStream on escriurem
    public BitOutputStream(DataOutputStream out) {
        this.out = out;
    }

    /**
     * Escriu un únic bit (true == 1, false == 0) al flux.
     * Quan tenim 8 bits acumulats, els escrivim com un byte.
     */
    void writeBit(boolean bit) throws IOException {
        // Desplacem currentByte cap a l'esquerra i afegim el nou bit a la dreta
        currentByte = (currentByte << 1) | (bit ? 1 : 0);
        numBits++;
        // Quan ja tenim 8 bits, podem escriure el byte sencer
        if (numBits == 8) {
            flushCurrent();
        }
    }

    /**
     * Escriu l'últim byte que hagi quedat incomplet (si hi ha bits pendents).
     * Afegeix zeros als bits que falten per completar el byte (això es diu
     * padding).
     */
    void flushRemaining() throws IOException {
        if (numBits > 0) {
            // Omplim els bits que falten amb zeros (desplacem cap a l'esquerra)
            currentByte <<= (8 - numBits);
            flushCurrent();
        }
    }

    // Escriu el currentByte (ja completat) al flux de sortida
    private void flushCurrent() throws IOException {
        out.writeByte(currentByte); // Escrivim el byte complet
        currentByte = 0; // Reiniciem el currentByte
        numBits = 0; // I també el comptador de bits
    }

    @Override
    public void close() throws IOException {
        flushRemaining(); // Quan tanquem, ens assegurem que no quedin bits penjats
    }
}
