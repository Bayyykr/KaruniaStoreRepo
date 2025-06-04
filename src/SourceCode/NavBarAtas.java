package SourceCode;

import PopUp_all.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.geom.RoundRectangle2D;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import db.conn;
import java.sql.*;
import Form.LoginForm;
import javax.swing.JFrame;

public class NavBarAtas extends JPanel {

    private JLabel  profileLabel;
    private JPopupMenu userMenu, detailMenu;
    private JPasswordField passwordField;
    private JTextField emailField, usernameField;
    private boolean passwordVisible = false;
    private Connection con;
    private String NoRFID, namaUser;
    private JFrame parentFrame;

    private class RoundedButton extends BasicButtonUI {

        private static final int ARC_SIZE = 10;

        @Override
        public void installUI(javax.swing.JComponent c) {
            super.installUI(c);
            JButton button = (JButton) c;
            button.setOpaque(false);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setContentAreaFilled(false);
        }

        @Override
        protected void paintButtonPressed(Graphics g, javax.swing.AbstractButton b) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(b.getBackground().darker());
            g2.fill(new RoundRectangle2D.Double(0, 0, b.getWidth(), b.getHeight(), ARC_SIZE, ARC_SIZE));
            g2.dispose();
        }

        @Override
        public void paint(Graphics g, javax.swing.JComponent c) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            JButton button = (JButton) c;
            g2.setColor(button.getBackground());
            g2.fill(new RoundRectangle2D.Double(0, 0, c.getWidth(), c.getHeight(), ARC_SIZE, ARC_SIZE));
            g2.dispose();

