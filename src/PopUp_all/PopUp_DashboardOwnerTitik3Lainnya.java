package PopUp_all;

import SourceCode.RoundedButtonLaporan;
import db.conn;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.sql.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import SourceCode.ScrollPane;

public class PopUp_DashboardOwnerTitik3Lainnya extends JDialog {

    private JComponent glassPane;
    private JFrame parentFrame;
    private JPanel contentPanel;
    private JTextField searchField;
    private JPanel itemListPanel;
    private JLabel closeButton;
    private Map<String, JButton> categoryButtons = new HashMap<>();

    private final int MAIN_PANEL_RADIUS = 20;
    private final int BUTTON_RADIUS = 10;
    private final int SEARCH_FIELD_RADIUS = 18;
    private final int ITEM_PANEL_RADIUS = 12;
    private final int STOCK_COUNT_RADIUS = 10;
    private final int INDICATOR_RADIUS = 10;
    private final int INFO_FIELD_RADIUS = 5; 

    private final int FINAL_WIDTH = 600;
    private final int FINAL_HEIGHT = 650;
    private final int ANIMATION_DURATION = 300;
    private final int ANIMATION_DELAY = 10;
    private float currentScale = 0.01f;
    private boolean animationStarted = false;
    private Timer animationTimer;
    private Timer closeAnimationTimer;

    private static boolean isShowingPopup = false;

    private ArrayList<ProdukItem> sepatuItems = new ArrayList<>();
    private ArrayList<ProdukItem> currentItems = new ArrayList<>();
    private String currentCategory = "Lainnya"; 
    private final String SEARCH_ICON_PATH = "/SourceImage/icon/icon_search_hitam.png";

    public PopUp_DashboardOwnerTitik3Lainnya() {
        this(null);
    }

    public PopUp_DashboardOwnerTitik3Lainnya(JFrame parent) {
        super(parent, "Detail Stok Lainnya", true);
        this.parentFrame = parent;
        setModal(true);
        setPreferredSize(new Dimension(FINAL_WIDTH, FINAL_HEIGHT));

        if (isShowingPopup) {
            dispose();
            return;
        }
        isShowingPopup = true;
        
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
    String sql = "SELECT p.id_produk, p.nama_produk, p.size, ks.produk_sisa " +
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
                "WHERE p.jenis_produk = 'lainnya' AND p.status = 'dijual' " +
                "ORDER BY COALESCE(ks.produk_sisa, 0) DESC, p.nama_produk, p.size";

        Connection connection = conn.getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String idProduk = rs.getString("id_produk");
                String namaProduk = rs.getString("nama_produk");
                String size = rs.getString("size");
                int stok = rs.getInt("produk_sisa");

                sepatuItems.add(new ProdukItem(idProduk, namaProduk, size, stok));
            }

            currentItems = sepatuItems;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data dari database: " + e.getMessage(), 
                                      "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createComponents() {
        JPanel headerPanel = new RoundedTopPanel(MAIN_PANEL_RADIUS);
        headerPanel.setBounds(0, 0, FINAL_WIDTH, 50);
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setLayout(null);

        JLabel titleLabel = new JLabel("Detail Stok - Lainnya");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBounds(25, 10, 300, 30);
        headerPanel.add(titleLabel);

        closeButton = new JLabel("×");
        closeButton.setFont(new Font("Arial", Font.BOLD, 24));
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

        JPanel searchPanel = new JPanel(null);
        searchPanel.setBounds(25, 60, FINAL_WIDTH - 50, 50);
        searchPanel.setBackground(Color.WHITE);

