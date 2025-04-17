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
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.border.AbstractBorder;

public class RegisterForm extends JFrame {

    private JPanel mainPanel;
    private JButton daftarButton, sendCodeButton;
    private JTextField fullNameField, emailField, verifikasiField, usernameField;
    private JPasswordField passwordField;
    private JLabel titleLabel, verifikasiLabel, backButton;
    int xMouse, yMouse;

    public RegisterForm() {
        initComponents();
        setupPlaceholders();
        back();
    }

    private void initComponents() {
        setSize(945, 570);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);

        // Main panel dengan layout null
        mainPanel = new JPanel(null);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setFocusable(true);

        // Back button
        backButton = new JLabel("←");
        backButton.setBounds(5, 5, 50, 30);
        backButton.setFont(new Font("Arial", Font.BOLD, 24));
        backButton.setForeground(new Color(51, 51, 51));
        backButton.setHorizontalAlignment(SwingConstants.CENTER);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Close button
        JLabel closeButton = new JLabel("×");
        closeButton.setFont(new Font("Arial", Font.BOLD, 24));
        closeButton.setForeground(new Color(51, 51, 51));
        closeButton.setHorizontalAlignment(SwingConstants.CENTER);
        closeButton.setBounds(910, 5, 30, 30);
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

        // Title
        titleLabel = new JLabel("Sign Up / Daftar");
        titleLabel.setBounds(0, 50, 945, 40);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // Form components - centered
        int fieldWidth = 390;
        int centerX = (945 - fieldWidth) / 2;
        int currentY = 140;
        int fieldHeight = 40;
        int spacing = 20;

        class RoundedBorder extends AbstractBorder {

            private final Color color;
            private final int radius = 20;

            RoundedBorder(Color color) {
                this.color = color;
            }

            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(color);
                g2d.draw(new RoundRectangle2D.Double(x, y, width - 1, height - 1, radius, radius));
                g2d.dispose();
            }

            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(4, 8, 4, 8);
            }
        }

        // Full Name
        fullNameField = new JTextField();
        fullNameField.setBounds(centerX, currentY, fieldWidth, fieldHeight);
        fullNameField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        fullNameField.setText("Full Name");
//        fullNameField.setEditable(false);
        currentY += fieldHeight + spacing;

        // Create email field with internal rounded send code button
        JPanel emailPanel = new JPanel(null);
        emailPanel.setBounds(centerX, currentY, fieldWidth, fieldHeight);
        emailPanel.setBackground(Color.WHITE);
        emailPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

// Email text field (taking full width)
        emailField = new JTextField();
        emailField.setBounds(18, 5, fieldWidth - 110, fieldHeight - 10);
        emailField.setBorder(null);
        emailField.setBackground(Color.WHITE);
//        emailField.setForeground("Email");
        emailField.setText("Email");

// Rounded send code button inside the email field
        sendCodeButton = new JButton("send code") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();

                super.paintComponent(g);
            }
        };
        sendCodeButton.setBounds(fieldWidth - 90, 5, 80, fieldHeight - 10);
        sendCodeButton.setBackground(new Color(135, 206, 250));
        sendCodeButton.setForeground(Color.WHITE);
        sendCodeButton.setBorder(null);
        sendCodeButton.setFocusPainted(false);
        sendCodeButton.setContentAreaFilled(false);
        sendCodeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendCodeButton.setFont(new Font("Arial", Font.PLAIN, 12));
        
        sendCodeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Slightly move the button down when pressed
                sendCodeButton.setLocation(sendCodeButton.getX(), sendCodeButton.getY() + 1);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // Move the button back up when released
                sendCodeButton.setLocation(sendCodeButton.getX(), sendCodeButton.getY() - 1);
            }
        });

// Add hover effect for send code button
        sendCodeButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                sendCodeButton.setBackground(new Color(100, 181, 246));
                sendCodeButton.repaint();
            }

            public void mouseExited(MouseEvent e) {
                sendCodeButton.setBackground(new Color(135, 206, 250));
                sendCodeButton.repaint();
            }
        });

