package produk;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.sql.*;
import db.conn;

public class ScanBarcodeDialog extends JDialog {

    private JPanel contentPanel;
    private Timer animationTimer, closeAnimationTimer;
    private final int ANIMATION_DURATION = 300, ANIMATION_DELAY = 10, FINAL_WIDTH = 500, FINAL_HEIGHT = 400;
    private float currentScale = 0.01f;
    private boolean animationStarted = false;
    private JComponent glassPane;
    private JFrame parentFrame;
    private JTextField codeField;
    private static boolean isShowingPopup = false;
    private String scannedBarcode = "";
    private Connection con;

    public ScanBarcodeDialog(JFrame parent) {
        super(parent, true);
        this.parentFrame = parent;
        setUndecorated(true);
        setSize(FINAL_WIDTH, FINAL_HEIGHT);

        con = conn.getConnection();

        if (isShowingPopup) {
            dispose();
            return;
        }
        isShowingPopup = true;

        setupGlassPane();
        setupContentPanel();
        initComponents();
        setLocationRelativeTo(parent);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanupAndClose();
            }
        });
    }

    private void setupGlassPane() {
        glassPane = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 180));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        glassPane.setOpaque(false);
        glassPane.setBounds(0, 0, parentFrame.getWidth(), parentFrame.getHeight());

        cleanupExistingGlassPane();
        parentLayeredPane().add(glassPane, JLayeredPane.POPUP_LAYER);
    }

    private void setupContentPanel() {
        contentPanel = new RoundedPanel(20);
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBounds(0, 0, FINAL_WIDTH, FINAL_HEIGHT);
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(contentPanel);
        setBackground(new Color(0, 0, 0, 0));
    }

    private JLayeredPane parentLayeredPane() {
        return parentFrame.getLayeredPane();
    }

    private void cleanupExistingGlassPane() {
        for (Component comp : parentLayeredPane().getComponentsInLayer(JLayeredPane.POPUP_LAYER)) {
            if (comp instanceof JComponent) {
                parentLayeredPane().remove(comp);
            }
        }
        parentLayeredPane().repaint();
    }

    private void initComponents() {
        contentPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        contentPanel.add(createScanPanel(), BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(null);
        headerPanel.setOpaque(false);
        headerPanel.setPreferredSize(new Dimension(FINAL_WIDTH, 60));

        JLabel titleLabel = new JLabel("Scan Product Barcode");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(100, 15, 300, 30);
        headerPanel.add(titleLabel);

        JButton closeButton = new JButton("×");
        closeButton.setFont(new Font("Poppins", Font.PLAIN, 30));
        closeButton.setForeground(Color.BLACK);
        closeButton.setBackground(Color.WHITE);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.setBounds(FINAL_WIDTH - 60, 2, 50, 50);
        closeButton.setMargin(new Insets(0, 0, 0, 0));
        closeButton.setVerticalAlignment(SwingConstants.TOP);
        closeButton.setHorizontalAlignment(SwingConstants.CENTER);
        closeButton.addActionListener(e -> startCloseAnimation());

        headerPanel.add(closeButton);
        startScaleAnimation();

        return headerPanel;
    }

    private JPanel createScanPanel() {
        JPanel scanPanel = new JPanel();
        scanPanel.setOpaque(false);
        scanPanel.setLayout(new BoxLayout(scanPanel, BoxLayout.Y_AXIS));
        scanPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        scanPanel.add(createScannerBox());
        scanPanel.add(Box.createVerticalStrut(20));

        JLabel orLabel = new JLabel("-OR-");
        orLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        orLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        scanPanel.add(orLabel);
        scanPanel.add(Box.createVerticalStrut(20));

        JLabel codeLabel = new JLabel("Enter Code Number / Masukkan Nomor Code");
        codeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        codeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        scanPanel.add(codeLabel);
        scanPanel.add(Box.createVerticalStrut(10));

        codeField = createRoundedTextField("Enter barcode number", 20);

        ((AbstractDocument) codeField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("\\d+")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("\\d*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        codeField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    processBarcode();
                }
            }
        });

        scanPanel.add(codeField);
        return scanPanel;
    }

    private void processBarcode() {
        String barcode = codeField.getText();
        try {
            if (!barcode.isEmpty() && !barcode.equals("Enter barcode number")) {
                String sql = "SELECT id_produk FROM produk WHERE id_produk = ?";
                try (PreparedStatement st = con.prepareStatement(sql)) {
                    st.setString(1, barcode);
                    ResultSet rs = st.executeQuery();
                    if (rs.next()) {
                        scannedBarcode = barcode;
                        System.out.println("BERHASIL MENCOCOKAN " + scannedBarcode);
                        startCloseAnimation();
//                        this.dispose();

                        // Baru munculkan AfterScanBarcodeDialog
                        AfterScanBarcodeDialog dialog = new AfterScanBarcodeDialog(parentFrame, scannedBarcode);
                        dialog.setVisible(true);

//                        
                    }else{
                        System.out.println("DATA PRODUK TIDAK ADA");
                    }
                }
            } else {
                System.out.println("harap scan terlebih dahulu");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getScannedBarcode() {
        return scannedBarcode;
    }
    public void showScanBarcodeDialog() {
    // Pastikan tidak ada dialog yang sedang aktif
    if (ScanBarcodeDialog.isShowingPopup) {
        return;
    }
    
    ScanBarcodeDialog dialog = new ScanBarcodeDialog(parentFrame);
    dialog.showDialog();
}
    private JPanel createScannerBox() {
        JPanel scannerBox = new JPanel(new BorderLayout()) {
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                float[] dash = {5.0f, 5.0f};
                g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, dash, 0.0f));
                g2.setColor(Color.DARK_GRAY);
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
                g2.dispose();
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(245, 245, 245));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        scannerBox.setPreferredSize(new Dimension(400, 150));
        scannerBox.setMaximumSize(new Dimension(400, 150));
        scannerBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        scannerBox.setOpaque(false);

        JPanel scannerContentPanel = new JPanel();
        scannerContentPanel.setLayout(new BoxLayout(scannerContentPanel, BoxLayout.X_AXIS));
        scannerContentPanel.setOpaque(false);

        scannerContentPanel.add(createScannerImagePanel());
        scannerContentPanel.add(createBarcodePanel());

        scannerBox.add(scannerContentPanel, BorderLayout.CENTER);
        return scannerBox;
    }

    private JPanel createScannerImagePanel() {
        JPanel scannerImagePanel = new RoundedImagePanel(15) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    Image img = ImageIO.read(new File("src/SourceImage/scan_barcode.png"));
                    if (img != null) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setClip(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                        g2.drawImage(img, 10, (getHeight() - 100) / 2, 100, 100, this);
                        g2.dispose();
                    } else {
                        drawHandheldScanner(g);
                    }
                } catch (Exception e) {
                    drawHandheldScanner(g);
                }
            }

            private void drawHandheldScanner(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setClip(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                g2.setColor(Color.BLACK);
                int[] xPoints = {30, 80, 90, 90, 60, 30, 20};
                int[] yPoints = {45, 45, 55, 80, 120, 120, 80};
                g2.fillPolygon(xPoints, yPoints, 7);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(35, 50, 40, 10, 5, 5);
                g2.dispose();
            }
        };
        scannerImagePanel.setPreferredSize(new Dimension(120, 150));
        return scannerImagePanel;
    }

    private JPanel createBarcodePanel() {
        JPanel barcodePanel = new RoundedImagePanel(15) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setClip(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                g2.setColor(Color.BLACK);
                int barHeight = 60;
                int startY = (getHeight() - barHeight) / 2;
                int x = 20;
                int[] barWidths = {2, 1, 3, 1, 2, 2, 4, 1, 2, 3, 1, 2};
                for (int width : barWidths) {
                    g2.fillRect(x, startY, width, barHeight);
                    x += width + 3;
                }
                g2.dispose();
            }
        };
        barcodePanel.setPreferredSize(new Dimension(280, 150));
        return barcodePanel;
    }

    private JTextField createRoundedTextField(String placeholder, int radius) {
        RoundedTextField field = new RoundedTextField(radius, placeholder);
        field.setOpaque(false);
        field.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        field.setMaximumSize(new Dimension(400, 40));
        field.setPreferredSize(new Dimension(400, 40));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        return field;
    }

    public void showDialog() {
        setVisible(true);
        startScaleAnimation();
    }

    private void startScaleAnimation() {
        if (animationStarted || (animationTimer != null && animationTimer.isRunning())) {
            return;
        }

        animationStarted = true;
        final int totalFrames = ANIMATION_DURATION / ANIMATION_DELAY;
        final int[] currentFrame = {0};

        animationTimer = new Timer(ANIMATION_DELAY, e -> {
            currentFrame[0]++;
            float progress = (float) currentFrame[0] / totalFrames;
            float easedProgress = 1 - (1 - progress) * (1 - progress) * (1 - progress);
            currentScale = 0.01f + 0.99f * easedProgress;
            repaint();

            if (currentFrame[0] >= totalFrames) {
                animationTimer.stop();
                currentScale = 1.0f;
                repaint();
            }
        });

        animationTimer.start();
    }

    private void startCloseAnimation() {
        if (closeAnimationTimer != null && closeAnimationTimer.isRunning()) {
            closeAnimationTimer.stop();
        }

        final int totalFrames = ANIMATION_DURATION / ANIMATION_DELAY;
        final int[] currentFrame = {0};

        closeAnimationTimer = new Timer(ANIMATION_DELAY, e -> {
            currentFrame[0]++;
            float progress = (float) currentFrame[0] / totalFrames;
            float easedProgress = progress * progress;
            currentScale = 1.0f - 0.99f * easedProgress;
            repaint();

            if (currentFrame[0] >= totalFrames) {
                closeAnimationTimer.stop();
                cleanupAndClose();
            }
        });

        closeAnimationTimer.start();
    }

    private void cleanupAndClose() {
        isShowingPopup = false;
        closePopup();
        dispose();
    }

    private void closePopup() {
        parentLayeredPane().remove(glassPane);
        parentLayeredPane().repaint();
    }

    // Kelas pendukung
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
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
                g2.setTransform(originalTransform);
            } else {
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

    class RoundedImagePanel extends JPanel {

        private int radius;

        public RoundedImagePanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(240, 240, 240));
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
            g2.dispose();
        }
    }

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

            addFocusListener(new FocusAdapter() {
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
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.setColor(Color.GRAY);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
