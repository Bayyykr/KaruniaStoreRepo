package SourceCode;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.Timer;

public class PopUpRFIDWarning extends JDialog {
    private final int FINAL_WIDTH = 400;
    private final int FINAL_HEIGHT = 300;
    private JFrame parentFrame;
    
    private JLabel warningIcon, warningTitleLabel, warningMessageLabel, warningCloseButton;
    private JLabel timerLabel; // Label untuk menampilkan timer
    public JTextField rfidTextField;
    
    private JPanel backgroundPanel;
    private RoundedPanel warningTopPanel, mainPanel;
    private Timer closeTimer; // Timer untuk menutup dialog
    private int timeLeft = 5; // Waktu countdown dalam detik
    
    // Menambahkan method untuk mereset TextField
    public void resetRfidTextField() {
        if (rfidTextField != null) {
            rfidTextField.setText("");
        }
    }
    
    public PopUpRFIDWarning(JFrame parent) {
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
        startCloseTimer(); // Memulai timer penutupan
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
        
        // Panel merah untuk warning
        warningTopPanel = new RoundedPanel(20, true);
        warningTopPanel.setBackground(new Color(255, 77, 77));
        warningTopPanel.setBounds(0, 0, FINAL_WIDTH, 140);
        warningTopPanel.setLayout(null);
        
        // Tombol tutup untuk warning
        warningCloseButton = new JLabel("×");
        warningCloseButton.setFont(new Font("Arial", Font.BOLD, 24));
        warningCloseButton.setForeground(new Color(51, 51, 51));
        warningCloseButton.setHorizontalAlignment(SwingConstants.CENTER);
        warningCloseButton.setBounds(360, 10, 30, 30);
        warningCloseButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
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
                dispose(); // Langsung menutup dialog tanpa animasi
            }
        });
        
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
        
        // Label judul warning
        warningTitleLabel = new JLabel("Warning!");
        warningTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        warningTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        warningTitleLabel.setBounds(50, 160, 300, 60);
        
        // Label pesan warning
        warningMessageLabel = new JLabel("<html><center>Card not detected.<br>Please ensure your RFID card is correct and try again</center></html>");
        warningMessageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        warningMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        warningMessageLabel.setBounds(50, 200, 300, 70);
        
        // Label timer
        timerLabel = new JLabel("Tutup dalam 5 detik");
        timerLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timerLabel.setForeground(new Color(128, 128, 128));
        timerLabel.setBounds(50, 260, 300, 30);
        
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
        
        mainPanel.add(warningTopPanel);
        mainPanel.add(warningTitleLabel);
        mainPanel.add(warningMessageLabel);
        mainPanel.add(timerLabel);
        mainPanel.add(rfidTextField);
        
        backgroundPanel.add(mainPanel);
        
        // Minta fokus ke text field
        SwingUtilities.invokeLater(() -> {
            rfidTextField.requestFocusInWindow();
        });
    }
    
    // Membuat dan memulai timer untuk menutup dialog
    private void startCloseTimer() {
        closeTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                if (timeLeft <= 0) {
                    closeTimer.stop();
                    dispose(); // Menutup dialog
                } else {
                    timerLabel.setText("Tutup dalam " + timeLeft + " detik");
                }
            }
        });
        closeTimer.start();
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
            
            g2d.setColor(getBackground());
            
            if (topOnly) {
                // Untuk panel warning, sudut bundar hanya di bagian atas
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight() + cornerRadius, cornerRadius, cornerRadius));
            } else {
                // Untuk panel normal, semua sudut bundar
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
            }
            
            g2d.dispose();
        }
    }
    
    public JTextField getRfidTextField() {
        return rfidTextField;
    }
    
    // Method untuk membatalkan timer jika dialog ditutup dengan cara lain
    @Override
    public void dispose() {
        if (closeTimer != null && closeTimer.isRunning()) {
            closeTimer.stop();
        }
        super.dispose();
    }
}