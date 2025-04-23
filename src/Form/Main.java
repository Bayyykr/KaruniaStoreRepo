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

public class Main extends JFrame {

    // Define panel references as class members
    private Dashboard dashboardPanel;
//    private Produk produkPanel;
    private DataKaryawan karyawanPanel;
    private GajiKaryawan gajikaryawanPanel;
    private AbsenKaryawan absenpanel;
//    private Laporan laporanPanel;
    private Transaksibeli transaksiBeli;
    // Current active panel
    private JPanel currentPanel;

    public Main() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        // Get screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        getContentPane().setBackground(Color.WHITE);
        // Set window size to match screen
        setSize(screenSize);
        // Use null layout for full control of component positions
        setLayout(null);

        // Initialize components
        Menu sidebar = new Menu();
        NavBarAtas top = new NavBarAtas();

        // Initialize all panels
        dashboardPanel = new Dashboard();
//        produkPanel = new Produk();
        karyawanPanel = new DataKaryawan();
        gajikaryawanPanel = new GajiKaryawan();
        absenpanel = new AbsenKaryawan();
//        laporanPanel = new Laporan();
        transaksiBeli = new Transaksibeli();

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
//        produkPanel.setBounds(panelX, panelY, panelWidth, panelHeight);
        absenpanel.setBounds(panelX, panelY, panelWidth, 640);
        gajikaryawanPanel.setBounds(panelX, panelY, panelWidth, 640);
        karyawanPanel.setBounds(panelX, panelY, panelWidth, 640);
//        laporanPanel.setBounds(panelX, panelY, panelWidth, panelHeight);
        transaksiBeli.setBounds(panelX, panelY, panelWidth, panelHeight);
        
        karyawanPanel.setAbsenKaryawan(() -> {
            switchToAbsenKaryawan();
        });
        karyawanPanel.setGajiKaryawan(() -> {
            switchToGajiKaryawan();
        });
        // Di dalam constructor Main(), setelah inisialisasi panel:
gajikaryawanPanel.setBackToDataKaryawan(() -> {
    switchToDataKaryawan();
});
        
        absenpanel.setBackToDataKaryawan(() -> {
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
//                        currentPanel = produkPanel;
                        System.out.println("ini produk");
                        break;
                    case 2: // Karyawan
                        currentPanel = karyawanPanel;
                        break;
                    case 3: // Laporan
//                        currentPanel = laporanPanel;
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
    
    public void switchToAbsenKaryawan() {
        if (currentPanel != null) {
            remove(currentPanel);
        }
        
        absenpanel.resetToCurrentDate();
        absenpanel.clearSearch();
        
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
        
        karyawanPanel.refreshTable();
    
        currentPanel = karyawanPanel;
        add(currentPanel);
        currentPanel.setVisible(true);
    
        revalidate();
        repaint();
    }
    public void switchToGajiKaryawan() {
        if (currentPanel != null) {
            remove(currentPanel);
        }
    
        currentPanel = gajikaryawanPanel;
        add(currentPanel);
        currentPanel.setVisible(true);
    
        revalidate();
        repaint();
    }
    
    
    
    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }
}
