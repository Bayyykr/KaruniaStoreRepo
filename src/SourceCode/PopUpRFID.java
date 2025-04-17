package SourceCode;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

public class PopUpRFID extends JDialog {
    private final int FINAL_WIDTH = 400;
    private final int FINAL_HEIGHT = 300;
    private final int ANIMATION_DURATION = 300;
    private final int ANIMATION_DELAY = 10;
    private float currentScale = 0.01f;
    private boolean animationStarted = false;
    private Timer animationTimer, closeAnimationTimer, resetTimer;
    private JFrame parentFrame;
    
    private JLabel rfidIcon, warningIcon, messageLabel;
    private JLabel warningTitleLabel, warningMessageLabel;
    private JLabel closeButton, warningCloseButton;
    public JTextField rfidTextField;
    
    private JPanel backgroundPanel;
    private RoundedPanel warningTopPanel, mainPanel;
    
    private boolean isWarningMode = false;
    
      // Menambahkan method untuk mereset TextField
    public void resetRfidTextField() {
        if (rfidTextField != null) {
            rfidTextField.setText("");
        }
    }
    
    public PopUpRFID(JFrame parent) {
        super(parent, true);
        this.parentFrame = parent;
        setUndecorated(true);
        setSize(945, 570);
        
        if (parent != null) {
            setLocationRelativeTo(parent);
        } else {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            setLocation((screenSize.width - 945) / 2, (screenSize.height - 570) / 2);
        }
        
        setBackground(new Color(0, 0, 0, 0));
        
        backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(0, 0, 0, 180));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        
        backgroundPanel.setOpaque(false);
        backgroundPanel.setLayout(null);
        backgroundPanel.setBounds(0, 0, 945, 570);
        
