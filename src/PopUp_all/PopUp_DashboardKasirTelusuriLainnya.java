package PopUp_all;

import SourceCode.RoundedButton;
import SourceCode.ScrollPane;
import SourceCode.RoundedButtonLaporan;
import SourceCode.RoundedBorder;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.AffineTransform;

public class PopUp_DashboardKasirTelusuriLainnya extends JDialog {

    private JComponent glassPane;
    private JFrame parentFrame;
    private JPanel contentPanel;
    private JTable diskonTable;
    private JTextField searchField;
    private JPanel filterPanel;
    private JButton filterSepatuBtn, filterSandalBtn, filterKaosKakiBtn, filterLainnyaBtn;

    private final int RADIUS = 20;
    private final int FINAL_WIDTH = 600;
    private final int FINAL_HEIGHT = 650;
    private final int ANIMATION_DURATION = 300;
    private final int ANIMATION_DELAY = 10;
    private float currentScale = 0.01f;
    private boolean animationStarted = false;
    private Timer animationTimer;
    private Timer closeAnimationTimer;
    
    // Warna untuk komponen
    private final Color RED_COLOR = new Color(255, 59, 48);
    
    // Flag untuk menghindari penambahan glassPane berulang
    private static boolean isShowingPopup = false;

    // Konstruktor tanpa parameter
    public PopUp_DashboardKasirTelusuriLainnya() {
        this(null); // Memanggil konstruktor dengan parameter
    }

