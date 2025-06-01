package PopUp_all;

import Form.Transjual;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import produk.ComboboxCustom;
import db.conn;
import java.lang.reflect.Method;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Popup_edittransjual extends JDialog {
    
    private JComponent glassPane;
    private static final int RADIUS = 20;
    private JButton cancelButton, updateButton;
    private RoundedTextField quantityField;
    private ComboboxCustom productComboBox;
    private JFrame parentFrame;
    private JLabel titleLabel, diskonLabel, qtyLabel;
    
    private int xMouse, yMouse;
    private final int ANIMATION_DURATION = 300; // ms
    private final int ANIMATION_DELAY = 10; // ms
    private float currentScale = 0.01f;
    private boolean animationStarted = false;
    private Timer animationTimer;
    private Timer closeAnimationTimer;
    private final int FINAL_WIDTH = 450;  // Ukuran tetap
    private final int FINAL_HEIGHT = 230; // Ukuran tetap

    private Connection con;
    private DecimalFormat formatter;

    // Variables to store original table data
    private JTable table;
    private int selectedRow;
    private String originalName;
    private String originalSize;
    private int originalPrice;
    private String originalDiscount;

    // Flag untuk menghindari penambahan glassPane berulang
    private static boolean isShowingPopup = false;
    
    public Popup_edittransjual(JFrame parent, JTable table, int selectedRow) {
        this.parentFrame = parent;
        this.table = table;
        this.selectedRow = selectedRow;
        setModal(true);
        setPreferredSize(new Dimension(FINAL_WIDTH, FINAL_HEIGHT));
        setLayout(null);
        con = conn.getConnection();

        // Menggunakan titik sebagai pemisah ribuan, bukan koma
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        formatter = new DecimalFormat("#,###", symbols);

        // Store original values
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        this.originalName = model.getValueAt(selectedRow, 1).toString();
        this.originalSize = model.getValueAt(selectedRow, 2).toString();
        String priceStr = model.getValueAt(selectedRow, 4).toString().replace("Rp. ", "").replace(".", "").trim();
        this.originalPrice = Integer.parseInt(priceStr);
        this.originalDiscount = model.getValueAt(selectedRow, 5).toString();

        // Periksa apakah popup sudah ditampilkan
        if (isShowingPopup) {
            dispose();
            return;
        }
        isShowingPopup = true;

        // Buat overlay transparan dengan warna hitam semi transparan
        glassPane = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 180)); // Transparansi lebih tinggi
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        glassPane.setOpaque(false);
        glassPane.setBounds(0, 0, parent.getWidth(), parent.getHeight());

        // Menghapus glassPane yang mungkin sudah ada sebelumnya
        cleanupExistingGlassPane();
        
        parentLayeredPane().add(glassPane, JLayeredPane.POPUP_LAYER);
        
        setUndecorated(true); // Menghilangkan border default
        setSize(FINAL_WIDTH, FINAL_HEIGHT);
        setLocationRelativeTo(parent);
        setLayout(null);
        setBackground(new Color(0, 0, 0, 0)); // Membuat background transparan

        JPanel contentPanel = new RoundedPanel(RADIUS);
        contentPanel.setLayout(null);
        contentPanel.setBounds(0, 0, FINAL_WIDTH, FINAL_HEIGHT);
        contentPanel.setBackground(Color.WHITE);
        add(contentPanel);
        
        titleLabel = createTextLabel("Edit Transaksi Jual", 20, 10, 400, 30, new Font("Arial", Font.BOLD, 16), Color.BLACK);
        titleLabel.setVisible(false);
        contentPanel.add(titleLabel);

        // Menambahkan label Atur Diskon di atas combo box
        diskonLabel = createTextLabel("Atur Diskon", 105, 50, 226, 20, new Font("Arial", Font.PLAIN, 12), Color.BLACK);
        diskonLabel.setHorizontalAlignment(SwingConstants.LEFT);
        diskonLabel.setVisible(false);
        contentPanel.add(diskonLabel);

        // Membuat ComboBox produk
        productComboBox = new ComboboxCustom(getDiscountOptionsFromDatabase());
        productComboBox.setBounds(105, 70, 226, 38);
        productComboBox.setVisible(false);

        // Set the initial selected discount
        if (!originalDiscount.equals("-")) {
            String discountValue = originalDiscount.replace("%", "").trim();
            // Get the actual discount name from database based on the percentage
            try {
                String query = "SELECT nama_diskon FROM diskon WHERE total_diskon = ? AND status = 'dipakai'";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setDouble(1, Double.parseDouble(discountValue));
                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    String diskonName = rs.getString("nama_diskon");
                    // Find and select the matching item in the combo box
                    for (int i = 0; i < productComboBox.getItemCount(); i++) {
                        if (productComboBox.getItemAt(i).toString().equals(diskonName)) {
                            productComboBox.setSelectedIndex(i);
                            break;
                        }
                    }
                } else {
                    // If not found in database, try to match by percentage
                    for (int i = 0; i < productComboBox.getItemCount(); i++) {
                        String item = productComboBox.getItemAt(i).toString();
                        // Check if the item contains the discount percentage
                        if (item.contains(discountValue + "%") || item.equals(discountValue)) {
                            productComboBox.setSelectedIndex(i);
                            break;
                        }
                    }
                }
                
                rs.close();
                ps.close();
            } catch (SQLException | NumberFormatException ex) {
                // Fallback to direct matching if database query fails
                for (int i = 0; i < productComboBox.getItemCount(); i++) {
                    String item = productComboBox.getItemAt(i).toString();
                    if (item.contains(discountValue)) {
                        productComboBox.setSelectedIndex(i);
                        break;
                    }
                }
                System.err.println("Error matching discount value: " + ex.getMessage());
            }
        }
        
        contentPanel.add(productComboBox);

        // Tambahkan label Qty di luar textfield
        qtyLabel = createTextLabel("Qty", 105, 120, 226, 20, new Font("Arial", Font.PLAIN, 12), Color.BLACK);
        qtyLabel.setHorizontalAlignment(SwingConstants.LEFT);
        qtyLabel.setVisible(false);
        contentPanel.add(qtyLabel);

        // Modifikasi field quantity dengan nilai dari tabel
        String currentQty = model.getValueAt(selectedRow, 3).toString();
        quantityField = new RoundedTextField(5, "");
        quantityField.setText(currentQty);
        quantityField.setBounds(105, 140, 226, 35);
        quantityField.setVisible(false);

        // Tambahkan KeyListener untuk validasi real-time pada quantity field
        quantityField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                // Hanya izinkan angka dan backspace
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
            }
        });
        
        contentPanel.add(quantityField);

        // Menginisialisasi tombol Cancel dan Update
        cancelButton = createRoundedButton("Cancel", 60, 190, 130, 30, new Color(0xEBEBEB), Color.BLACK);
        cancelButton.setVisible(false);
        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                startCloseAnimation();  // Memulai animasi penutupan saat tombol cancel diklik
            }
        });
        contentPanel.add(cancelButton);

        // Tombol Update
        updateButton = createRoundedButton("Update", 250, 190, 130, 30, new Color(0x34C759), Color.WHITE);
        updateButton.setVisible(false);
        updateButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (validateInput()) {
                    updateTableData();  // Call method to update table data
                    startCloseAnimation();
                }
            }
        });
        contentPanel.add(updateButton);

        // Tambahkan WindowListener untuk membersihkan saat popup ditutup
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanupAndClose();
            }
        });

        // Memulai animasi setelah pop-up muncul
        startScaleAnimation();  // Memulai animasi
    }
    
    //Validasi field edit
    private boolean validateInput() {
        String qtyText = quantityField.getText().trim();
        
        if (qtyText.isEmpty()) {
            PindahanAntarPopUp.showTransBeliQuantityTidakBolehKosong(parentFrame);
            quantityField.requestFocus();
            return false;
        }
        
        int qty;
        try {
            qty = Integer.parseInt(qtyText);
        } catch (NumberFormatException ex) {
            PindahanAntarPopUp.showTransBeliEditQtyHarusAngka(parentFrame);
            quantityField.requestFocus();
            return false;
        }
        
        if (qty <= 0) {
            PindahanAntarPopUp.showTransBeliQuantityHarusLebihDari0(parentFrame);
            quantityField.requestFocus();
            return false;
        }
        PindahanAntarPopUp.showProdukBerhasilDiedit(parentFrame);
        return true;
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error Validasi", JOptionPane.ERROR_MESSAGE);
    }

    // Method to update the table data with new discount and quantity
    private void updateTableData() {
        try {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            
            int newQty = Integer.parseInt(quantityField.getText().trim());

            // Get the new discount
            String diskonText = productComboBox.getSelectedItem().toString();
            double diskon = 0;
            
            if (!diskonText.isEmpty()) {
                // Get the actual discount percentage from database based on the selected name
                try {
                    String query = "SELECT total_diskon FROM diskon WHERE nama_diskon = ? AND status = 'dipakai'";
                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setString(1, diskonText);
                    ResultSet rs = ps.executeQuery();
                    
                    if (rs.next()) {
                        diskon = rs.getDouble("total_diskon");
                    } else {
                        // If not found in database (fallback), try to parse from the string
                        if (diskonText.contains("%")) {
                            diskon = Double.parseDouble(diskonText.replace("%", ""));
                        }
                    }
                    
                    rs.close();
                    ps.close();
                } catch (SQLException ex) {
                    // Fallback if database query fails
                    if (diskonText.contains("%")) {
                        diskon = Double.parseDouble(diskonText.replace("%", ""));
                    }
                    System.err.println("Error fetching discount value: " + ex.getMessage());
                }
            }

            // Calculate the new total
            double diskonAmount = (diskon / 100) * originalPrice;
            double priceAfterDiscount = originalPrice - diskonAmount;
            double totalPrice = priceAfterDiscount * newQty;

            // Format discount display
            String diskonDisplay = diskon == 0 ? "-" : "" + (int) diskon + "%";

            // Update the table
            model.setValueAt(newQty, selectedRow, 3);
            model.setValueAt(diskonDisplay, selectedRow, 5);
            model.setValueAt("Rp. " + formatter.format(totalPrice), selectedRow, 6);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // Metode untuk membersihkan glassPane yang mungkin sudah ada
    private void cleanupExistingGlassPane() {
        Component[] components = parentLayeredPane().getComponentsInLayer(JLayeredPane.POPUP_LAYER);
        for (Component comp : components) {
            if (comp instanceof JComponent) {
                parentLayeredPane().remove(comp);
            }
        }
        parentLayeredPane().repaint();
    }
    
    private void startScaleAnimation() {
        if (animationStarted || (animationTimer != null && animationTimer.isRunning())) {
            return;
        }
        
        animationStarted = true;
        final int totalFrames = ANIMATION_DURATION / ANIMATION_DELAY;
        final int[] currentFrame = {0};
        
        animationTimer = new Timer(ANIMATION_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentFrame[0]++;
                
                float progress = (float) currentFrame[0] / totalFrames;
                float easedProgress = 1 - (1 - progress) * (1 - progress) * (1 - progress);  // Easing untuk efek zoom-in

                currentScale = 0.01f + 0.99f * easedProgress;
                
                if (progress >= 0.3 && !titleLabel.isVisible()) {
                    titleLabel.setVisible(true);
                    diskonLabel.setVisible(true);
                    productComboBox.setVisible(true);
                    qtyLabel.setVisible(true);
                    quantityField.setVisible(true);
                    cancelButton.setVisible(true);
                    updateButton.setVisible(true);
                }
                
                repaint();
                
                if (currentFrame[0] >= totalFrames) {
                    animationTimer.stop();
                    currentScale = 1.0f;
                    
                    titleLabel.setVisible(true);
                    diskonLabel.setVisible(true);
                    productComboBox.setVisible(true);
                    qtyLabel.setVisible(true);
                    quantityField.setVisible(true);
                    cancelButton.setVisible(true);
                    updateButton.setVisible(true);
                    
                    repaint();
                }
            }
        });
        
        animationTimer.start();  // Memulai animasi zoom-in
    }
    
    private void startCloseAnimation() {
        if (closeAnimationTimer != null && closeAnimationTimer.isRunning()) {
            closeAnimationTimer.stop();
        }
        
        final int totalFrames = ANIMATION_DURATION / ANIMATION_DELAY;
        final int[] currentFrame = {0};
        
        closeAnimationTimer = new Timer(ANIMATION_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentFrame[0]++;
                
                float progress = (float) currentFrame[0] / totalFrames;
                float easedProgress = progress * progress;  // Easing untuk animasi halus

                currentScale = 1.0f - 0.99f * easedProgress;  // Zoom-out dari tengah

                repaint();
                
                if (currentFrame[0] >= totalFrames) {
                    closeAnimationTimer.stop();  // Menghentikan timer animasi
                    cleanupAndClose();
                }
            }
        });
        
        closeAnimationTimer.start();  // Memulai animasi zoom-out
    }
    
    private void cleanupAndClose() {
        // Reset flag saat popup ditutup
        isShowingPopup = false;

        // Hapus glassPane
        closePopup();

        // Tutup dialog
        dispose();
    }
    
    private void closePopup() {
        parentLayeredPane().remove(glassPane);
        parentLayeredPane().repaint();
    }
    
    private JLayeredPane parentLayeredPane() {
        return parentFrame.getLayeredPane();
    }
    
    private JButton createRoundedButton(String text, int x, int y, int width, int height, Color bgColor, Color textColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bgColor.darker() : bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setBounds(x, y, width, height);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setForeground(textColor);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        // Pastikan tidak ada efek tekan pada tombol
        button.setPressedIcon(button.getIcon());
        button.setRolloverEnabled(false);
        return button;
    }

    // Fungsi untuk membuat label teks
    private JLabel createTextLabel(String text, int x, int y, int width, int height, Font font, Color color) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setBounds(x, y, width, height);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }
    
    class RoundedPanel extends JPanel {
        
        private int radius;
        
        public RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (animationStarted) {
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                
                AffineTransform originalTransform = g2.getTransform();
                g2.translate(centerX, centerY);
                g2.scale(currentScale, currentScale);
                g2.translate(-centerX, -centerY);

                // Draw background with rounded corners
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
                
                g2.setTransform(originalTransform);
            } else {
                // Draw background with rounded corners
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            }
            
            g2.dispose();
        }
        
        @Override
        protected void paintChildren(Graphics g) {
            if (animationStarted && currentScale < 0.3) {
                return;
            }
            
            if (animationStarted) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                
                g2d.translate(centerX, centerY);
                g2d.scale(currentScale, currentScale);
                g2d.translate(-centerX, -centerY);
                
                super.paintChildren(g2d);
                g2d.dispose();
            } else {
                super.paintChildren(g);
            }
        }
    }

    // Kelas JTextField kustom dengan border rounded + placeholder
    static class RoundedTextField extends JTextField {
        
        private int radius;
        private String placeholder;
        private boolean isPlaceholderActive;
        
        public RoundedTextField(int radius, String placeholder) {
            super(placeholder);
            this.radius = radius;
            this.placeholder = placeholder;
            this.isPlaceholderActive = true;
            setForeground(Color.GRAY);
            setOpaque(false);
            setBorder(new EmptyBorder(5, 10, 5, 10));
            
            addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (getText().equals(placeholder) && isPlaceholderActive) {
                        setText("");
                        setForeground(Color.BLACK);
                        isPlaceholderActive = false;
                    }
                }
                
                @Override
                public void focusLost(FocusEvent e) {
                    if (getText().isEmpty()) {
                        setText(placeholder);
                        setForeground(Color.GRAY);
                        isPlaceholderActive = true;
                    }
                }
            });
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius); // Rounded textfield
            g2.setColor(Color.GRAY);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius); // Border rounded
            g2.dispose();
            super.paintComponent(g);
        }
    }
    
    private String[] getDiscountOptionsFromDatabase() {
        try {
            // Query to get distinct discount options from database
            String query = "SELECT DISTINCT nama_diskon FROM diskon WHERE id_diskon != 'DS_00' AND status = 'dipakai' ORDER BY total_diskon ASC";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            // Count the number of results
            ArrayList<String> options = new ArrayList<>();
            options.add(""); // Adding empty option first

            while (rs.next()) {
                String diskonValue = rs.getString("nama_diskon");
                options.add(diskonValue);
            }
            
            rs.close();
            ps.close();
            
            return options.toArray(new String[0]);
        } catch (SQLException ex) {
            System.err.println("Error fetching discount options: " + ex.getMessage());
            // Return default options if database fails
            return new String[]{"", "5%", "10%", "15%", "20%", "25%", "30%", "50%"};
        }
    }
}