        initComponents();
        startScaleAnimation();
    }
    
    private void initComponents() {
        setLayout(null);
        add(backgroundPanel);
        
        // Panel utama dengan sudut bulat
        mainPanel = new RoundedPanel(20, false);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(null);
        
        int x = (945 - FINAL_WIDTH) / 2;
        int y = (570 - FINAL_HEIGHT) / 2;
        mainPanel.setBounds(x, y, FINAL_WIDTH, FINAL_HEIGHT);
        
        // Tombol tutup untuk tampilan normal
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
        
        // Tombol tutup untuk warning
        warningCloseButton = new JLabel("×");
        warningCloseButton.setFont(new Font("Arial", Font.BOLD, 24));
        warningCloseButton.setForeground(new Color(51, 51, 51));
        warningCloseButton.setHorizontalAlignment(SwingConstants.CENTER);
        warningCloseButton.setBounds(360, 10, 30, 30);
        warningCloseButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        warningCloseButton.setVisible(false);
        
        warningCloseButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                warningCloseButton.setForeground(Color.WHITE);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                warningCloseButton.setForeground(new Color(51, 51, 51));
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                showNormalMode();
            }
        });
        
        // Ikon RFID
        rfidIcon = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/SourceImage/rfid-image.png"));
            rfidIcon.setIcon(icon);
        } catch (Exception e) {
            System.err.println("Tidak dapat memuat gambar RFID: " + e.getMessage());
        }
        
        rfidIcon.setBounds(0, -80, 400, 400);
        rfidIcon.setHorizontalAlignment(SwingConstants.CENTER);
        rfidIcon.setVisible(false);
        
        // Panel merah untuk warning
        warningTopPanel = new RoundedPanel(20, true);
        warningTopPanel.setBackground(new Color(255, 77, 77));
        warningTopPanel.setBounds(0, 0, FINAL_WIDTH, 140);
        warningTopPanel.setLayout(null);
        warningTopPanel.setVisible(false);
        
        // Ikon warning
        warningIcon = new JLabel();
        try {
            ImageIcon warningIconImg = new ImageIcon(getClass().getResource("/SourceImage/warning.png"));
            if (warningIconImg.getIconWidth() <= 0) {
                throw new Exception("Gambar tidak dapat dimuat dengan benar");
            }
            warningIcon.setIcon(warningIconImg);
        } catch (Exception e) {
            warningIcon.setText("!");
            warningIcon.setFont(new Font("Arial", Font.BOLD, 48));
            warningIcon.setForeground(Color.WHITE);
            System.err.println("Tidak dapat memuat gambar ikon peringatan: " + e.getMessage());
            e.printStackTrace();
        }
        
        warningIcon.setBounds(125, 20, 150, 100);
        warningIcon.setHorizontalAlignment(SwingConstants.CENTER);
        warningIcon.setVisible(false);
        
        // Label judul warning
        warningTitleLabel = new JLabel("Warning!");
        warningTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        warningTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        warningTitleLabel.setBounds(50, 160, 300, 60);
        warningTitleLabel.setVisible(false);
        
        // Label pesan warning
        warningMessageLabel = new JLabel("<html><center>Card not detected.<br>Please ensure your RFID card is correct and try again</center></html>");
        warningMessageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        warningMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        warningMessageLabel.setBounds(50, 200, 300, 70);
        warningMessageLabel.setVisible(false);
        
        // Label pesan normal
        messageLabel = new JLabel("Silakan tempelkan kartu RFID pada scanner");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setBounds(50, 230, 300, 30);
        messageLabel.setVisible(false);
        
        messageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showWarningMode();
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                messageLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        });
        
        // Text field untuk input RFID
        rfidTextField = new JTextField();
        rfidTextField.setBounds(100, 100, 200, 30);
        rfidTextField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rfidTextField.setHorizontalAlignment(SwingConstants.CENTER);
        rfidTextField.setBorder(null);
        rfidTextField.setBackground(Color.WHITE);
        rfidTextField.setOpaque(false);
        rfidTextField.setForeground(new Color(0, 0, 0, 0)); 
        rfidTextField.setCaretColor(new Color(0, 0, 0, 0));
        rfidTextField.setEditable(true); 
        rfidTextField.setFocusable(true);
        
        // Menambahkan komponen ke panel
        warningTopPanel.add(warningIcon);
        warningTopPanel.add(warningCloseButton);
        
        mainPanel.add(closeButton);
        mainPanel.add(rfidIcon);
        mainPanel.add(messageLabel);
        mainPanel.add(rfidTextField);
        mainPanel.add(warningTopPanel);
        mainPanel.add(warningTitleLabel);
        mainPanel.add(warningMessageLabel);
        
        backgroundPanel.add(mainPanel);
        
        backgroundPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Opsional: tutup dialog saat mengklik di luar panel utama
                // startCloseAnimation();
            }
        });
    }
    
    // Menampilkan mode warning
    private void showWarningMode() {
        if (isWarningMode) return;
        
        isWarningMode = true;
        
         // Reset text field saat mode warning ditampilkan
        resetRfidTextField();
        
        // Sembunyikan komponen mode normal
        rfidIcon.setVisible(false);
        messageLabel.setVisible(false);
        closeButton.setVisible(false);
        
        // Tampilkan komponen mode warning
        warningTopPanel.setVisible(true);
        warningIcon.setVisible(true);
        warningTitleLabel.setVisible(true);
        warningMessageLabel.setVisible(true);
        warningCloseButton.setVisible(true);
        
        // Timer untuk kembali ke mode normal setelah 3 detik
        resetTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showNormalMode();
                resetTimer.stop();
            }
        });
        resetTimer.setRepeats(false);
        resetTimer.start();
    }
    
    // Kembali ke mode normal
    private void showNormalMode() {
        isWarningMode = false;
        
        // Sembunyikan komponen mode warning
        warningTopPanel.setVisible(false);
        warningIcon.setVisible(false);
        warningTitleLabel.setVisible(false);
        warningMessageLabel.setVisible(false);
        warningCloseButton.setVisible(false);
        
        // Tampilkan komponen mode normal
        rfidIcon.setVisible(true);
        messageLabel.setVisible(true);
        closeButton.setVisible(true);
        
        // Minta fokus kembali ke rfidTextField
        SwingUtilities.invokeLater(() -> {
            rfidTextField.requestFocusInWindow();
        });
    }
    
    // Animasi menutup dialog
    private void startCloseAnimation() {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
        
        if (resetTimer != null && resetTimer.isRunning()) {
            resetTimer.stop();
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
    
    // Animasi membuka dialog
    private void startScaleAnimation() {
        if (animationStarted) return;
        
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
                
                if (progress >= 0.4 && !rfidIcon.isVisible() && !isWarningMode) {
                    rfidIcon.setVisible(true);
                    messageLabel.setVisible(true);
                    closeButton.setVisible(true);
                    rfidTextField.setVisible(true);
                    
                    SwingUtilities.invokeLater(() -> {
                        rfidTextField.requestFocusInWindow();
                    });
                }
                
                repaint();
                
                if (currentFrame[0] >= totalFrames) {
                    animationTimer.stop();
                    currentScale = 1.0f;
                    
                    if (!isWarningMode) {
                        rfidIcon.setVisible(true);
                        messageLabel.setVisible(true);
                        closeButton.setVisible(true);
                    } else {
                        warningTopPanel.setVisible(true);
                        warningIcon.setVisible(true);
                        warningTitleLabel.setVisible(true);
                        warningMessageLabel.setVisible(true);
                        warningCloseButton.setVisible(true);
                    }
                    
                    rfidTextField.setVisible(true);
                    
                    SwingUtilities.invokeLater(() -> {
                        rfidTextField.requestFocusInWindow();
                    });
                    
                    repaint();
                }
            }
        });
        
        animationTimer.start();
    }
    
    // Kelas RoundedPanel yang disederhanakan
    private class RoundedPanel extends JPanel {
        private int cornerRadius;
        private boolean topOnly;
        
        public RoundedPanel(int radius, boolean topOnly) {
            super();
            this.cornerRadius = radius;
            this.topOnly = topOnly;
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
                
                g2d.setColor(getBackground());
                
                if (topOnly) {
                    // Untuk panel warning, sudut bundar hanya di bagian atas
                    g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight() + cornerRadius, cornerRadius, cornerRadius));
                } else {
                    // Untuk panel normal, semua sudut bundar
                    g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
                }
                
                g2d.setTransform(originalTransform);
            } else {
                g2d.setColor(getBackground());
                
                if (topOnly) {
                    g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight() + cornerRadius, cornerRadius, cornerRadius));
                } else {
                    g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
                }
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