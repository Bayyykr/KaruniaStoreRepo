package laporan;

import SourceCode.RoundedBorder;
import SourceCode.JTableRounded;
import SourceCode.ScrollPane;
import calendar.CustomKalender;
import produk.ComboboxCustom;
import Form.diagramlaporan;
import Form.diagramlaporankeuangan;
import Form.diagramkaryawan;
import SourceCode.RoundedButtonLaporan;

import java.awt.*;
import java.awt.event.*;
import java.util.EventObject;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class Laporan extends javax.swing.JPanel {

    private JPanel mainPanel, headerPanel, contentPanel, innerContentPanel, tabPanel;
    private ComboboxCustom periodCombo, exportCombo;
    private JButton dateRangeButton;
    private CustomKalender calendarPanel;
    private JButton pemasukanTab, pengeluaranTab, labaTab;
    private diagramlaporan diagramPanel;
    private JTableRounded tabelPemasukan, tabelPengeluaran;
    private JPanel pemasukanPanel, pengeluaranPanel, labaPanel;

    // Warna dan styling
    private Color selectedTabColor = new Color(20, 20, 20);
    private Color unselectedTabColor = new Color(20, 20, 20);
    private Color selectedTabLineColor = new Color(0, 123, 255);
    private Color textColor = Color.WHITE;
    private Color innerPanelBgColor = new Color(20, 20, 20, 245);
    private Color tablePanelBgColor = new Color(20, 20, 20, 245);
    private JButton activeTab;

    public Laporan() {
        initComponents();
        setActiveTab(pemasukanTab);
        setupListeners();
    }

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
        // Buat dan atur UI rounded dengan border hitam
        RoundedButtonLaporan roundedUI = new RoundedButtonLaporan();
        roundedUI.setBorderColor(Color.LIGHT_GRAY);  // Atur warna border hitam
        dateRangeButton.setUI(roundedUI);

        // Comboboxes
        periodCombo = new ComboboxCustom();
        periodCombo.setPreferredSize(new Dimension(150, 35));
        periodCombo.addItem("Filter");
        periodCombo.addItem("Minggu Ini");
        periodCombo.addItem("Bulan Ini");
        periodCombo.addItem("Bulan Lalu");
        periodCombo.addItem("Tahun Ini");

        exportCombo = new ComboboxCustom();
        exportCombo.setPreferredSize(new Dimension(150, 35));
        exportCombo.addItem("Ekspor File");
        exportCombo.addItem("PDF");
        exportCombo.addItem("Excel");
        exportCombo.addItem("CSV");

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
        labaTab = createTabButton("Laba & Grafik");

        tabPanel.add(pemasukanTab);
        tabPanel.add(pengeluaranTab);
        tabPanel.add(labaTab);

        // Inisialisasi panel tab
        initPemasukanPanel();
        initPengeluaranPanel();
        initLabaPanel();

        JPanel dataContainer = new JPanel(new CardLayout());
        dataContainer.setOpaque(false);
        dataContainer.add(pemasukanPanel, "pemasukan");
        dataContainer.add(pengeluaranPanel, "pengeluaran");
        dataContainer.add(labaPanel, "laba");

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

    private void initPemasukanPanel() {
        pemasukanPanel = new JPanel(new BorderLayout());
        pemasukanPanel.setOpaque(false);

        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setOpaque(false);

        JLabel totalLabel = new JLabel("Total Pendapatan: Rp. 25.000.000");
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        totalLabel.setForeground(Color.WHITE);
        totalLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        totalPanel.add(totalLabel, BorderLayout.WEST);

        String[] kolom = {"No", "Tanggal", "Total", "Kasir"};
        tabelPemasukan = new JTableRounded(kolom);

        tabelPemasukan.setColumnWidth(0, 50);
        tabelPemasukan.setColumnWidth(1, 100);
        tabelPemasukan.setColumnWidth(2, 200);
        tabelPemasukan.setColumnWidth(3, 200);

        // Data contoh
        Object[][] data = {
            {"1", "10/04/2025", "Rp. 5.500.000", "Admin"},
            {"2", "09/04/2025", "Rp. 4.800.000", "Siti"},
            {"3", "08/04/2025", "Rp. 7.200.000", "Admin"},
            {"4", "07/04/2025", "Rp. 3.800.000", "Budi"},
            {"5", "06/04/2025", "Rp. 3.700.000", "Admin"},
            {"6", "06/04/2025", "Rp. 3.700.000", "Admin"},
            {"7", "06/04/2025", "Rp. 3.700.000", "Admin"},
            {"8", "06/04/2025", "Rp. 3.700.000", "Admin"},
            {"9", "06/04/2025", "Rp. 3.700.000", "Admin"},
            {"9", "06/04/2025", "Rp. 3.700.000", "Admin"},
            {"9", "06/04/2025", "Rp. 3.700.000", "Admin"},
            {"9", "06/04/2025", "Rp. 3.700.000", "Admin"},
            {"9", "06/04/2025", "Rp. 3.700.000", "Admin"},
            {"9", "06/04/2025", "Rp. 3.700.000", "Admin"},
            {"10", "06/04/2025", "Rp. 3.700.000", "Admin"}
        };

        for (Object[] row : data) {
            tabelPemasukan.addRow(row);
        }

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

    private void initPengeluaranPanel() {
        pengeluaranPanel = new JPanel(new BorderLayout());
        pengeluaranPanel.setOpaque(false);

        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setOpaque(false);

        JLabel totalLabel = new JLabel("Total Pengeluaran: Rp. 15.500.000");
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        totalLabel.setForeground(Color.WHITE);
        totalLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        totalPanel.add(totalLabel, BorderLayout.WEST);

        String[] kolom = {"No", "Tanggal", "Total"};
        tabelPengeluaran = new JTableRounded(kolom);

        tabelPengeluaran.setColumnWidth(0, 50);
        tabelPengeluaran.setColumnWidth(1, 200);
        tabelPengeluaran.setColumnWidth(2, 300);

        // Data contoh
        Object[][] data = {
            {"1", "10/04/2025", "Rp. 3.200.000"},
            {"2", "09/04/2025", "Rp. 2.800.000"},
            {"3", "08/04/2025", "Rp. 4.100.000"},
            {"4", "07/04/2025", "Rp. 2.500.000"},
            {"4", "07/04/2025", "Rp. 2.500.000"},
            {"4", "07/04/2025", "Rp. 2.500.000"},
            {"4", "07/04/2025", "Rp. 2.500.000"},
            {"4", "07/04/2025", "Rp. 2.500.000"},
            {"4", "07/04/2025", "Rp. 2.500.000"},
            {"4", "07/04/2025", "Rp. 2.500.000"},
            {"4", "07/04/2025", "Rp. 2.500.000"},
            {"4", "07/04/2025", "Rp. 2.500.000"},
            {"4", "07/04/2025", "Rp. 2.500.000"},
            {"4", "07/04/2025", "Rp. 2.500.000"},
            {"4", "07/04/2025", "Rp. 2.500.000"},
            {"5", "06/04/2025", "Rp. 2.900.000"}
        };

        for (Object[] row : data) {
            tabelPengeluaran.addRow(row);
        }

        JTable rawTable = tabelPengeluaran.getTable();
        TableColumnModel columnModel = rawTable.getColumnModel();

        columnModel.getColumn(2).setCellEditor(new DefaultCellEditor(new JTextField()) {
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

    private void initLabaPanel() {
        labaPanel = new JPanel(new BorderLayout());
        labaPanel.setOpaque(false);

        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setOpaque(false);

        JLabel totalLabel = new JLabel("Laba : Rp. 11.000.000");
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        totalLabel.setForeground(Color.WHITE);
        totalLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        totalPanel.add(totalLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JButton keuanganButton = createTabButtonForLaba("Keuangan", true);
        JButton karyawanButton = createTabButtonForLaba("Karyawan", false);

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
        headerPanel.add(totalLabel, BorderLayout.NORTH);
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
        tabPanel.repaint();

        CardLayout cardLayout = (CardLayout) ((JPanel) innerContentPanel.getComponent(1)).getLayout();

        if (tab == pemasukanTab) {
            cardLayout.show((JPanel) innerContentPanel.getComponent(1), "pemasukan");
        } else if (tab == pengeluaranTab) {
            cardLayout.show((JPanel) innerContentPanel.getComponent(1), "pengeluaran");
        } else if (tab == labaTab) {
            cardLayout.show((JPanel) innerContentPanel.getComponent(1), "laba");
        }
    }

    private void setupListeners() {
        pemasukanTab.addActionListener(e -> setActiveTab(pemasukanTab));
        pengeluaranTab.addActionListener(e -> setActiveTab(pengeluaranTab));
        labaTab.addActionListener(e -> setActiveTab(labaTab));
    }
}
