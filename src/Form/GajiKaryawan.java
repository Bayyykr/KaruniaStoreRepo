package Form;

import PopUp_all.*;
import PopUp_all.PopUp_DetailGajiKaryawan.PaymentCallback;
import SourceCode.RoundedBorder;
import SourceCode.RoundedButton;
import SourceCode.ScrollPane;
import SourceCode.JTableRounded;
import SourceCode.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import javax.swing.*;
import javax.swing.table.*;

import java.sql.*;
import db.conn;
import java.util.Locale;

public class GajiKaryawan extends JPanel {

    private Runnable backToDataKaryawan;
    Component parentComponent = this;
    private JFrame parentFrame;
    private JTextField searchField;
    private JTableRounded salaryTable;
    private JButton aturBulan, kembaliButton;
    private JLabel periodeLabel;
    private DefaultTableModel tableModel;
    private List<Object[]> originalEmployeeData = new ArrayList<>();
    private Connection con;

    private int currentYear;
    private String currentMonthName;

    public GajiKaryawan() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        con = conn.getConnection();

        // Tambahkan border rounded pada panel utama
        setBorder(new RoundedBorder(15, Color.BLACK, 3));

        // Panel dalam untuk konten (dengan padding)
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top Panel with Search and Buttons
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Gaji Karyawan");
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 30));
        titleLabel.setForeground(Color.BLACK);

        // Title Panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.WEST);

        // Search Panel
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(Color.WHITE);
        searchField = new JTextField("Search");
        searchField.setForeground(Color.GRAY);
        searchField.setBackground(Color.WHITE);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(300, 35));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        searchPanel.add(searchField, BorderLayout.WEST);

        searchField.addFocusListener(new FocusAdapter() {
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
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setBackground(Color.WHITE);

        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH);
        currentYear = cal.get(Calendar.YEAR);

        Locale locale = new Locale("id", "ID");
        cal.set(Calendar.MONTH, currentMonth);
        currentMonthName = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, locale);
        currentMonthName = currentMonthName.toUpperCase();

        periodeLabel = new JLabel(currentMonthName + " " + currentYear);
        periodeLabel.setPreferredSize(new Dimension(130, 35));
        periodeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        periodeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        periodeLabel.setForeground(Color.BLACK);
        periodeLabel.setBackground(Color.WHITE);
        periodeLabel.setOpaque(false);
        periodeLabel.setBorder(new RoundedBorder(10, Color.BLACK, 1));

        // ATUR JADWAL GAJI button
        aturBulan = new JButton("ATUR BULAN DAN TAHUN");
        aturBulan.setPreferredSize(new Dimension(170, 35));
        aturBulan.setBackground(new Color(52, 61, 70));
        aturBulan.setForeground(Color.WHITE);
        aturBulan.setFocusPainted(false);
        aturBulan.setBorderPainted(false);
        aturBulan.setBorder(new RoundedBorder(5, Color.BLACK, 1));
        aturBulan.setContentAreaFilled(false);
        aturBulan.setOpaque(false);
        aturBulan.setFont(new Font("Arial", Font.BOLD, 12));
        aturBulan.setUI(new RoundedButton());
        aturBulan.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                aturBulan.setLocation(aturBulan.getX(), aturBulan.getY() + 2); // Tombol turun sedikit
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                aturBulan.setLocation(aturBulan.getX(), aturBulan.getY() - 2); // Kembali ke posisi semula
            }
        });
        aturBulan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(aturBulan);
                PopUp_AturBulanDataAbsen.resetShowingPopupFlag();
                String monthYearText = periodeLabel.getText().trim();

                // Parse bulan dan tahun
                int bulanIndex = 1; // Default ke Januari
                int tahun = Calendar.getInstance().get(Calendar.YEAR); // Default ke tahun sekarang

                try {
                    String[] parts = monthYearText.split("\\s+");
                    System.out.println(parts[0]);
                    if (parts.length >= 2) {
                        // Ambil nama bulan dan convert ke indeks
                        String bulanName = parts[0];
                        bulanIndex = getMonthIndexFromName(bulanName);
                        System.out.println(bulanIndex);

                        // Ambil tahun
                        tahun = Integer.parseInt(parts[parts.length - 1]);
                    }

                    System.out.println("Membuka popup dengan bulan index: " + bulanIndex + ", tahun: " + tahun);
                } catch (Exception f) {
                    System.out.println("Error parsing bulan/tahun: " + f.getMessage());
                    f.printStackTrace();
                }

                PopUp_AturBulanGajiKaryawan dialog = new PopUp_AturBulanGajiKaryawan(parentFrame, bulanIndex, tahun, periodeLabel);

                dialog.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        loadDataKaryawan();
                        tableModel.fireTableDataChanged(); // Force table to refresh
                    }
                });

                dialog.setVisible(true);
            }
        });

        // KEMBALI button
        kembaliButton = new JButton("KEMBALI");
        kembaliButton.setPreferredSize(new Dimension(100, 35));
        kembaliButton.setBackground(Color.RED);
        kembaliButton.setForeground(Color.WHITE);
        kembaliButton.setFocusPainted(false);
        kembaliButton.setBorderPainted(false);
        kembaliButton.setBorder(new RoundedBorder(5, Color.BLACK, 1));
        kembaliButton.setContentAreaFilled(false);
        kembaliButton.setOpaque(false);
        kembaliButton.setFont(new Font("Arial", Font.BOLD, 12));
        kembaliButton.setUI(new RoundedButton());

        kembaliButton.addActionListener(e -> {
            // Panggil callback untuk mengganti panel
            if (backToDataKaryawan != null) {
                backToDataKaryawan.run();
            }
        });

        kembaliButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                kembaliButton.setLocation(kembaliButton.getX(), kembaliButton.getY() + 2); // Tombol turun sedikit
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                kembaliButton.setLocation(kembaliButton.getX(), kembaliButton.getY() - 2); // Kembali ke posisi semula
            }
        });

        buttonPanel.add(periodeLabel);
        buttonPanel.add(aturBulan);
        buttonPanel.add(kembaliButton);

        JPanel searchButtonPanel = new JPanel(new BorderLayout(10, 10));
        searchButtonPanel.setBackground(Color.WHITE);
        searchButtonPanel.add(searchPanel, BorderLayout.WEST);
        searchButtonPanel.add(buttonPanel, BorderLayout.EAST);

        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(searchButtonPanel, BorderLayout.CENTER);

        // Table columns
        String[] columnNames = {"No", "NO RFID", "Nama Karyawan", "Jabatan", "Status Gaji", "Aksi"};

        // Create the JTableRounded
        salaryTable = new JTableRounded(columnNames, 1027, 450);

        // Get the JTable from JTableRounded
        JTable table = salaryTable.getTable();
        table.setSelectionBackground(new Color(184, 207, 229));
        table.setSelectionForeground(Color.WHITE);

        // Set table to fill the viewport height
        table.setFillsViewportHeight(true);

        tableModel = (DefaultTableModel) table.getModel();
        tableModel.setRowCount(0); // Clear any existing rows
        table.setRowHeight(50);
        // Renderer untuk alignment tengah yang mempertahankan warna seleksi dan zebra stripping
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Pertahankan warna seleksi dan zebra stripping dari renderer default
                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(table.getSelectionForeground());
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));
                    c.setForeground(table.getForeground());
                }

                // Set alignment tengah
                ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        };

