package Form;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;

import SourceCode.ButtonLingkaran;
import PopUp_all.*;
import SourceCode.RoundedBorder;
import SourceCode.RoundedPanelProduk;
import SourceCode.RoundedButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.plaf.basic.BasicCheckBoxUI;

public class DeleteProductPanel extends JPanel {
   
    private boolean selectMode = false;
    private List<JCheckBox> checkBoxes = new ArrayList<>();
    private List<RoundedPanelProduk> productPanels = new ArrayList<>();
    private JLabel counterLabel;
    private JPanel productListPanel;
    private ButtonLingkaran selectButton;
    private JButton deleteSelectedButton;
    private int selectedCount = 0;
    
    // Method to reset form when panel is changed or closed
    public void prepareForPanelChange() {
        clearForm();
        // Additional cleanup if needed
    }

    private void clearForm() {
    // Reset the selection state
    selectMode = false;
    selectedCount = 0;
    if (counterLabel != null) {
        counterLabel.setText("0 Product Selected");
    }
    updateButtonsForSelectionMode();
    
    // Reset all checkboxes and panel colors
    for (int i = 0; i < checkBoxes.size(); i++) {
        checkBoxes.get(i).setSelected(false);
        if (i < productPanels.size()) {
            productPanels.get(i).setBackground(Color.WHITE);
            productPanels.get(i).repaint();
        }
    }
}

    private ImageIcon loadIcon(String path, int size) {
        try {
            // Try multiple loading methods
            URL resourceURL = getClass().getResource(path);
            URL contextURL = this.getClass().getClassLoader().getResource(path);

            ImageIcon icon = null;
            if (resourceURL != null) {
                icon = new ImageIcon(resourceURL);
            } else if (contextURL != null) {
                icon = new ImageIcon(contextURL);
            } else {
                File file = new File(path);
                if (file.exists()) {
                    icon = new ImageIcon(file.getAbsolutePath());
                }
            }

            if (icon == null || icon.getImage() == null) {
                System.err.println("Failed to load icon: " + path);
                return null;
            }

            Image scaledImage = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.err.println("Error loading icon: " + path);
            e.printStackTrace();
            return null;
        }
    }

    public DeleteProductPanel() {
        // Gunakan GridBagLayout untuk fleksibilitas
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();

        // Atur layout untuk mengisi ruang horizontal dan vertikal
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Pastikan komponen mengambil seluruh lebar

        // Top Panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 15, 10, 40);
        add(createTopPanel(), gbc);

        // Search Panel
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 15, 10, 40);
        add(createSearchPanel(), gbc);

