package SourceCode;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPanel;

public class RoundedPanelProduk extends JPanel {
    private int cornerRadius = 15;

    public RoundedPanelProduk() {
        setOpaque(false);
    }

    // Konstruktor baru untuk mengatur corner radius
    public RoundedPanelProduk(int cornerRadius) {
        this.cornerRadius = cornerRadius;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        
        // Membuat panel dengan semua sudut membulat (rounded sesuai parameter)
        RoundRectangle2D roundedRect = new RoundRectangle2D.Double(
                0, 0, getWidth(), getHeight(), cornerRadius * 2, cornerRadius * 2);
        
        g2.fill(roundedRect);
        g2.dispose();
    }
}