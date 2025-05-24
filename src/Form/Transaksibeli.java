package Form;

import PopUp_all.PindahanAntarPopUp;
import PopUp_all.PopUp_BayarTransjual;
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
import SourceCode.PopUp_transbelihapusdata.DeleteCallback;
import java.awt.geom.Path2D;
import java.math.BigInteger;
import db.conn;
import java.sql.*;
import java.util.EventObject;
import javax.swing.table.TableColumnModel;

public class Transaksibeli extends JPanel {

    private JFrame parentFrame;
    Component parentComponent = this;
    private final DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(new Locale("id", "ID"));
    private JTextField hargaBeliField, scanKodeField, namaProduk, qtyField, sizeProduk, txtIdTransaksi;
    private JPanel thisPanel;
    private JTableRounded roundedTable;
    private JButton btnClear, btnTambahBarang, btnCheckout;
    private JLabel totalValueLabel;
    private Connection con;
    private String NoRFID, namaUser;
    private static final String ID_PREFIX = "TRBL_";
    private static final int PADDING_LENGTH = 5;
    private String currentProductSize = "";

    public Transaksibeli() {
        thisPanel = this;
        setPreferredSize(new Dimension(1065, 640));
        setLayout(null);
        setBackground(Color.white);

        con = conn.getConnection();
        setNamaUser();

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
        JLabel transaksiLabel = new JLabel("Restok");
        transaksiLabel.setFont(new Font("Arial", Font.BOLD, 35));
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

        txtIdTransaksi = new JTextField();
        txtIdTransaksi.setText(generateNextTransaksiId());
        txtIdTransaksi.setEditable(false);
        txtIdTransaksi.setBounds(300, 50, 235, 40);
        txtIdTransaksi.setVisible(false);
        mainPanel.add(txtIdTransaksi);

        // Membuat rounded table dengan JTableRounded - diperlebar untuk mengisi space
        String[] columns = {"No", "Nama Produk", "Size", "Harga Satuan", "Qty", "Total", "Aksi"};
        roundedTable = new JTableRounded(columns, 750, 445);

        // Mengatur lebar tiap kolom yang lebih proporsional
        roundedTable.setColumnWidth(0, 40);   // No
        roundedTable.setColumnWidth(1, 220);  // Nama Produk
        roundedTable.setColumnWidth(2, 50);   // Size
        roundedTable.setColumnWidth(3, 170);  // Harga Satuan
        roundedTable.setColumnWidth(4, 50);   // Qty
        roundedTable.setColumnWidth(5, 120);  // Total
        roundedTable.setColumnWidth(6, 80);   // Aksi

        // Mendapatkan JTable dari JTableRounded untuk kustomisasi lanjutan
        JTable table = roundedTable.getTable();

        // Set table to fill the viewport height
        table.setFillsViewportHeight(true);

        // Menyesuaikan lebar kolom agar lebih proporsional dengan ukuran tabel
        int[] columnWidths = {40, 170, 50, 120, 45, 120, 80};
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

        // Cell renderer - tetap seperti aslinya, tidak perlu diubah
        table.getColumnModel().getColumn(6).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                // Kode renderer tetap sama seperti sebelumnya
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 3));
                panel.setPreferredSize(new Dimension(80, 30));
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
                btnDelete.setBackground(new Color(255, 59, 48));

                panel.add(btnEdit);
                panel.add(btnDelete);
                return panel;
            }
        });

// Cell editor - dimodifikasi untuk penanganan event yang lebih baik
        table.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            private JPanel panel;
            private JButton btnEdit;
            private JButton btnDelete;
            private int currentRow;
            private boolean isPushed = false;
            private final Component parentComponent = thisPanel;

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

                // Prepare icons
                ImageIcon originalIconEdit = new ImageIcon(getClass().getResource("/SourceImage/edit_icon.png"));
                ImageIcon originalIconDelete = new ImageIcon(getClass().getResource("/SourceImage/hapus_icon.png"));

                Image scaledImageEdit = originalIconEdit.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
                Image scaledImageDelete = originalIconDelete.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);

                ImageIcon scaledIconEdit = new ImageIcon(scaledImageEdit);
                ImageIcon scaledIconDelete = new ImageIcon(scaledImageDelete);

                // Edit button
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
                btnEdit.setBackground(new Color(255, 187, 85));

                btnEdit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        fireEditingStopped();
                        // Gunakan invokeLater untuk menjalankan aksi setelah editing selesai
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                handleEditTransaksi(currentRow);
                            }
                        });
                    }
                });

                // Delete button
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
                btnDelete.setBackground(new Color(255, 59, 48));

                btnDelete.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        fireEditingStopped();
                        // Gunakan invokeLater untuk menjalankan aksi setelah editing selesai
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                handleDeleteTransaksi(currentRow);
                            }
                        });
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

            // Override isCellEditable untuk memastikan sel bisa diedit
            @Override
            public boolean isCellEditable(EventObject anEvent) {
                return true;
            }
        });
