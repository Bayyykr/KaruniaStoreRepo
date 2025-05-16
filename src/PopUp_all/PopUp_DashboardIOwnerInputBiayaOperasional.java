package PopUp_all;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

public class PopUp_DashboardIOwnerInputBiayaOperasional extends JDialog {

    private JComponent glassPane;
    private JFrame parentFrame;
    private static final int RADIUS = 20;
    private JButton batalButton, simpanButton;
    private JTextField idField, penanggungJawabField;
    private JFormattedTextField totalField;
    private JTextArea catatanArea;
    private JLabel tanggalLabel;

    private int xMouse, yMouse;
    private final int ANIMATION_DURATION = 300; // ms
    private final int ANIMATION_DELAY = 10; // ms
    private float currentScale = 0.01f;
    private boolean animationStarted = false;
    private Timer animationTimer;
    private Timer closeAnimationTimer;
    private final int FINAL_WIDTH = 700; // Diubah dari 500 menjadi 700
    private final int FINAL_HEIGHT = 600;

    // Flag untuk menghindari penambahan glassPane berulang
    private static boolean isShowingPopup = false;

    public PopUp_DashboardIOwnerInputBiayaOperasional(JFrame parent) {
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

        // Title
        JLabel titleLabel = createTextLabel("Input Biaya Operasional", 30, 30, 300, 30,
                new Font("Poppins", Font.BOLD, 22), Color.BLACK);
        contentPanel.add(titleLabel);

        // Tanggal dengan sudut bulat 20px - UKURAN LEBIH KECIL
        String currentDate = "Tanggal: " + new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        JPanel tanggalPanel = new RoundedPanel(RADIUS);
        tanggalPanel.setBounds(FINAL_WIDTH - 180, 30, 150, 30); // Lebar dikurangi dari 200 menjadi 120
        tanggalPanel.setBackground(new Color(226, 240, 255));
        tanggalPanel.setLayout(new BorderLayout());

        tanggalLabel = createTextLabel(currentDate, 0, 0, 120, 30,
                new Font("Poppins", Font.BOLD, 12), new Color(23, 78, 166)); 
        tanggalLabel.setHorizontalAlignment(SwingConstants.CENTER); 
        tanggalLabel.setBorder(new EmptyBorder(5, 5, 5, 5)); 
        tanggalPanel.add(tanggalLabel, BorderLayout.CENTER);
        contentPanel.add(tanggalPanel);

        JPanel dataPanel = new RoundedPanel(RADIUS);
        dataPanel.setLayout(null);
        dataPanel.setBounds(30, 90, FINAL_WIDTH - 60, 85);
        dataPanel.setBackground(new Color(240, 240, 240));
        contentPanel.add(dataPanel);

        // Id
        JLabel idLabel = createTextLabel("Id", 20, 30, 100, 30,
                new Font("Poppins", Font.PLAIN, 18), Color.BLACK);
        dataPanel.add(idLabel);

        idField = createRoundTextField(50, 30, 180, 35);
        idField.setFocusable(false);
        dataPanel.add(idField);

        ((PlainDocument) idField.getDocument()).setDocumentFilter(new DocumentFilter() {
            private final int maxLength = 16;

            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) {
                    return;
                }

                StringBuilder sb = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
                sb.insert(offset, string);

                if (sb.length() <= maxLength && string.matches("\\d+")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) {
                    return;
                }

                StringBuilder sb = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
                sb.replace(offset, offset + length, text);

                if (sb.length() <= maxLength && text.matches("\\d*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        // Penanggung Jawab - PERBAIKAN LEBAR FIELD
        JLabel pjLabel = createTextLabel("Penanggung Jawab", 260, 30, 200, 30,
                new Font("Poppins", Font.BOLD, 16), Color.BLACK);
        dataPanel.add(pjLabel);

        // Memperbaiki lebar field penanggung jawab agar tidak terlihat putih di belakangnya
        penanggungJawabField = createRoundTextField(430, 30, dataPanel.getWidth() - 440, 35);
        penanggungJawabField.setFocusable(false);
        dataPanel.add(penanggungJawabField);

        ((PlainDocument) penanggungJawabField.getDocument()).setDocumentFilter(new DocumentFilter() {
            private final int maxLength = 30;

            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) {
                    return;
                }

                if (fb.getDocument().getLength() + string.length() <= maxLength) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) {
                    return;
                }

                int currentLength = fb.getDocument().getLength();
                int overLimit = (currentLength + text.length()) - length;

                if (overLimit <= maxLength) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        JLabel totalLabel = createTextLabel("Total", 30, 190, 100, 30,
                new Font("Poppins", Font.PLAIN, 18), Color.BLACK);
        contentPanel.add(totalLabel);

        totalField = new JFormattedTextField();
        totalField.setBounds(120, 190, FINAL_WIDTH - 150, 35);
        totalField.setFont(new Font("Poppins", Font.PLAIN, 14));
        totalField.setBorder(new RoundBorder(Color.LIGHT_GRAY, RADIUS, 1));
        totalField.setText("Rp. ");
        
        ((PlainDocument) totalField.getDocument()).setDocumentFilter(new DocumentFilter() {
            private final String prefix = "Rp. ";
            private final int maxDigits = 10; 

            private String formatWithSeparator(String value) {
                // Hapus semua pemisah ribuan yang sudah ada
                String plainNumber = value.replaceAll("\\.", "");

                // Format ulang dengan pemisah ribuan
                StringBuilder result = new StringBuilder();
                int length = plainNumber.length();

                for (int i = 0; i < length; i++) {
                    result.append(plainNumber.charAt(i));
                    if ((length - i - 1) % 3 == 0 && i < length - 1) {
                        result.append('.');
                    }
                }

                return result.toString();
            }

            private int countDigits(String text) {
                return text.replaceAll("\\.", "").length();
            }

            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                try {
                    // Jika mencoba mengedit prefix, abaikan
                    if (offset < prefix.length()) {
                        return;
                    }

                    // Periksa apakah string yang dimasukkan hanya angka
                    if (!string.matches("\\d+")) {
                        return;
                    }

                    // Dapatkan teks saat ini
                    String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                    String textAfterPrefix = "";
                    if (currentText.length() >= prefix.length()) {
                        textAfterPrefix = currentText.substring(prefix.length());
                    }

                    // Hapus semua pemisah ribuan untuk mendapatkan jumlah digit sebenarnya
                    String currentDigits = textAfterPrefix.replaceAll("\\.", "");

                    // Periksa apakah penambahan tidak melebihi batas maksimum digit
                    if (currentDigits.length() + string.length() <= maxDigits) {
                        // Validasi offset
                        int adjustedOffset = Math.min(offset - prefix.length(), textAfterPrefix.length());
                        adjustedOffset = Math.max(0, adjustedOffset);

                        // Tambahkan karakter baru ke teks saat ini dengan pengecekan batas
                        String textBeforeOffset = "";
                        if (adjustedOffset > 0) {
                            textBeforeOffset = textAfterPrefix.substring(0, adjustedOffset);
                        }

                        String textAfterOffset = "";
                        if (adjustedOffset < textAfterPrefix.length()) {
                            textAfterOffset = textAfterPrefix.substring(adjustedOffset);
                        }

                        // Hilangkan semua pemisah terlebih dahulu
                        textBeforeOffset = textBeforeOffset.replaceAll("\\.", "");
                        textAfterOffset = textAfterOffset.replaceAll("\\.", "");
                        string = string.replaceAll("\\.", "");

                        // Gabungkan dan format ulang teks dengan pemisah ribuan
                        String newText = formatWithSeparator(textBeforeOffset + string + textAfterOffset);

                        // Ganti seluruh teks dengan format baru
                        fb.remove(prefix.length(), textAfterPrefix.length());
                        super.insertString(fb, prefix.length(), newText, attr);
                    }
                } catch (Exception e) {
                    // Log error dan pastikan dokumen tetap dalam keadaan valid
                    System.err.println("Error in insertString: " + e.getMessage());
                    // Tidak melakukan perubahan jika terjadi error
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                try {
                    // Jika mencoba mengedit prefix, tangani secara khusus
                    if (offset < prefix.length()) {
                        // Jika pengguna mencoba menghapus/mengubah seluruh konten termasuk prefix
                        if (offset == 0 && length >= prefix.length()) {
                            // Hapus semua konten
                            fb.remove(0, fb.getDocument().getLength());
                            // Kembalikan prefix
                            super.insertString(fb, 0, prefix, attrs);

                            // Jika ada teks baru yang valid, tambahkan dan format
                            if (text != null && text.matches(".*\\d+.*")) {
                                // Ekstrak hanya digit dari teks baru
                                String digits = text.replaceAll("\\D", "");
                                if (digits.length() > 0 && digits.length() <= maxDigits) {
                                    String formattedDigits = formatWithSeparator(digits);
                                    super.insertString(fb, prefix.length(), formattedDigits, attrs);
                                }
                            }
                            return;
                        }
                        return;
                    }

                    // Jika text yang dimasukkan bukan angka, abaikan
                    if (text != null && !text.matches("\\d*")) {
                        return;
                    }

                    // Dapatkan teks saat ini
                    String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                    String textAfterPrefix = "";
                    if (currentText.length() >= prefix.length()) {
                        textAfterPrefix = currentText.substring(prefix.length());
                    }

                    // Hapus semua pemisah ribuan untuk mendapatkan jumlah digit sebenarnya
                    String plainTextAfterPrefix = textAfterPrefix.replaceAll("\\.", "");

                    // Pastikan offset valid dan tidak melebihi panjang string
                    int adjustedOffset = Math.min(offset - prefix.length(), plainTextAfterPrefix.length());
                    adjustedOffset = Math.max(0, adjustedOffset);

                    // Pastikan length valid dan tidak melebihi batas string
                    int adjustedLength = Math.min(length, plainTextAfterPrefix.length() - adjustedOffset);
                    adjustedLength = Math.max(0, adjustedLength);

                    // Hitung teks baru tanpa pemisah dengan pengecekan batas
                    String beforeReplace = plainTextAfterPrefix.substring(0, adjustedOffset);
                    String afterReplace = "";
                    if (adjustedOffset + adjustedLength <= plainTextAfterPrefix.length()) {
                        afterReplace = plainTextAfterPrefix.substring(adjustedOffset + adjustedLength);
                    }

                    String newPlainText = beforeReplace + (text != null ? text : "") + afterReplace;

                    // Periksa apakah tidak melebihi batas maksimum digit
                    if (newPlainText.length() <= maxDigits) {
                        // Hapus seluruh teks kecuali prefix
                        fb.remove(prefix.length(), textAfterPrefix.length());
                        // Masukkan teks terformat baru
                        super.insertString(fb, prefix.length(), formatWithSeparator(newPlainText), attrs);
                    }
                } catch (Exception e) {
                    // Log error dan pastikan dokumen tetap dalam keadaan valid
                    System.err.println("Error in replace: " + e.getMessage());
                    // Kembalikan ke keadaan terakhir yang valid
                    fb.remove(0, fb.getDocument().getLength());
                    super.insertString(fb, 0, prefix, attrs);
                }
            }

            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                try {
                    // Jika mencoba menghapus prefix atau sebagian dari prefix, abaikan
                    if (offset < prefix.length()) {
                        // Jika menghapus semuanya, kembalikan prefix
                        if (offset == 0 && length >= fb.getDocument().getLength()) {
                            fb.replace(0, fb.getDocument().getLength(), prefix, null);
                            return;
                        }
                        return;
                    }

                    // Dapatkan teks saat ini
                    String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                    String textAfterPrefix = "";
                    if (currentText.length() >= prefix.length()) {
                        textAfterPrefix = currentText.substring(prefix.length());
                    }

                    // Hapus pemisah ribuan untuk pemrosesan
                    String plainTextAfterPrefix = textAfterPrefix.replaceAll("\\.", "");

                    // Pastikan offset dan length valid
                    int adjustedOffset = Math.min(offset - prefix.length(), textAfterPrefix.length());
                    adjustedOffset = Math.max(0, adjustedOffset);

                    int adjustedLength = Math.min(length, textAfterPrefix.length() - adjustedOffset);
                    adjustedLength = Math.max(0, adjustedLength);

                    // Hitung posisi dalam teks tanpa pemisah
                    int plainOffset = 0;
                    if (adjustedOffset > 0) {
                        plainOffset = countDigits(textAfterPrefix.substring(0, adjustedOffset));
                    }

                    int plainEndOffset = plainOffset;

                    // Hitung berapa karakter yang akan dihapus dalam teks polos
                    if (adjustedOffset < textAfterPrefix.length()) {
                        String textToRemove = textAfterPrefix.substring(
                                adjustedOffset,
                                Math.min(adjustedOffset + adjustedLength, textAfterPrefix.length())
                        );
                        plainEndOffset += countDigits(textToRemove);
                    }

                    // Buat teks baru tanpa karakter yang dihapus
                    String newPlainText;
                    if (plainOffset <= plainTextAfterPrefix.length()) {
                        String beforeRemove = plainTextAfterPrefix.substring(0, Math.min(plainOffset, plainTextAfterPrefix.length()));
                        String afterRemove = "";
                        if (plainEndOffset < plainTextAfterPrefix.length()) {
                            afterRemove = plainTextAfterPrefix.substring(plainEndOffset);
                        }
                        newPlainText = beforeRemove + afterRemove;
                    } else {
                        newPlainText = "";
                    }

                    // Format ulang teks dan ganti seluruh teks kecuali prefix
                    fb.remove(prefix.length(), textAfterPrefix.length());
                    super.insertString(fb, prefix.length(), formatWithSeparator(newPlainText), null);
                } catch (Exception e) {
                    // Log error dan pastikan dokumen tetap dalam keadaan valid
                    System.err.println("Error in remove: " + e.getMessage());
                    // Kembalikan ke keadaan terakhir yang valid
                    fb.remove(0, fb.getDocument().getLength());
                    // Gunakan null sebagai attribute set karena kita tidak sedang membuat teks baru dengan atribut tertentu
                    super.insertString(fb, 0, prefix, null);
                }
            }
        });

        totalField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (totalField.getText().length() <= 4) {
                    totalField.setCaretPosition(4); // Posisikan tepat setelah "Rp. "
                } else {
                    totalField.setCaretPosition(totalField.getText().length());
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                // Pastikan prefix tetap ada saat kehilangan fokus
                if (!totalField.getText().startsWith("Rp. ")) {
                    totalField.setText("Rp. " + totalField.getText());
                }
            }
        });

        contentPanel.add(totalField);

        // Separator line
        JSeparator separator = new JSeparator();
        separator.setBounds(30, 240, FINAL_WIDTH - 60, 1);
        contentPanel.add(separator);

        // Catatan
        JLabel catatanLabel = createTextLabel("Catatan", 30, 260, 100, 30,
                new Font("Poppins", Font.PLAIN, 18), Color.BLACK);
        contentPanel.add(catatanLabel);

        catatanArea = new JTextArea();
        catatanArea.setFont(new Font("Poppins", Font.PLAIN, 14));
        catatanArea.setLineWrap(true);
        catatanArea.setWrapStyleWord(true);
        catatanArea.setBorder(new RoundBorder(Color.LIGHT_GRAY, RADIUS, 1));

        JScrollPane scrollPane = new JScrollPane(catatanArea);
        scrollPane.setBounds(30, 295, FINAL_WIDTH - 60, 210);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        contentPanel.add(scrollPane);

        // Tombol
        batalButton = createRoundedButton("Batal", 320, 530, 120, 40,
                new Color(240, 240, 240), Color.BLACK);
        batalButton.addActionListener(e -> startCloseAnimation());
        contentPanel.add(batalButton);

        simpanButton = createRoundedButton("Simpan Perubahan", 450, 530, 220, 40,
                new Color(40, 190, 100), Color.WHITE);
        simpanButton.addActionListener(e -> {
            // Logika simpan perubahan akan diimplementasikan di sini
            startCloseAnimation();
        });
        contentPanel.add(simpanButton);

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

    // Konstruktor tanpa parameter
    public PopUp_DashboardIOwnerInputBiayaOperasional() {
        this(null); // Memanggil konstruktor dengan parameter
    }

    // Metode untuk membuat text field dengan sudut bulat
    private JTextField createRoundTextField(int x, int y, int width, int height) {
        JTextField textField = new JTextField();
        textField.setBounds(x, y, width, height);
        textField.setFont(new Font("Poppins", Font.PLAIN, 14));
        textField.setBorder(new RoundBorder(Color.LIGHT_GRAY, RADIUS, 1));
        textField.setBackground(new Color(240, 240, 240));
        return textField;
    }

    // Kelas RoundBorder untuk membuat border bulat pada text field
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

    private JLabel createTextLabel(String text, int x, int y, int width, int height, Font font, Color color) {
        JLabel label = new JLabel(text);
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
        button.setBorderPainted(true);
        button.setForeground(textColor);
        button.setFont(new Font("Poppins", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
}
