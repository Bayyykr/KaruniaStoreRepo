package Form;

import SourceCode.RoundedBorder;
import SourceCode.RoundedButton;
import SourceCode.ScrollPane;
import SourceCode.JTableRounded;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

public class GajiKaryawan extends JPanel {

    private JTextField searchField;
    private JTableRounded salaryTable;
    private JButton aturJadwalGajiButton, kembaliButton;
    private DefaultTableModel tableModel;

    public GajiKaryawan() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // Top Panel with Search and Buttons
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Gaji Karyawan");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
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

        // ATUR JADWAL GAJI button
        aturJadwalGajiButton = new JButton("ATUR JADWAL GAJI");
        aturJadwalGajiButton.setPreferredSize(new Dimension(150, 35));
        aturJadwalGajiButton.setBackground(new Color(52, 61, 70));
        aturJadwalGajiButton.setForeground(Color.WHITE);
        aturJadwalGajiButton.setFocusPainted(false);
        aturJadwalGajiButton.setBorderPainted(false);
        aturJadwalGajiButton.setBorder(new RoundedBorder(5, Color.BLACK, 1));
        aturJadwalGajiButton.setContentAreaFilled(false);
        aturJadwalGajiButton.setOpaque(false);
        aturJadwalGajiButton.setFont(new Font("Arial", Font.BOLD, 12));
        aturJadwalGajiButton.setUI(new RoundedButton());

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

        buttonPanel.add(aturJadwalGajiButton);
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

        // Set table to fill the viewport height
        table.setFillsViewportHeight(true);

        tableModel = (DefaultTableModel) table.getModel();
        tableModel.setRowCount(0); // Clear any existing rows
        table.setRowHeight(50);
        table.setDefaultEditor(Object.class, null);

        // Add sample salary data
        Object[][] data = {
            {"1", "02020121", "Haikal Zayne", "Manager", "Belum Dibayar", ""},
            {"2", "20281272", "Greyson", "Karyawan", "Dibayar", ""}
        };

        for (Object[] row : data) {
            tableModel.addRow(row);
        }

        // Customize table columns
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);  // No
        columnModel.getColumn(1).setPreferredWidth(100); // NO RFID
        columnModel.getColumn(2).setPreferredWidth(200); // Nama Karyawan
        columnModel.getColumn(3).setPreferredWidth(150); // Jabatan
        columnModel.getColumn(4).setPreferredWidth(150); // Status Gaji
        columnModel.getColumn(5).setPreferredWidth(100); // Aksi

        columnModel.getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus,
                    int row, int column) {
                JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
                actionPanel.setBackground(Color.WHITE);

                // Lock button
                JButton lockButton = new JButton();
                lockButton.setPreferredSize(new Dimension(50, 30));
                lockButton.setBackground(new Color(40, 199, 111));
                lockButton.setForeground(Color.WHITE);
                lockButton.setBorderPainted(false);
                lockButton.setFocusPainted(false);
                lockButton.setContentAreaFilled(true);
                lockButton.setFont(new Font("Arial", Font.PLAIN, 12));
                lockButton.setUI(new RoundedButton());
                lockButton.setOpaque(false);
                lockButton.setContentAreaFilled(true);

                // Tambahkan action listener di sini
                lockButton.addActionListener(e -> {
                    System.out.println("Bayar untuk baris: " + row);
                    // Tambahkan logika pembayaran di sini
                    JOptionPane.showMessageDialog(null, "Membayar gaji untuk baris " + (row + 1));
                });

                try {
                    ImageIcon icon = new ImageIcon(getClass().getResource("../SourceImage/icon/bayar-icon.png"));
                    Image scaledImage = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                    lockButton.setIcon(new ImageIcon(scaledImage));
                } catch (Exception e) {
                    lockButton.setText("Bayar");
                }

                // Trash button
                JButton trashButton = new JButton();
                trashButton.setPreferredSize(new Dimension(50, 30));
                trashButton.setBackground(new Color(255, 59, 59));
                trashButton.setForeground(Color.WHITE);
                trashButton.setBorderPainted(false);
                trashButton.setFocusPainted(false);
                trashButton.setContentAreaFilled(true);
                trashButton.setFont(new Font("Arial", Font.PLAIN, 12));
                trashButton.setUI(new RoundedButton());
                trashButton.setOpaque(false);
                trashButton.setContentAreaFilled(true);

                // Tambahkan action listener untuk tombol trash
                trashButton.addActionListener(e -> {
                    System.out.println("Hapus untuk baris: " + row);
                    // Tambahkan logika penghapusan di sini
                    int konfirmasi = JOptionPane.showConfirmDialog(null,
                            "Apakah Anda yakin ingin menghapus data gaji untuk baris " + (row + 1) + "?",
                            "Konfirmasi Hapus",
                            JOptionPane.YES_NO_OPTION);

                    if (konfirmasi == JOptionPane.YES_OPTION) {
                        // Logika penghapusan
                        System.out.println("Data dihapus");
                    }
                });

                try {
                    ImageIcon icon = new ImageIcon(getClass().getResource("../SourceImage/icon/icon_sampah_putih.png"));
                    Image scaledImage = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                    trashButton.setIcon(new ImageIcon(scaledImage));
                } catch (Exception e) {
                    trashButton.setText("Hapus");
                }

                actionPanel.add(lockButton);
                actionPanel.add(trashButton);

                return actionPanel;
            }
        });

// Hapus mouse listener sebelumnya
        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setThumbColor(new Color(80, 80, 80, 180));
        scrollPane.setTrackColor(new Color(240, 240, 240, 80));
        scrollPane.setThumbThickness(8);
        scrollPane.setThumbRadius(8);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Add components to main panel
        add(topPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
    }
}