// Hapus cell editor dan gunakan pendekatan click detection murni
        table.getColumnModel().getColumn(6).setCellEditor(null);

// Tambahkan listener untuk mendeteksi klik pada area tombol
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = table.columnAtPoint(e.getPoint());
                int row = table.rowAtPoint(e.getPoint());

                if (column == 6 && row >= 0) {
                    Rectangle cellRect = table.getCellRect(row, column, false);
                    int relativeX = e.getX() - cellRect.x;

                    // Area tombol edit (sekitar 30px pertama dari kiri)
                    if (relativeX >= 5 && relativeX < 35) {
                        handleEditTransaksi(row);
                    } // Area tombol hapus (35px berikutnya, dengan jarak 2px)
                    else if (relativeX >= 37 && relativeX < 67) {
                        handleDeleteTransaksi(row);
                    }
                }
            }
        });

        // Tentukan bahwa kolom 6 bisa diedit
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(6).setCellEditor(table.getColumnModel().getColumn(6).getCellEditor());

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
        scanKodeField.addActionListener(e -> {
            getDataProduk();
        });
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
            scanKodeField.setEditable(true);
            namaProduk.setEditable(true);
            sizeProduk.setEditable(true);
            scanKodeField.setFocusable(true);
            namaProduk.setFocusable(true);
            sizeProduk.setFocusable(true);

            scanKodeField.requestFocus();
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
        btnCheckout = new JButton("SIMPAN") {
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
            DefaultTableModel model = (DefaultTableModel) roundedTable.getTable().getModel();
            if (model.getRowCount() == 0) {
                PindahanAntarPopUp.showTidakAdaItemYangDibeli(parentFrame);
                System.out.println("tidak ada produk dibeli");
                return;
            }

            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(parentComponent);
            final PopUp_BayarTransjual dialog = new PopUp_BayarTransjual(parentFrame);

            // Tambahkan listener ke tombol OK
            dialog.addOKButtonActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
//                         Proses transaksi hanya jika OK diklik
                    if (saveTransactionToDatabase()) {
                        updateTotalAmount();
                        txtIdTransaksi.setText(generateNextTransaksiId());
                        clearTransactionTable();
                        dialog.startCloseAnimation();
                    }
                }
            });

            // Tampilkan dialog
            dialog.setVisible(true);
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
        try {
            String nama = namaProduk.getText();
            String kode = scanKodeField.getText();
            String size = currentProductSize;
            String hargaText = hargaBeliField.getText().replace("Rp. ", "").replace(".", "").trim();
            String qtyText = qtyField.getText().trim();

            if (kode.isEmpty()) {
                PindahanAntarPopUp.showScanProdukTerlebihDahulu(parentFrame);
                scanKodeField.requestFocus();
                return;
            }

            if (!isKodeProdukValid(kode)) {
                PindahanAntarPopUp.showTransBeliKodeProdukTidakDitemukan(parentFrame);
                scanKodeField.setText("");
                namaProduk.setText("");
                sizeProduk.setText("");
                hargaBeliField.setText("");
                qtyField.setText("");
                scanKodeField.requestFocus();
                return;
            }

            if (nama.isEmpty()) {
                PindahanAntarPopUp.showTransBeliNamaProdukTidakBolehKosong(parentFrame);
                namaProduk.requestFocus();
                return;
            }

            if (size.isEmpty()) {
                PindahanAntarPopUp.showTransBeliSizeProdukTidakBolehKosong(parentFrame);
                sizeProduk.requestFocus();
                return;
            }

            if (hargaText.isEmpty() || hargaText.equals("0")) {
                PindahanAntarPopUp.showTransBeliHargaBeliTidakBolehKosongAtauNol(parentFrame);
                hargaBeliField.requestFocus();
                return;
            }

            if (qtyText.isEmpty()) {
                PindahanAntarPopUp.showTransBeliQuantityTidakBolehKosong(parentFrame);
                qtyField.requestFocus();
                return;
            }

            int qty = Integer.parseInt(qtyText);
            int harga = Integer.parseInt(hargaText);

            if (qty <= 0) {
                PindahanAntarPopUp.showTransBeliQuantityHarusLebihDari0(parentFrame);
                qtyField.requestFocus();
                return;
            }

            if (harga <= 0) {
                PindahanAntarPopUp.showTransBeliHargaBeliHarusLebihDari0(parentFrame);
                hargaBeliField.requestFocus();
                return;
            }

            double totalawal = harga * qty;

            DefaultTableModel model = (DefaultTableModel) roundedTable.getTable().getModel();
            boolean itemFound = false;
            double rowTotal;

            for (int i = 0; i < model.getRowCount(); i++) {
                String existingName = model.getValueAt(i, 1).toString();
                String existingSize = model.getValueAt(i, 2).toString();
                String existingHarga = model.getValueAt(i, 3).toString();
                String hargaForm = "Rp. " + formatter.format(harga);

                if (existingName.equals(nama)
                        && existingSize.equals(currentProductSize)
                        && existingHarga.equals(hargaForm)) {
                    int currentQty = Integer.parseInt(model.getValueAt(i, 4).toString());
                    int newQty = currentQty + qty;
                    model.setValueAt(newQty, i, 4);

                    rowTotal = harga * qty;
                    double currentTotal = Double.parseDouble(model.getValueAt(i, 5).toString().replace("Rp. ", "").replace(".", "").trim());
                    double total = currentTotal + rowTotal;
                    model.setValueAt("Rp. " + formatter.format(total), i, 5);
                    itemFound = true;
                    break;
                }
            }

            if (!itemFound) {
                int rowCount = model.getRowCount() + 1;
                model.addRow(new Object[]{
                    rowCount,
                    nama,
                    currentProductSize,
                    "Rp. " + formatter.format(harga),
                    qty,
                    "Rp. " + formatter.format(totalawal),
                    ""
                });
            }
            scanKodeField.setText("");
            namaProduk.setText("");
            sizeProduk.setText("");
            hargaBeliField.setText("Rp. ");
            qtyField.setText("");

            scanKodeField.setFocusable(true);
            namaProduk.setFocusable(true);
            sizeProduk.setFocusable(true);
            scanKodeField.setEditable(true);
            namaProduk.setEditable(true);
            sizeProduk.setEditable(true);
            scanKodeField.setEnabled(true);
            namaProduk.setEnabled(true);
            sizeProduk.setEnabled(true);

            updateTotalAmount();

            scanKodeField.requestFocus();

            PindahanAntarPopUp.showTransBeliProdukBerhasilDitambahkanKeKeranjang(parentFrame);
        } catch (NumberFormatException ex) {
            PindahanAntarPopUp.showTransBeliFormatQtyDanHargaHarusAngka(parentFrame);
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean isKodeProdukValid(String kodeProduk) {
        boolean isValid = false;
        try {
            Connection conn = db.conn.getConnection();
            String sql = "SELECT COUNT(*) FROM produk WHERE id_produk = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, kodeProduk);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                isValid = rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isValid;
    }

    private void updateTotalAmount() {
        double totalAmount = 0;
        DefaultTableModel model = (DefaultTableModel) roundedTable.getTable().getModel();

        for (int i = 0; i < model.getRowCount(); i++) {
            String totalStr = model.getValueAt(i, 5).toString().replace("Rp. ", "").replace(".", "").replace(",", "");
            try {
                totalAmount += Double.parseDouble(totalStr);
            } catch (NumberFormatException ex) {
                System.err.println("Error parsing total amount: " + ex.getMessage());
            }
        }

        totalValueLabel.setText("Rp. " + formatter.format(totalAmount));
    }

    private void getDataProduk() {
        try {
            String kode = scanKodeField.getText();
            String sql = "SELECT nama_produk, size FROM produk WHERE id_produk = ?";
            try (PreparedStatement st = con.prepareStatement(sql)) {
                st.setString(1, kode);
                ResultSet rs = st.executeQuery();

                if (rs.next()) {
                    namaProduk.setText(rs.getString("nama_produk"));
                    String ukuran = rs.getString("size");
                    sizeProduk.setText(ukuran);

                    currentProductSize = ukuran != null ? ukuran : "";
                    scanKodeField.setFocusable(false);
                    namaProduk.setFocusable(false);
                    sizeProduk.setFocusable(false);
                    hargaBeliField.requestFocus();
                } else {
                    PindahanAntarPopUp.showTransBeliKodeProdukTidakDitemukan(parentFrame);
                    scanKodeField.setText("");
                    scanKodeField.requestFocus();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String generateNextTransaksiId() {
        String nextId = ID_PREFIX + "00001"; // Default jika belum ada transaksi

        try {
            // Query untuk mendapatkan ID transaksi terakhir dari database
            String query = "SELECT id_transaksibeli FROM transaksi_beli ORDER BY id_transaksibeli DESC LIMIT 1";
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String lastId = rs.getString("id_transaksibeli");
                if (lastId != null && lastId.startsWith(ID_PREFIX)) {
                    // Ekstrak nomor dari ID terakhir
                    String numberPart = lastId.substring(ID_PREFIX.length());
                    try {
                        int lastNumber = Integer.parseInt(numberPart);
                        // Increment nomor
                        int nextNumber = lastNumber + 1;
                        // Format nomor dengan padding nol di depan
                        String paddedNumber = String.format("%0" + PADDING_LENGTH + "d", nextNumber);
                        // Gabungkan prefix dengan nomor
                        nextId = ID_PREFIX + paddedNumber;
                    } catch (NumberFormatException e) {
                        System.err.println("Format ID transaksi tidak valid: " + lastId);
                    }
                }
            }

            rs.close();
            pst.close();
        } catch (SQLException e) {
            System.err.println("Error mengambil ID transaksi terakhir: " + e.getMessage());
            e.printStackTrace();
        }

        return nextId;
    }

    private boolean saveTransactionToDatabase() {
        DefaultTableModel model = (DefaultTableModel) roundedTable.getTable().getModel();
        int rowCount = model.getRowCount();

        if (rowCount == 0) {
            return false; // Tidak ada item untuk disimpan
        }

        try {
            // Mulai transaksi untuk atomisitas
            con.setAutoCommit(false);

            // Buat nomor referensi untuk transaksi (norfid)
            String norfid = NoRFID;

            // Masukkan header transaksi ke tabel transaksi_jual
            String insertTransHeader = "INSERT INTO transaksi_beli (id_transaksibeli, tanggal_transaksi, norfid) VALUES (?, ?, ?)";
            PreparedStatement psHeader = con.prepareStatement(insertTransHeader, Statement.RETURN_GENERATED_KEYS);

            String transactionId = txtIdTransaksi.getText();
            System.out.println(transactionId);
            psHeader.setString(1, transactionId);

            // Set tanggal transaksi
            java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(System.currentTimeMillis());
            psHeader.setTimestamp(2, currentTimestamp);

            // Set nomor referensi
            psHeader.setString(3, norfid);

            psHeader.executeUpdate();

            // Sekarang masukkan setiap item dari tabel sebagai detail transaksi
            for (int i = 0; i < rowCount; i++) {
                // Dapatkan data dari tabel
                String productName = model.getValueAt(i, 1).toString();
                String productSize = model.getValueAt(i, 2).toString();
                System.out.println(productName);
                System.out.println(productSize);
                int quantity = Integer.parseInt(model.getValueAt(i, 4).toString());

                // Dapatkan harga dari kolom 4, hilangkan "Rp. " dan "."
                String priceStr = model.getValueAt(i, 3).toString().replace("Rp. ", "").replace(".", "");
                double price = Double.parseDouble(priceStr);

                // Dapatkan total harga dari kolom 6
                String totalStr = model.getValueAt(i, 5).toString().replace("Rp. ", "").replace(".", "");
                double totalPrice = Double.parseDouble(totalStr);

                // Dapatkan ID produk dari nama dan ukuran
                String productId = getProductIdFromNameAndSize(productName, productSize);
                System.out.println(productId);

                String updateHargaProduk = "UPDATE produk SET harga_beli = ? WHERE id_produk = ?";
                PreparedStatement psUpdate = con.prepareStatement(updateHargaProduk);
                psUpdate.setDouble(1, price);
                psUpdate.setString(2, productId);
                psUpdate.executeUpdate();
                psUpdate.close();

                String insertDetail = "INSERT INTO detail_transaksibeli (id_produk, id_transaksibeli, total_harga, jumlah_produk) VALUES (?, ?, ?, ?)";
                PreparedStatement psDetail = con.prepareStatement(insertDetail);

                psDetail.setString(1, productId);
                psDetail.setString(2, transactionId);
                psDetail.setDouble(3, totalPrice);
                psDetail.setInt(4, quantity);

                psDetail.executeUpdate();
                psDetail.close();
            }

            con.commit();
            con.setAutoCommit(true);

            PindahanAntarPopUp.showEditTransJualBerhasilDiSimpan(parentFrame);
//          PindahanAntarPopUp.showSuksesBayarTransjual(parentFrame);
            return true; // Transaksi berhasil

        } catch (SQLException ex) {
            // Rollback jika terjadi kesalahan
            try {
                con.rollback();
                con.setAutoCommit(true);
            } catch (SQLException rollbackEx) {
                System.err.println("Error melakukan rollback transaksi: " + rollbackEx.getMessage());
            }

            JOptionPane.showMessageDialog(this,
                    "Error menyimpan transaksi: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();

            return false; // Transaksi gagal
        }
    }

    //INI FUNGSI HUBUNG UNTUK EDIT DAN HAPUS BUTTON WOI
    private void handleEditTransaksi(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= roundedTable.getTable().getRowCount()) {
            return;
        }

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(parentComponent);
        PopUp_edittransbeli editPopup = new PopUp_edittransbeli(parentFrame, roundedTable.getTable(), rowIndex);
        editPopup.setVisible(true);

        updateTotalAmount();
    }

    private void handleDeleteTransaksi(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= roundedTable.getTable().getRowCount()) {
            return;
        }

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(parentComponent);
        PopUp_transbelihapusdata deletePopup = new PopUp_transbelihapusdata(parentFrame, rowIndex);

        deletePopup.setDeleteCallback(new DeleteCallback() {
            @Override
            public void onDeleteConfirmed(int rowToDelete) {
                DefaultTableModel model = (DefaultTableModel) roundedTable.getTable().getModel();
                model.removeRow(rowToDelete);

                // Renumber rows
                for (int i = 0; i < model.getRowCount(); i++) {
                    model.setValueAt(i + 1, i, 0);
                }

                updateTotalAmount();
                PindahanAntarPopUp.showProdukBerhasilDihapus(parentFrame);
            }
        });

        deletePopup.setVisible(true);
    }

    private void clearTransactionTable() {
        DefaultTableModel model = (DefaultTableModel) roundedTable.getTable().getModel();
        model.setRowCount(0); // Remove all rows

        // Clear related fields
        scanKodeField.setText("");
        namaProduk.setText("");
        sizeProduk.setText("");
        hargaBeliField.setText("Rp. ");
        totalValueLabel.setText("Rp. ");

        scanKodeField.requestFocus();
    }

    private String getProductIdFromNameAndSize(String productName, String productSize) {
        String productId = "";

        try {
            String query = "SELECT id_produk FROM produk WHERE nama_produk = ? AND size = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, productName);
            ps.setString(2, productSize);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                productId = rs.getString("id_produk");
            }

            rs.close();
            ps.close();
        } catch (SQLException ex) {
            System.err.println("Error looking up product ID: " + ex.getMessage());
        }

        return productId;
    }

    private void setNamaUser() {
        String email = LoginForm.getNamaUser();
        String norfid = LoginForm.getNoRFID();

        String sql = "SELECT nama_user, norfid FROM user WHERE email = ? OR norfid = ?";
        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, email);
            st.setString(2, norfid);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                NoRFID = rs.getString("norfid");
                namaUser = rs.getString("nama_user");
            } else {
                System.out.println("No karyawan found ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
