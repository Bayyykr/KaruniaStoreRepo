package PopUp_all;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.AffineTransform;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import db.conn;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PopUp_LupaPasswordLogin extends JDialog {

    private JComponent glassPane;
    private JFrame parentFrame;
    private JPanel contentPanel;
    private JTextField emailField;
    private JPasswordField newPasswordField, confirmPasswordField;
    private JButton simpanButton, backButton;
    private ImageIcon lockIcon, unlockIcon;
    private boolean newPasswordVisible = false;
    private boolean confirmPasswordVisible = false;

    private final int RADIUS = 20;
    private final int FINAL_WIDTH = 450;
    private final int FINAL_HEIGHT = 400;
    private final int ANIMATION_DURATION = 300;
    private final int ANIMATION_DELAY = 10;
    private float currentScale = 0.01f;
    private boolean animationStarted = false;
    private Timer animationTimer;
    private Timer closeAnimationTimer;

    // Flag untuk menghindari penambahan glassPane berulang
    private static boolean isShowingPopup = false;
    private Connection con;

    // Konstruktor tanpa parameter
    public PopUp_LupaPasswordLogin() {
        this(null);
    }

    public PopUp_LupaPasswordLogin(JFrame parent) {
        super(parent, "Lupa Password", true);
        this.parentFrame = parent;
        setModal(true);
        setPreferredSize(new Dimension(FINAL_WIDTH, FINAL_HEIGHT));

        con = conn.getConnection();

        // Periksa apakah popup sudah ditampilkan
        if (isShowingPopup) {
            dispose();
            return;
        }
        isShowingPopup = true;

        lockIcon = new ImageIcon(getClass().getResource("/SourceImage/lock.png"));
        unlockIcon = new ImageIcon(getClass().getResource("/SourceImage/unlock.png"));

        // Resize icons jika perlu
        Image lockImg = lockIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        Image unlockImg = unlockIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        lockIcon = new ImageIcon(lockImg);
        unlockIcon = new ImageIcon(unlockImg);

        // Buat overlay transparan dengan warna hitam semi transparan
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

        parentLayeredPane().add(glassPane, JLayeredPane.POPUP_LAYER);

        setUndecorated(true);
        setSize(FINAL_WIDTH, FINAL_HEIGHT);
        setLocationRelativeTo(parent);
        setBackground(new Color(0, 0, 0, 0));

        contentPanel = new RoundedPanel(RADIUS);
        contentPanel.setLayout(null);
        contentPanel.setBounds(0, 0, FINAL_WIDTH, FINAL_HEIGHT);
        contentPanel.setBackground(Color.WHITE);
        add(contentPanel);

        createComponents();

        // Tambahkan WindowListener untuk membersihkan saat popup ditutup
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanupAndClose();
            }
        });

        startScaleAnimation();
    }

    private void createComponents() {
        // Title label
        JLabel titleLabel = new JLabel("Forgot Password?");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBounds(0, 30, FINAL_WIDTH, 30);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(titleLabel);

        // Back label di pojok kiri atas
        JLabel backLabel = new JLabel("←");
        backLabel.setFont(new Font("Arial", Font.BOLD, 26));
        backLabel.setBounds(15, 15, 35, 35);
        backLabel.setForeground(Color.BLACK);
        backLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Supaya bisa diklik, tambahkan mouse listener
        backLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                startCloseAnimation();
            }
        });
        backLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        contentPanel.add(backLabel);

        // Email Label
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        emailLabel.setBounds(50, 80, 350, 20);
        contentPanel.add(emailLabel);

        // Email TextField
        emailField = createRoundedTextField();
        emailField.setBounds(50, 105, 350, 45);
        contentPanel.add(emailField);

        // New Password Label
        JLabel newPasswordLabel = new JLabel("New Password");
        newPasswordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        newPasswordLabel.setBounds(50, 160, 350, 20);
        contentPanel.add(newPasswordLabel);

        // New Password Field dengan Icon Inside
        JPanel newPasswordPanel = createPasswordFieldWithIcon(newPasswordVisible);
        newPasswordPanel.setBounds(50, 185, 350, 45);
        contentPanel.add(newPasswordPanel);

        // Dapatkan referensi ke password field dan icon label
        newPasswordField = (JPasswordField) ((Container) newPasswordPanel.getComponent(0)).getComponent(0);
        JLabel newPasswordIconLabel = (JLabel) ((Container) newPasswordPanel.getComponent(0)).getComponent(1);

        // Tambahkan listener untuk toggle icon
        newPasswordIconLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                newPasswordVisible = !newPasswordVisible;
                togglePasswordVisibility(newPasswordField, newPasswordIconLabel, newPasswordVisible);
            }
        });

        // Confirm Password Label
        JLabel confirmPasswordLabel = new JLabel("Confirm Password");
        confirmPasswordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        confirmPasswordLabel.setBounds(50, 240, 350, 20);
        contentPanel.add(confirmPasswordLabel);

        // Confirm Password Field dengan Icon Inside
        JPanel confirmPasswordPanel = createPasswordFieldWithIcon(confirmPasswordVisible);
        confirmPasswordPanel.setBounds(50, 265, 350, 45);
        contentPanel.add(confirmPasswordPanel);

        // Dapatkan referensi ke password field dan icon label
        confirmPasswordField = (JPasswordField) ((Container) confirmPasswordPanel.getComponent(0)).getComponent(0);
        JLabel confirmPasswordIconLabel = (JLabel) ((Container) confirmPasswordPanel.getComponent(0)).getComponent(1);

        // Tambahkan listener untuk toggle icon
        confirmPasswordIconLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                confirmPasswordVisible = !confirmPasswordVisible;
                togglePasswordVisibility(confirmPasswordField, confirmPasswordIconLabel, confirmPasswordVisible);
            }
        });

        // Simpan Button
        simpanButton = createSimpanButton();
        simpanButton.setBounds(50, 330, 350, 45);
        simpanButton.addActionListener(e -> resetPassword());
        contentPanel.add(simpanButton);
    }

    private JPanel createPasswordFieldWithIcon(boolean isVisible) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2.dispose();
            }
        };
        panel.setLayout(new BorderLayout());
        panel.setOpaque(false);

        // Sub panel untuk password field dan icon
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.setOpaque(false);

        // Create password field
        JPasswordField passwordField = new JPasswordField();
        passwordField.setOpaque(false);
        passwordField.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 5));
        passwordField.setEchoChar('\u2022'); // Bullet character

        // Create icon label
        JLabel iconLabel = new JLabel(isVisible ? unlockIcon : lockIcon);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Add components to inner panel
        innerPanel.add(passwordField, BorderLayout.CENTER);
        innerPanel.add(iconLabel, BorderLayout.EAST);

        // Add inner panel to main panel
        panel.add(innerPanel, BorderLayout.CENTER);

        return panel;
    }

    private void togglePasswordVisibility(JPasswordField field, JLabel iconLabel, boolean isVisible) {
        if (isVisible) {
            // Ubah ke mode terlihat
            field.setEchoChar((char) 0); // Menampilkan teks asli
            iconLabel.setIcon(unlockIcon);
        } else {
            // Ubah ke mode tersembunyi
            field.setEchoChar('\u2022'); // Karakter bullet
            iconLabel.setIcon(lockIcon);
        }
    }

    private JTextField createRoundedTextField() {
        JTextField textField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            public void updateUI() {
                super.updateUI();
                setOpaque(false);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            }
        };
        textField.setBackground(Color.WHITE);
        return textField;
    }

    private JButton createSimpanButton() {
        JButton button = new JButton("SIMPAN") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(51, 51, 51)); // Warna hitam untuk button
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
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
                float easedProgress = 1 - (1 - progress) * (1 - progress) * (1 - progress);

                currentScale = 0.01f + 0.99f * easedProgress;

                repaint();

                if (currentFrame[0] >= totalFrames) {
                    animationTimer.stop();
                    currentScale = 1.0f;
                    repaint();
                }
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

    private void resetPassword() {
        String email = emailField.getText();
        String pwbaru = newPasswordField.getText();
        String pwconfirm = confirmPasswordField.getText();

        if (email.isEmpty()) {
            PindahanAntarPopup.showEmailPopup(parentFrame);
            System.out.println("email tidak bole kosong");
            return;
        } else if (pwbaru.isEmpty() || pwbaru.isEmpty()) {
            PindahanAntarPopup.showPasswordPopup(parentFrame);
            System.out.println("pw tidak bole kosong");
            return;
        }

        if (!pwbaru.equals(pwconfirm)) {
            PindahanAntarPopup.showKonfirmPwTidakBolehKosong(parentFrame);
//            PindahanAntarPopup.showPasswordsalah(parentFrame);
            System.out.println("pw tidak sama");
            return;
        }

        if (!isValidGmailAddress(email)) {
            PindahanAntarPopup.showEmailsalah(parentFrame);
            System.out.println("penulisan email salah");
        } else {
            try {
                String query = "UPDATE user SET password = ? WHERE email = ?";
                try (PreparedStatement st = con.prepareStatement(query)) {
                    st.setString(1, pwbaru);
                    st.setString(2, email);

                    int rowUpdated = st.executeUpdate();
                    if (rowUpdated > 0) {
                        PindahanAntarPopup.showSuksesGantiPw(parentFrame);
                        System.out.println("berhasil diupdate");
                        startCloseAnimation();
                    } else {
                        PindahanAntarPopup.showEmailsalah(parentFrame);
                        System.out.println("email tidak tersedia");
                    }
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }

        }
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

    // RoundedPanel Inner Class
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
}
