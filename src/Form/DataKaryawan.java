package Form;

import SourceCode.RoundedButton;
import SourceCode.RoundedBorder;
import SourceCode.ScrollPane;
import SourceCode.JTableRounded;
import SourceCode.TambahKaryawan;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import db.conn;
import java.sql.*;

public class DataKaryawan extends JPanel {

    private Runnable setAbsenKaryawan;
    private Runnable setGajiKaryawan;
    private JTextField searchField;
    private JTableRounded employeeTable;
    private JButton dataAbsenButton, kelolaGajiButton, tambahKaryawanButton;
    private JPanel thisPanel;
    private DefaultTableModel tableModel;
    private java.util.List<Object[]> employeeData;

    private Connection con;

    public DataKaryawan() {
        thisPanel = this;

        // Set a rounded border for the entire panel using your RoundedBorder class
        setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, new Color(0, 0, 0), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        con = conn.getConnection();

        initComponents();
        getData();
        setupSearchFunction();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // Title Panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Data Karyawan");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.WEST);

        // Top Panel containing Search and Action Buttons
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(Color.WHITE);

        // Search Panel
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(Color.WHITE);
        searchField = new JTextField("Search");
        searchField.setForeground(Color.BLACK);
        searchField.setBackground(Color.WHITE);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(467, 35));

        searchField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, Color.BLACK, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        searchPanel.add(searchField, BorderLayout.WEST);

