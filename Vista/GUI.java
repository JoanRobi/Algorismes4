package Vista;

import javax.swing.*;

import Controlador.Notificar;
import Model.Dades;
import Main.Main;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JPanel implements Notificar, ActionListener {

    private final int HEIGHT = 600;
    private final int WIDTH = 835;
    private Nuvol nuvolPunts;
    private Dades dades;
    private final JComboBox<String> distribucio;
    private final JButton botoN2;
    private final JButton botoNlogN;
    private final JButton botoLluny;
    private final JButton predirTemps;
    private final JButton aturar;
    private final JTextField textFieldN;
    private JLabel labelTiempo;
    private final Main main;
    private String dist = "";

    public GUI(Dades dades, Main m) {
        this.dades = dades;
        main = m;
        this.setLayout(new BorderLayout());
        nuvolPunts = new Nuvol(WIDTH, HEIGHT, dades);
        this.add(nuvolPunts, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setPreferredSize(new Dimension(300, 40));

        String[] distribucions = { "Uniforme", "Normal", "Exponencial" };
        distribucio = new JComboBox(distribucions);
        botoN2 = new JButton("Procés N^2");
        botoNlogN = new JButton("Procés N*log N");
        botoLluny = new JButton("Punt llunyà");
        predirTemps = new JButton("Predir Temps");
        textFieldN = new JTextField(8);
        aturar = new JButton("Aturar");

        botoN2.addActionListener(this);
        botoNlogN.addActionListener(this);
        predirTemps.addActionListener(this);
        botoLluny.addActionListener(this);
        aturar.addActionListener(this);

        buttonPanel.add(new JLabel("Nombre de punts: "));
        buttonPanel.add(textFieldN);
        buttonPanel.add(distribucio);
        buttonPanel.add(botoN2);
        buttonPanel.add(botoNlogN);
        buttonPanel.add(botoLluny);
        buttonPanel.add(predirTemps);
        buttonPanel.add(aturar);
        add(buttonPanel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setPreferredSize(new Dimension(300, 30));
        labelTiempo = new JLabel();
        bottomPanel.add(labelTiempo);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    @Override
    public void notificar(String s) {
        if (s.equals("pintar")) {
            nuvolPunts.pintar(false);
        }
        if (s.equals("pintarLluny")) {
            nuvolPunts.pintar(true);
        }
        if (s.equals("actualitzarN2")) {
            labelTiempo.setText(
                    "L'algorisme ha tardat " + dades.getTempsN2() + " segons --- Punts més propers: P1("
                            + (double) Math.round(dades.getCoordX(dades.getPuntProper1()) * 1000) / 1000 + ", "
                            + (double) Math.round(dades.getCoordY(dades.getPuntProper1()) * 1000) / 1000 + "); P2("
                            + (double) Math.round(dades.getCoordX(dades.getPuntProper2()) * 1000) / 1000
                            + ", " + (double) Math.round(dades.getCoordY(dades.getPuntProper2()) * 1000) / 1000 + ")");
        }
        if (s.equals("actualitzarNlogN")) {
            labelTiempo.setText(
                    "L'algorisme ha tardat " + dades.getTempsNlogN() + " segons --- Punts més propers: P1("
                            + (double) Math.round(dades.getCoordX(dades.getPuntProper1()) * 1000) / 1000 + ", "
                            + (double) Math.round(dades.getCoordY(dades.getPuntProper1()) * 1000) / 1000 + "); P2("
                            + (double) Math.round(dades.getCoordX(dades.getPuntProper2()) * 1000) / 1000
                            + ", " + (double) Math.round(dades.getCoordY(dades.getPuntProper2()) * 1000) / 1000 + ")");
        }
        if (s.equals("actualitzarLluny")) {
            labelTiempo.setText(
                    "L'algorisme ha tardat " + dades.getTempsLluny() + " segons --- Punts més allunyats: P1("
                            + (double) Math.round(dades.getCoordX(dades.getPuntLlunya1()) * 1000) / 1000 + ", "
                            + (double) Math.round(dades.getCoordY(dades.getPuntLlunya1()) * 1000) / 1000 + "); P2("
                            + (double) Math.round(dades.getCoordX(dades.getPuntLlunya2()) * 1000) / 1000
                            + ", " + (double) Math.round(dades.getCoordY(dades.getPuntLlunya2()) * 1000) / 1000 + ")");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == botoN2 || e.getSource() == botoNlogN || e.getSource() == botoLluny) {
            String auxDist = distribucio.getSelectedItem().toString();
            try {
                // Obtenir el nombre de punts introduït per l'usuari
                int n = Integer.parseInt(textFieldN.getText());
                if (n > 0) {
                    // Inicialitzar les dades només si la n o la distribució han canviat
                    if ((n != dades.getN()) || (!auxDist.equals(dist))) {
                        dist = auxDist;
                        dades.inicialitzar(n, WIDTH, HEIGHT, dist);
                    }
                    // Començar l'algorisme corresponent
                    if (e.getSource() == botoN2) {
                        main.notificar("procesN2");
                    } else if (e.getSource() == botoNlogN) {
                        main.notificar("procesNlogN");
                    } else {
                        main.notificar("puntLlunya");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "El nombre de punts ha de ser un valor positiu.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Introdueix un nombre vàlid", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == aturar) {
            main.notificar("aturar");
        } else if (e.getSource() == predirTemps) {
            JPanel panel = new JPanel(new GridLayout(2, 2));

            JLabel labelNum = new JLabel("Introdueix un n: ");
            JTextField textField = new JTextField();
            JLabel labelOp = new JLabel("Selecciona l'algorisme': ");
            String[] options = { "N^2", "N*log(N)", "Punt llunyà" };
            JComboBox<String> comboBox = new JComboBox<>(options);

            panel.add(labelNum);
            panel.add(textField);
            panel.add(labelOp);
            panel.add(comboBox);

            int result = JOptionPane.showConfirmDialog(this, panel, "Predir Temps", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    int number = Integer.parseInt(textField.getText());
                    String operation = (String) comboBox.getSelectedItem();
                    double temps = 0;
                    double constant = dades.getConstant(operation);
                    switch (operation) {
                        case "N^2":
                            temps = Math.pow(number, 2) * constant;
                            break;
                        case "N*log(N)":
                            temps = number * Math.log10(number) * constant;
                            break;
                        case "Punt llunyà":
                            temps = number * Math.log10(number) * constant;
                            break;
                    }

                    JOptionPane.showMessageDialog(this,
                            "Per trobar els dos punts més pròxims amb l'algorisme de complexitat " + operation
                                    + "\namb "
                                    + number + " punts es tardaria aproximadament "
                                    + Math.round(temps * 1000.0) / 1000.0 + " segons",
                            "Resultat",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Si us plau, introdueix un número vàlid.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
