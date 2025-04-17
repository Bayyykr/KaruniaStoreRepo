/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SourceCode;

/**
 *
 * @author HP
 */
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPanel;

public class RoundedPanel extends JPanel {

    private int cornerRadius = 50;

    public RoundedPanel() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());

        // Create a path for the panel with rounded corners only on the left side
        Path2D path = new Path2D.Double();
        
        // Start from top-left (with round corner)
        path.moveTo(cornerRadius, 0);
        
        // Line to top-right (sharp corner)
        path.lineTo(getWidth(), 0);
        
        // Line to bottom-right (sharp corner)
        path.lineTo(getWidth(), getHeight());
        
        // Line to bottom-left (with round corner)
        path.lineTo(cornerRadius, getHeight());
        
        // Create the rounded left corners
        path.quadTo(0, getHeight(), 0, getHeight() - cornerRadius); // Bottom-left corner
        path.lineTo(0, cornerRadius); // Left side
        path.quadTo(0, 0, cornerRadius, 0); // Top-left corner
        
        path.closePath();
        
        g2.fill(path);
        g2.dispose();
    }
}
