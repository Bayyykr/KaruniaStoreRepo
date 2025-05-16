package PopUp_all;

import SourceCode.RoundedButtonLaporan;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import SourceCode.ScrollPane;
import db.conn;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PopUp_DashboardKasirTelusuriLainnya extends JDialog {

    private JComponent glassPane;
    private JFrame parentFrame;
    private JPanel contentPanel;
    private JTextField searchField;
    private JPanel itemListPanel;
    private JLabel closeButton;
    private JButton filterSepatuButton, filterSandalButton, filterKaosKakiButton, filterLainnyaButton;
    private Map<String, JButton> categoryButtons = new HashMap<>();

    // Radius terpisah untuk setiap komponen
    private final int MAIN_PANEL_RADIUS = 20;
    private final int BUTTON_RADIUS = 10;
    private final int SEARCH_FIELD_RADIUS = 18;
    private final int ITEM_PANEL_RADIUS = 12;
    private final int STOCK_COUNT_RADIUS = 10;
    private final int INDICATOR_RADIUS = 10;

    private final int FINAL_WIDTH = 600;
    private final int FINAL_HEIGHT = 650;
    private final int ANIMATION_DURATION = 300;
    private final int ANIMATION_DELAY = 10;
    private float currentScale = 0.01f;
    private boolean animationStarted = false;
    private Timer animationTimer;
    private Timer closeAnimationTimer;

    // Flag to avoid adding glassPane multiple times
    private static boolean isShowingPopup = false;

    // Data untuk item stok menipis
    private ArrayList<StockItem> sepatuItems = new ArrayList<>();
    private ArrayList<StockItem> sandalItems = new ArrayList<>();
    private ArrayList<StockItem> kaosKakiItems = new ArrayList<>();
    private ArrayList<StockItem> lainnyaItems = new ArrayList<>();
    private ArrayList<StockItem> currentItems = new ArrayList<>();
    private ArrayList<StockItem> allItems = new ArrayList<>();
    private String currentCategory = "Sepatu"; 

    // Icon paths - diperbarui untuk menggunakan SourceImage.icon
    private final String WARNING_ICON_PATH = "/SourceImage/icon/warning_segitiga_merah.png";
    private final String FILTER_ICON_PATH = "/SourceImage/icon/filter.png";
    private final String SEARCH_ICON_PATH = "/SourceImage/icon/icon_search_hitam.png";

    public PopUp_DashboardKasirTelusuriLainnya() {
        this(null);
    }

    public PopUp_DashboardKasirTelusuriLainnya(JFrame parent) {
        super(parent, "Cari Item Stok Menipis", true);
        this.parentFrame = parent;
        setModal(true);
        setPreferredSize(new Dimension(FINAL_WIDTH, FINAL_HEIGHT));

        if (isShowingPopup) {
            dispose();
            return;
        }
        isShowingPopup = true;
        
        // Load data from database first
        loadDataFromDatabase();
        
        glassPane = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 180));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        glassPane.setOpaque(false);

        if (parent != null) {
            glassPane.setBounds(0, 0, parent.getWidth(), parent.getHeight());
            parentLayeredPane().add(glassPane, JLayeredPane.POPUP_LAYER);
        }

        setUndecorated(true);
        setSize(FINAL_WIDTH, FINAL_HEIGHT);
        setLocationRelativeTo(parent);
        setBackground(new Color(0, 0, 0, 0));

        contentPanel = new RoundedPanel(MAIN_PANEL_RADIUS);
        contentPanel.setLayout(null);
        contentPanel.setBounds(0, 0, FINAL_WIDTH, FINAL_HEIGHT);
        contentPanel.setBackground(Color.WHITE);
        add(contentPanel);

        createComponents();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanupAndClose();
            }
        });

        startScaleAnimation();
    }

    private void loadDataFromDatabase() {
        // Clear existing data
        sepatuItems.clear();
        sandalItems.clear();
        kaosKakiItems.clear();
        lainnyaItems.clear();
        allItems.clear();
        
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            connection = conn.getConnection();
            
            String sql = "SELECT p.jenis_produk, p.nama_produk, ks.produk_sisa " +
                         "FROM produk p " +
                         "LEFT JOIN ( " +
                         "    SELECT ks1.id_produk, ks1.produk_sisa " +
                         "    FROM kartu_stok ks1 " +
                         "    INNER JOIN ( " +
                         "        SELECT id_produk, MAX(tanggal_transaksi) AS max_tanggal " +
                         "        FROM kartu_stok " +
                         "        GROUP BY id_produk " +
                         "    ) ks2 ON ks1.id_produk = ks2.id_produk AND ks1.tanggal_transaksi = ks2.max_tanggal " +
                         ") ks ON p.id_produk = ks.id_produk " +
                         "WHERE ks.produk_sisa < 10 " +
                         "ORDER BY ks.produk_sisa ASC";
            
            stmt = connection.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                String category = rs.getString("jenis_produk");
                String itemName = rs.getString("nama_produk");
                int stockCount = rs.getInt("produk_sisa");
                
                StockItem item = new StockItem(itemName, stockCount);
                allItems.add(item);
                
                // Sort by category
                if (category != null) {
                    if (category.equalsIgnoreCase("Sepatu")) {
                        sepatuItems.add(item);
                    } else if (category.equalsIgnoreCase("Sandal")) {
                        sandalItems.add(item);
                    } else if (category.equalsIgnoreCase("Kaos Kaki")) {
                        kaosKakiItems.add(item);
                    } else {
                        lainnyaItems.add(item);
                    }
                } else {
                    lainnyaItems.add(item);
                }
            }
            
            // Set the current items to the default category
            currentItems = sepatuItems;
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading data: " + e.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                // Don't close the connection here as it's managed by conn class
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void createComponents() {
        JPanel headerPanel = new RoundedTopPanel(MAIN_PANEL_RADIUS);
        headerPanel.setBounds(0, 0, FINAL_WIDTH, 50);
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setLayout(null);

        JLabel searchIcon = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(WARNING_ICON_PATH));
            Image img = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            searchIcon.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            // Handle icon loading error silently
        }
        searchIcon.setBounds(25, 13, 24, 24);
        headerPanel.add(searchIcon);

        // Title
        JLabel titleLabel = new JLabel("Cari Item Stok Menipis");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBounds(65, 10, 300, 30);
        headerPanel.add(titleLabel);

        // Close button (diubah menjadi JLabel)
        closeButton = new JLabel("x");
        closeButton.setFont(new Font("Arial", Font.BOLD, 20));
        closeButton.setBounds(FINAL_WIDTH - 50, 10, 30, 30);
        closeButton.setForeground(Color.BLACK);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.setHorizontalAlignment(SwingConstants.CENTER);

        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                startCloseAnimation();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                closeButton.setForeground(Color.GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeButton.setForeground(Color.BLACK);
            }
        });

        headerPanel.add(closeButton);

        contentPanel.add(headerPanel);

        // Search field
        JPanel searchPanel = new JPanel(null);
        searchPanel.setBounds(25, 60, FINAL_WIDTH - 50, 50);
        searchPanel.setBackground(Color.WHITE);

        JPanel searchFieldPanel = new JPanel(null);
        searchFieldPanel.setBounds(0, 0, FINAL_WIDTH - 50, 50);
        searchFieldPanel.setBackground(Color.WHITE);
        searchFieldPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));

        searchFieldPanel.setBorder(BorderFactory.createCompoundBorder(
                (Border) new RoundBorder(SEARCH_FIELD_RADIUS, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));

        searchField = new JTextField();
        searchField.setText("Search");
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setForeground(Color.GRAY);
        searchField.setBounds(10, 0, FINAL_WIDTH - 130, 50);
        searchField.setOpaque(false);
        searchField.setBorder(BorderFactory.createEmptyBorder());

        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });
        
        // Add search functionality
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = searchField.getText().toLowerCase();
                if (searchText.equals("search")) {
                    // If default text, just show all items for the category
                    updateItemList();
                    return;
                }
                
                filterItemsBySearchText(searchText);
            }
        });

        JLabel searchIconRight = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(SEARCH_ICON_PATH));
            Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            searchIconRight.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            // Handle icon loading error silently
        }
        searchIconRight.setBounds(FINAL_WIDTH - 90, 15, 24, 24);

        searchFieldPanel.add(searchField);
        searchFieldPanel.add(searchIconRight);
        searchPanel.add(searchFieldPanel);
        contentPanel.add(searchPanel);

        JPanel filterPanel = new JPanel(null);
        filterPanel.setBounds(25, 120, FINAL_WIDTH - 50, 50);
        filterPanel.setBackground(Color.WHITE);

        JLabel filterIcon = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(FILTER_ICON_PATH));
            Image img = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            filterIcon.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            // Handle icon loading error silently
        }
        filterIcon.setBounds(0, 15, 20, 20);
        filterPanel.add(filterIcon);

        JLabel filterText = new JLabel("Filter");
        filterText.setFont(new Font("Arial", Font.BOLD, 14));
        filterText.setBounds(25, 15, 50, 20);
        filterPanel.add(filterText);

        // Filter buttons
        int buttonWidth = 100;
        int spacing = 10;
        int startX = 85;

        filterSepatuButton = createFilterButton("Sepatu", startX);
        filterSandalButton = createFilterButton("Sandal", startX + buttonWidth + spacing);
        filterKaosKakiButton = createFilterButton("Kaos Kaki", startX + 2 * (buttonWidth + spacing));
        filterLainnyaButton = createFilterButton("Lainnya", startX + 3 * (buttonWidth + spacing));

        // Add filter buttons to panel
        filterPanel.add(filterSepatuButton);
        filterPanel.add(filterSandalButton);
        filterPanel.add(filterKaosKakiButton);
        filterPanel.add(filterLainnyaButton);

        categoryButtons.put("Sepatu", filterSepatuButton);
        categoryButtons.put("Sandal", filterSandalButton);
        categoryButtons.put("Kaos Kaki", filterKaosKakiButton);
        categoryButtons.put("Lainnya", filterLainnyaButton);

        updateActiveCategory("Sepatu"); 

        contentPanel.add(filterPanel);

        // Item list panel (scrollable)
        itemListPanel = new JPanel();
        itemListPanel.setLayout(new BoxLayout(itemListPanel, BoxLayout.Y_AXIS));
        itemListPanel.setBackground(Color.WHITE);

        ScrollPane scrollPane = new ScrollPane(itemListPanel, ScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBounds(25, 180, FINAL_WIDTH - 50, 450);

        scrollPane.setThumbColor(new Color(100, 100, 100, 150)); 
        scrollPane.setTrackColor(new Color(240, 240, 240, 100)); 
        scrollPane.setThumbThickness(8); 
        scrollPane.setThumbRadius(8); 

        contentPanel.add(scrollPane);

        // Load initial items
        updateItemList();
    }

    private void filterItemsBySearchText(String searchText) {
        // Clear current items
        itemListPanel.removeAll();
        
        ArrayList<StockItem> filteredItems = new ArrayList<>();
        
        // Filter the current category items
        ArrayList<StockItem> itemsToFilter;
        if (searchText.isEmpty()) {
            // If search field is empty, just use current category
            itemsToFilter = getCurrentCategoryItems();
        } else {
            // Otherwise filter from all items to allow cross-category search
            itemsToFilter = allItems;
        }
        
        // Perform filtering
        for (StockItem item : itemsToFilter) {
            if (item.getName().toLowerCase().contains(searchText)) {
                filteredItems.add(item);
            }
        }
        
        // Display filtered items
        for (StockItem item : filteredItems) {
            JPanel itemPanel = createItemPanel(item);
            itemListPanel.add(itemPanel);
            itemListPanel.add(createSpacingPanel());
        }
        
        // Add empty panels for spacing if needed
        int emptySpacesToAdd = Math.max(0, 5 - filteredItems.size());
        for (int i = 0; i < emptySpacesToAdd; i++) {
            itemListPanel.add(createEmptyItemPanel());
        }
        
        // Refresh panel
        itemListPanel.revalidate();
        itemListPanel.repaint();
    }
    
    private ArrayList<StockItem> getCurrentCategoryItems() {
        switch (currentCategory) {
            case "Sepatu":
                return sepatuItems;
            case "Sandal":
                return sandalItems;
            case "Kaos Kaki":
                return kaosKakiItems;
            case "Lainnya":
            default:
                return lainnyaItems;
        }
    }

    private JButton createFilterButton(String text, int x) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(getBackground().darker());
                } else {
                    g2.setColor(getBackground());
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), BUTTON_RADIUS*2, BUTTON_RADIUS*2);

                if (getBackground().equals(Color.BLACK)) {
                    setForeground(Color.WHITE);
                } else {
                    setForeground(Color.BLACK);
                }
                
                g2.dispose();
                super.paintComponent(g);
            }
            
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getBackground().equals(Color.BLACK)) {
                    g2.setColor(Color.BLACK);  
                } else {
                    g2.setColor(Color.LIGHT_GRAY); 
                }
                
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, BUTTON_RADIUS*2, BUTTON_RADIUS*2);
                g2.dispose();
            }
        };
        
        button.setBounds(x, 10, 100, 30);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setContentAreaFilled(false);  
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addActionListener(e -> {
            updateActiveCategory(text);
            updateItemList();
        });

        return button;
    }

    private void updateActiveCategory(String category) {
        for (Map.Entry<String, JButton> entry : categoryButtons.entrySet()) {
            entry.getValue().setBackground(Color.WHITE);
        }
        
        JButton activeButton = categoryButtons.get(category);
        if (activeButton != null) {
            activeButton.setBackground(Color.BLACK);
        }

        currentCategory = category;
        currentItems = getCurrentCategoryItems();
    }

    private void updateItemList() {
        // Clear current items
        itemListPanel.removeAll();

        // Add items based on current category
        for (StockItem item : currentItems) {
            JPanel itemPanel = createItemPanel(item);
            itemListPanel.add(itemPanel);

            // Menambahkan panel kosong untuk jarak antar item
            itemListPanel.add(createSpacingPanel());
        }

        // Add empty panels for spacing if needed
        int emptySpacesToAdd = Math.max(0, 5 - currentItems.size());
        for (int i = 0; i < emptySpacesToAdd; i++) {
            itemListPanel.add(createEmptyItemPanel());
        }

        // Refresh panel
        itemListPanel.revalidate();
        itemListPanel.repaint();
    }

    // Panel untuk spacing antar item
    private JPanel createSpacingPanel() {
        JPanel panel = new JPanel();
        panel.setMaximumSize(new Dimension(FINAL_WIDTH - 50, 10));
        panel.setPreferredSize(new Dimension(FINAL_WIDTH - 50, 10));
        panel.setBackground(Color.WHITE);
        return panel;
    }

    private JPanel createItemPanel(StockItem item) {
        JPanel panel = new JPanel(null);
        panel.setMaximumSize(new Dimension(FINAL_WIDTH - 50, 80));
        panel.setPreferredSize(new Dimension(FINAL_WIDTH - 50, 80));
        panel.setBackground(Color.WHITE);
        panel.setBorder((Border) new RoundBorder(ITEM_PANEL_RADIUS, Color.LIGHT_GRAY));

        // Red indicator dengan bagian yang di-rounded
        RoundedPanel indicator = new RoundedPanel(10);
        indicator.setBounds(10, 20, 8, 40);
        indicator.setBackground(new Color(255, 59, 48));
        indicator.setOpaque(true);

        // Membuat indicator merah menjadi rounded dengan radius khusus
        indicator.setBorder((Border) new RoundBorder(INDICATOR_RADIUS, new Color(255, 59, 48)));
        panel.add(indicator);

        // Item name
        JLabel nameLabel = new JLabel(item.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setBounds(25, 30, 400, 20);
        panel.add(nameLabel);

        // Stock count dengan radius khusus
        JLabel countLabel = new JLabel(String.valueOf(item.getStockCount()));
        countLabel.setFont(new Font("Arial", Font.BOLD, 14));
        countLabel.setHorizontalAlignment(SwingConstants.CENTER);
        countLabel.setBounds(FINAL_WIDTH - 140, 20, 80, 40);
        countLabel.setBorder((Border) new RoundBorder(STOCK_COUNT_RADIUS, Color.LIGHT_GRAY));
        panel.add(countLabel);

        return panel;
    }

    private JPanel createEmptyItemPanel() {
        JPanel panel = new JPanel();
        panel.setMaximumSize(new Dimension(FINAL_WIDTH - 50, 80));
        panel.setPreferredSize(new Dimension(FINAL_WIDTH - 50, 80));
        panel.setBackground(Color.WHITE);
        return panel;
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
                float easedProgress = 1 - (1 - progress) * (1 - progress) * (1 - progress);

                currentScale = 0.01f + 0.99f * easedProgress;

                repaint();

                if (currentFrame[0] >= totalFrames) {
                    animationTimer.stop();
                    currentScale = 1.0f;
                    repaint();
                }
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

        closeAnimationTimer = new Timer(ANIMATION_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentFrame[0]++;

                float progress = (float) currentFrame[0] / totalFrames;
                float easedProgress = progress * progress;

                currentScale = 1.0f - 0.99f * easedProgress;

                repaint();

                if (currentFrame[0] >= totalFrames) {
                    closeAnimationTimer.stop();
                    cleanupAndClose();
                }
            }
        });

        closeAnimationTimer.start();
    }

    private void cleanupAndClose() {
        // Reset flag when popup is closed
        isShowingPopup = false;
        // Remove glassPane
        closePopup();
        // Close dialog
        dispose();
    }

    private void closePopup() {
        if (parentFrame != null) {
            parentLayeredPane().remove(glassPane);
            parentLayeredPane().repaint();
        }
    }

    private JLayeredPane parentLayeredPane() {
        return parentFrame.getLayeredPane();
    }

    private class StockItem {
        private String name;
        private int stockCount;

        public StockItem(String name, int stockCount) {
            this.name = name;
            this.stockCount = stockCount;
        }

        public String getName() {
            return name;
        }

        public int getStockCount() {
            return stockCount;
        }
    }

    class RoundBorder extends AbstractBorder {
        private int radius;
        private Color color;

        public RoundBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2d.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(1, 1, 1, 1);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.top = insets.right = insets.bottom = 1;
            return insets;
        }

        public Color getColor() {
            return color;
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

    class RoundedTopPanel extends JPanel {
        private int radius;

        public RoundedTopPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(getBackground());

            int width = getWidth();
            int height = getHeight();

            g2.fillRoundRect(0, 0, width, height + radius, radius, radius);

            g2.dispose();
        }
    }
}