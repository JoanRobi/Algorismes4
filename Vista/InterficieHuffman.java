package Vista;

import Controlador.HuffmanCompress;
import Controlador.HuffmanDecompress;
import Model.Dades;
import Model.Node;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.util.Map;

// Interfície gràfica per comprimir i descomprimir fitxers amb l'algorisme de Huffman.
public class InterficieHuffman extends JFrame {
    private final Dades dades;                // Objecte que guarda es camins i estructures
    private final HuffmanCompress compressor; // Controlador per comprimir
    private final HuffmanDecompress descompressor; // Controlador per descomprimir

    // Camps de text per mostrar es camins d'entrada i sortida
    private final JTextField campEntrada;
    private final JTextField campSortida;
    // Botons per triar es fitxers
    private final JButton botoTriaEntrada;
    private final JButton botoTriaSortida;
    // Botons d'acció per comprimir o descomprimir
    private final JButton botoComprimir;
    private final JButton botoDescomprimir;

    // Components per mostrar sa informació de Huffman
    private final JTree arbre;
    private final JTable taula;

    // Etiquetes per estadístiques: temps, percentatge i longitud mitjana
    private final JLabel etiquetaTemps;
    private final JLabel etiquetaPercentatge;
    private final JLabel etiquetaLongMitjana;

    private final JTextArea areaRegistres;

