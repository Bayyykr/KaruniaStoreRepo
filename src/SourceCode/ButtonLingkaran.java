package SourceCode;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ButtonLingkaran extends JButton {
    private int radius;
    private Color borderColor;

    public ButtonLingkaran(int radius, Color borderColor) {
        this.radius = radius;
        this.borderColor = Color.BLACK;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
    }

   @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (getModel().isPressed()) {
            g2.setColor(getBackground().darker());
        } else if (getModel().isRollover()) {
            g2.setColor(getBackground().brighter());
        } else {
            g2.setColor(getBackground());
        }
        
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius * 2, radius * 2));
        
        // Black border with 1px width
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1f)); // 1 pixel border
        g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, radius * 2, radius * 2));
        
        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        // No border painting needed as we do it in paintComponent
    }

    @Override
    public boolean contains(int x, int y) {
        RoundRectangle2D shape = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius * 2, radius * 2);
        return shape.contains(x, y);
    }
}