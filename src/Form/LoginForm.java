/*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Form;

/**
 *
 * @author HP
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import SourceCode.RoundedPanel;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import SourceCode.PopUpRFID;
import SourceCode.RoundedBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.sql.*;
import db.conn;
import PopUp_all.*;
import SourceCode.PopUpRFIDWarning;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginForm extends JFrame {

    private JPanel mainPanel, imagePanel;
    private JButton loginButton, clickHereButton;
    private JTextField usernameField, rfidField;
    private JPasswordField passwordField;
    private JLabel forgotPassword;
    int xMouse, yMouse;

    private Connection con;
    private String jabatan = "";
    private static LoginForm instance;
    private static String namaUser = "";
    private static String NoRFID = "";

    public static String getNoRFID() {
        return NoRFID;
    }

    public static void setNoRFID(String NoRFID) {
        LoginForm.NoRFID = NoRFID;
    }

    public static String getNamaUser() {
        return namaUser;
    }

    private static void setNamaUser(String namaUser) {
        LoginForm.namaUser = namaUser;
    }

    public LoginForm() {
        con = conn.getConnection();
        initComponents();
        RFIDField();
        FungsiKomponenLogin();
    }
    
    public static LoginForm getInstance() {
        if (instance == null) {
            instance = new LoginForm();
        }
        return instance;
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setSize(945, 570);
        setBackground(new Color(0, 0, 0, 0));

        mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(new Color(40, 40, 40));

        // Create a separate panel for the image
        imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Load and draw the shoe image
                ImageIcon imageIcon = new ImageIcon(getClass().getResource("/SourceImage/Login_image.png"));
                Image img = imageIcon.getImage();

                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw the image to fill the panel
                g2d.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };
        imagePanel.setOpaque(false);
        imagePanel.setBounds(-10, 25, 530, 530); // Posisi dan ukuran panel gambar

        // Login panel with rounded corners
        RoundedPanel loginPanel = new RoundedPanel();
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setBounds(410, 0, 535, 570);
        loginPanel.setLayout(null);

        mainPanel.setComponentZOrder(imagePanel, 0);  // Set image panel to front
        mainPanel.setComponentZOrder(loginPanel, 1);

        // Close button
        JLabel closeButton = new JLabel("×");
        closeButton.setFont(new Font("Arial", Font.BOLD, 24));
        closeButton.setForeground(new Color(51, 51, 51));
        closeButton.setHorizontalAlignment(SwingConstants.CENTER);
        closeButton.setBounds(500, 5, 30, 30);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        closeButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                closeButton.setForeground(new Color(255, 70, 70));
            }

            public void mouseExited(MouseEvent e) {
                closeButton.setForeground(new Color(51, 51, 51));
            }

            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }
        });

        // Login components
        JLabel titleLabel = new JLabel("Sign In / Login");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setBounds(50, 100, 435, 40);
        //        titleLabel.setBackground(new Color(100, 100, 100));
        //        titleLabel.setOpaque(true);
        //        titleLabel.setBorder(new EmptyBorder(0, 0, 22, 0));

        JLabel rfidLabel = new JLabel("Please tap your RFID card to log in");
        rfidLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        rfidLabel.setForeground(Color.GRAY);
        rfidLabel.setBounds(50, 150, 340, 30);
        //        rfidLabel.setBackground(new Color(245, 245, 245));
        //         rfidLabel.setOpaque(true);
        rfidLabel.setHorizontalAlignment(SwingConstants.LEFT);
        //        rfidLabel.setBorder(new EmptyBorder(0, 0, 18, 0));

        clickHereButton = new JButton("Click Here") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw shadow
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 4, 10, 10);

                // Draw stronger shadow
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillRoundRect(2, 3, getWidth() - 4, getHeight() - 4, 10, 10);

                // Draw button background
                g2d.setColor(new Color(40, 40, 40));
                g2d.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 10, 10);

                // Draw text
                FontMetrics metrics = g2d.getFontMetrics(getFont());
                int x = (getWidth() - metrics.stringWidth(getText())) / 2;
                int y = ((getHeight() - 4) - metrics.getHeight()) / 2 + metrics.getAscent();
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                g2d.drawString(getText(), x, y);

                g2d.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                // No border needed
            }

            @Override
            public Dimension getPreferredSize() {
                Dimension size = super.getPreferredSize();
                size.width += 4; // Add extra space for shadow
                size.height += 4;
                return size;
            }
        };
        clickHereButton.setBounds(390, 150, 100, 30);
        clickHereButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        clickHereButton.setBorderPainted(false);
        clickHereButton.setContentAreaFilled(false);
        clickHereButton.setFocusPainted(false);
        clickHereButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Enhanced hover effect with shadow adjustments
        clickHereButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Slightly move the button down when pressed
                clickHereButton.setLocation(clickHereButton.getX(), clickHereButton.getY() + 1);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // Move the button back up when released
                clickHereButton.setLocation(clickHereButton.getX(), clickHereButton.getY() - 1);
            }
        });

        // Username field
        usernameField = new JTextField();
        usernameField.setBounds(50, 210, 435, 50);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        //        usernameField.setForeground(new Color(128, 128, 128));
        usernameField.setBackground(Color.WHITE);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 10)
        ));
        //        usernameField.setBorder(new EmptyBorder(0, 0, 22, 0));
        usernameField.setText("Email");

        JPanel passwordPanel = new JPanel(null); // using null layout
        passwordPanel.setBounds(50, 280, 435, 50);
        passwordPanel.setBackground(Color.WHITE);
        passwordPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Adjust password field to fill the panel except for icon space
        passwordField = new JPasswordField();
        passwordField.setBounds(18, 5, 370, 40);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        //        passwordField.setForeground(new Color(128, 128, 128));
        passwordField.setBackground(Color.WHITE);
        passwordField.setBorder(null); // Remove internal border
        passwordField.setText("Password");
        passwordField.setEchoChar((char) 0);

        // Create toggle button for password visibility
        JToggleButton showPasswordButton = new JToggleButton() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Load and draw the appropriate icon
                ImageIcon icon;
                if (isSelected()) {
                    icon = new ImageIcon(getClass().getResource("/SourceImage/unlock.png"));
                } else {
                    icon = new ImageIcon(getClass().getResource("/SourceImage/lock.png"));
                }

                if (icon != null) {
                    Image img = icon.getImage();
                    // Scale the icon to fit the button while maintaining aspect ratio
                    int iconSize = 20; // or whatever size you prefer
                    g2d.drawImage(img, (getWidth() - iconSize) / 2, (getHeight() - iconSize) / 2,
                            iconSize, iconSize, this);
                }
            }
        };
        showPasswordButton.setBounds(395, 10, 30, 30);
        showPasswordButton.setBackground(null);
        showPasswordButton.setBorder(null);
        showPasswordButton.setFocusPainted(false);
        showPasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        showPasswordButton.setContentAreaFilled(false);

        // Add action listener to toggle password visibility
        showPasswordButton.addActionListener(e -> {
            if (showPasswordButton.isSelected()) {
                passwordField.setEchoChar((char) 0); // Show password
            } else {
                if (!String.valueOf(passwordField.getPassword()).equals("Password")) {
                    passwordField.setEchoChar('•'); // Hide password with bullets
                }
            }
        });

        // Modify the password field focus listener
        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(passwordField.getPassword()).equals("Password")) {
                    passwordField.setText("");
                    if (!showPasswordButton.isSelected()) {
                        passwordField.setEchoChar('•');
                    }
                }
                passwordPanel.setBorder(BorderFactory.createCompoundBorder(
                        new RoundedBorder(15, new Color(200, 200, 200), 1),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (String.valueOf(passwordField.getPassword()).isEmpty()) {
                    passwordField.setEchoChar((char) 0);
                    passwordField.setText("Password");
                }
                passwordPanel.setBorder(BorderFactory.createCompoundBorder(
                        new RoundedBorder(15, new Color(200, 200, 200), 1),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }
        });

        // Add components to password panel
        passwordPanel.add(passwordField);
        passwordPanel.add(showPasswordButton);

        // Add focus listeners for both fields
        FocusListener fieldFocusListener = new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                JTextField field = (JTextField) e.getComponent();
                //                field.setForeground(new Color(128, 128, 128));
                field.setBorder(BorderFactory.createCompoundBorder(
                        new RoundedBorder(15, new Color(200, 200, 200), 1),
                        BorderFactory.createEmptyBorder(5, 17, 5, 10)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                JTextField field = (JTextField) e.getComponent();
                //                field.setForeground(new Color(128, 128, 128));
                field.setBorder(BorderFactory.createCompoundBorder(
                        new RoundedBorder(15, new Color(200, 200, 200), 1),
                        BorderFactory.createEmptyBorder(5, 17, 5, 10)
                ));
            }
        };

        usernameField.addFocusListener(fieldFocusListener);
        //        passwordField.addFocusListener(fieldFocusListener);

        // Add placeholder for username field
        usernameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (usernameField.getText().equals("Email")) {
                    usernameField.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (usernameField.getText().isEmpty()) {
                    usernameField.setText("Email");
                }
            }
        });

        forgotPassword = new JLabel("Forgot Password");
        forgotPassword.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        forgotPassword.setForeground(new Color(0, 123, 255));
        forgotPassword.setBounds(350, 350, 150, 25);
        forgotPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        //        forgotPassword.setBackground(new Color(100, 100, 100));
        //        forgotPassword.setOpaque(true);
        forgotPassword.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Dapatkan parent frame dari komponen forgotPassword
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(forgotPassword);

                // Buat dan tampilkan popup
                PopUp_LupaPasswordLogin popUpLupaPassword = new PopUp_LupaPasswordLogin(parentFrame);
                popUpLupaPassword.setVisible(true);
            }
        });
        loginButton = new JButton("LOGIN") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw shadow
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillRoundRect(2, 4, getWidth(), getHeight() - 3, 20, 30);

                // Draw button background
                g2d.setColor(new Color(40, 40, 40));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight() - 3, 20, 20);

                // Draw text
                FontMetrics metrics = g2d.getFontMetrics(getFont());
                int x = (getWidth() - metrics.stringWidth(getText())) / 2;
                int y = ((getHeight() - 3) - metrics.getHeight()) / 2 + metrics.getAscent();
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                g2d.drawString(getText(), x, y);

                g2d.dispose();
            }
        };
        loginButton.setBounds(50, 400, 435, 45);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBorderPainted(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Slightly move the button down when pressed
                loginButton.setLocation(loginButton.getX(), loginButton.getY() + 1);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // Move the button back up when released
                loginButton.setLocation(loginButton.getX(), loginButton.getY() - 1);
            }
        });

        MouseAdapter linkHoverAdapter = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                ((JLabel) e.getComponent()).setForeground(new Color(0, 86, 179));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                ((JLabel) e.getComponent()).setForeground(new Color(0, 123, 255));
            }
        };

        forgotPassword.addMouseListener(linkHoverAdapter);

        //RFID textfield
        //        rfidField = new JTextField();
        //        rfidField.setBounds(-100, -100, 200, 50); 
        //        rfidField.setVisible(true);
        // Add components to login panel
        loginPanel.add(closeButton);
        loginPanel.add(titleLabel);
        loginPanel.add(rfidLabel);
        //        loginPanel.add(shadowPanel);
        loginPanel.add(usernameField);
        //        loginPanel.add(passwordField);
        loginPanel.add(passwordPanel);
//        loginPanel.add(rememberMe);
        loginPanel.add(forgotPassword);
        loginPanel.add(loginButton);
        //        loginPanel.add(rfidField);
        loginPanel.add(clickHereButton);

        // Add panels to frame
        mainPanel.add(loginPanel);
        add(mainPanel);

        // Make window draggable
//        mainPanel.addMouseListener(new MouseAdapter() {
//            public void mousePressed(MouseEvent e) {
//                xMouse = e.getX();
//                yMouse = e.getY();
//            }
//        });
//
//        mainPanel.addMouseMotionListener(new MouseMotionAdapter() {
//            public void mouseDragged(MouseEvent e) {
//                int x = e.getXOnScreen() - xMouse;
//                int y = e.getYOnScreen() - yMouse;
//                setLocation(x, y);
//            }
//        });

        setLocationRelativeTo(null);
    }

    private void RFIDField() {
        clickHereButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(LoginForm.this);
                PopUpRFID dialog = new PopUpRFID(parentFrame);

                // Configure the text field
                rfidField = dialog.getRfidTextField();
                rfidField.setEditable(true);
                rfidField.setFocusable(true);

                // Add document listener for better text handling
                rfidField.getDocument().addDocumentListener(new DocumentListener() {
                    public void changedUpdate(DocumentEvent e) {
                        processInput();
                    }

                    public void removeUpdate(DocumentEvent e) {
                        processInput();
                    }

                    public void insertUpdate(DocumentEvent e) {
                        processInput();
                    }

                    private void processInput() {
                        String norfid = rfidField.getText();
                        System.out.println("RFID : " + norfid);
                    }
                });

                // Add key listener for filtering numeric input
                rfidField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        char c = e.getKeyChar();
                        if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                            e.consume();
                            System.out.println("Hanya angka yang diperbolehkan.");
                        }
                    }
                });

                rfidField.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String no = rfidField.getText();
                        if (validateRFID(no)) {
//                    javax.swing.JFrame parentFrame = (javax.swing.JFrame) SwingUtilities.getWindowAncestor(this);
                            System.out.println("masuk");
                            if (jabatan.equals("kasir")) {
                            new FormKasir().setVisible(true);
                            LoginForm.this.setVisible(false);
                            dialog.dispose();
                                System.out.println("ini kasir");
                            } else if (jabatan.equals("owner")) {
//                            new Dashboard().setVisible(true);
//                            this.setVisible(false);
                                System.out.println("ini owner");
                            }

//                    if (parentFrame != null) {
//                        parentFrame.dispose();
//                    }
                        } else {
                        PopUpRFIDWarning popup = new PopUpRFIDWarning(LoginForm.this);
                        popup.setLocationRelativeTo(SwingUtilities.getWindowAncestor(LoginForm.this));
                        popup.setVisible(true);
                            System.out.println(no);
                            System.out.println("no rfid salah");
                        }
                    }

                    private boolean validateRFID(String norfid) {
                        String query = "SELECT norfid, jabatan FROM user WHERE norfid = ?";
                        try {
                            PreparedStatement stmt = con.prepareStatement(query);
                            stmt.setString(1, norfid);

                            ResultSet rs = stmt.executeQuery();

                            // Jika ada hasil, berarti username, password, dan posisi sesuai
                            if (rs.next()) {
                                String rolee = rs.getString("jabatan");
                                jabatan = rolee;
                                setNoRFID(norfid);
                                return true;
                            } else {
                            // Reset field ketika validasi gagal
                                rfidField.setText("");
                                return false;
                            }
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(LoginForm.this, "Error saat validasi pengguna: " + e.getMessage());
                            return false;
                        }
                    }
                });

                // Show the dialog
                dialog.setVisible(true);

                // Request focus after showing the dialog
                SwingUtilities.invokeLater(() -> {
                    rfidField.requestFocusInWindow();
                });
            }
        });
    }

    public static void main(String args[]) {
        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        java.awt.EventQueue.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }

    private void FungsiKomponenLogin() {
        //Form
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = usernameField.getText();
                String pw = passwordField.getText();

                if (email.isEmpty() || email.equals("Email")) {
                    PopUp_login_emailtidakbolehkosong a = new PopUp_login_emailtidakbolehkosong(LoginForm.this);
                    a.setAlwaysOnTop(true);
                    a.setVisible(true);
                    return;
                } else if (pw.isEmpty() || pw.equals("Password")) {
                    PopUp_login_passwordtidakbolehkosong b = new PopUp_login_passwordtidakbolehkosong(LoginForm.this);
                    b.setAlwaysOnTop(true);
                    b.setVisible(true);
                    return;
                }

                if (!isValidGmailAddress(email)) {
                    PopUp_login_contohpenulisanemailyangbenar mail = new PopUp_login_contohpenulisanemailyangbenar(LoginForm.this);
                    mail.setAlwaysOnTop(true);
                    mail.setVisible(true);
                } else {
                    if (validateUser(email, pw)) {
                        javax.swing.JFrame parentFrame = (javax.swing.JFrame) SwingUtilities.getWindowAncestor(LoginForm.this);
                        System.out.println("masuk");
                        if (jabatan.equals("kasir")) {
                            new FormKasir().setVisible(true);
                            LoginForm.this.setVisible(false);
                            System.out.println("ini kasir");
                            PindahanAntarPopUp.showMasukSebagaiKasir(parentFrame);
                        } else if (jabatan.equals("owner")) {
                            new Productt().setVisible(true);
                            LoginForm.this.setVisible(false);
                            System.out.println("ini owner");
                            PindahanAntarPopUp.showMasukSebagaiOwner(parentFrame);
                        }

                        if (parentFrame != null) {
                            parentFrame.dispose();
                        }
                    } else {
                        PopUp_login_akuntidakada popup = new PopUp_login_akuntidakada(LoginForm.this);
                        popup.setAlwaysOnTop(true);
                        popup.setVisible(true);
                    }
                }
//            }
            }

            private boolean validateUser(String email, String pw) {
                String query = "SELECT email, password, jabatan FROM user WHERE email = ? AND password = ?";
                try {
                    PreparedStatement stmt = con.prepareStatement(query);
                    stmt.setString(1, email);
                    stmt.setString(2, pw);

                    ResultSet rs = stmt.executeQuery();

                    // Jika ada hasil, berarti username, password, dan posisi sesuai
                    if (rs.next()) {
                        String rolee = rs.getString("jabatan");
                        jabatan = rolee;
                        setNamaUser(email);
                        return true;
                    } else {
                        return false;
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(LoginForm.this, "Error saat validasi pengguna: " + e.getMessage());
                    return false;
                }
            }
        }
        );
    }

    private boolean isValidGmailAddress(String email) {
        if (email == null) {
            return false;
        }

        // Pola regex untuk memvalidasi email Gmail
        String gmailRegex = "^[a-zA-Z0-9._%+-]+@gmail\\.com$";
        Pattern pattern = Pattern.compile(gmailRegex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }
}