        JPanel searchFieldPanel = new JPanel(null);
        searchFieldPanel.setBounds(0, 0, FINAL_WIDTH - 50, 50);
        searchFieldPanel.setBackground(Color.WHITE);
        searchFieldPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(SEARCH_FIELD_RADIUS, Color.LIGHT_GRAY),
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

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterItems(searchField.getText());
            }
        });

        JLabel searchIconRight = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(SEARCH_ICON_PATH));
            Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            searchIconRight.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            e.printStackTrace();
        }
        searchIconRight.setBounds(FINAL_WIDTH - 90, 15, 24, 24);

        searchFieldPanel.add(searchField);
        searchFieldPanel.add(searchIconRight);
        searchPanel.add(searchFieldPanel);
        contentPanel.add(searchPanel);

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
        updateItemList();
    }

    private void filterItems(String searchText) {
        if (searchText.isEmpty() || searchText.equals("Search")) {
            currentItems = sepatuItems;
        } else {
            currentItems = new ArrayList<>();
            for (ProdukItem item : sepatuItems) {
                if (item.getNamaProduk().toLowerCase().contains(searchText.toLowerCase())) {
                    currentItems.add(item);
                }
            }
        }
        updateItemList();
    }

    private void updateItemList() {
        itemListPanel.removeAll();

        for (ProdukItem item : currentItems) {
            JPanel itemPanel = createProdukItemPanel(item);
            itemListPanel.add(itemPanel);
            itemListPanel.add(createSpacingPanel());
        }

        itemListPanel.revalidate();
        itemListPanel.repaint();
    }

    private JPanel createSpacingPanel() {
        JPanel panel = new JPanel();
        panel.setMaximumSize(new Dimension(FINAL_WIDTH - 50, 10));
        panel.setPreferredSize(new Dimension(FINAL_WIDTH - 50, 10));
        panel.setBackground(Color.WHITE);
        return panel;
    }

    private JPanel createProdukItemPanel(ProdukItem item) {
        JPanel panel = new JPanel(null);
        panel.setMaximumSize(new Dimension(FINAL_WIDTH - 50, 100));
        panel.setPreferredSize(new Dimension(FINAL_WIDTH - 50, 100));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new RoundBorder(ITEM_PANEL_RADIUS, Color.LIGHT_GRAY));

        JLabel nameLabel = new JLabel(item.getNamaProduk());
        nameLabel.setFont(new Font("Poppins", Font.BOLD, 16));
        nameLabel.setBounds(25, 15, 400, 20);
        panel.add(nameLabel);

        // Panel untuk barcode dengan border rounded
        JPanel barcodePanel = new JPanel(new BorderLayout());
        barcodePanel.setBounds(25, 45, 320, 30);
        barcodePanel.setBackground(Color.WHITE);
        barcodePanel.setBorder(new RoundBorder(INFO_FIELD_RADIUS, Color.LIGHT_GRAY));
        
        JLabel barcodeLabel = new JLabel("Barcode : " + item.getIdProduk());
        barcodeLabel.setFont(new Font("Poppins", Font.BOLD, 12));
        barcodeLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        barcodePanel.add(barcodeLabel, BorderLayout.CENTER);
        
        panel.add(barcodePanel);

        // Panel untuk size dengan border rounded
        JPanel sizePanel = new JPanel(new BorderLayout());
        sizePanel.setBounds(360, 45, 80, 30);
        sizePanel.setBackground(Color.WHITE);
        sizePanel.setBorder(new RoundBorder(INFO_FIELD_RADIUS, Color.LIGHT_GRAY));
        
        JLabel sizeLabel = new JLabel("Size : " + item.getSize());
        sizeLabel.setFont(new Font("Poppins", Font.BOLD, 12));
        sizeLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        sizePanel.add(sizeLabel, BorderLayout.CENTER);
        
        panel.add(sizePanel);

        // Panel untuk stok dengan border rounded
        JPanel stockPanel = new JPanel(new BorderLayout());
        stockPanel.setBounds(450, 45, 80, 30);
        stockPanel.setBackground(Color.WHITE);
        stockPanel.setBorder(new RoundBorder(INFO_FIELD_RADIUS, Color.LIGHT_GRAY));
        
        JLabel stockLabel = new JLabel("Stok : " + item.getStok());
        stockLabel.setFont(new Font("Poppins", Font.BOLD, 12));
        stockLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        stockPanel.add(stockLabel, BorderLayout.CENTER);
        
        panel.add(stockPanel);

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
        isShowingPopup = false;
        closePopup();
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

    private class ProdukItem {
        private String idProduk;
        private String namaProduk;
        private String size;
        private int stok;

        public ProdukItem(String idProduk, String namaProduk, String size, int stok) {
            this.idProduk = idProduk;
            this.namaProduk = namaProduk;
            this.size = size;
            this.stok = stok;
        }

        public String getIdProduk() {
            return idProduk;
        }

        public String getNamaProduk() {
            return namaProduk;
        }

        public String getSize() {
            return size;
        }

        public int getStok() {
            return stok;
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