package laporan;

import SourceCode.JTableRounded;
import SourceCode.ScrollPane;
import calendar.CustomKalender;
import produk.ComboboxCustom;
import Form.diagramlaporan;
import Form.diagramlaporankeuangan;
import Form.diagramkaryawan;
import PopUp_all.*;
import SourceCode.*;

import java.awt.*;
import java.awt.event.*;
import java.util.EventObject;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

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
        RoundedButtonLaporan roundedUI = new RoundedButtonLaporan();
        roundedUI.setBorderColor(Color.LIGHT_GRAY); 
        dateRangeButton.setUI(roundedUI);
        dateRangeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(parentComponent);
                PopUp_StartDateEndDate dialog = new PopUp_StartDateEndDate(parentFrame);
                dialog.setVisible(true);
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
        totalPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        RoundedPanelProduk totalPendapatanPanel = new RoundedPanelProduk(10);
        totalPendapatanPanel.setLayout(null);
        totalPendapatanPanel.setBackground(new Color(40, 50, 100));
        totalPendapatanPanel.setPreferredSize(new Dimension(520, 40));

        JLabel totalPendapatanLabel = new JLabel("Total Pendapatan");
        totalPendapatanLabel.setForeground(Color.WHITE);
        totalPendapatanLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        totalPendapatanLabel.setBounds(15, -5, 250, 50);

        JLabel totalPendapatanValue = new JLabel("Rp. 0");
        totalPendapatanValue.setForeground(Color.WHITE);
        totalPendapatanValue.setFont(new Font("SansSerif", Font.BOLD, 14));
        totalPendapatanValue.setBounds(300, -5, 235, 50);
        totalPendapatanValue.setHorizontalAlignment(SwingConstants.RIGHT);

        totalPendapatanPanel.add(totalPendapatanLabel);
        totalPendapatanPanel.add(totalPendapatanValue);
        totalPanel.add(totalPendapatanPanel, BorderLayout.CENTER);

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

        JLabel pemasukanValueLabel = new JLabel("Rp. 25.000.000");
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

        JLabel pengeluaranValueLabel = new JLabel("- Rp. 14.000.000");
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

        JLabel labaKotorValueLabel = new JLabel("Rp. 11.000.000");
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

        JLabel labaKotorItem2ValueLabel = new JLabel("Rp. 11.000.000");
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

        JLabel operasionalValueLabel = new JLabel("- Rp. 340.000");
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
        labaBersihItemLabel.setBounds(15, 0, 250, 50);

        JLabel labaBersihValueLabel = new JLabel("Rp. 10.660.000");
        labaBersihValueLabel.setForeground(new Color(100, 255, 100));
        labaBersihValueLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        labaBersihValueLabel.setBounds(270, 0, 235, 50);
        labaBersihValueLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        labaBersihPanel.add(labaBersihItemLabel);
        labaBersihPanel.add(labaBersihValueLabel);
        mainContentPanel.add(labaBersihPanel);

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

        JLabel totalPengeluaranValue = new JLabel("- Rp. 15.500.000");
        totalPengeluaranValue.setForeground(new Color(255, 75, 75));
        totalPengeluaranValue.setFont(new Font("SansSerif", Font.BOLD, 14));
        totalPengeluaranValue.setBounds(300, -5, 235, 50);
        totalPengeluaranValue.setHorizontalAlignment(SwingConstants.RIGHT);

        totalPengeluaranPanel.add(totalPengeluaranLabel);
        totalPengeluaranPanel.add(totalPengeluaranValue);
        totalPanel.add(totalPengeluaranPanel, BorderLayout.CENTER);

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
}
