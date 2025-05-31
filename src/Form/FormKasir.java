package Form;

import PopUp_all.*;
import SourceCode.NavBarAtas;
import SourceCode.SidebarCustom.EventMenu;
import SourceCode.SidebarCustom.Menu;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.*;
import db.conn;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import produk.ProductDisplayy;
import produk.AddNewProductFormm;
import produk.ProductDisplayyKasir;
import produk.EditProductPanel;

public class FormKasir extends JFrame {

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
    private static FormKasir mainFrame; // Reference to the main frame (static agar bisa diakses)
    private static String currentUserNorfid;
    private Connection con;

    // Getter untuk mengambil instance utama (mencegah null pointer)
    public static FormKasir getMainFrame() {
        return mainFrame;
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
                currentUserNorfid = rs.getString("norfid");
            } else {
                System.out.println("No karyawan found ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FormKasir() {
        mainFrame = this; // Simpan instance utama

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Ubah ke DO_NOTHING_ON_CLOSE
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Catat waktu keluar sebelum menutup
                recordLogoutOnClose();
                // Tutup aplikasi
                System.exit(0);
            }
        });
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        con = conn.getConnection();
        setNamaUser();

        // Get screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        getContentPane().setBackground(Color.WHITE);
        setSize(screenSize);
        setLayout(null);

        // Initialize components dengan menu khusus untuk kasir
        Menu sidebar = new Menu("kasir"); // Gunakan constructor dengan parameter "kasir"
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

        // Add menu change event listener
        sidebar.addEventMenu(new EventMenu() {
            @Override
            public void menuIndexChange(int index) {
                if (index == 2) {
                    Popup_LogOutKasir dialog = new Popup_LogOutKasir(FormKasir.this, currentUserNorfid);
                    dialog.setVisible(true);
                    return;
                }

                // Remove current panel if exists   
                if (currentPanel != null) {
                    remove(currentPanel);
                }
                switch (index) {
                    case 0: // Dashboard
                        currentPanel = dashboardPanelKasir;
                        break;
                    case 1: // Produk
                        currentPanel = produkPanelkasir;
                        System.out.println("ini produk");
                        break;
                    case 2: // Keluar (pada kasir, Keluar ada di indeks 2)
//                        System.exit(0);
                        break;
                    default:
                        currentPanel = dashboardPanelKasir;
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
        currentPanel = dashboardPanelKasir;
        add(currentPanel);

        // Make all components visible
        sidebar.setVisible(true);
        top.setVisible(true);
        currentPanel.setVisible(true);
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

    public void switchToTransJualPanel(String scannedBarcode) {
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

    public void switchBackToLoginPanel() {
        this.dispose();
        LoginForm loginForm = LoginForm.getInstance();
        loginForm.resetForm();
        loginForm.setVisible(true);
        loginForm.setLocationRelativeTo(null);
    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> {
            new FormKasir().setVisible(true);
        });
    }

    public void recordLogoutOnClose() {
        if (currentUserNorfid == null || currentUserNorfid.isEmpty()) {
            System.out.println("No user RFID found for logout recording");
            return;
        }

        try {
            String updateQuery = "UPDATE absensi SET waktu_keluar = NOW() WHERE norfid = ? AND DATE(waktu_masuk) = CURDATE()";

            PreparedStatement stmt = con.prepareStatement(updateQuery);
            stmt.setString(1, currentUserNorfid);

            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Auto logout time recorded successfully for RFID: " + currentUserNorfid);
            } else {
                System.out.println("No attendance record found to update for RFID: " + currentUserNorfid);
            }
        } catch (SQLException e) {
            System.err.println("Error recording auto logout: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
