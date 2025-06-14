package Form;

import PopUp_all.PopUp_BayarTransjual;
import PopUp_all.*;
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
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import produk.ProductDisplayyKasir;

public class Transjual extends JPanel {

    private JFrame parentFrame;
    Component parentComponent = this;
    private final DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(new Locale("id", "ID"));
    private JTextField hargaBeliField;
    private JTextField masukuangfield;
    private JPanel thisPanel;
    private JTextField scanKodeField;
    private JTextField namabarang, txtIdTransaksi;
    public JTableRounded roundedTable;
    private JLabel totalValueLabel, kembalianLabel, kembalianValueLabel;
    private JButton btnTambah, btnBatal, btnBayar;
    private ComboboxCustom diskonComboBox;
    private Connection con;
    private String currentProductSize = "";

    private static final String ID_PREFIX = "TRJL_";
    private static final int PADDING_LENGTH = 5;

    private String NoRFID = "";
    private String namaUser = "";

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
                            hitungKembalian();
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
                                        hitungKembalian();
                                        PindahanAntarPopUp.showProdukBerhasilDihapus(parentFrame);
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
                FormKasir.getMainFrame().switchBackToProductPanelKasir();
                FormKasir.getMainFrame().getProductDisplayKasirPanel().refreshProducts();
                Productt.getMainFrame().getLaporanRefresh().refreshLaporan();
                clearTransactionTable();
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
        formPanel.add(createLabel("Scan Kode Produk", 15, 15));
        scanKodeField = createRoundedTextField(15, 40, 205, 35);
        scanKodeField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                // Hanya menerima digit angka dan tombol kontrol (backspace, delete, dll)
                if (!Character.isLetterOrDigit(c) && c != '_' && !Character.isISOControl(c)) {
                    e.consume(); // Mengabaikan karakter yang bukan angka
                }
            }
        });
        formPanel.add(scanKodeField);

        formPanel.add(createLabel("Nama Barang", 15, 80));
        namabarang = createRoundedTextField(15, 105, 205, 35);
        namabarang.setFocusable(false);
        formPanel.add(namabarang);

        // Tambahkan label dan combo box untuk diskon
        formPanel.add(createLabel("Atur Diskon", 15, 145));
        diskonComboBox = new ComboboxCustom(getDiscountOptionsFromDatabase());
        diskonComboBox.setBounds(15, 170, 205, 35);
        formPanel.add(diskonComboBox);

        formPanel.add(createLabel("Harga Beli", 15, 210));
        hargaBeliField = createRoundedTextField(15, 235, 205, 35);
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

        formPanel.add(createLabel("Masukkan Uang", 15, 315));
        masukuangfield = createRoundedTextField(15, 340, 205, 35);
        masukuangfield.setText("Rp. "); // Prefix "Rp. "
        masukuangfield.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                DefaultTableModel model = (DefaultTableModel) roundedTable.getTable().getModel();
                if (model.getRowCount() == 0) {
                    masukuangfield.setText("Rp. ");
                    PindahanAntarPopUp.showMasukkanBarangTerlebihDahulu(parentFrame);
                    return;
                } else {
                    enforceRpPrefix();
                    formatMasukkanUang();
                    hitungKembalian();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) { // Jika tombol Enter ditekan
                    String hargaInput = masukuangfield.getText().replace("Rp. ", "").replace(".", "").trim();
                    System.out.println(hargaInput); // Output hanya angka
                    hitungKembalian();
                }
            }
        });

        formPanel.add(masukuangfield);

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
        btnBatal.setBounds(15, 280, 100, 35);
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
        btnTambah.setBounds(120, 280, 100, 35);
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

        kembalianLabel = new JLabel("Kembalian");
        kembalianLabel.setForeground(Color.WHITE);
        kembalianLabel.setFont(new Font("Poppins", Font.PLAIN, 14));
        kembalianLabel.setBounds(20, -20, 80, 130);
        totalPanel.add(kembalianLabel);

        kembalianValueLabel = new JLabel("Rp. ");
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
                // Pastikan ada data di tabel sebelum menampilkan dialog pembayaran
                DefaultTableModel model = (DefaultTableModel) roundedTable.getTable().getModel();
                if (model.getRowCount() == 0) {
                    PindahanAntarPopUp.showTidakAdaItemYangDibeli(parentFrame);
                    return;
                }

                String Masuk = masukuangfield.getText().replace("Rp. ", "").replace(".", "").trim();
                if (Masuk.isEmpty()) {
                    PindahanAntarPopUp.showMasukkanUangTerlebihDahulu(parentFrame);
                    return;
                }

                String totalStr = totalValueLabel.getText().replace("Rp. ", "").replace(".", "").trim();

                try {
                    int uangMasuk = Integer.parseInt(Masuk);
                    int totalBelanja = Integer.parseInt(totalStr);

                    if (uangMasuk < totalBelanja) {
                        PindahanAntarPopUp.showTransJualMasukkanuangTidakBolehMinus(parentFrame);
                        return;
                    }

                    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(parentComponent);
                    final PopUp_BayarTransjual dialog = new PopUp_BayarTransjual(parentFrame);

                    // Tambahkan listener ke tombol OK
                    dialog.addOKButtonActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (saveTransactionToDatabase()) {
                                updateTotalAmount();
                                printReceipt();
                                txtIdTransaksi.setText(generateNextTransaksiId());
                                clearTransactionTable();
                                dialog.startCloseAnimation();
                            }
                        }
                    });

                    // Tampilkan dialog
                    dialog.setVisible(true);

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(parentFrame, "Format uang tidak valid.", "Kesalahan", JOptionPane.ERROR_MESSAGE);
                }
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

        txtIdTransaksi = new JTextField();
        txtIdTransaksi.setText(generateNextTransaksiId());
        txtIdTransaksi.setEditable(false);
        txtIdTransaksi.setBounds(300, 50, 235, 40);
        txtIdTransaksi.setVisible(false);
        mainPanel.add(txtIdTransaksi);

        mainPanel.add(btnBayar);
        actionButton();
        setNamaUser();
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

    private void formatMasukkanUang() {
        try {
            String text = masukuangfield.getText().replace("Rp. ", "").replace(".", "").trim();
            if (text.isEmpty()) {
                return;
            }
            int value = Integer.parseInt(text);
            masukuangfield.setText("Rp. " + formatter.format(value));
        } catch (NumberFormatException e) {
            masukuangfield.setText("Rp. ");
        }
    }

    private void enforceRpPrefix() {
        if (!hargaBeliField.getText().startsWith("Rp. ")) {
            hargaBeliField.setText("Rp. ");
        }
        if (!masukuangfield.getText().startsWith("Rp. ")) {
            masukuangfield.setText("Rp. ");
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
                        System.out.println(txtIdTransaksi.getText());
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
                PindahanAntarPopUp.showProdukTidakDitemukan(parentFrame);
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
            String kode = scanKodeField.getText();
            if (kode.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "Nama produk tidak boleh kosong", "Error", JOptionPane.ERROR_MESSAGE);
                PindahanAntarPopUp.showScanProdukTerlebihDahulu(parentFrame);
                return;
            }

            int stokTersedia = cekStokProduk(kode);
            if (stokTersedia <= 0) {
                PindahanAntarPopUp.showTransJualStokProdukTidakTersedia(parentFrame);
                return;
            }

            // Get price from hargaBeliField
            String hargaText = hargaBeliField.getText().replace("Rp. ", "").replace(".", "").trim();
            int harga = Integer.parseInt(hargaText);

            // Get discount from combobox
            String diskonText = diskonComboBox.getSelectedItem().toString();
            double diskon = 0;

            if (!diskonText.isEmpty()) {
                // Get the actual discount percentage from database based on the selected name
                try {
                    String query = "SELECT total_diskon FROM diskon WHERE nama_diskon = ? AND status = 'dipakai'";
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

                    if (currentQty + 1 > stokTersedia) {
                        PindahanAntarPopUp.showTransJualStokProdukTidakTersedia(parentFrame);
                        return;
                    }

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
            ex.printStackTrace();
        }
    }

    private int cekStokProduk(String kodeProduk) {
        int stokTersedia = 0;

        try {
            String query = "SELECT produk_sisa FROM kartu_stok WHERE id_produk = ? ORDER BY tanggal_transaksi DESC LIMIT 1";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, kodeProduk);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                stokTersedia = rs.getInt("produk_sisa");
            }

            rs.close();
            ps.close();
        } catch (SQLException ex) {
            System.err.println("Error saat memeriksa stok produk: " + ex.getMessage());
        }

        return stokTersedia;
    }

    public void updateTotalAmount() {
        double totalAmount = 0;
        DefaultTableModel model = (DefaultTableModel) roundedTable.getTable().getModel();

        for (int i = 0; i < model.getRowCount(); i++) {
            // Get the already calculated total price (after discount) from column 6
            String totalStr = model.getValueAt(i, 6).toString().replace("Rp. ", "").replace(".", "").replace(",", "");
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
                hitungKembalian();
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
            String query = "SELECT DISTINCT nama_diskon FROM diskon WHERE id_diskon != 'DS_00' AND status = 'dipakai' ORDER BY total_diskon ASC";
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

    private String generateNextTransaksiId() {
        String nextId = ID_PREFIX + "00001"; // Default jika belum ada transaksi

        try {
            // Query untuk mendapatkan ID transaksi terakhir dari database
            String query = "SELECT id_transaksijual FROM transaksi_jual ORDER BY id_transaksijual DESC LIMIT 1";
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String lastId = rs.getString("id_transaksijual");
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
            return false;
        }

        try {
            con.setAutoCommit(false);
            String norfid = NoRFID;

            String insertTransHeader = "INSERT INTO transaksi_jual (id_transaksijual, tanggal_transaksi, norfid) VALUES (?, ?, ?)";
            PreparedStatement psHeader = con.prepareStatement(insertTransHeader, Statement.RETURN_GENERATED_KEYS);

            String transactionId = txtIdTransaksi.getText();
            psHeader.setString(1, transactionId);

            java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(System.currentTimeMillis());
            psHeader.setTimestamp(2, currentTimestamp);

            psHeader.setString(3, norfid);
            psHeader.executeUpdate();

            for (int i = 0; i < rowCount; i++) {
                String productName = model.getValueAt(i, 1).toString();
                String productSize = model.getValueAt(i, 2).toString();
                System.out.println(productName);
                System.out.println(productSize);
                int quantity = Integer.parseInt(model.getValueAt(i, 3).toString());

                String priceStr = model.getValueAt(i, 4).toString().replace("Rp. ", "").replace(".", "");
                double price = Double.parseDouble(priceStr);

                String discountStr = model.getValueAt(i, 5).toString();
                String discountId = "DS_00";
                if (!discountStr.equals("-")) {
                    discountId = getDiscountId(discountStr);
                    System.out.println(discountId);
                }

                String totalStr = model.getValueAt(i, 6).toString().replace("Rp. ", "").replace(".", "");
                double totalPrice = Double.parseDouble(totalStr);

                String productId = getProductIdFromNameAndSize(productName, productSize);
                System.out.println(productId);

                double hargaModal = getHargaModal(productId);
                double laba = (price - hargaModal) * quantity;

                String insertDetail = "INSERT INTO detail_transaksijual (id_produk, id_transaksijual, total_harga, jumlah_produk, id_diskon, laba) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement psDetail = con.prepareStatement(insertDetail);

                psDetail.setString(1, productId);
                psDetail.setString(2, transactionId);
                psDetail.setDouble(3, totalPrice);
                psDetail.setInt(4, quantity);
                psDetail.setString(5, discountId);
                psDetail.setDouble(6, laba); 

                psDetail.executeUpdate();
                psDetail.close();
            }

            con.commit();
            con.setAutoCommit(true);

            PindahanAntarPopUp.showSuksesBayarTransjual(parentFrame);
            return true;

        } catch (SQLException ex) {
            try {
                con.rollback();
                con.setAutoCommit(true);
            } catch (SQLException rollbackEx) {
                System.err.println("Error melakukan rollback transaksi: " + rollbackEx.getMessage());
            }

            JOptionPane.showMessageDialog(parentComponent,
                    "Error menyimpan transaksi: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            return false;
        }
    }

    private double getHargaModal(String idProduk) {
        double hargaModal = 0.0;

        try {
            String query = "SELECT harga_beli FROM produk WHERE id_produk = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, idProduk);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                hargaModal = rs.getDouble("harga_beli");
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Gagal mengambil harga beli: " + e.getMessage());
        }

        return hargaModal;
    }

    public void clearTransactionTable() {
        DefaultTableModel model = (DefaultTableModel) roundedTable.getTable().getModel();
        model.setRowCount(0); // Remove all rows

        // Clear related fields
        scanKodeField.setText("");
        namabarang.setText("");
        hargaBeliField.setText("Rp. ");
        diskonComboBox.setSelectedIndex(0);
        totalValueLabel.setText("Rp. ");
        kembalianValueLabel.setText("Rp. ");
        masukuangfield.setText("");

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

    private String getDiscountId(String discountStr) {
        String discountId = "DS_00";
        try {
            // Menghilangkan karakter % jika ada
            String cleanDiscount = discountStr.replace("%", "").trim();

            // Konversi string diskon ke float untuk pencarian
            float discountValue = Float.parseFloat(cleanDiscount);

            // Query untuk mendapatkan ID diskon berdasarkan nilai total_diskon
            String query = "SELECT id_diskon FROM diskon WHERE total_diskon = ? AND status = 'dipakai'";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setFloat(1, discountValue);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                // Konversi id_diskon dari char(5) ke int
                String strDiscountId = rs.getString("id_diskon");
                // Parse menjadi integer
                discountId = strDiscountId;
            }

            rs.close();
            pst.close();
        } catch (SQLException e) {
            System.err.println("Error mengambil ID diskon: " + e.getMessage());
            e.printStackTrace();
        }

        return discountId;
    }

    private void printReceipt() {
        try {
            // Membuat objek PrinterJob
            PrinterJob pj = PrinterJob.getPrinterJob();

            // Set printable untuk menentukan konten yang akan dicetak
            pj.setPrintable(new BillPrintable(), getPageFormat(pj));

            // Mencoba untuk mencetak
            if (pj.printDialog()) {
                pj.print();
//                JOptionPane.showMessageDialog(parentComponent, "Struk berhasil dicetak", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                PindahanAntarPopUp.showStrukBerhasilDicetak(parentFrame);
            }
        } catch (PrinterException ex) {
            PindahanAntarPopUp.showTransJualCetakStrukDibatalkan(parentFrame);
            ex.printStackTrace();
        }
    }

    private PageFormat getPageFormat(PrinterJob pj) {
        PageFormat pf = pj.defaultPage();
        Paper paper = pf.getPaper();

        // IMPORTANT FIX: Set EXACT thermal receipt width
        // Standard 80mm thermal width converted to points
        double width = convertMmToPpi(100); // Changed from 100mm to standard 80mm width

        // Calculate receipt height based on content
        double height = calculateDynamicHeight();

        // IMPROVED FIX: Consistent margins for proper alignment
        double marginLeft = convertMmToPpi(0);
        double marginRight = convertMmToPpi(3);
        double marginTop = convertMmToPpi(1);
        double marginBottom = convertMmToPpi(3);

        paper.setSize(width, height);
        // Set imageable area with consistent margins
        paper.setImageableArea(
                marginLeft,
                marginTop,
                width - (marginLeft + marginRight),
                height - (marginTop + marginBottom));

        pf.setPaper(paper);
        pf.setOrientation(PageFormat.PORTRAIT);

        return pf;
    }

// Improved dynamic height calculation with better handling for many items
    private double calculateDynamicHeight() {
        // Get data from table
        DefaultTableModel model = (DefaultTableModel) roundedTable.getTable().getModel();
        int rowCount = model.getRowCount();

        // Fixed components height (header, transaction info, footers)
        double fixedHeight = 200;

        // Calculate exact space needed for each product row with better estimation
        double itemsHeight = 0;
        for (int i = 0; i < rowCount; i++) {
            // Base height for product name and quantity
            double rowHeight = 30;

            // Get product name to check length
            String productName = model.getValueAt(i, 1).toString();
            String size = model.getValueAt(i, 2).toString();
            String combinedName = productName + " (" + size + ")";

            // Add extra space for long product names that might wrap
            if (combinedName.length() > 30) {
                rowHeight += 15; // Add space for potential wrapping
            }

            // Add space for discount line if applicable
            String diskon = model.getValueAt(i, 5).toString();
            if (!diskon.equals("-")) {
                rowHeight += 15;
            }

            itemsHeight += rowHeight;
        }

        // Calculate minimum height needed with improved estimation
        double contentHeight = fixedHeight + itemsHeight;

        // Add padding to ensure blank space at bottom
        double paddingBottom = convertMmToPpi(30); // 30mm padding at bottom

        // Ensure minimum paper length even for few items
        double minimumHeight = convertMmToPpi(150); // At least 150mm

        return Math.max(contentHeight + paddingBottom, minimumHeight);
    }

    protected static double convertMmToPpi(double mm) {
        return mm * 72 / 25.4;
    }

    public class BillPrintable implements Printable {

        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
            if (pageIndex > 0) {
                return NO_SUCH_PAGE;
            }

            Graphics2D g2d = (Graphics2D) graphics;

            // Enable anti-aliasing for smoother text
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Translate to the correct starting position
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            // Get the EXACT printable width
            double paperWidth = pageFormat.getImageableWidth();

            // Define exact positions - use full width with proper buffer for right margin
            int leftEdge = 0;
            int rightEdge = (int) (paperWidth - 3); // Add 2pt buffer to ensure right margin

            // Define fonts
            Font titleFont = new Font("Monospaced", Font.BOLD, 12);
            Font normalFont = new Font("Monospaced", Font.PLAIN, 10);
            Font smallFont = new Font("Monospaced", Font.PLAIN, 9);

            int y = 15; // Starting y position

            // Store name and address
            g2d.setFont(titleFont);
            drawCenteredString(g2d, "Karunia Store", (float) paperWidth, y);
            y += 15;

            g2d.setFont(smallFont);
            drawCenteredString(g2d, "Jalan Jawa no 1B, depan bunderan DPRD", (float) paperWidth, y);
            y += 15;

            // First separator
            drawSeparatorLine(g2d, leftEdge, y, rightEdge, y);
            y += 15;

            // Transaction details
            g2d.setFont(normalFont);

            // FIX: Ensure transaction ID is properly aligned
            String transactionId = txtIdTransaksi.getText();
            drawFixedWidthText(g2d, "ID Transaksi :", transactionId, leftEdge, rightEdge, y);
            y += 15;

            // Date & Time
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            Date currentDate = new Date();

            drawFixedWidthText(g2d, "Tanggal :", dateFormat.format(currentDate), leftEdge, rightEdge, y);
            y += 15;

            drawFixedWidthText(g2d, "Waktu :", timeFormat.format(currentDate), leftEdge, rightEdge, y);
            y += 15;

            drawFixedWidthText(g2d, "Kasir :", namaUser, leftEdge, rightEdge, y);
            y += 15;

            // Item separator
            drawSeparatorLine(g2d, leftEdge, y, rightEdge, y);
            y += 15;

            // Get data from table
            DefaultTableModel model = (DefaultTableModel) roundedTable.getTable().getModel();
            double totalBeforeDiscount = 0;
            double totalDiscount = 0;

            // Items section - IMPROVED RENDERING FOR LONG PRODUCT NAMES
            for (int i = 0; i < model.getRowCount(); i++) {
                String nama = model.getValueAt(i, 1).toString();
                String size = model.getValueAt(i, 2).toString();
                int qty = Integer.parseInt(model.getValueAt(i, 3).toString());
                String harga = model.getValueAt(i, 4).toString();
                String diskon = model.getValueAt(i, 5).toString();
                String total = model.getValueAt(i, 6).toString();

                // Calculate values for totals
                double itemPrice = parseAmount(harga);
                double itemTotal = parseAmount(total);
                double itemDiscount = (itemPrice * qty) - itemTotal;

                totalBeforeDiscount += (itemPrice * qty);
                totalDiscount += itemDiscount;

                // Product name with size
                String itemDetail = nama + " (" + size + ")";

                // IMPROVED SOLUTION: Handle long product names better with proper wrapping
                FontMetrics metrics = g2d.getFontMetrics();

                // New dynamic allocation - give more space to product names
                // Use 65% for left column when dealing with long names
                drawAdaptiveWidthText(g2d, itemDetail, harga, leftEdge, rightEdge, y, metrics);
                y += 15;

                // Quantity line
                g2d.drawString("x" + qty, leftEdge + 5, y);
                y += 15;

                // Discount line if applicable
                if (!diskon.equals("-")) {
                    String discountText = "Diskon (" + diskon + ")";
                    String discountAmount = "-Rp. " + formatter.format(itemDiscount);
                    drawFixedWidthText(g2d, discountText, discountAmount, leftEdge, rightEdge, y);
                    y += 15;
                }
            }

            // Separator before subtotals
            drawSeparatorLine(g2d, leftEdge, y, rightEdge, y);
            y += 15;

            // Subtotals
            drawFixedWidthText(g2d, "Sub Total Diskon:", "Rp. " + formatter.format(totalDiscount), leftEdge, rightEdge, y);
            y += 15;

            drawFixedWidthText(g2d, "Sub Total :", "Rp. " + formatter.format(totalBeforeDiscount), leftEdge, rightEdge, y);
            y += 15;

            // Separator before totals
            drawSeparatorLine(g2d, leftEdge, y, rightEdge, y);
            y += 15;

            // Total after discount
            double totalAfterDiscount = parseAmount(totalValueLabel.getText());
            drawFixedWidthText(g2d, "Total setelah Diskon :", "Rp. " + formatter.format(totalAfterDiscount), leftEdge, rightEdge, y);
            y += 15;

            // Payment
            double payment = parseAmount(masukuangfield.getText());
            drawFixedWidthText(g2d, "Bayar :", "Rp. " + formatter.format(payment), leftEdge, rightEdge, y);
            y += 15;

            // Final separator
            drawSeparatorLine(g2d, leftEdge, y, rightEdge, y);
            y += 15;

            // Change
            double change = payment - totalAfterDiscount;
            drawFixedWidthText(g2d, "Kembalian :", "Rp. " + formatter.format(change), leftEdge, rightEdge, y);
            y += 20;

            // Thank you message
            g2d.setFont(smallFont);
            drawCenteredString(g2d, "Terima Kasih telah Berbelanja!", (float) paperWidth, y);

            return PAGE_EXISTS;
        }

        // Helper method to parse currency amounts
        private double parseAmount(String amount) {
            return Double.parseDouble(amount.replace("Rp. ", "").replace(".", ""));
        }

        // NEW IMPROVED METHOD: Handle text layout with dynamic left/right allocation
        private void drawAdaptiveWidthText(Graphics2D g2d, String leftText, String rightText,
                int leftEdge, int rightEdge, int y, FontMetrics metrics) {
            int totalWidth = rightEdge - leftEdge;
            int rightTextWidth = metrics.stringWidth(rightText);

            // Reserve space for right text plus buffer
            int rightColumnWidth = rightTextWidth + 10; // 10pt safety buffer

            // Calculate available width for left text
            int leftColumnWidth = totalWidth - rightColumnWidth;

            // Ensure minimum widths for both columns
            if (leftColumnWidth < totalWidth * 0.5) {
                leftColumnWidth = (int) (totalWidth * 0.5);
                rightColumnWidth = totalWidth - leftColumnWidth;
            }

            // Check if left text needs truncation
            String displayLeftText = leftText;
            int leftTextWidth = metrics.stringWidth(leftText);

            if (leftTextWidth > leftColumnWidth) {
                int charWidth = leftTextWidth / leftText.length(); // Approximate width per character
                int maxChars = (leftColumnWidth - metrics.stringWidth("...")) / charWidth;

                if (maxChars > 3) {
                    displayLeftText = leftText.substring(0, maxChars - 3) + "...";
                }
            }

            // Draw left text aligned to left edge
            g2d.drawString(displayLeftText, leftEdge, y);

            // Calculate exact position for right text
            int rightTextX = rightEdge - rightTextWidth;

            // Draw right text perfectly aligned to right edge
            g2d.drawString(rightText, rightTextX, y);
        }

        // OPTIMIZED method for standard text with fixed ratios
        private void drawFixedWidthText(Graphics2D g2d, String leftText, String rightText,
                int leftEdge, int rightEdge, int y) {
            FontMetrics metrics = g2d.getFontMetrics();
            int totalWidth = rightEdge - leftEdge;

            // Allow more space for standard text fields
            int rightTextWidth = metrics.stringWidth(rightText);
            int leftTextWidth = metrics.stringWidth(leftText);

            // Determine if we need special handling for transaction info or totals
            boolean isTransactionInfo = leftText.contains("ID Transaksi")
                    || leftText.contains("Tanggal")
                    || leftText.contains("Waktu")
                    || leftText.contains("Kasir");

            boolean isTotalInfo = leftText.contains("Total")
                    || leftText.contains("Bayar")
                    || leftText.contains("Kembalian")
                    || leftText.contains("Sub Total");

            // Adjust ratio based on content type
            double leftRatio = 0.6; // Default 60% for left column

            if (isTransactionInfo) {
                leftRatio = 0.5; // Equal distribution for transaction info
            } else if (isTotalInfo) {
                leftRatio = 0.65; // Give more space to totals text
            }

            int leftColumnWidth = (int) (totalWidth * leftRatio);

            // Draw left text aligned to left edge
            g2d.drawString(leftText, leftEdge, y);

            // Calculate exact position for right text with proper right margin
            int rightTextX = rightEdge - rightTextWidth;

            // Draw right text aligned to right edge
            g2d.drawString(rightText, rightTextX, y);
        }

        // Draw perfectly centered text
        private void drawCenteredString(Graphics2D g2d, String text, float width, int y) {
            FontMetrics metrics = g2d.getFontMetrics();
            int x = (int) ((width - metrics.stringWidth(text)) / 2);
            g2d.drawString(text, x, y);
        }

        // Draw separator line with consistent dashes
        private void drawSeparatorLine(Graphics2D g2d, int x1, int y1, int x2, int y2) {
            Stroke oldStroke = g2d.getStroke();

            // Use consistent dash pattern for better appearance
            float[] dash = {3.0f, 2.0f};
            g2d.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));

            // Draw line across full width
            g2d.drawLine(x1, y1, x2, y2);
            g2d.setStroke(oldStroke);
        }
    }

    // Tambahkan fungsi untuk menghitung kembalian
    private void hitungKembalian() {
        try {
            // Ambil nilai dari masukuangfield (uang yang diberikan)
            String uangDiberikanStr = masukuangfield.getText().replace("Rp. ", "").replace(".", "").trim();

            // Ambil nilai dari totalValueLabel (total belanja)
            String totalBelanjaStr = totalValueLabel.getText().replace("Rp. ", "").replace(".", "").trim();

            // Konversi ke integer
            int uangDiberikan = 0;
            int totalBelanja = 0;

            if (!uangDiberikanStr.isEmpty()) {
                uangDiberikan = Integer.parseInt(uangDiberikanStr);
            }

            if (!totalBelanjaStr.isEmpty()) {
                totalBelanja = Integer.parseInt(totalBelanjaStr);
            }

            // Hitung kembalian
            int kembalian = uangDiberikan - totalBelanja;

            // Format hasil kembalian
            String formattedKembalian = "Rp. " + formatRupiah(kembalian);

            // Update kembalianValueLabel
            kembalianValueLabel.setText(formattedKembalian);

        } catch (NumberFormatException ex) {
            // Tangani error jika input bukan angka
            kembalianValueLabel.setText("Rp. 0");
        }
    }

