package Form;

import PopUp_all.PopUp_BayarTransjual;
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
import SourceCode.JTableRounded;
import java.awt.geom.Path2D;
import java.math.BigInteger;
import produk.ComboboxCustom;
import java.sql.*;
import db.conn;
import java.util.ArrayList;
import PopUp_all.Popup_edittransjual;
import PopUp_all.Popup_hapustransjual;

public class Transjual extends JPanel {

    Component parentComponent = this;
    private final DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(new Locale("id", "ID"));
    private JTextField hargaBeliField;
    private JPanel thisPanel;
    private JTextField scanKodeField;
    private JTextField namabarang;
    public JTableRounded roundedTable;
    private JLabel totalValueLabel;
    private JButton btnTambah, btnBatal, btnBayar;
    private ComboboxCustom diskonComboBox;
    private Connection con;
    private String currentProductSize = "";

    public Transjual() {
        thisPanel = this;
        setPreferredSize(new Dimension(1065, 640));
        setLayout(null);
        setBackground(Color.white);

        con = conn.getConnection();

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
        JLabel transaksiLabel = new JLabel("Transaksi Jual");
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
        String[] columns = {"No", "Nama Produk", "Size", "Jumlah", "Harga Satuan", "Diskon", "Total", "Aksi"};
        roundedTable = new JTableRounded(columns, 750, 445);

        // Mengatur lebar tiap kolom yang lebih proporsional
        roundedTable.setColumnWidth(0, 40);   // No
        roundedTable.setColumnWidth(1, 180);  // Nama Produk 
        roundedTable.setColumnWidth(2, 50);  // Size
        roundedTable.setColumnWidth(3, 50);  // Jumlah
        roundedTable.setColumnWidth(4, 130);  // Harga Satuan
        roundedTable.setColumnWidth(5, 60);   // Diskon
        roundedTable.setColumnWidth(6, 130);   // Total
        roundedTable.setColumnWidth(7, 80);   // Aksi

        // Mendapatkan JTable dari JTableRounded untuk kustomisasi lanjutan
        JTable table = roundedTable.getTable();
        table.setCursor(new Cursor(Cursor.HAND_CURSOR));

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

        // Penyesuaian posisi dan ukuran ScrollPane untuk area table
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
        // Add the scrollPane to the main panel
        mainPanel.add(scrollPane);

        // Cell renderer - make panels transparent and icons larger
        table.getColumnModel().getColumn(7).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

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
                        int selectedRow = roundedTable.getTable().getSelectedRow();
                        if (selectedRow != -1) {
                            handleEditTransaksi(selectedRow);
                        } else {
                            JOptionPane.showMessageDialog(Transjual.this,
                                    "Pilih baris yang ingin diedit terlebih dahulu",
                                    "Informasi", JOptionPane.INFORMATION_MESSAGE);
                        }
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
                        stopCellEditing(); // Stop editing before showing dialog

                        // Get the selected row (currentRow is already defined in your table cell editor)
                        int selectedRow = currentRow;

                        if (selectedRow != -1) {
                            // Store the row index for later deletion
                            final int rowToDelete = selectedRow;

                            // Create the confirmation popup
                            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(parentComponent);
                            Popup_hapustransjual dialog = new Popup_hapustransjual(parentFrame);

                            // Add a window listener to handle the result after the popup is closed
                            dialog.addWindowListener(new WindowAdapter() {
                                @Override
                                public void windowClosed(WindowEvent e) {
                                    // Check if delete was confirmed (this requires adding a flag in Popup_hapustransjual)
                                    if (dialog.isDeleteConfirmed()) {
                                        // Remove the row from the table model
                                        DefaultTableModel model = (DefaultTableModel) roundedTable.getTable().getModel();
                                        model.removeRow(rowToDelete);

                                        // Renumber the remaining rows
                                        for (int i = 0; i < model.getRowCount(); i++) {
                                            model.setValueAt(i + 1, i, 0); // Assuming the row number is in column 0
                                        }

                                        // Update the total amount
                                        updateTotalAmount();
                                    }
                                }
                            });

                            dialog.setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(Transjual.this,
                                    "Pilih baris yang ingin dihapus terlebih dahulu",
                                    "Informasi", JOptionPane.INFORMATION_MESSAGE);
                        }
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

                // Menambahkan border hitam 1px
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

                g2.dispose();
            }
        };
        formPanel.setBounds(800, 130, 235, 440);
        formPanel.setOpaque(false);
        mainPanel.add(formPanel);

        JLabel closeLabel = new JLabel("×");
        closeLabel.setBounds(215, 10, 20, 20);
        closeLabel.setForeground(Color.BLACK);
        closeLabel.setFont(new Font("Arial", Font.BOLD, 23));
        closeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Fungsi saat label X diklik
                System.out.println("Tombol close diklik");
                Productt.getMainFrame().switchBackToProductPanelKasir();
                scanKodeField.setText("");
                namabarang.setText("");
                hargaBeliField.setText("Rp. ");
                closeLabel.setForeground(Color.BLACK);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // Ketika mouse masuk area label
                closeLabel.setForeground(new Color(255, 59, 48));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Ketika mouse keluar area label
                closeLabel.setForeground(Color.BLACK);
            }
        });
        formPanel.add(closeLabel);
        formPanel.add(createLabel("Scan Kode Produk", 15, 25));
        scanKodeField = createRoundedTextField(15, 50, 205, 35);
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

        formPanel.add(createLabel("Nama Barang", 15, 95));
        namabarang = createRoundedTextField(15, 120, 205, 35);
        namabarang.setFocusable(false);
        formPanel.add(namabarang);

        // Tambahkan label dan combo box untuk diskon
        formPanel.add(createLabel("Atur Diskon", 15, 165));
        diskonComboBox = new ComboboxCustom(getDiscountOptionsFromDatabase());
        diskonComboBox.setBounds(15, 190, 205, 35);
        formPanel.add(diskonComboBox);

        formPanel.add(createLabel("Harga Beli", 15, 235));
        hargaBeliField = createRoundedTextField(15, 260, 205, 35);
        hargaBeliField.setText("Rp. "); // Prefix "Rp. "
        hargaBeliField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                enforceRpPrefix();
                formatHargaBeli();
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

        // Tambahkan tombol "Batal" setelah field harga beli
        btnBatal = new JButton("BATAL") {
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
        btnBatal.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                btnBatal.setLocation(btnBatal.getX(), btnBatal.getY() + 2); // Tombol turun sedikit
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                btnBatal.setLocation(btnBatal.getX(), btnBatal.getY() - 2); // Kembali ke posisi semula
            }
        });
        btnBatal.setBounds(15, 325, 100, 35);
        btnBatal.setOpaque(false);
        btnBatal.setContentAreaFilled(false);
        btnBatal.setBorderPainted(false);
        btnBatal.setForeground(Color.WHITE);
        btnBatal.setFont(new Font("Arial", Font.BOLD, 14));
        btnBatal.setFocusPainted(false);
