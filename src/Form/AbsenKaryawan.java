package Form;

import PopUp_all.PopUp_AturBulanDataAbsen;
import SourceCode.JTableRounded;
import SourceCode.RoundedBorder;
import SourceCode.RoundedButton;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import javax.swing.plaf.basic.BasicTextFieldUI;
import db.conn;
import java.text.ParseException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;

public class AbsenKaryawan extends JPanel {

    private Runnable backToDataKaryawan;
    private JTextField searchField, monthField;
    private JButton configButton, backButton;
    private JTableRounded tableRounded;
    private JTable mainTable;
    private JPanel legendPanel;
    private JPanel paginationPanel;
    private Calendar currentDate; // To keep track of current displayed date
    private SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy");
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private String currentSearchTerm = "";
    Locale indonesianLocale = new Locale("id", "ID");

    // Database connection parameters
    private Connection con;

    private int selectedMonth;
    private int selectedYear;

    public AbsenKaryawan() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, new Color(0, 0, 0), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        setBackground(new Color(255, 255, 255));

        con = conn.getConnection();

        this.selectedMonth = Calendar.getInstance().get(Calendar.MONTH) + 1; // +1 karena bulan di Calendar dimulai dari 0
        this.selectedYear = Calendar.getInstance().get(Calendar.YEAR);

        currentDate = Calendar.getInstance();
        currentDate.set(Calendar.DAY_OF_MONTH, 1);

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setOpaque(false);

