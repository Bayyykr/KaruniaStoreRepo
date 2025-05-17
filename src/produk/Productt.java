package Form;

import SourceCode.NavBarAtas;
import SourceCode.SidebarCustom.EventMenu;
import SourceCode.SidebarCustom.Menu;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import produk.ProductDisplayy;
import produk.AddNewProductFormm;
import produk.ProductDisplayyKasir;
import laporan.Laporan;
import produk.EditProductPanel;

public class Productt extends JFrame {

    // Define panel references as class members
    private Dashboard dashboardPanel;
    private DashboardKasir dashboardPanelKasir;
    private ProductDisplayyKasir produkPanelkasir;
    private ProductDisplayy produkPanel;
    private AddNewProductFormm addProductPanel;
    private DeleteProductPanel deleteProductPanel;
    private EditProductPanel editproductpanel;
    private DataKaryawan karyawanPanel;
    private AbsenKaryawan absenpanel;
    private GajiKaryawan gajikaryawan;
    private Transaksibeli transaksiBeli;
    private Transjual transaksiJual;
    private Laporan laporanPanel; // Tambahkan reference untuk panel laporan
    private JPanel currentPanel; // Current active panel
    private static Productt mainFrame; // Reference to the main frame (static agar bisa diakses)

    // Getter untuk mengambil instance utama (mencegah null pointer)
    public static Productt getMainFrame() {
        return mainFrame;
    }

    public Productt() {
        mainFrame = this; // Simpan instance utama

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Get screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        getContentPane().setBackground(Color.WHITE);
        setSize(screenSize);
        setLayout(null);

        // Initialize components
        Menu sidebar = new Menu();
        NavBarAtas top = new NavBarAtas();

        // Initialize all panels
        dashboardPanel = new Dashboard();
        dashboardPanelKasir = new DashboardKasir();
        produkPanelkasir = new ProductDisplayyKasir();
        produkPanel = new ProductDisplayy();
        addProductPanel = new AddNewProductFormm();
        deleteProductPanel = new DeleteProductPanel();
        editproductpanel = new EditProductPanel();
        karyawanPanel = new DataKaryawan();
        absenpanel = new AbsenKaryawan();
        transaksiBeli = new Transaksibeli();
        gajikaryawan = new GajiKaryawan();
        transaksiJual = new Transjual();
        laporanPanel = new Laporan();

        // Set bounds for fixed components
        sidebar.setBounds(0, 0, 260, screenSize.height);
        top.setBounds(250, 0, screenSize.width - 250, 50);

        // Calculate panel position and size
        int panelX = 280;
        int panelY = 80;
        int panelWidth = screenSize.width - 300;
        int panelHeight = screenSize.height - 110;

        // Set bounds for all panels
        dashboardPanel.setBounds(panelX, panelY, panelWidth, panelHeight);
        dashboardPanelKasir.setBounds(panelX, panelY, panelWidth, panelHeight);
        produkPanel.setBounds(panelX, 50, 1100, 720);
        produkPanelkasir.setBounds(panelX, 50, 1100, 720);
        addProductPanel.setBounds(panelX, 50, 1100, 720);
        deleteProductPanel.setBounds(panelX, 50, 1100, 720);
        karyawanPanel.setBounds(panelX, panelY, panelWidth, panelHeight);
        absenpanel.setBounds(panelX, panelY, panelWidth, 640);
        transaksiBeli.setBounds(panelX, panelY, panelWidth, panelHeight);
        transaksiJual.setBounds(panelX, panelY, panelWidth, panelHeight);
        gajikaryawan.setBounds(panelX, panelY, panelWidth, panelHeight);
        editproductpanel.setBounds(panelX, panelY, panelWidth, panelHeight);
        laporanPanel.setBounds(panelX, panelY, panelWidth, panelHeight); // Set bounds untuk panel laporan

        // Tambahkan method untuk mengganti panel dari ProductDisplayy ke AddNewProductFormm
        produkPanel.setPlusButtonListener(() -> {
            switchToAddProductPanel();
        });

        produkPanel.setPindahTransJual(() -> {
            switchToEditProductPanel();
        });

        // Add method to switch to DeleteProductPanel
        produkPanel.setTrashButtonListener(() -> {
            switchToDeleteProductPanel();
        });

        karyawanPanel.setAbsenKaryawan(() -> {
            switchToAbsenKaryawan();
        });

        absenpanel.setBackToDataKaryawan(() -> {
            switchToDataKaryawan();
        });

        karyawanPanel.setGajiKaryawan(() -> {
            switchToGajiKaryawanPanel();
        });

        gajikaryawan.setBackToDataKaryawan(() -> {
            switchToDataKaryawan();
        });
        // Add menu change event listener
        sidebar.addEventMenu(new EventMenu() {
            @Override
            public void menuIndexChange(int index) {
                // Remove current panel if exists   
                if (currentPanel != null) {
                    remove(currentPanel);
                }

                // Show the appropriate panel based on menu index
                switch (index) {
                    case 0: // Dashboard
                        currentPanel = dashboardPanel;
                        break;
                    case 1: // Produk
                        currentPanel = produkPanel;
                        System.out.println("ini produk");
                        break;
                    case 2: // Karyawan
                        currentPanel = karyawanPanel;
                        break;
                    case 3: // Laporan
                        currentPanel = laporanPanel;
                        break;
                    case 4: // Transaksi Beli
                        currentPanel = transaksiBeli;
                        break;
                    case 5: // Keluar
                        System.exit(0);
                        break;
                    default:
                        currentPanel = dashboardPanel;
                        break;
                }

                // Add and show the new panel
                add(currentPanel);
                currentPanel.setVisible(true);

                // Repaint to show changes
                revalidate();
                repaint();
            }
        });

        // Add fixed components
        add(sidebar);
        add(top);

        // Set Dashboard as initial panel
        currentPanel = dashboardPanel;
        add(currentPanel);

        // Make all components visible
        sidebar.setVisible(true);
        top.setVisible(true);
        currentPanel.setVisible(true);
    }