// Tambahkan action listener sesuai kebutuhan
        formPanel.add(btnBatal);

// Tambahkan tombol "Tambah" setelah field harga beli
        btnTambah = new JButton("TAMBAH") {
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
        btnTambah.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                btnTambah.setLocation(btnTambah.getX(), btnTambah.getY() + 2); // Tombol turun sedikit
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                btnTambah.setLocation(btnTambah.getX(), btnTambah.getY() - 2); // Kembali ke posisi semula
            }
        });
        btnTambah.setBounds(120, 325, 100, 35);
        btnTambah.setOpaque(false);
        btnTambah.setContentAreaFilled(false);
        btnTambah.setBorderPainted(false);
        btnTambah.setForeground(Color.WHITE);
        btnTambah.setFont(new Font("Arial", Font.BOLD, 14));
        btnTambah.setFocusPainted(false);
// Tambahkan action listener sesuai kebutuhan
        formPanel.add(btnTambah);

// Panel untuk total dan kembalian dengan latar belakang abu-abu gelap
        JPanel totalPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(60, 63, 65));

                // Membuat bentuk khusus dengan rounded corners hanya di bagian bawah
                int width = getWidth();
                int height = getHeight();
                int arcWidth = 15; // Lebar arc (rounded)
                int arcHeight = 15; // Tinggi arc (rounded)

                Path2D path = new Path2D.Float();
                path.moveTo(0, 0);
                path.lineTo(width, 0);
                path.lineTo(width, height - arcHeight);
                path.quadTo(width, height, width - arcWidth, height);
                path.lineTo(arcWidth, height);
                path.quadTo(0, height, 0, height - arcHeight);
                path.closePath();

                // Membuat path untuk border dengan bentuk yang sama
                Path2D borderPath = new Path2D.Float();
                borderPath.moveTo(0, 0);
                borderPath.lineTo(width - 1, 0);
                borderPath.lineTo(width - 1, height - arcHeight);
                borderPath.quadTo(width - 1, height - 1, width - arcWidth, height - 1);
                borderPath.lineTo(arcWidth, height - 1);
                borderPath.quadTo(0, height - 1, 0, height - arcHeight);
                borderPath.closePath();

                // Menggambar border hitam 1px
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(3.0f));
                g2.draw(borderPath);

                g2.fill(path);
                g2.dispose();
            }
        };
        totalPanel.setBounds(0, 380, 235, 60);
        totalPanel.setOpaque(false);
        formPanel.add(totalPanel);

        JLabel totalLabel = new JLabel("Total");
        totalLabel.setForeground(Color.WHITE);
        totalLabel.setFont(new Font("Poppins", Font.PLAIN, 14));
        totalLabel.setBounds(20, -45, 80, 125);
        totalPanel.add(totalLabel);

        totalValueLabel = new JLabel("Rp. ");
        totalValueLabel.setForeground(Color.WHITE);
        totalValueLabel.setFont(new Font("Poppins", Font.PLAIN, 14));
        totalValueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        totalValueLabel.setBounds(80, -45, 140, 125);
        totalPanel.add(totalValueLabel);

        JLabel kembalianLabel = new JLabel("Kembalian");
        kembalianLabel.setForeground(Color.WHITE);
        kembalianLabel.setFont(new Font("Poppins", Font.PLAIN, 14));
        kembalianLabel.setBounds(20, -20, 80, 130);
        totalPanel.add(kembalianLabel);

        JLabel kembalianValueLabel = new JLabel("Rp. ");
        kembalianValueLabel.setForeground(Color.WHITE);
        kembalianValueLabel.setFont(new Font("Poppins", Font.PLAIN, 14));
        kembalianValueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        kembalianValueLabel.setBounds(80, -15, 140, 120);
        totalPanel.add(kembalianValueLabel);

