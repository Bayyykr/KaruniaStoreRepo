package PopUp_all;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

public class ComboboxSlideCustom extends JPanel {
    private JLabel displayLabel; // Changed from JTextField to JLabel
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
        
        // Changed from JTextField to JLabel to make it non-editable/non-clickable
        displayLabel = new JLabel();
        displayLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
        displayLabel.setBackground(new Color(0, 0, 0, 0)); // Transparent background
        displayLabel.setFont(new Font("Arial", Font.BOLD, 12));
        displayLabel.setHorizontalAlignment(JLabel.LEFT);
        displayLabel.setOpaque(false); // Make the label transparent

        // Navigation buttons
        prevButton = createCircularButton("<");
        nextButton = createCircularButton(">");
        
        prevButton.setPreferredSize(new Dimension(28, 28));
        nextButton.setPreferredSize(new Dimension(28, 28));
    }

    private void setupLayout() {
        // Panel for navigation buttons with additional vertical padding to move buttons down
        JPanel navigationPanel = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                Dimension size = super.getPreferredSize();
                return new Dimension(size.width, size.height);
            }
        };
        navigationPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 2)); // Added vertical padding (2)
        navigationPanel.setOpaque(false);
        navigationPanel.add(prevButton);
        navigationPanel.add(nextButton);
        
        // Main panel
        add(displayLabel, BorderLayout.CENTER);
        add(navigationPanel, BorderLayout.EAST);
    }

    private void initEventListeners() {
        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentIndex > 0) {
                    currentIndex--;
                    updateDisplay();
                    updateNavigationButtons();
                    playButtonClickEffect(prevButton);
                }
            }
        });

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentIndex < options.length - 1) {
                    currentIndex++;
                    updateDisplay();
                    updateNavigationButtons();
                    playButtonClickEffect(nextButton);
                }
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
        // Reset button status each time this is called
        if (options.length <= 1) {
            // If only one or no options, disable both buttons
            prevButton.setEnabled(false);
            nextButton.setEnabled(false);
        } else {
            // Set button status based on current index
            prevButton.setEnabled(currentIndex > 0);
            nextButton.setEnabled(currentIndex < options.length - 1);
        }

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