    public InterficieHuffman() {
        super("Compressor Huffman");
        dades = new Dades();
        compressor = new HuffmanCompress(dades);
        descompressor = new HuffmanDecompress(dades);

        // Inicialitzam es camps de fitxer
        campEntrada = new JTextField(); campEntrada.setEditable(false);
        campSortida = new JTextField(); campSortida.setEditable(false);
        botoTriaEntrada = new JButton("Selecciona fitxer d'entrada");
        botoTriaSortida = new JButton("Selecciona fitxer de sortida");

        // Botons per executar sa compressió/descompressió
        botoComprimir = new JButton("Comprimir");
        botoDescomprimir = new JButton("Descomprimir");

        // Inicialitzam es components per sa vista de s'arbre i sa taula
        arbre = new JTree(new DefaultMutableTreeNode("Arbre de Huffman"));
        taula = new JTable(new DefaultTableModel(new Object[]{"Símbol","Freq","Codi"}, 0));

        // Etiquetes de sa barra d'estadístiques amb valors per defecte
        etiquetaTemps = new JLabel("Temps: N/D");
        etiquetaPercentatge = new JLabel("Compressió: N/D");
        etiquetaLongMitjana = new JLabel("Long mitjana codi: N/D");

        // Àrea per escriure missatges i errors
        areaRegistres = new JTextArea(5,40);
        areaRegistres.setEditable(false);

        inicialitzaDiseny();
        inicialitzaAccions();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900,600);
        setLocationRelativeTo(null);
    }

    // Disenyam sa disposició de tots es components dins sa finestra
    private void inicialitzaDiseny() {
        JPanel panelFitxers = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,4,4,4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Línia per es camp d'entrada
        gbc.gridx=0; gbc.gridy=0;
        panelFitxers.add(new JLabel("Fitxer d'entrada:"), gbc);
        gbc.gridx=1; gbc.weightx=1.0;
        panelFitxers.add(campEntrada, gbc);
        gbc.gridx=2; gbc.weightx=0;
        panelFitxers.add(botoTriaEntrada, gbc);

        // Línia per es camp de sortida
        gbc.gridx=0; gbc.gridy=1;
        panelFitxers.add(new JLabel("Fitxer de sortida:"), gbc);
        gbc.gridx=1;
        panelFitxers.add(campSortida, gbc);
        gbc.gridx=2;
        panelFitxers.add(botoTriaSortida, gbc);

        // Panel amb es botons d'acció centrats
        JPanel panelBotons = new JPanel();
        panelBotons.add(botoComprimir);
        panelBotons.add(botoDescomprimir);

        // Scrollpane per sa vista de l'arbre
        JScrollPane escArbre = new JScrollPane(arbre);
        escArbre.setBorder(BorderFactory.createTitledBorder("Arbre Huffman"));
        // Scrollpane per sa taula de codis i freqüències
        JScrollPane escTaula = new JScrollPane(taula);
        escTaula.setBorder(BorderFactory.createTitledBorder("Codis i freqüències"));
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, escArbre, escTaula);
        split.setDividerLocation(350);

        // Panel per mostrar ses estadístiques
        JPanel panelStats = new JPanel();
        panelStats.add(etiquetaTemps);
        panelStats.add(etiquetaPercentatge);
        panelStats.add(etiquetaLongMitjana);

        // Scrollpane per ses línies de registre
        JScrollPane escRegistres = new JScrollPane(areaRegistres);
        escRegistres.setBorder(BorderFactory.createTitledBorder("Registre"));

        // Afegim tot a nes container principal
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(panelFitxers, BorderLayout.NORTH);
        cp.add(panelBotons, BorderLayout.AFTER_LAST_LINE);
        cp.add(split, BorderLayout.CENTER);
        cp.add(panelStats, BorderLayout.SOUTH);
        cp.add(escRegistres, BorderLayout.SOUTH);
    }

    private void inicialitzaAccions() {
        botoTriaEntrada.addActionListener(e -> triaFitxerEntrada());
        botoTriaSortida.addActionListener(e -> triaFitxerSortida());
        botoComprimir.addActionListener(e -> comprimir());
        botoDescomprimir.addActionListener(e -> descomprimir());
    }

    // Obrim diàleg per triar es fitxer d'entrada i actualitzam campEntrada
    private void triaFitxerEntrada() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            campEntrada.setText(f.getAbsolutePath());
            dades.setInput(f.toPath());
            areaRegistres.append("Entrada: " + f.getName() + "\n");
        }
    }

    // Obrim diàleg per triar es fitxer de sortida i actualitzam campSortida
    private void triaFitxerSortida() {
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(this)==JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            campSortida.setText(f.getAbsolutePath());
            dades.setOutput(f.toPath());
            areaRegistres.append("Sortida: " + f.getName() + "\n");
        }
    }

    // Executam sa compressió cridant es controlador i actualitzam sa vista
    private void comprimir() {
        try {
            long ini = System.nanoTime();
            compressor.compress();
            long fi = System.nanoTime();
            double secs = (fi-ini)/1e9;
            actualitzaVistaDespresDeComprimir(secs);
            areaRegistres.append(String.format("Compressió feta en %.3f s\n", secs));
        } catch (Exception ex) {
            areaRegistres.append("Error durant compressió: "+ex.getMessage()+"\n");
        }
    }

    // Executam sa descompressió cridant es controlador
    private void descomprimir() {
        try {
            long ini = System.nanoTime();
            descompressor.decompress();
            long fi = System.nanoTime();
            double secs = (fi-ini)/1e9;
            areaRegistres.append(String.format("Descompressió feta en %.3f s\n", secs));
        } catch (Exception ex) {
            areaRegistres.append("Error durant descompressió: "+ex.getMessage()+"\n");
        }
    }

    // Desplegam arbre, taula i estadístiques després de fer sa compressió
    private void actualitzaVistaDespresDeComprimir(double tempsSegons) throws Exception {
        // Construïm arbre de manera recursiva
        Node arrel = dades.getRoot();
        DefaultMutableTreeNode nodeArrel = creaNodeArbre(arrel);
        arbre.setModel(new DefaultTreeModel(nodeArrel));

        // Omplim sa taula amb sos codis i sos comptatges
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Símbol","Freq","Codi"},0);
        Map<Byte,String> codis = dades.getCodeMap();
        Map<Byte,Integer> freqs = dades.getFreq();
        long total = freqs.values().stream().mapToLong(i->i).sum();
        for (Map.Entry<Byte,String> e : codis.entrySet()) {
            byte b = e.getKey();
            String simb = (b>=32 && b<=126)?"'"+(char)b+"'":Byte.toString(b);
            int f = freqs.getOrDefault(b,0);
            model.addRow(new Object[]{simb,f,e.getValue()});
        }
        taula.setModel(model);

        // Estadístiques de temps i compressió
        etiquetaTemps.setText(String.format("Temps: %.3f s", tempsSegons));
        long orig = Files.size(dades.getInput());
        long comp = Files.size(dades.getOutput());
        double pct = 100*(1-(double)comp/orig);
        etiquetaPercentatge.setText(String.format("Compressió: %.2f %%", pct));

        // Calculam sa longitud mitjana de codi
        long bitsTot=0;
        for (Map.Entry<Byte,String> e : codis.entrySet()) {
            bitsTot += e.getValue().length()*freqs.getOrDefault(e.getKey(),0);
        }
        double longMit = (double)bitsTot/total;
        etiquetaLongMitjana.setText(String.format("Long mitjana codi: %.2f bits",longMit));
    }

    // Crea recursivament mobles per es nodes des JTree
    private DefaultMutableTreeNode creaNodeArbre(Node node) {
        if (node.isLeaf()) {
            // Si és fulla, mostr sa lletra i sa freqüència
            String etq = String.format("'%c' (%d)", node.getValue(), node.getFreq());
            return new DefaultMutableTreeNode(etq);
        } else {
            // Si és intern, posam sa suma de freqüències
            DefaultMutableTreeNode parent = new DefaultMutableTreeNode(node.getFreq());
            parent.add(creaNodeArbre(node.getLeft()));
            parent.add(creaNodeArbre(node.getRight()));
            return parent;
        }
    }

}
