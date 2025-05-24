package SourceCode;

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

        titleLabel = createTextLabel("Edit Restok", 20, 10, 400, 30, new Font("Arial", Font.BOLD, 20), Color.BLACK);
        titleLabel.setVisible(false);
        contentPanel.add(titleLabel);
        
        priceLabel = createTextLabel("Harga Satuan", 70, 40, 300, 20, new Font("Poppins", Font.BOLD, 14), Color.BLACK);
        priceLabel.setVisible(true);
        contentPanel.add(priceLabel);

        priceField = new RoundedTextField(5, "Harga Satuan");
        setRpField(priceField);
        priceField.setBounds(70, 65, 300, 40);
        contentPanel.add(priceField);

        quantityLabel = createTextLabel("Quantity", 70, 110, 300, 20, new Font("Poppins", Font.BOLD, 14), Color.BLACK);
        quantityLabel.setVisible(true);
        contentPanel.add(quantityLabel);

        quantityField = new RoundedTextField(5, "Quantity");
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
        
        priceField.setText("Rp. " + formatter.format(Integer.parseInt(priceStr)));
        quantityField.setText(qtyStr);
    }

    private void updateRowData() {
        try {
            String priceText = priceField.getText().replace("Rp. ", "").replace(".", "").trim();
            String qtyText = quantityField.getText().trim();
            
            if (priceText.isEmpty() || qtyText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Harga dan quantity tidak boleh kosong", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int price = Integer.parseInt(priceText);
            int qty = Integer.parseInt(qtyText);
            
            if (price <= 0 || qty <= 0) {
                JOptionPane.showMessageDialog(this, "Harga dan quantity harus lebih dari 0", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int total = price * qty;

            table.setValueAt("Rp. " + formatter.format(price), row, 3);
            table.setValueAt(qty, row, 4);
            table.setValueAt("Rp. " + formatter.format(total), row, 5);

            startCloseAnimation();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Format harga dan quantity harus angka", "Error", JOptionPane.ERROR_MESSAGE);
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
    
    private void setRpField(final JTextField textField) {
        final String PREFIX = "Rp. ";
        final NumberFormat formatter = NumberFormat.getInstance(new Locale("id", "ID"));
        if (formatter instanceof DecimalFormat) {
            ((DecimalFormat) formatter).applyPattern("#,###");
        }

        if (!textField.getText().startsWith(PREFIX)) {
            textField.setText(PREFIX);
        }

        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {

                if (offset < PREFIX.length()) {
                    return;
                }

                if (!text.matches("\\d*")) {
                    return;
                }

                Document doc = fb.getDocument();
                StringBuilder sb = new StringBuilder(doc.getText(0, doc.getLength()));
                sb.replace(offset, offset + length, text);

                String numericText = sb.toString().substring(PREFIX.length()).replaceAll("[.,]", "");

                if (numericText.length() > 10) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }

                try {
                    if (numericText.isEmpty()) {
                        super.replace(fb, 0, doc.getLength(), PREFIX, attrs);
                        return;
                    }

                    long value = Long.parseLong(numericText);
                    
                    if (value > Integer.MAX_VALUE) {
                        Toolkit.getDefaultToolkit().beep();
                        return;
                    }

                    String formattedText = PREFIX + formatter.format(value);
                    super.replace(fb, 0, doc.getLength(), formattedText, attrs);

                    SwingUtilities.invokeLater(() -> {
                        try {
                            int newPos = Math.min(offset + text.length(), formattedText.length());
                            int addedSeparators = countSeparators(formattedText, offset + text.length());
                            textField.setCaretPosition(Math.min(newPos + addedSeparators, formattedText.length()));
                        } catch (Exception e) {
                            textField.setCaretPosition(formattedText.length());
                        }
                    });
                } catch (NumberFormatException e) {
                    super.replace(fb, 0, doc.getLength(), PREFIX, attrs);
                }
            }

            private int countSeparators(String text, int position) {
                int count = 0;
                for (int i = PREFIX.length(); i < Math.min(position, text.length()); i++) {
                    if (text.charAt(i) == '.' || text.charAt(i) == ',') {
                        count++;
                    }
                }
                return count;
            }

            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                replace(fb, offset, 0, string, attr);
            }

            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                if (offset < PREFIX.length()) {
                    if (offset + length > PREFIX.length()) {
                        length = (offset + length) - PREFIX.length();
                        offset = PREFIX.length();
                    } else {
                        return;
                    }
                }
                replace(fb, offset, length, "", null);
            }
        });

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(PREFIX)) {
                    textField.setCaretPosition(PREFIX.length());
                }
            }
        });

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int caretPos = textField.getCaretPosition();
                if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) 
                        && caretPos <= PREFIX.length()) {
                    textField.setCaretPosition(PREFIX.length());
                    e.consume();
                }
                if (e.getKeyCode() == KeyEvent.VK_HOME) {
                    textField.setCaretPosition(PREFIX.length());
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
            super(placeholder);
            this.radius = radius;
            this.placeholder = placeholder;
            this.isPlaceholderActive = true;
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