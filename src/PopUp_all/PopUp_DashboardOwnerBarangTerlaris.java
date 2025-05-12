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

public class PopUp_DashboardOwnerBarangTerlaris extends JDialog {

    private JComponent glassPane;
    private JFrame parentFrame;
    private JPanel contentPanel;
    private JPanel itemListPanel;
    private JLabel closeButton;
    private JButton filterSepatuButton, filterSandalButton, filterKaosKakiButton, filterLainnyaButton;
    private Map<String, JButton> categoryButtons = new HashMap<>();
    private JLabel filterIcon; // Icon filter
    private JLabel filterText; // Text filter
    private boolean isFilterActive = false; // Status filter

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
    private ArrayList<StockItem> allItems = new ArrayList<>(); // Untuk menyimpan semua item campuran
    private ArrayList<StockItem> currentItems = new ArrayList<>();
    private String currentCategory = "Semua"; // Default adalah semua item

    // Icon paths - diperbarui untuk menggunakan SourceImage.icon
    private final String WARNING_ICON_PATH = "/SourceImage/icon/icon_up_ijo.png";
    private final String FILTER_ICON_PATH = "/SourceImage/icon/filter.png";
    private final String FILTER_ICON_MINUS_PATH = "/SourceImage/icon/icon_filter_minus.png";
    private final String SEARCH_ICON_PATH = "/SourceImage/icon/icon_search_hitam.png";

    public PopUp_DashboardOwnerBarangTerlaris() {
        this(null);
    }

    public PopUp_DashboardOwnerBarangTerlaris(JFrame parent) {
        super(parent, "Barang Terlaris", true);
        this.parentFrame = parent;
        setModal(true);
        setPreferredSize(new Dimension(FINAL_WIDTH, FINAL_HEIGHT));

        if (isShowingPopup) {
            dispose();
            return;
        }
        isShowingPopup = true;
        
        initializeData();

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

    private void initializeData() {
        // Data Sepatu
        sepatuItems.add(new StockItem("Adidas Simanjuntak", "37", 10, 10));
        sepatuItems.add(new StockItem("Nike Air Max", "40", 8, 12));
        sepatuItems.add(new StockItem("Puma RS-X", "42", 5, 7));
        sepatuItems.add(new StockItem("Vans Old Skool", "39", 15, 20));
        sepatuItems.add(new StockItem("Converse Chuck Taylor", "38", 12, 15));

        // Data Sandal
        sandalItems.add(new StockItem("Sandal Kulit Hitam", "37", 5, 15));
        sandalItems.add(new StockItem("Sandal Gunung", "40", 8, 10));
        sandalItems.add(new StockItem("Sandal Jepit Premium", "41", 20, 25));
        sandalItems.add(new StockItem("Sandal Selop", "36", 7, 12));

        // Data Kaos Kaki
        kaosKakiItems.add(new StockItem("Kaos Kaki Hitam", "All Size", 30, 45));
        kaosKakiItems.add(new StockItem("Kaos Kaki Putih", "All Size", 25, 40));
        kaosKakiItems.add(new StockItem("Kaos Kaki Sport", "L", 15, 20));
        kaosKakiItems.add(new StockItem("Kaos Kaki Formal", "M", 12, 18));

        // Data Lainnya
        lainnyaItems.add(new StockItem("Tali Sepatu Premium", "120 cm", 25, 30));
        lainnyaItems.add(new StockItem("Sol Dalam", "40", 18, 22));
        lainnyaItems.add(new StockItem("Semprotan Anti Air", "250 ml", 10, 15));
        lainnyaItems.add(new StockItem("Sikat Sepatu", "Standard", 8, 12));

        // Gabungkan semua item
        allItems.addAll(sepatuItems);
        allItems.addAll(sandalItems);
        allItems.addAll(kaosKakiItems);
        allItems.addAll(lainnyaItems);

        // Set default item yang ditampilkan
        currentItems = allItems;
    }

    private void createComponents() {
        JPanel headerPanel = new RoundedTopPanel(MAIN_PANEL_RADIUS);
        headerPanel.setBounds(0, 0, FINAL_WIDTH, 50);
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setLayout(null);

        JLabel warningIcon = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(WARNING_ICON_PATH));
            Image img = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            warningIcon.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            System.err.println("Error loading warning icon: " + e.getMessage());
        }
        warningIcon.setBounds(25, 13, 24, 24);
        headerPanel.add(warningIcon);

        // Title
        JLabel titleLabel = new JLabel("Barang Terlaris");
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

        JPanel filterPanel = new JPanel(null);
        filterPanel.setBounds(25, 60, FINAL_WIDTH - 50, 50);
        filterPanel.setBackground(Color.WHITE);

