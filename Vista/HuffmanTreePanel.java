package Vista;

import Model.Node;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class HuffmanTreePanel extends JPanel {

    private Node root;                             // arrel de l'arbre Huffman
    private final int NODE_W = 60, NODE_H = 30;    // mida d'un node en píxels
    private final int X_GAP = 20, Y_GAP = 50;      // espai horitzontal i vertical
    private final Map<Node, Point> pos = new HashMap<>();   // posicions calculades

    HuffmanTreePanel(Node root) { setRoot(root); setBackground(Color.WHITE); }

    // assigna una nova arrel i torna a calcular posicions
    void setRoot(Node root) {
        this.root = root;
        pos.clear();
        if (root!=null) calculaPosicions(root, 0, 0);
        int width  = pos.values().stream().mapToInt(p -> p.x+NODE_W).max().orElse(200);
        int height = pos.values().stream().mapToInt(p -> p.y+NODE_H).max().orElse(200);
        setPreferredSize(new Dimension(width+40, height+40));
    }

    // càlcul recursiu de coordenades
    private int calculaPosicions(Node n, int x, int depth) {
        if (n==null) return x;

        // si és fulla, li assignam la posició i avançam
        if (n.isLeaf()) {
            pos.put(n, new Point(x, depth*Y_GAP));
            return x + NODE_W + X_GAP;
        }

        // primer recorrem l'esquerra, després la dreta i col·locam el pare al mig
        int xAfterLeft  = calculaPosicions(n.getLeft(),  x, depth+1);
        int xAfterRight = calculaPosicions(n.getRight(), xAfterLeft, depth+1);

        int xMid = (xAfterLeft + xAfterRight - NODE_W - X_GAP)/2;
        pos.put(n, new Point(xMid, depth*Y_GAP));
        return xAfterRight;
    }

    // dibuixam l'arbre recursivament
    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (root!=null) dibuixa(g, root);
    }
    private void dibuixa(Graphics g, Node n) {
        if (n==null) return;
        Point p = pos.get(n);
        int cx=p.x, cy=p.y;

        // línies cap als fills + text 0/1
        if (n.getLeft()!=null) {
            Point pl = pos.get(n.getLeft());
            g.drawLine(cx+NODE_W/2, cy+NODE_H, pl.x+NODE_W/2, pl.y);
            g.drawString("0", (cx+pl.x)/2, (cy+pl.y)/2);
            dibuixa(g, n.getLeft());
        }
        if (n.getRight()!=null) {
            Point pr = pos.get(n.getRight());
            g.drawLine(cx+NODE_W/2, cy+NODE_H, pr.x+NODE_W/2, pr.y);
            g.drawString("1", (cx+pr.x)/2, (cy+pr.y)/2);
            dibuixa(g, n.getRight());
        }

        // dibuix del node (oval + text)
        g.drawOval(cx, cy, NODE_W, NODE_H);
        String txt = n.isLeaf()
                ? (((n.getValue()>=32&&n.getValue()<=126)? "'"+(char)n.getValue()+"'" : Byte.toString(n.getValue())) + ":" + n.getFreq())
                : String.valueOf(n.getFreq());
        FontMetrics fm = g.getFontMetrics();
        int tx = cx + (NODE_W - fm.stringWidth(txt))/2;
        int ty = cy + (NODE_H + fm.getAscent())/2 - 2;
        g.drawString(txt, tx, ty);
    }
}