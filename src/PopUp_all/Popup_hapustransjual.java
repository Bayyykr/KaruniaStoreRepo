package PopUp_all;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;

public class Popup_hapustransjual extends JDialog {

    private JComponent glassPane;
    private JFrame parentFrame;
    private static final int RADIUS = 20;
    private JButton cancelButton, deleteButton;
    private JLabel warningIconLabel, warningTextLabel, confirmTextLabel;

    private int xMouse, yMouse;
    private final int ANIMATION_DURATION = 300; // ms
    private final int ANIMATION_DELAY = 10; // ms
    private float currentScale = 0.01f;
    private boolean animationStarted = false;
    private Timer animationTimer;
    private Timer closeAnimationTimer;
    private final int FINAL_WIDTH = 450;  // Ukuran tetap
    private final int FINAL_HEIGHT = 250; // Ukuran tetap

    // Flag untuk menghindari penambahan glassPane berulang
    private static boolean isShowingPopup = false;
    private boolean deleteConfirmed = false;

    public Popup_hapustransjual(JFrame parent) {
        this.parentFrame = parent;
        setModal(true);
        setPreferredSize(new Dimension(FINAL_WIDTH, FINAL_HEIGHT));
        setLayout(null);

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
                g2.setColor(new Color(0, 0, 0, 180));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        glassPane.setOpaque(false);
        glassPane.setBounds(0, 0, parent.getWidth(), parent.getHeight());

        // Menghapus glassPane yang mungkin sudah ada sebelumnya
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

        warningIconLabel = createLabel("/SourceImage/icon_seru_merah.png", 205, 10, 80, 80);
        warningIconLabel.setVisible(false);
        contentPanel.add(warningIconLabel);

        warningTextLabel = createTextLabel("Warning!", 150, 75, 150, 30, new Font("Arial", Font.BOLD, 18), Color.RED);
        warningTextLabel.setVisible(false);
        contentPanel.add(warningTextLabel);

        confirmTextLabel = createTextLabel(
                "<html><center>Are you sure you want to delete this<br>purchase transaction data?</center></html>",
                25, 110, 400, 50, new Font("Arial", Font.PLAIN, 14), Color.BLACK);
        confirmTextLabel.setVisible(false);
        contentPanel.add(confirmTextLabel);

        // Menginisialisasi tombol Cancel dan Delete
        cancelButton = createRoundedButton("Cancel", 60, 190, 130, 30, new Color(0xEBEBEB), Color.BLACK);
        cancelButton.setVisible(false);
        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                startCloseAnimation();
            }
        });
        contentPanel.add(cancelButton);

        deleteButton = createRoundedButton("Delete", 250, 190, 130, 30, new Color(0xFF6347), Color.WHITE);
        deleteButton.setVisible(false);
        deleteButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Set the flag to true indicating delete was confirmed
                deleteConfirmed = true;

                // Start close animation
                startCloseAnimation();

                // Rest of your existing delete button code...
            }
        });
        contentPanel.add(deleteButton);

        // Tambahkan WindowListener untuk membersihkan saat popup ditutup
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanupAndClose();
            }
        });

        // Memulai animasi setelah pop-up muncul
        startScaleAnimation();
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
                float easedProgress = 1 - (1 - progress) * (1 - progress) * (1 - progress);

                currentScale = 0.01f + 0.99f * easedProgress;

                if (progress >= 0.3 && !warningIconLabel.isVisible()) {
                    warningIconLabel.setVisible(true);
                    warningTextLabel.setVisible(true);
                    confirmTextLabel.setVisible(true);
                    cancelButton.setVisible(true);
                    deleteButton.setVisible(true);
                }

                repaint();

                if (currentFrame[0] >= totalFrames) {
                    animationTimer.stop();
                    currentScale = 1.0f;

                    warningIconLabel.setVisible(true);
                    warningTextLabel.setVisible(true);
                    confirmTextLabel.setVisible(true);
                    cancelButton.setVisible(true);
                    deleteButton.setVisible(true);

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

    private JLabel createLabel(String path, int x, int y, int width, int height) {
        JLabel label = new JLabel();
        label.setBounds(x, y, width, height);
        ImageIcon icon = new ImageIcon(getClass().getResource(path));
        if (icon.getIconWidth() == -1) {
            System.out.println("Gambar tidak ditemukan: " + path);
        }
        label.setIcon(icon);
        return label;
    }

    private JLabel createTextLabel(String text, int x, int y, int width, int height, Font font, Color color) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setBounds(x, y, width, height);
        label.setFont(font);
        label.setForeground(color);
        return label;
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

    public boolean isDeleteConfirmed() {
        return deleteConfirmed;
    }
}
