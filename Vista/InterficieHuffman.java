package Vista;

import Controlador.Notificar;
import Model.Dades;
import Model.Node;
import Main.Main;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

// Interfície gràfica per comprimir i descomprimir fitxers amb l'algorisme de Huffman.
public class InterficieHuffman extends JPanel implements Notificar, ActionListener {
    private Dades dades;
    private final Main main;

    private final JTextField campEntrada;
    private final JTextField campSortida;
    private final JButton botoTriaEntrada;
    // private final JButton botoTriaSortida;
    private final JButton botoComprimir;
    private final JButton botoDescomprimir;

    private final JTree arbre;
    private final JTable taula;

    private final JLabel etiquetaTemps;
    private final JLabel etiquetaPercentatge;
    private final JLabel etiquetaLongMitjana;

    private final JTextArea areaRegistres;

    public InterficieHuffman(Dades dades, Main main) {
        this.dades = dades;
        this.main = main;

        campEntrada = new JTextField();
        campEntrada.setEditable(false);
        campSortida = new JTextField();
        campSortida.setEditable(true);
        botoTriaEntrada = new JButton("Selecciona fitxer d'entrada");
        // botoTriaSortida = new JButton("Selecciona fitxer de sortida");

        botoComprimir = new JButton("Comprimir");
        botoDescomprimir = new JButton("Descomprimir");

        arbre = new JTree(new DefaultMutableTreeNode("Arbre de Huffman"));
        taula = new JTable(new DefaultTableModel(new Object[] { "Símbol", "Freq", "Codi" }, 0));

        etiquetaTemps = new JLabel("Temps: N/D");
        etiquetaPercentatge = new JLabel("Compressió: N/D");
        etiquetaLongMitjana = new JLabel("Long mitjana codi: N/D");

        areaRegistres = new JTextArea(5, 40);
        areaRegistres.setEditable(false);

        inicialitzaDiseny();
    }

    private void inicialitzaDiseny() {
        setLayout(new BorderLayout());

        JPanel panelFitxers = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panelFitxers.add(new JLabel("Ruta fitxer d'entrada:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panelFitxers.add(campEntrada, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        botoTriaEntrada.addActionListener(this);
        panelFitxers.add(botoTriaEntrada, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panelFitxers.add(new JLabel("Nom del fitxer de sortida:"), gbc);
        gbc.gridx = 1;
        panelFitxers.add(campSortida, gbc);
        gbc.gridx = 2;

        // botoTriaSortida.addActionListener(this);
        // panelFitxers.add(botoTriaSortida, gbc);
        // panelFitxers.add(new JLabel("Introdueix nom del fitxer de sortida: "), this);
        // panelFitxers.add(stringSortida, this);

        JPanel panelBotons = new JPanel();
        botoComprimir.addActionListener(this);
        botoDescomprimir.addActionListener(this);
        panelBotons.add(botoComprimir);
        panelBotons.add(botoDescomprimir);

        JScrollPane escArbre = new JScrollPane(arbre);
        escArbre.setBorder(BorderFactory.createTitledBorder("Arbre Huffman"));
        JScrollPane escTaula = new JScrollPane(taula);
        escTaula.setBorder(BorderFactory.createTitledBorder("Codis i freqüències"));
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, escArbre, escTaula);
        split.setDividerLocation(350);

        JPanel panelStats = new JPanel();
        panelStats.add(etiquetaTemps);
        panelStats.add(etiquetaPercentatge);
        panelStats.add(etiquetaLongMitjana);

        JScrollPane escRegistres = new JScrollPane(areaRegistres);
        escRegistres.setBorder(BorderFactory.createTitledBorder("Registre"));

        // Agrupam a un panell central
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.add(panelBotons, BorderLayout.NORTH);
        panelCentral.add(split, BorderLayout.CENTER);
        panelCentral.add(panelStats, BorderLayout.SOUTH);

        add(panelFitxers, BorderLayout.NORTH);
        add(panelCentral, BorderLayout.CENTER);
        add(escRegistres, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == botoTriaEntrada) {
            triaFitxerEntrada();
        }
        /*
         * if (e.getSource() == botoTriaSortida) {
         * triaFitxerSortida();
         * }
         */
        if (e.getSource() == botoComprimir) {
            String ruta = campSortida.getText();
            // Comprobar si la ruta ya termina con ".huff"
            if (!ruta.endsWith(".huff")) {
                ruta += ".huff"; // Si no, añadir la extensión
            }
            // Establecer la salida
            dades.setOutput(Path.of(ruta));
            main.notificar("comprimir"); // Notificar al Main que inicie la compresión
        }
        if (e.getSource() == botoDescomprimir) {
            String ruta = campSortida.getText();
            // Comprobar si la ruta ya termina con ".txt"
            if (!ruta.endsWith(".txt")) {
                ruta += ".txt"; // Si no, añadir la extensión
            }
            // Establecer la salida
            dades.setOutput(Path.of(ruta));
            main.notificar("descomprimir"); // Notificar al Main que inicie la descompresión
        }
    }

    private void triaFitxerEntrada() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            campEntrada.setText(f.getAbsolutePath());
            dades.setInput(f.toPath());
            areaRegistres.append("Entrada: " + f.getName() + "\n");
        }
    }