            super.paint(g, c);
        }
    }

    public NavBarAtas() {
        initComponents();
    }

    private void initComponents() {
        setPreferredSize(new Dimension(800, 40));
        setBackground(new Color(0, 0, 0));
        setLayout(new FlowLayout(FlowLayout.RIGHT, 30, 10));
        con = conn.getConnection();

        setNamaUser();


        profileLabel = new JLabel();
        try {
            ImageIcon profileIcon = loadImage("/SourceImage/user-icon.png");
            profileLabel.setIcon(profileIcon);
        } catch (Exception e) {
            System.err.println("Error loading profile icon: " + e.getMessage());
        }

        createUserMenu();
        createDetailMenu();
        loadUser();

        profileLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                userMenu.show(profileLabel, 0, profileLabel.getHeight() + 10);
            }
        });

        add(profileLabel);
    }

    private ImageIcon loadImage(String path) {
        java.net.URL imageURL = getClass().getResource(path);
        if (imageURL != null) {
            return new ImageIcon(imageURL);
        } else {
            throw new RuntimeException("Could not find image: " + path);
        }
    }

    private ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        java.awt.Image img = icon.getImage();
        java.awt.Image resizedImage = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }

    private void createUserMenu() {
        userMenu = new JPopupMenu();
        userMenu.setBackground(Color.WHITE);
        userMenu.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JLabel detailLabel = new JLabel("Detail Akun");
        detailLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        detailLabel.setBorder(new EmptyBorder(5, 10, 5, 50));

        detailLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                userMenu.setVisible(false);
                detailMenu.show(profileLabel, 0, profileLabel.getHeight() + 10);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                detailLabel.setForeground(new Color(0, 123, 255));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                detailLabel.setForeground(Color.BLACK);
            }
        });

        userMenu.add(detailLabel);
    }

    private void createDetailMenu() {
        detailMenu = new JPopupMenu();
        detailMenu.setBackground(Color.WHITE);
        detailMenu.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        // Create form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        headerPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Detail Akun");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel closeLabel = new JLabel("×");
        closeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        closeLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        closeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                detailMenu.setVisible(false);
            }
        });

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createHorizontalStrut(220));
        headerPanel.add(closeLabel);
        formPanel.add(headerPanel);
        formPanel.add(Box.createVerticalStrut(10));

        // Username field
        JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        usernamePanel.setBackground(Color.WHITE);

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setPreferredSize(new Dimension(70, 20));
        usernameField = new JTextField();
        usernameField.setText("");
        usernameField.setPreferredSize(new Dimension(220, 25));

        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameField);
        formPanel.add(usernamePanel);
        formPanel.add(Box.createVerticalStrut(5));

        // Email field
        JPanel emailPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        emailPanel.setBackground(Color.WHITE);

        JLabel emailLabel = new JLabel("E-mail");
        emailLabel.setPreferredSize(new Dimension(70, 20));
        emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(220, 25));

        emailPanel.add(emailLabel);
        emailPanel.add(emailField);
        formPanel.add(emailPanel);
        formPanel.add(Box.createVerticalStrut(5));
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        passwordPanel.setBackground(Color.WHITE);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setPreferredSize(new Dimension(70, 20));

        // Custom password panel with embedded toggle button
        JPanel passwordFieldPanel = new JPanel(new BorderLayout());
        passwordFieldPanel.setPreferredSize(new Dimension(220, 25));
        passwordFieldPanel.setBackground(Color.WHITE);

        // Create password field
        passwordField = new JPasswordField();
        passwordField.setEchoChar('*');
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                passwordField.getBorder(),
                BorderFactory.createEmptyBorder(0, 5, 0, 30)));

        JButton toggleButton = new JButton();
        toggleButton.setPreferredSize(new Dimension(30, 20));
        toggleButton.setBorderPainted(false);
        toggleButton.setContentAreaFilled(false);
        toggleButton.setFocusPainted(false);

        final ImageIcon lockIcon;
        final ImageIcon unlockIcon;

        try {
            ImageIcon originalLockIcon = loadImage("/SourceImage/lock.png");
            ImageIcon originalUnlockIcon = loadImage("/SourceImage/unlock.png");

            lockIcon = resizeIcon(originalLockIcon, 16, 16);
            unlockIcon = resizeIcon(originalUnlockIcon, 16, 16);

            toggleButton.setIcon(lockIcon);

            toggleButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    passwordVisible = !passwordVisible;
                    if (passwordVisible) {
                        passwordField.setEchoChar((char) 0);
                        toggleButton.setIcon(unlockIcon);
                    } else {
                        passwordField.setEchoChar('*');
                        toggleButton.setIcon(lockIcon);
                    }
                }
            });

            passwordFieldPanel.add(passwordField, BorderLayout.CENTER);

            JPanel overlayPanel = new JPanel(new BorderLayout());
            overlayPanel.setOpaque(false);
            overlayPanel.add(toggleButton, BorderLayout.EAST);

            // Use a layered pane to position the button over the text field
            JLayeredPane layeredPane = new JLayeredPane();
            layeredPane.setPreferredSize(new Dimension(220, 25));

            passwordFieldPanel.setBounds(0, 0, 220, 25);
            overlayPanel.setBounds(0, 0, 220, 25);

            layeredPane.add(passwordFieldPanel, JLayeredPane.DEFAULT_LAYER);
            layeredPane.add(overlayPanel, JLayeredPane.PALETTE_LAYER);

            passwordPanel.add(passwordLabel);
            passwordPanel.add(layeredPane);

        } catch (Exception e) {
            System.err.println("Error loading lock/unlock icons: " + e.getMessage());
            // If lock/unlock icons fail to load, just add the password field without toggle
            passwordPanel.add(passwordLabel);
            passwordPanel.add(passwordField);
        }

        formPanel.add(passwordPanel);
        formPanel.add(Box.createVerticalStrut(20));

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        buttonsPanel.setBackground(Color.WHITE);

        // Create Cancel button with rounded corners
        JButton CancelButton = new JButton("Cancel");
        CancelButton.setUI(new RoundedButton());
        CancelButton.setBackground(new Color(108, 117, 125));
        CancelButton.setForeground(Color.WHITE);
        CancelButton.setPreferredSize(new Dimension(90, 30));
        CancelButton.addActionListener(e ->{
            detailMenu.setVisible(false);
            clear();
        });

        // Create Change button with rounded corners
        JButton ChangeButton = new JButton("Change");
        ChangeButton.setUI(new RoundedButton());
        ChangeButton.setBackground(new Color(52, 58, 64));
        ChangeButton.setForeground(Color.WHITE);
        ChangeButton.setPreferredSize(new Dimension(90, 30));
        ChangeButton.addActionListener(e -> {
            updateUser();
            clear();
        });

        buttonsPanel.add(CancelButton);
        buttonsPanel.add(Box.createHorizontalStrut(20));
        buttonsPanel.add(ChangeButton);
        formPanel.add(buttonsPanel);

        detailMenu.add(formPanel);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
                NoRFID = rs.getString("norfid");
                namaUser = rs.getString("nama_user");
            } else {
                System.out.println("No karyawan found ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadUser() {
        try {
            String nama = "", email = "", password = "";
            String sql = "SELECT * FROM user WHERE norfid = ?";
            try (PreparedStatement st = con.prepareStatement(sql)) {
                st.setString(1, NoRFID);
                ResultSet rs = st.executeQuery();

                if (rs.next()) {
                    nama = rs.getString("nama_user");
                    email = rs.getString("email");
                    password = rs.getString("password");
                }
            }
            usernameField.setText(nama);
            emailField.setText(email);
            passwordField.setText(password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void clear(){
        loadUser();
    }
    
    private void updateUser(){
        String nama = usernameField.getText();
        String email = emailField.getText().toLowerCase();
        String pw = passwordField.getText();
        
        if (nama.isEmpty() || email.isEmpty() || pw.isEmpty()) {
        PindahanAntarPopUp.showEditProductFieldTidakBolehKosong(parentFrame);
        System.out.println("Field Tidak Boleh Kosong");     
        return; 
        }
        if (!email.endsWith("@gmail.com")) {
        PindahanAntarPopUp.showNavBarAtasPenulisanEmailSalah(parentFrame);
        return;
        }
        
        try {
            String sql = "UPDATE user SET nama_user = ?, email = ?, password = ? WHERE norfid = ?";
            try(PreparedStatement st = con.prepareStatement(sql)){
                st.setString(1, nama);
                st.setString(2, email);
                st.setString(3, pw);
                st.setString(4, NoRFID);
                
                int rowUpdated = st.executeUpdate();
                if(rowUpdated > 0){
                    PindahanAntarPopUp.showEditKaryawanSuksesDiEdit(parentFrame);
                    System.out.println("user berhasil diupdate");
                }
            }
        } catch (Exception e) {
        }
    }
}
