package Vista;

import Controlador.Notificar;
import Model.Dades;
import Model.Node;
import Main.Main;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

// Interfície gràfica per comprimir i descomprimir fitxers amb l'algorisme de Huffman
public class InterficieHuffman extends JPanel implements Notificar, ActionListener {

    private Dades dades;                // objecte que conté la informació de compressió
    private final Main main;            // controlador central per llançar processos

    private final JTextField campEntrada;
    private final JTextField campSortida;
    private final JButton botoTriaEntrada;
    private final JButton botoComprimir;
    private final JButton botoDescomprimir;

    private HuffmanTreePanel panelArbre;
    private final JTable taula;

    private final JLabel etiquetaTemps;
    private final JLabel etiquetaPercentatge;
    private final JLabel etiquetaLongMitjana;
    private final JTextArea areaRegistres;

    public InterficieHuffman(Dades dades, Main main) {
        this.dades = dades;
        this.main  = main;

        campEntrada     = new JTextField();
        campEntrada.setEditable(false);
        campSortida     = new JTextField();
        botoTriaEntrada = new JButton("Selecciona fitxer d'entrada");
        botoComprimir   = new JButton("Comprimir");
        botoDescomprimir= new JButton("Descomprimir");

        panelArbre = new HuffmanTreePanel(null);

        taula = new JTable(new DefaultTableModel(new Object[]{"Símbol","Freq","Codi"}, 0));

        etiquetaTemps       = new JLabel("Temps: N/D");
        etiquetaPercentatge = new JLabel("Compressió: N/D");
        etiquetaLongMitjana = new JLabel("Long mitjana codi: N/D");

        areaRegistres = new JTextArea(5, 40);
        areaRegistres.setEditable(false);

        inicialitzaDiseny();
    }

    private void inicialitzaDiseny() {
        setLayout(new BorderLayout());  // distribuïm per zones

        JPanel panelFitxers = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,4,4,4);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        gbc.gridx=0; gbc.gridy=0; panelFitxers.add(new JLabel("Ruta fitxer d'entrada:"), gbc);
        gbc.gridx=1; gbc.weightx=1; panelFitxers.add(campEntrada, gbc);
        gbc.gridx=2; gbc.weightx=0; botoTriaEntrada.addActionListener(this); panelFitxers.add(botoTriaEntrada, gbc);

        gbc.gridx=0; gbc.gridy=1; panelFitxers.add(new JLabel("Nom del fitxer de sortida:"), gbc);
        gbc.gridx=1; panelFitxers.add(campSortida, gbc);

        JPanel panelBotons = new JPanel();
        botoComprimir.addActionListener(this);
        botoDescomprimir.addActionListener(this);
        panelBotons.add(botoComprimir);
        panelBotons.add(botoDescomprimir);

        JScrollPane escArbre = new JScrollPane(panelArbre);  // afegim scroll per arbres grans
        escArbre.setBorder(BorderFactory.createTitledBorder("Arbre Huffman"));
        JScrollPane escTaula = new JScrollPane(taula);
        escTaula.setBorder(BorderFactory.createTitledBorder("Codis i freqüències"));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, escArbre, escTaula);
        split.setDividerLocation(450);   // proporció inicial

        JPanel panelStats = new JPanel();
        panelStats.add(etiquetaTemps);
        panelStats.add(etiquetaPercentatge);
        panelStats.add(etiquetaLongMitjana);

        JScrollPane escRegistres = new JScrollPane(areaRegistres);
        escRegistres.setBorder(BorderFactory.createTitledBorder("Registre"));

        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.add(panelBotons, BorderLayout.NORTH);
        panelCentral.add(split,       BorderLayout.CENTER);
        panelCentral.add(panelStats,  BorderLayout.SOUTH);

        add(panelFitxers,  BorderLayout.NORTH);
        add(panelCentral,  BorderLayout.CENTER);
        add(escRegistres,  BorderLayout.SOUTH);
    }

    @Override public void actionPerformed(ActionEvent e) {
        if (e.getSource()==botoTriaEntrada)      triaFitxerEntrada();
        if (e.getSource()==botoComprimir)        accioComprimir();
        if (e.getSource()==botoDescomprimir)     accioDescomprimir();
    }

    // prepara la compressió
    private void accioComprimir() {
        asseguraExtensio(".huff");
        main.notificar("comprimir");
    }
    // prepara la descompressió
    private void accioDescomprimir() {
        asseguraExtensio(".txt");
        main.notificar("descomprimir");
    }
    // afegeix l'extensió si manca
    private void asseguraExtensio(String ext) {
        String ruta = campSortida.getText();
        if (!ruta.endsWith(ext)) ruta += ext;
        dades.setOutput(Path.of(ruta));
    }

    // obre diàleg per triar fitxer d'entrada
    private void triaFitxerEntrada() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            campEntrada.setText(f.getAbsolutePath());
            dades.setInput(f.toPath());
            areaRegistres.append("Entrada: "+f.getName()+"\n");
        }
    }

    private void comprimir() {
        try {
            actualitzaVistaDespresDeComprimir(dades.getTempsCompresio());
            areaRegistres.append(String.format("Compressió feta en %.3f s%n", dades.getTempsCompresio()));
        } catch (Exception ex) {
            ex.printStackTrace();
            areaRegistres.append("Error durant compressió: "+ex+"\n");
        }
    }
    private void descomprimir() {
        try {
            areaRegistres.append(String.format("Descompressió feta en %.3f s%n", dades.getTempsDescompresio()));
        } catch (Exception ex) {
            areaRegistres.append("Error durant descompressió: "+ex+"\n");
        }
    }

    // refresca taula, arbre i estadístiques
    private void actualitzaVistaDespresDeComprimir(double tempsSegons) throws Exception {
        Node arrel = dades.getRoot();
        if (arrel==null) throw new IllegalStateException("L'arbre de Huffman no s'ha generat");

        // actualitzam el dibuix de l'arbre
        panelArbre.setRoot(arrel);
        panelArbre.revalidate(); panelArbre.repaint();

        // omplim la taula de codis
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Símbol","Freq","Codi"}, 0);
        Map<Byte,String>  codis = dades.getCodeMap();
        Map<Byte,Integer> freqs = dades.getFreq();
        long total = freqs.values().stream().mapToLong(i->i).sum();

        for (Map.Entry<Byte,String> e : codis.entrySet()) {
            byte b = e.getKey();
            String simb = (b>=32 && b<=126) ? "'"+(char)b+"'" : Byte.toString(b);
            int f = freqs.getOrDefault(b,0);
            model.addRow(new Object[]{simb, f, e.getValue()});
        }
        taula.setModel(model);

        // càlcul d'estadístiques de compressió
        etiquetaTemps.setText(String.format("Temps: %.3f s", tempsSegons));
        long orig = Files.size(dades.getInput());
        long comp = Files.size(dades.getOutput());
        double pct = 100*(1-(double)comp/orig);
        etiquetaPercentatge.setText(String.format("Compressió: %.2f %%", pct));

        long bitsTot=0;
        for (Map.Entry<Byte,String> e : codis.entrySet())
            bitsTot += e.getValue().length() * freqs.getOrDefault(e.getKey(),0);
        double longMit = (double)bitsTot/total;
        etiquetaLongMitjana.setText(String.format("Long mitjana codi: %.2f bits", longMit));
    }

    @Override public void notificar(String s) {
        switch(s) {
            case "comprimit"    -> comprimir();
            case "descomprimit" -> descomprimir();
        }
    }
}