// Fungsi untuk memformat angka menjadi format Rupiah
    private String formatRupiah(int nilai) {
        if (nilai < 0) {
            return "0"; // Jika kembalian negatif, tampilkan 0
        }

        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(nilai).replace(",", ".");
    }

    public void addProductToTable(String productName, String size, int quantity,
            String price, String discount, String total) {
        DefaultTableModel model = (DefaultTableModel) roundedTable.getTable().getModel();

        // Check if the product already exists in the table
        boolean productExists = false;
        for (int i = 0; i < model.getRowCount(); i++) {
            String existingName = model.getValueAt(i, 1).toString();
            String existingSize = model.getValueAt(i, 2).toString();

            if (existingName.equals(productName) && existingSize.equals(size)) {
                // Update quantity and total
                int currentQty = Integer.parseInt(model.getValueAt(i, 3).toString());
                int newQty = currentQty + quantity;
                model.setValueAt(newQty, i, 3);

                // Calculate new total
                double priceValue = Double.parseDouble(price.replace("Rp. ", "").replace(".", ""));
                double totalValue = priceValue * newQty;
                model.setValueAt("Rp. " + formatter.format(totalValue), i, 6);

                productExists = true;
                break;
            }
        }

        // If product doesn't exist, add new row
        if (!productExists) {
            model.addRow(new Object[]{
                model.getRowCount() + 1, // Row number
                productName,
                size,
                quantity,
                price,
                discount,
                total,
                "" // Action column
            });
        }
    }
}
