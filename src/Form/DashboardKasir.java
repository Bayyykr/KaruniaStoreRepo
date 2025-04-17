package Form;

import SourceCode.RoundedBorder;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.text.SimpleDateFormat;
import java.util.Date;
import produk.ComboboxCustom;

public class DashboardKasir extends JPanel {
    
    public DashboardKasir() {
        setLayout(new BorderLayout());
        setOpaque(true);
        initComponents();
    }

    private void initComponents() {
        removeAll();

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(true);

        // Buat single header panel
        JPanel headerPanel = createSingleHeader();
        headerPanel.setPreferredSize(new Dimension(800, 233));

        // Tambahkan header ke main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Buat content panel dengan BorderLayout
        JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
        contentPanel.setBackground(Color.white);

        // Buat panel wrapper dengan GridLayout untuk 2 panel yang sejajar
        JPanel panelWrapper = new JPanel(new GridLayout(1, 2, 20, 0));
        panelWrapper.setOpaque(false);
        panelWrapper.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Panel Stok Harian
        JPanel stockPanel = createInventoryStockPanel();
        
        // Panel Data Absen
        JPanel absenPanel = createAbsenPanel();
        
        // Tambahkan kedua panel ke wrapper
        panelWrapper.add(stockPanel);
        panelWrapper.add(absenPanel);
        
        // Tambahkan wrapper ke content panel
        contentPanel.add(panelWrapper, BorderLayout.CENTER);
        
        // Tambahkan content panel ke main panel
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createSingleHeader() {
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, getWidth(), getHeight());
                try {
                    BufferedImage bgImage = ImageIO.read(getClass().getResourceAsStream("/SourceImage/slide-terlaris.png"));
                    // Gambar untuk mengisi seluruh panel, dengan margin kecil
                    g.drawImage(bgImage, 25, 0, getWidth() - 50, getHeight(), this);
                } catch (Exception e) {
                    // Fallback jika gambar tidak ditemukan
                    g.setColor(new Color(200, 220, 255));
                    g.fillRect(0, 0, getWidth(), getHeight());
                    e.printStackTrace();
                }
            }
        };

        headerPanel.setLayout(null);
        headerPanel.setPreferredSize(new Dimension(800, 233));

        JLabel welcomeLabel = new JLabel("Welcome Zaynie.zayne");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 30));
        welcomeLabel.setForeground(Color.white);
        welcomeLabel.setBounds(80, 50, 400, 40);

        // Tetapkan posisi dan ukuran tombol
        JButton reportButton = createRegularButton("BARANG TERLARIS", new Dimension(320, 50), 80, 140, true, "/SourceImage/next-icon-dark.png");
        reportButton.addActionListener(e -> {
            System.out.println("ini button barang terlaris");
        });

        headerPanel.add(welcomeLabel);
        headerPanel.add(reportButton);

        return headerPanel;
    }

    // Membuat button reguler (tanpa animasi skala)
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
        // Panel utama dengan padding dan border melengkung
        JPanel stockPanel = new JPanel();
        stockPanel.setLayout(new BorderLayout(0, 5));
        stockPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(50, new Color(220, 220, 220), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        stockPanel.setBackground(Color.white);

        // Label judul
        JLabel titleLabel = new JLabel("Stok Harian");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        stockPanel.add(titleLabel, BorderLayout.NORTH);

        // Panel untuk item produk
        JPanel itemsContainer = new JPanel();
        itemsContainer.setLayout(new BoxLayout(itemsContainer, BoxLayout.Y_AXIS));
        itemsContainer.setBackground(Color.WHITE);

        itemsContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Data produk
        String[][] data = {
            {"Sepatu", "115", "shoes-icon.png"},
            {"Sandal", "105", "sandal-icon.png"},
            {"Kaos kaki", "50", "sabuk-icon.png"},
            {"Produk Lainnya", "29", "produk-lainnya-icon.png"}
        };

        // Buat setiap baris produk
        for (int i = 0; i < data.length; i++) {
            JPanel itemPanel = createProductRow(data[i][0], data[i][1], data[i][2]);
            itemsContainer.add(itemPanel);

            // Tambahkan jarak antar baris
            if (i < data.length - 1) {
                itemsContainer.add(Box.createVerticalStrut(10));
            }
        }

        stockPanel.add(itemsContainer, BorderLayout.CENTER);
        return stockPanel;
    }

    private JPanel createProductRow(String productName, String quantity, String iconPath) {
        // Panel baris utama dengan border melengkung
        JPanel rowPanel = new JPanel();
        rowPanel.setLayout(new BorderLayout());
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, new Color(0, 0, 0), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Panel kiri untuk ikon dan nama produk
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
        leftPanel.setBackground(Color.WHITE);

        // Tambahkan ikon produk
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/SourceImage/" + iconPath));
            // Skala ikon ke ukuran yang sesuai (24x24 piksel)
            Image scaledImage = originalIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            JLabel iconLabel = new JLabel(new ImageIcon(scaledImage));
            iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
            leftPanel.add(iconLabel);
        } catch (Exception e) {
            // Fallback jika ikon tidak ditemukan - buat persegi kecil berwarna sebagai placeholder
            JPanel iconPlaceholder = new JPanel();
            iconPlaceholder.setBackground(new Color(230, 230, 230));
            iconPlaceholder.setPreferredSize(new Dimension(24, 24));
            iconPlaceholder.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
            leftPanel.add(iconPlaceholder);
            System.err.println("Icon not found: " + iconPath);
        }

        // Label nama produk
        JLabel nameLabel = new JLabel(productName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        leftPanel.add(nameLabel);

        // Panel kanan untuk jumlah dan tombol menu
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.X_AXIS));
        rightPanel.setBackground(Color.WHITE);

        // Field jumlah dengan border
        JLabel quantityField = new JLabel(quantity);
        quantityField.setFont(new Font("Arial", Font.PLAIN, 14));
        quantityField.setHorizontalAlignment(SwingConstants.CENTER);
        quantityField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        quantityField.setBackground(Color.WHITE);
        quantityField.setOpaque(true);

        // Tombol menu (menggunakan ikon dari PNG)
        JButton menuButton = new JButton();
        try {
            // Muat gambar ikon
            BufferedImage iconImage = ImageIO.read(getClass().getResourceAsStream("/SourceImage/titik 3 sepatu.png"));
            // Skala gambar agar sesuai dengan tombol
            Image scaledImage = iconImage.getScaledInstance(4, 17, Image.SCALE_SMOOTH);
            menuButton.setIcon(new ImageIcon(scaledImage));
        } catch (IOException e) {
            // Fallback ke teks jika gambar tidak dapat dimuat
            menuButton.setText("⋮");
            menuButton.setFont(new Font("Arial", Font.BOLD, 16));
            menuButton.setForeground(new Color(100, 100, 100));
            e.printStackTrace();
        }

        menuButton.setBorderPainted(false);
        menuButton.setContentAreaFilled(false);
        menuButton.setFocusPainted(false);
        menuButton.setPreferredSize(new Dimension(30, 30));
        menuButton.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        rightPanel.add(quantityField);
        rightPanel.add(Box.createHorizontalStrut(10));
        rightPanel.add(menuButton);

        // Tambahkan komponen ke panel baris
        rowPanel.add(leftPanel, BorderLayout.WEST);
        rowPanel.add(rightPanel, BorderLayout.EAST);

        // Tetapkan tinggi tetap untuk ukuran baris yang konsisten
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        rowPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 60));

        return rowPanel;
    }
    
  private JPanel createAbsenPanel() {
    // Panel utama dengan padding dan border melengkung
    JPanel absenPanel = new JPanel();
    absenPanel.setLayout(new BorderLayout(0, 5));
    absenPanel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(50, new Color(220, 220, 220), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
    ));
    absenPanel.setBackground(Color.white);

    // Panel header dengan judul dan dropdown
    JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
    headerPanel.setBackground(Color.WHITE);
    
    // Label judul
    JLabel titleLabel = new JLabel("Data Absen");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
    titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    
    // Dropdown bulan - gunakan ComboboxCustom
    ComboboxCustom monthComboBox = new ComboboxCustom(new String[]{"Pilih Bulan","Bulan Ini", "Bulan Lalu"});
    monthComboBox.setPreferredSize(new Dimension(150, 30));
    
    headerPanel.add(titleLabel, BorderLayout.WEST);
    headerPanel.add(monthComboBox, BorderLayout.EAST);
    
    // Tambahkan header ke panel utama
    absenPanel.add(headerPanel, BorderLayout.NORTH);

    // Panel untuk item absensi
    JPanel itemsContainer = new JPanel();
    itemsContainer.setLayout(new BoxLayout(itemsContainer, BoxLayout.Y_AXIS));
    itemsContainer.setBackground(Color.WHITE);
    itemsContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

    // Data absensi
    String[][] data = {
        {"Hadir", "20"},
        {"Tidak Hadir", "0"}
    };

    // Buat setiap baris absensi
    for (int i = 0; i < data.length; i++) {
        JPanel itemPanel = createAbsenRow(data[i][0], data[i][1]);
        itemsContainer.add(itemPanel);

        // Tambahkan jarak antar baris
        if (i < data.length - 1) {
            itemsContainer.add(Box.createVerticalStrut(10));
        }
    }
    
    // Panel tanggal gajian
    JPanel tanggalGajianPanel = createTanggalGajianRow();
    itemsContainer.add(Box.createVerticalStrut(10));
    itemsContainer.add(tanggalGajianPanel);

    absenPanel.add(itemsContainer, BorderLayout.CENTER);
    return absenPanel;
}
  
    private JPanel createAbsenRow(String label, String value) {
        JPanel rowPanel = new JPanel();
        rowPanel.setLayout(new BorderLayout());
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, new Color(0, 0, 0), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Label nama status
        JLabel nameLabel = new JLabel(label);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Panel kanan untuk field nilai dan tombol menu
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.X_AXIS));
        rightPanel.setBackground(Color.WHITE);

        // Field nilai dengan border
        JLabel valueField = new JLabel(value);
        valueField.setFont(new Font("Arial", Font.PLAIN, 14));
        valueField.setHorizontalAlignment(SwingConstants.CENTER);
        valueField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        valueField.setBackground(Color.WHITE);
        valueField.setOpaque(true);

        // Tombol menu (titik tiga)
        JButton menuButton = new JButton();
        try {
            // Muat gambar ikon
            BufferedImage iconImage = ImageIO.read(getClass().getResourceAsStream("/SourceImage/titik 3 sepatu.png"));
            // Skala gambar agar sesuai dengan tombol
            Image scaledImage = iconImage.getScaledInstance(4, 17, Image.SCALE_SMOOTH);
            menuButton.setIcon(new ImageIcon(scaledImage));
        } catch (IOException e) {
            // Fallback ke teks jika gambar tidak dapat dimuat
            menuButton.setText("⋮");
            menuButton.setFont(new Font("Arial", Font.BOLD, 16));
            menuButton.setForeground(new Color(100, 100, 100));
            e.printStackTrace();
        }

        menuButton.setBorderPainted(false);
        menuButton.setContentAreaFilled(false);
        menuButton.setFocusPainted(false);
        menuButton.setPreferredSize(new Dimension(30, 30));
        menuButton.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        rightPanel.add(valueField);
        rightPanel.add(Box.createHorizontalStrut(10));
        rightPanel.add(menuButton);

        // Tambahkan komponen ke panel baris
        rowPanel.add(nameLabel, BorderLayout.WEST);
        rowPanel.add(rightPanel, BorderLayout.EAST);

        // Tetapkan tinggi tetap untuk ukuran baris yang konsisten
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        rowPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 60));

        return rowPanel;
    }
    
 private JPanel createTanggalGajianRow() {
    JPanel rowPanel = new JPanel();
    rowPanel.setLayout(new BorderLayout());
    rowPanel.setBackground(Color.WHITE);
    rowPanel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(10, new Color(0, 0, 0), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
    ));
    
    // Panel utama dengan layout yang lebih fleksibel
    JPanel mainContent = new JPanel();
    mainContent.setLayout(null); // Menggunakan null layout untuk kontrol posisi absolut
    mainContent.setBackground(Color.WHITE);
    
    // Label tanggal gajian
    JLabel nameLabel = new JLabel("Tanggal Gajian");
    nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
    nameLabel.setBounds(0, 0, 120, 25);
    
    // Indikator merah (bulat) - diposisikan di pojok kanan atas
    JPanel redIndicator = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Color.RED);
            g2d.fillOval(0, 0, 10, 10);
            g2d.dispose();
        }
        
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(10, 10);
        }
    };
    redIndicator.setOpaque(false);
    redIndicator.setBounds(420, 0, 10, 10); // Posisi di pojok kanan atas
    
    // Tanggal saat ini
    SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd/MM/yyyy");
    String currentDate = dateFormat.format(new Date());
    
    // Field tanggal dengan border - diposisikan di bawah label
    JTextField dateField = new JTextField(currentDate);
    dateField.setFont(new Font("Arial", Font.PLAIN, 14));
    dateField.setEditable(false);
    dateField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
    ));
    dateField.setBackground(Color.WHITE);
    dateField.setBounds(0, 30, 320, 30); // Posisi di bawah label
    
    // Tombol Belum
    JButton belumButton = new JButton("Belum");
    belumButton.setFont(new Font("Arial", Font.BOLD, 12));
    belumButton.setBackground(Color.WHITE);
    belumButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
    ));
    belumButton.setFocusPainted(false);
    belumButton.setBounds(345, 30, 80, 30); // Posisi di sebelah kanan field tanggal
    
    // Tambahkan komponen ke panel utama
    mainContent.add(nameLabel);
    mainContent.add(redIndicator);
    mainContent.add(dateField);
    mainContent.add(belumButton);
    
    // Tambahkan panel utama ke panel baris
    rowPanel.add(mainContent, BorderLayout.CENTER);
    // Tetapkan tinggi tetap untuk ukuran baris yang konsisten
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,100));
        rowPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 100));
    
    // Tetapkan ukuran preferensi
    mainContent.setPreferredSize(new Dimension(240, 70));
    rowPanel.setPreferredSize(new Dimension(240, 70));
    
    return rowPanel;
}
}