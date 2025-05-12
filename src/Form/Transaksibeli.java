package Form;

import SourceCode.PopUp_edittransbeli;
import SourceCode.ScrollPane;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.swing.border.EmptyBorder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import SourceCode.PopUp_transbelihapusdata;
import SourceCode.JTableRounded;
import java.awt.geom.Path2D;
import java.math.BigInteger;

public class Transaksibeli extends JPanel {

    private final DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(new Locale("id", "ID"));
    private JTextField hargaBeliField, scanKodeField, namaProduk, qtyField, sizeProduk;
    private JPanel thisPanel;
    private JTableRounded roundedTable;
    private JButton btnClear, btnTambahBarang, btnCheckout;
    private JLabel totalValueLabel;

    public Transaksibeli() {
        thisPanel = this;
        setPreferredSize(new Dimension(1065, 640));
        setLayout(null);
        setBackground(Color.white);

        // Panel utama - menggunakan seluruh area yang tersedia
        JPanel mainPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 50, 50);
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 50, 50);
            }
        };
        mainPanel.setBounds(0, 0, 1065, 640);
        mainPanel.setOpaque(false);
        add(mainPanel);

        // Label Transaksi Beli - posisi tetap
        JLabel transaksiLabel = new JLabel("Transaksi Beli");
        transaksiLabel.setFont(new Font("Arial", Font.BOLD, 30));
        transaksiLabel.setBounds(30, 50, 250, 30);
        mainPanel.add(transaksiLabel);

        // Field Tanggal otomatis - disesuaikan sedikit ke kanan
        JTextField dateField = createRoundedTextField(850, 30, 180, 30);
        dateField.setText(getCurrentDate());
        dateField.setEditable(false);
        dateField.setFocusable(false);
        dateField.setBounds(835, 30, 200, 30);
        dateField.setBackground(new Color(200, 200, 200));
        mainPanel.add(dateField);

        // Membuat rounded table dengan JTableRounded - diperlebar untuk mengisi space
        String[] columns = {"No", "Kode Produk", "Nama Produk", "Size", "Harga Satuan", "Qty", "Total", "Aksi"};
        roundedTable = new JTableRounded(columns, 750, 445);

        // Mengatur lebar tiap kolom yang lebih proporsional
        roundedTable.setColumnWidth(0, 40);   // No
        roundedTable.setColumnWidth(1, 100);  // Kode Produk
        roundedTable.setColumnWidth(2, 170);  // Nama Produk
        roundedTable.setColumnWidth(3, 50);   // Size
        roundedTable.setColumnWidth(4, 120);  // Harga Satuan
        roundedTable.setColumnWidth(5, 50);   // Qty
        roundedTable.setColumnWidth(6, 120);  // Total
        roundedTable.setColumnWidth(7, 80);   // Aksi

        // Mendapatkan JTable dari JTableRounded untuk kustomisasi lanjutan
        JTable table = roundedTable.getTable();

        // Set table to fill the viewport height
        table.setFillsViewportHeight(true);

        // Menyesuaikan lebar kolom agar lebih proporsional dengan ukuran tabel
        int[] columnWidths = {40, 100, 170, 50, 120, 45, 120, 80};
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        // Mengatur model tabel untuk menyesuaikan dengan JTableRounded
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        tableModel.setRowCount(0); // Hapus semua baris yang mungkin sudah ada

        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setBounds(20, 120, 750, 445); // Diperlebar dan diperpanjang
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.setThumbColor(new Color(80, 80, 80, 180)); // Darker, more opaque scrollbar thumb
        scrollPane.setTrackColor(new Color(240, 240, 240, 80)); // Lighter track
        scrollPane.setThumbThickness(8); // Set the thickness of the scrollbar
        scrollPane.setThumbRadius(8);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainPanel.add(scrollPane);

        // Cell renderer - make panels transparent and icons larger
        table.getColumnModel().getColumn(7).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 3));
                panel.setPreferredSize(new Dimension(80, 30)); // Lebih lebar untuk tombol
                panel.setOpaque(false);
                panel.setBackground(new Color(0, 0, 0, 0));

                // Slightly larger icons - consistent size
                ImageIcon originalIconEdit = new ImageIcon(getClass().getResource("/SourceImage/edit_icon.png"));
                ImageIcon originalIconDelete = new ImageIcon(getClass().getResource("/SourceImage/hapus_icon.png"));

                Image scaledImageEdit = originalIconEdit.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
                Image scaledImageDelete = originalIconDelete.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);

                ImageIcon scaledIconEdit = new ImageIcon(scaledImageEdit);
                ImageIcon scaledIconDelete = new ImageIcon(scaledImageDelete);

                // Edit button dengan custom renderer untuk semua state
                JButton btnEdit = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(255, 187, 85));
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                        super.paintComponent(g2);
                        g2.dispose();
                    }
                };
                btnEdit.setIcon(scaledIconEdit);
                btnEdit.setPreferredSize(new Dimension(30, 26)); // Tombol sedikit lebih besar
                btnEdit.setHorizontalAlignment(SwingConstants.CENTER);
                btnEdit.setVerticalAlignment(SwingConstants.CENTER);
                btnEdit.setVerticalTextPosition(SwingConstants.CENTER);
                btnEdit.setIconTextGap(-2);
                btnEdit.setFocusable(false);
                btnEdit.setContentAreaFilled(false);
                btnEdit.setBorderPainted(false);
                btnEdit.setFocusPainted(false);
                btnEdit.setRolloverEnabled(false);
                btnEdit.setMargin(new Insets(0, 0, 0, 0));
                btnEdit.setBackground(new Color(255, 187, 85));

                // Delete button dengan custom renderer untuk semua state
                JButton btnDelete = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(255, 59, 48));
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                        super.paintComponent(g2);
                        g2.dispose();
                    }
                };
                btnDelete.setIcon(scaledIconDelete);
                btnDelete.setPreferredSize(new Dimension(30, 26)); // Tombol sedikit lebih besar
                btnDelete.setHorizontalAlignment(SwingConstants.CENTER);
                btnDelete.setVerticalAlignment(SwingConstants.CENTER);
                btnDelete.setVerticalTextPosition(SwingConstants.CENTER);
                btnDelete.setIconTextGap(-2);
                btnDelete.setFocusable(false);
                btnDelete.setContentAreaFilled(false);
                btnDelete.setBorderPainted(false);
                btnDelete.setFocusPainted(false);
                btnDelete.setRolloverEnabled(false);
                btnDelete.setMargin(new Insets(0, 0, 0, 0));
                btnDelete.setBackground(new Color(255, 59, 48));

                panel.add(btnEdit);
                panel.add(btnDelete);
                return panel;
            }
        });

        // Cell editor - make panels transparent and ensure buttons trigger popups
        table.getColumnModel().getColumn(7).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            private JPanel panel;
            private JButton btnEdit;
            private JButton btnDelete;
            private int currentRow;
            private boolean isPushed = false;
            private final Component parentComponent = thisPanel; // Store a reference to the parent component

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                currentRow = row;
                isPushed = true;

                panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 3)) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        g.setColor(new Color(0, 0, 0, 0));
                        g.fillRect(0, 0, getWidth(), getHeight());
                        super.paintComponent(g);
                    }
                };
                panel.setPreferredSize(new Dimension(80, 30));
                panel.setOpaque(false);
                panel.setBackground(new Color(0, 0, 0, 0));

                ImageIcon originalIconEdit = new ImageIcon(getClass().getResource("/SourceImage/edit_icon.png"));
                ImageIcon originalIconDelete = new ImageIcon(getClass().getResource("/SourceImage/hapus_icon.png"));

                Image scaledImageEdit = originalIconEdit.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
                Image scaledImageDelete = originalIconDelete.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);

                ImageIcon scaledIconEdit = new ImageIcon(scaledImageEdit);
                ImageIcon scaledIconDelete = new ImageIcon(scaledImageDelete);

                btnEdit = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(255, 187, 85));
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                        super.paintComponent(g2);
                        g2.dispose();
                    }

                    @Override
                    public void setPressedIcon(Icon icon) {
                        // Do nothing - keep the same appearance
                    }

                    @Override
                    public void updateUI() {
                        super.updateUI();
                        setContentAreaFilled(false);
                        setBorderPainted(false);
                        setFocusPainted(false);
                    }

                    @Override
                    public boolean isFocusable() {
                        return false;
                    }
                };
                btnEdit.setIcon(scaledIconEdit);
                btnEdit.setPreferredSize(new Dimension(30, 26));
                btnEdit.setHorizontalAlignment(SwingConstants.CENTER);
                btnEdit.setVerticalAlignment(SwingConstants.CENTER);
                btnEdit.setVerticalTextPosition(SwingConstants.CENTER);
                btnEdit.setIconTextGap(-2);
                btnEdit.setFocusable(false);
                btnEdit.setContentAreaFilled(false);
                btnEdit.setBorderPainted(false);
                btnEdit.setFocusPainted(false);
                btnEdit.setRolloverEnabled(false);
                btnEdit.setMargin(new Insets(0, 0, 0, 0));
                btnEdit.setName("edit");
                btnEdit.setBackground(new Color(255, 187, 85));

                btnEdit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        stopCellEditing();
                        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(parentComponent);
                        PopUp_edittransbeli dialog = new PopUp_edittransbeli(parentFrame);
                        dialog.setVisible(true);
                    }
                });

                btnDelete = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(255, 59, 48));
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                        super.paintComponent(g2);
                        g2.dispose();
                    }

                    @Override
                    public void setPressedIcon(Icon icon) {
                        // Do nothing - keep the same appearance
                    }

                    @Override
                    public void updateUI() {
                        super.updateUI();
                        setContentAreaFilled(false);
                        setBorderPainted(false);
                        setFocusPainted(false);
                    }

                    @Override
                    public boolean isFocusable() {
                        return false;
                    }
                };
                btnDelete.setIcon(scaledIconDelete);
                btnDelete.setPreferredSize(new Dimension(30, 26));
                btnDelete.setHorizontalAlignment(SwingConstants.CENTER);
                btnDelete.setVerticalAlignment(SwingConstants.CENTER);
                btnDelete.setVerticalTextPosition(SwingConstants.CENTER);
                btnDelete.setIconTextGap(-2);
                btnDelete.setFocusable(false);
                btnDelete.setContentAreaFilled(false);
                btnDelete.setBorderPainted(false);
                btnDelete.setFocusPainted(false);
                btnDelete.setRolloverEnabled(false);
                btnDelete.setMargin(new Insets(0, 0, 0, 0));
                btnDelete.setName("delete");
                btnDelete.setBackground(new Color(255, 59, 48));

                btnDelete.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        stopCellEditing();
                        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(parentComponent);
                        PopUp_transbelihapusdata dialog = new PopUp_transbelihapusdata(parentFrame);
                        dialog.setVisible(true);
                    }
                });

                panel.add(btnEdit);
                panel.add(btnDelete);
                return panel;
            }

            @Override
            public Object getCellEditorValue() {
                return isPushed;
            }

            @Override
            public boolean stopCellEditing() {
                isPushed = false;
                return super.stopCellEditing();
            }
        });

       // Panel Form - diposisikan lebih baik di sisi kanan dengan tinggi yang disesuaikan
        JPanel formPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(220, 220, 220, 150));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2.dispose();
            }
        };
        // Tinggi form disesuaikan untuk menampung field Size yang ditambahkan (dari 360 ke 390)
        formPanel.setBounds(800, 130, 235, 390);
        formPanel.setOpaque(false);
        mainPanel.add(formPanel);

        // Jarak antar komponen lebih dipadatkan
        formPanel.add(createLabel("Scan Kode Produk", 15, 10));
        scanKodeField = createRoundedTextField(15, 30, 205, 30);
        scanKodeField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && !Character.isISOControl(c)) {
                    e.consume();
                }
            }
        });
        formPanel.add(scanKodeField);

        formPanel.add(createLabel("Nama Produk", 15, 65));
        namaProduk = createRoundedTextField(15, 85, 205, 30);
        formPanel.add(namaProduk);
        
        formPanel.add(createLabel("Size", 15, 120));
        sizeProduk = createRoundedTextField(15, 140, 205, 30);
        formPanel.add(sizeProduk);

        formPanel.add(createLabel("Harga Beli", 15, 175));
        hargaBeliField = createRoundedTextField(15, 195, 205, 30);
        hargaBeliField.setText("Rp. ");
        hargaBeliField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                enforceRpPrefix();
                formatHargaBeli();
                hitungTotal();
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String hargaInput = hargaBeliField.getText().replace("Rp. ", "").replace(".", "").trim();
                    System.out.println(hargaInput);
                }
            }
        });
        formPanel.add(hargaBeliField);

        formPanel.add(createLabel("Qty", 15, 230));
        qtyField = createRoundedTextField(15, 250, 60, 30);
        qtyField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                String currentText = qtyField.getText().replace(".", "");
                if (!Character.isDigit(c) && !Character.isISOControl(c)) {
                    e.consume();
                    return;
                }
                if (currentText.length() >= 3 && Character.isDigit(c)) {
                    e.consume();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                formatQty();
                hitungTotal();
            }
        });
        formPanel.add(qtyField);

        // Posisi tombol Clear dan Tambah disesuaikan
        btnClear = new JButton("CLEAR") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 59, 48)); // Warna merah
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                g2.setColor(Color.black);
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                // No border
            }
        };
        btnClear.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                btnClear.setLocation(btnClear.getX(), btnClear.getY() + 2);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                btnClear.setLocation(btnClear.getX(), btnClear.getY() - 2);
            }
        });
        btnClear.setBounds(15, 290, 100, 35);
        btnClear.setOpaque(false);
        btnClear.setContentAreaFilled(false);
        btnClear.setBorderPainted(false);
        btnClear.setForeground(Color.WHITE);
        btnClear.setFont(new Font("Arial", Font.BOLD, 14));
        btnClear.setFocusPainted(false);
        btnClear.addActionListener(e -> {
            scanKodeField.setText("");
            namaProduk.setText("");
            sizeProduk.setText("");
            hargaBeliField.setText("Rp. ");
            qtyField.setText("");
        });
        formPanel.add(btnClear);

        // Posisi tombol Tambah Barang disesuaikan
        btnTambahBarang = new JButton("TAMBAH") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 122, 255)); // Warna biru
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                g2.setColor(Color.black);
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                // No border
            }
        };
        btnTambahBarang.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                btnTambahBarang.setLocation(btnTambahBarang.getX(), btnTambahBarang.getY() + 2);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                btnTambahBarang.setLocation(btnTambahBarang.getX(), btnTambahBarang.getY() - 2);
            }
        });
        btnTambahBarang.setBounds(120, 290, 100, 35);
        btnTambahBarang.setOpaque(false);
        btnTambahBarang.setContentAreaFilled(false);
        btnTambahBarang.setBorderPainted(false);
        btnTambahBarang.setForeground(Color.WHITE);
        btnTambahBarang.setFont(new Font("Arial", Font.BOLD, 14));
        btnTambahBarang.setFocusPainted(false);
        btnTambahBarang.addActionListener(e -> addItemToTable());
        formPanel.add(btnTambahBarang);

        // Panel total disesuaikan 
        JPanel totalPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(60, 63, 65));

                int width = getWidth();
                int height = getHeight();
                int arcWidth = 15;
                int arcHeight = 15;

                Path2D path = new Path2D.Float();
                path.moveTo(0, 0);
                path.lineTo(width, 0);
                path.lineTo(width, height - arcHeight);
                path.quadTo(width, height, width - arcWidth, height);
                path.lineTo(arcWidth, height);
                path.quadTo(0, height, 0, height - arcHeight);
                path.closePath();

                Path2D borderPath = new Path2D.Float();
                borderPath.moveTo(0, 0);
                borderPath.lineTo(width - 1, 0);
                borderPath.lineTo(width - 1, height - arcHeight);
                borderPath.quadTo(width - 1, height - 1, width - arcWidth, height - 1);
                borderPath.lineTo(arcWidth, height - 1);
                borderPath.quadTo(0, height - 1, 0, height - arcHeight);
                borderPath.closePath();

                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(3.0f));
                g2.draw(borderPath);

                g2.fill(path);
                g2.dispose();
            }
        };
        totalPanel.setBounds(0, 330, 235, 60);
        totalPanel.setOpaque(false);
        formPanel.add(totalPanel);

        JLabel totalLabel = new JLabel("Total");
        totalLabel.setForeground(Color.WHITE);
        totalLabel.setFont(new Font("Poppins", Font.PLAIN, 14));
        totalLabel.setBounds(20, -45, 80, 140);
        totalPanel.add(totalLabel);

        totalValueLabel = new JLabel("Rp. ");
        totalValueLabel.setForeground(Color.WHITE);
        totalValueLabel.setFont(new Font("Poppins", Font.PLAIN, 14));
        totalValueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        totalValueLabel.setBounds(80, -45, 140, 140);
        totalPanel.add(totalValueLabel);

        // Posisi tombol checkout disesuaikan
        btnCheckout = new JButton("CHECKOUT") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(52, 199, 89));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                g2.setColor(Color.black);
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                // No border
            }
        };
        // Posisi tombol checkout disesuaikan dengan panel form yang baru
        btnCheckout.setBounds(800, 530, 235, 40);
        btnCheckout.setOpaque(false);
        btnCheckout.setContentAreaFilled(false);
        btnCheckout.setBorderPainted(false);
        btnCheckout.setForeground(Color.WHITE);
        btnCheckout.setFont(new Font("Arial", Font.BOLD, 14));
        btnCheckout.setFocusPainted(false);
        btnCheckout.addActionListener(e -> {
            // Check if there are items in the table
            DefaultTableModel model = (DefaultTableModel) roundedTable.getTable().getModel();
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(thisPanel, "Tidak ada item yang ditambahkan", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Show confirmation dialog
            int option = JOptionPane.showConfirmDialog(thisPanel, 
                    "Konfirmasi checkout transaksi pembelian?", 
                    "Konfirmasi", 
                    JOptionPane.YES_NO_OPTION);
            
            if (option == JOptionPane.YES_OPTION) {
                // Clear the table after checkout
                model.setRowCount(0);
                totalValueLabel.setText("Rp. ");
                JOptionPane.showMessageDialog(thisPanel, "Transaksi pembelian berhasil dicatat", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnCheckout.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                btnCheckout.setLocation(btnCheckout.getX(), btnCheckout.getY() + 2);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                btnCheckout.setLocation(btnCheckout.getX(), btnCheckout.getY() - 2);
            }
        });

        mainPanel.add(btnCheckout);
    } 

    private void formatQty() {
        try {
            String text = qtyField.getText().replace(".", "").trim();
            if (text.isEmpty()) {
                return;
            }

            int value = Integer.parseInt(text);
            qtyField.setText(formatter.format(value));
        } catch (NumberFormatException e) {
            qtyField.setText("");
        }
    }

    private void formatHargaBeli() {
        try {
            String text = hargaBeliField.getText().replace("Rp. ", "").replace(".", "").trim();
            if (text.isEmpty()) {
                return;
            }

            int value = Integer.parseInt(text);
            hargaBeliField.setText("Rp. " + formatter.format(value));
        } catch (NumberFormatException e) {
            hargaBeliField.setText("Rp. ");
        }
    }

    private void hitungTotal() {
        try {
            String hargaText = hargaBeliField.getText().replace("Rp. ", "").replace(".", "").trim();
            if (hargaText.isEmpty() || qtyField.getText().isEmpty()) {
                return;
            }

            BigInteger harga = new BigInteger(hargaText);
            BigInteger qty = new BigInteger(qtyField.getText().replace(".", ""));

            BigInteger total = harga.multiply(qty);
            totalValueLabel.setText("Rp. " + formatter.format(total));
        } catch (NumberFormatException e) {
            totalValueLabel.setText("Rp. ");
        }
    }

    private void enforceRpPrefix() {
        if (!hargaBeliField.getText().startsWith("Rp. ")) {
            hargaBeliField.setText("Rp. ");
        }
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(new Date());
    }

    private JTextField createRoundedTextField(int x, int y, int width, int height) {
        return new JTextField() {
            private final int cornerRadius = 15;

            {
                setBounds(x, y, width, height);
                setOpaque(false);
                setBorder(new EmptyBorder(5, 10, 5, 10));
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
                g2.setColor(Color.GRAY);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
                g2.dispose();
                super.paintComponent(g);
            }
        };
    }

    private JLabel createLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, 150, 20);
        return label;
    }

    private void addItemToTable() {
    String kodeProduk = scanKodeField.getText();
    String produkName = namaProduk.getText();
    String size = sizeProduk.getText(); // Get the size value
    String hargaText = hargaBeliField.getText().replace("Rp. ", "").replace(".", "").trim();
    
    if (kodeProduk.isEmpty() || produkName.isEmpty() || hargaText.isEmpty() || qtyField.getText().isEmpty()) {
        JOptionPane.showMessageDialog(thisPanel, "Harap lengkapi semua field", "Peringatan", JOptionPane.WARNING_MESSAGE);
        return;
    }

    try {
        int harga = Integer.parseInt(hargaText);
        int qty = Integer.parseInt(qtyField.getText().replace(".", ""));
        int total = harga * qty;

        DefaultTableModel model = (DefaultTableModel) roundedTable.getTable().getModel();
        int rowCount = model.getRowCount() + 1;

        model.addRow(new Object[]{
            rowCount,
            kodeProduk,
            produkName,
            size, // Add the size value to the table
            "Rp. " + formatter.format(harga),
            formatter.format(qty),
            "Rp. " + formatter.format(total),
            "" // Action column
        });

        // Clear fields after adding
        scanKodeField.setText("");
        namaProduk.setText("");
        sizeProduk.setText(""); // Clear the size field
        hargaBeliField.setText("Rp. ");
        qtyField.setText("");
        
        // Update total
        updateTotalAmount();
        
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(thisPanel, "Format harga atau qty tidak valid", "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
    private void updateTotalAmount() {
        double totalAmount = 0;
        DefaultTableModel model = (DefaultTableModel) roundedTable.getTable().getModel();

        for (int i = 0; i < model.getRowCount(); i++) {
            String totalStr = model.getValueAt(i, 6).toString().replace("Rp. ", "").replace(".", "").replace(",", "");
            try {
                totalAmount += Double.parseDouble(totalStr);
            } catch (NumberFormatException ex) {
                System.err.println("Error parsing total amount: " + ex.getMessage());
            }
        }

        totalValueLabel.setText("Rp. " + formatter.format(totalAmount));
    }
}