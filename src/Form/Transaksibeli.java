package Form;

import SourceCode.PopUp_edittransbeli;
import SourceCode.ScrollPane;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import SourceCode.SidebarCustom.Menu;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import SourceCode.PopUp_transbelihapusdata;
import SourceCode.PopUp_edittransbeli;
import SourceCode.JTableRounded;
import java.math.BigInteger;

public class Transaksibeli extends JPanel {

    private final DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(new Locale("id", "ID"));
    private JTextField hargaBeliField;
    private JTextField qtyField;
    private JTextField totalField;
    private JPanel thisPanel;
    private JTextField scanKodeField;
    private JTableRounded roundedTable;

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
        transaksiLabel.setFont(new Font("Arial", Font.BOLD, 20));
        transaksiLabel.setBounds(30, 30, 200, 30);
        mainPanel.add(transaksiLabel);

        // Field Tanggal otomatis - disesuaikan sedikit ke kanan
        JTextField dateField = createRoundedTextField(850, 30, 180, 30);
        dateField.setText(getCurrentDate());
        dateField.setEditable(false);
        dateField.setBackground(new Color(200, 200, 200));
        mainPanel.add(dateField);

        // Search field - diperlebar sedikit
        JTextField searchField = createRoundedTextField(30, 70, 280, 35);
        searchField.setText("Search");
        searchField.setForeground(Color.BLACK);
        searchField.setBackground(new Color(200, 200, 200));
        searchField.addFocusListener(new FocusListener() {
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
                    searchField.setForeground(Color.BLACK);
                }
            }
        });
        mainPanel.add(searchField);

        // Membuat rounded table dengan JTableRounded - diperlebar untuk mengisi space
        String[] columns = {"No", "Kode Produk", "Nama Produk", "Size", "Harga Satuan", "Qty", "Total", "Aksi"};
        roundedTable = new JTableRounded(columns, 750, 450);

        // Mengatur lebar tiap kolom yang lebih proporsional
        roundedTable.setColumnWidth(0, 40);   // No
        roundedTable.setColumnWidth(1, 100);  // Kode Produk (reduced from 120)
        roundedTable.setColumnWidth(2, 170);  // Nama Produk (reduced from 180)
        roundedTable.setColumnWidth(3, 45);   // Size
        roundedTable.setColumnWidth(4, 120);  // Harga Satuan (reduced from 130)
        roundedTable.setColumnWidth(5, 50);   // Qty
        roundedTable.setColumnWidth(6, 120);  // Total (reduced from 130)
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

        // Menambahkan data otomatis ke dalam tabel
        Object[][] data = {
            {1, "2345678", "Adidas Simanjutak Original", "37", "Rp. 200.000,00", 1, "Rp. 200.000,00", "Aksi"},
            {2, "2345678", "Adidas Simanjutak Original", "41", "Rp. 250.000,00", 1, "Rp. 250.000,00", "Aksi"},
            {3, "2345678", "Adidas Simanjutak Original", "41", "Rp. 250.000,00", 1, "Rp. 250.000,00", "Aksi"},
            {4, "2345678", "Adidas Simanjutak Original", "41", "Rp. 250.000,00", 1, "Rp. 250.000,00", "Aksi"},
            {5, "2345678", "Adidas Simanjutak Original", "41", "Rp. 250.000,00", 1, "Rp. 250.000,00", "Aksi"},
            {6, "2345678", "Adidas Simanjutak Original", "41", "Rp. 250.000,00", 1, "Rp. 250.000,00", "Aksi"},
            {7, "2345678", "Adidas Simanjutak Original", "41", "Rp. 250.000,00", 1, "Rp. 250.000,00", "Aksi"},
            {8, "2345678", "Adidas Simanjutak Original", "41", "Rp. 250.000,00", 1, "Rp. 250.000,00", "Aksi"},
            {9, "2345678", "Adidas Simanjutak Original", "41", "Rp. 250.000,00", 1, "Rp. 250.000,00", "Aksi"},
            {10, "2345678", "Adidas Simanjutak Original", "41", "Rp. 250.000,00", 1, "Rp. 250.000,00", "Aksi"},
            {11, "2345678", "Adidas Simanjutak Original", "41", "Rp. 250.000,00", 1, "Rp. 250.000,00", "Aksi"},
            {12, "2345678", "Adidas Simanjutak Original", "41", "Rp. 250.000,00", 1, "Rp. 250.000,00", "Aksi"},
            {13, "2345678", "Adidas Simanjutak Original", "41", "Rp. 250.000,00", 1, "Rp. 250.000,00", "Aksi"},
            {14, "2345678", "Adidas Simanjutak Original", "41", "Rp. 250.000,00", 1, "Rp. 250.000,00", "Aksi"},
            {15, "2345678", "Adidas Simanjutak Original", "41", "Rp. 250.000,00", 1, "Rp. 250.000,00", "Aksi"}
        };

        for (Object[] row : data) {
            tableModel.addRow(row);
        }

        // Penyesuaian posisi dan ukuran ScrollPane untuk area table
        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setBounds(30, 120, 750, 480); // Diperlebar dan diperpanjang
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.setThumbColor(new Color(80, 80, 80, 180)); // Darker, more opaque scrollbar thumb
        scrollPane.setTrackColor(new Color(240, 240, 240, 80)); // Lighter track
        scrollPane.setThumbThickness(8); // Set the thickness of the scrollbar
        scrollPane.setThumbRadius(8);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        // Add the scrollPane to the main panel
        mainPanel.add(scrollPane);

        // Cell renderer - make panels transparent and icons larger
        table.getColumnModel().getColumn(7).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (row >= data.length) {
                    return new JLabel("");
                }

                // Create a completely transparent panel
                // Mengubah padding vertikal dari 5 menjadi 2 agar button posisinya lebih ke atas
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
                if (row >= data.length) {
                    return new JLabel("");
                }

                currentRow = row;
                isPushed = true;

                // Create a completely transparent panel 
                // Mengubah padding vertikal dari 5 menjadi 2 agar button posisinya lebih ke atas
                panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 3)) {
                    // Override paintComponent untuk memastikan panel transparan
                    @Override
                    protected void paintComponent(Graphics g) {
                        g.setColor(new Color(0, 0, 0, 0));
                        g.fillRect(0, 0, getWidth(), getHeight());
                        super.paintComponent(g);
                    }
                };
                panel.setPreferredSize(new Dimension(80, 30)); // Sesuaikan dengan lebar kolom
                panel.setOpaque(false);
                panel.setBackground(new Color(0, 0, 0, 0));

                // Prepare icons - same as renderer
                ImageIcon originalIconEdit = new ImageIcon(getClass().getResource("/SourceImage/edit_icon.png"));
                ImageIcon originalIconDelete = new ImageIcon(getClass().getResource("/SourceImage/hapus_icon.png"));

                Image scaledImageEdit = originalIconEdit.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
                Image scaledImageDelete = originalIconDelete.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);

                ImageIcon scaledIconEdit = new ImageIcon(scaledImageEdit);
                ImageIcon scaledIconDelete = new ImageIcon(scaledImageDelete);

                // Create the edit button dengan custom renderer yang menangani semua state
                btnEdit = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        // Selalu gunakan warna yang sama apapun state tombol
                        g2.setColor(new Color(255, 187, 85));
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                        // Lanjutkan dengan rendering icon
                        super.paintComponent(g2);
                        g2.dispose();
                    }

                    // Tidak mengubah appearance saat pressed
                    @Override
                    public void setPressedIcon(Icon icon) {
                        // Do nothing - keep the same appearance
                    }

                    // Override UI delegate methods to prevent default look and feel
                    @Override
                    public void updateUI() {
                        super.updateUI();
                        // Restore custom appearance after UI update
                        setContentAreaFilled(false);
                        setBorderPainted(false);
                        setFocusPainted(false);
                    }

                    // Override isFocusable untuk mencegah fokus
                    @Override
                    public boolean isFocusable() {
                        return false;
                    }
                };
                btnEdit.setIcon(scaledIconEdit);
                btnEdit.setPreferredSize(new Dimension(30, 26)); // Sedikit lebih besar
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

                // Action for Edit button
                btnEdit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        stopCellEditing(); // Hentikan editing sebelum menampilkan dialog
                        // Use SwingUtilities to find the parent frame
                        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(parentComponent);
                        PopUp_edittransbeli dialog = new PopUp_edittransbeli(parentFrame);
                        dialog.setVisible(true);
                        // fireEditingStopped sudah dipanggil oleh stopCellEditing()
                    }
                });

                // Create the delete button dengan custom renderer yang menangani semua state
                btnDelete = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        // Selalu gunakan warna yang sama apapun state tombol
                        g2.setColor(new Color(255, 59, 48));
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                        // Lanjutkan dengan rendering icon
                        super.paintComponent(g2);
                        g2.dispose();
                    }

                    // Tidak mengubah appearance saat pressed
                    @Override
                    public void setPressedIcon(Icon icon) {
                        // Do nothing - keep the same appearance
                    }

                    // Override UI delegate methods to prevent default look and feel
                    @Override
                    public void updateUI() {
                        super.updateUI();
                        // Restore custom appearance after UI update
                        setContentAreaFilled(false);
                        setBorderPainted(false);
                        setFocusPainted(false);
                    }

                    // Override isFocusable untuk mencegah fokus
                    @Override
                    public boolean isFocusable() {
                        return false;
                    }
                };
                btnDelete.setIcon(scaledIconDelete);
                btnDelete.setPreferredSize(new Dimension(30, 26)); // Sedikit lebih besar
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

                // Action for Delete button
                btnDelete.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        stopCellEditing(); // Hentikan editing sebelum menampilkan dialog
                        // Use SwingUtilities to find the parent frame
                        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(parentComponent);
                        PopUp_transbelihapusdata dialog = new PopUp_transbelihapusdata(parentFrame);
                        dialog.setVisible(true);
                        // fireEditingStopped sudah dipanggil oleh stopCellEditing()
                    }
                });

                // Add buttons to panel
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

        // Panel Form - diposisikan lebih baik di sisi kanan
        JPanel formPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Menggambar latar belakang dengan sudut melengkung 15px
                g2.setColor(new Color(220, 220, 220, 150));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
            }
        };
        formPanel.setBounds(800, 120, 235, 480); // Disesuaikan posisi dan ukurannya
        formPanel.setOpaque(false); // Memastikan transparansi bekerja dengan baik
        mainPanel.add(formPanel);

        // Tambahkan label dan text field dengan jarak yang lebih baik
        formPanel.add(createLabel("Scan Kode Produk", 15, 25));
        scanKodeField = createRoundedTextField(15, 50, 205, 35);
        // Tambahkan key listener untuk memastikan hanya angka yang dapat diinput
        scanKodeField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                // Hanya menerima digit angka dan tombol kontrol (backspace, delete, dll)
                if (!Character.isDigit(c) && !Character.isISOControl(c)) {
                    e.consume(); // Mengabaikan karakter yang bukan angka
                }
            }
        });
        formPanel.add(scanKodeField);

        formPanel.add(createLabel("Nama Produk", 15, 95));
        formPanel.add(createRoundedTextField(15, 120, 205, 35));

        formPanel.add(createLabel("Harga Beli", 15, 165));
        hargaBeliField = createRoundedTextField(15, 190, 205, 35);
        hargaBeliField.setText("Rp. "); // Prefix "Rp. "
        hargaBeliField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                enforceRpPrefix();
                formatHargaBeli();
                hitungTotal();
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) { // Jika tombol Enter ditekan
                    String hargaInput = hargaBeliField.getText().replace("Rp. ", "").replace(".", "").trim();
                    System.out.println(hargaInput); // Output hanya angka
                }
            }
        });
        formPanel.add(hargaBeliField);

        formPanel.add(createLabel("Qty", 15, 235));
        qtyField = createRoundedTextField(15, 260, 60, 35);
        // Modifikasi key listener untuk membatasi input hanya angka dan maksimal 3 digit
        qtyField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                String currentText = qtyField.getText().replace(".", "");

                // Hanya menerima digit angka dan tombol kontrol
                if (!Character.isDigit(c) && !Character.isISOControl(c)) {
                    e.consume(); // Mengabaikan karakter yang bukan angka
                    return;
                }

                // Jika sudah 3 digit dan bukan tombol kontrol, tolak input
                if (currentText.length() >= 3 && Character.isDigit(c)) {
                    e.consume(); // Mengabaikan digit ke-4 dan seterusnya
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                formatQty();
                hitungTotal();
            }
        });
        formPanel.add(qtyField);

        formPanel.add(createLabel("Total", 15, 305));
        totalField = createRoundedTextField(15, 330, 205, 35);
        totalField.setEditable(false);
        formPanel.add(totalField);

        // Membuat tombol dengan border 1px tanpa mengubah createRoundedButton
        JButton btnClear = createRoundedButtonWithThickBorder("Clear", 15, 385, 95, 35, new Color(255, 59, 48)); // Merah gelap
        JButton btnTambahBarang = createRoundedButtonWithThickBorder("Tambah Barang", 120, 385, 100, 35, new Color(0, 107, 214)); // Biru gelap
        JButton btnCheckout = createRoundedButtonWithThickBorder("Checkout", 15, 430, 205, 35, new Color(52, 199, 89)); // Hijau gelap

        // Menambahkan tombol ke formPanel
        formPanel.add(btnClear);
        formPanel.add(btnTambahBarang);
        formPanel.add(btnCheckout);
    }

    // Method untuk memformat angka ribuan
    private void formatQty() {
        try {
            String text = qtyField.getText().replace(".", "").trim();
            if (text.isEmpty()) {
                return;
            }

            int value = Integer.parseInt(text);
            qtyField.setText(formatter.format(value)); // Format ribuan dengan separator titik
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
                totalField.setText("");
                return;
            }

            BigInteger harga = new BigInteger(hargaText);
            BigInteger qty = new BigInteger(qtyField.getText());

            BigInteger total = harga.multiply(qty);
            totalField.setText("Rp. " + formatter.format(total));
        } catch (NumberFormatException e) {
            totalField.setText("");
        }
    }

    private void enforceRpPrefix() {
        if (!hargaBeliField.getText().startsWith("Rp. ")) {
            hargaBeliField.setText("Rp. ");
        }
    }

    // Method untuk mendapatkan tanggal saat ini dengan format tertentu
    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(new Date());
    }

    // Method untuk membuat text field melengkung
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

                // Background dengan sudut melengkung
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

                // Border
                g2.setColor(Color.GRAY);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);

                g2.dispose();
                super.paintComponent(g);
            }
        };
    }

    private JButton createRoundedButton(String text, int x, int y, int width, int height, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Warna latar dengan sudut melengkung
                g2.setColor(getModel().isPressed() ? bgColor.darker() : bgColor); // Lebih gelap saat ditekan
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                g2.dispose();
                super.paintComponent(g); // Pastikan teks tetap terlihat
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setStroke(new BasicStroke(0)); // Ketebalan border
                g2.setColor(Color.BLACK);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2.dispose();
            }
        };

        button.setBounds(x, y, width, height);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);

        // Efek hover dan klik
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                button.setLocation(button.getX(), button.getY() + 2); // Tombol turun sedikit
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setLocation(button.getX(), button.getY() - 2); // Kembali ke posisi semula
            }
        });

        return button;
    }

    private JButton createRoundedButtonWithThickBorder(String text, int x, int y, int width, int height, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Mengubah warna tombol menjadi putih saat tombol ditekan
                Color buttonColor = bgColor; // Menjadi putih jika ditekan
                g2.setColor(buttonColor); // Warna latar belakang tombol
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                g2.dispose();
                super.paintComponent(g); // Pastikan teks tetap terlihat
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setStroke(new BasicStroke(1)); // Ketebalan border 1px
                g2.setColor(Color.BLACK); // Warna border hitam
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2.dispose();
            }
        };

        button.setBounds(x, y, width, height);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);

        // Efek hover dan klik
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                button.setLocation(button.getX(), button.getY() + 2); // Tombol turun sedikit
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setLocation(button.getX(), button.getY() - 2); // Kembali ke posisi semula
            }
        });

        return button;
    }

    private JLabel createLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, 150, 20);
        return label;
    }

}
