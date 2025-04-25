package Main;

import javax.swing.JFrame;

import Controlador.*;
import Model.Dades;
import Vista.InterficieHuffman;

public class Main implements Notificar {

    private InterficieHuffman finestra;
    private Dades dades;
    private JFrame frame;

    public static void main(String[] args) {
        (new Main()).iniciar();
    }

    public void iniciar() {
        dades = new Dades();
        frame = new JFrame("Càlcul punts més llunyans i més propers");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        finestra = new InterficieHuffman();
        frame.setContentPane(finestra);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override
    public void notificar(String s) {
        switch (s) {
            case "aturar":

                break;
            case "procesN2":
                break;
            case "procesNlogN":
                break;
            // Procés punt llunyà
            case "puntLlunya":
            case "pintar":
           
        }
    }
}