    public void switchToEditProductPanelScan(String scannedBarcode) {
        if (currentPanel != null) {
            remove(currentPanel);
        }

        // Buat panel edit baru dengan parameter barcode
        editproductpanel = new EditProductPanel(this, scannedBarcode);

        // Set bounds (ukuran & posisi)
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int panelX = 280;
        int panelY = 80;
        int panelWidth = screenSize.width - 300;
        int panelHeight = screenSize.height - 110;
        editproductpanel.setBounds(panelX, panelY, panelWidth, panelHeight);

        currentPanel = editproductpanel;
        add(currentPanel);
        currentPanel.setVisible(true);

        revalidate();
        repaint();
    }

    // Method untuk beralih ke panel AddNewProductFormm
    public void switchToAddProductPanel() {
        if (currentPanel != null) {
            remove(currentPanel);
        }
        
        currentPanel = addProductPanel;
        add(currentPanel);
        currentPanel.setVisible(true);

        revalidate();
        repaint();
    }

    // Method untuk beralih ke panel DeleteProductPanel
    public void switchToDeleteProductPanel() {
        if (currentPanel != null) {
            remove(currentPanel);
        }

        currentPanel = deleteProductPanel;
        add(currentPanel);
        currentPanel.setVisible(true);

        revalidate();
        repaint();
    }

    public void switchToEditProductPanel() {
        if (currentPanel != null) {
            remove(currentPanel);
        }

        // Get the selected product ID from ProductDisplayy
        String selectedId = produkPanel.getSelectedProductId();
        System.out.println("Switching to edit panel with ID: " + selectedId);

        // Create a new EditProductPanel with the correct parameters
        editproductpanel = new EditProductPanel(this, selectedId);

        // Set bounds for the new panel (use the same bounds as before)
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int panelX = 280;
        int panelY = 80;
        int panelWidth = screenSize.width - 300;
        int panelHeight = screenSize.height - 110;
        editproductpanel.setBounds(panelX, panelY, panelWidth, panelHeight);

        currentPanel = editproductpanel;
        add(currentPanel);
        currentPanel.setVisible(true);

        revalidate();
        repaint();
    }

//    //INI GANTI PANEL EDIT    
//    public EditProductPanel getEditProductPanel() {
//        return this.editproductpanel;
//    }
    // Tambahkan method untuk beralih ke panel Transjual
    public void switchToTransJualPanel() {
        if (currentPanel != null) {
            remove(currentPanel);
        }

        currentPanel = transaksiJual;
        add(currentPanel);
        currentPanel.setVisible(true);

        revalidate();
        repaint();
    }

    // Method untuk kembali ke panel produk
    public void switchBackToProductPanel() {
        if (currentPanel != null) {
            remove(currentPanel);
        }

        currentPanel = produkPanel;
        add(currentPanel);
        currentPanel.setVisible(true);

        revalidate();
        repaint();
    }

    // Method untuk kembali ke panel produk
    public void switchBackToProductPanelKasir() {
        if (currentPanel != null) {
            remove(currentPanel);
        }

        currentPanel = produkPanelkasir;
        add(currentPanel);
        currentPanel.setVisible(true);

        revalidate();
        repaint();
    }

    public void switchToAbsenKaryawan() {
        if (currentPanel != null) {
            remove(currentPanel);
        }

        currentPanel = absenpanel;
        add(currentPanel);
        currentPanel.setVisible(true);

        revalidate();
        repaint();
    }

    public void switchToDataKaryawan() {
        if (currentPanel != null) {
            remove(currentPanel);
        }

        currentPanel = karyawanPanel;
        add(currentPanel);
        currentPanel.setVisible(true);

        revalidate();
        repaint();
    }

    public void switchToGajiKaryawanPanel() {
        if (currentPanel != null) {
            remove(currentPanel);
        }

        currentPanel = gajikaryawan;
        add(currentPanel);
        currentPanel.setVisible(true);

        revalidate();
        repaint();
    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> {
            new Productt().setVisible(true);
        });
    }
}
