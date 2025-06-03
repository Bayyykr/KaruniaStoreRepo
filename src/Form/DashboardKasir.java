package Form;

import PopUp_all.*;
import SourceCode.RoundedBorder;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import db.conn;
import java.text.SimpleDateFormat;
import java.util.Date;
import produk.ComboboxCustom;
import java.sql.SQLException;

public class DashboardKasir extends JPanel {

    Component parentComponent = this;
    private JFrame parentFrame;
    private CardLayout headerCardLayout;
    private JPanel headerCardsPanel;
    private int currentCardIndex = 0;
    private final String[] cardNames = {"welcome", "aturpromodandiskon", "barangTerlaris"};

    // Animation properties
    private JPanel animationPanel;
    private final int ANIMATION_DURATION = 300; // milliseconds - reduced to 0.3s
    private BufferedImage currentCardImage;
    private BufferedImage nextCardImage;
    private float animationProgress = 0.0f;
    private boolean isAnimating = false;
    private Timer animationTimer;
    private int animationDirection = 1; // 1 for forward, -1 for backward

    // Button animation properties for slide buttons only
    private java.util.List<SlideButton> slideButtons = new java.util.ArrayList<>();
    private Timer buttonAnimationTimer;
    private float buttonAnimationProgress = 0.0f;
    private boolean isButtonAnimating = false;
    private String namaUser = "";

    private Connection con;

    private class SlideButton {

        JButton button;
        float scale = 0.0f;

        public SlideButton(JButton button) {
            this.button = button;
        }
    }

    public DashboardKasir() {
        setLayout(new BorderLayout());
        setOpaque(true);
        con = conn.getConnection();
        setNamaUser();
        initComponents();
    }

