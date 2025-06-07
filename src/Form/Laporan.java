package Form;

import SourceCode.JTableRounded;
import SourceCode.ScrollPane;
import calendar.CustomKalender;
import produk.ComboboxCustom;
import Form.diagramlaporan;
import java.util.List;
import java.util.ArrayList;
import Form.diagramlaporankeuangan;
import Form.diagramkaryawan;
import PopUp_all.*;
import SourceCode.*;
import com.mysql.cj.xdevapi.Row;
import java.sql.*;
import java.awt.*;
import com.mysql.cj.xdevapi.Statement;

import db.conn;
import Form.ExcelExporter.LaporanData;
import static Form.ExcelExporter.exportToExcel;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EventObject;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date; // java.util.Date
import java.text.SimpleDateFormat;
import java.awt.Dimension;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.swing.text.Document;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.function.BiConsumer;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;

public class Laporan extends javax.swing.JPanel {

    Component parentComponent = this;
    private JFrame parentFrame;
    private JPanel mainPanel, headerPanel, contentPanel, innerContentPanel, tabPanel;
    private ComboboxCustom periodCombo, exportCombo;
    private JButton dateRangeButton;
    private CustomKalender calendarPanel;
    private JButton pemasukanTab, pengeluaranTab, labaTab, GrafikTab;
    private diagramlaporan diagramPanel;
    private JTableRounded tabelPemasukan, tabelPengeluaran;
    private JPanel pemasukanPanel, pengeluaranPanel, labaPanel, grafikPanel;
    private java.sql.Date selectedDate;
    private boolean isPopupOpen = false;
    // Warna dan styling
    private Color selectedTabColor = new Color(20, 20, 20);
    private Color unselectedTabColor = new Color(20, 20, 20);
    private Color selectedTabLineColor = new Color(0, 123, 255);
    private Color textColor = Color.WHITE;
    private Color innerPanelBgColor = new Color(20, 20, 20, 245);
    private Color tablePanelBgColor = new Color(20, 20, 20, 245);
    private JButton activeTab;
    public String start = "";
    public String end = "";
    private Connection con;
    double totalPengeluaran = 0;
    double totalPendapatan = 0;
    double totalOperasional = 0;
    double labaBersih = 0;
    double labaKotor = 0;
    JLabel totalPendapatanValue;
    JLabel pemasukanValueLabel;
    JLabel pengeluaranValueLabel;
    JLabel labaKotorValueLabel;
    JLabel labaKotorItem2ValueLabel;
    JLabel operasionalValueLabel;
    JLabel labaBersihValueLabel;
    JLabel totalPengeluaranValue;
    java.sql.Date sqlStartDate;
    java.sql.Date sqlEndDate;
//    private Date selectedDate;
    public static java.sql.Date tanggalTerpilih;