        // Product List Panel
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 15, 15, 40); // Tambahkan margin bawah
        add(createProductListPanel(), gbc);
    }

    private JPanel createTopPanel() {
        RoundedPanelProduk topPanel = new RoundedPanelProduk(15);
        topPanel.setLayout(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        // Label judul
        JLabel titleLabel = new JLabel("Delete / Hapus Produk");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 20)); // Increased left padding

        // Label untuk menampilkan jumlah produk yang dipilih
        counterLabel = new JLabel("0 Product Selected");
        counterLabel.setFont(new Font("Arial", Font.BOLD, 14));
        counterLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        counterLabel.setVisible(false); // Awalnya tidak terlihat

        // Panel untuk tombol
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10)); // Added horizontal and vertical gap
        buttonPanel.setOpaque(false);

        // Tombol delete selected dengan ikon
        deleteSelectedButton = new JButton();
        try {
            URL iconUrl = getClass().getResource("/SourceImage/icon/icon_sampah_putih.png");
            if (iconUrl != null) {
                ImageIcon deleteIcon = new ImageIcon(iconUrl);
                Image img = deleteIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                deleteIcon = new ImageIcon(img);
                deleteSelectedButton.setIcon(deleteIcon);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
     // Dalam method createTopPanel(), ganti implementasi tombol deleteSelectedButton:
deleteSelectedButton = new ButtonLingkaran(20, new Color(64, 72, 82));
deleteSelectedButton.setText("");
deleteSelectedButton.setForeground(Color.WHITE);
deleteSelectedButton.setBackground(new Color(64, 72, 82));
deleteSelectedButton.setPreferredSize(new Dimension(35, 35));
        deleteSelectedButton.setBorder(new RoundedBorder(20, new Color(64, 72, 82), 0));
        deleteSelectedButton.setVisible(false); // Awalnya tidak terlihat
try {
    URL iconUrl = getClass().getResource("/SourceImage/icon/icon_sampah_putih.png");
    if (iconUrl != null) {
        ImageIcon deleteIcon = new ImageIcon(iconUrl);
        Image img = deleteIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        deleteIcon = new ImageIcon(img);
        deleteSelectedButton.setIcon(deleteIcon);
    }
} catch (Exception e) {
    e.printStackTrace();
}
        
        // Tombol Pilih
        selectButton = new ButtonLingkaran(20, new Color(0, 0, 0));
        selectButton.setText("Select Product to Delete");
        selectButton.setForeground(Color.WHITE);
        selectButton.setBackground(new Color(64, 72, 82));
        selectButton.setPreferredSize(new Dimension(200, 35));
        
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectMode = true;
                updateButtonsForSelectionMode();
                refreshProductList();
            }
        });

        try {
            URL iconUrl = getClass().getResource("/SourceImage/icon/icon_sampah_putih.png"); // Ganti dengan path ikon Anda
            if (iconUrl != null) {
                ImageIcon selectIcon = new ImageIcon(iconUrl);
                Image img = selectIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH); // Sesuaikan ukuran
                selectIcon = new ImageIcon(img);
                selectButton.setIcon(selectIcon); // Set ikon pada tombol
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Tombol Tutup dengan Ikon
        ButtonLingkaran closeButton = new ButtonLingkaran(20, Color.RED);

        try {
            URL iconUrl = getClass().getResource("/SourceImage/icon/close.png"); // Ganti dengan path ikon Anda
            if (iconUrl != null) {
                ImageIcon closeIcon = new ImageIcon(iconUrl);
                Image img = closeIcon.getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH); // Sesuaikan ukuran
                closeIcon = new ImageIcon(img);

                closeButton.setIcon(closeIcon); // Set ikon pada tombol
                closeButton.setText(""); // Hapus teks
            }
        } catch (Exception e) {
            e.printStackTrace();
            closeButton.setText("✕"); // Jika ikon gagal dimuat, gunakan simbol default
        }
        closeButton.setBackground(Color.RED);
        closeButton.setForeground(Color.WHITE);
        closeButton.setPreferredSize(new Dimension(35, 35));
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectMode) {
                    // Jika dalam mode select, tutup mode selection
                    selectMode = false;
                    selectedCount = 0;
                    counterLabel.setText("0 Product Selected");
                    updateButtonsForSelectionMode();
                    refreshProductList();
                } else {
                    // Reset the form before showing the exit popup
                    prepareForPanelChange();

                    // Gunakan SwingUtilities untuk mencari parent frame
                    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(DeleteProductPanel.this);
                    ((Productt) parentFrame).switchBackToProductPanel();
                }
            }
        });
        
        // Add action listener for delete selected button
        deleteSelectedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implementasi penghapusan produk yang terpilih
                JOptionPane.showMessageDialog(DeleteProductPanel.this, 
                                              "Menghapus " + selectedCount + " produk terpilih", 
                                              "Delete Confirmation", 
                                              JOptionPane.INFORMATION_MESSAGE);
                
                // Setelah menghapus, reset mode pilihan
                selectMode = false;
                selectedCount = 0;
                counterLabel.setText("0 Product Selected");
                updateButtonsForSelectionMode();
                refreshProductList();
            }
        });
        
        // Panel untuk menampung label counter dan tombol delete
        JPanel selectionPanel = new JPanel(new BorderLayout(10, 0));
        selectionPanel.setOpaque(false);
        selectionPanel.add(counterLabel, BorderLayout.WEST);
        selectionPanel.add(deleteSelectedButton, BorderLayout.EAST);
        
        buttonPanel.add(selectionPanel);
        buttonPanel.add(selectButton);
        buttonPanel.add(closeButton);
        
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        return topPanel;
    }

    private void updateButtonsForSelectionMode() {
    if (selectMode) {
        selectButton.setVisible(false);
        counterLabel.setVisible(true);
        deleteSelectedButton.setVisible(true);
        
        // Refresh untuk menampilkan UI dalam mode select
        refreshProductList();
    } else {
        selectButton.setVisible(true);
        counterLabel.setVisible(false);
        deleteSelectedButton.setVisible(false);
        
        // Reset semua panel
        for (RoundedPanelProduk panel : productPanels) {
            panel.setBackground(Color.WHITE);
            panel.repaint();
        }
        
        // Refresh untuk menampilkan UI dalam mode normal
        refreshProductList();
    }
}
    
    private void refreshProductList() {
    // Hapus semua produk yang ada
    productListPanel.removeAll();
    checkBoxes.clear();
    productPanels.clear();
    
    // Tambahkan ulang produk dengan tampilan sesuai mode
    for (int i = 0; i < 20; i++) {
        RoundedPanelProduk productEntry = createProductEntry();
        productPanels.add(productEntry);
        productListPanel.add(productEntry);
        productListPanel.add(Box.createVerticalStrut(10));
    }
    
    // Refresh tampilan
    productListPanel.revalidate();
    productListPanel.repaint();
}