        // Title panel at the top
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);

        // Title
        JLabel titleLabel = new JLabel("Data Absen");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        topPanel.add(titlePanel, BorderLayout.NORTH);

        // Controls panel for search field and buttons below the title
        JPanel controlsPanel = new JPanel(new BorderLayout(10, 0));
        controlsPanel.setOpaque(false);

        // Search field on the left - UPDATED with rounded border
        searchField = new JTextField("Search");
        searchField.setPreferredSize(new Dimension(400, 40));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchField.setMargin(new Insets(2, 10, 2, 10));
        searchField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search")) {
                    searchField.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search");
                }
            }
        });

        // Add key listener for search functionality
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchTerm = searchField.getText();
                if (!searchTerm.equals("Search")) {
                    performSearchh(searchTerm);
                }
            }
        });

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(searchField);
        controlsPanel.add(searchPanel, BorderLayout.WEST);

        // Month display and buttons on the right
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setOpaque(false);

        // Month field - UPDATED with rounded border
        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", indonesianLocale);
        monthField = new JTextField(monthYearFormat.format(currentDate.getTime()).toUpperCase());
        monthField.setPreferredSize(new Dimension(140, 40));
        monthField.setEnabled(false);
        monthField.setBackground(Color.WHITE);
        monthField.setDisabledTextColor(Color.BLACK);
        monthField.setHorizontalAlignment(JTextField.CENTER);
        monthField.setBorder(new RoundedBorder(15, Color.LIGHT_GRAY, 1));

        monthField.setUI(new BasicTextFieldUI() {
            @Override
            protected void paintSafely(Graphics g) {
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, monthField.getWidth(), monthField.getHeight());
                super.paintSafely(g);
            }
        });

        // Config button - UPDATED with rounded UI
        configButton = new JButton("ATUR BULAN DAN TAHUN");
        configButton.setBackground(new Color(52, 73, 94));
        configButton.setForeground(Color.WHITE);
        configButton.setPreferredSize(new Dimension(220, 40));
        configButton.setFocusPainted(false);
        configButton.setBorderPainted(false);
        configButton.setBorder(new RoundedBorder(5, Color.BLACK, 1));
        configButton.setContentAreaFilled(false);
        configButton.setOpaque(false);
        configButton.setUI(new RoundedButton());

        // Back button - UPDATED with rounded UI
        backButton = new JButton("KEMBALI");
        backButton.setBackground(new Color(231, 76, 60));
        backButton.setForeground(Color.WHITE);
        backButton.setPreferredSize(new Dimension(120, 40));
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setBorder(new RoundedBorder(5, Color.BLACK, 1));
        backButton.setContentAreaFilled(false);
        backButton.setOpaque(false);
        backButton.setUI(new RoundedButton());

        backButton.addActionListener(e -> {
            // Panggil callback untuk mengganti panel
            if (backToDataKaryawan != null) {
                backToDataKaryawan.run();
            }
        });

        configButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                configButton.setLocation(configButton.getX(), configButton.getY() + 1);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                configButton.setLocation(configButton.getX(), configButton.getY() - 1);
            }
        });
        configButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(configButton);
                PopUp_AturBulanDataAbsen.resetShowingPopupFlag();

                // Ambil teks dari monthField
                String monthYearText = monthField.getText().trim();

                // Parse bulan dan tahun
                int bulanIndex = 1; // Default ke Januari
                int tahun = Calendar.getInstance().get(Calendar.YEAR); // Default ke tahun sekarang

                try {
                    // Format monthField: "BULAN TAHUN" (contoh: "APRIL 2025")
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

                // Buka popup dengan indeks bulan dan tahun
                PopUp_AturBulanDataAbsen popup = new PopUp_AturBulanDataAbsen(parentFrame, bulanIndex, tahun);
                popup.setVisible(true);
            }
        });

        backButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                backButton.setLocation(backButton.getX(), backButton.getY() + 1);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                backButton.setLocation(backButton.getX(), backButton.getY() - 1);
            }
        });

        // Add all buttons to the right panel
        buttonsPanel.add(monthField);
        buttonsPanel.add(configButton);
        buttonsPanel.add(backButton);
        controlsPanel.add(buttonsPanel, BorderLayout.EAST);

        topPanel.add(controlsPanel, BorderLayout.CENTER);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(topPanel, BorderLayout.NORTH);

        // Create the table
        createTable();
        add(tableRounded, BorderLayout.CENTER);

        // Bottom panel for legend and pagination
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 10));
        bottomPanel.setOpaque(false);

        // Legend Panel
        legendPanel = createLegendPanel();
        bottomPanel.add(legendPanel, BorderLayout.NORTH);

        add(bottomPanel, BorderLayout.SOUTH);

        // Load data from database when panel is first created
        loadDataFromDatabase();
        initializeCurrentMonthView();
    }

    private void createTable() {
        // Define columns: No, Nama Karyawan, Posisi, «, Tanggal, », Total
        String[] columnHeaders = {"No.", "Nama Karyawan", "Posisi", "«", "Tanggal", "»", "Total"};

        // Create table with proper dimensions
        tableRounded = new JTableRounded(columnHeaders, 1023, 520);
        mainTable = tableRounded.getTable();

        // Configure the table appearance
        mainTable.setRowHeight(50); // Increased row height to accommodate date labels
        mainTable.setShowGrid(false);
        mainTable.setShowHorizontalLines(true);
        mainTable.setShowVerticalLines(false);
        mainTable.setGridColor(Color.LIGHT_GRAY);
        mainTable.setBackground(Color.WHITE);

        // Set the header row height
        JTableHeader header = mainTable.getTableHeader();
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 60));

        tableRounded.setColumnWidth(0, 50);     // No. (slightly wider)
        tableRounded.setColumnWidth(1, 150);    // Nama Karyawan
        tableRounded.setColumnWidth(2, 120);    // Posisi
        tableRounded.setColumnWidth(3, 40);     // « (slightly wider)
        tableRounded.setColumnWidth(4, 530);    // Tanggal (make wider to fit 3 dates)
        tableRounded.setColumnWidth(5, 40);     // » (slightly wider)
        tableRounded.setColumnWidth(6, 90);     // Total

        // Set custom renderers for header and cells
        setCustomRenderers();

        // Add mouse listener for the navigation buttons (for data cells)
        addNavigationListeners();

        // Add mouse listener for the header navigation buttons
        addHeaderNavigationListeners();

        // Lock table configuration
        tableRounded.setColumnsResizable(false);
        tableRounded.lockTablePosition();
    }

    private void addNavigationListeners() {
        mainTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = mainTable.columnAtPoint(e.getPoint());

                // If the previous button (column 3, «) is clicked
                if (column == 3) {
                    // Move back 3 days
                    currentDate.add(Calendar.DAY_OF_MONTH, -3);

                    updateSelectedMonthYearFromCurrentDate();
                    if (!currentSearchTerm.isEmpty()) {
                        performSearchh(currentSearchTerm);
                    } else {
                        // Otherwise load all data
                        updateTable();
                    }
                } // If the next button (column 5, ») is clicked
                else if (column == 5) {
                    // Move forward 3 days
                    currentDate.add(Calendar.DAY_OF_MONTH, 3);

                    updateSelectedMonthYearFromCurrentDate();
                    if (!currentSearchTerm.isEmpty()) {
                        performSearchh(currentSearchTerm);
                    } else {
                        // Otherwise load all data
                        updateTable();
                    }
                }
            }
        });
    }

    private void addHeaderNavigationListeners() {
        mainTable.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = mainTable.getTableHeader().columnAtPoint(e.getPoint());

                // If the previous button header (column 3, «) is clicked
                if (column == 3) {
                    // Move back 3 days
                    currentDate.add(Calendar.DAY_OF_MONTH, -3);
                    updateSelectedMonthYearFromCurrentDate();
                    if (!currentSearchTerm.isEmpty()) {
                        performSearchh(currentSearchTerm);
                    } else {
                        // Otherwise load all data
                        updateTable();
                    }
                } // If the next button header (column 5, ») is clicked
                else if (column == 5) {
                    // Move forward 3 days
                    currentDate.add(Calendar.DAY_OF_MONTH, 3);
                    updateSelectedMonthYearFromCurrentDate();
                    if (!currentSearchTerm.isEmpty()) {
                        performSearchh(currentSearchTerm);
                    } else {
                        // Otherwise load all data
                        updateTable();
                    }
                }
            }
        });
    }

    private void updateTable() {
        if (!currentSearchTerm.isEmpty()) {
            performSearchh(currentSearchTerm);
        } else {
            // Otherwise load all data
            loadDataFromDatabase();
        }
    }

    private void loadDataFromDatabase() {
        if (!currentSearchTerm.isEmpty()) {
            performSearchh(currentSearchTerm);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) mainTable.getModel();
        model.setRowCount(0); // Clear existing data

        try {
            String employeeQuery = "SELECT norfid, nama_user, jabatan "
                    + "FROM user WHERE jabatan != 'owner' AND status != 'nonaktif' "
                    + "ORDER BY nama_user";

            try (PreparedStatement empStmt = con.prepareStatement(employeeQuery); ResultSet empRs = empStmt.executeQuery()) {

                int rowNum = 1;

                while (empRs.next()) {
                    String norfid = empRs.getString("norfid");
                    String name = empRs.getString("nama_user");
                    String position = empRs.getString("jabatan");

                    // Gunakan selectedMonth dan selectedYear yang sudah diupdate
                    int totalAttendance = getTotalAttendanceDays(norfid, selectedMonth, selectedYear);

                    Object[] row = new Object[model.getColumnCount()];
                    row[0] = rowNum + ".";
                    row[1] = name;
                    row[2] = position != null ? position : "";
                    row[6] = totalAttendance > 0 ? String.valueOf(totalAttendance) : "";

                    model.addRow(row);
                    rowNum++;
                }
            }

            mainTable.repaint();
            updateMonthFieldDisplay(); // Pastikan tampilan bulan sesuai

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private int getTotalAttendanceDays(String norfid, int month, int year) {
        int total = 0;
        try {
            // SQL query to count distinct days for specific month and year based on waktu_masuk
            String query = "SELECT COUNT(DISTINCT DATE(waktu_masuk)) AS total FROM absensi "
                    + "WHERE norfid = ? AND MONTH(waktu_masuk) = ? AND YEAR(waktu_masuk) = ?";

            try (PreparedStatement stmt = con.prepareStatement(query)) {
                stmt.setString(1, norfid);
                stmt.setInt(2, month);
                stmt.setInt(3, year);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        total = rs.getInt("total");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return total;
    }

    private Map<String, Map<String, String>> getAttendanceData(String norfid, Date[] dates) {
        Map<String, Map<String, String>> attendanceData = new HashMap<>();

        try {
            // Get today's date for comparison
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);
            Date todayDate = today.getTime();

            for (Date date : dates) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                Date currentDate = cal.getTime();

                // Is this date in the past, today, or in the future?
                boolean isPast = currentDate.before(todayDate);
                boolean isToday = currentDate.equals(todayDate);
                boolean isFuture = currentDate.after(todayDate);

                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                Date startDate = cal.getTime();

                cal.add(Calendar.DAY_OF_MONTH, 1);
                Date endDate = cal.getTime();

                String dateKey = dateFormat.format(date);
                Map<String, String> dayData = new HashMap<>();
                dayData.put("masuk_time", "");
                dayData.put("masuk_status", "");
                dayData.put("keluar_time", "");
                dayData.put("keluar_status", "");

                // For future dates, we set empty values (will display as "-")
                if (isFuture) {
                    attendanceData.put(dateKey, dayData);
                    continue;
                }

                String query = "SELECT waktu_masuk, waktu_keluar "
                        + "FROM absensi "
                        + "WHERE norfid = ? AND waktu_masuk >= ? AND waktu_masuk < ?";

                try (PreparedStatement stmt = con.prepareStatement(query)) {
                    stmt.setString(1, norfid);
                    stmt.setString(2, dateTimeFormat.format(startDate));
                    stmt.setString(3, dateTimeFormat.format(endDate));

                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            // Get check-in time and determine status
                            Timestamp masukTime = rs.getTimestamp("waktu_masuk");
                            if (masukTime != null) {
                                dayData.put("masuk_time", timeFormat.format(masukTime));

                                // Determine check-in status (on time: 07:00-08:30, late: after 08:30)
                                cal.setTime(masukTime);
                                int hour = cal.get(Calendar.HOUR_OF_DAY);
                                int minute = cal.get(Calendar.MINUTE);

                                if ((hour == 7) || (hour == 8 && minute <= 30)) {
                                    dayData.put("masuk_status", "green"); // On time
                                } else {
                                    dayData.put("masuk_status", "yellow"); // Late
                                }
                            }

                            // Get check-out time and determine status
                            Timestamp keluarTime = rs.getTimestamp("waktu_keluar");
                            if (keluarTime != null && !keluarTime.toString().equals("0000-00-00 00:00:00")) {
                                dayData.put("keluar_time", timeFormat.format(keluarTime));

                                // Determine check-out status (on time: after 15:00, early: before 15:00)
                                cal.setTime(keluarTime);
                                int hour = cal.get(Calendar.HOUR_OF_DAY);

                                if (hour >= 16) {
                                    dayData.put("keluar_status", "green"); // On time
                                } else {
                                    dayData.put("keluar_status", "yellow"); // Early
                                }
                            }
                        } else {
                            // No record for this day
                            if (isPast) {
                                // Past days with no attendance marked as absent (red)
                                dayData.put("masuk_status", "red");
                                dayData.put("keluar_status", "red");
                            }
                            // Current day with no attendance yet or future days stay empty
                        }
                    }
                }

                attendanceData.put(dateKey, dayData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return attendanceData;
    }

    private String getEmployeeRfid(int row) {
        // First try to get the RFID directly from the table model
        DefaultTableModel model = (DefaultTableModel) mainTable.getModel();
        if (row >= 0 && row < model.getRowCount()) {
            String employeeName = (String) model.getValueAt(row, 1); // Name is in column 1

            try {
                // Get RFID based on employee name
                String query = "SELECT norfid FROM user WHERE nama_user = ?";
                try (PreparedStatement stmt = con.prepareStatement(query)) {
                    stmt.setString(1, employeeName);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            return rs.getString("norfid");
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private void performSearchh(String searchTerm) {
        this.currentSearchTerm = searchTerm;
        try {
            String query = "SELECT norfid, nama_user, jabatan "
                    + "FROM user WHERE nama_user LIKE ? AND jabatan != 'owner' AND status != 'nonaktif' "
                    + "ORDER BY nama_user";
            try (PreparedStatement stmt = con.prepareStatement(query)) {
                stmt.setString(1, "%" + searchTerm + "%");
                ResultSet rs = stmt.executeQuery();
                DefaultTableModel model = (DefaultTableModel) mainTable.getModel();
                model.setRowCount(0); // Clear existing data
                int rowNum = 1;
                while (rs.next()) {
                    String norfid = rs.getString("norfid");
                    String name = rs.getString("nama_user");
                    String position = rs.getString("jabatan");

                    // Use the current selectedMonth and selectedYear for filtering
                    int totalAttendance = getTotalAttendanceDays(norfid, selectedMonth, selectedYear);

                    // Create a new row for this employee
                    Object[] row = new Object[model.getColumnCount()];
                    row[0] = rowNum + ".";
                    row[1] = name;
                    row[2] = position != null ? position : "";
                    row[6] = totalAttendance > 0 ? String.valueOf(totalAttendance) : "";
                    model.addRow(row);
                    rowNum++;
                }
                // This is the key fix - properly refresh the table including custom renderers
                mainTable.tableChanged(new TableModelEvent(model));
                mainTable.revalidate(); // Ensure layout is recalculated
                mainTable.repaint();    // Repaint the UI
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void clearSearch() {
        searchField.setText("Search");
        currentSearchTerm = "";
        loadDataFromDatabase();
    }

    private void setCustomRenderers() {
        // Custom header renderer with increased height
        TableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel header = new JLabel(value.toString(), JLabel.CENTER);
                header.setOpaque(true);

                // Use light gray background for all headers
                header.setBackground(new Color(240, 240, 240));
                header.setForeground(Color.BLACK);
                header.setFont(header.getFont().deriveFont(Font.BOLD));

                // Add more vertical padding to make the header taller
                header.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(1, 0, 1, 0, Color.LIGHT_GRAY),
                        BorderFactory.createEmptyBorder(10, 0, 10, 0)
                ));

                // Add special cursor and visual cue for navigation columns
                if (column == 3 || column == 5) {
                    header.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    // Make the navigation headers more noticeable
                    header.setForeground(new Color(52, 73, 94));
                }

                return header;
            }
        };

        // Set header renderer for all columns
        for (int i = 0; i < mainTable.getColumnCount(); i++) {
            TableColumn column = mainTable.getColumnModel().getColumn(i);
            column.setHeaderRenderer(headerRenderer);
        }

        // Create a custom cell renderer for all columns
        mainTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel cell = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                cell.setForeground(Color.black);

                cell.setOpaque(true);
                cell.setBackground(Color.WHITE);

                // Ensure vertical centering for all cells
                cell.setVerticalAlignment(JLabel.CENTER);

                // Standard border for all cells
                cell.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

                // Specific alignment and styling for each column
                if (column == 0) { // No.
                    cell.setHorizontalAlignment(JLabel.CENTER);
                } else if (column == 1) { // Nama Karyawan
                    cell.setHorizontalAlignment(JLabel.LEFT);
                    // Add padding for text
                    cell.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                            BorderFactory.createEmptyBorder(0, 10, 0, 0)
                    ));
                } else if (column == 2) { // Posisi
                    cell.setHorizontalAlignment(JLabel.CENTER);
                } else if (column == 3) { // «
                    cell.setHorizontalAlignment(JLabel.CENTER);
                    cell.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    cell.setBorder(BorderFactory.createEmptyBorder());
                } else if (column == 5) { // »
                    cell.setHorizontalAlignment(JLabel.CENTER);
                    cell.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    cell.setBorder(BorderFactory.createEmptyBorder());
                } else if (column == 6) { // Total
                    cell.setHorizontalAlignment(JLabel.CENTER);
                } else if (column == 4) { // Tanggal column
                    // Replace with a custom component for dates
                    return createDatePanel(row);
                }

                return cell;
            }
        });
    }

    private JPanel createDatePanel(int row) {
        // Create the main date panel
        JPanel datePanel = new JPanel(new GridLayout(1, 3));
        datePanel.setBackground(Color.WHITE);
        datePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        // Fix the panel height to match the row height
        datePanel.setPreferredSize(new Dimension(datePanel.getPreferredSize().width, mainTable.getRowHeight()));

        // Create a temporary calendar to generate date labels for the 3 days
        Calendar tempCal = (Calendar) currentDate.clone();
        Date[] dates = new Date[3];
        String[] dateLabels = new String[3];

        for (int i = 0; i < 3; i++) {
            dates[i] = tempCal.getTime();
            dateLabels[i] = dateFormat.format(tempCal.getTime());
            tempCal.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Get employee's RFID for this row to fetch attendance data
        String norfid = getEmployeeRfid(row);

        // Fetch attendance data for this employee on these dates
        Map<String, Map<String, String>> attendanceData = new HashMap<>();
        if (!norfid.isEmpty()) {
            attendanceData = getAttendanceData(norfid, dates);
        }

        // Get today's date for comparison
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        Date todayDate = today.getTime();

        // For data rows (actual employee data)
        if (row >= 0 && !norfid.isEmpty()) {
            // Create cells with actual data
            for (int day = 0; day < 3; day++) {
                JPanel dayColumn = new JPanel(new BorderLayout());
                dayColumn.setBackground(Color.WHITE);
                dayColumn.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, Color.LIGHT_GRAY));

                // Add date label at top
                JLabel dateLabel = new JLabel(dateLabels[day], JLabel.CENTER);
                dateLabel.setFont(dateLabel.getFont().deriveFont(Font.BOLD, 10));
                dateLabel.setForeground(Color.DARK_GRAY);
                dateLabel.setBackground(new Color(240, 240, 240));
                dateLabel.setOpaque(true);
                dateLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
                dayColumn.add(dateLabel, BorderLayout.NORTH);

                // Create panel for check-in/check-out times
                JPanel timePanel = new JPanel(new GridLayout(1, 2));
                timePanel.setBackground(Color.WHITE);

                // Get attendance data for this date
                String dateKey = dateLabels[day];
                Map<String, String> dayData = attendanceData.getOrDefault(dateKey, new HashMap<>());

                String masukTime = dayData.getOrDefault("masuk_time", "");
                String masukStatus = dayData.getOrDefault("masuk_status", "");
                String keluarTime = dayData.getOrDefault("keluar_time", "");
                String keluarStatus = dayData.getOrDefault("keluar_status", "");

                // Check if this date is today, past, or future
                Calendar dateCalendar = Calendar.getInstance();
                try {
                    dateCalendar.setTime(dateFormat.parse(dateKey));
                    dateCalendar.set(Calendar.HOUR_OF_DAY, 0);
                    dateCalendar.set(Calendar.MINUTE, 0);
                    dateCalendar.set(Calendar.SECOND, 0);
                    dateCalendar.set(Calendar.MILLISECOND, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Date currentDate = dateCalendar.getTime();
                boolean isPast = currentDate.before(todayDate);
                boolean isToday = currentDate.equals(todayDate);
                boolean isFuture = currentDate.after(todayDate);

                // Masuk (check-in) cell
                JPanel masukPanel = new JPanel(new BorderLayout());
                masukPanel.setBackground(Color.WHITE);
                masukPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));

                JPanel masukContent = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
                masukContent.setBackground(Color.WHITE);

                JLabel masukHeaderLabel = new JLabel("Masuk", JLabel.CENTER);
                masukHeaderLabel.setFont(masukHeaderLabel.getFont().deriveFont(Font.PLAIN, 9));
                masukHeaderLabel.setForeground(Color.GRAY);

                // Time label
                JLabel masukTimeLabel;
                if (masukStatus.equals("red")) {
                    // If absent (red status), don't show dash
                    masukTimeLabel = new JLabel("", JLabel.CENTER);
                } else {
                    // Otherwise show time or dash
                    masukTimeLabel = new JLabel(masukTime.isEmpty() ? "-" : masukTime, JLabel.CENTER);
                }

                // Status indicator
                JLabel statusDot = new JLabel("●");
                if (masukStatus.equals("green")) {
                    statusDot.setForeground(new Color(46, 204, 113)); // Green
                } else if (masukStatus.equals("yellow")) {
                    statusDot.setForeground(new Color(243, 156, 18)); // Yellow
                } else if (masukStatus.equals("red")) {
                    statusDot.setForeground(new Color(231, 76, 60)); // Red
                } else {
                    // Empty status - should be "-" for today or future
                    statusDot.setText("");
                }

                masukContent.add(masukTimeLabel);
                if (!masukStatus.isEmpty()) {
                    masukContent.add(statusDot);
                }

                masukPanel.add(masukHeaderLabel, BorderLayout.NORTH);
                masukPanel.add(masukContent, BorderLayout.CENTER);

                // Keluar (check-out) cell
                JPanel keluarPanel = new JPanel(new BorderLayout());
                keluarPanel.setBackground(Color.WHITE);

                JPanel keluarContent = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
                keluarContent.setBackground(Color.WHITE);

                // Add "Keluar" label
                JLabel keluarHeaderLabel = new JLabel("Keluar", JLabel.CENTER);
                keluarHeaderLabel.setFont(keluarHeaderLabel.getFont().deriveFont(Font.PLAIN, 9));
                keluarHeaderLabel.setForeground(Color.GRAY);

                // Time label
                JLabel keluarTimeLabel;
                if (keluarStatus.equals("red")) {
                    // If absent (red status), don't show dash
                    keluarTimeLabel = new JLabel("", JLabel.CENTER);
                } else {
                    // Otherwise show time or dash
                    keluarTimeLabel = new JLabel(keluarTime.isEmpty() ? "-" : keluarTime, JLabel.CENTER);
                }

                // Status indicator
                JLabel keluarStatusDot = new JLabel("●");
                if (keluarStatus.equals("green")) {
                    keluarStatusDot.setForeground(new Color(46, 204, 113)); // Green
                } else if (keluarStatus.equals("yellow")) {
                    keluarStatusDot.setForeground(new Color(243, 156, 18)); // Yellow
                } else if (keluarStatus.equals("red")) {
                    keluarStatusDot.setForeground(new Color(231, 76, 60)); // Red
                } else {
                    // Empty status - should be "-" for today or future
                    keluarStatusDot.setText("");
                }

                keluarContent.add(keluarTimeLabel);
                if (!keluarStatus.isEmpty()) {
                    keluarContent.add(keluarStatusDot);
                }

                keluarPanel.add(keluarHeaderLabel, BorderLayout.NORTH);
                keluarPanel.add(keluarContent, BorderLayout.CENTER);

                timePanel.add(masukPanel);
                timePanel.add(keluarPanel);
                dayColumn.add(timePanel, BorderLayout.CENTER);

                datePanel.add(dayColumn);
            }
        } else {
            // Handle empty rows - create empty cells with dates
            for (int day = 0; day < 3; day++) {
                JPanel dayColumn = new JPanel(new BorderLayout());
                dayColumn.setBackground(Color.WHITE);
                dayColumn.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, Color.LIGHT_GRAY));

                // Add date label
                JLabel dateLabel = new JLabel(dateLabels[day], JLabel.CENTER);
                dateLabel.setFont(dateLabel.getFont().deriveFont(Font.BOLD, 10));
                dateLabel.setForeground(Color.DARK_GRAY);
                dateLabel.setBackground(new Color(240, 240, 240));
                dateLabel.setOpaque(true);
                dateLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
                dayColumn.add(dateLabel, BorderLayout.NORTH);

                JPanel timePanel = new JPanel(new GridLayout(1, 2));
                timePanel.setBackground(Color.WHITE);

                JPanel masukPanel = new JPanel(new BorderLayout());
                masukPanel.setBackground(Color.WHITE);
                masukPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));

                JLabel masukHeaderLabel = new JLabel("Masuk", JLabel.CENTER);
                masukHeaderLabel.setFont(masukHeaderLabel.getFont().deriveFont(Font.PLAIN, 9));
                masukHeaderLabel.setForeground(Color.GRAY);
                masukPanel.add(masukHeaderLabel, BorderLayout.NORTH);
                masukPanel.add(new JLabel("-", JLabel.CENTER), BorderLayout.CENTER);

                JPanel keluarPanel = new JPanel(new BorderLayout());
                keluarPanel.setBackground(Color.WHITE);

                JLabel keluarHeaderLabel = new JLabel("Keluar", JLabel.CENTER);
                keluarHeaderLabel.setFont(keluarHeaderLabel.getFont().deriveFont(Font.PLAIN, 9));
                keluarHeaderLabel.setForeground(Color.GRAY);
                keluarPanel.add(keluarHeaderLabel, BorderLayout.NORTH);
                keluarPanel.add(new JLabel("-", JLabel.CENTER), BorderLayout.CENTER);

                timePanel.add(masukPanel);
                timePanel.add(keluarPanel);
                dayColumn.add(timePanel, BorderLayout.CENTER);

                datePanel.add(dayColumn);
            }
        }

        return datePanel;
    }

    private JPanel createLegendPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setOpaque(false);

        // On time legend
        JPanel onTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        onTimePanel.setOpaque(false);
        JLabel onTimeDot = new JLabel("●");
        onTimeDot.setForeground(new Color(46, 204, 113)); // Green
        onTimePanel.add(onTimeDot);
        onTimePanel.add(new JLabel("Tepat waktu"));
        panel.add(onTimePanel);

        // Late legend
        JPanel latePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        latePanel.setOpaque(false);
        JLabel lateDot = new JLabel("●");
        lateDot.setForeground(new Color(243, 156, 18)); // Yellow/Orange
        latePanel.add(lateDot);
        latePanel.add(new JLabel("Terlambat/Terlalu cepat"));
        panel.add(latePanel);

        // Absent legend
        JPanel absentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        absentPanel.setOpaque(false);
        JLabel absentDot = new JLabel("●");
        absentDot.setForeground(new Color(231, 76, 60)); // Red
        absentPanel.add(absentDot);
        absentPanel.add(new JLabel("Tidak Hadir"));
        panel.add(absentPanel);

        return panel;
    }

    public void setBackToDataKaryawan(Runnable listener) {
        this.backToDataKaryawan = listener;
    }

    public void resetToCurrentDate() {
        // Reset currentDate to current date (today)
        currentDate = Calendar.getInstance();
        currentDate.add(Calendar.DAY_OF_MONTH, -1);

        // Update month field if it exists
        if (configButton != null && configButton.getParent() != null) {
            JTextField monthField = (JTextField) ((JPanel) configButton.getParent()).getComponent(0);
            SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", indonesianLocale);
            monthField.setText(monthYearFormat.format(currentDate.getTime()).toUpperCase());
        }

        // Refresh the table data
        loadDataFromDatabase();
    }

    public void updateTableByMonth(int month, int year) {
        // Update the selected month and year
        this.selectedMonth = month;
        this.selectedYear = year;

        // Update currentDate ke tanggal 1 bulan yang dipilih
        currentDate.set(Calendar.YEAR, year);
        currentDate.set(Calendar.MONTH, month - 1);
        currentDate.set(Calendar.DAY_OF_MONTH, 1);

        // Update the month field display
        updateMonthFieldDisplay();

        // Reload data - preserve current search if any
        if (!currentSearchTerm.isEmpty()) {
            performSearchh(currentSearchTerm);
        } else {
            loadDataFromDatabase();
        }
    }

    private void updateMonthFieldDisplay() {
        if (monthField != null) {
            SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", indonesianLocale);
            monthField.setText(monthYearFormat.format(currentDate.getTime()).toUpperCase());
        } else {
            // If monthField isn't directly accessible, try to find it
            JTextField monthField = (JTextField) ((JPanel) configButton.getParent()).getComponent(0);
            if (monthField != null) {
                SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", indonesianLocale);
                monthField.setText(monthYearFormat.format(currentDate.getTime()).toUpperCase());
            }
        }
    }

    private void updateSelectedMonthYearFromCurrentDate() {
        // Calendar months are 0-based, database months are 1-based
        selectedMonth = currentDate.get(Calendar.MONTH) + 1;
        selectedYear = currentDate.get(Calendar.YEAR);

        updateMonthFieldDisplay();
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

    private void initializeCurrentMonthView() {
        // Set calendar ke bulan dan tahun saat ini
        Calendar cal = Calendar.getInstance();
        this.selectedMonth = cal.get(Calendar.MONTH) + 1;
        this.selectedYear = cal.get(Calendar.YEAR);

        // Update currentDate ke tanggal 1 bulan ini
        currentDate.set(Calendar.YEAR, selectedYear);
        currentDate.set(Calendar.MONTH, selectedMonth - 1);
        currentDate.set(Calendar.DAY_OF_MONTH, 1);

        // Update tampilan bulan
        updateMonthFieldDisplay();

        // Load data untuk bulan ini
        loadDataFromDatabase();
    }
}