    private void initComponents() {
        removeAll();

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(true);

        // Create card layout for header
        headerCardLayout = new CardLayout();
        headerCardsPanel = new JPanel(headerCardLayout);
        headerCardsPanel.setPreferredSize(new Dimension(800, 233));

        // Create header panels
        JPanel welcomeHeaderPanel = createWelcomeHeader();
//        JPanel createStokOpnamePanel = createStokOpname();
        JPanel CekPromoDanDiskonPanel = createDiksondanPromo();
        JPanel barangTelarisPanelSlide = createBarangTerlaris();

        headerCardsPanel.add(welcomeHeaderPanel, "welcome");
//        headerCardsPanel.add(createStokOpnamePanel, "stokOpname");
        headerCardsPanel.add(CekPromoDanDiskonPanel, "aturpromodandiskon");
        headerCardsPanel.add(barangTelarisPanelSlide, "barangTerlaris");

        // Create animation panel that will be used for transitions
        animationPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (isAnimating && currentCardImage != null && nextCardImage != null) {
                    Graphics2D g2d = (Graphics2D) g;
                    int width = getWidth();
                    int height = getHeight();

                    // Draw sliding animation based on direction and progress
                    if (animationDirection == 1) { // Forward slide
                        g2d.drawImage(currentCardImage, (int) (-width * animationProgress), 0, width, height, null);
                        g2d.drawImage(nextCardImage, (int) (width - width * animationProgress), 0, width, height, null);
                    } else { // Backward slide
                        g2d.drawImage(currentCardImage, (int) (width * animationProgress), 0, width, height, null);
                        g2d.drawImage(nextCardImage, (int) (-width + width * animationProgress), 0, width, height, null);
                    }
                }
            }
        };
        animationPanel.setPreferredSize(new Dimension(800, 233));

        // Set up animation timer - faster animation speed
        animationTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                animationProgress += 0.16f; // Increased for faster animation
                if (animationProgress >= 1.0f) {
                    // Animation complete
                    isAnimating = false;
                    animationProgress = 0.0f;
                    animationTimer.stop();
                    animationPanel.setVisible(false);
                    headerCardsPanel.setVisible(true);
                    // Show the next card
                    headerCardLayout.show(headerCardsPanel, cardNames[currentCardIndex]);
                    // Start button animations for slide buttons
                    startSlideButtonAnimations();
                } else {
                    // Continue animation
                    animationPanel.repaint();
                }
            }
        });

        // Set up button animation timer for slide buttons
        buttonAnimationTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonAnimationProgress += 0.16f; // Faster animation
                if (buttonAnimationProgress >= 1.0f) {
                    // Animation complete
                    isButtonAnimating = false;
                    buttonAnimationProgress = 0.0f;
                    buttonAnimationTimer.stop();

                    // Set all buttons to full scale
                    for (SlideButton sb : slideButtons) {
                        sb.scale = 1.0f;
                    }
                    repaint();
                } else {
                    // Update button scales
                    for (SlideButton sb : slideButtons) {
                        sb.scale = buttonAnimationProgress;
                    }
                    repaint();
                }
            }
        });

        // Add panels to the main panel
        JPanel headerContainer = new JPanel(new CardLayout());
        headerContainer.add(headerCardsPanel, "cards");
        headerContainer.add(animationPanel, "animation");
        mainPanel.add(headerContainer, BorderLayout.NORTH);

        // Initially show the cards panel
        animationPanel.setVisible(false);
        headerCardsPanel.setVisible(true);

        // Create content panel with BorderLayout
        JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
        contentPanel.setBackground(Color.white);

        // Buat panel wrapper dengan GridLayout untuk 2 panel yang sejajar
        JPanel panelWrapper = new JPanel(new GridLayout(1, 2, 20, 0));
        panelWrapper.setOpaque(false);
        panelWrapper.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Panel Stok Harian
        JPanel stockPanel = createInventoryStockPanel();

        // Panel Data Absen
        JPanel stokPanel = createStokPanel();

        // Tambahkan kedua panel ke wrapper
        panelWrapper.add(stockPanel);
        panelWrapper.add(stokPanel);

        // Tambahkan wrapper ke content panel
        contentPanel.add(panelWrapper, BorderLayout.CENTER);

        // Add content panel to main panel
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Start slide button animations for initial view
        SwingUtilities.invokeLater(() -> {
            startSlideButtonAnimations();
        });
    }

    private void startSlideButtonAnimations() {
        if (!slideButtons.isEmpty()) {
            // Reset button scale
            for (SlideButton sb : slideButtons) {
                sb.scale = 0.0f;
            }

            // Start animation
            isButtonAnimating = true;
            buttonAnimationProgress = 0.0f;
            buttonAnimationTimer.start();
        }
    }

    // Method to animate slide transition
    private void animateTransition(boolean forward) {
        if (isAnimating) {
            return;
        }

        isAnimating = true;
        animationDirection = forward ? 1 : -1;

        // Calculate next card index
        int nextCardIndex = forward
                ? (currentCardIndex + 1) % cardNames.length
                : (currentCardIndex - 1 + cardNames.length) % cardNames.length;

        // Capture current card as image
        currentCardImage = new BufferedImage(headerCardsPanel.getWidth(), headerCardsPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
        headerCardLayout.show(headerCardsPanel, cardNames[currentCardIndex]);
        headerCardsPanel.paint(currentCardImage.getGraphics());

        // Capture next card as image
        nextCardImage = new BufferedImage(headerCardsPanel.getWidth(), headerCardsPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
        headerCardLayout.show(headerCardsPanel, cardNames[nextCardIndex]);
        headerCardsPanel.paint(nextCardImage.getGraphics());

        // Switch to animation panel
        headerCardsPanel.setVisible(false);
        animationPanel.setVisible(true);

        // Start animation
        animationProgress = 0.0f;
        animationTimer.start();

        // Update current card index
        currentCardIndex = nextCardIndex;

    }

    private JPanel createWelcomeHeader() {
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, getWidth(), getHeight());
                try {
                    BufferedImage bgImage = ImageIO.read(getClass().getResourceAsStream("/SourceImage/slide_stokOpname.png"));
                    // Draw the image to fit the entire panel, with small margins
                    g.drawImage(bgImage, 25, 0, getWidth() - 50, getHeight(), this);
                } catch (Exception e) {
                    // Fallback if image not found
                    g.setColor(new Color(200, 220, 255));
                    g.fillRect(0, 0, getWidth(), getHeight());
                    e.printStackTrace();
                }
            }
        };

        headerPanel.setLayout(null);
        headerPanel.setPreferredSize(new Dimension(800, 233));

        JLabel welcomeLabel = new JLabel("Welcome " + namaUser);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 30));
        welcomeLabel.setForeground(Color.white);
        welcomeLabel.setBounds(80, 50, 400, 40);

        // Fix the position of the right navigation button
        JButton slideRightButton = createAnimatedSlideButton("→", new Dimension(50, 50), 1010, 100);
        slideRightButton.addActionListener(e -> {
            animateTransition(true);
        });

        // Fix the position and size of the report button
        JButton reportButton = createRegularButton("STOK OPNAME", new Dimension(300, 50), 80, 140, true, "/SourceImage/next-icon-dark.png");
        reportButton.addActionListener(e -> {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(reportButton);
            PopUp_DashboardKaryawanStokOpname stokopname = new PopUp_DashboardKaryawanStokOpname(parentFrame);
            stokopname.setVisible(true);
            System.out.println("ini button stok opname");
        });

        headerPanel.add(welcomeLabel);
        headerPanel.add(reportButton);
        headerPanel.add(slideRightButton);

        return headerPanel;
    }

    private JPanel createDiksondanPromo() {
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, getWidth(), getHeight());
                try {
                    BufferedImage bgImage = ImageIO.read(getClass().getResourceAsStream("/SourceImage/slide-diskon.png"));
                    // Draw the image to fit the entire panel
                    g.drawImage(bgImage, 25, 0, getWidth() - 50, getHeight(), this);
                } catch (Exception e) {
                    // Fallback if image not found
                    g.setColor(new Color(200, 220, 255));
                    g.fillRect(0, 0, getWidth(), getHeight());
                    e.printStackTrace();
                }
            }
        };

        headerPanel.setLayout(null);
        headerPanel.setPreferredSize(new Dimension(800, 233));

        // Fix left button position
        JButton slideLeftButton = createAnimatedSlideButton("←", new Dimension(50, 50), 5, 100);
        slideLeftButton.addActionListener(e -> {
            animateTransition(false);
        });

        JLabel titleLabel = new JLabel("Welcome " + namaUser);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setForeground(Color.white);
        titleLabel.setBounds(80, 50, 400, 40);

        // Correctly position and size the product image
