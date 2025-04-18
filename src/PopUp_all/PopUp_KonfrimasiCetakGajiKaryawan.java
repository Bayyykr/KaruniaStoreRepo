package PopUp_all;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import javax.swing.*;
import javax.swing.table.*;
import java.util.Vector;
import SourceCode.*;

public class PopUp_KonfrimasiCetakGajiKaryawan extends JDialog {

    private JComponent glassPane;
    private JFrame parentFrame;
    private JPanel contentPanel;
    private JTableRounded tableGaji; // Using the proper JTableRounded class
    private JButton batalButton, bayarButton;

    private final int RADIUS = 20;
    private final int FINAL_WIDTH = 450;
    private final int FINAL_HEIGHT = 550; // Menambah tinggi dialog dari 500 menjadi 550
    private final int ANIMATION_DURATION = 300;
    private final int ANIMATION_DELAY = 10;
    private float currentScale = 0.01f;
    private boolean animationStarted = false;
    private Timer animationTimer;
    private Timer closeAnimationTimer;
    private PaymentCallback paymentCallback;
    private int rowIndex;

    // Flag to avoid adding glassPane multiple times
    private static boolean isShowingPopup = false;
    
    // Tambahkan interface callback
    public interface PaymentCallback {
        void onPaymentSuccess(int rowIndex);
    }
    
    // Di PopUp_DetailGajiKaryawan.java
    public void setPaymentCallback(PaymentCallback callback, int rowIndex) {
        this.paymentCallback = callback;
        this.rowIndex = rowIndex;
    }
    
    // Constructor without parameters
    public PopUp_KonfrimasiCetakGajiKaryawan() {
        this(null); // Call the constructor with parameters
    }

    public PopUp_KonfrimasiCetakGajiKaryawan(JFrame parent) {
        super(parent, "Form Gaji", true);
        this.parentFrame = parent;
        setModal(true);
        setPreferredSize(new Dimension(FINAL_WIDTH, FINAL_HEIGHT));

        // Check if popup is already being displayed
        if (isShowingPopup) {
            dispose();
            return;
        }
        isShowingPopup = true;

        // Create transparent overlay with semi-transparent black color
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

        // Add WindowListener to clean up when popup is closed
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanupAndClose();
            }
        });

        startScaleAnimation();
    }

    private void createComponents() {
        // Form Title
        JLabel titleLabel = new JLabel("Form Gaji");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(0, 20, FINAL_WIDTH, 30);
        contentPanel.add(titleLabel);

        // Create table columns
        String[] columnNames = {"Informasi", "Keterangan"};

        // Create JTableRounded using the proper implementation
        tableGaji = new JTableRounded(columnNames);
        tableGaji.setSize(FINAL_WIDTH - 50, 380);

        // Add data rows to the table
        tableGaji.addRow(new Object[]{"Nomor RFID", "29281272"});
        tableGaji.addRow(new Object[]{"Nama", "Greyson"});
        tableGaji.addRow(new Object[]{"Jabatan", "Karyawan"});
        tableGaji.addRow(new Object[]{"Jumlah Perhari", "29"});
        tableGaji.addRow(new Object[]{"Gaji Pokok", "Rp. 3.000.000,00"});
        tableGaji.addRow(new Object[]{"Jumlah Lembur", "3 jam"});
        tableGaji.addRow(new Object[]{"Gaji Lembur", "Rp. 15.000,00/jam"});
        tableGaji.addRow(new Object[]{"Total", "Rp. 3.045.000,00"});
        tableGaji.addRow(new Object[]{"Tanggal Pembayaran", "30 Januari 2025"});
        tableGaji.addRow(new Object[]{"Dibayar Oleh", "Owner (Admin)"});
        tableGaji.addRow(new Object[]{"Status", "Dibayar"});

        // Configure table formatting
        JTable table = tableGaji.getTable();
        table.setRowHeight(30);
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(230, 230, 230));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setEnabled(false); // Make table non-editable

        // Set column widths
        tableGaji.setColumnWidth(0, 150);
        tableGaji.setColumnWidth(1, 250);

        // Set custom cell renderer for alignment
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

                if (column == 0) {
                    label.setHorizontalAlignment(JLabel.LEFT);
                } else {
                    label.setHorizontalAlignment(JLabel.CENTER);
                }
                return label;
            }
        };

        // Apply renderer to all columns
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        // Lock table to prevent column reordering and resizing
        tableGaji.lockTablePosition();
        tableGaji.setColumnsResizable(false);

        // Place table in a scrollable container to ensure all rows are visible
        JScrollPane scrollPane = new JScrollPane(tableGaji.getTable());
        scrollPane.setBounds(25, 60, FINAL_WIDTH - 50, 380); // Menambah tinggi table dari 330 menjadi 380
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // Memastikan scroll pane menampilkan semua data
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        contentPanel.add(scrollPane);

        // Cancel Button
        batalButton = createButton("BATAL", new Color(255, 77, 77), Color.WHITE);
        batalButton.setBounds(25, FINAL_HEIGHT - 70, (FINAL_WIDTH - 60) / 2, 45);
        batalButton.addActionListener(e -> startCloseAnimation());
        contentPanel.add(batalButton);

        // Pay Button
        bayarButton = createButton("CETAK", new Color(38, 211, 103), Color.WHITE);
        bayarButton.setBounds(25 + (FINAL_WIDTH - 60) / 2 + 10, FINAL_HEIGHT - 70, (FINAL_WIDTH - 60) / 2, 45);
        bayarButton.addActionListener(e -> bayarGaji());
        contentPanel.add(bayarButton);
    }

    private JButton createButton(String text, Color background, Color foreground) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(background);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
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

    private void bayarGaji() {
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
        // Reset flag when popup is closed
        isShowingPopup = false;
        // Remove glassPane
        closePopup();
        // Close dialog
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