    public Laporan() {
        con = conn.getConnection();
        initComponents();
        setActiveTab(pemasukanTab);
        setupListeners();
    }

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private void initComponents() {
        setLayout(new BorderLayout(15, 0));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Main Panel dengan RoundedBorder
        mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isOpaque()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                    g2.setColor(new Color(20, 20, 20));
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                    g2.dispose();
                }
                super.paintComponent(g);
            }

            @Override
            public boolean isOpaque() {
                return false;
            }
        };
        mainPanel.setBackground(Color.WHITE);

        // Header Panel
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Laporan");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));

        dateRangeButton = new JButton("Start Date - End Date");
        dateRangeButton.setPreferredSize(new Dimension(180, 35));
        dateRangeButton.setFocusPainted(false);
        dateRangeButton.setBorderPainted(false);
        dateRangeButton.setContentAreaFilled(false);
        dateRangeButton.setBackground(Color.WHITE);
        dateRangeButton.setForeground(Color.BLACK);
        RoundedButtonLaporan roundedUI = new RoundedButtonLaporan();
        roundedUI.setBorderColor(Color.LIGHT_GRAY);
        dateRangeButton.setUI(roundedUI);
        dateRangeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(parentComponent);
                PopUp_StartDateEndDate dialog = new PopUp_StartDateEndDate(parentFrame);

                dialog.setVisible(true);

                sqlStartDate = dialog.getSqlStartDate();
                sqlEndDate = dialog.getSqlEndDate();
                loadPemasukanData(sqlStartDate, sqlEndDate);
                loadLabaData(sqlStartDate, sqlEndDate);
                loadPengeluaranData(sqlStartDate, sqlEndDate);
                if (sqlStartDate != null && sqlEndDate != null) {
                    // Gunakan untuk query database
                    System.out.println("SQL Start Date: " + sqlStartDate);
                    System.out.println("SQL End Date: " + sqlEndDate);
                }
                System.out.println("start date end date di klik");
            }
        });

        // Comboboxes
        periodCombo = new ComboboxCustom();
        periodCombo.setPreferredSize(new Dimension(150, 35));
        periodCombo.addItem("Filter");
        periodCombo.addItem("Minggu Ini");
        periodCombo.addItem("Bulan Ini");
        periodCombo.addItem("Bulan Lalu");
        periodCombo.addItem("Tahun Ini");
        periodCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (periodCombo.getSelectedIndex() > 0) {
                    Calendar calendar = Calendar.getInstance();
                    Date startDate = null;
                    Date endDate = null;

                    switch (periodCombo.getSelectedItem().toString()) {
                        case "Minggu Ini":
                            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                            resetTimeToStart(calendar);
                            startDate = calendar.getTime();

                            calendar = Calendar.getInstance();
                            resetTimeToEnd(calendar);
                            endDate = calendar.getTime();
                            break;

                        case "Bulan Ini":
                            calendar.set(Calendar.DAY_OF_MONTH, 1);
                            resetTimeToStart(calendar);
                            startDate = calendar.getTime();

                            calendar = Calendar.getInstance();
                            resetTimeToEnd(calendar);
                            endDate = calendar.getTime();
                            break;

                        case "Bulan Lalu":
                            calendar.add(Calendar.MONTH, -1);
                            calendar.set(Calendar.DAY_OF_MONTH, 1);
                            resetTimeToStart(calendar);
                            startDate = calendar.getTime();

                            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                            resetTimeToEnd(calendar);
                            endDate = calendar.getTime();
                            break;

                        case "Tahun Ini":
                            calendar.set(Calendar.MONTH, Calendar.JANUARY);
                            calendar.set(Calendar.DAY_OF_MONTH, 1);
                            resetTimeToStart(calendar);
                            startDate = calendar.getTime();

                            calendar = Calendar.getInstance();
                            resetTimeToEnd(calendar);
                            endDate = calendar.getTime();
                            break;
                    }

                    System.out.println("=== FILTER APPLIED ===");
                    System.out.println("Filter: " + periodCombo.getSelectedItem().toString());
                    System.out.println("Start Date: " + startDate);
                    System.out.println("End Date: " + endDate);
                    System.out.println("======================");

                    loadPemasukanData(startDate, endDate);
                    loadPengeluaranData(startDate, endDate);
                    loadLabaData(startDate, endDate);
                } else {
                    System.out.println("=== FILTER RESET ===");
                    System.out.println("Loading all data without filter");
                    System.out.println("====================");

                    loadPemasukanData(null, null);
                    loadPengeluaranData(null, null);
                    loadLabaData(null, null);
                }
            }

            private void resetTimeToStart(Calendar calendar) {
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
            }

            private void resetTimeToEnd(Calendar calendar) {
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
            }
        });
        exportCombo = new ComboboxCustom();
        exportCombo.setPreferredSize(new Dimension(140, 35));
        exportCombo.addItem("Ekspor File");
        exportCombo.addItem("PDF");
        exportCombo.addItem("Excel");
        exportCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (exportCombo.getSelectedIndex() > 0) { // Skip jika memilih "Ekspor File"
                    String exportType = exportCombo.getSelectedItem().toString();

                    // Dapatkan tanggal yang sedang difilter (jika ada)
                    Date startDate = null;
                    Date endDate = null;

                    // Jika menggunakan filter periode
                    if (periodCombo.getSelectedIndex() > 0) {
                        Calendar calendar = Calendar.getInstance();
                        // ... (logika sama seperti sebelumnya untuk menentukan startDate dan endDate)
                    }

                    try {
                        switch (exportType) {
                            case "PDF":

                                PDFExporter exporter = new PDFExporter();

                                String[][] dataContoh = {
                                    {"Pemasukan", "1111", pemasukanValueLabel.getText()},
                                    {"Pengeluaran", "150", pengeluaranValueLabel.getText()},
                                    {"Laba Kotor", "200", labaKotorValueLabel.getText()}
                                };
                                LocalDate tanggal = LocalDate.now();
                                LocalTime waktu = LocalTime.now();

                                // Format tanggal dan waktu
                                DateTimeFormatter formatTanggal = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                                DateTimeFormatter formatWaktu = DateTimeFormatter.ofPattern("HH-mm-ss"); // gunakan '-' agar aman untuk file

                                String tanggalStr = tanggal.format(formatTanggal);
                                String waktuStr = waktu.format(formatWaktu);

//                                String donwloadsPath = System.getProperty("user.home") + "/Downloads/" + "laporan data keuangan tanggal " + tanggalStr + " waktu " + waktuStr + ".pdf";
                                exporter.exportToPDF("KARUNIA STORE", namaUser, "C:/Users/user/Downloads/laporan data keuangan tanggal " + tanggalStr + " waktu " + waktuStr + " .pdf", dataContoh);
//                                exporter.exportToPDF("KARUNIA STORE", donwloadsPath, dataContoh);
                                break;

                            case "Excel":

                                exportDataKaryawan();
                                break;

                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error saat ekspor data: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        exportCombo.setSelectedIndex(0); // Reset ke "Ekspor File"
                    }
                }
            }
        });

        JPanel buttonControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonControlsPanel.setOpaque(false);
        buttonControlsPanel.add(dateRangeButton);
        buttonControlsPanel.add(periodCombo);
        buttonControlsPanel.add(exportCombo);

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(buttonControlsPanel, BorderLayout.SOUTH);

        // Content Panel
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        // Inner Content Panel
        innerContentPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(innerPanelBgColor);
                g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 20, 20);
                g2.dispose();
            }
        };
        innerContentPanel.setOpaque(false);

        // Tab Panel
        tabPanel = new JPanel() {
            @Override
            public void doLayout() {
                super.doLayout();
                repaint();
            }
        };
        tabPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabPanel.setOpaque(false);

        pemasukanTab = createTabButton("Pemasukan");
        pengeluaranTab = createTabButton("Pengeluaran");
        labaTab = createTabButton("Laba");
        GrafikTab = createTabButton("Grafik");

        tabPanel.add(pemasukanTab);
        tabPanel.add(pengeluaranTab);
        tabPanel.add(labaTab);
        tabPanel.add(GrafikTab);

        // Inisialisasi panel tab
        initPemasukanPanel();
        initPengeluaranPanel();
        initGrafikPanel();
        initLabaPanel();

        JPanel dataContainer = new JPanel(new CardLayout());
        dataContainer.setOpaque(false);
        dataContainer.add(pemasukanPanel, "pemasukan");
        dataContainer.add(pengeluaranPanel, "pengeluaran");
        dataContainer.add(grafikPanel, "laba");
        dataContainer.add(labaPanel, "Grafik");
        innerContentPanel.add(tabPanel, BorderLayout.NORTH);
        innerContentPanel.add(dataContainer, BorderLayout.CENTER);
        contentPanel.add(innerContentPanel, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Panel horizontal
        JPanel horizontalPanel = new JPanel(new BorderLayout(15, 0));
        horizontalPanel.setOpaque(false);
        horizontalPanel.add(mainPanel, BorderLayout.CENTER);

        // Kalender
        calendarPanel = new CustomKalender();
        calendarPanel.setPreferredSize(new Dimension(350, 280));
        calendarPanel.addPropertyChangeListener("selectedDate", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                // Konversi dari java.util.Date ke java.sql.Date
                java.util.Date utilDate = (java.util.Date) evt.getNewValue();
                selectedDate = new java.sql.Date(utilDate.getTime());

                // Lakukan sesuatu dengan tanggal yang dipilih
                System.out.println("Tanggal yang dipilih: " + dateFormat.format(selectedDate));

                // Update UI atau lakukan tindakan yang diperlukan
                loadPemasukanData(selectedDate, null);
                loadLabaData(selectedDate, null);
                loadPengeluaranData(selectedDate, null);
            }
        });
        // Container diagram
        JPanel diagramContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isOpaque()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                    g2.setColor(new Color(20, 20, 20));
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                    g2.dispose();
                }
                super.paintComponent(g);
            }

            @Override
            public boolean isOpaque() {
                return false;
            }
        };
        diagramContainer.setPreferredSize(new Dimension(400, 280));
        diagramContainer.setBackground(new Color(20, 20, 20, 0));
        diagramContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        diagramPanel = new diagramlaporan();
        diagramPanel.setOpaque(false);
        diagramContainer.add(diagramPanel, BorderLayout.CENTER);

        JPanel calendarContainer = new JPanel(new BorderLayout(0, 15));
        calendarContainer.setOpaque(false);
        calendarContainer.add(calendarPanel, BorderLayout.NORTH);
        calendarContainer.add(diagramContainer, BorderLayout.CENTER);

        horizontalPanel.add(calendarContainer, BorderLayout.EAST);
        add(horizontalPanel, BorderLayout.CENTER);
    }

    private void loadPemasukanData(Date startDate, Date endDate) {
        String query = "SELECT tj.id_transaksijual, tj.tanggal_transaksi, "
                + "SUM(dtj.total_harga) AS total, "
                + "GROUP_CONCAT(CONCAT(p.nama_produk, ' (', dtj.jumlah_produk, ')') SEPARATOR ', ') AS nama_produk, "
                + "u.nama_user "
                + "FROM transaksi_jual tj "
                + "JOIN detail_transaksijual dtj ON tj.id_transaksijual = dtj.id_transaksijual "
                + "JOIN produk p ON dtj.id_produk = p.id_produk "
                + "JOIN user u ON tj.norfid = u.norfid ";

        if (startDate != null && endDate != null) {
            query += "WHERE tj.tanggal_transaksi BETWEEN ? AND ? ";
        } else if (startDate != null) {
            query += "WHERE DATE(tj.tanggal_transaksi) = DATE(?) ";
        }

        query += "GROUP BY tj.id_transaksijual, tj.tanggal_transaksi, u.nama_user "
                + "ORDER BY tj.tanggal_transaksi DESC";

        try {
            PreparedStatement stmt = con.prepareStatement(query);

            if (startDate != null && endDate != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(endDate);
                cal.add(Calendar.DATE, 1); // Tambah 1 hari
                java.sql.Date adjustedEndDate = new java.sql.Date(cal.getTimeInMillis());
                stmt.setDate(1, new java.sql.Date(startDate.getTime()));
                stmt.setDate(2, adjustedEndDate);
            } else if (startDate != null) {
                stmt.setDate(1, new java.sql.Date(startDate.getTime()));
            }

            ResultSet rs = stmt.executeQuery();
            JTable rawTable = tabelPemasukan.getTable();
rawTable.addMouseListener(new MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e) {
        if (isPopupOpen) {
            return;
        }

        int row = rawTable.rowAtPoint(e.getPoint());
        int col = rawTable.columnAtPoint(e.getPoint());

        if (row == -1 || col == -1) {
            return;
        }

        if (col == 3 || col == 2) {
            String kodeTransaksi = rawTable.getValueAt(row, 2).toString();

            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(parentComponent);
            PopUp_LaporanDetailPemasukanAtautransaksiJual detailTransaksiPopup = new PopUp_LaporanDetailPemasukanAtautransaksiJual(parentFrame);
            isPopupOpen = true;

            detailTransaksiPopup.loadTransactionDetails(kodeTransaksi);
            detailTransaksiPopup.setVisible(true);

            System.out.println("Menampilkan detail untuk transaksi: " + kodeTransaksi);

            detailTransaksiPopup.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    isPopupOpen = false;
                }

                @Override
                public void windowClosing(WindowEvent e) {
                    isPopupOpen = false;
                }
            });
        }
    }
});
            DefaultTableModel model = (DefaultTableModel) rawTable.getModel();
            model.setRowCount(0);
            DecimalFormat decimalFormat = new DecimalFormat("#,###");
            decimalFormat.setGroupingUsed(true);
            SimpleDateFormat displayDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            int no = 1;
            double totalPendapatan = 0;

            while (rs.next()) {
                String idTransaksi = rs.getString("id_transaksijual");
                Date tanggalRaw = rs.getDate("tanggal_transaksi");
                String tanggal = displayDateFormat.format(tanggalRaw);
                String namaProduk = rs.getString("nama_produk");
                double total = rs.getDouble("total");
                String namaKasir = rs.getString("nama_user");

                totalPendapatan += total;
                String totalFormatted = "Rp. " + decimalFormat.format(total);

                model.addRow(new Object[]{
                    no++,
                    tanggal,
                    idTransaksi,
                    namaProduk,
                    totalFormatted,
                    namaKasir
                });
            }

            String totalPendapatanFormatted = "Rp. " + decimalFormat.format(totalPendapatan);
            totalPendapatanValue.setText(totalPendapatanFormatted);

            rs.close();
            stmt.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saat mengambil data pemasukan: " + e.getMessage());
        }
    }

    private void initPemasukanPanel() {
        pemasukanPanel = new JPanel(new BorderLayout());
        pemasukanPanel.setOpaque(false);

        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setOpaque(false);
        totalPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        RoundedPanelProduk totalPendapatanPanel = new RoundedPanelProduk(10);
        totalPendapatanPanel.setLayout(null);
        totalPendapatanPanel.setBackground(new Color(40, 50, 100));
        totalPendapatanPanel.setPreferredSize(new Dimension(520, 40));

        JLabel totalPendapatanLabel = new JLabel("Total Pendapatan");
        totalPendapatanLabel.setForeground(Color.WHITE);
        totalPendapatanLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        totalPendapatanLabel.setBounds(15, -5, 250, 50);

        totalPendapatanValue = new JLabel("Rp. 999.000.000");
        totalPendapatanValue.setForeground(Color.WHITE);
        totalPendapatanValue.setFont(new Font("SansSerif", Font.BOLD, 14));
        totalPendapatanValue.setBounds(295, -5, 235, 50);
        totalPendapatanValue.setHorizontalAlignment(SwingConstants.RIGHT);

        totalPendapatanPanel.add(totalPendapatanLabel);
        totalPendapatanPanel.add(totalPendapatanValue);
        totalPanel.add(totalPendapatanPanel, BorderLayout.CENTER);

        String[] kolom = {"No", "Tanggal", "Kode Transaksi", "Detail Produk", "Total", "Kasir"};
        tabelPemasukan = new JTableRounded(kolom);

        tabelPemasukan.setColumnWidth(0, 38);
        tabelPemasukan.setColumnWidth(1, 85);
        tabelPemasukan.setColumnWidth(2, 95);
        tabelPemasukan.setColumnWidth(3, 150);
        tabelPemasukan.setColumnWidth(4, 100);
        tabelPemasukan.setColumnWidth(5, 90);

        loadPemasukanData(null, null);

        JTable rawTable = tabelPemasukan.getTable();
        TableColumnModel columnModel = rawTable.getColumnModel();

        for (int i = 2; i < columnModel.getColumnCount(); i++) {
            final int index = i;
            columnModel.getColumn(i).setCellEditor(new DefaultCellEditor(new JTextField()) {
                @Override
                public boolean isCellEditable(EventObject event) {
                    return false;
                }
            });
        }

        ScrollPane scrollPane = new ScrollPane(rawTable);

        JPanel tabelContainer = new JPanel(new BorderLayout());
        tabelContainer.setBackground(tablePanelBgColor);
        tabelContainer.setOpaque(false);
        tabelContainer.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        tabelContainer.add(scrollPane, BorderLayout.CENTER);

        pemasukanPanel.add(totalPanel, BorderLayout.NORTH);
        pemasukanPanel.add(tabelContainer, BorderLayout.CENTER);
    }

    private void loadLabaData(Date startDate, Date endDate) {
        System.out.println("=== DEBUG LABA QUERY ===");
        System.out.println("Start Date: " + startDate);
        System.out.println("End Date: " + endDate);

        // Tentukan kondisi WHERE untuk tanggal transaksi
        String whereCondition = "";
        boolean useRange = (startDate != null && endDate != null);
        boolean useSingleDate = (startDate != null && endDate == null);

        if (useRange) {
            whereCondition = " WHERE tanggal_transaksi >= ? AND tanggal_transaksi <= ? ";
        } else if (useSingleDate) {
            whereCondition = " WHERE DATE(tanggal_transaksi) = DATE(?) ";
        } else {
            whereCondition = "";  // Kalau null semua, ambil semua data tanpa filter tanggal
        }

        // Query utama dengan alias tabel yg diupdate supaya gampang parameternya
        String query = "SELECT "
                + "(SELECT IFNULL(SUM(dtj.total_harga), 0) FROM detail_transaksijual dtj "
                + " JOIN transaksi_jual tj ON dtj.id_transaksijual = tj.id_transaksijual "
                + whereCondition.replace("tanggal_transaksi", "tj.tanggal_transaksi")
                + ") AS pemasukan, "
                + "(SELECT IFNULL(SUM(dtb.total_harga), 0) FROM detail_transaksibeli dtb "
                + " JOIN transaksi_beli tb ON dtb.id_transaksibeli = tb.id_transaksibeli "
                + whereCondition.replace("tanggal_transaksi", "tb.tanggal_transaksi")
                + ") AS pengeluaran, "
                + "(SELECT IFNULL(SUM(total), 0) FROM biaya_operasional "
                + whereCondition.replace("tanggal_transaksi", "tanggal")
                + ") AS totalOperasional, "
                + "(SELECT IFNULL(SUM(dtj.total_harga - (p.harga_beli * dtj.jumlah_produk)), 0) "
                + " FROM detail_transaksijual dtj "
                + " JOIN transaksi_jual tj ON dtj.id_transaksijual = tj.id_transaksijual "
                + " JOIN produk p ON dtj.id_produk = p.id_produk "
                + whereCondition.replace("tanggal_transaksi", "tj.tanggal_transaksi")
                + ") AS labaFromSales";

        System.out.println("Query: " + query);

        try {
            PreparedStatement stmt = con.prepareStatement(query);

            // Karena setiap subquery menggunakan parameter tanggal yang sama,
            // kita set parameter sekali per subquery, total 4 subqueries.
            // Jadi, set parameter startDate dan endDate 4 kali berturut-turut sesuai kondisi.
            if (startDate != null) {
                java.sql.Timestamp sqlStartDate = new java.sql.Timestamp(startDate.getTime());
                java.sql.Timestamp sqlEndDate = (endDate != null) ? new java.sql.Timestamp(endDate.getTime()) : null;

                // Fungsi untuk set parameter tanggal sesuai kondisi
                BiConsumer<Integer, PreparedStatement> setParams = (paramStartIndex, preparedStatement) -> {
                    try {
                        if (useRange) {
                            preparedStatement.setTimestamp(paramStartIndex, sqlStartDate);
                            preparedStatement.setTimestamp(paramStartIndex + 1, sqlEndDate);
                        } else {
                            preparedStatement.setTimestamp(paramStartIndex, sqlStartDate);
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                };

                int paramIndex = 1;
                for (int i = 0; i < 4; i++) {
                    if (useRange) {
                        stmt.setTimestamp(paramIndex++, sqlStartDate);
                        stmt.setTimestamp(paramIndex++, sqlEndDate);
                    } else {
                        stmt.setTimestamp(paramIndex++, sqlStartDate);
                    }
                }
            }

            // Execute main query
            ResultSet rs = stmt.executeQuery();
            DecimalFormat decimalFormat = new DecimalFormat("#,###");
            decimalFormat.setGroupingUsed(true);

            if (rs.next()) {
                double totalPemasukan = rs.getDouble("pemasukan");
                double totalPengeluaran = rs.getDouble("pengeluaran");
                double biayaOperasional = rs.getDouble("totalOperasional");
                double labaKotor = rs.getDouble("labaFromSales");  // langsung dari DB
                double labaBersih = labaKotor - biayaOperasional;  // laba kotor - operasional

                pemasukanValueLabel.setText("Rp. " + decimalFormat.format(totalPemasukan));
                pengeluaranValueLabel.setText("- Rp. " + decimalFormat.format(totalPengeluaran));
                labaKotorValueLabel.setText("Rp. " + decimalFormat.format(labaKotor));
                labaKotorItem2ValueLabel.setText("Rp. " + decimalFormat.format(labaKotor));
                operasionalValueLabel.setText("- Rp. " + decimalFormat.format(biayaOperasional));
                labaBersihValueLabel.setText("Rp. " + decimalFormat.format(labaBersih));

                System.out.println("=== FINAL RESULTS ===");
                System.out.println("Pemasukan: " + totalPemasukan);
                System.out.println("Pengeluaran: " + totalPengeluaran);
                System.out.println("Biaya Operasional: " + biayaOperasional);
                System.out.println("Laba Kotor (labaFromSales): " + labaKotor);
                System.out.println("Laba Bersih: " + labaBersih);
                System.out.println("====================");
            }

            rs.close();
            stmt.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saat mengambil data laba: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initLabaPanel() {
        grafikPanel = new JPanel(null);
        grafikPanel.setOpaque(false);

        JPanel mainContentPanel = new JPanel(null);
        mainContentPanel.setOpaque(false);
        mainContentPanel.setBounds(15, 10, 550, 500);

        JLabel labaKotorLabel = new JLabel("Laba Kotor:");
        labaKotorLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        labaKotorLabel.setForeground(Color.WHITE);
        labaKotorLabel.setBounds(0, 0, 200, 30);
        mainContentPanel.add(labaKotorLabel);

        RoundedPanelProduk pemasukanPanel = new RoundedPanelProduk();
        pemasukanPanel.setLayout(null);
        pemasukanPanel.setBackground(new Color(30, 30, 50));
        pemasukanPanel.setBounds(0, 40, 520, 50);

        JLabel pemasukanLabel = new JLabel("Pemasukan");
        pemasukanLabel.setForeground(Color.WHITE);
        pemasukanLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        pemasukanLabel.setBounds(15, 0, 250, 50);

        pemasukanValueLabel = new JLabel("Rp. 25.000.000");
        pemasukanValueLabel.setForeground(Color.WHITE);
        pemasukanValueLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        pemasukanValueLabel.setBounds(270, 0, 235, 50);
        pemasukanValueLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        pemasukanPanel.add(pemasukanLabel);
        pemasukanPanel.add(pemasukanValueLabel);
        mainContentPanel.add(pemasukanPanel);

        RoundedPanelProduk pengeluaranPanel = new RoundedPanelProduk();
        pengeluaranPanel.setLayout(null);
        pengeluaranPanel.setBackground(new Color(30, 30, 50));
        pengeluaranPanel.setBounds(0, 100, 520, 50);

        JLabel pengeluaranLabel = new JLabel("Pengeluaran");
        pengeluaranLabel.setForeground(Color.WHITE);
        pengeluaranLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        pengeluaranLabel.setBounds(15, 0, 250, 50);

        pengeluaranValueLabel = new JLabel("- Rp. 14.000.000");
        pengeluaranValueLabel.setForeground(new Color(255, 75, 75));
        pengeluaranValueLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        pengeluaranValueLabel.setBounds(270, 0, 235, 50);
        pengeluaranValueLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        pengeluaranPanel.add(pengeluaranLabel);
        pengeluaranPanel.add(pengeluaranValueLabel);
        mainContentPanel.add(pengeluaranPanel);

        RoundedPanelProduk labaKotorPanel = new RoundedPanelProduk();
        labaKotorPanel.setLayout(null);
        labaKotorPanel.setBackground(new Color(40, 50, 100));
        labaKotorPanel.setBounds(0, 160, 520, 50);

        JLabel labaKotorItemLabel = new JLabel("Laba Kotor");
        labaKotorItemLabel.setForeground(Color.WHITE);
        labaKotorItemLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        labaKotorItemLabel.setBounds(15, 0, 250, 50);

        labaKotorValueLabel = new JLabel("Rp. 11.000.000");
        labaKotorValueLabel.setForeground(new Color(100, 255, 100));
        labaKotorValueLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        labaKotorValueLabel.setBounds(270, 0, 235, 50);
        labaKotorValueLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        labaKotorPanel.add(labaKotorItemLabel);
        labaKotorPanel.add(labaKotorValueLabel);
        mainContentPanel.add(labaKotorPanel);

        JLabel labaBersihLabel = new JLabel("Laba Bersih:");
        labaBersihLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        labaBersihLabel.setForeground(Color.WHITE);
        labaBersihLabel.setBounds(0, 230, 200, 30);
        mainContentPanel.add(labaBersihLabel);

        RoundedPanelProduk labaKotorItem2Panel = new RoundedPanelProduk();
        labaKotorItem2Panel.setLayout(null);
        labaKotorItem2Panel.setBackground(new Color(30, 30, 50));
        labaKotorItem2Panel.setBounds(0, 270, 520, 50);

        JLabel labaKotorItem2Label = new JLabel("Laba Kotor");
        labaKotorItem2Label.setForeground(Color.WHITE);
        labaKotorItem2Label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        labaKotorItem2Label.setBounds(15, 0, 250, 50);

        labaKotorItem2ValueLabel = new JLabel("Rp. 11.000.000");
        labaKotorItem2ValueLabel.setForeground(new Color(100, 255, 100));
        labaKotorItem2ValueLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        labaKotorItem2ValueLabel.setBounds(270, 0, 235, 50);
        labaKotorItem2ValueLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        labaKotorItem2Panel.add(labaKotorItem2Label);
        labaKotorItem2Panel.add(labaKotorItem2ValueLabel);
        mainContentPanel.add(labaKotorItem2Panel);

        // Item Biaya Operasional
        RoundedPanelProduk operasionalPanel = new RoundedPanelProduk();
        operasionalPanel.setLayout(null);
        operasionalPanel.setBackground(new Color(30, 30, 50));
        operasionalPanel.setBounds(0, 330, 520, 50);

        JLabel operasionalLabel = new JLabel("Biaya Operasional");
        operasionalLabel.setForeground(Color.WHITE);
        operasionalLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        operasionalLabel.setBounds(15, 0, 250, 50);

        operasionalValueLabel = new JLabel("- Rp. 340.000");
        operasionalValueLabel.setForeground(new Color(255, 75, 75));
        operasionalValueLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        operasionalValueLabel.setBounds(270, 0, 235, 50);
        operasionalValueLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        operasionalPanel.add(operasionalLabel);
        operasionalPanel.add(operasionalValueLabel);
        mainContentPanel.add(operasionalPanel);

        // Item Laba Bersih
        RoundedPanelProduk labaBersihPanel = new RoundedPanelProduk();
        labaBersihPanel.setLayout(null);
        labaBersihPanel.setBackground(new Color(40, 50, 100));
        labaBersihPanel.setBounds(0, 390, 520, 50);

        JLabel labaBersihItemLabel = new JLabel("Laba Bersih");
        labaBersihItemLabel.setForeground(Color.WHITE);
        labaBersihItemLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        labaBersihItemLabel.setBounds(15, 0, 235, 50);

        labaBersihValueLabel = new JLabel("Rp. 10.660.000");
        labaBersihValueLabel.setForeground(new Color(100, 255, 100));
        labaBersihValueLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        labaBersihValueLabel.setBounds(270, 0, 250, 50);
        labaBersihValueLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        labaBersihPanel.add(labaBersihItemLabel);
        labaBersihPanel.add(labaBersihValueLabel);
        mainContentPanel.add(labaBersihPanel);

        loadLabaData(null, null);
        // Menambahkan panel utama ke grafikPanel
        grafikPanel.add(mainContentPanel);

        // Method untuk mengatur ukuran panel sesuai dengan parent
        grafikPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = grafikPanel.getWidth() - 30;
                mainContentPanel.setBounds(15, 10, width, 500);

                // Mengatur ulang lebar semua panel
                pemasukanPanel.setBounds(0, 40, width, 50);
                pengeluaranPanel.setBounds(0, 100, width, 50);
                labaKotorPanel.setBounds(0, 160, width, 50);
                labaKotorItem2Panel.setBounds(0, 270, width, 50);
                operasionalPanel.setBounds(0, 330, width, 50);
                labaBersihPanel.setBounds(0, 390, width, 50);

                // Mengatur ulang posisi label nilai
                int labelWidth = 250;
                int valueWidth = width - labelWidth - 20;

                pemasukanValueLabel.setBounds(width - valueWidth - 15, 0, valueWidth, 50);
                pengeluaranValueLabel.setBounds(width - valueWidth - 15, 0, valueWidth, 50);
                labaKotorValueLabel.setBounds(width - valueWidth - 15, 0, valueWidth, 50);
                labaKotorItem2ValueLabel.setBounds(width - valueWidth - 15, 0, valueWidth, 50);
                operasionalValueLabel.setBounds(width - valueWidth - 15, 0, valueWidth, 50);
                labaBersihValueLabel.setBounds(width - valueWidth - 15, 0, valueWidth, 50);
            }
        });
    }
    private String namaUser;

    private void loadPengeluaranData(Date startDate, Date endDate) {
        String query = "SELECT tb.id_transaksibeli, tb.tanggal_transaksi, "
                + "SUM(dtb.total_harga) AS total, "
                + "GROUP_CONCAT(CONCAT(p.nama_produk, ' (', dtb.jumlah_produk, ')') SEPARATOR ', ') AS nama_produk, "
                + "u.nama_user "
                + "FROM transaksi_beli tb "
                + "JOIN detail_transaksibeli dtb ON tb.id_transaksibeli = dtb.id_transaksibeli "
                + "JOIN produk p ON dtb.id_produk = p.id_produk "
                + "JOIN user u ON tb.norfid = u.norfid ";

        // Tambahkan WHERE clause berdasarkan ketersediaan tanggal
        if (startDate != null && endDate != null) {
            query += "WHERE tb.tanggal_transaksi BETWEEN ? AND ? ";
        } else if (startDate != null) {
            query += "WHERE DATE(tb.tanggal_transaksi) = DATE(?) ";
        }

        query += "GROUP BY tb.id_transaksibeli, tb.tanggal_transaksi, tb.norfid, u.nama_user "
                + "ORDER BY tb.tanggal_transaksi DESC";

        try {
            PreparedStatement stmt = con.prepareStatement(query);

            // Set parameter tanggal
            if (startDate != null && endDate != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(endDate);
                cal.add(Calendar.DATE, 1); // Tambah 1 hari
                java.sql.Date adjustedEndDate = new java.sql.Date(cal.getTimeInMillis());
                stmt.setDate(1, new java.sql.Date(startDate.getTime()));
                stmt.setDate(2, adjustedEndDate);
            } else if (startDate != null) {
                stmt.setDate(1, new java.sql.Date(startDate.getTime()));
            }

            ResultSet rs = stmt.executeQuery();
            JTable rawTable = tabelPengeluaran.getTable();
            DefaultTableModel model = (DefaultTableModel) rawTable.getModel();
            model.setRowCount(0);

            DecimalFormat decimalFormat = new DecimalFormat("#,###");
            decimalFormat.setGroupingUsed(true);
            SimpleDateFormat displayDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            int no = 1;
            double totalPengeluaran = 0;

            while (rs.next()) {
                String kodeTransaksi = rs.getString("id_transaksibeli");
                Date tanggalRaw = rs.getDate("tanggal_transaksi");
                String tanggal = displayDateFormat.format(tanggalRaw);
                String namaProduk = rs.getString("nama_produk");
                double total = rs.getDouble("total");
                totalPengeluaran += total;

                String totalFormatted = "Rp. " + decimalFormat.format(total);

                model.addRow(new Object[]{
                    no++,
                    tanggal,
                    kodeTransaksi,
                    namaProduk,
                    totalFormatted
                });
            }

            String totalPengeluaranFormatted = "- Rp. " + decimalFormat.format(totalPengeluaran);
            totalPengeluaranValue.setText(totalPengeluaranFormatted);

            rs.close();
            stmt.close();
            MouseListener[] listeners = rawTable.getMouseListeners();
            for (MouseListener listener : listeners) {
                rawTable.removeMouseListener(listener);
            }

            rawTable.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (isPopupOpen) {
                        return;
                    }

                    int row = rawTable.rowAtPoint(e.getPoint());
                    int col = rawTable.columnAtPoint(e.getPoint());

                    if (row >= 0 && col >= 0 && (col == 2 || col == 3)) {
                        String kodeTransaksi = rawTable.getValueAt(row, 2).toString();

                        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(parentComponent);
                        PopUp_LaporanDetailPengeluaranAtautransaksibeli detailPengeluaranPopup
                                = new PopUp_LaporanDetailPengeluaranAtautransaksibeli(parentFrame);

                        detailPengeluaranPopup.loadPengeluaranTransactionDetails(kodeTransaksi);
                        detailPengeluaranPopup.setVisible(true);

                        isPopupOpen = true;

                        detailPengeluaranPopup.addWindowListener(new WindowAdapter() {
                            @Override
                            public void windowClosed(WindowEvent e) {
                                isPopupOpen = false;
                            }

                            @Override
                            public void windowClosing(WindowEvent e) {
                                isPopupOpen = false;
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saat mengambil data pengeluaran: " + e.getMessage());
        }
    }

    private void initPengeluaranPanel() {
        pengeluaranPanel = new JPanel(new BorderLayout());
        pengeluaranPanel.setOpaque(false);

        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setOpaque(false);
        totalPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        RoundedPanelProduk totalPengeluaranPanel = new RoundedPanelProduk(10);
        totalPengeluaranPanel.setLayout(null);
        totalPengeluaranPanel.setBackground(new Color(40, 50, 100));
        totalPengeluaranPanel.setPreferredSize(new Dimension(520, 40));

        JLabel totalPengeluaranLabel = new JLabel("Total Pengeluaran");
        totalPengeluaranLabel.setForeground(Color.WHITE);
        totalPengeluaranLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        totalPengeluaranLabel.setBounds(15, -5, 250, 50);

        totalPengeluaranValue = new JLabel("- Rp. 15.500.000");
        totalPengeluaranValue.setForeground(new Color(255, 75, 75));
        totalPengeluaranValue.setFont(new Font("SansSerif", Font.BOLD, 14));
        totalPengeluaranValue.setBounds(295, -5, 235, 50);
        totalPengeluaranValue.setHorizontalAlignment(SwingConstants.RIGHT);

        totalPengeluaranPanel.add(totalPengeluaranLabel);
        totalPengeluaranPanel.add(totalPengeluaranValue);
        totalPanel.add(totalPengeluaranPanel, BorderLayout.CENTER);

        String[] kolom = {"No", "Tanggal", "Kode Transaksi", "Detail Produk", "Total"};
        tabelPengeluaran = new JTableRounded(kolom);

        tabelPengeluaran.setColumnWidth(0, 40);
        tabelPengeluaran.setColumnWidth(1, 90);
        tabelPengeluaran.setColumnWidth(2, 100);
        tabelPengeluaran.setColumnWidth(3, 220);
        tabelPengeluaran.setColumnWidth(4, 110);

        loadPengeluaranData(null, null);
        JTable rawTable = tabelPengeluaran.getTable();
        TableColumnModel columnModel = rawTable.getColumnModel();

        columnModel.getColumn(4).setCellEditor(new DefaultCellEditor(new JTextField()) {
            @Override
            public boolean isCellEditable(EventObject event) {
                return false;
            }
        });

        ScrollPane scrollPane = new ScrollPane(rawTable);

        JPanel tabelContainer = new JPanel(new BorderLayout());
        tabelContainer.setBackground(tablePanelBgColor);
        tabelContainer.setOpaque(false);
        tabelContainer.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        tabelContainer.add(scrollPane, BorderLayout.CENTER);

        pengeluaranPanel.add(totalPanel, BorderLayout.NORTH);
        pengeluaranPanel.add(tabelContainer, BorderLayout.CENTER);
    }

    private void initGrafikPanel() {
        labaPanel = new JPanel(new BorderLayout());
        labaPanel.setOpaque(false);

        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setOpaque(false);

        JPanel buttonPanel = new JPanel(new FlowLayout(10, 10, 0));
        buttonPanel.setOpaque(false);

        JButton keuanganButton = createTabButtonForLaba("Keuangan", true);
        JButton karyawanButton = createTabButtonForLaba("Karyawan", false);

        keuanganButton.setPreferredSize(new Dimension(200, 35));
        karyawanButton.setPreferredSize(new Dimension(200, 35));

        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 5, 10));

        buttonPanel.add(keuanganButton);
        buttonPanel.add(karyawanButton);

        JPanel contentPanel = new JPanel(new CardLayout());
        contentPanel.setOpaque(false);

        JPanel keuanganPanel = new JPanel(new BorderLayout());
        keuanganPanel.setOpaque(false);
        diagramlaporankeuangan diagramKeuangan = new diagramlaporankeuangan();
        diagramKeuangan.setOpaque(false);
        keuanganPanel.add(diagramKeuangan, BorderLayout.CENTER);

        JPanel karyawanPanel = new JPanel(new BorderLayout());
        karyawanPanel.setOpaque(false);
        diagramkaryawan diagramKaryawan = new diagramkaryawan();
        diagramKaryawan.setOpaque(false);
        karyawanPanel.add(diagramKaryawan, BorderLayout.CENTER);

        contentPanel.add(keuanganPanel, "keuangan");
        contentPanel.add(karyawanPanel, "karyawan");

        keuanganButton.addActionListener(e -> {
            CardLayout cl = (CardLayout) contentPanel.getLayout();
            cl.show(contentPanel, "keuangan");
            keuanganButton.setBackground(new Color(0, 123, 255));
            karyawanButton.setBackground(Color.BLACK);
            // Update tampilan UI
            keuanganButton.repaint();
            karyawanButton.repaint();
        });

        karyawanButton.addActionListener(e -> {
            CardLayout cl = (CardLayout) contentPanel.getLayout();
            cl.show(contentPanel, "karyawan");
            karyawanButton.setBackground(new Color(0, 123, 255));
            keuanganButton.setBackground(Color.BLACK);
            // Update tampilan UI
            keuanganButton.repaint();
            karyawanButton.repaint();
        });

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(buttonPanel, BorderLayout.CENTER);

        labaPanel.add(headerPanel, BorderLayout.NORTH);
        labaPanel.add(contentPanel, BorderLayout.CENTER);
    }

    private JButton createTabButtonForLaba(String text, boolean isActive) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(120, 40));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(isActive ? new Color(0, 123, 255) : Color.BLACK);

        // Buat dan atur UI rounded dengan border putih
        RoundedButtonLaporan roundedUI = new RoundedButtonLaporan();
        roundedUI.setBorderColor(Color.WHITE);  // Atur warna border putih
        button.setUI(roundedUI);

        return button;
    }

    private JButton createTabButton(final String text) {
        final JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (this == activeTab) {
                    int padding = 10;
                    g.setColor(selectedTabLineColor);
                    g.fillRect(padding, getHeight() - 3, getWidth() - (padding * 2), 3);
                }
            }
        };

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBackground(unselectedTabColor);
        button.setForeground(textColor);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));

        return button;
    }

    private void setActiveTab(JButton tab) {
        activeTab = tab;
        pemasukanTab.repaint();
        pengeluaranTab.repaint();
        labaTab.repaint();
        GrafikTab.repaint();
        tabPanel.repaint();

        CardLayout cardLayout = (CardLayout) ((JPanel) innerContentPanel.getComponent(1)).getLayout();

        if (tab == pemasukanTab) {
            cardLayout.show((JPanel) innerContentPanel.getComponent(1), "pemasukan");
        } else if (tab == pengeluaranTab) {
            cardLayout.show((JPanel) innerContentPanel.getComponent(1), "pengeluaran");
        } else if (tab == labaTab) {
            cardLayout.show((JPanel) innerContentPanel.getComponent(1), "laba");
        } else if (tab == GrafikTab) {
            cardLayout.show((JPanel) innerContentPanel.getComponent(1), "Grafik");
        }
    }

    private void setupListeners() {
        pemasukanTab.addActionListener(e -> setActiveTab(pemasukanTab));
        pengeluaranTab.addActionListener(e -> setActiveTab(pengeluaranTab));
        labaTab.addActionListener(e -> setActiveTab(labaTab));
        GrafikTab.addActionListener(e -> setActiveTab(GrafikTab));
    }

    // Method helper untuk memformat nilai mata uang
    private String formatCurrency(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "Rp. 0";
        }

        // Bersihkan nilai dari karakter non-digit
        String cleanValue = value.replaceAll("[^0-9-]", "").trim();

        // Pastikan format konsisten
        if (cleanValue.startsWith("-")) {
            return "- Rp. " + cleanValue.substring(1).replaceAll("[^0-9]", "");
        } else {
            return "Rp. " + cleanValue.replaceAll("[^0-9]", "");
        }
    }

    public void exportDataKaryawan() {
        // Membuat data
        System.setProperty("log4j2.loggerContextFactory",
                "org.apache.logging.log4j.simple.SimpleLoggerContextFactory");

        List<ExcelExporter.LaporanData> dataList = new ArrayList<>();
        dataList.add(new ExcelExporter.LaporanData("Pemasukan", pemasukanValueLabel.getText()));
        dataList.add(new ExcelExporter.LaporanData("Pengeluaran", pengeluaranValueLabel.getText()));
        dataList.add(new ExcelExporter.LaporanData("Laba Kotor", labaKotorValueLabel.getText()));

        // Export ke Excel
        LocalDate tanggal = LocalDate.now();
        LocalTime waktu = LocalTime.now();

        // Format tanggal dan waktu
        DateTimeFormatter formatTanggal = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter formatWaktu = DateTimeFormatter.ofPattern("HH-mm-ss"); // gunakan '-' agar aman untuk file

        String tanggalStr = tanggal.format(formatTanggal);
        String waktuStr = waktu.format(formatWaktu);

        ExcelExporter.exportToExcel(dataList, "C:/Users/user/Downloads/laporan data keuangan tanggal " + tanggalStr + " waktu " + waktuStr + " .xlsx");
        exportToExcel(dataList, namaUser, "C:/Users/user/Downloads/laporan data keuangan tanggal " + tanggalStr + " waktu " + waktuStr + " .xlsx");
    }

}