//        JLabel imageLabel = new JLabel();
//        try {
//            BufferedImage shoeImage = ImageIO.read(getClass().getResourceAsStream("/SourceImage/slide-laporan.png"));
//            ImageIcon icon = new ImageIcon(shoeImage.getScaledInstance(150, 150, Image.SCALE_SMOOTH));
//            imageLabel.setIcon(icon);
//            imageLabel.setBounds(600, 40, 150, 150);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        // Fix button position and size
        JButton aturDiskonButton = createRegularButton("LIHAT DISKON", new Dimension(330, 50), 80, 140, true, "/SourceImage/next-icon-dark.png");
        aturDiskonButton.addActionListener(e -> {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(aturDiskonButton);
            PopUp_DashboardKasirCekPromoDanDiskon promodiskon = new PopUp_DashboardKasirCekPromoDanDiskon(parentFrame);
            promodiskon.setVisible(true);
            System.out.println("ini diskon dan promo");
        });

        // Fix right button position
        JButton slideRightButton = createAnimatedSlideButton("→", new Dimension(50, 50), 1010, 100);
        slideRightButton.addActionListener(e -> {
            animateTransition(true);
        });

        headerPanel.add(slideLeftButton);
        headerPanel.add(titleLabel);
//        headerPanel.add(imageLabel);
        headerPanel.add(aturDiskonButton);
        headerPanel.add(slideRightButton);

        return headerPanel;
    }

    private JPanel createBarangTerlaris() {
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, getWidth(), getHeight());
                try {
                    BufferedImage bgImage = ImageIO.read(getClass().getResourceAsStream("/SourceImage/slide-terlaris.png"));
                    // Draw the image to fit the entire panel
                    g.drawImage(bgImage, 25, 0, getWidth() - 50, getHeight(), this);
                } catch (Exception e) {
                    // Fallback if image not found
                    g.setColor(new Color(200, 220, 255));
                    g.fillRect(0, 0, getWidth(), getHeight());
                    e.printStackTrace();
                }
            }
        };

        headerPanel.setLayout(null);
        headerPanel.setPreferredSize(new Dimension(800, 233));

        // Fix left button position
        JButton slideLeftButton = createAnimatedSlideButton("←", new Dimension(50, 50), 5, 100);
        slideLeftButton.addActionListener(e -> {
            animateTransition(false);
        });

        JLabel titleLabel = new JLabel("Welcome " + namaUser);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setForeground(Color.white);
        titleLabel.setBounds(80, 50, 400, 40);

        // Position and size the promo image correctly