// Membuat tombol BAYAR diluar panel form
        btnBayar = new JButton("BAYAR") {
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
        btnBayar.setBounds(800, 580, 235, 40);
        btnBayar.setOpaque(false);
        btnBayar.setContentAreaFilled(false);
        btnBayar.setBorderPainted(false);
        btnBayar.setForeground(Color.WHITE);
        btnBayar.setFont(new Font("Arial", Font.BOLD, 14));
        btnBayar.setFocusPainted(false);
        btnBayar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(parentComponent);
                PopUp_BayarTransjual dialog = new PopUp_BayarTransjual(parentFrame);
                dialog.setVisible(true);
            }
        });

// Efek hover dan klik
        btnBayar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                btnBayar.setLocation(btnBayar.getX(), btnBayar.getY() + 2); // Tombol turun sedikit
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                btnBayar.setLocation(btnBayar.getX(), btnBayar.getY() - 2); // Kembali ke posisi semula
            }
        });

        mainPanel.add(btnBayar);
        actionButton();
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

    private void setupScanKodeFieldListener() {
        scanKodeField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String kode = scanKodeField.getText().trim();
                    if (!kode.isEmpty()) {
                        lookupProduct(kode);
                    }
                }
            }
        });
    }

    private void lookupProduct(String kode) {
        try {
            String query = "SELECT nama_produk, harga_jual, size FROM produk WHERE id_produk = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, kode);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String nama = rs.getString("nama_produk");
                int harga = rs.getInt("harga_jual");
                String ukuran = rs.getString("size");

                namabarang.setText(nama);
                hargaBeliField.setText("Rp. " + formatter.format(harga));
                currentProductSize = ukuran != null ? ukuran : "";
            } else {
//                JOptionPane.showMessageDialog(this, "Produk tidak ditemukan", "Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("produk tidak ada");
                namabarang.setText("");
                hargaBeliField.setText("Rp. ");
            }

            rs.close();
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error querying database: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addItemToTable() {
        try {
            String nama = namabarang.getText();
            if (nama.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama produk tidak boleh kosong", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get price from hargaBeliField
            String hargaText = hargaBeliField.getText().replace("Rp. ", "").replace(".", "").trim();
            if (hargaText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Harga tidak boleh kosong", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int harga = Integer.parseInt(hargaText);

            // Get discount from combobox
            String diskonText = diskonComboBox.getSelectedItem().toString();
            double diskon = 0;

            if (!diskonText.isEmpty()) {
                // Get the actual discount percentage from database based on the selected name
                try {
                    String query = "SELECT total_diskon FROM diskon WHERE nama_diskon = ?";
                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setString(1, diskonText);
                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        diskon = rs.getDouble("total_diskon");
                    } else {
                        // If not found in database (fallback), try to parse from the string
                        diskon = Double.parseDouble(diskonText.replace("%", ""));
                    }

                    rs.close();
                    ps.close();
                } catch (SQLException ex) {
                    // Fallback if database query fails
                    if (diskonText.contains("%")) {
                        diskon = Double.parseDouble(diskonText.replace("%", ""));
                    }
                    System.err.println("Error fetching discount value: " + ex.getMessage());
                }
            }

            // Calculate total for a single item
            double diskonAmount = (diskon / 100) * harga;
            double total = harga - diskonAmount;

            // Format discount display
            String diskonDisplay = diskon == 0 ? "-" : "" + (int) diskon + "%";

            // Check if the item already exists in the table with the same discount
            DefaultTableModel model = (DefaultTableModel) roundedTable.getTable().getModel();
            boolean itemFound = false;

            for (int i = 0; i < model.getRowCount(); i++) {
                String existingName = model.getValueAt(i, 1).toString();
                String existingSize = model.getValueAt(i, 2).toString();
                String existingDiskon = model.getValueAt(i, 5).toString();

                // Compare name, size and discount
                if (existingName.equals(nama)
                        && existingSize.equals(currentProductSize)
                        && existingDiskon.equals(diskonDisplay)) {

                    // Update quantity
                    int currentQty = Integer.parseInt(model.getValueAt(i, 3).toString());
                    int newQty = currentQty + 1;
                    model.setValueAt(newQty, i, 3);

                    // Update total price for this row
                    double rowTotal = total * newQty;
                    model.setValueAt("Rp. " + formatter.format(rowTotal), i, 6);

                    itemFound = true;
                    break;
                }
            }

            // If item not found with same discount, add as new row
            if (!itemFound) {
                int rowCount = model.getRowCount() + 1;

                model.addRow(new Object[]{
                    rowCount,
                    nama,
                    currentProductSize,
                    1, // Default quantity
                    "Rp. " + formatter.format(harga),
                    diskonDisplay,
                    "Rp. " + formatter.format(total),
                    "" // Action column
                });
            }

            // Clear fields
            scanKodeField.setText("");
            namabarang.setText("");
            hargaBeliField.setText("Rp. ");
            diskonComboBox.setSelectedIndex(0);
            updateTotalAmount();

            // Set focus back to scan field for next item
            scanKodeField.requestFocus();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Format harga tidak valid", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateTotalAmount() {
        double totalAmount = 0;
        DefaultTableModel model = (DefaultTableModel) roundedTable.getTable().getModel();

        for (int i = 0; i < model.getRowCount(); i++) {
            // Get the already calculated total price (after discount) from column 6
            String totalStr = model.getValueAt(i, 6).toString().replace("Rp. ", "").replace(".", "");
            try {
                totalAmount += Double.parseDouble(totalStr);
            } catch (NumberFormatException ex) {
                System.err.println("Error parsing total amount: " + ex.getMessage());
            }
        }

        totalValueLabel.setText("Rp. " + formatter.format(totalAmount));
    }

    private void actionButton() {
        // Add this code in the constructor after creating btnTambah
        btnTambah.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addItemToTable();
            }
        });

        btnBatal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scanKodeField.setText("");
                namabarang.setText("");
                hargaBeliField.setText("Rp. ");
                diskonComboBox.setSelectedIndex(0);
            }
        });

