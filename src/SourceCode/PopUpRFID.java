package SourceCode;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;

public class PopUpRFID extends JDialog {
    private int xMouse, yMouse;
    private final int FINAL_WIDTH = 400;
    private final int FINAL_HEIGHT = 300;
    private final int ANIMATION_DURATION = 300; // ms
    private final int ANIMATION_DELAY = 10; // ms
    private float currentScale = 0.01f;
    private boolean animationStarted = false;
    private Timer animationTimer;
    private Timer closeAnimationTimer;
    private JFrame parentFrame;
    
    private JLabel rfidIcon;
    private JLabel messageLabel;
    private JLabel closeButton;
    public JTextField rfidTextField;
    
    // Panel to serve as dark background
    private JPanel backgroundPanel;
    
    public PopUpRFID(JFrame parent) {
        super(parent, true);
        this.parentFrame = parent;
        setUndecorated(true);
        
        // Set a fixed size of 945x570 as specified
        setSize(945, 570);
        
        // Handle case when parent is null
        if (parent != null) {
            setLocationRelativeTo(parent);
        } else {
            // Use screen dimensions if parent is null
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            setLocation((screenSize.width - 945) / 2, (screenSize.height - 570) / 2);
        }
        
        // Create a semi-transparent background for the entire dialog
        setBackground(new Color(0, 0, 0, 0));
        
        // Add semi-transparent background panel that will cover the entire dialog
        backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                // Semi-transparent black background
                g2d.setColor(new Color(0, 0, 0, 180));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        
        backgroundPanel.setOpaque(false);
        backgroundPanel.setLayout(null);
        // Set the background panel to exactly match the dialog size of 945x570
        backgroundPanel.setBounds(0, 0, 945, 570);
        
        initComponents();
        startScaleAnimation();
    }
    
