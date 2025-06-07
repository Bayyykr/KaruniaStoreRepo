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

public class PopUp_LaporanDetailPengeluaranAtautransaksibeli extends JDialog {

    private JComponent glassPane;
    private JFrame parentFrame;
    private JLabel titleLabel, infoLabel, totalLabel;
    private JPanel contentPanel;
    private JTableRounded tablePengeluaran;
    private JButton batalButton;

    private final int RADIUS = 20;
    private final int FINAL_WIDTH = 900;
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

    public PopUp_LaporanDetailPengeluaranAtautransaksibeli(JFrame parent) {
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
        titleLabel = new JLabel("Detail Pengeluaran: -");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(0, 10, FINAL_WIDTH, 30);
        contentPanel.add(titleLabel);

        infoLabel = new JLabel("Tanggal: -");
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

        String[] columnNames = {"No", "Nama Produk", "Harga Beli", "Jumlah", "Total"};

        // Create JTableRounded using the proper implementation
        tablePengeluaran = new JTableRounded(columnNames);
        tablePengeluaran.setSize(FINAL_WIDTH - 50, 430);

        JTable table = tablePengeluaran.getTable();
        table.setRowHeight(30);
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(230, 230, 230));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setEnabled(false);

        tablePengeluaran.setColumnWidth(0, 50);   
        tablePengeluaran.setColumnWidth(1, 300);  
        tablePengeluaran.setColumnWidth(2, 180);  
        tablePengeluaran.setColumnWidth(3, 100);  
        tablePengeluaran.setColumnWidth(4, 180);  

        // Set custom cell renderer for alignment
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

                if (column == 0 || column == 3) { 
                    label.setHorizontalAlignment(JLabel.CENTER);
                } else if (column == 2 || column == 4) { 
                    label.setHorizontalAlignment(JLabel.CENTER);
                } else {
                    label.setHorizontalAlignment(JLabel.CENTER);
                }
                return label;
            }
        };

        // Apply renderer to all columns
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        tablePengeluaran.lockTablePosition();
        tablePengeluaran.setColumnsResizable(false);

        JScrollPane scrollPane = new JScrollPane(tablePengeluaran.getTable());
        scrollPane.setBounds(25, 100, FINAL_WIDTH - 50, 430);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);

        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        contentPanel.add(scrollPane);

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
    public void loadPengeluaranTransactionDetails(String kodeTransaksi) {
        this.kodeTransaksi = kodeTransaksi;

        // PERBAIKAN: Query disesuaikan dengan struktur database yang benar
        String query = "SELECT dtb.id_transaksibeli, p.nama_produk, p.harga_beli, "
                + "dtb.total_harga, dtb.jumlah_produk, "
                + "tb.tanggal_transaksi, u.nama_user "
                + "FROM detail_transaksibeli dtb "
                + "JOIN produk p ON dtb.id_produk = p.id_produk "
                + "JOIN transaksi_beli tb ON dtb.id_transaksibeli = tb.id_transaksibeli "
                + "JOIN user u ON tb.norfid = u.norfid "
                + "WHERE tb.id_transaksibeli = ? "
                + "ORDER BY dtb.id_transaksibeli";

        try {
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, kodeTransaksi);
            ResultSet rs = stmt.executeQuery();

            DefaultTableModel model = (DefaultTableModel) tablePengeluaran.getTable().getModel();
            model.setRowCount(0); // Clear existing data

            DecimalFormat decimalFormat = new DecimalFormat("#,###");
            decimalFormat.setGroupingUsed(true);
            SimpleDateFormat displayDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            int no = 1;
            double grandTotal = 0;
            String tanggalTransaksi = "";
            String namaOwner = "";

            while (rs.next()) {
                String namaProduk = rs.getString("nama_produk");
                double hargaBeli = rs.getDouble("harga_beli");
                double totalHarga = rs.getDouble("total_harga");
                int jumlahProduk = rs.getInt("jumlah_produk");

                // Format harga dalam rupiah
                String hargaBeliFormatted = "Rp. " + decimalFormat.format(hargaBeli);
                String totalHargaFormatted = "Rp. " + decimalFormat.format(totalHarga);

                model.addRow(new Object[]{
                    no++,
                    namaProduk,
                    hargaBeliFormatted,
                    jumlahProduk,
                    totalHargaFormatted
                });

                grandTotal += totalHarga;

                if (no == 2) {
                    Date tanggalRaw = rs.getTimestamp("tanggal_transaksi");
                    if (tanggalRaw != null) {
                        tanggalTransaksi = displayDateFormat.format(tanggalRaw);
                    }
                    namaOwner = rs.getString("nama_user");
                }
            }

            // Update label header dengan informasi transaksi
            if (!tanggalTransaksi.isEmpty()) {
                titleLabel.setText("Detail Pengeluaran: " + kodeTransaksi);
                infoLabel.setText("Tanggal: " + tanggalTransaksi + " | Owner: " + (namaOwner != null ? namaOwner : "-"));
                totalLabel.setText("Total Transaksi: Rp. " + decimalFormat.format(grandTotal));
            } else {
                // Jika tidak ada data ditemukan
                titleLabel.setText("Detail Pengeluaran: " + kodeTransaksi);
                infoLabel.setText("Data tidak ditemukan");
                totalLabel.setText("Total Transaksi: Rp. 0");
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error saat mengambil detail transaksi: " + e.getMessage(),
                    "Error Database",
                    JOptionPane.ERROR_MESSAGE);

            // Set default values jika terjadi error
            titleLabel.setText("Detail Pengeluaran: " + kodeTransaksi);
            infoLabel.setText("Error mengambil data");
            totalLabel.setText("Total Transaksi: Rp. 0");
        }
    }

    /**
     * Overloaded method untuk backward compatibility
     */
    public void loadEmployeeData(String norfid, String startDate, String endDate) {
        // This method is kept for backward compatibility but redirects to transaction details
        loadPengeluaranTransactionDetails(norfid);
    }

    // Metode untuk memformat nilai rupiah
    private String formatRupiah(int value) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        return "Rp. " + decimalFormat.format(value);
    }
}