// Add focus listeners for the email field
        emailField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (emailField.getText().equals("Email")) {
                    emailField.setText("");
                }
                emailPanel.setBorder(BorderFactory.createCompoundBorder(
                        new RoundedBorder(new Color(0, 123, 255)),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (emailField.getText().isEmpty()) {
                    emailField.setText("Email");
                }
                emailPanel.setBorder(BorderFactory.createCompoundBorder(
                        new RoundedBorder(new Color(200, 200, 200)),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }
        });

// Add components to email panel
        emailPanel.add(emailField);
        emailPanel.add(sendCodeButton);

        currentY += fieldHeight + spacing;

        // Verifikasi Kode
        JPanel verifikasiPanel = new JPanel(null);
        verifikasiPanel.setBounds(centerX, currentY, fieldWidth, fieldHeight);
        verifikasiPanel.setBackground(Color.WHITE);
        verifikasiPanel.setBorder(null);

// Label di sebelah kiri
        verifikasiLabel = new JLabel("Verifikasi Kode");
        verifikasiLabel.setBounds(0, 0, 100, fieldHeight);
        verifikasiLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        verifikasiLabel.setForeground(new Color(128, 128, 128));

// Textfield di sebelah kanan label
        verifikasiField = new JTextField();
        verifikasiField.setBounds(110, 0, fieldWidth - 110, fieldHeight);
        verifikasiField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        currentY += fieldHeight + spacing;

// Tambahkan komponen ke panel
        verifikasiPanel.add(verifikasiLabel);
        verifikasiPanel.add(verifikasiField);

        // Username
        usernameField = new JTextField();
        usernameField.setBounds(centerX, currentY, fieldWidth, fieldHeight);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        usernameField.setText("Username");

        // Password with icon
        currentY += fieldHeight + spacing;

        JPanel passwordPanel = new JPanel(null);
        passwordPanel.setBounds(centerX, currentY, fieldWidth, fieldHeight);
        passwordPanel.setBackground(Color.WHITE);
        passwordPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

// Adjust password field to fit the panel
        passwordField = new JPasswordField();
        passwordField.setBounds(18, 5, fieldWidth - 65, fieldHeight - 10);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//        passwordField.setForeground(new Color(128, 128, 128));
        passwordField.setBackground(Color.WHITE);
        passwordField.setBorder(null);
        passwordField.setText("Password");
        passwordField.setEchoChar((char) 0);

//        JLabel lockIcon = new JLabel("🔒");
//        lockIcon.setBounds(fieldWidth - 40, 0, 40, fieldHeight);
//        lockIcon.setHorizontalAlignment(SwingConstants.CENTER);
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
        showPasswordButton.setBounds(fieldWidth - 40, 5, 30, 30);
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
                        new RoundedBorder(new Color(0, 123, 255)),
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
                        new RoundedBorder(new Color(200, 200, 200)),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }
        });

        FocusListener fieldFocusListener = new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                JTextField field = (JTextField) e.getComponent();
//                field.setForeground(new Color(128, 128, 128));
                field.setBorder(BorderFactory.createCompoundBorder(
                        new RoundedBorder(new Color(0, 123, 255)),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                JTextField field = (JTextField) e.getComponent();
//                field.setForeground(new Color(128, 128, 128));
                field.setBorder(BorderFactory.createCompoundBorder(
                        new RoundedBorder(new Color(200, 200, 200)),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }
        };
        fullNameField.addFocusListener(fieldFocusListener);
        verifikasiField.addFocusListener(fieldFocusListener);
        usernameField.addFocusListener(fieldFocusListener);

        // Add components to password panel
        passwordPanel.add(passwordField);
        passwordPanel.add(showPasswordButton);
        currentY += fieldHeight + spacing;

        // Daftar button
        daftarButton = new JButton("LOGIN") {
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
        daftarButton.setBounds(centerX, currentY, fieldWidth, fieldHeight);
        daftarButton.setBorderPainted(false);
        daftarButton.setContentAreaFilled(false);
        daftarButton.setFocusPainted(false);
        daftarButton.setFont(new Font("Arial", Font.BOLD, 14));
        daftarButton.setFocusPainted(false);
        
        daftarButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Slightly move the button down when pressed
                daftarButton.setLocation(daftarButton.getX(), daftarButton.getY() + 1);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // Move the button back up when released
                daftarButton.setLocation(daftarButton.getX(), daftarButton.getY() - 1);
            }
        });

        // Add all components
        mainPanel.add(backButton);
        mainPanel.add(titleLabel);
        mainPanel.add(fullNameField);
        mainPanel.add(emailPanel);
//        mainPanel.add(verifikasiLabel);
        mainPanel.add(verifikasiPanel);
        mainPanel.add(usernameField);
        mainPanel.add(passwordPanel);
        mainPanel.add(daftarButton);
        mainPanel.add(closeButton);

        // Add panel to frame
        add(mainPanel);

        mainPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                xMouse = e.getX();
                yMouse = e.getY();
            }
        });

        mainPanel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                int x = e.getXOnScreen() - xMouse;
                int y = e.getYOnScreen() - yMouse;
                setLocation(x, y);
            }
        });

    }

    private void setupPlaceholders() {
        // Setup placeholder behavior for text fields
        setupPlaceholder(fullNameField, "Full Name");
        setupPlaceholder(emailField, "Email");
        setupPlaceholder(usernameField, "Username");
        setupPlaceholder(passwordField, "Password");
    }

    private void setupPlaceholder(JTextField field, String placeholder) {
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                }
            }
        });
    }
    
    private void back() {
        backButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                LoginForm login = new LoginForm();
                login.setVisible(true);
                dispose();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new RegisterForm().setVisible(true);
        });
    }
}