    private void initComponents() {
        setLayout(null);
        
        // First add the background panel to the dialog
        add(backgroundPanel);
        
        // Create the main rounded panel for the popup content
        RoundedPanel mainPanel = new RoundedPanel(20); // Radius of 20 pixels
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(null);
        
        // Center the main panel in the dialog (based on 945x570 size)
        int x = (945 - FINAL_WIDTH) / 2;
        int y = (570 - FINAL_HEIGHT) / 2;
        mainPanel.setBounds(x, y, FINAL_WIDTH, FINAL_HEIGHT);
        
        closeButton = new JLabel("×");
        closeButton.setFont(new Font("Arial", Font.BOLD, 24));
        closeButton.setForeground(new Color(51, 51, 51));
        closeButton.setHorizontalAlignment(SwingConstants.CENTER);
        closeButton.setBounds(360, 10, 30, 30);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.setVisible(false);
        
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                closeButton.setForeground(new Color(255, 70, 70));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                closeButton.setForeground(new Color(51, 51, 51));
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                startCloseAnimation();
            }
        });
        
        rfidIcon = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/SourceImage/rfid-image.png"));
            rfidIcon.setIcon(icon);
        } catch (Exception e) {
            // Handle case when image resource cannot be found
            System.err.println("Could not load RFID image: " + e.getMessage());
        }
        
        rfidIcon.setBounds(0, -80, 400, 400);
        rfidIcon.setHorizontalAlignment(SwingConstants.CENTER);
        rfidIcon.setVisible(false);
        
        messageLabel = new JLabel("Please tap your RFID card on the scanner");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setBounds(50, 230, 300, 30);
        messageLabel.setVisible(false);
        
        // Initialize and configure the text field
        rfidTextField = new JTextField();
        rfidTextField.setBounds(100, 100, 200, 30);  // Adjusted position
        rfidTextField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rfidTextField.setHorizontalAlignment(SwingConstants.CENTER);
        rfidTextField.setBorder(null);
        rfidTextField.setBackground(Color.WHITE);
        rfidTextField.setOpaque(false);
        rfidTextField.setForeground(new Color(0, 0, 0, 0)); 
        rfidTextField.setCaretColor(new Color(0, 0, 0, 0));
        rfidTextField.setEditable(true); 
        rfidTextField.setFocusable(true);
        
        mainPanel.add(closeButton);
        mainPanel.add(rfidIcon);
        mainPanel.add(messageLabel);
        mainPanel.add(rfidTextField);
        
        // Add the main panel to the background panel
        backgroundPanel.add(mainPanel);
        
        // Add click listener to background panel to prevent clicks passing through
        backgroundPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Optional: close dialog when clicking outside the main panel
                // startCloseAnimation();
            }
        });
    }
    
    private void startCloseAnimation() {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
        
        final int totalFrames = ANIMATION_DURATION / ANIMATION_DELAY;
        final int[] currentFrame = {0};
        
        closeAnimationTimer = new Timer(ANIMATION_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentFrame[0]++;
                
                float progress = (float)currentFrame[0] / totalFrames;
                float easedProgress = progress * progress;
                
                currentScale = 1.0f - 0.99f * easedProgress;
                
                repaint();
                
                if (currentFrame[0] >= totalFrames) {
                    closeAnimationTimer.stop();
                    dispose();
                }
            }
        });
        
        closeAnimationTimer.start();
    }
    
    private void startScaleAnimation() {
        if (animationStarted) {
            return;
        }
        
        animationStarted = true;
        final int totalFrames = ANIMATION_DURATION / ANIMATION_DELAY;
        final int[] currentFrame = {0};
        
        animationTimer = new Timer(ANIMATION_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentFrame[0]++;
                
                float progress = (float)currentFrame[0] / totalFrames;
                float easedProgress = 1 - (1-progress) * (1-progress) * (1-progress);
                
                currentScale = 0.01f + 0.99f * easedProgress;
                
                if (progress >= 0.4 && !rfidIcon.isVisible()) {
                    rfidIcon.setVisible(true);
                    messageLabel.setVisible(true);
                    closeButton.setVisible(true);
                    rfidTextField.setVisible(true);
                    
                    // Request focus after components become visible
                    SwingUtilities.invokeLater(() -> {
                        rfidTextField.requestFocusInWindow();
                    });
                }
                
                repaint();
                
                if (currentFrame[0] >= totalFrames) {
                    animationTimer.stop();
                    currentScale = 1.0f;
                    rfidIcon.setVisible(true);
                    messageLabel.setVisible(true);
                    closeButton.setVisible(true);
                    rfidTextField.setVisible(true);
                    
                    // Request focus again after animation completes
                    SwingUtilities.invokeLater(() -> {
                        rfidTextField.requestFocusInWindow();
                    });
                    
                    repaint();
                }
            }
        });
        
        animationTimer.start();
    }
    
    // Custom rounded panel class
    private class RoundedPanel extends JPanel {
        private int cornerRadius;
        
        public RoundedPanel(int radius) {
            super();
            this.cornerRadius = radius;
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (animationStarted) {
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                
                AffineTransform originalTransform = g2d.getTransform();
                g2d.translate(centerX, centerY);
                g2d.scale(currentScale, currentScale);
                g2d.translate(-centerX, -centerY);
                
                // Draw background with rounded corners
                g2d.setColor(getBackground());
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
                
                g2d.setTransform(originalTransform);
            } else {
                // Draw background with rounded corners
                g2d.setColor(getBackground());
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
            }
            
            g2d.dispose();
        }
        
        @Override
        protected void paintChildren(Graphics g) {
            if (animationStarted && currentScale < 0.4) {
                return;
            }
            
            if (animationStarted) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                
                g2d.translate(centerX, centerY);
                g2d.scale(currentScale, currentScale);
                g2d.translate(-centerX, -centerY);
                
                super.paintChildren(g2d);
                g2d.dispose();
            } else {
                super.paintChildren(g);
            }
        }
    }
    
    public JTextField getRfidTextField() {
        return rfidTextField;
    }
}