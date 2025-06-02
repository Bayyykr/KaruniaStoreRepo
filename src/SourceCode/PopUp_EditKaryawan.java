package SourceCode;

import Form.AbsenKaryawan;
import Form.GajiKaryawan;
import Form.Productt;
import PopUp_all.PindahanAntarPopUp;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.sql.*;
import db.conn;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class PopUp_EditKaryawan extends javax.swing.JDialog {

    private float currentScale = 0.01f;
    private Timer enterAnimationTimer;
    private Timer closeAnimationTimer;
    private final int ANIMATION_DURATION = 300; // ms
    private final int ANIMATION_DELAY = 10; // ms
    private boolean animationStarted = false;
    private static boolean isShowingPopup = false;

    // Tambahkan variabel untuk overlay
    private JComponent glassPane;
    private JFrame parentFrame;

    // Tambahkan variabel untuk menyimpan komponen
    private JLabel titleLabel;
    // Pisahkan text field menjadi satu-satu
    private JTextField rfidField;
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField addressField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton batalButton;
    private JButton simpanButton;
    private JPanel buttonPanel;
    private Connection con;
    public boolean wasDataUpdated = false;
    private String norfid;

    public PopUp_EditKaryawan(java.awt.Frame parent, boolean modal, String norfid) {
        super(parent, modal);
        this.parentFrame = (JFrame) parent;
        this.norfid = norfid;

        con = conn.getConnection();

        // Periksa apakah popup sudah ditampilkan
        if (isShowingPopup) {
            dispose();
            return;
        }
        isShowingPopup = true;

        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

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

        initComponents();
        setTitle("Edit Karyawan");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        centerOnScreen();

        // Tambahkan WindowListener untuk membersihkan saat popup ditutup
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanupAndClose();
            }
        });

        showData();
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

    private JLayeredPane parentLayeredPane() {
        return parentFrame.getLayeredPane();
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

    private void centerOnScreen() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - getWidth()) / 2;
        int y = (screenSize.height - getHeight()) / 2;
        setLocation(x, y);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;

                g2.translate(centerX, centerY);
                g2.scale(currentScale, currentScale);
                g2.translate(-centerX, -centerY);

                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintChildren(Graphics g) {
                if (currentScale < 0.4) {
                    return;
                }

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;

                g2.translate(centerX, centerY);
                g2.scale(currentScale, currentScale);
                g2.translate(-centerX, -centerY);

                super.paintChildren(g2);
                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title Label
        titleLabel = new JLabel("Edit Karyawan");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);

        // Buat label untuk setiap field
        JLabel rfidLabel = new JLabel("No. RFID");
        JLabel nameLabel = new JLabel("Full Name");
        JLabel emailLabel = new JLabel("Email");
        JLabel passwordLabel = new JLabel("Password");
        JLabel phoneLabel = new JLabel("Nomor Telepon");
        JLabel addressLabel = new JLabel("Alamat");

        // Set font untuk label
        Font labelFont = new Font("Arial", Font.PLAIN, 14);
        rfidLabel.setFont(labelFont);
        nameLabel.setFont(labelFont);
        emailLabel.setFont(labelFont);
        passwordLabel.setFont(labelFont);
        phoneLabel.setFont(labelFont);
        addressLabel.setFont(labelFont);

        // Inisialisasi setiap text field tanpa placeholder
        rfidField = createRoundedTextField("");
        nameField = createRoundedTextField("");
        emailField = createRoundedTextField("");
        passwordField = createRoundedPasswordField("");
        phoneField = createRoundedTextField("");
        addressField = createRoundedTextField("");

        // Customize field width
        Dimension fieldDimension = new Dimension(300, 40);

        // Set dimensi untuk setiap field
        rfidField.setPreferredSize(fieldDimension);
        rfidField.setMaximumSize(fieldDimension);
        rfidField.setMinimumSize(fieldDimension);
        rfidField.setEditable(false);
        rfidField.setFocusable(false);

        nameField.setPreferredSize(fieldDimension);
        nameField.setMaximumSize(fieldDimension);
        nameField.setMinimumSize(fieldDimension);

        emailField.setPreferredSize(fieldDimension);
        emailField.setMaximumSize(fieldDimension);
        emailField.setMinimumSize(fieldDimension);

        passwordField.setPreferredSize(fieldDimension);
        passwordField.setMaximumSize(fieldDimension);
        passwordField.setMinimumSize(fieldDimension);

        phoneField.setPreferredSize(fieldDimension);
        phoneField.setMaximumSize(fieldDimension);
        phoneField.setMinimumSize(fieldDimension);

        addressField.setPreferredSize(fieldDimension);
        addressField.setMaximumSize(fieldDimension);
        addressField.setMinimumSize(fieldDimension);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 0, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Tambah title ke main panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 8, 16, 8);
        mainPanel.add(titleLabel, gbc);

        // RFID Label dan Field
        gbc.gridy = 1;
        gbc.insets = new Insets(4, 8, 0, 8);
        mainPanel.add(rfidLabel, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(2, 8, 8, 8);
        mainPanel.add(rfidField, gbc);

        // Name Label dan Field
        gbc.gridy = 3;
        gbc.insets = new Insets(4, 8, 0, 8);
        mainPanel.add(nameLabel, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(2, 8, 8, 8);
        mainPanel.add(nameField, gbc);

        // Email Label dan Field
        gbc.gridy = 5;
        gbc.insets = new Insets(4, 8, 0, 8);
        mainPanel.add(emailLabel, gbc);

        gbc.gridy = 6;
        gbc.insets = new Insets(2, 8, 8, 8);
        mainPanel.add(emailField, gbc);

        // Password Label dan Field
        gbc.gridy = 7;
        gbc.insets = new Insets(4, 8, 0, 8);
        mainPanel.add(passwordLabel, gbc);

        gbc.gridy = 8;
        gbc.insets = new Insets(2, 8, 8, 8);
        mainPanel.add(passwordField, gbc);

        // Phone Label dan Field
        gbc.gridy = 9;
        gbc.insets = new Insets(4, 8, 0, 8);
        mainPanel.add(phoneLabel, gbc);

        gbc.gridy = 10;
        gbc.insets = new Insets(2, 8, 8, 8);
        mainPanel.add(phoneField, gbc);

        // Address Label dan Field
        gbc.gridy = 11;
        gbc.insets = new Insets(4, 8, 0, 8);
        mainPanel.add(addressLabel, gbc);

        gbc.gridy = 12;
        gbc.insets = new Insets(2, 8, 8, 8);
        mainPanel.add(addressField, gbc);

        // Buttons
        batalButton = createRoundedButton("Batal", Color.RED);
        simpanButton = createRoundedButton("Simpan", Color.GREEN);

        // Button panel
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
        buttonPanel.add(batalButton);
        buttonPanel.add(Box.createHorizontalStrut(160));
        buttonPanel.add(simpanButton);

        // Tambah button panel ke main panel
        gbc.gridx = 0;
        gbc.gridy = 13;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 8, 0, 8);
        mainPanel.add(buttonPanel, gbc);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        simpanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String no = rfidField.getText();
                String nama = nameField.getText();
                String email = emailField.getText().trim();
                String pw = passwordField.getText();
                String notelp = phoneField.getText();
                String alamat = addressField.getText();
                
                if ( nama.equals("Full Name") || email.equals("Email")
                        || pw.equals("Password") || notelp.equals("Nomor Telepon") || alamat.equals("Alamat")
                        || no.isEmpty() || nama.isEmpty() || email.isEmpty() || pw.isEmpty() || notelp.isEmpty() || alamat.isEmpty()) {
                    PindahanAntarPopUp.showTambahKaryawanTIdakBolehKosong(parentFrame);
                    System.out.println("data tidak boleh kosong");
                    return;
                }

                if (nama.length() > 30) {
                    PindahanAntarPopUp.showEditDataKaryawanNamaTidakLebihDari30karakter(parentFrame);
                    nameField.requestFocus();
                    return;
                }

                if (email.length() > 30) {
                    PindahanAntarPopUp.showEditDataKaryawanEmailTidakLebihDari30karakter(parentFrame);
                    emailField.requestFocus();
                    return;
                }

                if (pw.length() > 20) {
                    PindahanAntarPopUp.showEditDataKaryawanPassswordTidakLebihDari20karakter(parentFrame);
                    passwordField.requestFocus();
                    return;
                }
                
                if (!notelp.matches("\\d{12,13}")) {
                    PindahanAntarPopUp.showTambahKaryawanNomorTeleponTidakValid(parentFrame);
                    phoneField.requestFocus();
                    return;
                }

//                if (!notelp.matches("\\d+")) {
//                    PindahanAntarPopUp.showEditDataKaryawanNoTlpHarusBerupaAngka(parentFrame);
//                    phoneField.requestFocus();
//                    return;
//                }

//                if (notelp.length() > 13) {
//                    PindahanAntarPopUp.showEditDataKaryawanPassswordTidakLebihDari20karakter(parentFrame);
//                    phoneField.requestFocus();
//                    return;
//                }

                if (!email.contains("@")) {
                    PindahanAntarPopUp.showTambahKaryawanEmailHarusBenarPenulisannya(parentFrame);
                    emailField.requestFocus();
                    return;
                }
                String[] emailParts = email.split("@");
                if (emailParts.length != 2) {
                    PindahanAntarPopUp.showTambahKaryawanEmailHarusBenarPenulisannya(parentFrame);
                    emailField.requestFocus();
                    return;
                }
                String domain = emailParts[1];
                if (!domain.equals("gmail.com")) {
                    PindahanAntarPopUp.showTambahKaryawanEmailHarusBenarPenulisannya(parentFrame);
                    emailField.requestFocus();
                    return;
                }

                try {
                    String query = "UPDATE user SET norfid = ?, nama_user = ?, email = ?, "
                            + "password = ?, alamat = ?, no_hp = ?, "
                            + "jabatan = 'kasir', status = 'aktif' WHERE norfid = ?";
                    try (PreparedStatement st = con.prepareStatement(query)) {
                        st.setString(1, no);
                        st.setString(2, nama);
                        st.setString(3, email);
                        st.setString(4, pw);
                        st.setString(5, alamat);
                        st.setString(6, notelp);
                        st.setString(7, norfid);

                        int rowUpdate = st.executeUpdate();
                        if (rowUpdate > 0) {
                            wasDataUpdated = true;
                            PindahanAntarPopUp.showEditKaryawanSuksesDiEdit(parentFrame);
                            Productt mainFrame = Productt.getMainFrame();
                            AbsenKaryawan absen = mainFrame.getAbsenPanel();
                            if (absen != null) {
                                absen.refreshData();
                            }
                            
                            GajiKaryawan gaji = mainFrame.getGajiPanel();
                            if (gaji != null) {
                                gaji.refreshData();
                            }
                            startCloseAnimation();
                        }
                    }
                } catch (Exception ee) {
                    System.err.println("Error retrieving employee data: " + ee.getMessage());
                }
            }
        });
        batalButton.addActionListener(e -> startCloseAnimation());

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                requestFocusInWindow(false);
            }
        });

        pack();
        centerOnScreen();
        setSize(400, 580); // Sedikit lebih tinggi untuk akomodasi label tambahan
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 25, 25));

        SwingUtilities.invokeLater(this::startEnterAnimation);
    }

    private void startEnterAnimation() {
        if (animationStarted) {
            return;
        }

        animationStarted = true;
        currentScale = 0.01f;

        final int totalFrames = ANIMATION_DURATION / ANIMATION_DELAY;
        final int[] currentFrame = {0};

        enterAnimationTimer = new Timer(ANIMATION_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentFrame[0]++;

                float progress = (float) currentFrame[0] / totalFrames;
                float easedProgress = 1 - (1 - progress) * (1 - progress) * (1 - progress);

                currentScale = 0.01f + 0.99f * easedProgress;

                // Pastikan semua komponen terlihat
                if (progress >= 0.4) {
                    // Tampilkan semua komponen termasuk label baru
                    for (Component comp : getContentPane().getComponents()) {
                        if (comp instanceof JPanel) {
                            JPanel panel = (JPanel) comp;
                            for (Component innerComp : panel.getComponents()) {
                                innerComp.setVisible(true);
                            }
                        }
                    }
                }

                repaint();

                if (currentFrame[0] >= totalFrames) {
                    enterAnimationTimer.stop();
                    currentScale = 1.0f;
                    repaint();
                }
            }
        });

        enterAnimationTimer.start();
    }

    private void startCloseAnimation() {
        if (closeAnimationTimer != null && closeAnimationTimer.isRunning()) {
            return;
        }

        final int totalFrames = ANIMATION_DURATION / ANIMATION_DELAY;
        final int[] currentFrame = {0};

        closeAnimationTimer = new Timer(ANIMATION_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentFrame[0]++;

                float progress = (float) currentFrame[0] / totalFrames;
                float easedProgress = progress * progress;

                currentScale = 1.0f - 0.99f * easedProgress;

                repaint();

                if (currentFrame[0] >= totalFrames) {
                    closeAnimationTimer.stop();
                    cleanupAndClose();
                }
            }
        });

        closeAnimationTimer.start();
    }

    private JTextField createRoundedTextField(String text) {
        JTextField textField = new JTextField(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Create white background
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        // Custom border with extra left padding
        Border roundBorder = new RoundedBorder(10, new Color(220, 220, 220), 1);
        Border paddingBorder = BorderFactory.createEmptyBorder(0, 10, 0, 0);
        textField.setBorder(BorderFactory.createCompoundBorder(roundBorder, paddingBorder));

        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setForeground(Color.BLACK);
        textField.setOpaque(false);

        return textField;
    }

    private JPasswordField createRoundedPasswordField(String text) {
        JPasswordField passwordField = new JPasswordField(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Create white background
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        Border roundBorder = new RoundedBorder(10, new Color(220, 220, 220), 1);
        Border paddingBorder = BorderFactory.createEmptyBorder(0, 10, 0, 15);
        passwordField.setBorder(BorderFactory.createCompoundBorder(roundBorder, paddingBorder));

        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setForeground(Color.BLACK);
        passwordField.setOpaque(false);
        passwordField.setEchoChar('•');

        JButton toggleButton = new JButton();
        toggleButton.setOpaque(false);
        toggleButton.setContentAreaFilled(false);
        toggleButton.setBorderPainted(false);
        toggleButton.setFocusPainted(false);
        toggleButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toggleButton.setMargin(new Insets(0, 0, 0, 0));
        toggleButton.setPreferredSize(new Dimension(30, 30));
        toggleButton.setBounds(passwordField.getWidth() - 35, 5, 30, 30);

        try {
            ImageIcon lockIcon = new ImageIcon(getClass().getResource("/SourceImage/lock.png"));
            ImageIcon unlockIcon = new ImageIcon(getClass().getResource("/SourceImage/unlock.png"));

            Image lockImg = lockIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            Image unlockImg = unlockIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);

            toggleButton.setIcon(new ImageIcon(lockImg));

            toggleButton.addActionListener(e -> {
                if (passwordField.getEchoChar() == '•') {
                    passwordField.setEchoChar((char) 0);
                    toggleButton.setIcon(new ImageIcon(unlockImg));
                } else {
                    passwordField.setEchoChar('•');
                    toggleButton.setIcon(new ImageIcon(lockImg));
                }
            });
        } catch (Exception e) {
        }
        passwordField.setLayout(new BorderLayout());
        passwordField.add(toggleButton, BorderLayout.EAST);
        passwordField.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                toggleButton.setBounds(passwordField.getWidth() - 35,
                        (passwordField.getHeight() - 30) / 2,
                        30, 30);
            }
        });

        return passwordField;
    }

    private JButton createRoundedButton(String text, Color bgColor) {
        JButton button = new JButton(text);

        // Set the custom UI
        button.setUI(new RoundedButton());

        // Set background color
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);

        // Styling
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setContentAreaFilled(false);
        button.setOpaque(false);

        return button;
    }

    private void addDragListeners(JPanel panel) {
        final Point[] mouseDownCompCoords = new Point[1];
        panel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouseDownCompCoords[0] = e.getPoint();
            }
        });
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point currCoords = e.getLocationOnScreen();
                setLocation(currCoords.x - mouseDownCompCoords[0].x,
                        currCoords.y - mouseDownCompCoords[0].y);
            }
        });
    }

    @Override
    public void dispose() {
        if (enterAnimationTimer != null) {
            enterAnimationTimer.stop();
        }
        if (closeAnimationTimer != null) {
            closeAnimationTimer.stop();
        }

        // Remove direct call to cleanupAndClose()
        isShowingPopup = false;

        // Remove glassPane
        if (parentFrame != null && glassPane != null) {
            parentFrame.getLayeredPane().remove(glassPane);
            parentFrame.getLayeredPane().repaint();
        }

        super.dispose();
    }

    private void showData() {
        try {
            String query = "SELECT * FROM user WHERE norfid = ?";
            try (PreparedStatement st = con.prepareStatement(query)) {
                st.setString(1, norfid);
                ResultSet rs = st.executeQuery();

                if (rs.next()) {
                    rfidField.setText(rs.getString("norfid"));
                    nameField.setText(rs.getString("nama_user"));
                    emailField.setText(rs.getString("email"));
                    passwordField.setText(rs.getString("password"));
                    phoneField.setText(rs.getString("no_hp"));
                    addressField.setText(rs.getString("alamat"));
                }
            }
        } catch (Exception ee) {
            System.err.println("error show data" + ee.getMessage());
        }
    }

    // Untuk kelas RoundedButton dan RoundedBorder, kita perlu juga sertakan di sini
    class RoundedButton extends javax.swing.plaf.basic.BasicButtonUI {

        @Override
        public void installUI(JComponent c) {
            super.installUI(c);
            AbstractButton button = (AbstractButton) c;
            button.setOpaque(false);
            button.setBorderPainted(false);
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            AbstractButton b = (AbstractButton) c;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = b.getWidth();
            int height = b.getHeight();

            // Paint background
            g2.setColor(b.getBackground());
            g2.fillRoundRect(0, 0, width, height, 15, 15);

            // Tambahkan border hitam di sini
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1));
            g2.drawRoundRect(0, 0, width - 1, height - 1, 15, 15);

            super.paint(g, c);
            g2.dispose();
        }
    }

    class RoundedBorder extends AbstractBorder {

        private int radius;
        private Color color;
        private int thickness;

        public RoundedBorder(int radius, Color color, int thickness) {
            this.radius = radius;
            this.color = color;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness, thickness, thickness, thickness);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.top = insets.right = insets.bottom = thickness;
            return insets;
        }
    }
}