    public PopUp_DashboardKasirTelusuriLainnya(JFrame parent) {
        super(parent, "Cari Item Stok Menipis", true);
        this.parentFrame = parent;
        setModal(true);
        setPreferredSize(new Dimension(FINAL_WIDTH, FINAL_HEIGHT));

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
                g2.setColor(new Color(0, 0, 0, 180));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        glassPane.setOpaque(false);
        glassPane.setBounds(0, 0, parent.getWidth(), parent.getHeight());

        parentLayeredPane().add(glassPane, JLayeredPane.POPUP_LAYER);

        setUndecorated(true);
        setSize(FINAL_WIDTH, FINAL_HEIGHT);
        setLocationRelativeTo(parent);
        setBackground(new Color(0, 0, 0, 0));

        contentPanel = new RoundedPanel(RADIUS);
        contentPanel.setLayout(null);
        contentPanel.setBounds(0, 0, FINAL_WIDTH, FINAL_HEIGHT);
        contentPanel.setBackground(Color.WHITE);
        add(contentPanel);

        initComponents();

        // Tambahkan WindowListener untuk membersihkan saat popup ditutup
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanupAndClose();
            }
        });

        startScaleAnimation();
    }

    private void initComponents() {
        // Header dengan ikon peringatan dan judul
        JPanel headerPanel = new JPanel(null);
        headerPanel.setBounds(0, 0, FINAL_WIDTH, 90);
        headerPanel.setBackground(Color.WHITE);
        contentPanel.add(headerPanel);
        
        // Ikon peringatan
        JLabel warningIcon = new JLabel("\u26A0");
        warningIcon.setFont(new Font("Dialog", Font.BOLD, 24));
        warningIcon.setForeground(RED_COLOR);
        warningIcon.setBounds(60, 45, 30, 30);
        headerPanel.add(warningIcon);
        
        // Judul
        JLabel titleLabel = new JLabel("Cari Item Stok Menipis");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 20));
        titleLabel.setBounds(100, 45, 300, 30);
        headerPanel.add(titleLabel);
        
        // Tombol close (X)
        JButton closeButton = createCloseButton();
        closeButton.setBounds(FINAL_WIDTH - 50, 45, 30, 30);
        headerPanel.add(closeButton);
        
        // Search field
        searchField = createRoundedSearchField();
        searchField.setBounds(50, 115, FINAL_WIDTH - 100, 40);
        contentPanel.add(searchField);
        
        // Filter Panel
        filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setBounds(50, 165, FINAL_WIDTH - 100, 50);
        filterPanel.setBackground(Color.WHITE);
        contentPanel.add(filterPanel);
        
        // Filter icon
        JLabel filterIcon = new JLabel("\u25BC Filter");
        filterIcon.setFont(new Font("Dialog", Font.PLAIN, 14));
        filterPanel.add(filterIcon);
        
        // Filter buttons
        filterSepatuBtn = createFilterButton("Sepatu");
        filterSandalBtn = createFilterButton("Sandal");
        filterKaosKakiBtn = createFilterButton("Kaos Kaki");
        filterLainnyaBtn = createFilterButton("Lainnya");
        
        filterPanel.add(filterSepatuBtn);
        filterPanel.add(filterSandalBtn);
        filterPanel.add(filterKaosKakiBtn);
        filterPanel.add(filterLainnyaBtn);
        
        // Item List Panel
        JPanel itemListPanel = new JPanel();
        itemListPanel.setLayout(null);
        itemListPanel.setBounds(50, 220, FINAL_WIDTH - 100, 380);
        itemListPanel.setBackground(Color.WHITE);
        contentPanel.add(itemListPanel);
        
        // Items with red indicators and count
        addItemRow(itemListPanel, "Toe Sock Kulit", "3", 0);
        addItemRow(itemListPanel, "Sandal Kulit Hitam", "5", 1);
        
        // Placeholder items (gray lines)
        for (int i = 2; i < 7; i++) {
            addPlaceholderRow(itemListPanel, i);
        }
    }
    
    private JTextField createRoundedSearchField() {
        JTextField searchField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
            }
        };
        searchField.setBorder(new RoundedBorder(20, Color.LIGHT_GRAY, 1));
        searchField.setOpaque(false);
        searchField.setText("Search");
        searchField.setForeground(Color.GRAY);
        searchField.setFont(new Font("Dialog", Font.PLAIN, 14));
        
        // Search icon on the right
        JLabel searchIcon = new JLabel("\u26B2");
        searchIcon.setFont(new Font("Dialog", Font.PLAIN, 14));
        searchField.setLayout(new BorderLayout());
        searchField.add(searchIcon, BorderLayout.EAST);
        
        return searchField;
    }
    
    private JButton createFilterButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Dialog", Font.PLAIN, 12));
        button.setPreferredSize(new Dimension(80, 30));
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        
        // Apply RoundedButtonLaporan UI
        RoundedButtonLaporan buttonUI = new RoundedButtonLaporan();
        buttonUI.setBorderRadius(15);
        buttonUI.setBorderColor(Color.LIGHT_GRAY);
        button.setUI(buttonUI);
        
        return button;
    }
    
    private void addItemRow(JPanel parent, String itemName, String count, int index) {
        int yPos = index * 60;
        int panelHeight = 50;
        
        JPanel itemPanel = new JPanel(null);
        itemPanel.setBounds(0, yPos, FINAL_WIDTH - 100, panelHeight);
        itemPanel.setBackground(Color.WHITE);
        parent.add(itemPanel);
        
        // Red indicator
        JPanel redIndicator = new JPanel();
        redIndicator.setBounds(0, 15, 5, 20);
        redIndicator.setBackground(RED_COLOR);
        itemPanel.add(redIndicator);
        
        // Item name
        JLabel nameLabel = new JLabel(itemName);
        nameLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
        nameLabel.setBounds(15, 10, 200, 30);
        itemPanel.add(nameLabel);
        
        // Count in square
        JPanel countPanel = new JPanel(new BorderLayout());
        countPanel.setBounds(FINAL_WIDTH - 150, 10, 40, 30);
        countPanel.setBackground(Color.WHITE);
        countPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        JLabel countLabel = new JLabel(count);
        countLabel.setHorizontalAlignment(SwingConstants.CENTER);
        countLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        countLabel.setForeground(RED_COLOR);
        countPanel.add(countLabel, BorderLayout.CENTER);
        
        itemPanel.add(countPanel);
        
        // Bottom separator line
        JSeparator separator = new JSeparator();
        separator.setBounds(0, panelHeight - 1, FINAL_WIDTH - 100, 1);
        separator.setForeground(Color.LIGHT_GRAY);
        itemPanel.add(separator);
    }
    
    private void addPlaceholderRow(JPanel parent, int index) {
        int yPos = index * 60;
        int panelHeight = 50;
        
        JPanel placeholderPanel = new JPanel(null);
        placeholderPanel.setBounds(0, yPos, FINAL_WIDTH - 100, panelHeight);
        placeholderPanel.setBackground(Color.WHITE);
        parent.add(placeholderPanel);
        
        // Gray placeholder line for name
        JPanel namePlaceholder = new JPanel();
        namePlaceholder.setBounds(15, 20, 200, 10);
        namePlaceholder.setBackground(new Color(220, 220, 220));
        placeholderPanel.add(namePlaceholder);
        
        // Gray placeholder line for count
        JPanel countPlaceholder = new JPanel();
        countPlaceholder.setBounds(FINAL_WIDTH - 150, 15, 40, 20);
        countPlaceholder.setBackground(new Color(220, 220, 220));
        placeholderPanel.add(countPlaceholder);
        
        // Bottom separator line
        JSeparator separator = new JSeparator();
        separator.setBounds(0, panelHeight - 1, FINAL_WIDTH - 100, 1);
        separator.setForeground(Color.LIGHT_GRAY);
        placeholderPanel.add(separator);
    }

    private JButton createCloseButton() {
        JButton closeButton = new JButton("×");
        closeButton.setFont(new Font("Poppins", Font.BOLD, 20));
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setForeground(Color.BLACK);
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> startCloseAnimation());
        return closeButton;
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

    // RoundedPanel Inner Class
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
}