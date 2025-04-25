package Main;

import javax.swing.JFrame;

import Controlador.*;
import Model.Dades;
import Vista.GUI;

public class Main implements Notificar {

    private GUI finestra;
    private Dades dades;
    private JFrame frame;
    private Punts procesN2;
    // Proces punt llunyà
    private PuntsRecursiu procesNlogN;
    private PuntLluny procesLluny;

    public static void main(String[] args) {
        (new Main()).iniciar();
    }

    public void iniciar() {
        dades = new Dades();
        frame = new JFrame("Càlcul punts més llunyans i més propers");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        finestra = new GUI(dades, this);
        frame.setContentPane(finestra);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override
    public void notificar(String s) {
        switch (s) {
            case "aturar":
                if (procesN2 != null && procesN2.isAlive()) {
                    procesN2.notificar("aturar");
                } else if (procesNlogN != null && procesNlogN.isAlive()) {
                    procesNlogN.notificar("aturar");
                } else if (procesLluny != null && procesLluny.isAlive()) {
                    procesLluny.notificar("aturar");
                }
                break;
            case "procesN2":
                if (procesN2 == null || !procesN2.isAlive()) {
                    procesN2 = new Punts(dades, this);
                    procesN2.start();
                }
                break;
            case "procesNlogN":
                if (procesNlogN == null || !procesNlogN.isAlive()) {
                    procesNlogN = new PuntsRecursiu(dades, this);
                    procesNlogN.start();
                }
                break;
            // Procés punt llunyà
            case "puntLlunya":
                if (procesLluny == null || !procesLluny.isAlive()) {
                    procesLluny = new PuntLluny(dades, this);
                    procesLluny.start();
                }
            case "pintar":
                finestra.notificar("pintar");
                break;
            case "pintarLluny":
                finestra.notificar("pintarLluny");
                break;
            case "actualitzarN2":
                finestra.notificar("actualitzarN2");
                break;
            case "actualitzarNlogN":
                finestra.notificar("actualitzarNlogN");
                break;
            case "actualitzarLluny":
                finestra.notificar("actualitzarLluny");
                break;
        }
    }
}