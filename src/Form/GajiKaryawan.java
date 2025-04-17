package Form;

import SourceCode.RoundedBorder;
import SourceCode.RoundedButton;
import SourceCode.ScrollPane;
import SourceCode.JTableRounded;
import SourceCode.PopUp_edittransbeli;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

public class GajiKaryawan extends JPanel {
    Component parentComponent = this;
    private JTextField searchField;
    private JTableRounded salaryTable;
    private JButton aturJadwalGajiButton, kembaliButton;
    private JLabel periodeLabel;
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

        // Periode Label
        periodeLabel = new JLabel("FEBRUARI / 2025");
        periodeLabel.setPreferredSize(new Dimension(130, 35));
        periodeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        periodeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        periodeLabel.setForeground(Color.BLACK);
        periodeLabel.setBackground(Color.WHITE);
        periodeLabel.setOpaque(false);
        periodeLabel.setBorder(new RoundedBorder(10, Color.BLACK, 1));

        // ATUR JADWAL GAJI button
        aturJadwalGajiButton = new JButton("ATUR BULAN DAN TAHUN");
        aturJadwalGajiButton.setPreferredSize(new Dimension(170, 35));
        aturJadwalGajiButton.setBackground(new Color(52, 61, 70));
        aturJadwalGajiButton.setForeground(Color.WHITE);
        aturJadwalGajiButton.setFocusPainted(false);
        aturJadwalGajiButton.setBorderPainted(false);
        aturJadwalGajiButton.setBorder(new RoundedBorder(5, Color.BLACK, 1));
        aturJadwalGajiButton.setContentAreaFilled(false);
        aturJadwalGajiButton.setOpaque(false);
        aturJadwalGajiButton.setFont(new Font("Arial", Font.BOLD, 12));
        aturJadwalGajiButton.setUI(new RoundedButton());
        aturJadwalGajiButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                aturJadwalGajiButton.setLocation(aturJadwalGajiButton.getX(), aturJadwalGajiButton.getY() + 2); // Tombol turun sedikit
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                aturJadwalGajiButton.setLocation(aturJadwalGajiButton.getX(), aturJadwalGajiButton.getY() - 2); // Kembali ke posisi semula
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
        
        // Konfigurasi warna baris tabel
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Kolom aksi dihandle oleh ButtonRenderer sendiri, jadi kita skip
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

        // Add sample salary data
        Object[][] data = {
            {"1", "02020121", "Haikal Zayne", "Manager", "Belum Dibayar", ""},
            {"2", "20281272", "Greyson", "Karyawan", "Dibayar", ""},
            {"2", "20281272", "Greyson", "Karyawan", "Dibayar", ""},
            {"2", "20281272", "Greyson", "Karyawan", "Dibayar", ""},
            {"2", "20281272", "Greyson", "Karyawan", "Dibayar", ""},
            {"2", "20281272", "Greyson", "Karyawan", "Dibayar", ""},
            {"2", "20281272", "Greyson", "Karyawan", "Dibayar", ""},
            {"2", "20281272", "Greyson", "Karyawan", "Dibayar", ""},
            {"2", "20281272", "Greyson", "Karyawan", "Dibayar", ""},
            {"2", "20281272", "Greyson", "Karyawan", "Dibayar", ""},
            {"2", "20281272", "Greyson", "Karyawan", "Dibayar", ""},
            {"2", "20281272", "Greyson", "Karyawan", "Dibayar", ""},
            {"2", "20281272", "Greyson", "Karyawan", "Dibayar", ""},
            {"2", "20281272", "Greyson", "Karyawan", "Dibayar", ""},
            {"2", "20281272", "Greyson", "Karyawan", "Dibayar", ""},
            {"2", "20281272", "Greyson", "Karyawan", "Dibayar", ""},
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

            // Buat tombol hapus
            hapusButton = new JButton();
            hapusButton.setPreferredSize(new Dimension(40, 30));
            hapusButton.setBackground(new Color(255, 59, 59));
            hapusButton.setForeground(Color.WHITE);
            hapusButton.setBorderPainted(false);
            hapusButton.setFocusPainted(false);
            hapusButton.setContentAreaFilled(true);
            hapusButton.setFont(new Font("Arial", Font.PLAIN, 12));
            hapusButton.setUI(new RoundedButton());
            hapusButton.setOpaque(false);
            hapusButton.setContentAreaFilled(true);

            try {
                ImageIcon icon = new ImageIcon(getClass().getResource("../SourceImage/icon/icon_sampah_putih.png"));
                Image scaledImage = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                hapusButton.setIcon(new ImageIcon(scaledImage));
            } catch (Exception e) {
                hapusButton.setText("Hapus");
            }

            add(bayarButton);
            add(hapusButton);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            // Set warna latar belakang berdasarkan kondisi baris
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                // Warna zebra-striping
                setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));
            }
            
            // Memastikan border tabel tetap terlihat
            setBorder(BorderFactory.createEmptyBorder());
            
            return this;
        }
    }

    // Kelas ButtonEditor untuk menghandle aksi klik pada tombol - DENGAN PERBAIKAN
    class ButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton bayarButton, hapusButton;
        private String action = "";
        private boolean isPushed;
        private int clickedRow;
        private JTable table;
        
        public ButtonEditor(JCheckBox checkBox, JTable table) {
            super(checkBox);
            this.table = table;
            
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
            
            // Buat tombol hapus
            hapusButton = new JButton();
            hapusButton.setPreferredSize(new Dimension(40, 30));
            hapusButton.setBackground(new Color(255, 59, 59));
            hapusButton.setForeground(Color.WHITE);
            hapusButton.setBorderPainted(false);
            hapusButton.setFocusPainted(false);
            hapusButton.setContentAreaFilled(true);
            hapusButton.setFont(new Font("Arial", Font.PLAIN, 12));
            hapusButton.setUI(new RoundedButton());
            hapusButton.setOpaque(false);
            hapusButton.setContentAreaFilled(true);

            try {
                ImageIcon icon = new ImageIcon(getClass().getResource("../SourceImage/icon/icon_sampah_putih.png"));
                Image scaledImage = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                hapusButton.setIcon(new ImageIcon(scaledImage));
            } catch (Exception e) {
                hapusButton.setText("Hapus");
            }
            
            hapusButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    action = "HAPUS";
                    isPushed = true;
                    fireEditingStopped();
                }
            });
            
            panel.add(bayarButton);
            panel.add(hapusButton);
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            
            isPushed = false;
            action = "";
            clickedRow = row;
            
          
            panel.setBackground(row % 2 == 0 ? Color.WHITE : new Color(57, 105, 138));
            
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                if (action.equals("BAYAR")) {
                    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(parentComponent);
                    PopUp_edittransbeli dialog = new PopUp_edittransbeli(parentFrame);
                    
                    // Tambahkan listener untuk memastikan tabel diperbarui setelah dialog ditutup
                    dialog.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            // Force refresh tabel setelah dialog ditutup
                            table.clearSelection();
                            table.repaint();
                        }
                    });
                    
                    dialog.setVisible(true);
                } else if (action.equals("HAPUS")) {
                    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(parentComponent);
                    PopUp_edittransbeli dialog = new PopUp_edittransbeli(parentFrame);
                    
                    // Tambahkan listener untuk memastikan tabel diperbarui setelah dialog ditutup
                    dialog.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            // Force refresh tabel setelah dialog ditutup
                            table.clearSelection();
                            table.repaint();
                        }
                    });
                    
                    dialog.setVisible(true);
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
}