package PopUp_all;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import javax.swing.*;
import javax.swing.table.*;
import java.util.Vector;
import SourceCode.*;
import db.conn;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Date;

public class PopUp_LaporanDetailPemasukanAtautransaksiJual extends JDialog {

    private JComponent glassPane;
    private JFrame parentFrame;
    private JLabel titleLabel, infoLabel, totalLabel;
    private JPanel contentPanel;
    private JTableRounded tablePemasukan; 
    private JButton batalButton;

    private final int RADIUS = 20;
    private final int FINAL_WIDTH = 1100; 
    private final int FINAL_HEIGHT = 650;
    private final int ANIMATION_DURATION = 300;
    private final int ANIMATION_DELAY = 10;
    private float currentScale = 0.01f;
    private boolean animationStarted = false;
    private Timer animationTimer;
    private Timer closeAnimationTimer;
    private PaymentCallback paymentCallback;
    private int rowIndex;

    // Flag to avoid adding glassPane multiple times
    private static boolean isShowingPopup = false;
    private String kodeTransaksi; 
    private Connection con;

    // Tambahkan interface callback
    public interface PaymentCallback {

        void onPaymentSuccess(int rowIndex);
    }

    // Di PopUp_DetailGajiKaryawan.java
    public void setPaymentCallback(PaymentCallback callback, int rowIndex) {
        this.paymentCallback = callback;
        this.rowIndex = rowIndex;
    }

