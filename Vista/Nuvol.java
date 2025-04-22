package Vista;

import java.awt.*;

import javax.swing.JPanel;

import Model.Dades;

public class Nuvol extends JPanel {
    private Dades dades;
    private boolean lluny = false;

    public Nuvol(int w, int h, Dades dades) {
        this.dades = dades;
        this.setPreferredSize(new Dimension(w, h));
    }

    public void pintar(boolean a) {
        lluny = a;
        if (this.getGraphics() != null) {
            paintComponent(this.getGraphics());
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int w = this.getWidth();
        int h = this.getHeight();

        // Fondo blanco
        g.setColor(Color.white);
        g.fillRect(0, 0, w, h);

        if (dades != null && dades.getN() > 0) {
            g.setColor(Color.blue);

            for (int i = 0; i < dades.getN(); i++) {
                int x = (int) dades.getCoordX(i);
                int y = (int) dades.getCoordY(i);

                g.fillOval(x - 1, y - 1, 3, 3); // Punto de 3x3 centrado
            }

            // Dibuja una línea roja entre los dos puntos más cercanos
            if (dades.getN() > 1) {
                if (!lluny) {
                    int i1 = dades.getPuntProper1();
                    int i2 = dades.getPuntProper2();

                    int x1 = (int) dades.getCoordX(i1);
                    int y1 = (int) dades.getCoordY(i1);
                    int x2 = (int) dades.getCoordX(i2);
                    int y2 = (int) dades.getCoordY(i2);

                    // Dibujar la línea roja
                    g.setColor(Color.red);
                    g.drawLine(x1, y1, x2, y2);

                    // Calcular centro y radio
                    int centerX = (x1 + x2) / 2;
                    int centerY = (y1 + y2) / 2;
                    double distance = Math.hypot(x2 - x1, y2 - y1);
                    int radius1 = (int) (distance / 2) + 8;
                    int radius2 = (int) (distance / 2) + 7;

                    // Dibujar el círculo
                    g.drawOval(centerX - radius1, centerY - radius1, radius1 * 2, radius1 * 2);
                    g.drawOval(centerX - radius2, centerY - radius2, radius2 * 2, radius2 * 2);
                } else {
                    int i1 = dades.getPuntLlunya1();
                    int i2 = dades.getPuntLlunya2();

                    int x1 = (int) dades.getCoordX(i1);
                    int y1 = (int) dades.getCoordY(i1);
                    int x2 = (int) dades.getCoordX(i2);
                    int y2 = (int) dades.getCoordY(i2);

                    // Dibujar la línea roja
                    g.setColor(Color.red);
                    g.drawLine(x1, y1, x2, y2);
                }

            }
        }
    }
}
