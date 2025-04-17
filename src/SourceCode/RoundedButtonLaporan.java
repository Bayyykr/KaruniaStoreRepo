package SourceCode;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicButtonUI;

public class RoundedButtonLaporan extends BasicButtonUI {
    
    private Color borderColor = Color.LIGHT_GRAY; // Default border color
    private int borderRadius = 10; // Default corner radius

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setBorderRadius(int radius) {
        this.borderRadius = radius;
    }

    public int getBorderRadius() {
        return borderRadius;
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        AbstractButton b = (AbstractButton) c;
        ButtonModel model = b.getModel();

        // Background
        g2.setColor(b.getBackground());
        g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), borderRadius, borderRadius);

        // Border
        g2.setColor(borderColor);
        g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, borderRadius, borderRadius);

        g2.dispose();

        // Paint icon and text
        super.paint(g, c);
    }
}
