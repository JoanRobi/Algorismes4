package Main;

import javax.swing.JFrame;

import Controlador.*;
import Model.Dades;
import Vista.InterficieHuffman;

public class Main implements Notificar {

    private InterficieHuffman finestra;
    private Dades dades;
    private JFrame frame;
    private HuffmanCompress huffmanCompress;
    private HuffmanDecompress huffmanDecompress;

    public static void main(String[] args) {
        (new Main()).iniciar();
    }

    public void iniciar() {
        dades = new Dades();
        huffmanCompress = new HuffmanCompress(dades, this);
        huffmanDecompress = new HuffmanDecompress(dades, this);
        frame = new JFrame("Compressió i descompressió d'arxius mitjançant Huffman");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        finestra = new InterficieHuffman(dades, this);
        frame.setContentPane(finestra);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override
    public void notificar(String s) {
        switch (s) {
            case "comprimit":
                finestra.notificar("comprimit");
                break;
            case "descomprimit":
                finestra.notificar("descomprimit");
                break;
            case "comprimir":
                huffmanCompress.notificar("comprimir");
                break;
            case "descomprimir":
                huffmanDecompress.notificar("descomprimir");
                break;
        }
    }
}