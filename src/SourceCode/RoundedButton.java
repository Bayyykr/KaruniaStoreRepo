/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SourceCode;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;

/**
 *
 * @author HP
 */
public class RoundedButton extends javax.swing.plaf.basic.BasicButtonUI {

    @Override
    public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        AbstractButton b = (AbstractButton) c;
        ButtonModel model = b.getModel();

        // Always use the button's background color regardless of state
        g2.setColor(b.getBackground());

        // Draw rounded rectangle for the button background
        g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 10, 10);

        // Draw a border
        g2.setColor(Color.BLACK);
        g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 10, 10);

        g2.dispose();

        // Paint the text and icon
        super.paint(g, c);
    }
}
