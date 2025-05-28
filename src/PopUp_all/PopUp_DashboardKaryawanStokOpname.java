package PopUp_all;

import Form.LoginForm;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.text.*;
import java.sql.*;
import db.conn;
import java.time.LocalDate;

public class PopUp_DashboardKaryawanStokOpname extends JDialog {

    private Connection con;
    private JComponent glassPane;
    private JFrame parentFrame;
    private static final int RADIUS = 20;
    private JButton batalButton, simpanButton;
    private JTextField barcodeField;
    private JLabel namaProdukLabel, tanggalLabel;
    private JTextField jumlahField;
    private JTextArea keteranganArea;

    private int xMouse, yMouse;
    private final int ANIMATION_DURATION = 300;
    private final int ANIMATION_DELAY = 10;
    private float currentScale = 0.01f;
    private boolean animationStarted = false;
    private Timer animationTimer;
    private Timer closeAnimationTimer;
    private final int FINAL_WIDTH = 520;
    private final int FINAL_HEIGHT = 480;

    private static boolean isShowingPopup = false;
    private int jumlah1 = 0;
    private String norfid;

    public PopUp_DashboardKaryawanStokOpname(JFrame parent) {
        this.parentFrame = parent;
        setModal(true);
        setPreferredSize(new Dimension(FINAL_WIDTH, FINAL_HEIGHT));
        setLayout(null);

        if (isShowingPopup) {

            dispose();
            return;
        }
        isShowingPopup = true;

        glassPane = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 180));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        glassPane.setOpaque(false);
        glassPane.setBounds(0, 0, parent.getWidth(), parent.getHeight());
        cleanupExistingGlassPane();
        parentLayeredPane().add(glassPane, JLayeredPane.POPUP_LAYER);

        setUndecorated(true);
        setSize(FINAL_WIDTH, FINAL_HEIGHT);
        setLocationRelativeTo(parent);
        setLayout(null);
        setBackground(new Color(0, 0, 0, 0));

        JPanel contentPanel = new RoundedPanel(RADIUS);
        contentPanel.setLayout(null);
        contentPanel.setBounds(0, 0, FINAL_WIDTH, FINAL_HEIGHT);
        contentPanel.setBackground(Color.WHITE);
        add(contentPanel);
        con = conn.getConnection();
        // Title
        JLabel titleLabel = createTextLabel("Stok Opname", 30, 20, 300, 30,
                new Font("Poppins", Font.BOLD, 22), Color.BLACK);
        contentPanel.add(titleLabel);

        String currentDate = "Tanggal: " + new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        JPanel tanggalPanel = new RoundedPanel(RADIUS);
        tanggalPanel.setBounds(FINAL_WIDTH - 180, 30, 150, 30);
        tanggalPanel.setBackground(new Color(226, 240, 255));
        tanggalPanel.setLayout(new BorderLayout());

        tanggalLabel = createTextLabel(currentDate, 0, 0, 120, 30,
                new Font("Poppins", Font.BOLD, 12), new Color(23, 78, 166));
        tanggalLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tanggalLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        tanggalPanel.add(tanggalLabel, BorderLayout.CENTER);
        contentPanel.add(tanggalPanel);

        // Barcode Input
        JLabel barcodeLabel = createTextLabel("Scan Barcode / Masukkan Kode", 30, 60, 300, 20,
                new Font("Poppins", Font.PLAIN, 14), new Color(100, 100, 100));
        contentPanel.add(barcodeLabel);

        barcodeField = createRoundTextField(30, 85, FINAL_WIDTH - 60, 35);
        addPlaceholder(barcodeField, "Gunakan Scanner atau Masukkan kode produk secara manual");
        contentPanel.add(barcodeField);
        barcodeField.addActionListener(e -> {
            checkBarcode();
        });

        // Separator
        JSeparator separator1 = new JSeparator();
        separator1.setBounds(30, 130, FINAL_WIDTH - 60, 1);
        separator1.setForeground(new Color(230, 230, 230));
        contentPanel.add(separator1);

        // Nama Produk
        JLabel namaLabel = createTextLabel("Nama Produk", 30, 140, 300, 20,
                new Font("Poppins", Font.PLAIN, 14), new Color(100, 100, 100));
        contentPanel.add(namaLabel);

        namaProdukLabel = createTextLabel("Nama produk akan muncul otomatis", 30, 165, FINAL_WIDTH - 60, 30,
                new Font("Poppins", Font.PLAIN, 14), Color.DARK_GRAY);
        namaProdukLabel.setBorder(new RoundBorder(Color.LIGHT_GRAY, RADIUS, 1));
        namaProdukLabel.setOpaque(true);
        namaProdukLabel.setBackground(new Color(250, 250, 250));
        contentPanel.add(namaProdukLabel);

        // Separator
        JSeparator separator2 = new JSeparator();
        separator2.setBounds(30, 205, FINAL_WIDTH - 60, 1);
        separator2.setForeground(new Color(230, 230, 230));
        contentPanel.add(separator2);

        // Jumlah Stok
        JLabel jumlahLabel = createTextLabel("Jumlah (real/terkini)", 30, 215, 300, 20,
                new Font("Poppins", Font.PLAIN, 14), new Color(100, 100, 100));
        contentPanel.add(jumlahLabel);

        jumlahField = createRoundTextField(30, 240, FINAL_WIDTH - 60, 35);
        addPlaceholder(jumlahField, "Masukkan jumlah stok produk aktual atau terkini");
        // Filter untuk hanya menerima angka
        ((PlainDocument) jumlahField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (string.matches("\\d*")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text.matches("\\d*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        contentPanel.add(jumlahField);

        // Separator
        JSeparator separator3 = new JSeparator();
        separator3.setBounds(30, 285, FINAL_WIDTH - 60, 1);
        separator3.setForeground(new Color(230, 230, 230));
        contentPanel.add(separator3);

        // Keterangan
        JLabel keteranganLabel = createTextLabel("Keterangan", 30, 295, 300, 20,
                new Font("Poppins", Font.PLAIN, 14), new Color(100, 100, 100));
        contentPanel.add(keteranganLabel);

        keteranganArea = new JTextArea();
        keteranganArea.setFont(new Font("Poppins", Font.PLAIN, 14));
        keteranganArea.setLineWrap(true);
        keteranganArea.setWrapStyleWord(true);
        keteranganArea.setBorder(new RoundBorder(Color.LIGHT_GRAY, RADIUS, 1));
        addPlaceholder(keteranganArea, "Tambahkan keterangan jika perlu");

        JScrollPane scrollPane = new JScrollPane(keteranganArea);
        scrollPane.setBounds(30, 320, FINAL_WIDTH - 60, 70);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        contentPanel.add(scrollPane);

        // Tombol
        batalButton = createRoundedButton("Batal", 30, 410, 120, 40,
                new Color(240, 240, 240), Color.BLACK);
        batalButton.addActionListener(e -> startCloseAnimation());
        contentPanel.add(batalButton);

        simpanButton = createRoundedButton("Simpan", FINAL_WIDTH - 160, 410, 120, 40,
                new Color(40, 190, 100), Color.WHITE);
        simpanButton.addActionListener(e -> {
            if (insertDataStok()) {
                startCloseAnimation();
            }
        });
        contentPanel.add(simpanButton);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanupAndClose();
            }
        });

        startScaleAnimation();
    }

    private void addPlaceholder(JTextComponent component, String placeholderText) {
        component.setText(placeholderText);
        component.setForeground(Color.GRAY);

        component.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (component.getText().equals(placeholderText)) {
                    // Untuk JTextField dengan DocumentFilter, kita perlu bypass filter sementara
                    if (component instanceof JTextField && ((JTextField) component).getDocument() instanceof PlainDocument) {
                        PlainDocument doc = (PlainDocument) ((JTextField) component).getDocument();
                        DocumentFilter filter = doc.getDocumentFilter();
                        doc.setDocumentFilter(null); // Nonaktifkan filter sementara
                        component.setText("");
                        doc.setDocumentFilter(filter); // Aktifkan kembali filter
                    } else {
                        component.setText("");
                    }
                    component.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (component.getText().isEmpty()) {
                    // Untuk JTextField dengan DocumentFilter, kita perlu bypass filter sementara
                    if (component instanceof JTextField && ((JTextField) component).getDocument() instanceof PlainDocument) {
                        PlainDocument doc = (PlainDocument) ((JTextField) component).getDocument();
                        DocumentFilter filter = doc.getDocumentFilter();
                        doc.setDocumentFilter(null); // Nonaktifkan filter sementara
                        component.setText(placeholderText);
                        doc.setDocumentFilter(filter); // Aktifkan kembali filter
                    } else {
                        component.setText(placeholderText);
                    }
                    component.setForeground(Color.GRAY);
                }
            }
        });
    }

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

        animationTimer = new Timer(ANIMATION_DELAY, e -> {
            currentFrame[0]++;
            float progress = (float) currentFrame[0] / totalFrames;
            float easedProgress = 1 - (1 - progress) * (1 - progress) * (1 - progress);
            currentScale = 0.01f + 0.99f * easedProgress;
            repaint();

            if (currentFrame[0] >= totalFrames) {
                animationTimer.stop();
                currentScale = 1.0f;
                repaint();
            }
        });
        animationTimer.start();
    }

    private void startCloseAnimation() {
        if (closeAnimationTimer != null && closeAnimationTimer.isRunning()) {
            closeAnimationTimer.stop();
        }

        final int totalFrames = ANIMATION_DURATION / ANIMATION_DELAY;
        final int[] currentFrame = {0};

        closeAnimationTimer = new Timer(ANIMATION_DELAY, e -> {
            currentFrame[0]++;
            float progress = (float) currentFrame[0] / totalFrames;
            float easedProgress = progress * progress;
            currentScale = 1.0f - 0.99f * easedProgress;
            repaint();

            if (currentFrame[0] >= totalFrames) {
                closeAnimationTimer.stop();
                cleanupAndClose();
            }
        });
        closeAnimationTimer.start();
    }

    private void cleanupAndClose() {

        isShowingPopup = false;
        closePopup();
        dispose();
    }

    private void closePopup() {
        parentLayeredPane().remove(glassPane);
        parentLayeredPane().repaint();
    }

    private JLayeredPane parentLayeredPane() {
        return parentFrame.getLayeredPane();
    }

    private JLabel createTextLabel(String text, int x, int y, int width, int height, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, width, height);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    private JTextField createRoundTextField(int x, int y, int width, int height) {
        JTextField textField = new JTextField();
        textField.setBounds(x, y, width, height);
        textField.setFont(new Font("Poppins", Font.PLAIN, 14));
        textField.setBorder(new RoundBorder(Color.LIGHT_GRAY, RADIUS, 1));
        textField.setBackground(Color.WHITE);
        return textField;
    }

    private JButton createRoundedButton(String text, int x, int y, int width, int height, Color bgColor, Color textColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bgColor.darker() : bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(200, 200, 200));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
            }
        };
        button.setBounds(x, y, width, height);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setForeground(textColor);
        button.setFont(new Font("Poppins", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    class RoundBorder extends AbstractBorder {

        private Color color;
        private int radius;
        private int thickness;

        public RoundBorder(Color color, int radius, int thickness) {
            this.color = color;
            this.radius = radius;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 8, 4, 8);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = 8;
            insets.top = insets.bottom = 4;
            return insets;
        }
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

                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
                g2.setTransform(originalTransform);
            } else {
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

    private void checkBarcode() {
        String kode = barcodeField.getText().trim();
        if (!kode.isEmpty() && !kode.equals("Gunakan Scanner atau Masukkan kode produk secara manual")) {
            try {
                String query = "SELECT nama_produk FROM produk WHERE id_produk = ?";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, kode);

                // 3. Eksekusi query
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String namaProduk = rs.getString("nama_produk");
                    SwingUtilities.invokeLater(() -> {
                        namaProdukLabel.setText(namaProduk);
                        namaProdukLabel.setForeground(Color.BLACK); // Ubah warna teks
                    });
                } else {
                    SwingUtilities.invokeLater(() -> {
                        PindahanAntarPopUp.showTransBeliKodeProdukTidakDitemukan(parentFrame);
                        namaProdukLabel.setText("Produk tidak ditemukan");
                        namaProdukLabel.setForeground(Color.RED);
                    });
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    namaProdukLabel.setText("Error saat mencari produk");
                    namaProdukLabel.setForeground(Color.RED);
                });
            }
        }
    }

    private boolean isNorfidValid(String norfid) throws SQLException {
        String query = "SELECT 1 FROM user WHERE norfid = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, norfid);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void getNoRfid() {

        String namaUser = LoginForm.getNamaUser();
        String query = "SELECT norfid FROM user WHERE email = ?";

        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, namaUser); 
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                norfid = rs.getString("norfid");
            } else {
                JOptionPane.showMessageDialog(this, "User not found in database");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }

  public boolean insertDataStok() {
    String sqlOpname = "INSERT INTO stok_opname (tanggal, norfid) VALUES (?, ?)";
    String sqlDetail = "INSERT INTO detail_opname (id_produk, id_opname, jumlah_produk, keterangan) VALUES (?, ?, ?, ?)";
    String sqlInsertKartuStok = "INSERT INTO kartu_stok (produk_masuk, produk_keluar, tanggal_transaksi, produk_sisa, id_produk) VALUES (?, ?, NOW(), ?, ?)";
    LocalDate today = LocalDate.now();

    String idProduk = barcodeField.getText();
    String angka = jumlahField.getText();
    String keterangan = keteranganArea.getText();

    if (idProduk.isEmpty() || angka.isEmpty() || keterangan.isEmpty()) {
        PindahanAntarPopUp.showEditProductFieldTidakBolehKosong(parentFrame);
        return false;
    }

    int jumlahProduk;
    try {
        jumlahProduk = Integer.parseInt(angka);
    } catch (NumberFormatException e) {
        PindahanAntarPopUp.showEditProductFieldTidakBolehKosong(parentFrame);
        return false;
    }

    getNoRfid();

    try (PreparedStatement stmt = con.prepareStatement(sqlOpname, Statement.RETURN_GENERATED_KEYS)) {
        stmt.setString(1, today.toString());
        stmt.setString(2, norfid);

        int rowsInserted = stmt.executeUpdate();

        if (rowsInserted > 0) {
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int idOpnameBaru = generatedKeys.getInt(1);

                try (PreparedStatement stmtDetail = con.prepareStatement(sqlDetail)) {
                    stmtDetail.setString(1, idProduk);
                    stmtDetail.setInt(2, idOpnameBaru);
                    stmtDetail.setInt(3, jumlahProduk);
                    stmtDetail.setString(4, keterangan);
                    stmtDetail.executeUpdate();
                }

                try (PreparedStatement stmtKartu = con.prepareStatement(sqlInsertKartuStok)) {
                    stmtKartu.setInt(1, jumlahProduk); // produk_masuk (hasil opname)
                    stmtKartu.setInt(2, 0);            // produk_keluar
                    stmtKartu.setInt(3, jumlahProduk); // produk_sisa = hasil opname
                    stmtKartu.setString(4, idProduk);  // id_produk
                    stmtKartu.executeUpdate();
                }

                PindahanAntarPopUp.showDashboardOwnerStokOpnameSuksesDitambahkan(parentFrame);
                return true;
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}
}
