package produk;

import SourceCode.RoundedButtonLaporan;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import javax.swing.border.EmptyBorder;
import Form.Productt;
import db.conn;
import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;

public class AfterScanBarcodeDialog extends JDialog {

    private JPanel contentPanel;
    private Timer animationTimer, closeAnimationTimer;
    private final int ANIMATION_DURATION = 300, ANIMATION_DELAY = 10, FINAL_WIDTH = 600, FINAL_HEIGHT = 450;
    private float currentScale = 0.01f;
    private boolean animationStarted = false;
    private JComponent glassPane;
    private JFrame parentFrame;
    private JButton editButton;
    private static boolean isShowingPopup = false;
    private String scannedBarcode = "";

    // Data produk
    private String productCategory = "";
    private String productName = "";
    private String brand = "";
    private String size = "";
    private String price = "";
    private String stock = "";
    private String style = "";
    private String gender = "";
    private byte[] productImage;
    private Connection con;

    public AfterScanBarcodeDialog(JFrame parent, String barcode) {
        super(parent, true);
        this.parentFrame = parent;
        setUndecorated(true);
        setSize(FINAL_WIDTH, FINAL_HEIGHT);
        con = conn.getConnection();
        this.scannedBarcode = barcode;

        if (isShowingPopup) {
            dispose();
            return;
        }
        isShowingPopup = true;

        fetchProductData();
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
        contentPanel.add(createProductPanel(), BorderLayout.CENTER);
        contentPanel.add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("Scan a New Barcode to Replace the Product");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton closeButton = new JButton("×");
        closeButton.setFont(new Font("Poppins", Font.BOLD, 24));
        closeButton.setForeground(Color.BLACK);
        closeButton.setBackground(Color.WHITE);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.setMargin(new Insets(0, 0, 0, 0));
        closeButton.addActionListener(e -> startCloseAnimation());
        headerPanel.add(closeButton, BorderLayout.EAST);

        // Garis pemisah
        JPanel separator = new JPanel();
        separator.setBackground(new Color(220, 220, 220));
        separator.setPreferredSize(new Dimension(FINAL_WIDTH, 1));

        JPanel headerContainer = new JPanel(new BorderLayout());
        headerContainer.setOpaque(false);
        headerContainer.add(headerPanel, BorderLayout.CENTER);
        headerContainer.add(separator, BorderLayout.SOUTH);

        startScaleAnimation();

        return headerContainer;
    }