// Renderer khusus untuk Status Gaji dengan warna teks
        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Pertahankan warna background
                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(table.getSelectionForeground());
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));
                    c.setForeground(table.getForeground());
                }

                // Style khusus untuk status gaji
//        c.setFont(c.getFont().deriveFont(Font.BOLD));
//        if (value != null && value.toString().equals("Sudah Dibayar")) {
//            c.setForeground(new Color(40, 167, 69)); // Hijau
//        } else {
//            c.setForeground(new Color(220, 53, 69)); // Merah
//        }
                ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        };

// Terapkan renderer ke kolom yang ingin ditengahkan
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(statusRenderer);

        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 5) {
                    return comp;
                }
                // Jika seleksi, gunakan warna seleksi
                if (isSelected) {
                    comp.setBackground(table.getSelectionBackground());
                    comp.setForeground(table.getSelectionForeground());
                } else {
                    // Jika tidak diseleksi, gunakan warna zebra-striping
                    comp.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));
                    comp.setForeground(table.getForeground());
                }

                return comp;
            }
        });
        loadDataKaryawan();

        // Customize table columns
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);  // No
        columnModel.getColumn(1).setPreferredWidth(100); // NO RFID
        columnModel.getColumn(2).setPreferredWidth(200); // Nama Karyawan
        columnModel.getColumn(3).setPreferredWidth(150); // Jabatan
        columnModel.getColumn(4).setPreferredWidth(150); // Status Gaji
        columnModel.getColumn(5).setPreferredWidth(100); // Aksi

        // Buat kelas ButtonEditor dan ButtonRenderer untuk menghandle tombol di tabel
        table.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox(), table));

        // ScrollPane
        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setThumbColor(new Color(80, 80, 80, 180));
        scrollPane.setTrackColor(new Color(240, 240, 240, 80));
        scrollPane.setThumbThickness(8);
        scrollPane.setThumbRadius(8);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Table Panel dengan border rounded
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Tambahkan komponen ke content panel
        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(tablePanel, BorderLayout.CENTER);

        // Tambahkan content panel ke panel utama
        add(contentPanel, BorderLayout.CENTER);
        setupSearchFunctionality();
    }

    // Kelas ButtonRenderer untuk menampilkan tombol dalam cell - DENGAN PERBAIKAN
    class ButtonRenderer extends JPanel implements TableCellRenderer {

        private JButton bayarButton, hapusButton;

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
            setOpaque(true); // Kita ubah jadi true, tapi warnanya akan disesuaikan

            // Buat tombol bayar
            bayarButton = new JButton();
            bayarButton.setPreferredSize(new Dimension(40, 30));
            bayarButton.setBackground(new Color(40, 199, 111));
            bayarButton.setForeground(Color.WHITE);
            bayarButton.setBorderPainted(false);
            bayarButton.setFocusPainted(false);
            bayarButton.setContentAreaFilled(true);
            bayarButton.setFont(new Font("Arial", Font.PLAIN, 12));
            bayarButton.setUI(new RoundedButton());
            bayarButton.setOpaque(false);
            bayarButton.setContentAreaFilled(true);

            try {
                ImageIcon icon = new ImageIcon(getClass().getResource("../SourceImage/icon/bayar-icon.png"));
                Image scaledImage = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                bayarButton.setIcon(new ImageIcon(scaledImage));
            } catch (Exception e) {
                bayarButton.setText("Bayar");
            }

            add(bayarButton);
        }

        // Pada class ButtonRenderer, ubah metode getTableCellRendererComponent
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            // Reset panel
            removeAll();

            // Set warna latar belakang berdasarkan kondisi baris
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                // Warna zebra-striping
                setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));
            }

            // Memastikan border tabel tetap terlihat
            setBorder(BorderFactory.createEmptyBorder());

            // Periksa status pembayaran
            String status = (String) table.getValueAt(row, 4);

            // Jika sudah dibayar, tampilkan hanya tombol detail/cetak
            if (status.equals("Sudah Dibayar")) {
                JButton detailButton = new JButton();
                detailButton.setPreferredSize(new Dimension(100, 30));
                detailButton.setBackground(new Color(52, 152, 219)); // Warna biru
                detailButton.setForeground(Color.WHITE);
                detailButton.setBorderPainted(false);
                detailButton.setFocusPainted(false);
                detailButton.setContentAreaFilled(true);
                detailButton.setFont(new Font("Poppins", Font.PLAIN, 15));
                detailButton.setText("Cetak");
                detailButton.setUI(new RoundedButton());
                detailButton.setOpaque(false);
                detailButton.setContentAreaFilled(true);

                try {
                    // Perhatikan path yang benar untuk icon detail
                    ImageIcon icon = new ImageIcon(getClass().getResource("../SourceImage/icon/detail_icon.png"));
                    Image scaledImage = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                    detailButton.setIcon(new ImageIcon(scaledImage));
                } catch (Exception e) {
                    // Jika icon tidak tersedia, hanya menampilkan teks
                    System.out.println("Icon detail tidak ditemukan: " + e.getMessage());
                }

                add(detailButton);
            } else {
                // Jika belum dibayar, tampilkan tombol bayar dan hapus
                add(bayarButton);
            }

            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {

        private JPanel panel;
        private JButton bayarButton;
        private String action = "";
        private boolean isPushed;
        private int clickedRow;
        private JTable table;
        private DefaultTableModel tableModel;

        public ButtonEditor(JCheckBox checkBox, JTable table) {
            super(checkBox);
            this.table = table;
            this.tableModel = (DefaultTableModel) table.getModel();

            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 10));
            panel.setOpaque(true); // Kita ubah jadi true, tapi warnanya akan disesuaikan

            // Buat tombol bayar
            bayarButton = new JButton();
            bayarButton.setPreferredSize(new Dimension(40, 30));
            bayarButton.setBackground(new Color(40, 199, 111));
            bayarButton.setForeground(Color.WHITE);
            bayarButton.setBorderPainted(false);
            bayarButton.setFocusPainted(false);
            bayarButton.setContentAreaFilled(true);
            bayarButton.setFont(new Font("Arial", Font.PLAIN, 12));
            bayarButton.setUI(new RoundedButton());
            bayarButton.setOpaque(false);
            bayarButton.setContentAreaFilled(true);

            try {
                ImageIcon icon = new ImageIcon(getClass().getResource("../SourceImage/icon/bayar-icon.png"));
                Image scaledImage = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                bayarButton.setIcon(new ImageIcon(scaledImage));
            } catch (Exception e) {
                bayarButton.setText("Bayar");
            }

            bayarButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    action = "BAYAR";
                    isPushed = true;
                    fireEditingStopped();
                }
            });
            panel.add(bayarButton);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            // Reset panel
            panel.removeAll();
            isPushed = false;
            action = "";
            clickedRow = row;

            // Periksa status pembayaran
            String status = (String) table.getValueAt(row, 4);

            // Jika sudah dibayar, tampilkan hanya tombol detail
            if (status.equals("Sudah Dibayar")) {
                JButton detailButton = new JButton();
                detailButton.setPreferredSize(new Dimension(100, 30));
                detailButton.setBackground(new Color(52, 152, 219)); // Warna biru
                detailButton.setForeground(Color.WHITE);
                detailButton.setBorderPainted(false);
                detailButton.setFocusPainted(false);
                detailButton.setContentAreaFilled(true);
                detailButton.setFont(new Font("Poppins", Font.PLAIN, 15));
                detailButton.setText("Cetak");
                detailButton.setUI(new RoundedButton());
                detailButton.setOpaque(false);
                detailButton.setContentAreaFilled(true);

                try {
                    // Perbaiki path untuk icon, gunakan path yang sama dengan di ButtonRenderer
                    ImageIcon icon = new ImageIcon(getClass().getResource("../SourceImage/icon/detail_icon.png"));
                    Image scaledImage = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                    detailButton.setIcon(new ImageIcon(scaledImage));
                } catch (Exception e) {
                    // Jika icon tidak tersedia, hanya menampilkan teks
                    System.out.println("Icon detail tidak ditemukan: " + e.getMessage());
                }

                detailButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        action = "DETAIL";
                        isPushed = true;
                        fireEditingStopped();
                    }
                });

                panel.add(detailButton);
            } else {
                // Jika belum dibayar, tampilkan tombol bayar dan hapus seperti biasa
                panel.add(bayarButton);
            }

            // Set warna latar belakang
            if (isSelected) {
                panel.setBackground(table.getSelectionBackground());
            } else {
                // Warna zebra-striping
                panel.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));
            }

            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            String periodelabelText = periodeLabel.getText().trim();
            String[] parts = periodelabelText.split("\\s+");

            // Extract month name and year from periodeLabel
            String currentMonth = "";
            String currentYear = "";

            if (parts.length >= 2) {
                currentMonth = parts[0]; // First part is month
                currentYear = parts[parts.length - 1]; // Last part is year
            } else {
                // Fallback if parsing fails
                Calendar cal = Calendar.getInstance();
                Locale locale = new Locale("id", "ID");
                currentMonth = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, locale).toUpperCase();
                currentYear = String.valueOf(cal.get(Calendar.YEAR));
            }

            System.out.println("Current Month: " + currentMonth);
            System.out.println("Current Year: " + currentYear);

            // Get month number using the existing method
            String monthNumber = getMonthNumber(currentMonth);

            // Format date for SQL (e.g., 2025-05-01)
            String periodStartDate = currentYear + "-" + monthNumber + "-01";

            // Calculate the last day of the month
            int lastDay = getLastDayOfMonth(Integer.parseInt(currentYear), Integer.parseInt(monthNumber));
            String periodEndDate = currentYear + "-" + monthNumber + "-" + lastDay;

            System.out.println("Period Start: " + periodStartDate);
            System.out.println("Period End: " + periodEndDate);

            if (isPushed) {
                String norfid = table.getValueAt(clickedRow, 1).toString();
                if (action.equals("BAYAR")) {
                    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(parentComponent);
                    System.out.println("RFID: " + norfid);

                    // Get current month and year from periodeLabel
                    // Check employee attendance for this month
                    boolean hasAttendance = checkEmployeeAttendance(norfid, periodStartDate, periodEndDate);
                    System.out.println("Has Attendance: " + hasAttendance);

                    if (hasAttendance) {
                        // If attendance exists, show salary details dialog
                        PopUp_KonfrimasiCetakGajiKaryawan dialog = new PopUp_KonfrimasiCetakGajiKaryawan(parentFrame, norfid, periodStartDate, periodEndDate);

                        // Add a window listener to update the table after dialog is closed
                        dialog.addWindowListener(new WindowAdapter() {
                            @Override
                            public void windowClosed(WindowEvent e) {
                                loadDataKaryawan();
                                tableModel.fireTableDataChanged(); // Force table to refresh
                            }
                        });

                        dialog.setVisible(true);
                    }
                } else if (action.equals("HAPUS")) {
                    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(parentComponent);
//                    PopUp_HapusDataGajiKaryawan dialog = new PopUp_HapusDataGajiKaryawan(parentFrame);
//                    dialog.setVisible(true);
                } else if (action.equals("DETAIL")) {
                    // Get parent frame dynamically
                    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(parentComponent);
                    PopUp_DetailGajiKaryawan tambahKaryawanDialog = new PopUp_DetailGajiKaryawan(parentFrame, norfid, periodStartDate, periodEndDate);
                    tambahKaryawanDialog.setVisible(true);
                }
            }
            isPushed = false;
            return "";
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
            // Tambahan untuk memastikan seleksi dihapus
            table.clearSelection();
            table.repaint();
        }
    }

    public void setBackToDataKaryawan(Runnable listener) {
        this.backToDataKaryawan = listener;
    }

    private void loadDataKaryawan() {
        tableModel.setRowCount(0);
        originalEmployeeData.clear();

        // Get current month and year from periodeLabel
        String periodelabelText = periodeLabel.getText().trim();
        String[] parts = periodelabelText.split("\\s+");

        // Extract month name and year from periodeLabel
        String currentMonth = "";
        String currentYear = "";

        if (parts.length >= 2) {
            currentMonth = parts[0]; // First part is month
            currentYear = parts[parts.length - 1]; // Last part is year
        } else {
            // Fallback if parsing fails
            Calendar cal = Calendar.getInstance();
            Locale locale = new Locale("id", "ID");
            currentMonth = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, locale).toUpperCase();
            currentYear = String.valueOf(cal.get(Calendar.YEAR));
        }

        System.out.println("Loading data for: " + currentMonth + " " + currentYear);

        // Format date for SQL comparison
        String monthNumber = getMonthNumber(currentMonth);

        // Get the last day of the month
        int lastDay = getLastDayOfMonth(Integer.parseInt(currentYear), Integer.parseInt(monthNumber));

        String periodStartDate = currentYear + "-" + monthNumber + "-01";
        String periodEndDate = currentYear + "-" + monthNumber + "-" + lastDay;

        System.out.println("Period Start: " + periodStartDate);
        System.out.println("Period End: " + periodEndDate);

        try {
            // Query to get all employees from user table
            String userQuery = "SELECT * FROM user WHERE jabatan != 'owner' AND status != 'nonaktif'";
            try (PreparedStatement st = con.prepareStatement(userQuery)) {
                ResultSet rs = st.executeQuery();
                int counter = 1;
                while (rs.next()) {
                    String norfid = rs.getString("norfid");
                    String name = rs.getString("nama_user");
                    String position = rs.getString("jabatan");

                    // Check if employee has been paid this month
                    boolean isPaid = checkPaymentStatus(con, norfid, periodStartDate, periodEndDate, name);
                    String paymentStatus = isPaid ? "Sudah Dibayar" : "Belum Dibayar";

                    // Add row to table
                    Object[] row = {
                        String.valueOf(counter++),
                        norfid,
                        name,
                        position,
                        paymentStatus,
                        ""
                    };
                    tableModel.addRow(row);
                    originalEmployeeData.add(row.clone());
                }
            }

            String searchText = searchField.getText();
            if (!searchText.equals("Search") && !searchText.isEmpty()) {
                applySearchFilter();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading employee data: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private boolean checkPaymentStatus(Connection con, String norfid,
            String startDate, String endDate, String nama) throws SQLException {
        try {
            // Extract month and year from startDate
            String bulan = "";
            String tahun = "";

            String[] dateParts = startDate.split("-");
            if (dateParts.length >= 2) {
                tahun = dateParts[0]; // Year is the first part

                // Convert month number to name if needed
                int monthNum = Integer.parseInt(dateParts[1]);
                switch (monthNum) {
                    case 1:
                        bulan = "Januari";
                        break;
                    case 2:
                        bulan = "Februari";
                        break;
                    case 3:
                        bulan = "Maret";
                        break;
                    case 4:
                        bulan = "April";
                        break;
                    case 5:
                        bulan = "Mei";
                        break;
                    case 6:
                        bulan = "Juni";
                        break;
                    case 7:
                        bulan = "Juli";
                        break;
                    case 8:
                        bulan = "Agustus";
                        break;
                    case 9:
                        bulan = "September";
                        break;
                    case 10:
                        bulan = "Oktober";
                        break;
                    case 11:
                        bulan = "November";
                        break;
                    case 12:
                        bulan = "Desember";
                        break;
                    default:
                        bulan = String.valueOf(monthNum).toUpperCase();
                }
            }

            String query = "SELECT * FROM biaya_operasional WHERE catatan LIKE ?";
            try (PreparedStatement st = con.prepareStatement(query)) {
                st.setString(1, "Pembayaran gaji karyawan - " + nama + " " + bulan + " " + tahun);
                ResultSet rs = st.executeQuery();
                return rs.next();
            }
        } finally {
            // Your finally block was empty
        }
    }

    private boolean checkEmployeeAttendance(String norfid, String startDate, String endDate) {
        boolean hasAttendance = false;

        try {
            // Query untuk mengambil data absensi karyawan dalam bulan ini
            String query = "SELECT * FROM absensi WHERE norfid = ? AND "
                    + "waktu_masuk BETWEEN ? AND ? "
                    + "ORDER BY waktu_masuk ASC";

            try (PreparedStatement st = con.prepareStatement(query)) {
                st.setString(1, norfid);
                st.setString(2, startDate + " 00:00:00");
                st.setString(3, endDate + " 23:59:59");
                ResultSet rs = st.executeQuery();

                int count = 0;
                while (rs.next()) {
                    count++;
                    int id = rs.getInt("id_absen");
                    String waktuMasuk = rs.getString("waktu_masuk");
                    String waktuKeluar = rs.getString("waktu_keluar");
                    String norFID = rs.getString("norfid");

                    System.out.println(id + " | " + waktuMasuk + " | " + waktuKeluar + " | " + norFID);
                }

                hasAttendance = (count > 0);

                if (count == 0) {
                    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(parentComponent);
                    PopUp_GajiKaryawanBelumMelakukanAbsensiPdaBulanIni Absensi = new PopUp_GajiKaryawanBelumMelakukanAbsensiPdaBulanIni(parentFrame);
                    Absensi.setVisible(true);
                    System.out.println("Karyawan dengan NORFID " + norfid + " tidak memiliki data absensi pada bulan ini.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saat memeriksa data absensi: " + e.getMessage());
            e.printStackTrace();
        }

        return hasAttendance;
    }

    private String getMonthNumber(String monthName) {
        String[] monthNames = {"JANUARI", "FEBRUARI", "MARET", "APRIL", "MEI", "JUNI",
            "JULI", "AGUSTUS", "SEPTEMBER", "OKTOBER", "NOVEMBER", "DESEMBER"};

        for (int i = 0; i < monthNames.length; i++) {
            if (monthNames[i].equals(monthName)) {
                // Add 1 because array is 0-indexed, and format with leading zero
                return String.format("%02d", i + 1);
            }
        }

        // Default to current month if not found
        return String.format("%02d", Calendar.getInstance().get(Calendar.MONTH) + 1);
    }

    private int getLastDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1); // Convert from 1-based to 0-based
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    private int getMonthIndexFromName(String monthName) {
        // Map nama bulan dalam bahasa Indonesia ke indeks (1-12)
        String[][] bulanMap = {
            {"JANUARI", "1"}, {"FEBRUARI", "2"}, {"MARET", "3"},
            {"APRIL", "4"}, {"MEI", "5"}, {"JUNI", "6"},
            {"JULI", "7"}, {"AGUSTUS", "8"}, {"SEPTEMBER", "9"},
            {"OKTOBER", "10"}, {"NOVEMBER", "11"}, {"DESEMBER", "12"}
        };

        int index = 0;

        String upperMonthName = monthName.toUpperCase();
        System.out.println(upperMonthName + " tanggal");
        for (String[] entry : bulanMap) {
            if (upperMonthName.equals(entry[0])) {
                index = Integer.parseInt(entry[1]);
                System.out.println("ini entry index " + index);
            }
        }
        return index;
    }

    private void setupSearchFunctionality() {
        // Add a KeyListener to the searchField
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                applySearchFilter();
            }
        });
    }

    private void applySearchFilter() {
        String searchText = searchField.getText().toLowerCase();

        // If search field contains the default "Search" text or is empty, show all data
        if (searchText.equals("search") || searchText.isEmpty()) {
            tableModel.setRowCount(0);
            for (Object[] row : originalEmployeeData) {
                tableModel.addRow(row);
            }
            return;
        }

        // Clear the table
        tableModel.setRowCount(0);

        // Filter data and add matching rows
        for (Object[] row : originalEmployeeData) {
            // Convert each field to string for searching
            String norfid = row[1].toString().toLowerCase();
            String name = row[2].toString().toLowerCase();
            String position = row[3].toString().toLowerCase();
            String status = row[4].toString().toLowerCase();

            // Check if any field contains the search text
            if (norfid.contains(searchText)
                    || name.contains(searchText)
                    || position.contains(searchText)
                    || status.contains(searchText)) {
                tableModel.addRow(row);
            }
        }
    }

    public void refreshData() {
        loadDataKaryawan(); // Memuat ulang data dari database
        tableModel.fireTableDataChanged(); // Memperbarui tampilan tabel
    }
}
