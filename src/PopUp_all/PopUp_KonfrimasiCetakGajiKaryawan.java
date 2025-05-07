package PopUp_all;

import Form.LoginForm;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import javax.swing.*;
import javax.swing.table.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import SourceCode.*;

import db.conn;
import java.text.ParseException;
import java.util.Calendar;

public class PopUp_KonfrimasiCetakGajiKaryawan extends JDialog {

    private JComponent glassPane;
    private JFrame parentFrame;
    private JPanel contentPanel;
    private JTableRounded tableGaji; // Using the proper JTableRounded class
    private JButton batalButton, bayarButton;

    private final int RADIUS = 20;
    private final int FINAL_WIDTH = 450;
    private final int FINAL_HEIGHT = 580;
    private final int ANIMATION_DURATION = 300;
    private final int ANIMATION_DELAY = 10;
    private float currentScale = 0.01f;
    private boolean animationStarted = false;
    private Timer animationTimer;
    private Timer closeAnimationTimer;
    private PaymentCallback paymentCallback;
    private int rowIndex;
    private String norfid, starDate, endDate; // Store employee's RFID

    // Flag to avoid adding glassPane multiple times
    private static boolean isShowingPopup = false;
    private Connection con;
    private String owner, periodeBulanTahun;

    // Tambahkan interface callback
    public interface PaymentCallback {

        void onPaymentSuccess(int rowIndex);
    }

    // Di PopUp_DetailGajiKaryawan.java
    public void setPaymentCallback(PaymentCallback callback, int rowIndex) {
        this.paymentCallback = callback;
        this.rowIndex = rowIndex;
    }

    public PopUp_KonfrimasiCetakGajiKaryawan(JFrame parent, String norfid, String startDate, String endDate) {
        super(parent, "Form Gaji", true);
        this.parentFrame = parent;
        this.norfid = norfid;
        this.starDate = startDate;
        this.endDate = endDate;
        setModal(true);
        setPreferredSize(new Dimension(FINAL_WIDTH, FINAL_HEIGHT));

        con = conn.getConnection();

        setNamaUser();

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
        // Form Title
        JLabel titleLabel = new JLabel("Form Gaji");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(0, 20, FINAL_WIDTH, 30);
        contentPanel.add(titleLabel);

        // Create table columns
        String[] columnNames = {"Informasi", "Keterangan"};

        // Create JTableRounded using the proper implementation
        tableGaji = new JTableRounded(columnNames);
        tableGaji.setSize(FINAL_WIDTH - 50, 460);

        if (norfid != null) {
            loadEmployeeData(norfid, starDate, endDate);
        } else {
            System.out.println("karyawan tidak ada");
        }

        // Configure table formatting
        JTable table = tableGaji.getTable();
        table.setRowHeight(30);
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(230, 230, 230));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setEnabled(false); // Make table non-editable

        // Set column widths
        tableGaji.setColumnWidth(0, 170);
        tableGaji.setColumnWidth(1, 200);

        // Set custom cell renderer for alignment
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

                if (column == 0) {
                    label.setHorizontalAlignment(JLabel.LEFT);
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

        // Lock table to prevent column reordering and resizing
        tableGaji.lockTablePosition();
        tableGaji.setColumnsResizable(false);

        // Place table in the content panel
        tableGaji.setBounds(25, 60, FINAL_WIDTH - 50, 400);
        contentPanel.add(tableGaji);

        // Cancel Button
        batalButton = createButton("BATAL", new Color(255, 77, 77), Color.WHITE);
        batalButton.setBounds(25, FINAL_HEIGHT - 70, (FINAL_WIDTH - 60) / 2, 45);
        batalButton.addActionListener(e -> startCloseAnimation());
        contentPanel.add(batalButton);

        // Pay Button
        bayarButton = createButton("BAYAR", new Color(38, 211, 103), Color.WHITE);
        bayarButton.setBounds(25 + (FINAL_WIDTH - 60) / 2 + 10, FINAL_HEIGHT - 70, (FINAL_WIDTH - 60) / 2, 45);
        bayarButton.addActionListener(e -> bayarGaji());
        contentPanel.add(bayarButton);
    }