// Modifikasi pada method createSearchPanel() dalam class DeleteProductPanel

private RoundedPanelProduk createSearchPanel() {
    RoundedPanelProduk searchPanel = new RoundedPanelProduk(15);
    searchPanel.setLayout(new BorderLayout());
    searchPanel.setPreferredSize(new Dimension(400, 40));
    searchPanel.setBackground(Color.WHITE);
    searchPanel.setBorder(new RoundedBorder(30, Color.BLACK, 1));

    // Field pencarian transparan dengan placeholder
    JTextField searchField = new JTextField("Search");
    searchField.setFont(new Font("Arial", Font.PLAIN, 12));
    searchField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    searchField.setOpaque(false);
    searchField.setBackground(new Color(0, 0, 0, 0));
    searchField.setForeground(Color.GRAY); // Warna text placeholder
    
    // Tambahkan listener untuk menangani perilaku placeholder
    searchField.addFocusListener(new java.awt.event.FocusAdapter() {
        @Override
        public void focusGained(java.awt.event.FocusEvent evt) {
            if (searchField.getText().equals("Search")) {
                searchField.setText("");
                searchField.setForeground(Color.BLACK); // Warna text normal
            }
        }
        
        @Override
        public void focusLost(java.awt.event.FocusEvent evt) {
            if (searchField.getText().isEmpty()) {
                searchField.setText("Search");
                searchField.setForeground(Color.GRAY); // Kembali ke warna placeholder
            }
        }
    });

    // Tombol pencarian dengan background hitam
    ButtonLingkaran searchButton = new ButtonLingkaran(5, Color.BLACK);

    try {
        URL iconUrl = getClass().getResource("/SourceImage/icon/icon_search.png");
        if (iconUrl != null) {
            ImageIcon searchIcon = new ImageIcon(iconUrl);
            Image img = searchIcon.getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH);
            searchIcon = new ImageIcon(img);

            // Explicitly set the icon on the button
            searchButton.setIcon(searchIcon);
            searchButton.setText(""); // Remove any text
        }
    } catch (Exception e) {
        e.printStackTrace();
        searchButton.setText("🔍");
    }

    searchButton.setBackground(Color.BLACK);
    searchButton.setForeground(Color.WHITE);
    searchButton.setPreferredSize(new Dimension(30, 25));

    JPanel searchContainer = new JPanel(new BorderLayout());
    searchContainer.setOpaque(false);
    searchContainer.add(searchField, BorderLayout.CENTER);
    searchContainer.add(searchButton, BorderLayout.EAST);

    searchContainer.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 15));

    searchPanel.add(searchContainer, BorderLayout.CENTER);

    return searchPanel;
}

    private JScrollPane createProductListPanel() {
        // Panel daftar produk dengan BoxLayout
        productListPanel = new JPanel();
        productListPanel.setLayout(new BoxLayout(productListPanel, BoxLayout.Y_AXIS));
        productListPanel.setBackground(Color.WHITE); // Set background to white
        productListPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Add some vertical padding

        // Tambahkan entri produk
        for (int i = 0; i < 20; i++) {
            RoundedPanelProduk productEntry = createProductEntry();
            productPanels.add(productEntry);
            productListPanel.add(productEntry);
            productListPanel.add(Box.createVerticalStrut(10));
        }

        // Scroll pane dengan border transparan
        JScrollPane scrollPane = new JScrollPane(productListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove border
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setBackground(Color.WHITE); // Set viewport background to white

        // Hide vertical scrollbar by default
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        // Add listener to show scrollbar only when needed
        SwingUtilities.invokeLater(() -> {
            JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
            verticalScrollBar.setPreferredSize(new Dimension(0, 0)); // Make scrollbar invisible
        });

        return scrollPane;
    }

// Modifikasi createProductEntry() untuk menambahkan fungsi klik
private RoundedPanelProduk createProductEntry() {
    final RoundedPanelProduk productPanel = new RoundedPanelProduk(15);
    productPanel.setLayout(new BorderLayout());
    productPanel.setBackground(Color.WHITE);
    productPanel.setBorder(new RoundedBorder(15, Color.BLACK, 1));
    productPanel.setPreferredSize(new Dimension(0, 50));
    productPanel.setMaximumSize(new Dimension(1100, 50));

    // Tambahkan tombol hitam di sebelah kiri dengan border custom
    ButtonLingkaran leftButton = new ButtonLingkaran(5, Color.BLACK) {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Path untuk rounded kiri saja
            int width = getWidth();
            int height = getHeight();
            int arcSize = 5; // Ukuran rounded corner

            Path2D path = new Path2D.Double();
            path.moveTo(arcSize, 0); // Mulai dari kanan atas
            path.lineTo(width, 0); // Garis ke kanan atas
            path.lineTo(width, height); // Garis ke kanan bawah
            path.lineTo(arcSize, height); // Garis ke kiri bawah

            // Tambahkan curved corner di kiri atas
            path.quadTo(0, 0, 0, arcSize);

            // Tambahkan curved corner di kiri bawah
            path.quadTo(0, height, arcSize, height);

            path.closePath();

            g2.setColor(getBackground());
            g2.fill(path);

            g2.dispose();
            super.paintComponent(g);
        }
    };

    leftButton.setBackground(Color.BLACK);
    leftButton.setPreferredSize(new Dimension(10, 20));
    leftButton.setBorder(null);
    
    // Definisi warna untuk item terpilih
    final Color selectedColor = new Color(220, 230, 255); // Light blue color for selected items
    
    // Buat checkbox (baik untuk mode select maupun non-select)
    final JCheckBox checkBox = new JCheckBox();
    checkBox.setOpaque(false);
    checkBox.setPreferredSize(new Dimension(25, 25));
    
    // Custom renderer untuk checkbox dengan border dan sudut lengkung
    checkBox.setUI(new BasicCheckBoxUI() {
        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            JCheckBox cb = (JCheckBox) c;
            int width = cb.getWidth();
            int height = cb.getHeight();

            // Gambar kotak dengan sudut lengkung 10px dan border
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(3, 3, 20, 20, 10, 10);
            g2.setColor(Color.BLACK);
            g2.drawRoundRect(3, 3, 20, 20, 10, 10);

            // Gambar tanda centang jika dipilih
            if (cb.isSelected()) {
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(3)); // Atur ketebalan garis ceklis
                
                // Gambar ceklis (✔) dengan garis
                g2.drawLine(7, 13, 10, 17); // Garis miring ke bawah
                g2.drawLine(10, 17, 17, 7); // Garis miring ke atas
            }

            g2.dispose();
        }
    });
    
    // Add action listener to checkbox
    checkBox.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (checkBox.isSelected()) {
                productPanel.setBackground(selectedColor);
            } else {
                productPanel.setBackground(Color.WHITE);
            }
            productPanel.repaint();
            updateSelectedCount();
        }
    });
    
    checkBoxes.add(checkBox);
    
    // Tambahkan tombol delete dalam panel terpisah
    ButtonLingkaran deleteButton = new ButtonLingkaran(5, Color.BLACK);
    try {
        URL iconUrl = getClass().getResource("/SourceImage/icon/icon_hapus.png");
        if (iconUrl != null) {
            ImageIcon deleteIcon = new ImageIcon(iconUrl);
            Image img = deleteIcon.getImage().getScaledInstance(23, 23, Image.SCALE_SMOOTH);
            deleteIcon = new ImageIcon(img);
            deleteButton.setIcon(deleteIcon);
            deleteButton.setText("");
        }
    } catch (Exception e) {
        e.printStackTrace();
        deleteButton.setText("🗑");
    }
    deleteButton.setBackground(new Color(255, 59, 48));
    deleteButton.setForeground(Color.WHITE);
    deleteButton.setPreferredSize(new Dimension(30, 25));
    
    // Panel untuk komponenen kanan (checkbox atau delete button)
    JPanel rightComponentPanel = new JPanel(new BorderLayout());
    rightComponentPanel.setOpaque(false);
    rightComponentPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));
    
    // Tampilkan komponen sesuai mode
    if (selectMode) {
        rightComponentPanel.add(checkBox, BorderLayout.CENTER);
    } else {
        rightComponentPanel.add(deleteButton, BorderLayout.CENTER);
    }
    
    // Add components to the content container
    JPanel contentContainer = new JPanel(new BorderLayout());
    contentContainer.setOpaque(false);
    contentContainer.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

    // Label produk dan deskripsi sama seperti sebelumnya
    JLabel productLabel = new JLabel("Adidas Simanjuntak");
    productLabel.setFont(new Font("Arial", Font.BOLD, 12));

    JLabel descriptionLabel = new JLabel("ini deksripsinya");
    descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 10));
    descriptionLabel.setForeground(Color.GRAY);

    JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
    textPanel.setOpaque(false);
    textPanel.add(productLabel);
    textPanel.add(descriptionLabel);
    
    contentContainer.add(textPanel, BorderLayout.CENTER);
    contentContainer.add(rightComponentPanel, BorderLayout.EAST);
    
    // Tambahkan tombol kiri dan container ke panel utama
    productPanel.add(leftButton, BorderLayout.WEST);
    productPanel.add(contentContainer, BorderLayout.CENTER);
    
    // Tambahkan mouse listener untuk panel produk
    productPanel.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            // Jika dalam mode select, toggle checkbox
            if (selectMode) {
                checkBox.setSelected(!checkBox.isSelected());
                updateSelectedCount();
                
                // Update warna panel
                if (checkBox.isSelected()) {
                    productPanel.setBackground(selectedColor);
                } else {
                    productPanel.setBackground(Color.WHITE);
                }
                productPanel.repaint();
            } else {
                // Jika tidak dalam mode select, aktifkan mode select dan centang item ini
                selectMode = true;
                
                // Simpan indeks panel ini
                final int clickedPanelIndex = productPanels.indexOf(productPanel);
                
                // Update UI untuk mode select
                updateButtonsForSelectionMode();
                
                // Gunakan SwingUtilities.invokeLater untuk menjalankan setelah refresh UI
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (clickedPanelIndex >= 0 && clickedPanelIndex < checkBoxes.size()) {
                            // Centang checkbox yang sesuai
                            JCheckBox newCheckBox = checkBoxes.get(clickedPanelIndex);
                            newCheckBox.setSelected(true);
                            
                            // Update warna panel
                            if (clickedPanelIndex < productPanels.size()) {
                                RoundedPanelProduk newPanel = productPanels.get(clickedPanelIndex);
                                newPanel.setBackground(selectedColor);
                                newPanel.repaint();
                            }
                            
                            // Update counter
                            updateSelectedCount();
                        }
                    }
                });
            }
        }
        
        @Override
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            // Efek hover untuk kedua mode
            if (!selectMode || (selectMode && !checkBox.isSelected())) {
                productPanel.setBackground(new Color(245, 245, 245));
                productPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                productPanel.repaint();
            }
        }
        
        @Override
        public void mouseExited(java.awt.event.MouseEvent evt) {
            // Reset warna saat mouse keluar
            if (selectMode && checkBox.isSelected()) {
                productPanel.setBackground(selectedColor);
            } else {
                productPanel.setBackground(Color.WHITE);
            }
            productPanel.repaint();
        }
    });
    
    return productPanel;
}
    
    private void updateSelectedCount() {
        selectedCount = 0;
        for (JCheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                selectedCount++;
            }
        }
        counterLabel.setText(selectedCount + " Product Selected");
    }
}