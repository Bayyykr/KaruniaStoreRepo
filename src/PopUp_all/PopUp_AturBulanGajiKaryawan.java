package PopUp_all;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.AffineTransform;
import produk.ComboboxCustom; // Import ComboboxCustom dari package produk

public class PopUp_AturBulanGajiKaryawan extends JDialog {

    private JComponent glassPane;
    private JFrame parentFrame;
    private JPanel contentPanel;
    private ComboboxCustom comboBoxBulan, comboBoxTahun;
    private JButton terapkanButton, batalButton;

    private final int RADIUS = 20;
    private final int FINAL_WIDTH = 350;
    private final int FINAL_HEIGHT = 300;
    private final int ANIMATION_DURATION = 300;
    private final int ANIMATION_DELAY = 10;
    private float currentScale = 0.01f;
    private boolean animationStarted = false;
    private Timer animationTimer;
    private Timer closeAnimationTimer;

    // Flag untuk menghindari penambahan glassPane berulang
    private static boolean isShowingPopup = false;

    // Konstruktor tanpa parameter
    public PopUp_AturBulanGajiKaryawan() {
        this(null); // Memanggil konstruktor dengan parameter
    }

    public PopUp_AturBulanGajiKaryawan(JFrame parent) {
        super(parent, "Atur Bulan Gaji", true);
        this.parentFrame = parent;
        setModal(true);
        setPreferredSize(new Dimension(FINAL_WIDTH, FINAL_HEIGHT));

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
        // Label Atur Bulan/Month
        JLabel bulanLabel = new JLabel("Atur Bulan/Month");
        bulanLabel.setFont(new Font("Arial", Font.BOLD, 14));
        bulanLabel.setBounds(25, 30, 300, 20);
        contentPanel.add(bulanLabel);

        // ComboBox Bulan dengan menggunakan ComboboxCustom
        String[] bulanItems = {"Pilih Bulan", "Januari", "Februari", "Maret", "April", "Mei", "Juni", 
                              "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
        comboBoxBulan = new ComboboxCustom(bulanItems);
        comboBoxBulan.setBounds(25, 60, 300, 38);
        contentPanel.add(comboBoxBulan);

        // Label Atur Tahun/Year
        JLabel tahunLabel = new JLabel("Atur Tahun/Year");
        tahunLabel.setFont(new Font("Arial", Font.BOLD, 14));
        tahunLabel.setBounds(25, 120, 300, 20);
        contentPanel.add(tahunLabel);

        // ComboBox Tahun dengan menggunakan ComboboxCustom
        String[] tahunItems = {"Pilih Tahun", "2023", "2024", "2025", "2026"};
        comboBoxTahun = new ComboboxCustom(tahunItems);
        comboBoxTahun.setBounds(25, 150, 300, 38);
        contentPanel.add(comboBoxTahun);

        // Batal Button
        batalButton = createButton("BATAL", Color.WHITE, Color.BLACK);
        batalButton.setBounds(25, 220, 140, 45);
        batalButton.addActionListener(e -> startCloseAnimation());
        contentPanel.add(batalButton);

        // Terapkan Button
        terapkanButton = createButton("TERAPKAN", new Color(51, 51, 51), Color.WHITE);
        terapkanButton.setBounds(185, 220, 140, 45);
        terapkanButton.addActionListener(e -> terapkan());
        contentPanel.add(terapkanButton);
    }

    private JButton createButton(String text, Color background, Color foreground) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(background);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Jika ini adalah tombol BATAL, tambahkan border
                if (text.equals("BATAL")) {
                    g2.setColor(new Color(200, 200, 200));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                }
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setForeground(foreground);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void terapkan() {
        String bulan = comboBoxBulan.getSelectedItem().toString();
        String tahun = comboBoxTahun.getSelectedItem().toString();

        if (bulan.equals("Pilih Bulan") || tahun.equals("Pilih Tahun")) {
            JOptionPane.showMessageDialog(this, "Mohon pilih bulan dan tahun", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Di sini Anda bisa menambahkan logika untuk menyimpan bulan dan tahun yang dipilih
        // Misalnya dengan mengirim ke database atau menyimpan ke variabel global

        // Setelah berhasil, tutup dialog
        startCloseAnimation();
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