    private void loadEmployeeData(String norfid, String startDate, String endDate) {
        try {
            // Format untuk parsing parameter tanggal
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            // Validasi format tanggal
            Date parsedStartDate;
            Date parsedEndDate;
            try {
                parsedStartDate = inputDateFormat.parse(startDate);
                parsedEndDate = inputDateFormat.parse(endDate);
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this,
                        "Format tanggal tidak valid. Gunakan format YYYY-MM-DD",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // SQL query untuk menghitung gaji karyawan dengan filter rentang tanggal
            // Pelanggaran dihitung per hari (maksimal 1 pelanggaran per hari)
            String sql = "SELECT "
                    + "u.norfid, "
                    + "u.nama_user, "
                    + "u.jabatan, "
                    + "COUNT(a.id_absen) AS jumlah_hadir, "
                    + "COUNT(a.id_absen) * 60000 AS gaji_pokok, "
                    + "COUNT(CASE WHEN TIME(a.waktu_masuk) > '08:30:00' OR "
                    + "(TIME(a.waktu_keluar) < '16:00:00' AND TIME(a.waktu_keluar) != '00:00:00') THEN 1 ELSE NULL END) AS total_hari_pelanggaran, "
                    + "COUNT(CASE WHEN TIME(a.waktu_masuk) > '08:30:00' OR "
                    + "(TIME(a.waktu_keluar) < '16:00:00' AND TIME(a.waktu_keluar) != '00:00:00') THEN 1 ELSE NULL END) * 5000 AS total_potongan, "
                    + "(COUNT(a.id_absen) * 60000) - (COUNT(CASE WHEN TIME(a.waktu_masuk) > '08:30:00' OR "
                    + "(TIME(a.waktu_keluar) < '16:00:00' AND TIME(a.waktu_keluar) != '00:00:00') THEN 1 ELSE NULL END) * 5000) AS total_gaji "
                    + "FROM user u "
                    + "LEFT JOIN absensi a ON u.norfid = a.norfid "
                    + "WHERE u.norfid = ? "
                    + "AND DATE(a.waktu_masuk) >= ? "
                    + "AND DATE(a.waktu_masuk) <= ? "
                    + "GROUP BY u.norfid, u.nama_user, u.jabatan";

            try (PreparedStatement st = con.prepareStatement(sql)) {
                st.setString(1, norfid);
                st.setString(2, startDate);
                st.setString(3, endDate);

                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    // Format untuk mata uang Rupiah
                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

                    // Mendapatkan tanggal saat ini sebagai tanggal pembayaran
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
                    String paymentDate = dateFormat.format(new Date());

                    // Format untuk periode laporan (hanya bulan dan tahun)
                    SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", new Locale("id", "ID"));
                    periodeBulanTahun = monthYearFormat.format(parsedStartDate);

                    // Menambahkan data ke tabel
                    tableGaji.addRow(new Object[]{"Nomor RFID", rs.getString("norfid")});
                    tableGaji.addRow(new Object[]{"Nama", rs.getString("nama_user")});
                    tableGaji.addRow(new Object[]{"Jabatan", rs.getString("jabatan")});
                    tableGaji.addRow(new Object[]{"Periode", periodeBulanTahun});
                    tableGaji.addRow(new Object[]{"Jumlah Presensi", rs.getInt("jumlah_hadir") + " Hari"});

                    // Format nilai mata uang
                    String gajiPokok = "Rp. " + formatRupiah(rs.getInt("gaji_pokok")) + ",00";
                    tableGaji.addRow(new Object[]{"Gaji Pokok", gajiPokok});

                    int totalHariPelanggaran = rs.getInt("total_hari_pelanggaran");

                    // Hanya menampilkan total hari dengan pelanggaran
                    tableGaji.addRow(new Object[]{"Total Keterlambatan", totalHariPelanggaran + " Hari"});

                    String potonganPerPelanggaran = "Rp. 5.000,00/hari";
                    tableGaji.addRow(new Object[]{"Potongan Keterlambatan", potonganPerPelanggaran});

                    String totalPotongan = "Rp. " + formatRupiah(rs.getInt("total_potongan")) + ",00";
                    tableGaji.addRow(new Object[]{"Total Potongan", totalPotongan});

                    String totalGaji = "Rp. " + formatRupiah(rs.getInt("total_gaji")) + ",00";
                    tableGaji.addRow(new Object[]{"Total Gaji Bersih", totalGaji});
                    tableGaji.addRow(new Object[]{"Tanggal Pembayaran", paymentDate});
                    tableGaji.addRow(new Object[]{"Dibayar Oleh", "Owner (Admin)"});
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Tidak ada data absensi untuk karyawan ini dalam periode yang dipilih",
                            "Informasi",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Terjadi kesalahan: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bayarGaji() {
        try {
            String namaKaryawan = null;
            Double jumlahGaji = 0.0;

            // Get the actual JTable from JTableRounded
            JTable table = tableGaji.getTable();

            // Find RFID and name from the table data
            for (int i = 0; i < table.getRowCount(); i++) {
                String rowName = (String) table.getValueAt(i, 0);
                if ("Nomor RFID".equals(rowName)) {
                    norfid = (String) table.getValueAt(i, 1);
                    System.out.println("ini" + norfid);
                } else if ("Nama".equals(rowName)) {
                    namaKaryawan = (String) table.getValueAt(i, 1);
                } else if ("Total Gaji Bersih".equals(rowName)) {
                    // Extract the numeric value from the formatted string "Rp. X.XXX.XXX,00"
                    String gajiStr = ((String) table.getValueAt(i, 1))
                            .replace("Rp. ", "")
                            .replace(".", "")
                            .replace(",00", "");
                    jumlahGaji = Double.parseDouble(gajiStr);
                }
            }

            if (norfid == null || namaKaryawan == null || jumlahGaji <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Data gaji tidak lengkap atau tidak valid!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Insert data to both tables
            boolean success = insertToGajiKaryawan(norfid, jumlahGaji, namaKaryawan)
                    && insertToBiayaOperasional(owner, namaKaryawan, jumlahGaji, periodeBulanTahun);

            if (success) {
//                JOptionPane.showMessageDialog(this,
//                        "Pembayaran gaji untuk " + namaKaryawan + " berhasil!",
//                        "Sukses",
//                        JOptionPane.INFORMATION_MESSAGE); 
                PindahanAntarPopUp.showGajiKaryawanSuksesBayarGaji(parentFrame);
                // Close the form after successful payment
                startCloseAnimation();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Gagal melakukan pembayaran!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }   
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean insertToGajiKaryawan(String norfid, Double jumlahGaji, String nama) {
        boolean success = false;
        try {
            Calendar cal = Calendar.getInstance();
            int currentMonth = cal.get(Calendar.MONTH) + 1;
            int currentYear = cal.get(Calendar.YEAR);

            JTable table = tableGaji.getTable();

            int pelanggaran = 0;
            for (int i = 0; i < table.getRowCount(); i++) {
                String rowName = (String) table.getValueAt(i, 0);
                if ("Pelanggaran".equals(rowName)) {
                    String pelanggaranStr = (String) table.getValueAt(i, 1);
                    // Extract numbers from "Terlambat: X kali, Pulang Cepat: Y kali"
                    String[] parts = pelanggaranStr.split(",");
                    int terlambat = Integer.parseInt(parts[0].replaceAll("\\D+", ""));
                    int pulangCepat = Integer.parseInt(parts[1].replaceAll("\\D+", ""));
                    pelanggaran = terlambat + pulangCepat;
                    break;
                }
            }

            String checkQuery = "SELECT bo.tanggal "
                    + "FROM biaya_operasional bo "
                    + "WHERE bo.catatan LIKE ? "
                    + "AND MONTH(bo.tanggal) = ? "
                    + "AND YEAR(bo.tanggal) = ?";

            try (PreparedStatement st = con.prepareStatement(checkQuery)) {
                st.setString(1, "Pembayaran gaji karyawan - " + nama);
                st.setInt(2, currentMonth);
                st.setInt(3, currentYear);

                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(null,
                            "Gaji karyawan ini sudah dibayarkan untuk bulan ini!",
                            "Peringatan",
                            JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            }
            String query = "INSERT INTO gaji_karyawan (total_gaji, total_pelanggaran, norfid) VALUES (?, ?, ?)";

            try (PreparedStatement st = con.prepareStatement(query)) {
                st.setDouble(1, jumlahGaji);
                st.setInt(2, pelanggaran);
                st.setString(3, norfid);

                int result = st.executeUpdate();
                success = (result > 0);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error saat menyimpan data gaji: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        return success;
    }

    private boolean insertToBiayaOperasional(String norfid, String namaKaryawan, Double jumlahGaji, String bultahun) {
        boolean success = false;

        try {
            String newBpId = generateNewBpId(con);

            // Insert into biaya_operasional table
            String query = "INSERT INTO biaya_operasional (id_biaya, catatan, total, tanggal, norfid) VALUES (?, ?, ?, NOW(), ?)";
            try (PreparedStatement st = con.prepareStatement(query)) {
                st.setString(1, newBpId);
                st.setString(2, "Pembayaran gaji karyawan - " + namaKaryawan + " " + bultahun);
                st.setDouble(3, jumlahGaji);
                st.setString(4, norfid);

                int result = st.executeUpdate();
                success = (result > 0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error saat menyimpan biaya operasional: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        return success;
    }

    private String generateNewBpId(Connection koneksi) throws SQLException {
        String highestId = "BP_0000";

        String query = "SELECT id_biaya FROM biaya_operasional ORDER BY id_biaya DESC LIMIT 1";
        try (PreparedStatement st = koneksi.prepareStatement(query)) {
            ResultSet rs = st.executeQuery(query);
            if (rs.next()) {
                highestId = rs.getString("id_biaya");
            }
        }

        String numericPart = highestId.substring(3);
        int idNumber = Integer.parseInt(numericPart);
        idNumber++;

        return String.format("BP_%04d", idNumber);
    }

    private String formatRupiah(int value) {
        // Format number with thousands separator
        return String.format("%,d", value).replace(",", ".");
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

    private void setNamaUser() {
        String email = LoginForm.getNamaUser();
        String norfid = LoginForm.getNoRFID();

        String sql = "SELECT nama_user, norfid FROM user WHERE email = ? OR norfid = ?";
        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, email);
            st.setString(2, norfid);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                owner = rs.getString("norfid");
            } else {
                System.out.println("No karyawan found ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
