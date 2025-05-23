package PopUp_all;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.Document;
import java.util.ArrayList;

public class PopUp_SmallAturDiskonOwnerDiskonTidakBolehKosong extends JDialog {

    private JFrame parentFrame;
    private static final int RADIUS = 10;
    private JLabel iconLabel, successLabel, descriptionLabel, closeLabel;
    private JTextField leftTextField;

    private final int ANIMATION_DELAY = 10; // ms
    private int currentX;
    private int targetX; // Target posisi X setelah animasi
    private boolean animationStarted = false;
    private Timer animationTimer;
    private Timer autoCloseTimer;
    private final int FINAL_WIDTH = 250;
    private final int FINAL_HEIGHT = 50;

    // Jarak antara pop-ups (vertikal)
    private static final int POPUP_VERTICAL_GAP = 10;

    // Untuk mengelola multiple popups
    private static ArrayList<PopUp_SmallAturDiskonOwnerDiskonTidakBolehKosong> activePopups = new ArrayList<>();

    // Untuk animasi kedip
    private float opacity = 0.0f;
    private boolean fadingIn = true;

    public PopUp_SmallAturDiskonOwnerDiskonTidakBolehKosong(JFrame parent) {
        super(parent, false); // Non-modal dialog
        this.parentFrame = parent;
        setPreferredSize(new Dimension(FINAL_WIDTH, FINAL_HEIGHT));
        setLayout(null);

        setUndecorated(true);
        setSize(FINAL_WIDTH, FINAL_HEIGHT);

        // Set posisi awal X (lebih ke kanan)
        if (parentFrame != null) {
            // Pindahkan pop-up lebih ke kanan dengan menambah nilai offset
            currentX = parentFrame.getX() + parentFrame.getWidth(); // Mulai dari luar layar di kanan
        } else {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            currentX = screenSize.width; // Mulai dari luar layar di kanan
        }

        // Posisi Y - akan dihitung berdasarkan popups yang sudah aktif
        int positionY = calculateVerticalPosition();

        setLocation(currentX, positionY);
        setBackground(new Color(0, 0, 0, 0));
        setLayout(null);

        // Set opacity awal ke 0 untuk animasi kedip
        setOpacity(1.0f); // Ubah ke 1.0f karena kita akan menggunakan animasi geser, bukan fade

        JPanel contentPanel = new RoundedPanel(RADIUS);
        contentPanel.setLayout(null);
        contentPanel.setBounds(0, 0, FINAL_WIDTH, FINAL_HEIGHT);
        contentPanel.setBackground(new Color(254, 95, 86)); // Background merah sangat muda
        add(contentPanel);

        // Text field di kiri
        leftTextField = createLeftTextField(new Color(254, 95, 86), 1, 0, 30, FINAL_HEIGHT, RADIUS);
        contentPanel.add(leftTextField);

        // Icon centang hijau (diperkecil) - digeser lebih ke atas
        iconLabel = createLabel("/SourceImage/icon_seru_merah.png", 15, 8, 20, 20);
        contentPanel.add(iconLabel);

        // Label Success
        successLabel = createTextLabel("Gagal", 40, 5, 100, 20, new Font("poppins", Font.BOLD, 14), new Color(0, 0, 0));
        contentPanel.add(successLabel);

        // Label deskripsi
        descriptionLabel = createTextLabel("Nilai Diskon Tidak Boleh Kosong!!", 40, 25, 250, 20, new Font("poppins", Font.PLAIN, 11), new Color(90, 90, 90));
        contentPanel.add(descriptionLabel);

        // Label close (X) - di pojok kanan atas
        closeLabel = createTextLabel("×", 230, 0, 20, 20, new Font("Arial", Font.BOLD, 18), new Color(100, 100, 100));
        closeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                startCloseAnimation();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                closeLabel.setForeground(Color.BLACK);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeLabel.setForeground(new Color(100, 100, 100));
            }
        });
        contentPanel.add(closeLabel);

        // Tambahkan popup ini ke daftar aktif
        activePopups.add(0, this); // Tambahkan di awal list sehingga popup baru berada di atas

        // Atur ulang posisi semua popup yang ada
        repositionActivePopups();

        // Tentukan posisi target
        calculateTargetPosition();

        // Tambahkan WindowListener untuk membersihkan saat popup ditutup
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanupAndClose();
            }
        });

        // Pastikan popup selalu di depan
        setAlwaysOnTop(true);

        // Memulai animasi dari kanan ke kiri
        startEnterAnimation();

        // Timer untuk menutup otomatis setelah 3 detik
        autoCloseTimer = new Timer(3000, e -> {
            startCloseAnimation();
        });
        autoCloseTimer.setRepeats(false);
        autoCloseTimer.start();
    }

    // Konstruktor tanpa parameter
    public PopUp_SmallAturDiskonOwnerDiskonTidakBolehKosong() {
        this(null); // Memanggil konstruktor dengan parameter
    }

    private int calculateVerticalPosition() {
        int baseY = 85; // Posisi default

        // Jika parent tidak null, gunakan bounds parent untuk posisi relatif
        if (parentFrame != null) {
            baseY = parentFrame.getY() + 85;
        }

        return baseY; // Selalu kembalikan posisi dasar, karena tumpukan popup akan diatur ulang
    }

    private void repositionActivePopups() {
        int baseY = 85;
        if (parentFrame != null) {
            baseY = parentFrame.getY() + 85;
        }

        // Hitung posisi target X berdasarkan parent frame atau layar
        int baseX;
        if (parentFrame != null) {
            // Posisikan dekat dengan parent frame, lebih ke kanan
            baseX = parentFrame.getX() + parentFrame.getWidth() - FINAL_WIDTH - 10; // Ubah dari 20 menjadi 10
        } else {
            // Jika tidak ada parent frame, gunakan posisi dari kanan layar
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            baseX = screenSize.width - FINAL_WIDTH - 10;
        }

        // Atur posisi dari atas ke bawah (popup terbaru di atas)
        for (int i = 0; i < activePopups.size(); i++) {
            PopUp_SmallAturDiskonOwnerDiskonTidakBolehKosong popup = activePopups.get(i);
            int newY = baseY + (i * (FINAL_HEIGHT + POPUP_VERTICAL_GAP));

            // Hanya ubah posisi Y, biarkan X sesuai dengan animasi
            popup.setLocation(popup.getX(), newY);
            
            // Update target X untuk popup ini
            popup.targetX = baseX;

            // Atur z-index agar popup terbaru selalu di depan
            // Popup dengan index lebih kecil harus berada di depan yang lain
            try {
                popup.setComponentZOrder(popup, activePopups.size() - i - 1);
            } catch (Exception e) {
                // Ignore - jika setComponentZOrder gagal
            }

            // Pastikan popup selalu di depan
            popup.toFront();
        }
    }

    // Metode untuk menghitung posisi target
    private void calculateTargetPosition() {
        // Targetnya adalah posisi X dari pop-up ini setelah repositioning
        if (parentFrame != null) {
            this.targetX = parentFrame.getX() + parentFrame.getWidth() - FINAL_WIDTH - 10;
        } else {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            this.targetX = screenSize.width - FINAL_WIDTH - 10;
        }
    }

    // Animasi masuk dari kanan ke kiri
    private void startEnterAnimation() {
        if (animationStarted || (animationTimer != null && animationTimer.isRunning())) {
            return;
        }

        animationStarted = true;
        setVisible(true);

        // Animasi geser dari kanan ke kiri
        animationTimer = new Timer(ANIMATION_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Geser dari kanan ke kiri
                if (currentX > targetX) {
                    currentX -= 10; // Kecepatan geser
                    if (currentX <= targetX) {
                        currentX = targetX;
                        animationTimer.stop();
                        animationStarted = false;
                    }
                    setLocation(currentX, getY());
                }
            }
        });

        // Selalu tampilkan di depan
        toFront();
        animationTimer.start();
    }

    private void startCloseAnimation() {
        if (autoCloseTimer.isRunning()) {
            autoCloseTimer.stop();
        }

        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        // Animasi geser dari kiri ke kanan (keluar layar)
        currentX = getX();
        int closeTargetX;
        
        if (parentFrame != null) {
            closeTargetX = parentFrame.getX() + parentFrame.getWidth() + 50; // Geser ke kanan luar layar
        } else {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            closeTargetX = screenSize.width + 50; // Geser ke kanan luar layar
        }
        
        animationTimer = new Timer(ANIMATION_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Geser dari kiri ke kanan
                currentX += 10; // Kecepatan geser
                if (currentX >= closeTargetX) {
                    animationTimer.stop();
                    cleanupAndClose();
                }
                setLocation(currentX, getY());
            }
        });

        animationTimer.start();
    }

    private void cleanupAndClose() {
        // Hapus popup ini dari daftar aktif
        activePopups.remove(this);

        // Reposisi popup yang masih aktif
        repositionActivePopups();

        // Tutup dialog
        dispose();
    }

    private JLabel createLabel(String path, int x, int y, int width, int height) {
        JLabel label = new JLabel();
        label.setBounds(x, y, width, height);
        ImageIcon icon = new ImageIcon(getClass().getResource(path));
        if (icon.getIconWidth() == -1) {
            System.out.println("Gambar tidak ditemukan: " + path);
        } else {
            // Resize gambar sesuai ukuran yang diinginkan
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(img));
        }
        return label;
    }

    private JLabel createTextLabel(String text, int x, int y, int width, int height, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, width, height);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    private JTextField createLeftTextField(Color color, int x, int y, int width, int height, int radius) {
        JTextField textField = new JTextField();
        textField.setBounds(x, y, width, height);
        textField.setOpaque(false);
        textField.setBorder(new RoundedLeftBorder(color, radius));
        textField.setEditable(false); // Agar tidak bisa diedit
        return textField;
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

            // Background untuk panel
            g2.setColor(new Color(255, 233, 220));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

            // Border merah
            g2.setColor(new Color(254, 95, 86));
            g2.setStroke(new BasicStroke(1));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

            g2.dispose();
        }
    }

    // Kelas untuk border kiri dengan sudut membulat
    static class RoundedLeftBorder extends EmptyBorder {
        private Color color;
        private int radius;

        RoundedLeftBorder(Color color, int radius) {
            super(0, 0, 0, 0); // Insets: top, left, bottom, right
            this.color = color;
            this.radius = 5;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            // Membuat bentuk rounded di sisi kiri
            g2.fillRoundRect(x, y, radius, height, radius, radius);
            g2.dispose();
        }
    }

    // Metode untuk menampilkan popup
    public static void showPopup(JFrame parent) {
        SwingUtilities.invokeLater(() -> {
            new PopUp_SmallTransJualSuksesBayar(parent);
        });
    }

    // Metode untuk menampilkan popup dengan posisi kustom
    public static void showPopup(JFrame parent, int x, int y) {
        SwingUtilities.invokeLater(() -> {
            PopUp_SmallAturDiskonOwnerDiskonTidakBolehKosong popup = new PopUp_SmallAturDiskonOwnerDiskonTidakBolehKosong(parent);
            popup.setLocationOnClose(x, y);
        });
    }

    // Metode untuk mengatur posisi akhir setelah animasi
    private void setLocationOnClose(int x, int y) {
        // Menghentikan timer animasi yang sedang berjalan
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        // Mengatur posisi tetap
        targetX = x;
        setLocation(x, y);
    }
}