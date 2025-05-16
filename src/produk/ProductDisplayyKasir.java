package produk;

import Form.FormKasir;
import PopUp_all.PopUp_aturdiskon;
import SourceCode.ScrollPane;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.HashMap;
import java.util.Map;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.DefaultListCellRenderer;
import javax.swing.border.Border;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.BasicPopupMenuUI;
import javax.swing.plaf.basic.ComboPopup;
import Form.Productt;

public class ProductDisplayyKasir extends javax.swing.JPanel {
    // Deklarasikan plusButton sebagai field class
    Component parentComponent = this; 
    private JButton sellButton;
    private Runnable trashButtonListener;
    private JButton trashButton;
    private JPanel activeTab;
    private JWindow tooltipWindow;
    private Timer fadeInTimer;
    private Timer fadeOutTimer;
    private Runnable sellButtonListener;
    private boolean isPressed = false;
    private JButton pressedButton = null;
    private Map<JButton, ImageIcon> normalIcons = new HashMap<>();
    private Map<JButton, ImageIcon> pressedIcons = new HashMap<>();
    private Map<JButton, String> buttonTooltips = new HashMap<>();
    private JLabel tooltipLabel = new JLabel();
    
    // Panel untuk masing-masing kategori
    private JPanel sepatuPanel;
    private JPanel sandalPanel;
    private JPanel kaosKakiPanel;
    private JPanel lainnyaPanel;
    
    // Current displayed panel
    private JPanel currentContentPanel;