        searchField.addFocusListener(new FocusAdapter() {
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

        // Button Panel (for action buttons)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setBackground(Color.white);

        // DATA ABSEN button - dark gray with white text
        dataAbsenButton = new JButton("DATA ABSEN");
        dataAbsenButton.setPreferredSize(new Dimension(123, 40));
        dataAbsenButton.setBackground(new Color(52, 61, 70));
        dataAbsenButton.setForeground(Color.WHITE);
        dataAbsenButton.setFocusPainted(false);
        dataAbsenButton.setBorderPainted(false);
        dataAbsenButton.setBorder(new RoundedBorder(5, Color.BLACK, 1));
        dataAbsenButton.setContentAreaFilled(false);
        dataAbsenButton.setOpaque(false);
        dataAbsenButton.setFont(new Font("Arial", Font.BOLD, 12));

        dataAbsenButton.addActionListener(e -> {
            // Panggil callback untuk mengganti panel
            if (setAbsenKaryawan != null) {
                setAbsenKaryawan.run();
            }
        });

        // KELOLA GAJI button - white with black border
        kelolaGajiButton = new JButton("KELOLA GAJI");
        kelolaGajiButton.setPreferredSize(new Dimension(123, 40));
        kelolaGajiButton.setBackground(Color.WHITE);
        kelolaGajiButton.setForeground(Color.BLACK);
        kelolaGajiButton.setFocusPainted(false);
        kelolaGajiButton.setBorderPainted(false);
        kelolaGajiButton.setBorder(new RoundedBorder(5, Color.BLACK, 1));
        kelolaGajiButton.setContentAreaFilled(false);
        kelolaGajiButton.setOpaque(false);
        kelolaGajiButton.setFont(new Font("Arial", Font.BOLD, 12));
        
        kelolaGajiButton.addActionListener(e -> {
            // Panggil callback untuk mengganti panel
            if (setGajiKaryawan != null) {
                setGajiKaryawan.run();
            }
        });

        // TAMBAH KARYAWAN button - dark gray with white text and user icon
        tambahKaryawanButton = new JButton("TAMBAH KARYAWAN");
        tambahKaryawanButton.setPreferredSize(new Dimension(200, 40));
        tambahKaryawanButton.setBackground(new Color(52, 61, 70));
        tambahKaryawanButton.setForeground(Color.WHITE);
        tambahKaryawanButton.setFocusPainted(false);
        tambahKaryawanButton.setBorderPainted(false);
        tambahKaryawanButton.setBorder(new RoundedBorder(5, Color.BLACK, 1));
        tambahKaryawanButton.setContentAreaFilled(false);
        tambahKaryawanButton.setOpaque(false);
        tambahKaryawanButton.setFont(new Font("Arial", Font.BOLD, 12));

        tambahKaryawanButton.addActionListener(e -> {
            // Dapatkan parent frame secara dinamis
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            Frame parentFrame = parentWindow instanceof Frame ? (Frame) parentWindow : null;

            TambahKaryawan tambahKaryawanDialog = new TambahKaryawan(parentFrame, true);
            tambahKaryawanDialog.setVisible(true);

            if (tambahKaryawanDialog.wasDataAdded) {
                getData();  // Refresh table data
            }
        });

        // Add user icon to TAMBAH KARYAWAN button
        try {
            ImageIcon userIcon = new ImageIcon(getClass().getResource("/SourceImage/add-icon.png"));
            tambahKaryawanButton.setIcon(userIcon);
            tambahKaryawanButton.setIconTextGap(10);
        } catch (Exception ex) {
            tambahKaryawanButton.setText("TAMBAH KARYAWAN ⊕");
        }

        dataAbsenButton.setUI(new RoundedButton());
        kelolaGajiButton.setUI(new RoundedButton());
        tambahKaryawanButton.setUI(new RoundedButton());

        dataAbsenButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dataAbsenButton.setLocation(dataAbsenButton.getX(), dataAbsenButton.getY() + 1);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dataAbsenButton.setLocation(dataAbsenButton.getX(), dataAbsenButton.getY() - 1);
            }
        });

        kelolaGajiButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                kelolaGajiButton.setLocation(kelolaGajiButton.getX(), kelolaGajiButton.getY() + 1);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                kelolaGajiButton.setLocation(kelolaGajiButton.getX(), kelolaGajiButton.getY() - 1);
            }
        });

        tambahKaryawanButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                tambahKaryawanButton.setLocation(tambahKaryawanButton.getX(), tambahKaryawanButton.getY() + 1);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                tambahKaryawanButton.setLocation(tambahKaryawanButton.getX(), tambahKaryawanButton.getY() - 1);
            }
        });

        buttonPanel.add(dataAbsenButton);
        buttonPanel.add(kelolaGajiButton);
        buttonPanel.add(tambahKaryawanButton);

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // Combine title and top panel
        JPanel headerPanel = new JPanel(new BorderLayout(0, 10));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.add(titlePanel, BorderLayout.NORTH);
        headerPanel.add(topPanel, BorderLayout.CENTER);

        // Replace the existing table implementation with JTableRounded
        String[] columnNames = {"No", "NO RFID", "Nama Karyawan", "Email", "Password", "No hp", "Alamat", "Aksi"};

        // Create the JTableRounded
        employeeTable = new JTableRounded(columnNames, 1027, 450);

        // Set column widths - making them more proportional
        int[] columnWidths = {40, 150, 150, 200, 120, 140, 140, 70};
        for (int i = 0; i < employeeTable.getTable().getColumnCount(); i++) {
            employeeTable.setColumnWidth(i, columnWidths[i]);
        }

        // Get the JTable from JTableRounded
        JTable table = employeeTable.getTable();

        // Set table to fill the viewport height
        table.setFillsViewportHeight(true);

        tableModel = (DefaultTableModel) table.getModel();
        tableModel.setRowCount(0); // Clear any existing rows
        table.setRowHeight(50);

        if (employeeData != null && !employeeData.isEmpty()) {
            for (Object[] row : employeeData) {
                tableModel.addRow(row);
            }
            employeeData = null; // Clear the temporary data
        }

        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setThumbColor(new Color(80, 80, 80, 180));
        scrollPane.setTrackColor(new Color(240, 240, 240, 80));
        scrollPane.setThumbThickness(8);
        scrollPane.setThumbRadius(8);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // Create a panel to hold the scroll pane with proper padding
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Add some padding
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Add the table panel to the main panel's CENTER region
        add(tablePanel, BorderLayout.CENTER);

        // Set custom cell renderer for the action column (with improved styling)
        table.getColumnModel().getColumn(7).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                // Create a transparent panel with improved layout
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 10));
                panel.setPreferredSize(new Dimension(80, 30));
                panel.setOpaque(false);
                panel.setBackground(new Color(0, 0, 0, 0));

                // Load and scale icons for better appearance
                ImageIcon originalIconEdit = new ImageIcon(getClass().getResource("/SourceImage/edit_icon.png"));
                ImageIcon originalIconDelete = new ImageIcon(getClass().getResource("/SourceImage/delete-icon.png"));

                // Check if the images were loaded successfully, if not, try alternative paths
                if (originalIconEdit.getIconWidth() == -1) {
                    originalIconEdit = new ImageIcon(getClass().getResource("/SourceImage/edit-icon.png"));
                }
                if (originalIconDelete.getIconWidth() == -1) {
                    originalIconDelete = new ImageIcon(getClass().getResource("/SourceImage/delete_icon.png"));
                }

                Image scaledImageEdit = originalIconEdit.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
                Image scaledImageDelete = originalIconDelete.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);

                ImageIcon scaledIconEdit = new ImageIcon(scaledImageEdit);
                ImageIcon scaledIconDelete = new ImageIcon(scaledImageDelete);

                // Edit button with custom rounded appearance
                JButton btnEdit = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(255, 153, 0)); // Orange color
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
                btnEdit.setBackground(new Color(255, 153, 0));

                // Delete button with custom rounded appearance
                JButton btnDelete = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(231, 76, 60)); // Red color
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
                btnDelete.setBackground(new Color(231, 76, 60));

                // Add both buttons to the panel
                panel.add(btnEdit);
                panel.add(btnDelete);
                return panel;
            }
        });

        // Set custom cell editor for the action column
        table.getColumnModel().getColumn(7).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            private JPanel panel;
            private JButton btnEdit;
            private JButton btnDelete;
            private int currentRow;
            private boolean isPushed = false;

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                currentRow = row;
                isPushed = true;

                // Create a transparent panel with improved layout
                panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 10)) {
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

                // Load and scale icons
                ImageIcon originalIconEdit = new ImageIcon(getClass().getResource("/SourceImage/edit_icon.png"));
                ImageIcon originalIconDelete = new ImageIcon(getClass().getResource("/SourceImage/delete-icon.png"));

                // Check if the images were loaded successfully, if not, try alternative paths
                if (originalIconEdit.getIconWidth() == -1) {
                    originalIconEdit = new ImageIcon(getClass().getResource("/SourceImage/edit-icon.png"));
                }
                if (originalIconDelete.getIconWidth() == -1) {
                    originalIconDelete = new ImageIcon(getClass().getResource("/SourceImage/delete_icon.png"));
                }

                Image scaledImageEdit = originalIconEdit.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
                Image scaledImageDelete = originalIconDelete.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);

                ImageIcon scaledIconEdit = new ImageIcon(scaledImageEdit);
                ImageIcon scaledIconDelete = new ImageIcon(scaledImageDelete);

                // Edit button with custom appearance and behavior
                btnEdit = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(255, 153, 0)); // Orange color
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                        super.paintComponent(g2);
                        g2.dispose();
                    }

                    @Override
                    public void setPressedIcon(Icon icon) {
                        // Do nothing - keep the same appearance
                    }

                    @Override
                    public void updateUI() {
                        super.updateUI();
                        setContentAreaFilled(false);
                        setBorderPainted(false);
                        setFocusPainted(false);
                    }

                    @Override
                    public boolean isFocusable() {
                        return false;
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
                btnEdit.setName("edit");
                btnEdit.setBackground(new Color(255, 153, 0));

                // Action for Edit button
                btnEdit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        stopCellEditing();
                        System.out.println("ini button edit");
                    }
                });

                // Delete button with custom appearance and behavior
                btnDelete = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(231, 76, 60)); // Red color
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                        super.paintComponent(g2);
                        g2.dispose();
                    }

                    @Override
                    public void setPressedIcon(Icon icon) {
                        // Do nothing - keep the same appearance
                    }

                    @Override
                    public void updateUI() {
                        super.updateUI();
                        setContentAreaFilled(false);
                        setBorderPainted(false);
                        setFocusPainted(false);
                    }

                    @Override
                    public boolean isFocusable() {
                        return false;
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
                btnDelete.setName("delete");
                btnDelete.setBackground(new Color(231, 76, 60));

                // Action for Delete button
                btnDelete.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        stopCellEditing();
                        System.out.println("ini button delete");
                    }
                });

                // Add both buttons to the panel
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

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                if (col == 7) { // Kolom aksi (indeks 7)
                    // Hitung posisi relatif dalam sel
                    Rectangle cellRect = table.getCellRect(row, col, false);
                    int relativeX = e.getX() - cellRect.x;

                    if (relativeX <= 30) { // Tombol edit (lebar 30px)
                        System.out.println("ini tombol edit");
                        // Tambahkan kode untuk menangani klik tombol edit
                    } else if (relativeX > 32 && relativeX <= 62) { // Tombol delete (lebar 30px)
                        System.out.println("ini tombol delete");
                        // Tambahkan kode untuk menangani klik tombol delete
                    }
                }
            }
        });

        // Add all panels to main panel
        add(headerPanel, BorderLayout.NORTH);
    }

    // Method untuk menetapkan listener tombol plus
    public void setAbsenKaryawan(Runnable listener) {
        this.setAbsenKaryawan = listener;
    }
    public void setGajiKaryawan(Runnable listener) {
        this.setGajiKaryawan = listener;
    }

    public void getData() {
        // Clear existing table data if table model exists
        if (tableModel != null) {
            tableModel.setRowCount(0);
        }

        try {
            String query = "SELECT * FROM user ORDER BY norfid";
            try (PreparedStatement st = con.prepareStatement(query)) {

                int rowNumber = 1; // For numbering the rows

                // Create a list to store the data if tableModel isn't initialized yet
                java.util.List<Object[]> dataList = new java.util.ArrayList<>();
                ResultSet rs = st.executeQuery();

                while (rs.next()) {
                    // Retrieve data from result set
                    String rfid = rs.getString("norfid");
                    String nama = rs.getString("nama_user");
                    String email = rs.getString("email");
                    String pw = rs.getString("password");
                    String nohp = rs.getString("no_hp");
                    String alamat = rs.getString("alamat");

                    // Create a row array
                    Object[] row = {
                        String.valueOf(rowNumber),
                        rfid,
                        nama,
                        email,
                        pw,
                        nohp,
                        alamat,
                        "" // Empty string for action column
                    };

                    // Add to table model if it exists, otherwise add to our temporary list
                    if (tableModel != null) {
                        tableModel.addRow(row);
                    } else {
                        dataList.add(row);
                    }

                    rowNumber++;
                }

                // Store the data list as a class field to use later if tableModel was null
                if (tableModel == null && !dataList.isEmpty()) {
                    this.employeeData = dataList;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving employee data: " + e.getMessage());
            e.printStackTrace();

            // Show an error dialog
            JOptionPane.showMessageDialog(this,
                    "Failed to retrieve employee data from database: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupSearchFunction() {
        // Add key listener to search field
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                performSearch();
            }
        });

        // Also handle the case when user clicks the search field and clears "Search" text
        searchField.addActionListener(e -> performSearch());
    }

    private void performSearch() {
        String searchText = searchField.getText();
        if (searchText.equals("search")) {
            return; // Don't search for the placeholder text
        }

        // Clear existing table data
        tableModel.setRowCount(0);

        try {
            // Create a query that searches across multiple columns
            String query = "SELECT * FROM user WHERE "
                    + "norfid LIKE ? OR "
                    + "nama_user LIKE ?";

            try (PreparedStatement st = con.prepareStatement(query)) {
                // Set all parameters to the same search value
                String searchPattern = "%" + searchText + "%";
                for (int i = 1; i <= 2; i++) {
                    st.setString(i, searchPattern);
                }

                ResultSet rs = st.executeQuery();

                int rowNumber = 1; // For numbering the rows

                while (rs.next()) {
                    // Retrieve data from result set
                    String rfid = rs.getString("norfid");
                    String nama = rs.getString("nama_user");
                    String email = rs.getString("email");
                    String pw = rs.getString("password");
                    String nohp = rs.getString("no_hp");
                    String alamat = rs.getString("alamat");

                    // Create a row array
                    Object[] row = {
                        String.valueOf(rowNumber),
                        rfid,
                        nama,
                        email,
                        pw,
                        nohp,
                        alamat,
                        "" // Empty string for action column
                    };

                    // Add to table model
                    tableModel.addRow(row);
                    rowNumber++;
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error searching employee data: " + ex.getMessage());
            ex.printStackTrace();

            // Show an error dialog
            JOptionPane.showMessageDialog(thisPanel,
                    "Failed to search employee data: " + ex.getMessage(),
                    "Search Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshTable() {
        searchField.setText("Search");
        getData();
    }

}
