package SourceCode;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;

public class PopUp_edittransbeli extends JDialog {
    
    private JTable table;
    private int row;
    private JComponent glassPane;
    private static final int RADIUS = 20;
    private JButton cancelButton, updateButton;
    private RoundedTextField itemNameField, totalField;
//    private RoundedTextField priceField, quantityField;
    private JFrame parentFrame;
    private JLabel titleLabel;

    private int xMouse, yMouse;
    private final int ANIMATION_DURATION = 300; // ms
    private final int ANIMATION_DELAY = 10; // ms
    private float currentScale = 0.01f;
    private boolean animationStarted = false;
    private Timer animationTimer;
    private Timer closeAnimationTimer;
    private final int FINAL_WIDTH = 450;  // Ukuran tetap
    private final int FINAL_HEIGHT = 230; // Ukuran tetap
    
    // Flag untuk menghindari penambahan glassPane berulang
    private static boolean isShowingPopup = false;

    public PopUp_edittransbeli(JFrame parent, JTable table, int row) {
        super(parent, true);
        this.table = table;
        this.row = row;
        this.parentFrame = parent;
        setModal(true);
        setPreferredSize(new Dimension(FINAL_WIDTH, FINAL_HEIGHT));
        setLayout(null);

        // Periksa apakah popup sudah ditampilkan
        if (isShowingPopup) {
            dispose();
            return;
        }
        isShowingPopup = true;

        // Buat overlay transparan dengan warna hitam semi transparan
        glassPane = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 180)); // Transparansi lebih tinggi
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        glassPane.setOpaque(false);
        glassPane.setBounds(0, 0, parent.getWidth(), parent.getHeight());
        
        // Menghapus glassPane yang mungkin sudah ada sebelumnya
        cleanupExistingGlassPane();
        
        parentLayeredPane().add(glassPane, JLayeredPane.POPUP_LAYER);

        setUndecorated(true); // Menghilangkan border default
        setSize(FINAL_WIDTH, FINAL_HEIGHT);
        setLocationRelativeTo(parent);
        setLayout(null);
        setBackground(new Color(0, 0, 0, 0)); // Membuat background transparan

        JPanel contentPanel = new RoundedPanel(RADIUS);
        contentPanel.setLayout(null);
        contentPanel.setBounds(0, 0, FINAL_WIDTH, FINAL_HEIGHT);
        contentPanel.setBackground(Color.WHITE);
        add(contentPanel);

        titleLabel = createTextLabel("Edit Restok", 20, 10, 400, 30, new Font("Arial", Font.BOLD, 20), Color.BLACK);
        titleLabel.setVisible(false);
        contentPanel.add(titleLabel);

        itemNameField = new RoundedTextField(5, "Nama Produk");
        itemNameField.setBounds(70, 55, 300, 40);
        itemNameField.setVisible(false);
        contentPanel.add(itemNameField);

//        priceField = new RoundedTextField(5, "Harga Satuan");
//        priceField.setBounds(105, 90, 226, 31);
//        priceField.setVisible(false);
//        contentPanel.add(priceField);