    public PopUp_LaporanDetailPemasukanAtautransaksiJual(JFrame parent) {
        this.parentFrame = parent;

        con = conn.getConnection();

        setModal(true);
        setPreferredSize(new Dimension(FINAL_WIDTH, FINAL_HEIGHT));

        // Check if popup is already being displayed
        if (isShowingPopup) {
            dispose();
            return;
        }
        isShowingPopup = true;

        // Create transparent overlay with semi-transparent black color
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

        createComponents();

        // Add WindowListener to clean up when popup is closed
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanupAndClose();
            }
        });

        startScaleAnimation();
    }

    private void createComponents() {
        // Buat label terpisah
        titleLabel = new JLabel("Detail Pemasukan: -");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(0, 10, FINAL_WIDTH, 30);
        contentPanel.add(titleLabel);

        infoLabel = new JLabel("Tanggal: - | Kasir: -");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoLabel.setBounds(0, 40, FINAL_WIDTH, 25);
        contentPanel.add(infoLabel);

        totalLabel = new JLabel("Total Transaksi: Rp. 0");
        totalLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        totalLabel.setForeground(new Color(102, 102, 102));
        totalLabel.setHorizontalAlignment(SwingConstants.CENTER);
        totalLabel.setBounds(0, 65, FINAL_WIDTH, 20);
        contentPanel.add(totalLabel);

        // Create table columns for transaction details - urutan diubah
        String[] columnNames = {"No", "Nama Produk", "Harga Beli", "Harga Jual", "Jumlah", "Diskon", "Total", "Laba Kotor"};

        // Create JTableRounded using the proper implementation
        tablePemasukan = new JTableRounded(columnNames);
        tablePemasukan.setSize(FINAL_WIDTH - 50, 430);

        // Configure table formatting
        JTable table = tablePemasukan.getTable();
        table.setRowHeight(30);
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(230, 230, 230));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setEnabled(false); // Make table non-editable

        // Adjusted column widths to fit all columns properly
        tablePemasukan.setColumnWidth(0, 50);   // No
        tablePemasukan.setColumnWidth(1, 250);  // Nama Produk (reduced slightly)
        tablePemasukan.setColumnWidth(2, 150);  // Harga Beli
        tablePemasukan.setColumnWidth(3, 150);  // Harga Jual
        tablePemasukan.setColumnWidth(4, 80);   // Jumlah (increased)
        tablePemasukan.setColumnWidth(5, 80);   // Diskon (increased)
        tablePemasukan.setColumnWidth(6, 130);  // Total
        tablePemasukan.setColumnWidth(7, 130);  // Laba

        // Set custom cell renderer for CENTER alignment for ALL columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                
                // Center alignment for ALL columns
                label.setHorizontalAlignment(JLabel.CENTER);

                return label;
            }
        };

        // Apply center renderer to all columns
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Lock table to prevent column reordering and resizing
        tablePemasukan.lockTablePosition();
        tablePemasukan.setColumnsResizable(false);

        // Place table in a scrollable container to ensure all rows are visible
        JScrollPane scrollPane = new JScrollPane(tablePemasukan.getTable());
        scrollPane.setBounds(25, 100, FINAL_WIDTH - 50, 430);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Memastikan scroll pane menampilkan semua data
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        contentPanel.add(scrollPane);

        // Cancel Button
        batalButton = createButton("TUTUP", new Color(255, 77, 77), Color.WHITE);
        batalButton.setBounds((FINAL_WIDTH - 200) / 2, FINAL_HEIGHT - 70, 200, 45);
        batalButton.addActionListener(e -> startCloseAnimation());
        contentPanel.add(batalButton);
    }

    private JButton createButton(String text, Color background, Color foreground) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(background);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setForeground(foreground);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
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

    /**
     * Method untuk memuat detail transaksi berdasarkan kode transaksi
     *
     * @param kodeTransaksi ID transaksi yang akan ditampilkan detailnya
     */
   public void loadTransactionDetails(String kodeTransaksi) {
    this.kodeTransaksi = kodeTransaksi;

    String query = "SELECT dtj.id_transaksijual, p.nama_produk, p.harga_jual, p.harga_beli, "
            + "dtj.total_harga, dtj.jumlah_produk, dtj.id_diskon, dtj.laba, " // ← tambahkan dtj.laba
            + "tj.tanggal_transaksi, u.nama_user, "
            + "COALESCE(d.total_diskon, 0) as total_diskon "
            + "FROM detail_transaksijual dtj "
            + "JOIN produk p ON dtj.id_produk = p.id_produk "
            + "JOIN transaksi_jual tj ON dtj.id_transaksijual = tj.id_transaksijual "
            + "JOIN user u ON tj.norfid = u.norfid "
            + "LEFT JOIN diskon d ON dtj.id_diskon = d.id_diskon "
            + "WHERE tj.id_transaksijual = ? "
            + "ORDER BY dtj.id_transaksijual";

    try {
        PreparedStatement stmt = con.prepareStatement(query);
        stmt.setString(1, kodeTransaksi);
        ResultSet rs = stmt.executeQuery();

        DefaultTableModel model = (DefaultTableModel) tablePemasukan.getTable().getModel();
        model.setRowCount(0);

        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        SimpleDateFormat displayDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        int no = 1;
        double grandTotal = 0;
        String tanggalTransaksi = "";
        String namaKasir = "";

        while (rs.next()) {
            String namaProduk = rs.getString("nama_produk");
            double hargaJual = rs.getDouble("harga_jual");
            double hargaBeli = rs.getDouble("harga_beli");
            int jumlah = rs.getInt("jumlah_produk");
            double subtotal = rs.getDouble("total_harga");
            double totalDiskon = rs.getDouble("total_diskon");
            float laba = rs.getFloat("laba"); 

            String hargaBeliFormatted = "Rp. " + decimalFormat.format(hargaBeli);
            String hargaJualFormatted = "Rp. " + decimalFormat.format(hargaJual);
            String subtotalFormatted = "Rp. " + decimalFormat.format(subtotal);
            String labaFormatted = "Rp. " + decimalFormat.format(laba);

            String diskonFormatted = totalDiskon > 0
                    ? decimalFormat.format(totalDiskon) + "%"
                    : "-";

            model.addRow(new Object[]{
                no++,
                namaProduk,
                hargaBeliFormatted,
                hargaJualFormatted,
                jumlah,
                diskonFormatted,
                subtotalFormatted,
                labaFormatted
            });

            grandTotal += subtotal;

            if (no == 2) {
                Date tanggalRaw = rs.getTimestamp("tanggal_transaksi");
                tanggalTransaksi = displayDateFormat.format(tanggalRaw);
                namaKasir = rs.getString("nama_user");
            }
        }

        if (!tanggalTransaksi.isEmpty()) {
            titleLabel.setText("Detail Pemasukan: " + kodeTransaksi);
            infoLabel.setText("Tanggal: " + tanggalTransaksi + " | Kasir: " + namaKasir);
            totalLabel.setText("Total Transaksi: Rp. " + decimalFormat.format(grandTotal));
        }

        rs.close();
        stmt.close();

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this,
                "Error saat mengambil detail transaksi: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}


    /**
     * Overloaded method untuk backward compatibility
     */
    public void loadEmployeeData(String norfid, String startDate, String endDate) {
        // This method is kept for backward compatibility but redirects to transaction details
        loadTransactionDetails(norfid);
    }

    // Metode untuk memformat nilai rupiah
    private String formatRupiah(int value) {
        StringBuilder result = new StringBuilder();
        String valueStr = String.valueOf(value);

        for (int i = 0; i < valueStr.length(); i++) {
            if (i > 0 && (valueStr.length() - i) % 3 == 0) {
                result.append('.');
            }
            result.append(valueStr.charAt(i));
        }

        return result.toString();
    }
}