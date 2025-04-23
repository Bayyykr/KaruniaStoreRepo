package PopUp_all;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

public class ComboboxSlideCustom extends JPanel {
    private JLabel displayLabel;
    private JButton prevButton;
    private JButton nextButton;
    private String[] options;
    private int currentIndex = 0;
    private int cornerRadius = 15;
    private Color borderColor = Color.LIGHT_GRAY;
    private int borderThickness = 1;
    private boolean showBorder = true;

    public ComboboxSlideCustom(String[] options) {
        this.options = options;
        initComponents();
        setupLayout();
        initEventListeners();
        updateDisplay();
        updateNavigationButtons();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setOpaque(false); // Make the panel transparent
        
        // Label untuk menampilkan teks di tengah
        displayLabel = new JLabel();
        displayLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
        displayLabel.setBackground(new Color(0, 0, 0, 0)); // Transparent background
        displayLabel.setFont(new Font("Arial", Font.BOLD, 12));
        displayLabel.setHorizontalAlignment(JLabel.CENTER); // Teks berada di tengah
        displayLabel.setOpaque(false); // Make the label transparent

        // Tombol navigasi
        prevButton = createCircularButton("<");
        nextButton = createCircularButton(">");
        
        prevButton.setPreferredSize(new Dimension(28, 28));
        nextButton.setPreferredSize(new Dimension(28, 28));
    }

    private void setupLayout() {
        // Panel untuk tombol prev di kiri
        JPanel leftNavPanel = new JPanel();
        leftNavPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 10));
        leftNavPanel.setOpaque(false);
        leftNavPanel.add(prevButton);
        
        // Panel untuk tombol next di kanan
        JPanel rightNavPanel = new JPanel();
        rightNavPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 10));
        rightNavPanel.setOpaque(false);
        rightNavPanel.add(nextButton);
        
        // Panel utama
        add(leftNavPanel, BorderLayout.WEST);
        add(displayLabel, BorderLayout.CENTER);
        add(rightNavPanel, BorderLayout.EAST);
    }

    private void initEventListeners() {
        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Circular navigation - jika di indeks pertama, kembali ke indeks terakhir
                if (currentIndex > 0) {
                    currentIndex--;
                } else {
                    currentIndex = options.length - 1; // Kembali ke indeks terakhir
                }
                updateDisplay();
                playButtonClickEffect(prevButton);
            }
        });

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Circular navigation - jika di indeks terakhir, kembali ke indeks pertama
                if (currentIndex < options.length - 1) {
                    currentIndex++;
                } else {
                    currentIndex = 0; // Kembali ke indeks pertama
                }
                updateDisplay();
                playButtonClickEffect(nextButton);
            }
        });
    }
    
    private void updateDisplay() {
        if (options.length > 0 && currentIndex >= 0 && currentIndex < options.length) {
            displayLabel.setText(options[currentIndex]);
        } else {
            displayLabel.setText("");
        }
    }
    
    private void updateNavigationButtons() {
        // Karena kita menggunakan circular navigation, tombol selalu aktif
        // kecuali jika tidak ada pilihan atau hanya satu pilihan
        boolean hasMultipleOptions = options.length > 1;
        
        prevButton.setEnabled(hasMultipleOptions);
        nextButton.setEnabled(hasMultipleOptions);

        // Improved visual disabled state
        if (!prevButton.isEnabled()) {
            prevButton.setForeground(Color.GRAY);
            prevButton.setBackground(new Color(220, 220, 220));
        } else {
            prevButton.setForeground(Color.BLACK);
            prevButton.setBackground(Color.WHITE);
        }
        
        if (!nextButton.isEnabled()) {
            nextButton.setForeground(Color.GRAY);
            nextButton.setBackground(new Color(220, 220, 220));
        } else {
            nextButton.setForeground(Color.BLACK);
            nextButton.setBackground(Color.WHITE);
        }
    }
    
    // Method to get the currently selected option
    public String getSelectedItem() {
        if (options.length > 0 && currentIndex >= 0 && currentIndex < options.length) {
            return options[currentIndex];
        }
        return null;
    }
    
    // Method to set the selected option by index
    public void setSelectedIndex(int index) {
        if (index >= 0 && index < options.length) {
            currentIndex = index;
            updateDisplay();
            updateNavigationButtons();
        }
    }
    
    // Method to set the selected option by value
    public void setSelectedItem(String item) {
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(item)) {
                setSelectedIndex(i);
                break;
            }
        }
    }
    
    // Method to change the options array
    public void setOptions(String[] options) {
        this.options = options;
        currentIndex = 0;
        updateDisplay();
        updateNavigationButtons();
    }
    
    // Method to get the current index
    public int getSelectedIndex() {
        return currentIndex;
    }
    
    // Method to set border visibility
    public void setShowBorder(boolean showBorder) {
        this.showBorder = showBorder;
        repaint();
    }
    
    private JButton createCircularButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Create circular button
                g2.setColor(getBackground());
                g2.fillOval(1, 1, getWidth() - 2, getHeight() - 2);

                // Draw border
                g2.setColor(isEnabled() ? borderColor : Color.GRAY);
                g2.setStroke(new BasicStroke(borderThickness));
                g2.drawOval(1, 1, getWidth() - 3, getHeight() - 3);

                // Draw text
                if (!getText().isEmpty()) {
                    g2.setColor(isEnabled() ? getForeground() : Color.DARK_GRAY);
                    FontMetrics fm = g2.getFontMetrics();
                    Rectangle2D r = fm.getStringBounds(getText(), g2);
                    int x = (getWidth() - (int) r.getWidth()) / 2;
                    int y = (getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(getText(), x, y);
                }

                g2.dispose();
            }

            @Override
            public boolean isOpaque() {
                return false;
            }
        };

        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    // Method for button click animation effect
    private void playButtonClickEffect(JButton button) {
        // Temporary size reduction to simulate press
        Dimension originalSize = button.getSize();
        button.setSize(originalSize.width - 2, originalSize.height - 2);

        // Create a timer to restore original size
        Timer timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button.setSize(originalSize);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background - semi-transparent white
        g2.setColor(new Color(255, 255, 255, 200));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

        // Border
        if (showBorder) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(borderThickness));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        }

        g2.dispose();
    }

    // Method to set corner radius
    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    // Method to set border color
    public void setBorderColor(Color color) {
        this.borderColor = color;
        repaint();
    }

    // Method to set border thickness
    public void setBorderThickness(int thickness) {
        this.borderThickness = thickness;
        repaint();
    }
}