//        JLabel imageLabel = new JLabel();
//        try {
//            BufferedImage promoImage = ImageIO.read(getClass().getResourceAsStream("/SourceImage/slide-diskon.png"));
//            ImageIcon icon = new ImageIcon(promoImage.getScaledInstance(150, 150, Image.SCALE_SMOOTH));
//            imageLabel.setIcon(icon);
//            imageLabel.setBounds(600, 40, 150, 150);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        // Fix button position and size
        JButton barangTerlarisButton = createRegularButton("BARANG TERLARIS", new Dimension(320, 50), 80, 140, true, "/SourceImage/next-icon-dark.png");
        barangTerlarisButton.addActionListener(e -> {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(barangTerlarisButton);
            PopUp_DashboardOwnerBarangTerlaris popupbarangterlaris = new PopUp_DashboardOwnerBarangTerlaris(parentFrame);
            popupbarangterlaris.setVisible(true);
            System.out.println("ini barang terlaris");
        });

        headerPanel.add(slideLeftButton);
        headerPanel.add(titleLabel);
//        headerPanel.add(imageLabel);
        headerPanel.add(barangTerlarisButton);

        return headerPanel;
    }

    // Membuat button reguler (tanpa animasi skala)
    private JButton createAnimatedSlideButton(String text, Dimension size, int x, int y) {
        // Create button with no text
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                float scale = 1.0f;
                for (SlideButton sb : slideButtons) {
                    if (sb.button == this) {
                        scale = sb.scale;
                        break;
                    }
                }

                // Apply scaling transformation
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                AffineTransform oldTransform = g2d.getTransform();
                g2d.translate(centerX, centerY);
                g2d.scale(scale, scale);
                g2d.translate(-centerX, -centerY);

                // Skip drawing if scale is too small
                if (scale < 0.05f) {
                    g2d.dispose();
                    return;
                }

                // Shadow
                g2d.setColor(new Color(0, 0, 0, 60));
                g2d.fillRoundRect(4, 2, getWidth() - 6, getHeight() - 2, 50, 50);

                // Button background
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 50, 50);

                try {
                    String iconPath = text.equals("→")
                            ? "/SourceImage/right-icon.png"
                            : "/SourceImage/left-icon.png";

                    BufferedImage iconImage = ImageIO.read(getClass().getResourceAsStream(iconPath));
                    int iconSize = 24; // Size of icon
                    g2d.drawImage(iconImage,
                            (getWidth() - iconSize) / 2,
                            (getHeight() - iconSize) / 2,
                            iconSize, iconSize, null);
                } catch (IOException e) {
                    e.printStackTrace();
                    // Fallback if icon not found
                    g2d.setColor(Color.BLACK);
                    g2d.setFont(new Font("Arial", Font.BOLD, 20));
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(text);
                    int textHeight = fm.getHeight();
                    g2d.drawString(text, (getWidth() - textWidth) / 2, (getHeight() + textHeight) / 2 - 5);
                }

                // Restore original transform
                g2d.setTransform(oldTransform);
                g2d.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return size;
            }
        };

        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBounds(x, y, size.width, size.height);

        // Add to animated slide buttons list
        SlideButton slideButton = new SlideButton(button);
        slideButtons.add(slideButton);

        return button;
    }

    // Create a regular button (without scale animation)
    private JButton createRegularButton(String text, Dimension size, int x, int y, boolean withIcon, String iconPath) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow
                g2d.setColor(new Color(0, 0, 0, 60));
                g2d.fillRoundRect(4, 2, getWidth() - 6, getHeight() - 2, 50, 50);

                // Button background
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 50, 50);

                // Text positioning
                g2d.setColor(Color.BLACK);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getHeight();

                // Add Icon if path is provided
                if (withIcon && iconPath != null) {
                    try {
                        BufferedImage icon = ImageIO.read(getClass().getResourceAsStream(iconPath));
                        g2d.drawImage(icon, getWidth() - 50, (getHeight() - 40) / 2, 40, 40, null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // Draw text
                g2d.drawString(getText(), (getWidth() - textWidth) / 2 - 10, (getHeight() + textHeight) / 2 - 5);

                g2d.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return size;
            }
        };

        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setBounds(x, y, size.width, size.height);

        return button;
    }

    private JPanel createInventoryStockPanel() {
        JPanel stockPanel = new JPanel();
        stockPanel.setLayout(new BorderLayout(0, 5));
        stockPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(50, new Color(220, 220, 220), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        stockPanel.setBackground(Color.white);

        JLabel titleLabel = new JLabel("Stok Harian");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        stockPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel itemsContainer = new JPanel();
        itemsContainer.setLayout(new BoxLayout(itemsContainer, BoxLayout.Y_AXIS));
        itemsContainer.setBackground(Color.WHITE);
        itemsContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        String sql = "SELECT p.jenis_produk, COALESCE(SUM(ks.produk_sisa), 0) AS total_stok "
                + "FROM produk p "
                + "LEFT JOIN ( "
                + "    SELECT ks1.id_produk, ks1.produk_sisa "
                + "    FROM kartu_stok ks1 "
                + "    INNER JOIN ( "
                + "        SELECT id_produk, MAX(tanggal_transaksi) AS max_tanggal "
                + "        FROM kartu_stok "
                + "        GROUP BY id_produk "
                + "    ) ks2 ON ks1.id_produk = ks2.id_produk AND ks1.tanggal_transaksi = ks2.max_tanggal "
                + ") ks ON p.id_produk = ks.id_produk "
                + "WHERE p.status = 'dijual' "
                + "GROUP BY p.jenis_produk";

        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            connection = db.conn.getConnection();
            stmt = connection.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                String jenisProduk = rs.getString("jenis_produk");
                int totalStok = rs.getInt("total_stok");

                String iconFile = switch (jenisProduk.toLowerCase()) {
                    case "sepatu" ->
                        "shoes-icon.png";
                    case "sandal" ->
                        "sandal-icon.png";
                    case "kaos kaki" ->
                        "kaoskaki.png";
                    default ->
                        "produk-lainnya-icon.png";
                };

                JPanel itemPanel = createProductRow(jenisProduk, String.valueOf(totalStok), iconFile, jenisProduk);
                itemsContainer.add(itemPanel);
                itemsContainer.add(Box.createVerticalStrut(10));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JLabel errorLabel = new JLabel("Gagal memuat data stok.");
            errorLabel.setForeground(Color.RED);
            itemsContainer.add(errorLabel);
        }

        stockPanel.add(itemsContainer, BorderLayout.CENTER);
        return stockPanel;
    }

    private JPanel createProductRow(String productName, String quantity, String iconPath, String jenisProduk) {
        // Main row panel with rounded border
        JPanel rowPanel = new JPanel();
        rowPanel.setLayout(new BorderLayout());
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, new Color(0, 0, 0), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Left panel for icon and product name
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
        leftPanel.setBackground(Color.WHITE);

        // Add product icon
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/SourceImage/" + iconPath));
            Image scaledImage = originalIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            JLabel iconLabel = new JLabel(new ImageIcon(scaledImage));
            iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
            leftPanel.add(iconLabel);
        } catch (Exception e) {
            JPanel iconPlaceholder = new JPanel();
            iconPlaceholder.setBackground(new Color(230, 230, 230));
            iconPlaceholder.setPreferredSize(new Dimension(24, 24));
            iconPlaceholder.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
            leftPanel.add(iconPlaceholder);
            System.err.println("Icon not found: " + iconPath);
        }

        // Product name label
        JLabel nameLabel = new JLabel(productName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        leftPanel.add(nameLabel);

        // Right panel for quantity
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.X_AXIS));
        rightPanel.setBackground(Color.WHITE);

        // Quantity field
        JLabel quantityField = new JLabel(quantity);
        quantityField.setFont(new Font("Arial", Font.PLAIN, 14));
        quantityField.setHorizontalAlignment(SwingConstants.CENTER);
        quantityField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        quantityField.setBackground(Color.WHITE);
        quantityField.setOpaque(true);

        // Menu button (ikon titik 3 sesuai jenis produk)
        JButton menuButton = new JButton();

        String titik3Icon = switch (jenisProduk.toLowerCase()) {
            case "sepatu" ->
                "titik 3 sepatu.png";
            case "sandal" ->
                "titik 3 sepatu.png";
            case "kaos kaki" ->
                "titik 3 sepatu.png";
            default ->
                "titik 3 sepatu.png";
        };

        try {
            BufferedImage iconImage = ImageIO.read(getClass().getResourceAsStream("/SourceImage/" + titik3Icon));
            Image scaledImage = iconImage.getScaledInstance(4, 17, Image.SCALE_SMOOTH);
            menuButton.setIcon(new ImageIcon(scaledImage));
        } catch (IOException e) {
            menuButton.setText("⋮");
            menuButton.setFont(new Font("Arial", Font.BOLD, 16));
            menuButton.setForeground(new Color(100, 100, 100));
            System.err.println("Titik tiga icon not found: " + titik3Icon);
            e.printStackTrace();
        }

        menuButton.setBorderPainted(false);
        menuButton.setContentAreaFilled(false);
        menuButton.setFocusPainted(false);
        menuButton.setPreferredSize(new Dimension(30, 30));
        menuButton.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        menuButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        menuButton.addActionListener(e -> {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(rowPanel);

            switch (jenisProduk.toLowerCase()) {
                case "sepatu" -> {
                    PopUp_DashboardOwnerTitik3Sepatu popup = new PopUp_DashboardOwnerTitik3Sepatu(parentFrame);
                    popup.setVisible(true);
                    System.out.println("Ini Titik Sepatu");
                }
                case "sandal" -> {
                    PopUp_DashboardOwnerTitik3Sandal popupSandal = new PopUp_DashboardOwnerTitik3Sandal(parentFrame);
                    popupSandal.setVisible(true);
                    System.out.println("Ini Titik Sandal");
                }
                case "kaos kaki" -> {
                    PopUp_DashboardOwnerTitik3KaosKaki popupKaosKaki = new PopUp_DashboardOwnerTitik3KaosKaki(parentFrame);
                    popupKaosKaki.setVisible(true);
                    System.out.println("Ini Titik kaos kaki");
                }
                default -> {
                    PopUp_DashboardOwnerTitik3Lainnya popupLainnya = new PopUp_DashboardOwnerTitik3Lainnya(parentFrame);
                    popupLainnya.setVisible(true);
                    System.out.println("Ini Titik Lainnya");
                }
            }
        });

        rightPanel.add(quantityField);
        rightPanel.add(Box.createHorizontalStrut(10));
        rightPanel.add(menuButton);

        // Add panels to main row
        rowPanel.add(leftPanel, BorderLayout.WEST);
        rowPanel.add(rightPanel, BorderLayout.EAST);
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        rowPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 60));

        return rowPanel;
    }

    private JPanel createStokPanel() {
        JPanel stokPanel = new JPanel();
        stokPanel.setLayout(new BorderLayout(0, 5));
        stokPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(50, new Color(220, 220, 220), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        stokPanel.setBackground(Color.white);

        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Daftar Stok Menipis");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel warningIcon = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/SourceImage/icon/warning_segitiga_merah.png"));
            Image img = icon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
            warningIcon.setIcon(new ImageIcon(img));
        } catch (Exception e) {
        }

        warningIcon.setBounds(210, 15, 20, 20);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(warningIcon);

        stokPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel itemsContainer = new JPanel();
        itemsContainer.setLayout(new BoxLayout(itemsContainer, BoxLayout.Y_AXIS));
        itemsContainer.setBackground(Color.WHITE);
        itemsContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        String sql = "SELECT p.jenis_produk, p.nama_produk, ks.produk_sisa "
                + "FROM (SELECT * FROM produk WHERE status = 'dijual') p "
                + "LEFT JOIN ( "
                + "    SELECT ks1.id_produk, ks1.produk_sisa "
                + "    FROM kartu_stok ks1 "
                + "    INNER JOIN ( "
                + "        SELECT id_produk, MAX(tanggal_transaksi) AS max_tanggal "
                + "        FROM kartu_stok "
                + "        GROUP BY id_produk "
                + "    ) ks2 ON ks1.id_produk = ks2.id_produk AND ks1.tanggal_transaksi = ks2.max_tanggal "
                + ") ks ON p.id_produk = ks.id_produk "
                + "WHERE ks.produk_sisa < 10 "
                + "ORDER BY ks.produk_sisa DESC "
                + "LIMIT 4";

        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            connection = db.conn.getConnection();
            stmt = connection.prepareStatement(sql);
            rs = stmt.executeQuery();

            int itemCount = 0;
            while (rs.next()) {
                String jenisProduk = rs.getString("jenis_produk");
                String namaProduk = rs.getString("nama_produk");
                int stokSisa = rs.getInt("produk_sisa");

                String iconFile = switch (jenisProduk.toLowerCase()) {
                    case "sepatu" ->
                        "shoes-icon.png";
                    case "sandal" ->
                        "sandal-icon.png";
                    case "kaos kaki" ->
                        "kaoskaki.png";
                    default ->
                        "produk-lainnya-icon.png";
                };

                JPanel itemPanel = createStokRow(namaProduk, String.valueOf(stokSisa), iconFile);
                itemsContainer.add(itemPanel);
                itemsContainer.add(Box.createVerticalStrut(9));
                itemCount++;
            }

            if (itemCount == 0) {
                itemsContainer.add(Box.createRigidArea(new Dimension(0, 100)));

                JLabel emptyLabel = new JLabel("Tidak Ada stok yang menipis!!");
                emptyLabel.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 16));
                emptyLabel.setForeground(Color.RED);
                emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                itemsContainer.add(emptyLabel);
            } else {
                int currentCount = itemCount * 2;
                int needed = 4 * 2 - currentCount;
                for (int i = 0; i < needed; i++) {
                    itemsContainer.add(Box.createVerticalStrut(9));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JLabel errorLabel = new JLabel("Gagal memuat data stok.");
            errorLabel.setForeground(Color.RED);
            itemsContainer.add(errorLabel);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        stokPanel.add(itemsContainer, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, -5, 0));

        JButton moreButton = new JButton("Telusuri Lainnya");
        moreButton.setFont(new Font("Arial", Font.BOLD, 12));
        moreButton.setBorderPainted(false);
        moreButton.setContentAreaFilled(false);
        moreButton.setFocusPainted(false);
        moreButton.setPreferredSize(new Dimension(150, 12));
        moreButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/SourceImage/arrow-right.png"));
            Image img = icon.getImage().getScaledInstance(12, 12, Image.SCALE_SMOOTH);
            moreButton.setIcon(new ImageIcon(img));
            moreButton.setIconTextGap(5);
            moreButton.setHorizontalTextPosition(SwingConstants.LEFT);
        } catch (Exception e) {
            moreButton.setText("Telusuri Lainnya >");
        }

        moreButton.addActionListener(e -> {
            System.out.println("Tombol Telusuri Lainnya diklik!");
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(parentComponent);
            PopUp_DashboardKasirTelusuriLainnya dialog = new PopUp_DashboardKasirTelusuriLainnya(parentFrame);
            dialog.setVisible(true);
        });

        footerPanel.add(moreButton);
        stokPanel.add(footerPanel, BorderLayout.SOUTH);

        return stokPanel;
    }

    private JPanel createStokRow(String namaBarang, String jumlah, String iconFile) {
        JPanel rowPanel = new JPanel();
        rowPanel.setLayout(new BorderLayout());
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, new Color(0, 0, 0), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setBackground(Color.WHITE);

        JLabel iconLabel = new JLabel();
        try {
            String iconPath = "/SourceImage/" + iconFile;
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            Image img = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            // fallback tanpa ikon
        }

        JLabel nameLabel = new JLabel(namaBarang);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        leftPanel.add(iconLabel);
        leftPanel.add(nameLabel);

        JTextField quantityField = new JTextField(jumlah);
        quantityField.setFont(new Font("Arial", Font.PLAIN, 14));
        quantityField.setEditable(false);
        quantityField.setHorizontalAlignment(SwingConstants.CENTER);
        quantityField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        quantityField.setFocusable(false);
        quantityField.setBackground(Color.WHITE);
        quantityField.setPreferredSize(new Dimension(50, 30));

        rowPanel.add(leftPanel, BorderLayout.WEST);
        rowPanel.add(quantityField, BorderLayout.EAST);

        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        rowPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 60));

        return rowPanel;
    }

    private void setNamaUser() {
        String email = LoginForm.getNamaUser();
        String norfid = LoginForm.getNoRFID();

        String sql = "SELECT nama_user FROM user WHERE email = ? OR norfid = ?";
        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, email);
            st.setString(2, norfid);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                namaUser = rs.getString("nama_user");
            } else {
                System.out.println("No karyawan found ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshDashboardKasir() {
        // Remove existing components
        removeAll();

        // Reinitialize components
        initComponents();

        // Force UI update
        revalidate();
        repaint();
    }

}