    private JPanel createProductsGrid(String category) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 4, 20, 25));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Buat produk sesuai kategori
        for (int i = 0; i < 20; i++) {
            panel.add(createProductCard(category));
        }

        return panel;
    }

    private JPanel initializeMainPanel(String category) {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
        mainPanel.setBackground(Color.WHITE);

        // Tambahkan filter panel hanya jika bukan kategori "Lainnya"
        if (!"Lainnya".equals(category)) {
            mainPanel.add(createFilterPanel(category), BorderLayout.NORTH);
        }

        // Create products panel
        final JPanel productsPanel = createProductsGrid(category);

        // Create scroll pane
        ScrollPane scrollPane = new ScrollPane(productsPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);

        int preferredHeight = 250 * 2 + 40;
        scrollPane.setPreferredSize(new Dimension(0, preferredHeight));

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        return mainPanel;
    }

    public ProductDisplayyKasir() {
        initComponents();

        // Initialize category panels
        sepatuPanel = initializeMainPanel("Sepatu");
        sandalPanel = initializeMainPanel("Sandal");
        kaosKakiPanel = initializeMainPanel("Kaos Kaki");
        lainnyaPanel = initializeMainPanel("Lainnya");

        // Set preferred size
        setPreferredSize(new Dimension(1065, 640));
        setLayout(new BorderLayout(0, 15));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Add product category panel
        add(createCategoryPanel(), BorderLayout.NORTH);

        // Add initial content (Sepatu panel by default)
        currentContentPanel = sepatuPanel;
        add(currentContentPanel, BorderLayout.CENTER);

        // Add button panel on the right
        add(createActionButtons(), BorderLayout.EAST);
    }

    private JPanel createCategoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Panel tab kategori
        JPanel tabPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        tabPanel.setBackground(Color.WHITE);

        // Membuat tab Sepatu (tab aktif default)
        JPanel sepatuTab = createClickableTab("Sepatu", true);
        tabPanel.add(sepatuTab);
        activeTab = sepatuTab;

        // Membuat tab Sandal
        JPanel sandalTab = createClickableTab("Sandal", false);
        tabPanel.add(sandalTab);

        // Membuat tab Kaos Kaki
        JPanel kaosKakiTab = createClickableTab("Kaos Kaki", false);
        tabPanel.add(kaosKakiTab);

        // Membuat tab Lainnya
        JPanel lainnyaTab = createClickableTab("Lainnya", false);
        tabPanel.add(lainnyaTab);

        panel.add(tabPanel, BorderLayout.CENTER);
        panel.add(Box.createVerticalStrut(15), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createClickableTab(String tabName, boolean isActive) {
        JPanel tabContainer = new JPanel(new BorderLayout());
        tabContainer.setBackground(Color.WHITE);

        JButton tabButton = new JButton(tabName);
        tabButton.setFont(new Font("Arial", Font.BOLD, 14));
        tabButton.setForeground(Color.BLACK);
        tabButton.setBackground(Color.WHITE);
        tabButton.setBorderPainted(false);
        tabButton.setFocusPainted(false);
        tabButton.setContentAreaFilled(false);
        tabButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        tabContainer.add(tabButton, BorderLayout.CENTER);

        // Garis bawah untuk tab yang aktif
        if (isActive) {
            JPanel underline = new JPanel();
            underline.setPreferredSize(new Dimension(0, 2));
            underline.setBackground(Color.BLACK);
            tabContainer.add(underline, BorderLayout.SOUTH);
        }

        // Tambahkan action listener
        final JPanel currentTab = tabContainer;
        tabButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (activeTab != currentTab) {
                    // Hapus underline dari tab aktif sebelumnya
                    if (activeTab != null) {
                        activeTab.remove(activeTab.getComponent(1));
                        activeTab.revalidate();
                        activeTab.repaint();
                    }

                    // Tambahkan underline ke tab yang baru dipilih
                    JPanel underline = new JPanel();
                    underline.setPreferredSize(new Dimension(0, 3));
                    underline.setBackground(Color.BLACK);
                    currentTab.add(underline, BorderLayout.SOUTH);
                    currentTab.revalidate();
                    currentTab.repaint();

                    // Update tab yang aktif
                    activeTab = currentTab;

                    // Ganti panel konten berdasarkan tab yang dipilih
                    remove(currentContentPanel);
                    switch (tabName) {
                        case "Sepatu":
                            currentContentPanel = sepatuPanel;
                            break;
                        case "Sandal":
                            currentContentPanel = sandalPanel;
                            break;
                        case "Kaos Kaki":
                            currentContentPanel = kaosKakiPanel;
                            break;
                        case "Lainnya":
                            currentContentPanel = lainnyaPanel;
                            break;
                    }
                    add(currentContentPanel, BorderLayout.CENTER);
                    revalidate();
                    repaint();
                }
            }
        });

        return tabContainer;
    }

    private JPanel createFilterPanel(String category) {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));
        
        // Left side with Filter label and icon
        JPanel filterLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterLabelPanel.setBackground(Color.WHITE);
        
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/SourceImage/icon/filter.png"));
        Image resizedImg = originalIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        JLabel filterIcon = new JLabel(new ImageIcon(resizedImg));
        JLabel filterText = new JLabel("Filter");
        filterText.setFont(new Font("Arial", Font.BOLD, 14));
        
        Box verticalBox = Box.createVerticalBox();
        verticalBox.add(Box.createVerticalStrut(5));
        verticalBox.add(filterIcon);
        Box textBox = Box.createVerticalBox();
        textBox.add(Box.createVerticalStrut(5));
        textBox.add(filterText);
        filterLabelPanel.add(verticalBox);
        filterLabelPanel.add(textBox);
        
        // Panel dropdown dengan FlowLayout
        JPanel dropdownsPanel = new JPanel();
        dropdownsPanel.setLayout(new BoxLayout(dropdownsPanel, BoxLayout.X_AXIS));
        dropdownsPanel.setBackground(Color.WHITE);
        
        // Buat dropdown berdasarkan kategori
        String[] merkOptions = getMerkOptions(category);
        String[] ukuranOptions = getUkuranOptions(category);
        String[] hargaOptions = getHargaOptions(category);
        String[] styleOptions = getStyleOptions(category);
        String[] genderOptions = getGenderOptions(category);
        
        ComboboxCustomKhususProduk merkDropdown = new ComboboxCustomKhususProduk(merkOptions);
        merkDropdown.setPreferredSize(new Dimension(180, 35));
        merkDropdown.setMaximumSize(new Dimension(180, 35));
        
        ComboboxCustomKhususProduk ukuranDropdown = new ComboboxCustomKhususProduk(ukuranOptions);
        ukuranDropdown.setPreferredSize(new Dimension(180, 35));
        ukuranDropdown.setMaximumSize(new Dimension(180, 35));
        
        ComboboxCustomKhususProduk hargaDropdown = new ComboboxCustomKhususProduk(hargaOptions);
        hargaDropdown.setPreferredSize(new Dimension(180, 35));
        hargaDropdown.setMaximumSize(new Dimension(180, 35));
        
        ComboboxCustomKhususProduk styleDropdown = new ComboboxCustomKhususProduk(styleOptions);
        styleDropdown.setPreferredSize(new Dimension(180, 35));
        styleDropdown.setMaximumSize(new Dimension(180, 35));
        
        ComboboxCustomKhususProduk genderDropdown = new ComboboxCustomKhususProduk(genderOptions);
        genderDropdown.setPreferredSize(new Dimension(180, 35));
        genderDropdown.setMaximumSize(new Dimension(180, 35));
        
        dropdownsPanel.add(merkDropdown);
        dropdownsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        dropdownsPanel.add(ukuranDropdown);
        dropdownsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        dropdownsPanel.add(hargaDropdown);
        dropdownsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        dropdownsPanel.add(styleDropdown);
        dropdownsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        dropdownsPanel.add(genderDropdown);
        
        panel.add(filterLabelPanel, BorderLayout.WEST);
        panel.add(dropdownsPanel, BorderLayout.CENTER);
        
        return panel;
    }

    // Helper methods untuk mendapatkan opsi dropdown berdasarkan kategori
    private String[] getMerkOptions(String category) {
        switch (category) {
            case "Sepatu":
                return new String[]{"Merk", "Adidas", "Nike", "Puma", "Reebok"};
            case "Sandal":
                return new String[]{"Merk", "Swallow", "Bata", "Nevada", "Eiger"};
            case "Kaos Kaki":
                return new String[]{"Merk", "Peds", "Nike", "Adidas", "Uniqlo"};
            default:
                return new String[]{"Merk"};
        }
    }

    private String[] getUkuranOptions(String category) {
        switch (category) {
            case "Sepatu":
                return new String[]{"Ukuran", "< 25", "25 - 30", "30 - 35", "35 - 40", "> 40"};
            case "Sandal":
                return new String[]{"Ukuran", "S", "M", "L", "XL"};
            case "Kaos Kaki":
                return new String[]{"Ukuran", "S (36-38)", "M (39-41)", "L (42-44)", "XL (45-47)"};
            default:
                return new String[]{"Ukuran"};
        }
    }

    private String[] getHargaOptions(String category) {
        switch (category) {
            case "Sepatu":
                return new String[]{"Harga", "< 50.000", "50.000 - 150.000", "151.000 - 300.000", "301.000 - 450.000"};
            case "Sandal":
                return new String[]{"Harga", "< 30.000", "30.000 - 100.000", "101.000 - 200.000", "> 200.000"};
            case "Kaos Kaki":
                return new String[]{"Harga", "< 20.000", "20.000 - 50.000", "51.000 - 100.000", "> 100.000"};
            default:
                return new String[]{"Harga"};
        }
    }

    private String[] getStyleOptions(String category) {
        switch (category) {
            case "Sepatu":
                return new String[]{"Style", "Olahraga", "Formal", "Kasual", "Boots"};
            case "Sandal":
                return new String[]{"Style", "Pantai", "Gunung", "Santai", "Olahraga"};
            case "Kaos Kaki":
                return new String[]{"Style", "Pendek", "Panjang", "Olahraga", "Fashion"};
            default:
                return new String[]{"Style"};
        }
    }

    private String[] getGenderOptions(String category) {
        switch (category) {
            case "Sepatu":
            case "Sandal":
                return new String[]{"Gender", "Male", "Female", "Unisex"};
            case "Kaos Kaki":
                return new String[]{"Gender", "Male", "Female", "Kids"};
            default:
                return new String[]{"Gender"};
        }
    }

    private JPanel createProductCard(String category) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Panel gambar produk
        JPanel imageContainer = new JPanel(new BorderLayout());
        imageContainer.setBackground(Color.WHITE);
        imageContainer.setPreferredSize(new Dimension(230, 230));
        imageContainer.setMinimumSize(new Dimension(230, 230));
        imageContainer.setMaximumSize(new Dimension(230, 230));

        JPanel imagePanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(245, 245, 245));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2d.setColor(new Color(115, 115, 115));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 30, 30);
                g2d.dispose();
            }
        };
        imagePanel.setOpaque(false);
        imagePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        imagePanel.setPreferredSize(new Dimension(220, 220));
        addPanelClickListener(imagePanel);

        JPanel imageLabelContainer = new JPanel(new GridBagLayout());
        imageLabelContainer.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.insets = new Insets(0, -20, 0, 20);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Set different images based on category
        String imagePath = getImagePathForCategory(category);
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));
            if (icon.getIconWidth() == -1) {
                JLabel placeholderLabel = new JLabel(category.toUpperCase());
                placeholderLabel.setFont(new Font("Arial", Font.BOLD, 14));
                placeholderLabel.setHorizontalAlignment(JLabel.CENTER);
                placeholderLabel.setForeground(Color.DARK_GRAY);
                imageLabel.add(placeholderLabel);
            } else {
                Image img = icon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(img));
            }
        } catch (Exception e) {
            JLabel placeholderLabel = new JLabel(category.toUpperCase());
            placeholderLabel.setFont(new Font("Arial", Font.BOLD, 14));
            placeholderLabel.setHorizontalAlignment(JLabel.CENTER);
            placeholderLabel.setForeground(Color.DARK_GRAY);
            imageLabel.add(placeholderLabel);
        }

        imageLabelContainer.add(imageLabel, gbc);
        imagePanel.add(imageLabelContainer, BorderLayout.CENTER);
        imageContainer.add(imagePanel, BorderLayout.CENTER);

        // Tambahkan indikator slide (dots) di bawah gambar
        JPanel dotsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        dotsPanel.setBackground(Color.WHITE);
        imageContainer.add(dotsPanel, BorderLayout.SOUTH);
        card.add(imageContainer, BorderLayout.CENTER);

        // Panel informasi produk
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        infoPanel.setPreferredSize(new Dimension(230, 100));

        // Set different product info based on category
        String productName = getProductNameForCategory(category);
        String price = getPriceForCategory(category);
        String size = getSizeForCategory(category);
        String stock = getStockForCategory(category);

        JLabel nameLabel = new JLabel(productName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        addLabelClickListener(nameLabel);

        JLabel priceLabel = new JLabel(price);
        priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        priceLabel.setForeground(new Color(0, 102, 204));
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        addLabelClickListener(priceLabel);

        JLabel sizeLabel = new JLabel(size);
        sizeLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        sizeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        addLabelClickListener(sizeLabel);

        JPanel stockAddPanel = new JPanel(new BorderLayout(5, 0));
        stockAddPanel.setBackground(Color.WHITE);
        stockAddPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        stockAddPanel.setMaximumSize(new Dimension(230, 30));
        
        JLabel stockLabel = new JLabel(stock);
        stockLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        stockAddPanel.add(stockLabel, BorderLayout.WEST);
        addLabelClickListener(stockLabel);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(priceLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(sizeLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(stockAddPanel);

        card.setPreferredSize(new Dimension(230, 350));
        card.add(infoPanel, BorderLayout.SOUTH);

        return card;
    }
    
    private String getImagePathForCategory(String category) {
        switch (category) {
            case "Sepatu":
                return "/SourceImage/gambar_sepatu.png";
            case "Sandal":
                return "/SourceImage/gambar_sandal.png";
            case "Kaos Kaki":
                return "/SourceImage/gambar_kaoskaki.png";
            default:
                return "/SourceImage/gambar_default.png";
        }
    }
    
    private String getProductNameForCategory(String category) {
        switch (category) {
            case "Sepatu":
                return "Adidas Simanjutak";
            case "Sandal":
                return "Swallow Sandal";
            case "Kaos Kaki":
                return "Peds Kaos Kaki";
            default:
                return "Produk Lainnya";
        }
    }
    
    private String getPriceForCategory(String category) {
        switch (category) {
            case "Sepatu":
                return "Rp. 250.000,00";
            case "Sandal":
                return "Rp. 120.000,00";
            case "Kaos Kaki":
                return "Rp. 45.000,00";
            default:
                return "Rp. 100.000,00";
        }
    }
    
    private String getSizeForCategory(String category) {
        switch (category) {
            case "Sepatu":
                return "Uk : 37";
            case "Sandal":
                return "Uk : M";
            case "Kaos Kaki":
                return "Uk : L (42-44)";
            default:
                return "Uk : -";
        }
    }
    
    private String getStockForCategory(String category) {
        switch (category) {
            case "Sepatu":
                return "Stok : 10";
            case "Sandal":
                return "Stok : 15";
            case "Kaos Kaki":
                return "Stok : 20";
            default:
                return "Stok : 5";
        }
    }
    
        // Buat method untuk menambahkan MouseListener ke label
private void addLabelClickListener(JLabel label) {
    label.setCursor(new Cursor(Cursor.HAND_CURSOR)); // ubah kursor jadi tangan biar lebih UX-friendly
    label.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            FormKasir.getMainFrame().switchToTransJualPanel();
        }
    });
}