        // Filter icon yang bisa di-klik
        filterIcon = new JLabel();
        updateFilterIcon(false); // Set default icon
        filterIcon.setBounds(10, 15, 20, 20);
        filterIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Filter text yang bisa di-klik
        filterText = new JLabel("Filter");
        filterText.setFont(new Font("Arial", Font.BOLD, 14));
        filterText.setBounds(35, 15, 50, 20);
        filterText.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Menambahkan action listener untuk filterIcon dan filterText
        MouseAdapter filterClickListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleFilterMode();
            }
        };
        
        filterIcon.addMouseListener(filterClickListener);
        filterText.addMouseListener(filterClickListener);
        
        filterPanel.add(filterIcon);
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

        updateActiveCategory("Semua"); // Default pilihan adalah Sepatu

        contentPanel.add(filterPanel);

        // Item list panel (scrollable)
        itemListPanel = new JPanel();
        itemListPanel.setLayout(new BoxLayout(itemListPanel, BoxLayout.Y_AXIS));
        itemListPanel.setBackground(Color.WHITE);

        ScrollPane scrollPane = new ScrollPane(itemListPanel, ScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBounds(25, 120, FINAL_WIDTH - 50, 510);

        scrollPane.setThumbColor(new Color(100, 100, 100, 150));
        scrollPane.setTrackColor(new Color(240, 240, 240, 100));
        scrollPane.setThumbThickness(8);
        scrollPane.setThumbRadius(8);

        contentPanel.add(scrollPane);

        // Load initial items
        updateItemList();
    }

    private void updateFilterIcon(boolean isMinusIcon) {
    try {
        ImageIcon icon;
        if (isMinusIcon) {
            icon = new ImageIcon(getClass().getResource(FILTER_ICON_MINUS_PATH));
            // Ukuran khusus untuk icon minus
            Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH); // Ukuran lebih besar
            filterIcon.setIcon(new ImageIcon(img));
            // Sesuaikan juga bounds-nya jika perlu
            filterIcon.setBounds(10, 15, 20, 20);
        } else {
            icon = new ImageIcon(getClass().getResource(FILTER_ICON_PATH));
            // Ukuran normal
            Image img = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            filterIcon.setIcon(new ImageIcon(img));
            // Bounds normal
            filterIcon.setBounds(10, 15, 16, 16);
        }
    } catch (Exception e) {
        System.err.println("Error loading filter icon: " + e.getMessage());
    }
}

    private void toggleFilterMode() {
         if (!isFilterActive && currentCategory.equals("Semua")) {
        // Don't allow activating filter mode when "Semua" is selected
        return;
    }
    
    isFilterActive = !isFilterActive;
    
    if (isFilterActive) {
        updateFilterIcon(true);
        updateActiveCategory(currentCategory);
    } else {
        updateFilterIcon(false);
        resetCategoryButtons();
        currentItems = allItems;
        currentCategory = "Semua";
    }
    
    updateItemList();
    }

    private void resetCategoryButtons() {
        for (Map.Entry<String, JButton> entry : categoryButtons.entrySet()) {
            entry.getValue().setBackground(Color.WHITE);
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

                if (getBackground().equals(new Color(64, 64, 64))) {
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

                if (getBackground().equals(new Color(64, 64, 64))) {
                    g2.setColor(new Color(64, 64, 64));  
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
            if (!isFilterActive) {
                // Aktifkan mode filter jika belum aktif
                isFilterActive = true;
                updateFilterIcon(true);
            }
            updateActiveCategory(text);
            updateItemList();
        });

        return button;
    }

    private void updateActiveCategory(String category) {
        // Reset semua tombol kategori
        for (Map.Entry<String, JButton> entry : categoryButtons.entrySet()) {
        entry.getValue().setBackground(Color.WHITE);
    }
    
    currentCategory = category;
    
    // Set active button with dark color if not "Semua"
    if (!category.equals("Semua")) {
        JButton activeButton = categoryButtons.get(category);
        if (activeButton != null) {
            activeButton.setBackground(new Color(64, 64, 64));
        }
        
        // Enable filter icon interaction
        filterIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        filterText.setCursor(new Cursor(Cursor.HAND_CURSOR));
    } else {
        // Disable filter icon interaction
        filterIcon.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        filterText.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        isFilterActive = false;
        updateFilterIcon(false);
    }

    switch (category) {
        case "Sepatu":
            currentItems = sepatuItems;
            break;
        case "Sandal":
            currentItems = sandalItems;
            break;
        case "Kaos Kaki":
            currentItems = kaosKakiItems;
            break;
        case "Lainnya":
            currentItems = lainnyaItems;
            break;
        default:
            currentItems = allItems;
            break;
    }
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

        // Add empty panels with gray placeholder lines if needed
        int emptySpacesToAdd = 5 - currentItems.size();
        if (emptySpacesToAdd > 0) {
            for (int i = 0; i < emptySpacesToAdd; i++) {
                itemListPanel.add(createEmptyItemPanel());
                if (i < emptySpacesToAdd - 1) {
                    itemListPanel.add(createSpacingPanel());
                }
            }
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
        panel.setBorder(new RoundBorder(ITEM_PANEL_RADIUS, Color.LIGHT_GRAY));

        // Nama item
        JLabel nameLabel = new JLabel(item.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setBounds(20, 15, 400, 20);
        panel.add(nameLabel);

        // UK/Size
        JLabel ukLabel = new JLabel("UK/Size : " + item.getSize());
        ukLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        ukLabel.setBounds(20, 45, 100, 20);
        ukLabel.setBorder(new RoundBorder(STOCK_COUNT_RADIUS, Color.LIGHT_GRAY));
        ukLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(ukLabel);

        // Stok
        JLabel stokLabel = new JLabel("Stok : " + item.getStockCount());
        stokLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        stokLabel.setBounds(130, 45, 100, 20);
        stokLabel.setBorder(new RoundBorder(STOCK_COUNT_RADIUS, Color.LIGHT_GRAY));
        stokLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(stokLabel);

       // Dijual Bulan Ini - modified to be properly rounded
    JPanel dijualPanel = new JPanel(new BorderLayout());
    dijualPanel.setBounds(240, 45, 140, 20);
    dijualPanel.setOpaque(false);
    
    JLabel dijualLabel = new JLabel("Dijual Bulan Ini : " + item.getSoldCount(), SwingConstants.CENTER);
    dijualLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    dijualLabel.setForeground(Color.WHITE);
    dijualLabel.setOpaque(false);
    
    JPanel roundedPanel = new JPanel(new BorderLayout()) {
        final int STOCK_COUNT_RADIUS = 5;
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(46, 204, 113));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), STOCK_COUNT_RADIUS*2, STOCK_COUNT_RADIUS*2);
            g2.dispose();
        }
    };
    roundedPanel.setOpaque(false);
    roundedPanel.add(dijualLabel, BorderLayout.CENTER);
    dijualPanel.add(roundedPanel, BorderLayout.CENTER);
    
    panel.add(dijualPanel);

        return panel;
    }

    private JPanel createEmptyItemPanel() {
        JPanel panel = new JPanel(null);
        panel.setMaximumSize(new Dimension(FINAL_WIDTH - 50, 80));
        panel.setPreferredSize(new Dimension(FINAL_WIDTH - 50, 80));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new RoundBorder(ITEM_PANEL_RADIUS, Color.LIGHT_GRAY));
        
        // Membuat garis placeholder abu-abu seperti di screenshot
        JPanel placeholderLine = new JPanel();
        placeholderLine.setBounds(20, 25, 250, 10);
        placeholderLine.setBackground(new Color(230, 230, 230));
        panel.add(placeholderLine);
        
        // UK/Size placeholder
        JLabel ukLabel = new JLabel("UK/Size : ");
        ukLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        ukLabel.setBounds(20, 45, 100, 20);
        ukLabel.setBorder(new RoundBorder(STOCK_COUNT_RADIUS, Color.LIGHT_GRAY));
        ukLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(ukLabel);

        // Stok placeholder
        JLabel stokLabel = new JLabel("Stok : ");
        stokLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        stokLabel.setBounds(130, 45, 100, 20);
        stokLabel.setBorder(new RoundBorder(STOCK_COUNT_RADIUS, Color.LIGHT_GRAY));
        stokLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(stokLabel);

        // Dijual Bulan Ini placeholder
        JLabel dijualLabel = new JLabel("Dijual Bulan Ini : ");
        dijualLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        dijualLabel.setBounds(240, 45, 140, 20);
        dijualLabel.setBorder(new RoundBorder(STOCK_COUNT_RADIUS, Color.LIGHT_GRAY));
        dijualLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(dijualLabel);
        
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

    // Class untuk menyimpan item stok
    private class StockItem {
        private String name;
        private String size;
        private int stockCount;
        private int soldCount;

        public StockItem(String name, String size, int stockCount, int soldCount) {
            this.name = name;
            this.size = size;
            this.stockCount = stockCount;
            this.soldCount = soldCount;
        }

        public String getName() {
            return name;
        }

        public String getSize() {
            return size;
        }

        public int getStockCount() {
            return stockCount;
        }
        
        public int getSoldCount() {
            return soldCount;
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

            // Draw background with rounded top corners only
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight() + radius, radius, radius);

            g2.dispose();
        }
    }
}