    private void triaFitxerSortida() {
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            campSortida.setText(f.getAbsolutePath());
            dades.setOutput(f.toPath());
            areaRegistres.append("Sortida: " + f.getName() + "\n");
        }
    }

    private void comprimir() {
        try {
            actualitzaVistaDespresDeComprimir(dades.getTempsCompresio());
            areaRegistres.append(String.format("Compressió feta en " + dades.getTempsCompresio() + " s\n"));
        } catch (Exception ex) {
            areaRegistres.append("Error durant compressió: " + ex.getMessage() + "\n");
        }
    }

    private void descomprimir() {
        try {
            areaRegistres.append(String.format("Descompressió feta en " + dades.getTempsDescompresio() + " s\n"));
        } catch (Exception ex) {
            areaRegistres.append("Error durant descompressió: " + ex.getMessage() + "\n");
        }
    }

    private void actualitzaVistaDespresDeComprimir(double tempsSegons) throws Exception {
        Node arrel = dades.getRoot();
        DefaultMutableTreeNode nodeArrel = creaNodeArbre(arrel);
        arbre.setModel(new DefaultTreeModel(nodeArrel));

        DefaultTableModel model = new DefaultTableModel(new Object[] { "Símbol", "Freq", "Codi" }, 0);
        Map<Byte, String> codis = dades.getCodeMap();
        Map<Byte, Integer> freqs = dades.getFreq();
        long total = freqs.values().stream().mapToLong(i -> i).sum();

        for (Map.Entry<Byte, String> e : codis.entrySet()) {
            byte b = e.getKey();
            String simb = (b >= 32 && b <= 126) ? "'" + (char) b + "'" : Byte.toString(b);
            int f = freqs.getOrDefault(b, 0);
            model.addRow(new Object[] { simb, f, e.getValue() });
        }

        taula.setModel(model);

        etiquetaTemps.setText(String.format("Temps: %.3f s", tempsSegons));
        long orig = Files.size(dades.getInput());
        long comp = Files.size(dades.getOutput());
        double pct = 100 * (1 - (double) comp / orig);
        etiquetaPercentatge.setText(String.format("Compressió: %.2f %%", pct));

        long bitsTot = 0;
        for (Map.Entry<Byte, String> e : codis.entrySet()) {
            bitsTot += e.getValue().length() * freqs.getOrDefault(e.getKey(), 0);
        }

        double longMit = (double) bitsTot / total;
        etiquetaLongMitjana.setText(String.format("Long mitjana codi: %.2f bits", longMit));
    }

    private DefaultMutableTreeNode creaNodeArbre(Node node) {
        if (node.isLeaf()) {
            String etq = String.format("'%c' (%d)", node.getValue(), node.getFreq());
            return new DefaultMutableTreeNode(etq);
        } else {
            DefaultMutableTreeNode parent = new DefaultMutableTreeNode(node.getFreq());
            parent.add(creaNodeArbre(node.getLeft()));
            parent.add(creaNodeArbre(node.getRight()));
            return parent;
        }
    }

    @Override
    public void notificar(String s) {
        switch (s) {
            case "comprimit":
                this.comprimir();
                break;
            case "descomprimit":
                this.descomprimir();
                break;
        }
    }
}
