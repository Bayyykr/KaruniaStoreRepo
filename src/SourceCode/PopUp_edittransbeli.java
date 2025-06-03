package SourceCode;

import PopUp_all.PindahanAntarPopUp;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.AttributeSet;

public class PopUp_edittransbeli extends JDialog {
    
    private JTable table;
    private int row;
    private JComponent glassPane;
    private static final int RADIUS = 20;
    private JButton cancelButton, updateButton;
    private RoundedTextField priceField, quantityField;
    private JFrame parentFrame;
    private JLabel titleLabel, priceLabel, quantityLabel;
    private final DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(new Locale("id", "ID"));

    private int xMouse, yMouse;
    private final int ANIMATION_DURATION = 300; // ms
    private final int ANIMATION_DELAY = 10; // ms
    private float currentScale = 0.01f;
    private boolean animationStarted = false;
    private Timer animationTimer;
    private Timer closeAnimationTimer;
    private final int FINAL_WIDTH = 450;  // Ukuran tetap
    private final int FINAL_HEIGHT = 230; // Ukuran tetap
    
    private static boolean isShowingPopup = false;

    public PopUp_edittransbeli(JFrame parent, JTable table, int row) {
        super(parent, true);
        this.table = table;
        this.row = row;
        this.parentFrame = parent;
        setModal(true);
        setPreferredSize(new Dimension(FINAL_WIDTH, FINAL_HEIGHT));
        setLayout(null);

        if (isShowingPopup) {
            dispose();
            return;
        }
        isShowingPopup = true;

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

        titleLabel = createTextLabel("Edit Restok", 160, 10, 400, 30, new Font("Arial", Font.BOLD, 20), Color.BLACK);
        titleLabel.setVisible(false);
        contentPanel.add(titleLabel);
        
        priceLabel = createTextLabel("Harga Satuan", 70, 40, 300, 20, new Font("Poppins", Font.BOLD, 14), Color.BLACK);
        priceLabel.setVisible(true);
        contentPanel.add(priceLabel);

        priceField = new RoundedTextField(5, "Harga Satuan");
        setupPriceField(priceField);
        priceField.setBounds(70, 65, 300, 40);
        contentPanel.add(priceField);

        quantityLabel = createTextLabel("Quantity", 70, 110, 300, 20, new Font("Poppins", Font.BOLD, 14), Color.BLACK);
        quantityLabel.setVisible(true);
        contentPanel.add(quantityLabel);

        quantityField = new RoundedTextField(5, "Quantity");
        setupQuantityField(quantityField);
        quantityField.setBounds(70, 135, 300, 40);
        contentPanel.add(quantityField);

        cancelButton = createRoundedButton("Cancel", 60, 190, 130, 30, new Color(0xEBEBEB), Color.BLACK);
        cancelButton.setVisible(false);
        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                startCloseAnimation();
            }
        });
        contentPanel.add(cancelButton);
        
        updateButton = createRoundedButton("Update", 250, 190, 130, 30, new Color(0x34C759), Color.WHITE);
        updateButton.setVisible(false);
        updateButton.addActionListener(e -> updateRowData());
        contentPanel.add(updateButton);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanupAndClose();
            }
        });

        loadRowData();
        startScaleAnimation();
    }

    private void loadRowData() {
        String priceStr = table.getValueAt(row, 3).toString().replace("Rp. ", "").replace(".", "").trim();
        String qtyStr = table.getValueAt(row, 4).toString();
        
        // Set nilai tanpa memicu placeholder logic
        priceField.setActualValue("Rp. " + formatter.format(Integer.parseInt(priceStr)));
        quantityField.setActualValue(qtyStr);
    }

    private void updateRowData() {
        try {
            String priceText = priceField.getText().replace("Rp. ", "").replace(".", "").trim();
            String qtyText = quantityField.getText().trim();
            
            if (priceText.isEmpty() || qtyText.isEmpty()) {
                PindahanAntarPopUp.showEditProductFieldTidakBolehKosong(parentFrame);
                return;
            }

            int price = Integer.parseInt(priceText);
            int qty = Integer.parseInt(qtyText);
            
            if (price <= 0 || qty <= 0) {
                PindahanAntarPopUp.showTransBeliEditQtyDanHargaHarusLebihDari0(parentFrame);
                return;
            }

            int total = price * qty;

            table.setValueAt("Rp. " + formatter.format(price), row, 3);
            table.setValueAt(qty, row, 4);
            table.setValueAt("Rp. " + formatter.format(total), row, 5);
            
            PindahanAntarPopUp.showEditProductBerhasilDiEdit(parentFrame);
            startCloseAnimation();
        } catch (NumberFormatException ex) {
           PindahanAntarPopUp.showTransBeliEditQtyHarusAngka(parentFrame);
        }
    }

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

        animationTimer = new Timer(ANIMATION_DELAY, e -> {
            currentFrame[0]++;

            float progress = (float) currentFrame[0] / totalFrames;
            float easedProgress = 1 - (1 - progress) * (1 - progress) * (1 - progress);

            currentScale = 0.01f + 0.99f * easedProgress;

            if (progress >= 0.3 && !titleLabel.isVisible()) {
                titleLabel.setVisible(true);
                priceField.setVisible(true);
                quantityField.setVisible(true);
                cancelButton.setVisible(true);
                updateButton.setVisible(true);
            }

            repaint();

            if (currentFrame[0] >= totalFrames) {
                animationTimer.stop();
                currentScale = 1.0f;

                titleLabel.setVisible(true);
                priceField.setVisible(true);
                quantityField.setVisible(true);
                cancelButton.setVisible(true);
                updateButton.setVisible(true);

                repaint();
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

        closeAnimationTimer = new Timer(ANIMATION_DELAY, e -> {
            currentFrame[0]++;

            float progress = (float) currentFrame[0] / totalFrames;
            float easedProgress = progress * progress;

            currentScale = 1.0f - 0.99f * easedProgress;

            repaint();

            if (currentFrame[0] >= totalFrames) {
                closeAnimationTimer.stop();
                cleanupAndClose();
            }
        });

        closeAnimationTimer.start();
    }

    private void cleanupAndClose() {
        isShowingPopup = false;
        closePopup();
        dispose();
    }

    private void closePopup() {
        parentLayeredPane().remove(glassPane);
        parentLayeredPane().repaint();
    }

    private JLayeredPane parentLayeredPane() {
        return parentFrame.getLayeredPane();
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
        button.setPressedIcon(button.getIcon());
        button.setRolloverEnabled(false);
        return button;
    }
    
    // Method baru untuk setup price field yang lebih sederhana
    private void setupPriceField(final JTextField textField) {
        final String PREFIX = "Rp. ";
        final NumberFormat formatter = NumberFormat.getInstance(new Locale("id", "ID"));
        
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                
                // Hanya izinkan angka dan backspace
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                    return;
                }
                
                SwingUtilities.invokeLater(() -> {
                    String text = textField.getText();
                    
                    // Jika tidak ada prefix, tambahkan
                    if (!text.startsWith(PREFIX)) {
                        text = PREFIX + text.replace(PREFIX, "");
                    }
                    
                    // Ekstrak angka saja
                    String numbers = text.substring(PREFIX.length()).replaceAll("[^0-9]", "");
                    
                    if (!numbers.isEmpty()) {
                        try {
                            long value = Long.parseLong(numbers);
                            if (value <= Integer.MAX_VALUE) {
                                String formatted = PREFIX + formatter.format(value);
                                if (!textField.getText().equals(formatted)) {
                                    textField.setText(formatted);
                                }
                            }
                        } catch (NumberFormatException ex) {
                            textField.setText(PREFIX);
                        }
                    } else {
                        textField.setText(PREFIX);
                    }
                });
            }
            
            @Override
            public void keyPressed(KeyEvent e) {
                int caretPos = textField.getCaretPosition();
                if (caretPos < PREFIX.length()) {
                    textField.setCaretPosition(PREFIX.length());
                    if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_LEFT) {
                        e.consume();
                    }
                }
            }
        });
        
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                String text = textField.getText();
                if (text.isEmpty()) {
                    textField.setText(PREFIX);
                    textField.setCaretPosition(PREFIX.length());
                }
            }
        });
    }
    
    // Method baru untuk setup quantity field
    private void setupQuantityField(final JTextField textField) {
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                
                // Hanya izinkan angka dan backspace
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                    return;
                }
                
                // Batasi panjang
                if (textField.getText().length() >= 8 && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
            }
        });
    }

    private JLabel createTextLabel(String text, int x, int y, int width, int height, Font font, Color color) {
        JLabel label = new JLabel(text, SwingConstants.LEFT);
        label.setBounds(x, y, width, height);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    public static class RoundedBorder extends AbstractBorder {
        private int radius;

        public RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius, radius, radius, radius);
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(Color.BLACK);
            g.fillRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
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
                
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
                
                g2.setTransform(originalTransform);
            } else {
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
     
    static class RoundedTextField extends JTextField {
        private int radius;
        private String placeholder;
        private boolean isPlaceholderActive;

        public RoundedTextField(int radius, String placeholder) {
            super();
            this.radius = radius;
            this.placeholder = placeholder;
            this.isPlaceholderActive = true;
            setText(placeholder);
            setForeground(Color.GRAY);
            setOpaque(false);
            setBorder(new EmptyBorder(5, 10, 5, 10));

            addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (getText().equals(placeholder) && isPlaceholderActive) {
                        setText("");
                        setForeground(Color.BLACK);
                        isPlaceholderActive = false;
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (getText().isEmpty()) {
                        setText(placeholder);
                        setForeground(Color.GRAY);
                        isPlaceholderActive = true;
                    }
                }
            });
        }
        
        // Method untuk set nilai tanpa memicu placeholder logic
        public void setActualValue(String value) {
            if (value != null && !value.trim().isEmpty()) {
                isPlaceholderActive = false;
                setText(value);
                setForeground(Color.BLACK);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.setColor(Color.GRAY);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}