    private void fetchProductData() {
        try {
            String query = "SELECT p.jenis_produk, p.nama_produk, p.merk, p.size, "
                    + "p.harga_jual, s.nama_style, p.gender, p.gambar "
                    + "FROM produk p "
                    + "JOIN style s ON p.id_style = s.id_style "
                    + "WHERE p.id_produk = ?";

            try (PreparedStatement stmt = con.prepareStatement(query)) {
                stmt.setString(1, scannedBarcode);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        // Get data from result set
                        productCategory = rs.getString("jenis_produk");
                        productName = rs.getString("nama_produk");
                        brand = rs.getString("merk");
                        size = rs.getString("size");

                        // Format price with thousand separator
                        double priceValue = rs.getDouble("harga_jual");
                        NumberFormat currencyFormat = NumberFormat.getNumberInstance(new Locale("id", "ID"));
                        price = currencyFormat.format(priceValue);

                        stock = String.valueOf(1);
                        style = rs.getString("nama_style");
                        gender = rs.getString("gender");

                        // Langsung ambil data BLOB
                        try {
                            Blob imageBlob = rs.getBlob("gambar");
                            if (imageBlob != null) {
                                int blobLength = (int) imageBlob.length();

                                // Cek ukuran BLOB - jika terlalu kecil (di bawah 1KB), anggap tidak valid
                                if (blobLength < 1024) {
                                    System.out.println("Gambar produk terlalu kecil (" + blobLength + " bytes), menggunakan gambar default");
                                    productImage = null;
                                } else {
                                    productImage = imageBlob.getBytes(1, blobLength);
                                }
                                imageBlob.free(); // Penting untuk membebaskan resource BLOB
                            } else {
                                productImage = null;
                            }
                        } catch (Exception e) {
                            System.err.println("Error saat membaca BLOB gambar: " + e.getMessage());
                            productImage = null;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPanel createProductPanel() {
        JPanel productPanel = new JPanel(new BorderLayout());
        productPanel.setOpaque(false);

        // Buat panel konten dengan null layout untuk penempatan bebas
        JPanel contentPanel = new JPanel(null);
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        contentPanel.setPreferredSize(new Dimension(FINAL_WIDTH, 280));

        // Panel gambar produk dengan border rounded - ukuran lebih kecil
        JPanel imagePanel = new RoundedBorder(10);
        imagePanel.setBounds(20, 30, 250, 250); // Posisi kiri atas dengan ukuran lebih kecil
        imagePanel.setLayout(new BorderLayout());

        // Tambahkan gambar produk dari data BLOB
        try {
            if (productImage != null) {
                // Konversi byte array dari BLOB menjadi ImageIcon
                ImageIcon imageIcon = new ImageIcon(productImage);
                Image image = imageIcon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(image));
                imageLabel.setHorizontalAlignment(JLabel.CENTER);
                imagePanel.add(imageLabel, BorderLayout.CENTER);
            } else {
                ImageIcon defaultIcon = new ImageIcon("src/SourceImage/gambar_sepatu.png");
                Image defaultImage = defaultIcon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
                JLabel defaultLabel = new JLabel(new ImageIcon(defaultImage));
                defaultLabel.setHorizontalAlignment(JLabel.CENTER);
                imagePanel.add(defaultLabel, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            // Jika ada error, buat placeholder
            JLabel placeholderLabel = new JLabel("Product Image");
            placeholderLabel.setHorizontalAlignment(JLabel.CENTER);
            placeholderLabel.setVerticalAlignment(JLabel.CENTER);
            imagePanel.add(placeholderLabel, BorderLayout.CENTER);
            System.err.println("Error menampilkan gambar: " + e.getMessage());
        }

        // Label kategori produk
        JLabel categoryLabel = new JLabel("Product Category / Kategori : " + productCategory);
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 12));
        categoryLabel.setBounds(290, 25, 350, 20);

        // Label nama produk
        JLabel nameLabel = new JLabel(productName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setBounds(290, 50, 350, 20);

        // Label dan nilai untuk setiap properti produk
        // Merk
        JLabel merkLabel = new JLabel("Merk:");
        merkLabel.setFont(new Font("Arial", Font.BOLD, 14));
        merkLabel.setBounds(290, 80, 100, 20);

        JLabel merkValue = new JLabel(brand);
        merkValue.setFont(new Font("Arial", Font.BOLD, 14));
        merkValue.setBounds(400, 80, 240, 20);

        // Ukuran
        JLabel sizeLabel = new JLabel("Ukuran / Size:");
        sizeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        sizeLabel.setBounds(290, 105, 100, 20);

        JLabel sizeValue = new JLabel(size);
        sizeValue.setFont(new Font("Arial", Font.BOLD, 14));
        sizeValue.setBounds(400, 105, 240, 20);

        // Harga
        JLabel priceLabel = new JLabel("Price / Harga:");
        priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        priceLabel.setBounds(290, 130, 100, 20);

        JLabel priceValue = new JLabel(price);
        priceValue.setFont(new Font("Arial", Font.BOLD, 14));
        priceValue.setBounds(400, 130, 240, 20);

        // Stok
        JLabel stockLabel = new JLabel("Stok:");
        stockLabel.setFont(new Font("Arial", Font.BOLD, 14));
        stockLabel.setBounds(290, 155, 100, 20);

        JLabel stockValue = new JLabel(stock);
        stockValue.setFont(new Font("Arial", Font.BOLD, 14));
        stockValue.setBounds(400, 155, 240, 20);

        // Style dan Gender dalam satu baris
        JLabel styleLabel = new JLabel("Style: " + style);
        styleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        styleLabel.setBounds(290, 180, 120, 20);

        JLabel genderLabel = new JLabel("Gender: " + gender);
        genderLabel.setFont(new Font("Arial", Font.BOLD, 14));
        genderLabel.setBounds(430, 180, 120, 20);

        // Tambahkan semua komponen ke panel konten
        contentPanel.add(imagePanel);
        contentPanel.add(categoryLabel);
        contentPanel.add(nameLabel);
        contentPanel.add(merkLabel);
        contentPanel.add(merkValue);
        contentPanel.add(sizeLabel);
        contentPanel.add(sizeValue);
        contentPanel.add(priceLabel);
        contentPanel.add(priceValue);
        contentPanel.add(stockLabel);
        contentPanel.add(stockValue);
        contentPanel.add(styleLabel);
        contentPanel.add(genderLabel);

        // Tambahkan garis pembatas di bagian bawah panel produk
        JPanel separator = new JPanel();
        separator.setBackground(new Color(220, 220, 220));
        separator.setPreferredSize(new Dimension(FINAL_WIDTH, 1));

        productPanel.add(contentPanel, BorderLayout.CENTER);
        productPanel.add(separator, BorderLayout.SOUTH);

        return productPanel;
    }
    
    public String getScannedBarcode() {
        return scannedBarcode;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        footerPanel.setOpaque(false);

        editButton = new JButton("Edit Product Data");
        editButton.setIcon(new ImageIcon("src/SourceImage/edit_icon.png"));
        editButton.setPreferredSize(new Dimension(200, 40));
        editButton.setFocusPainted(false);
        editButton.setBorderPainted(false);
        editButton.setContentAreaFilled(false);
        editButton.setBackground(new Color(60, 63, 65));
        editButton.setForeground(Color.WHITE);
        editButton.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (scannedBarcode != null && !scannedBarcode.isEmpty()) {
                startCloseAnimation(); // animasi tutup pop-up (jika ada)

                // Panggil method switchToEditProductPanel dengan parameter scannedBarcode
                Productt.getMainFrame().switchToEditProductPanelScan(scannedBarcode);
            } else {
                JOptionPane.showMessageDialog(null, "Barcode kosong atau tidak valid.");
            }
        }
    });

        // Buat dan atur UI rounded dengan border hitam
        RoundedButtonLaporan roundedUI = new RoundedButtonLaporan();
        roundedUI.setBorderColor(Color.BLACK);  // Atur warna border hitam
        roundedUI.setBorderRadius(40);
        editButton.setUI(roundedUI);

        footerPanel.add(editButton);

        return footerPanel;
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

    // Class RoundedBorder untuk panel gambar
    class RoundedBorder extends JPanel {

        private int radius;

        public RoundedBorder(int radius) {
            this.radius = radius;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(245, 245, 245));
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));

            // Tambahkan border
            g2.setColor(new Color(220, 220, 220));
            g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