//        quantityField = new RoundedTextField(5, "Qty");
//        quantityField.setBounds(105, 130, 226, 31);
//        quantityField.setVisible(false);
//        contentPanel.add(quantityField);

        totalField = new RoundedTextField(5, "Total");
        totalField.setBounds(70, 120, 300, 40);
        totalField.setVisible(false);
        contentPanel.add(totalField);

        // Menginisialisasi tombol Cancel dan Update
        cancelButton = createRoundedButton("Cancel", 60, 190, 130, 30, new Color(0xEBEBEB), Color.BLACK);
        cancelButton.setVisible(false);
        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                startCloseAnimation();  // Memulai animasi penutupan saat tombol cancel diklik
            }
        });
        contentPanel.add(cancelButton);
        
        // Tombol Update
        updateButton = createRoundedButton("Update", 250, 190, 130, 30, new Color(0x34C759), Color.WHITE);
        updateButton.setVisible(false);
        contentPanel.add(updateButton);

        // Tambahkan WindowListener untuk membersihkan saat popup ditutup
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanupAndClose();
            }
        });

        // Memulai animasi setelah pop-up muncul
        startScaleAnimation();  // Memulai animasi
    }

    // Metode untuk membersihkan glassPane yang mungkin sudah ada
    private void cleanupExistingGlassPane() {
        Component[] components = parentLayeredPane().getComponentsInLayer(JLayeredPane.POPUP_LAYER);
        for (Component comp : components) {
            if (comp instanceof JComponent) {
                parentLayeredPane().remove(comp);
            }
        }
        parentLayeredPane().repaint();
    }

    private void startScaleAnimation() {
        if (animationStarted || (animationTimer != null && animationTimer.isRunning())) {
            return;
        }

        animationStarted = true;
        final int totalFrames = ANIMATION_DURATION / ANIMATION_DELAY;
        final int[] currentFrame = {0};

        animationTimer = new Timer(ANIMATION_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentFrame[0]++;

                float progress = (float) currentFrame[0] / totalFrames;
                float easedProgress = 1 - (1 - progress) * (1 - progress) * (1 - progress);  // Easing untuk efek zoom-in

                currentScale = 0.01f + 0.99f * easedProgress;

                if (progress >= 0.3 && !titleLabel.isVisible()) {
                    titleLabel.setVisible(true);
                    itemNameField.setVisible(true);
//                    priceField.setVisible(true);
//                    quantityField.setVisible(true);
                    totalField.setVisible(true);
                    cancelButton.setVisible(true);
                    updateButton.setVisible(true);
                }

                repaint();

                if (currentFrame[0] >= totalFrames) {
                    animationTimer.stop();
                    currentScale = 1.0f;

                    titleLabel.setVisible(true);
                    itemNameField.setVisible(true);
//                    priceField.setVisible(true);
//                    quantityField.setVisible(true);
                    totalField.setVisible(true);
                    cancelButton.setVisible(true);
                    updateButton.setVisible(true);

                    repaint();
                }
            }
        });

        animationTimer.start();  // Memulai animasi zoom-in
    }

    private void startCloseAnimation() {
        if (closeAnimationTimer != null && closeAnimationTimer.isRunning()) {
            closeAnimationTimer.stop();
        }

        final int totalFrames = ANIMATION_DURATION / ANIMATION_DELAY;
        final int[] currentFrame = {0};

        closeAnimationTimer = new Timer(ANIMATION_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentFrame[0]++;

                float progress = (float) currentFrame[0] / totalFrames;
                float easedProgress = progress * progress;  // Easing untuk animasi halus

                currentScale = 1.0f - 0.99f * easedProgress;  // Zoom-out dari tengah

                repaint();

                if (currentFrame[0] >= totalFrames) {
                    closeAnimationTimer.stop();  // Menghentikan timer animasi
                    cleanupAndClose();
                }
            }
        });

        closeAnimationTimer.start();  // Memulai animasi zoom-out
    }

    private void cleanupAndClose() {
        // Reset flag saat popup ditutup
        isShowingPopup = false;
        
        // Hapus glassPane
        closePopup();
        
        // Tutup dialog
        dispose();
    }

    private void closePopup() {
        parentLayeredPane().remove(glassPane);
        parentLayeredPane().repaint();
    }

    private JLayeredPane parentLayeredPane() {
        return parentFrame.getLayeredPane();
    }

    private JButton createRoundedButton(String text, int x, int y, int width, int height, Color bgColor, Color textColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bgColor.darker() : bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setBounds(x, y, width, height);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setForeground(textColor);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        // Pastikan tidak ada efek tekan pada tombol
        button.setPressedIcon(button.getIcon());
        button.setRolloverEnabled(false);
        return button;
    }

    // Fungsi untuk membuat label teks
    private JLabel createTextLabel(String text, int x, int y, int width, int height, Font font, Color color) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setBounds(x, y, width, height);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    // Kelas untuk membuat border bundar pada komponen
    public static class RoundedBorder extends AbstractBorder {
        private int radius;

        public RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius, radius, radius, radius);
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(Color.BLACK);
            g.fillRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }

    class RoundedPanel extends JPanel {
        private int radius;

        public RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (animationStarted) {
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                
                AffineTransform originalTransform = g2.getTransform();
                g2.translate(centerX, centerY);
                g2.scale(currentScale, currentScale);
                g2.translate(-centerX, -centerY);
                
                // Draw background with rounded corners
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
                
                g2.setTransform(originalTransform);
            } else {
                // Draw background with rounded corners
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            }
            
            g2.dispose();
        }
        
        @Override
        protected void paintChildren(Graphics g) {
            if (animationStarted && currentScale < 0.3) {
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
     
    // Kelas JTextField kustom dengan border rounded + placeholder
    static class RoundedTextField extends JTextField {

        private int radius;
        private String placeholder;
        private boolean isPlaceholderActive;

        public RoundedTextField(int radius, String placeholder) {
            super(placeholder);
            this.radius = radius;
            this.placeholder = placeholder;
            this.isPlaceholderActive = true;
            setForeground(Color.GRAY);
            setOpaque(false);
            setBorder(new EmptyBorder(5, 10, 5, 10));

            addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (getText().equals(placeholder) && isPlaceholderActive) {
                        setText("");
                        setForeground(Color.BLACK);
                        isPlaceholderActive = false;
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (getText().isEmpty()) {
                        setText(placeholder);
                        setForeground(Color.GRAY);
                        isPlaceholderActive = true;
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius); // Rounded textfield
            g2.setColor(Color.GRAY);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius); // Border rounded
            g2.dispose();
            super.paintComponent(g);
        }
    }
}