// Initialize scan field listener
        setupScanKodeFieldListener();
    }

    // Add this method to fetch discount options from database
    private String[] getDiscountOptionsFromDatabase() {
        try {
            // Query to get distinct discount options from database
            String query = "SELECT DISTINCT nama_diskon FROM diskon ORDER BY total_diskon ASC";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            // Count the number of results
            ArrayList<String> options = new ArrayList<>();
            options.add(""); // Adding empty option first

            while (rs.next()) {
                String diskonValue = rs.getString("nama_diskon");
                options.add(diskonValue);
            }

            rs.close();
            ps.close();

            return options.toArray(new String[0]);
        } catch (SQLException ex) {
            System.err.println("Error fetching discount options: " + ex.getMessage());
            // Return default options if database fails
            return new String[]{"", "5%", "10%", "15%", "20%", "25%", "30%", "50%"};
        }
    }

    private void handleEditTransaksi(int rowIndex) {
        // Check if row index is valid
        if (rowIndex < 0 || rowIndex >= roundedTable.getTable().getRowCount()) {
            return;
        }

        // Create and show the edit popup
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(parentComponent);
        Popup_edittransjual editPopup = new Popup_edittransjual(parentFrame, roundedTable.getTable(), rowIndex);
        editPopup.setVisible(true);

        refreshAfterEdit();
    }

    public void refreshAfterEdit() {
        updateTotalAmount();
    }
}