private void addPanelClickListener(JPanel panel) {
    panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
    panel.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            FormKasir.getMainFrame().switchToTransJualPanel();
        }
    });
}

 private JPanel createActionButtons() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBackground(Color.WHITE);
    panel.setBorder(new EmptyBorder(450, 5, 10, 0)); // Menambahkan padding atas agar tombol bergeser ke bawah
    
    // Tambahkan padding atas tambahan jika diperlukan
    panel.add(Box.createVerticalStrut(50));
    
    // Tombol Fullscreen/Scan
    JButton fullscreenButton = createRoundedIconButton("scan.png", new Color(20, 20, 20), "Scan Cari Produk");
    fullscreenButton.addActionListener(e -> {
        animateHideTooltip();
        // Dapatkan parent frame dari tombol
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(fullscreenButton);
        if (parentFrame != null) { // Cek apakah berhasil mendapatkan parentFrame
            ScanBarcodeDialogKasir dialog = new ScanBarcodeDialogKasir(parentFrame);
            dialog.setVisible(true);
        } else {
            System.out.println("Gagal mendapatkan parent frame.");
        }
    });
    panel.add(fullscreenButton);
    panel.add(Box.createVerticalStrut(15));
  
    // Tombol sell (Jual Produk)
    sellButton = createRoundedIconButton("keranjang.png", new Color(20, 20, 20), "Transaksi Jual");
    sellButton.addActionListener(e -> {
        // Sembunyikan tooltip
        animateHideTooltip();
        // Panggil callback untuk mengganti panel
        if (sellButtonListener != null) {
            sellButtonListener.run();
        }
    });
    panel.add(sellButton);
    
    // Set listener untuk tombol sell
    // Ini seharusnya dipanggil di constructor, bukan di createActionButtons
    this.sellButtonListener = () -> {
        // Cari instance Productt menggunakan SwingUtilities
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (parentFrame != null) {
            // Cara yang lebih aman untuk memeriksa tipe parentFrame
            try {
                // Menggunakan reflection untuk memanggil metode pada kelas Productt
                Class<?> producttClass = Class.forName("Form.FormKasir");
                if (producttClass.isInstance(parentFrame)) {
                    // Memanggil metode switchToTransJualPanel melalui reflection
                    java.lang.reflect.Method method = producttClass.getMethod("switchToTransJualPanel");
                    method.invoke(parentFrame);
                } else {
                    // Jika tidak berhasil, coba gunakan singleton method 
                    java.lang.reflect.Method getMainFrameMethod = producttClass.getMethod("getMainFrame");
                    Object mainFrame = getMainFrameMethod.invoke(null);
                    java.lang.reflect.Method switchMethod = producttClass.getMethod("switchToTransJualPanel");
                    switchMethod.invoke(mainFrame);
                }
            } catch (Exception ex) {
                System.out.println("Error saat mencoba mengganti panel: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    };

    return panel;
}

// Method untuk menetapkan listener tombol sampah
public void setTrashButtonListener(Runnable listener) {
    this.trashButtonListener = listener;
}
// Method untuk menetapkan listener tombol plus
    public void sellButtonListener(Runnable listener) {
        this.sellButtonListener = listener;
    }
// Animator untuk menghilangkan tooltip dengan efek fade-out
    private void animateHideTooltip() {
        if (tooltipWindow != null && tooltipWindow.isVisible()) {
            fadeInTimer.stop();
            fadeOutTimer.start();
        }
    }

    private JButton createRoundedIconButton(String iconName, Color bgColor, String tooltipText) {
        // Ukuran tombol ditingkatkan
        final int buttonSize = 50; // Sebelumnya 40
        final int iconSize = 30; // Sebelumnya 24
        final int pressedIconSize = 26; // Ukuran ikon saat ditekan (lebih kecil)

        // Warna asli dan warna saat ditekan
        final Color originalColor = bgColor;
        final Color pressedColor = new Color(
                Math.max(0, bgColor.getRed() - 30),
                Math.max(0, bgColor.getGreen() - 30),
                Math.max(0, bgColor.getBlue() - 30)
        );

        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isOpaque()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // Menggunakan warna yang berbeda berdasarkan status tombol
                    if (getModel().isPressed() || (pressedButton == this && isPressed)) {
                        g2.setColor(pressedColor); // Warna saat ditekan
                        // Efek bayangan dalam saat ditekan
                        g2.setColor(pressedColor.darker());
                        g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 15, 15);
                        g2.setColor(pressedColor);
                        g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 15, 15);
                    } else {
                        g2.setColor(originalColor); // Warna asli
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                        // Efek highlight saat tidak ditekan
                        g2.setColor(originalColor.brighter());
                        g2.setStroke(new BasicStroke(2));
                        g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 30, 30);
                    }

                    g2.dispose();
                }
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                // Tidak menggambar border
            }
        };

        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setPreferredSize(new Dimension(buttonSize, buttonSize)); // Ukuran tombol lebih besar
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/SourceImage/icon/" + iconName));
            if (icon.getIconWidth() != -1) {
                // Buat dua versi ikon: normal dan saat ditekan (lebih kecil)
                Image normalImg = icon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
                Image pressedImg = icon.getImage().getScaledInstance(pressedIconSize, pressedIconSize, Image.SCALE_SMOOTH);

                ImageIcon normalIcon = new ImageIcon(normalImg);
                ImageIcon pressedIcon = new ImageIcon(pressedImg);

                // Simpan kedua ikon untuk digunakan kemudian
                normalIcons.put(button, normalIcon);
                pressedIcons.put(button, pressedIcon);

                // Gunakan ikon normal sebagai default
                button.setIcon(normalIcon);
            } else {
                button.setText(iconName.substring(0, 1).toUpperCase());
                button.setForeground(Color.WHITE);
                button.setFont(new Font("Poppins", Font.BOLD, 18)); // Ukuran font lebih besar jika menggunakan teks
            }
        } catch (Exception e) {
            button.setText(iconName.substring(0, 1).toUpperCase());
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Poppins", Font.BOLD, 18)); // Ukuran font lebih besar jika menggunakan teks
        }

        // Simpan teks tooltip untuk tombol ini
        buttonTooltips.put(button, tooltipText);

        // Buat tooltip window jika belum ada
        if (tooltipWindow == null) {
            tooltipWindow = new JWindow();
            tooltipWindow.setLayout(new BorderLayout());
            tooltipWindow.setBackground(new Color(0, 0, 0, 0)); // Transparan

            JPanel tooltipPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(0, 0, 0)); // Transparansi 200
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                    g2.dispose();
                }
            };
            tooltipPanel.setOpaque(false);
            tooltipPanel.setLayout(new BorderLayout());
            tooltipPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            tooltipLabel.setForeground(Color.WHITE);
            tooltipLabel.setFont(new Font("Poppins", Font.BOLD, 12)); // Font tooltip dibuat lebih besar
            tooltipLabel.setHorizontalAlignment(SwingConstants.CENTER);

            tooltipPanel.add(tooltipLabel, BorderLayout.CENTER);
            tooltipWindow.add(tooltipPanel);
            tooltipWindow.setAlwaysOnTop(true);

            // Inisialisasi timer jika belum ada
            if (fadeInTimer == null) {
                fadeInTimer = new Timer(10, e -> {
                    float opacity = tooltipWindow.getOpacity() + 0.05f;
                    if (opacity >= 1.0f) {
                        tooltipWindow.setOpacity(1.0f);
                        fadeInTimer.stop();
                    } else {
                        tooltipWindow.setOpacity(opacity);
                    }
                });
            }

            if (fadeOutTimer == null) {
                fadeOutTimer = new Timer(10, e -> {
                    float opacity = tooltipWindow.getOpacity() - 0.05f;
                    if (opacity <= 0.0f) {
                        tooltipWindow.setOpacity(0.0f);
                        tooltipWindow.setVisible(false);
                        fadeOutTimer.stop();
                    } else {
                        tooltipWindow.setOpacity(opacity);
                    }
                });
            }
        }

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Atur teks tooltip sesuai dengan tombol yang di-hover
                tooltipLabel.setText(buttonTooltips.get(button));

                // Perbarui ukuran tooltip berdasarkan teks yang baru
                tooltipWindow.pack();
                tooltipWindow.setSize(tooltipLabel.getPreferredSize().width + 20, tooltipLabel.getPreferredSize().height + 10);

                Point location = button.getLocationOnScreen();
                tooltipWindow.setLocation(location.x - tooltipWindow.getWidth() - 10, location.y + button.getHeight() / 2 - tooltipWindow.getHeight() / 2);
                tooltipWindow.setVisible(true);
                tooltipWindow.setOpacity(0.0f);
                fadeOutTimer.stop();
                fadeInTimer.start();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                fadeInTimer.stop();
                fadeOutTimer.start();
            }

            // Tambahkan efek saat tombol ditekan
            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                pressedButton = button;

                // Ganti ikon dengan versi yang lebih kecil saat ditekan
                if (pressedIcons.containsKey(button)) {
                    button.setIcon(pressedIcons.get(button));
                }

                button.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                pressedButton = null;

                // Kembalikan ke ikon normal
                if (normalIcons.containsKey(button)) {
                    button.setIcon(normalIcons.get(button));
                }

                button.repaint();
            }
        });

        return button;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>                        

    // Variables declaration - do not modify                     
    // End of variables declaration                   
}
