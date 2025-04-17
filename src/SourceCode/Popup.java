/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SourceCode;

/**
 *
 * @author HP
 */
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPanel;

public class Popup extends JPanel{
    private static final int RADIUS = 20;
    private static final int SHADOW_SIZE = 8;
    private static final int SHADOW_OPACITY = 40; // 0-255 range, lower is more transparent
    
    public Popup() {
        setOpaque(false);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Paint shadow first
        paintShadow(g2);
        
        // Paint panel content
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - SHADOW_SIZE, getHeight() - SHADOW_SIZE, RADIUS, RADIUS));
        
        g2.dispose();
    }
    
    private void paintShadow(Graphics2D g2) {
        // Set shadow color (black with transparency)
        g2.setColor(new Color(0, 0, 0, SHADOW_OPACITY));
        
        // Set composite for blending shadow
        g2.setComposite(AlphaComposite.SrcOver.derive(0.3f));
        
        // Draw shadow layers for a softer effect
        for (int i = 0; i < SHADOW_SIZE; i++) {
            float opacity = 1.0f - (float) i / SHADOW_SIZE;
            g2.setComposite(AlphaComposite.SrcOver.derive(opacity * 0.3f));
            g2.fill(new RoundRectangle2D.Float(
                    SHADOW_SIZE - i, 
                    SHADOW_SIZE - i, 
                    getWidth() - SHADOW_SIZE + i, 
                    getHeight() - SHADOW_SIZE + i, 
                    RADIUS, 
                    RADIUS
            ));
        }
        
        // Reset composite to normal
        g2.setComposite(AlphaComposite.SrcOver);
    }
}
