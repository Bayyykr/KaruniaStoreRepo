package SourceCode;

import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class ScrollPane extends JScrollPane {
    
    private Color thumbColor = new Color(100, 100, 100, 150); // Semi-transparent dark gray
    private Color trackColor = new Color(240, 240, 240, 100); // Very light gray, almost transparent
    private int thumbThickness = 8;
    private int thumbRadius = 8;
    
    public ScrollPane(Component view) {
        super(view);
        setupScrollPane();
    }
    
    public ScrollPane(Component view, int vsbPolicy, int hsbPolicy) {
        super(view, vsbPolicy, hsbPolicy);
        setupScrollPane();
    }
    
    private void setupScrollPane() {
        // Make the scroll pane itself transparent
        setOpaque(false);
        getViewport().setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder());
        
        // Customize the vertical scrollbar
        JScrollBar verticalScrollBar = getVerticalScrollBar();
        verticalScrollBar.setOpaque(false);
        verticalScrollBar.setUnitIncrement(16); // Smoother scrolling speed
        verticalScrollBar.setPreferredSize(new Dimension(thumbThickness, 0));
        
        // Customize the horizontal scrollbar (if needed)
        JScrollBar horizontalScrollBar = getHorizontalScrollBar();
        horizontalScrollBar.setOpaque(false);
        horizontalScrollBar.setUnitIncrement(16);
        horizontalScrollBar.setPreferredSize(new Dimension(0, thumbThickness));
        
        // Custom UI for vertical scrollbar
        verticalScrollBar.setUI(new ModernScrollBarUI());
        horizontalScrollBar.setUI(new ModernScrollBarUI());
        
        // Add a small delay to the scrollbar visibility for a smoother appearance
        verticalScrollBar.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                verticalScrollBar.repaint();
            }
        });
    }
    
    public void setThumbColor(Color color) {
        this.thumbColor = color;
        repaint();
    }
    
    public void setTrackColor(Color color) {
        this.trackColor = color;
        repaint();
    }
    
    public void setThumbThickness(int thickness) {
        this.thumbThickness = thickness;
        getVerticalScrollBar().setPreferredSize(new Dimension(thickness, 0));
        getHorizontalScrollBar().setPreferredSize(new Dimension(0, thickness));
        repaint();
    }
    
    public void setThumbRadius(int radius) {
        this.thumbRadius = radius;
        repaint();
    }
    
    // Custom UI for modern looking scrollbars
    private class ModernScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(trackColor);
            
            // Draw rounded track
            if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
                g2.fillRoundRect(
                    trackBounds.x, 
                    trackBounds.y, 
                    trackBounds.width, 
                    trackBounds.height, 
                    thumbRadius, 
                    thumbRadius
                );
            } else {
                g2.fillRoundRect(
                    trackBounds.x, 
                    trackBounds.y, 
                    trackBounds.width, 
                    trackBounds.height, 
                    thumbRadius, 
                    thumbRadius
                );
            }
            g2.dispose();
        }
        
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }
            
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Adjust thumb bounds to make it look nicer
            if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
                int thumbWidth = thumbThickness;
                int offset = (thumbBounds.width - thumbWidth) / 2;
                thumbBounds.x += offset;
                thumbBounds.width = thumbWidth;
            } else {
                int thumbHeight = thumbThickness;
                int offset = (thumbBounds.height - thumbHeight) / 2;
                thumbBounds.y += offset;
                thumbBounds.height = thumbHeight;
            }
            
            // Draw the thumb with nice rounded corners
            g2.setColor(thumbColor);
            g2.fillRoundRect(
                thumbBounds.x, 
                thumbBounds.y, 
                thumbBounds.width, 
                thumbBounds.height, 
                thumbRadius, 
                thumbRadius
            );
            
            g2.dispose();
        }
        
        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }
        
        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }
        
        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }
    }
}