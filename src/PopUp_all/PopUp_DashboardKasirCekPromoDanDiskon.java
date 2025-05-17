package PopUp_all;

import SourceCode.RoundedButtonLaporan;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import javax.swing.*;
import java.util.ArrayList;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import SourceCode.ScrollPane;
import db.conn;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PopUp_DashboardKasirCekPromoDanDiskon extends JDialog {

    private JComponent glassPane;
    private JFrame parentFrame;
    private JPanel contentPanel;
    private JTextField searchField;
    private JPanel itemListPanel;
    private JLabel closeButton;

    // Radius terpisah untuk setiap komponen
    private final int MAIN_PANEL_RADIUS = 20;
    private final int BUTTON_RADIUS = 10;
    private final int SEARCH_FIELD_RADIUS = 18;
    private final int ITEM_PANEL_RADIUS = 12;

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

    // Data untuk item diskon
    private ArrayList<DiskonItem> diskonItems = new ArrayList<>();
    private final String SEARCH_ICON_PATH = "/SourceImage/icon/icon_search_hitam.png";

    public PopUp_DashboardKasirCekPromoDanDiskon() {
        this(null);
    }

    public PopUp_DashboardKasirCekPromoDanDiskon(JFrame parent) {
        super(parent, "Diskon yang Dipakai", true);
        this.parentFrame = parent;
        setModal(true);
        setPreferredSize(new Dimension(FINAL_WIDTH, FINAL_HEIGHT));

        if (isShowingPopup) {
            dispose();
            return;
        }
        isShowingPopup = true;
        
        // Ambil data diskon dari database
        loadDiskonDataDariDatabase();

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

    private void loadDiskonDataDariDatabase() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            // Mendapatkan koneksi dari class conn
            connection = conn.getConnection();
            
            // Query untuk mengambil data diskon dengan status = dipakai
            String sql = "SELECT nama_diskon, total_diskon FROM diskon WHERE status = 'dipakai'";
            statement = connection.prepareStatement(sql);
            
            resultSet = statement.executeQuery();
            
            // Reset list diskon
            diskonItems.clear();
            
            // Memproses hasil query
            while (resultSet.next()) {
                String namaDiskon = resultSet.getString("nama_diskon");
                int totalDiskon = resultSet.getInt("total_diskon");
                
                diskonItems.add(new DiskonItem(namaDiskon, totalDiskon));
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(PopUp_DashboardKasirCekPromoDanDiskon.class.getName()).log(Level.SEVERE, "Error saat mengambil data diskon", ex);
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat mengambil data diskon: " + ex.getMessage(), 
                    "Error Database", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Menutup resources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                // Jangan tutup koneksi karena kita menggunakan shared connection
            } catch (SQLException ex) {
                Logger.getLogger(PopUp_DashboardKasirCekPromoDanDiskon.class.getName()).log(Level.SEVERE, "Error saat menutup resources", ex);
            }
        }
    }

    private void createComponents() {
        JPanel headerPanel = new RoundedTopPanel(MAIN_PANEL_RADIUS);
        headerPanel.setBounds(0, 0, FINAL_WIDTH, 50);
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setLayout(null);

        // Judul
        JLabel titleLabel = new JLabel("Daftar Diskon yang Dipakai");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBounds(25, 10, 300, 30);
        headerPanel.add(titleLabel);

        // Close button
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
            // Jika gagal memuat ikon, tampilkan teks sebagai pengganti
            searchIconRight.setText("🔍");
        }
        searchIconRight.setBounds(FINAL_WIDTH - 90, 15, 24, 24);

        searchFieldPanel.add(searchField);
        searchFieldPanel.add(searchIconRight);
        searchPanel.add(searchFieldPanel);
        contentPanel.add(searchPanel);

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

        // Load items
        updateItemList();
    }
    
    private void filterItems(String searchText) {
        if (searchText.equals("Search")) {
            updateItemList();
            return;
        }
        
        // Clear current items
        itemListPanel.removeAll();
        
        searchText = searchText.toLowerCase();
        
        // Add filtered items
        for (DiskonItem item : diskonItems) {
            if (item.getName().toLowerCase().contains(searchText)) {
                JPanel itemPanel = createDiskonItemPanel(item);
                itemListPanel.add(itemPanel);
                // Menambahkan panel kosong untuk jarak antar item
                itemListPanel.add(createSpacingPanel());
            }
        }
        
        // Refresh panel
        itemListPanel.revalidate();
        itemListPanel.repaint();
    }

    private void updateItemList() {
        // Clear current items
        itemListPanel.removeAll();

        // Add items from database
        for (DiskonItem item : diskonItems) {
            JPanel itemPanel = createDiskonItemPanel(item);
            itemListPanel.add(itemPanel);

            // Menambahkan panel kosong untuk jarak antar item
            itemListPanel.add(createSpacingPanel());
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

    private JPanel createDiskonItemPanel(DiskonItem item) {
        JPanel panel = new JPanel(null);
        panel.setMaximumSize(new Dimension(FINAL_WIDTH - 50, 50));
        panel.setPreferredSize(new Dimension(FINAL_WIDTH - 50, 50));
        panel.setBackground(Color.WHITE);
        panel.setBorder((Border) new RoundBorder(ITEM_PANEL_RADIUS, Color.LIGHT_GRAY));

        // Item name (nama diskon)
        JLabel nameLabel = new JLabel(item.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setBounds(25, 15, 400, 20);
        panel.add(nameLabel);

        // Diskon value dengan format persentase
        JLabel percentLabel = new JLabel(item.getDiskonPercent() + " %");
        percentLabel.setFont(new Font("Arial", Font.BOLD, 14));
        percentLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        percentLabel.setBounds(FINAL_WIDTH - 140, 15, 80, 20);
        panel.add(percentLabel);

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

    // Class untuk menyimpan item diskon
    private class DiskonItem {
        private String name;
        private int diskonPercent;

        public DiskonItem(String name, int diskonPercent) {
            this.name = name;
            this.diskonPercent = diskonPercent;
        }

        public String getName() {
            return name;
        }

        public int getDiskonPercent() {
            return diskonPercent;
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