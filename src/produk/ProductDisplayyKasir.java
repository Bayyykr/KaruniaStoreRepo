package produk;

import Form.FormKasir;
import PopUp_all.*;
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
import java.util.List;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.DefaultListCellRenderer;
import javax.swing.border.Border;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.BasicPopupMenuUI;
import javax.swing.plaf.basic.ComboPopup;
import Form.Productt;
import java.sql.*;
import db.conn;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ProductDisplayyKasir extends javax.swing.JPanel {

    // Deklarasikan plusButton sebagai field class
    Component parentComponent = this;
    private JButton sellButton;
    private Runnable trashButtonListener;
    private JButton trashButton; // Add this as a field to match the plusButton approach
    private JPanel activeTab;
    private JWindow tooltipWindow;
    private Timer fadeInTimer;
    private Timer fadeOutTimer;
    private Runnable sellButtonListener;
//    private Runnable addLabelClickListener;
    private Runnable sellPanelClickListener;
    private String selectedProductId;
    private boolean isPressed = false;
    private JButton pressedButton = null;
    private Map<JButton, ImageIcon> normalIcons = new HashMap<>();
    private Map<JButton, ImageIcon> pressedIcons = new HashMap<>();
    // Map untuk menyimpan teks tooltip untuk setiap tombol
    private Map<JButton, String> buttonTooltips = new HashMap<>();
    // Deklarasi label tooltip
    private JLabel tooltipLabel = new JLabel();

    // Panel untuk masing-masing kategori produk
    private JPanel sepatuPanel, sandalPanel, kaosKakiPanel, lainnyaPanel;
    // Panel scroll untuk setiap kategori
    private ScrollPane sepatuScrollPane, sandalScrollPane, kaosKakiScrollPane, lainnyaScrollPane;
    // Panel utama untuk konten
    private JPanel mainContentPanel;

    private Connection con;

    public ProductDisplayyKasir() {
        // Tetapkan ukuran preferensi yang tetap
        setPreferredSize(new Dimension(1065, 640));
        setLayout(new BorderLayout(0, 15));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        con = conn.getConnection();

        // Inisialisasi panel untuk setiap kategori
        sepatuPanel = new JPanel(new BorderLayout());
        sandalPanel = new JPanel(new BorderLayout());
        kaosKakiPanel = new JPanel(new BorderLayout());
        lainnyaPanel = new JPanel(new BorderLayout());

        // Inisialisasi panel utama untuk konten
        mainContentPanel = new JPanel(new CardLayout());
        mainContentPanel.setBackground(Color.WHITE);

        // Add product category panel
        add(createCategoryPanel(), BorderLayout.NORTH);

        // Inisialisasi semua panel kategori
        initializeCategoryPanels();

        // Tambahkan panel konten utama ke frame
        add(mainContentPanel, BorderLayout.CENTER);

        // Add button panel on the right
        add(createActionButtons(), BorderLayout.EAST);
    }

    private void initializeCategoryPanels() {
        // Inisialisasi panel untuk Sepatu (termasuk filter)
        sepatuPanel = new JPanel(new BorderLayout());
        sepatuPanel.add(createFilterPanel("Sepatu"), BorderLayout.NORTH);
        JPanel sepatuProductsGrid = createProductsGrid("Sepatu");
        sepatuScrollPane = createScrollPaneForProducts(sepatuProductsGrid);
        sepatuPanel.add(sepatuScrollPane, BorderLayout.CENTER);

        // Inisialisasi panel untuk Sandal (termasuk filter)
        sandalPanel = new JPanel(new BorderLayout());
        sandalPanel.add(createFilterPanel("Sandal"), BorderLayout.NORTH);
        JPanel sandalProductsGrid = createProductsGrid("Sandal");
        sandalScrollPane = createScrollPaneForProducts(sandalProductsGrid);
        sandalPanel.add(sandalScrollPane, BorderLayout.CENTER);

        // Inisialisasi panel untuk Kaos Kaki (termasuk filter)
        kaosKakiPanel = new JPanel(new BorderLayout());
        kaosKakiPanel.add(createFilterPanel("Kaos Kaki"), BorderLayout.NORTH);
        JPanel kaosKakiProductsGrid = createProductsGrid("Kaos Kaki");
        kaosKakiScrollPane = createScrollPaneForProducts(kaosKakiProductsGrid);
        kaosKakiPanel.add(kaosKakiScrollPane, BorderLayout.CENTER);

        // Inisialisasi panel untuk Lainnya (tanpa filter)
        lainnyaPanel = new JPanel(new BorderLayout());
        JPanel lainnyaProductsGrid = createProductsGrid("Lainnya");
        lainnyaScrollPane = createScrollPaneForProducts(lainnyaProductsGrid);
        lainnyaPanel.add(lainnyaScrollPane, BorderLayout.CENTER);

        // Tambahkan semua panel kategori ke panel konten utama dengan CardLayout
        mainContentPanel.add(sepatuPanel, "Sepatu");
        mainContentPanel.add(sandalPanel, "Sandal");
        mainContentPanel.add(kaosKakiPanel, "Kaos Kaki");
        mainContentPanel.add(lainnyaPanel, "Lainnya");
    }

    private ScrollPane createScrollPaneForProducts(JPanel productsPanel) {
        ScrollPane scrollPane = new ScrollPane(productsPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Hilangkan border dan atur viewport border menjadi null
        scrollPane.setBorder(null);
        scrollPane.setViewportBorder(null);

        // Atur viewport background ke putih
        scrollPane.getViewport().setBackground(Color.WHITE);

        return scrollPane;
    }

    private JPanel createProductsGrid(String category) {
        // Gunakan JPanel dengan FlowLayout sebagai wadah utama
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 25)); // Left aligned with spacing
        panel.setBackground(Color.white);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        try {
            // Create a list to store products with their stock info
            List<Map<String, Object>> productsWithStock = new ArrayList<>();
            // First query to get all products of the specified category
            String productSql = "SELECT id_produk, nama_produk, harga_jual, size, gambar, merk, jenis_produk, gender "
                    + "FROM produk WHERE status = 'dijual'";

            // Filter by category
            if (category.equals("Sepatu")) {
                productSql += " AND jenis_produk = 'Sepatu'";
            } else if (category.equals("Sandal")) {
                productSql += " AND jenis_produk = 'Sandal'";
            } else if (category.equals("Kaos Kaki")) {
                productSql += " AND jenis_produk = 'Kaos Kaki'";
            } else if (category.equals("Lainnya")) {
                productSql += " AND jenis_produk NOT IN ('Sepatu', 'Sandal', 'Kaos Kaki')";
            }

            try (PreparedStatement st = con.prepareStatement(productSql)) {
                ResultSet rs = st.executeQuery();
                while (rs.next()) {
                    String productId = rs.getString("id_produk");
                    int stock = 0;
                    // Get the latest stock for each product
                    String stokSql = "SELECT produk_sisa FROM kartu_stok WHERE id_produk = ? ORDER BY tanggal_transaksi DESC LIMIT 1";
                    try (PreparedStatement stockStmt = con.prepareStatement(stokSql)) {
                        stockStmt.setString(1, productId);
                        ResultSet stockRs = stockStmt.executeQuery();
                        if (stockRs.next()) {
                            stock = stockRs.getInt("produk_sisa");
                        }
                    }
                    // Store product data and stock in a Map
                    Map<String, Object> productData = new HashMap<>();
                    productData.put("id_produk", productId);
                    productData.put("nama_produk", rs.getString("nama_produk"));
                    productData.put("harga_jual", rs.getDouble("harga_jual"));
                    productData.put("size", rs.getInt("size"));
                    productData.put("stock", stock);
                    // Properly handle BLOB image data
                    byte[] imageData = null;
                    Blob blob = rs.getBlob("gambar");
                    if (blob != null) {
                        imageData = blob.getBytes(1, (int) blob.length());
                    }
                    productData.put("gambar", imageData);
                    productData.put("merk", rs.getString("merk"));
                    productData.put("jenis_produk", rs.getString("jenis_produk"));
                    productData.put("gender", rs.getString("gender"));
                    productsWithStock.add(productData);
                }
            }

            // Sort the products by stock (ascending)
            productsWithStock.sort((p1, p2)
                    -> Integer.compare((Integer) p1.get("stock"), (Integer) p2.get("stock")));

            // Buat panel grid dengan ukuran tetap menggunakan JPanel yang berisi 4 kolom
            JPanel gridPanel = new JPanel(new GridLayout(0, 4, 20, 25));
            gridPanel.setBackground(Color.WHITE);

            // Set ukuran tetap untuk grid panel
            // Asumsi lebar: (230px card * 4) + (20px gap * 3) + 20px padding kiri kanan = 980px
            // Biarkan tinggi ditentukan oleh isinya
            Dimension gridSize = new Dimension(980, 0);
            gridPanel.setPreferredSize(gridSize);
            gridPanel.setMinimumSize(gridSize);

            // Add sorted products to the grid panel
            for (Map<String, Object> product : productsWithStock) {
                JPanel productCard = createProductCard(
                        (String) product.get("id_produk"),
                        (String) product.get("nama_produk"),
                        (Double) product.get("harga_jual"),
                        (Integer) product.get("size"),
                        (Integer) product.get("stock"),
                        (byte[]) product.get("gambar"),
                        (String) product.get("merk"),
                        (String) product.get("jenis_produk"),
                        (String) product.get("gender")
                );
                gridPanel.add(productCard);
            }

            // Tambahkan panel kosong jika jumlah produk kurang dari 4
            int productsCount = productsWithStock.size();
            int remainder = productsCount % 4;
            if (remainder > 0) {
                int emptyPanelsNeeded = 4 - remainder;
                for (int i = 0; i < emptyPanelsNeeded; i++) {
                    JPanel emptyPanel = new JPanel();
                    emptyPanel.setPreferredSize(new Dimension(230, 350));
                    emptyPanel.setMinimumSize(new Dimension(230, 350));
                    emptyPanel.setMaximumSize(new Dimension(230, 350));
                    emptyPanel.setBackground(Color.WHITE);
                    gridPanel.add(emptyPanel);
                }
            }

            // Hitung jumlah baris yang dibutuhkan
            int rowCount = (int) Math.ceil(productsCount / 4.0);
            // Set tinggi tetap untuk grid panel: (350px tinggi card * rowCount) + (25px gap * (rowCount-1)) + padding
            int gridHeight = (rowCount * 350) + ((rowCount - 1) * 25) + 5;
            gridPanel.setPreferredSize(new Dimension(950, gridHeight));

            // Tambahkan grid panel ke panel utama
            panel.add(gridPanel);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error connecting to database: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return panel;
    }

    private JComboBox<String> createCustomComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(Color.BLACK);
        comboBox.setPreferredSize(new Dimension(150, 40));
        comboBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        // Set custom renderer untuk mengatur penampilan item
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(new EmptyBorder(5, 10, 5, 10));
                return this;
            }
        });

        // Tambahkan popup listener untuk mengatur styling dropdown
        comboBox.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                // Custom logic if needed when popup becomes visible
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                // Custom logic if needed when popup becomes invisible
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                // Custom logic if needed when popup is canceled
            }
        });

        return comboBox;
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

                    // Panggil method switchCategory untuk berpindah kategori
                    switchCategory(tabName);
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

        // Cara lebih simpel untuk memuat gambar
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/SourceImage/icon/filter.png"));
        Image resizedImg = originalIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        JLabel filterIcon = new JLabel(new ImageIcon(resizedImg));
        JLabel filterText = new JLabel("Filter");
        filterText.setFont(new Font("Arial", Font.BOLD, 14));

        // Box layout yang lebih simpel
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

        // Ambil data filter dari database untuk dropdown
        List<String> merkList = getMerkListFromDatabase(category);
        List<String> ukuranList = getSizeListFromDatabase();
        List<String> hargaList = getPriceRangesFromDatabase();
        List<String> styleList = getStyleListFromDatabase(category);
        List<String> genderList = getGenderListFromDatabase();

        // Konversi list ke array untuk dropdown
        String[] merkOptions = new String[merkList.size() + 1];
        merkOptions[0] = "Merk";
        for (int i = 0; i < merkList.size(); i++) {
            merkOptions[i + 1] = merkList.get(i);
        }

        String[] ukuranOptions = new String[ukuranList.size() + 1];
        ukuranOptions[0] = "Ukuran";
        for (int i = 0; i < ukuranList.size(); i++) {
            ukuranOptions[i + 1] = ukuranList.get(i);
        }

        String[] hargaOptions = new String[hargaList.size() + 1];
        hargaOptions[0] = "Harga";
        for (int i = 0; i < hargaList.size(); i++) {
            hargaOptions[i + 1] = hargaList.get(i);
        }

        String[] styleOptions = new String[styleList.size() + 1];
        styleOptions[0] = "Style";
        for (int i = 0; i < styleList.size(); i++) {
            styleOptions[i + 1] = styleList.get(i);
        }

        String[] genderOptions = new String[genderList.size() + 1];
        genderOptions[0] = "Gender";
        for (int i = 0; i < genderList.size(); i++) {
            genderOptions[i + 1] = genderList.get(i);
        }

        // Buat ComboboxCustomKhususProduk untuk setiap dropdown
        final ComboboxCustomKhususProduk merkDropdown = new ComboboxCustomKhususProduk(merkOptions);
        merkDropdown.setPreferredSize(new Dimension(180, 35));
        merkDropdown.setMaximumSize(new Dimension(180, 35));

        final ComboboxCustomKhususProduk ukuranDropdown = new ComboboxCustomKhususProduk(ukuranOptions);
        ukuranDropdown.setPreferredSize(new Dimension(180, 35));
        ukuranDropdown.setMaximumSize(new Dimension(180, 35));

        final ComboboxCustomKhususProduk hargaDropdown = new ComboboxCustomKhususProduk(hargaOptions);
        hargaDropdown.setPreferredSize(new Dimension(180, 35));
        hargaDropdown.setMaximumSize(new Dimension(180, 35));

        final ComboboxCustomKhususProduk styleDropdown = new ComboboxCustomKhususProduk(styleOptions);
        styleDropdown.setPreferredSize(new Dimension(180, 35));
        styleDropdown.setMaximumSize(new Dimension(180, 35));

        final ComboboxCustomKhususProduk genderDropdown = new ComboboxCustomKhususProduk(genderOptions);
        genderDropdown.setPreferredSize(new Dimension(180, 35));
        genderDropdown.setMaximumSize(new Dimension(180, 35));

        // Tambahkan listener untuk filter dropdown
        merkDropdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String merkp = merkDropdown.getSelectedItem().toString();
                if (!merkp.isEmpty()) {
                    applyFilters(merkDropdown, ukuranDropdown, hargaDropdown, styleDropdown, genderDropdown);
                }
            }
        });

        ukuranDropdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ukuranp = ukuranDropdown.getSelectedItem().toString();
                if (!ukuranp.isEmpty()) {
                    applyFilters(merkDropdown, ukuranDropdown, hargaDropdown, styleDropdown, genderDropdown);
                }
            }
        });

        hargaDropdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String hargap = hargaDropdown.getSelectedItem().toString();
                if (!hargap.isEmpty()) {
                    applyFilters(merkDropdown, ukuranDropdown, hargaDropdown, styleDropdown, genderDropdown);
                }
            }
        });

        styleDropdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String stylep = styleDropdown.getSelectedItem().toString();
                if (!stylep.isEmpty()) {
                    applyFilters(merkDropdown, ukuranDropdown, hargaDropdown, styleDropdown, genderDropdown);
                }
            }
        });

        genderDropdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String genderp = genderDropdown.getSelectedItem().toString();
                if (!genderp.isEmpty()) {
                    applyFilters(merkDropdown, ukuranDropdown, hargaDropdown, styleDropdown, genderDropdown);
                }
            }
        });

        // Tambahkan dengan rigid area di antara komponen
        dropdownsPanel.add(merkDropdown);
        dropdownsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        dropdownsPanel.add(ukuranDropdown);
        dropdownsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        dropdownsPanel.add(hargaDropdown);
        dropdownsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        dropdownsPanel.add(styleDropdown);
        dropdownsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        dropdownsPanel.add(genderDropdown);

        // Tambahkan panel label dan dropdown ke panel utama
        panel.add(filterLabelPanel, BorderLayout.WEST);
        panel.add(dropdownsPanel, BorderLayout.CENTER);
        return panel;
    }

    private List<String> getMerkListFromDatabase(String category) {
        List<String> merkList = new ArrayList<>();

        try {
            String query = "SELECT DISTINCT merk FROM produk WHERE status = 'dijual'";

            // Filter by category
            if (category.equals("Sepatu")) {
                query += " AND jenis_produk = 'sepatu'";
            } else if (category.equals("Sandal")) {
                query += " AND jenis_produk = 'sandal'";
            } else if (category.equals("Kaos Kaki")) {
                query += " AND jenis_produk = 'kaos kaki'";
            } else if (category.equals("Lainnya")) {
                query += " AND jenis_produk NOT IN ('sepatu', 'sndal', 'kaos kaki')";
            }

            query += " ORDER BY merk";

            try (PreparedStatement stmt = con.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String merk = rs.getString("merk");
                    if (merk != null && !merk.trim().isEmpty()) {
                        merkList.add(merk);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saat mengambil data merk: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        return merkList;
    }

// Metode untuk mengambil data ukuran dari database
    private List<String> getSizeListFromDatabase() {
        List<String> sizeList = new ArrayList<>();

        // Buat kategori ukuran
        sizeList.add("< 25");
        sizeList.add("25 - 30");
        sizeList.add("30 - 35");
        sizeList.add("35 - 40");
        sizeList.add("> 40");

        return sizeList;
    }

// Metode untuk mengambil range harga dari database
    private List<String> getPriceRangesFromDatabase() {
        List<String> priceRanges = new ArrayList<>();

        // Tetapkan range harga yang sesuai
        priceRanges.add("< 50.000");
        priceRanges.add("50.000 - 150.000");
        priceRanges.add("151.000 - 300.000");
        priceRanges.add("301.000 - 450.000");
        priceRanges.add("> 450.000");

        return priceRanges;
    }

    private List<String> getStyleListFromDatabase(String category) {
        List<String> styleList = new ArrayList<>();

        // Determine which styles to show based on category
        String[] styleOptions;
        String[] styleIds;

        switch (category.toLowerCase()) {
            case "sepatu":
                styleOptions = new String[]{"Running", "Casual", "Formal", "Sport"};
                styleIds = new String[]{"00001", "00002", "00003", "00004"};
                break;
            case "sandal":
                styleOptions = new String[]{"Casual", "Formal", "Outdoor", "Pantai"};
                styleIds = new String[]{"00002", "00003", "00005", "00006"};
                break;
            case "kaos kaki":
                styleOptions = new String[]{"Olahraga", "Formal", "Casual", "Ankle"};
                styleIds = new String[]{"00007", "00003", "00002", "00008"};
                break;
            case "lainnya":
                styleOptions = new String[]{"Vintage", "Modern", "Klasik", "Trendy"};
                styleIds = new String[]{"00009", "00010", "00011", "00012"};
                break;
            default:
                styleOptions = new String[]{""};
                styleIds = new String[]{""};
                break;
        }

        try {
            // Query to get style names for the specified IDs
            String query = "SELECT nama_style FROM style WHERE id_style IN (";
            for (int i = 0; i < styleIds.length; i++) {
                if (i > 0) {
                    query += ",";
                }
                query += "?";
            }
            query += ") ORDER BY FIELD(id_style, ";
            for (int i = 0; i < styleIds.length; i++) {
                if (i > 0) {
                    query += ",";
                }
                query += "?";
            }
            query += ")";

            try (PreparedStatement stmt = con.prepareStatement(query)) {
                // Set parameters for both IN and FIELD clauses
                int paramIndex = 1;
                for (String id : styleIds) {
                    stmt.setString(paramIndex++, id);
                }
                for (String id : styleIds) {
                    stmt.setString(paramIndex++, id);
                }

                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String style = rs.getString("nama_style");
                    if (style != null && !style.trim().isEmpty()) {
                        styleList.add(style);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saat mengambil data style: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        return styleList;
    }

    private List<String> getGenderListFromDatabase() {
        List<String> genderList = new ArrayList<>();

        // Instead of querying from database, we'll use predefined values
        genderList.add("Cowok");
        genderList.add("Cewek");
        genderList.add("Unisex");

        return genderList;
    }

    private void applyFilters(ComboboxCustomKhususProduk merkDropdown, ComboboxCustomKhususProduk ukuranDropdown,
            ComboboxCustomKhususProduk hargaDropdown, ComboboxCustomKhususProduk styleDropdown,
            ComboboxCustomKhususProduk genderDropdown) {

        String selectedMerk = merkDropdown.getSelectedIndex() > 0 ? (String) merkDropdown.getSelectedItem() : null;
        String selectedUkuran = ukuranDropdown.getSelectedIndex() > 0 ? (String) ukuranDropdown.getSelectedItem() : null;
        String selectedHarga = hargaDropdown.getSelectedIndex() > 0 ? (String) hargaDropdown.getSelectedItem() : null;
        String selectedStyle = styleDropdown.getSelectedIndex() > 0 ? (String) styleDropdown.getSelectedItem() : null;
        String selectedGender = genderDropdown.getSelectedIndex() > 0 ? (String) genderDropdown.getSelectedItem() : null;

        // Dapatkan panel aktif saat ini
        JPanel activePanel;
        String activeCategory = "";

        if (activeTab.getComponent(0) instanceof JButton) {
            activeCategory = ((JButton) activeTab.getComponent(0)).getText();
        }

        switch (activeCategory) {
            case "Sepatu":
                activePanel = sepatuPanel;
                break;
            case "Sandal":
                activePanel = sandalPanel;
                break;
            case "Kaos Kaki":
                activePanel = kaosKakiPanel;
                break;
            case "Lainnya":
                activePanel = lainnyaPanel;
                break;
            default:
                activePanel = sepatuPanel;
        }

        // Panggil metode untuk memuat produk dengan filter
        loadFilteredProducts(activePanel, activeCategory, selectedMerk, selectedUkuran, selectedHarga, selectedStyle, selectedGender);
    }

    private JPanel createProductsGridWithFilters(String category, String merk, String ukuran,
            String harga, String style, String gender) {
        // Gunakan JPanel dengan FlowLayout sebagai wadah utama
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 25)); // Left aligned with spacing
        panel.setBackground(Color.white);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        try {
            // Create a list to store products with their stock info
            List<Map<String, Object>> productsWithStock = new ArrayList<>();

            // Build the base SQL query
            StringBuilder productSql = new StringBuilder(
                    "SELECT p.id_produk, p.nama_produk, p.harga_jual, p.size, p.gambar, p.merk, p.jenis_produk, p.gender "
                    + "FROM produk p WHERE p.status = 'dijual'");

            // Filter by category
            if (category.equals("Sepatu")) {
                productSql.append(" AND p.jenis_produk = 'Sepatu'");
            } else if (category.equals("Sandal")) {
                productSql.append(" AND p.jenis_produk = 'Sandal'");
            } else if (category.equals("Kaos Kaki")) {
                productSql.append(" AND p.jenis_produk = 'Kaos Kaki'");
            } else if (category.equals("Lainnya")) {
                productSql.append(" AND p.jenis_produk NOT IN ('Sepatu', 'Sandal', 'Kaos Kaki')");
            }

            // Apply additional filters
            if (merk != null && !merk.isEmpty()) {
                productSql.append(" AND p.merk = ?");
            }

            if (ukuran != null && !ukuran.isEmpty()) {
                // Handle size ranges
                if (ukuran.equals("< 25")) {
                    productSql.append(" AND p.size < 25");
                } else if (ukuran.equals("25 - 30")) {
                    productSql.append(" AND p.size >= 25 AND p.size <= 30");
                } else if (ukuran.equals("30 - 35")) {
                    productSql.append(" AND p.size > 30 AND p.size <= 35");
                } else if (ukuran.equals("35 - 40")) {
                    productSql.append(" AND p.size > 35 AND p.size <= 40");
                } else if (ukuran.equals("> 40")) {
                    productSql.append(" AND p.size > 40");
                }
            }

            if (harga != null && !harga.isEmpty()) {
                // Handle price ranges
                if (harga.equals("< 50.000")) {
                    productSql.append(" AND p.harga_jual < 50000");
                } else if (harga.equals("50.000 - 150.000")) {
                    productSql.append(" AND p.harga_jual >= 50000 AND p.harga_jual <= 150000");
                } else if (harga.equals("151.000 - 300.000")) {
                    productSql.append(" AND p.harga_jual > 150000 AND p.harga_jual <= 300000");
                } else if (harga.equals("301.000 - 450.000")) {
                    productSql.append(" AND p.harga_jual > 300000 AND p.harga_jual <= 450000");
                } else if (harga.equals("> 450.000")) {
                    productSql.append(" AND p.harga_jual > 450000");
                }
            }

            if (style != null && !style.isEmpty()) {
                // First get style ID from style name
                String styleId = null;
                String styleQuery = "SELECT id_style FROM style WHERE nama_style = ?";
                try (PreparedStatement styleStmt = con.prepareStatement(styleQuery)) {
                    styleStmt.setString(1, style);
                    ResultSet styleRs = styleStmt.executeQuery();
                    if (styleRs.next()) {
                        styleId = styleRs.getString("id_style");
                    }
                }

                if (styleId != null) {
                    productSql.append(" AND p.id_style = ?");
                }
            }

            if (gender != null && !gender.isEmpty()) {
                // Convert the display text to database values
                String dbGenderValue;
                switch (gender) {
                    case "Cowok":
                        dbGenderValue = "cowok";
                        break;
                    case "Cewek":
                        dbGenderValue = "cewek";
                        break;
                    case "Unisex":
                        dbGenderValue = "unisex";
                        break;
                    default:
                        dbGenderValue = gender; // fallback
                }
                productSql.append(" AND p.gender = ?");
            }

            // Prepare and execute the query
            try (PreparedStatement st = con.prepareStatement(productSql.toString())) {
                int paramIndex = 1;

                // Set parameters for the prepared statement
                if (merk != null && !merk.isEmpty()) {
                    st.setString(paramIndex++, merk);
                }

                if (style != null && !style.isEmpty()) {
                    // Get style ID again to ensure it's fresh
                    String styleId = null;
                    String styleQuery = "SELECT id_style FROM style WHERE nama_style = ?";
                    try (PreparedStatement styleStmt = con.prepareStatement(styleQuery)) {
                        styleStmt.setString(1, style);
                        ResultSet styleRs = styleStmt.executeQuery();
                        if (styleRs.next()) {
                            styleId = styleRs.getString("id_style");
                            st.setString(paramIndex++, styleId);
                        }
                    }
                }

                if (gender != null && !gender.isEmpty()) {
                    st.setString(paramIndex++, gender);
                }

                ResultSet rs = st.executeQuery();
                while (rs.next()) {
                    String productId = rs.getString("id_produk");
                    int stock = 0;

                    // Get the latest stock for each product
                    String stokSql = "SELECT produk_sisa FROM kartu_stok WHERE id_produk = ? ORDER BY tanggal_transaksi DESC LIMIT 1";
                    try (PreparedStatement stockStmt = con.prepareStatement(stokSql)) {
                        stockStmt.setString(1, productId);
                        ResultSet stockRs = stockStmt.executeQuery();
                        if (stockRs.next()) {
                            stock = stockRs.getInt("produk_sisa");
                        }
                    }

                    // Store product data and stock in a Map
                    Map<String, Object> productData = new HashMap<>();
                    productData.put("id_produk", productId);
                    productData.put("nama_produk", rs.getString("nama_produk"));
                    productData.put("harga_jual", rs.getDouble("harga_jual"));
                    productData.put("size", rs.getInt("size"));
                    productData.put("stock", stock);

                    // Properly handle BLOB image data
                    byte[] imageData = null;
                    Blob blob = rs.getBlob("gambar");
                    if (blob != null) {
                        imageData = blob.getBytes(1, (int) blob.length());
                    }
                    productData.put("gambar", imageData);
                    productData.put("merk", rs.getString("merk"));
                    productData.put("jenis_produk", rs.getString("jenis_produk"));
                    productData.put("gender", rs.getString("gender"));

                    productsWithStock.add(productData);
                }
            }

            // Sort the products by stock (ascending)
            productsWithStock.sort((p1, p2)
                    -> Integer.compare((Integer) p1.get("stock"), (Integer) p2.get("stock")));

            // Create grid panel with fixed size (4 columns)
            JPanel gridPanel = new JPanel(new GridLayout(0, 4, 20, 25));
            gridPanel.setBackground(Color.WHITE);
            gridPanel.setPreferredSize(new Dimension(980, 0));
            gridPanel.setMinimumSize(new Dimension(980, 0));

            // Add products to the grid panel
            for (Map<String, Object> product : productsWithStock) {
                JPanel productCard = createProductCard(
                        (String) product.get("id_produk"),
                        (String) product.get("nama_produk"),
                        (Double) product.get("harga_jual"),
                        (Integer) product.get("size"),
                        (Integer) product.get("stock"),
                        (byte[]) product.get("gambar"),
                        (String) product.get("merk"),
                        (String) product.get("jenis_produk"),
                        (String) product.get("gender")
                );
                gridPanel.add(productCard);
            }

            // Add empty panels if needed to maintain grid layout
            int productsCount = productsWithStock.size();
            int remainder = productsCount % 4;
            if (remainder > 0) {
                int emptyPanelsNeeded = 4 - remainder;
                for (int i = 0; i < emptyPanelsNeeded; i++) {
                    JPanel emptyPanel = new JPanel();
                    emptyPanel.setPreferredSize(new Dimension(230, 350));
                    emptyPanel.setMinimumSize(new Dimension(230, 350));
                    emptyPanel.setMaximumSize(new Dimension(230, 350));
                    emptyPanel.setBackground(Color.WHITE);
                    gridPanel.add(emptyPanel);
                }
            }

            // Calculate and set the preferred height based on number of rows
            int rowCount = (int) Math.ceil(productsCount / 4.0);
            int gridHeight = (rowCount * 350) + ((rowCount - 1) * 25) + 5;
            gridPanel.setPreferredSize(new Dimension(950, gridHeight));

            // Add grid panel to the main panel
            panel.add(gridPanel);

            // Show message if no products found
            if (productsWithStock.isEmpty()) {
                // Hapus gridPanel yang ada
                panel.removeAll();

                // Buat panel khusus untuk pesan "tidak ada produk"
                JPanel noProductsPanel = new JPanel(new BorderLayout());
                noProductsPanel.setBackground(Color.WHITE);

                JLabel noProductsLabel = new JLabel("Tidak ada produk yang sesuai dengan filter");
                noProductsLabel.setFont(new Font("Arial", Font.BOLD, 14));
                noProductsLabel.setHorizontalAlignment(JLabel.CENTER);
                noProductsPanel.add(noProductsLabel, BorderLayout.CENTER);

                // Tambahkan panel kosong di sebelah kanan untuk menghilangkan garis
                noProductsPanel.add(Box.createHorizontalGlue(), BorderLayout.EAST);

                panel.add(noProductsPanel);

                // Atur ukuran preferred untuk menghindari garis
                panel.setPreferredSize(new Dimension(980, 100));
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error connecting to database: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return panel;
    }

// Perbaiki metode loadFilteredProducts:
    private void loadFilteredProducts(JPanel targetPanel, String category, String merk, String ukuran,
            String harga, String style, String gender) {

        // Hapus komponen yang ada di panel target
        Component[] components = targetPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof ScrollPane) {
                targetPanel.remove(comp);
            }
        }

        try {
            // Buat produk grid baru dengan filter
            JPanel productsGrid = createProductsGridWithFilters(category, merk, ukuran, harga, style, gender);
            ScrollPane newScrollPane = createScrollPaneForProducts(productsGrid);

            // Tambahkan scroll pane baru ke panel target
            targetPanel.add(newScrollPane, BorderLayout.CENTER);

            // Refresh tampilan
            targetPanel.revalidate();
            targetPanel.repaint();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saat memfilter produk: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createProductCard(String productId, String productName, double price, int size,
            int stock, byte[] imageData, String merk, String jenis, String gender) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Panel gambar produk dengan background abu-abu muda rounded
        JPanel imageContainer = new JPanel(new BorderLayout());
        imageContainer.setBackground(Color.white);

        // Tetapkan ukuran tetap untuk imageContainer
        imageContainer.setPreferredSize(new Dimension(230, 230));
        imageContainer.setMinimumSize(new Dimension(230, 230));
        imageContainer.setMaximumSize(new Dimension(230, 230));

        // Membuat custom panel dengan sudut melengkung menggunakan paintComponent
        JPanel imagePanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();

                // Gunakan anti-aliasing untuk membuat sudut yang halus
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Buat warna background abu-abu muda
                g2d.setColor(new Color(255, 255, 255));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                // Tambahkan border 2px dengan warna abu-abu lebih gelap
                g2d.setColor(new Color(115, 115, 115));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 30, 30);

                g2d.dispose();
            }
        };
        imagePanel.setOpaque(false);
        imagePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Tetapkan ukuran tetap untuk imagePanel
        imagePanel.setPreferredSize(new Dimension(220, 220));
        imagePanel.setMinimumSize(new Dimension(220, 220));
        imagePanel.setMaximumSize(new Dimension(220, 220));

        // Store product ID as a property to use in click listener
        imagePanel.putClientProperty("productId", productId);
        sellPanelClickListener(imagePanel);

        // Menggunakan GridBagLayout untuk lebih presisi dalam pengaturan posisi
        JPanel imageLabelContainer = new JPanel(new GridBagLayout());
        imageLabelContainer.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);

        try {
            // Handle byte array image data
            if (imageData != null && imageData.length > 0) {
                ImageIcon icon = new ImageIcon(imageData);
                Image img = icon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(img));
            } else {
                // If no image data, show placeholder with product name
                createPlaceholderLabel(imageLabel, productName);
            }
        } catch (Exception e) {
            // If error occurs, show placeholder
            createPlaceholderLabel(imageLabel, productName);
            e.printStackTrace();
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
        infoPanel.setMinimumSize(new Dimension(230, 100));
        infoPanel.setMaximumSize(new Dimension(230, 100));

        // Use data from the database
        JLabel nameLabel = new JLabel(productName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameLabel.putClientProperty("productId", productId);
//        addLabelClickListener(nameLabel);

        // Format the price with proper formatting
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        String formattedPrice = currencyFormat.format(price);
        JLabel priceLabel = new JLabel(formattedPrice);
        priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        priceLabel.setForeground(new Color(0, 102, 204));
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        priceLabel.putClientProperty("productId", productId);
//        addLabelClickListener(priceLabel);

        JLabel sizeLabel = new JLabel("Uk : " + size);
        sizeLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        sizeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sizeLabel.putClientProperty("productId", productId);
//        addLabelClickListener(sizeLabel);

        JPanel stockAddPanel = new JPanel(new BorderLayout(5, 0));
        stockAddPanel.setBackground(Color.WHITE);
        stockAddPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        stockAddPanel.setMaximumSize(new Dimension(230, 30));

        JLabel stockLabel = new JLabel("Stok : " + stock);
        stockLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        stockLabel.putClientProperty("productId", productId);
//        addLabelClickListener(stockLabel);

        stockAddPanel.add(stockLabel, BorderLayout.WEST);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(priceLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(sizeLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(stockAddPanel);

        card.setPreferredSize(new Dimension(230, 350));
        card.setMinimumSize(new Dimension(230, 350));
        card.setMaximumSize(new Dimension(230, 350));

        card.add(infoPanel, BorderLayout.SOUTH);
        return card;
    }

    // Helper method to create a placeholder label
    private void createPlaceholderLabel(JLabel imageLabel, String productName) {
        // Create a placeholder panel for better visual appearance
        JPanel placeholderPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(230, 230, 230));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(new Color(180, 180, 180));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2d.dispose();
            }
        };
        placeholderPanel.setPreferredSize(new Dimension(180, 180));

        // Create the text label
        String displayText = productName.length() > 15
                ? productName.substring(0, 15) + "..." : productName;
        JLabel textLabel = new JLabel(displayText);
        textLabel.setFont(new Font("Arial", Font.BOLD, 14));
        textLabel.setForeground(Color.DARK_GRAY);
        textLabel.setHorizontalAlignment(JLabel.CENTER);

        // Add icon representing missing image
        JLabel iconLabel = new JLabel(new ImageIcon(getClass().getResource("/icons/no_image.png")));
        iconLabel.setHorizontalAlignment(JLabel.CENTER);

        // If icon fails to load, use text only
        if (iconLabel.getIcon() == null || iconLabel.getIcon().getIconWidth() <= 0) {
            placeholderPanel.setLayout(new GridBagLayout());
            placeholderPanel.add(textLabel);
        } else {
            placeholderPanel.setLayout(new BorderLayout());
            placeholderPanel.add(iconLabel, BorderLayout.CENTER);
            placeholderPanel.add(textLabel, BorderLayout.SOUTH);
        }

        imageLabel.setLayout(new BorderLayout());
        imageLabel.add(placeholderPanel, BorderLayout.CENTER);
    }

//    private void addLabelClickListener(JLabel label) {
//        label.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                String productId = (String) label.getClientProperty("productId");
//                if (productId != null) {
//                    selectedProductId = productId; // Store the selected product ID
//                    System.out.println("Product ID clicked: " + selectedProductId);
////                    if (addLabelClickListener != null) {
////                        // Now run the listener which will call switchToEditProductPanel
////                        addLabelClickListener.run();
////                    }
//                }
//            }
//
//            @Override
//            public void mouseEntered(MouseEvent e) {
//                label.setCursor(new Cursor(Cursor.HAND_CURSOR));
//            }
//
//            @Override
//            public void mouseExited(MouseEvent e) {
//                label.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//            }
//        });
//    }

    private void sellPanelClickListener(JPanel panel) {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String productId = (String) panel.getClientProperty("productId");
                if (productId != null) {
                    selectedProductId = productId; // Store the selected product ID
                    System.out.println("Product ID clicked: " + selectedProductId);
                    FormKasir.getMainFrame().switchToTransJualPanel();
                    if (sellPanelClickListener != null) {
                        // Now run the listener which will call switchToEditProductPanel
                        FormKasir.getMainFrame().switchToTransJualPanel();
                        sellPanelClickListener.run();
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
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

    public String getSelectedProductId() {
        return selectedProductId;
    }

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

    private void switchCategory(String category) {
        // Reset semua filter sebelum berpindah kategori
        resetAllFilters();

        // Tampilkan panel yang sesuai
        CardLayout cl = (CardLayout) mainContentPanel.getLayout();
        cl.show(mainContentPanel, category);

        // Refresh tampilan
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    private void resetAllFilters() {
        // Reset semua dropdown filter ke nilai default
        Component[] components = sepatuPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                Component[] subComps = ((JPanel) comp).getComponents();
                for (Component subComp : subComps) {
                    if (subComp instanceof ComboboxCustomKhususProduk) {
                        ((ComboboxCustomKhususProduk) subComp).setSelectedIndex(0);
                    }
                }
            }
        }

        Component[] componentss = sandalPanel.getComponents();
        for (Component comp : componentss) {
            if (comp instanceof JPanel) {
                Component[] subComps = ((JPanel) comp).getComponents();
                for (Component subComp : subComps) {
                    if (subComp instanceof ComboboxCustomKhususProduk) {
                        ((ComboboxCustomKhususProduk) subComp).setSelectedIndex(0);
                    }
                }
            }
        }

        Component[] componentsk = kaosKakiPanel.getComponents();
        for (Component comp : componentsk) {
            if (comp instanceof JPanel) {
                Component[] subComps = ((JPanel) comp).getComponents();
                for (Component subComp : subComps) {
                    if (subComp instanceof ComboboxCustomKhususProduk) {
                        ((ComboboxCustomKhususProduk) subComp).setSelectedIndex(0);
                    }
                }
            }
        }

        Component[] componentsl = lainnyaPanel.getComponents();
        for (Component comp : componentsl) {
            if (comp instanceof JPanel) {
                Component[] subComps = ((JPanel) comp).getComponents();
                for (Component subComp : subComps) {
                    if (subComp instanceof ComboboxCustomKhususProduk) {
                        ((ComboboxCustomKhususProduk) subComp).setSelectedIndex(0);
                    }
                }
            }
        }

    }

}
