package SourceCode;

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

public class TambahKaryawan extends javax.swing.JDialog {

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
    public boolean wasDataAdded = false;

    public TambahKaryawan(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.parentFrame = (JFrame) parent;

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
        setTitle("Tambah Karyawan");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        centerOnScreen();

        // Tambahkan WindowListener untuk membersihkan saat popup ditutup
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanupAndClose();
            }
        });

        TambahData();
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
        titleLabel = new JLabel("Tambah Karyawan");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);

        // Inisialisasi setiap text field secara individual
        rfidField = createRoundedTextField("No. RFID");
        nameField = createRoundedTextField("Full Name");
        phoneField = createRoundedTextField("Email");
        passwordField = createRoundedPasswordField("Password");
        emailField = createRoundedTextField("Nomor Telepon");
        addressField = createRoundedTextField("Alamat");

        // Customize field width
        Dimension fieldDimension = new Dimension(300, 40);

        // Set dimensi untuk setiap field
        rfidField.setPreferredSize(fieldDimension);
        rfidField.setMaximumSize(fieldDimension);
        rfidField.setMinimumSize(fieldDimension);

        nameField.setPreferredSize(fieldDimension);
        nameField.setMaximumSize(fieldDimension);
        nameField.setMinimumSize(fieldDimension);

        phoneField.setPreferredSize(fieldDimension);
        phoneField.setMaximumSize(fieldDimension);
        phoneField.setMinimumSize(fieldDimension);

        addressField.setPreferredSize(fieldDimension);
        addressField.setMaximumSize(fieldDimension);
        addressField.setMinimumSize(fieldDimension);

        emailField.setPreferredSize(fieldDimension);
        emailField.setMaximumSize(fieldDimension);
        emailField.setMinimumSize(fieldDimension);

        passwordField.setPreferredSize(fieldDimension);
        passwordField.setMaximumSize(fieldDimension);
        passwordField.setMinimumSize(fieldDimension);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Tambah title ke main panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 8, 10, 8);
        mainPanel.add(titleLabel, gbc);

        // Tambah fields satu per satu
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 8, 8, 8);
        mainPanel.add(rfidField, gbc);

        gbc.gridy = 2;
        mainPanel.add(nameField, gbc);

        gbc.gridy = 4;
        mainPanel.add(phoneField, gbc);

        gbc.gridy = 5;
        mainPanel.add(passwordField, gbc);

        gbc.gridy = 6;
        mainPanel.add(emailField, gbc);

        gbc.gridy = 7;
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
        gbc.gridy = 8;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 8, 0, 8);
        mainPanel.add(buttonPanel, gbc);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        addDragListeners(mainPanel);

        batalButton.addActionListener(e -> startCloseAnimation());
//        simpanButton.addActionListener(e -> {
//            JOptionPane.showMessageDialog(this, "Data Tersimpan", "Konfirmasi", JOptionPane.INFORMATION_MESSAGE);
//            startCloseAnimation();
//        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                requestFocusInWindow(false);
            }
        });

        pack();
        centerOnScreen();
        setSize(400, 480);
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
                    titleLabel.setVisible(true);
                    rfidField.setVisible(true);
                    nameField.setVisible(true);
                    phoneField.setVisible(true);
                    addressField.setVisible(true);
                    emailField.setVisible(true);
                    passwordField.setVisible(true);
                    batalButton.setVisible(true);
                    simpanButton.setVisible(true);
                    buttonPanel.setVisible(true);
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

    private JTextField createRoundedTextField(String placeholder) {
        JTextField textField = new JTextField(placeholder) {
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
        textField.setForeground(Color.GRAY);
        textField.setOpaque(false);

        // Add focus listeners to handle placeholder behavior
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
                }
            }
        });

        return textField;
    }

    private JPasswordField createRoundedPasswordField(String placeholder) {
        JPasswordField passwordField = new JPasswordField(placeholder) {
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
        passwordField.setBorder(BorderFactory.createCompoundBorder(roundBorder, paddingBorder));

        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setForeground(Color.GRAY);
        passwordField.setOpaque(false);
        passwordField.setEchoChar((char) 0);

        // Add focus listeners to handle placeholder behavior
        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(passwordField.getPassword()).equals(placeholder)) {
                    passwordField.setText("");
                    passwordField.setForeground(Color.BLACK);
                    passwordField.setEchoChar('•');
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (String.valueOf(passwordField.getPassword()).isEmpty()) {
                    passwordField.setText(placeholder);
                    passwordField.setForeground(Color.GRAY);
                    passwordField.setEchoChar((char) 0);
                }
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

    private void TambahData() {

        simpanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String no = rfidField.getText();
                String nama = nameField.getText();
                String email = emailField.getText();
                String pw = passwordField.getText();
                String notelp = phoneField.getText();
                String alamat = addressField.getText();

                if (no.equals("No. RFID") || nama.equals("Full Name") || email.equals("Email")
                        || pw.equals("Password") || notelp.equals("Nomor Telepon") || alamat.equals("Alamat")
                        || no.isEmpty() || nama.isEmpty() || email.isEmpty() || pw.isEmpty() || notelp.isEmpty() || alamat.isEmpty()) {
                    System.out.println("data tidak boleh kosong");
                    return;
                }

                try {
                    String query = "INSERT INTO user (norfid, nama_user, email, password, alamat, no_hp, jabatan, status)"
                            + "VALUES (?, ?, ?, ?, ?, ?, 'kasir', 'aktif')";
                    try (PreparedStatement st = con.prepareStatement(query)) {
                        st.setString(1, no);
                        st.setString(2, nama);
                        st.setString(3, email);
                        st.setString(4, pw);
                        st.setString(5, alamat);
                        st.setString(6, notelp);

                        int rowInserted = st.executeUpdate();
                        if (rowInserted > 0) {
                            wasDataAdded = true;
                            startCloseAnimation();
                        }
                    }
                } catch (Exception ee) {
                    System.err.println("Error retrieving employee data: " + ee.getMessage());
                }
            }
        });
    }
}
