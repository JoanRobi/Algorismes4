package Controlador;

import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Tiny utilitat per escriure bits en un flux d'enegada.
 */
public class BitOutputStream implements Closeable {
    private final DataOutputStream out;
    private int currentByte = 0;
    private int numBits = 0;

    public BitOutputStream(DataOutputStream out) {
        this.out = out;
    }

    void writeBit(boolean bit) throws IOException {
        currentByte = (currentByte << 1) | (bit ? 1 : 0);
        numBits++;
        if (numBits == 8) {
            flushCurrent();
        }
    }

    void flushRemaining() throws IOException {
        if (numBits > 0) {
            currentByte <<= (8 - numBits); // padding 0's
            flushCurrent();
        }
    }

    private void flushCurrent() throws IOException {
        out.writeByte(currentByte);
        currentByte = 0;
        numBits = 0;
    }

    @Override
    public void close() throws IOException {
        flushRemaining();
    }
}