package produk;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import javax.swing.plaf.ComboBoxUI;

public class ComboboxCustom extends JComboBox<String> {

    private final boolean[] isPopupVisible = {false};
    private final double[] arrowRotation = {0.0};
    private final Timer[] animationTimer = {null};
    private DefaultComboBoxModel<String> originalModel;
    private DefaultComboBoxModel<String> modelWithoutLabel;
    private int topCornerRadius = 15;
    private int maxVisibleRows = 3;

    public ComboboxCustom() {
        super();
        initialize();
    }

    public ComboboxCustom(String[] items) {
        super(items);
        initialize();
        setUpModels(items);
    }

    private void initialize() {
        setRenderer(createCustomRenderer());
        setUI(createCustomUI());
        setBorder(createCustomBorder());
        addPopupMenuListener(createPopupMenuListener());
        addActionListener(createActionListener());
        setBackground(Color.WHITE);
        setOpaque(false);
        setPreferredSize(new Dimension(0, 38));

        setMaximumRowCount(maxVisibleRows);
    }

    private void setUpModels(String[] items) {
        originalModel = new DefaultComboBoxModel<>(items);
        setModel(originalModel);

        modelWithoutLabel = new DefaultComboBoxModel<>();
        modelWithoutLabel.addElement(""); // Item kosong di awal
        for (int i = 1; i < items.length; i++) {
            modelWithoutLabel.addElement(items[i]);
        }
    }

    @Override
    public void addItem(String item) {
        super.addItem(item);

        if (originalModel != null) {
            originalModel.addElement(item);
        }

        if (modelWithoutLabel != null && getItemCount() > 1) {
            modelWithoutLabel.addElement(item);
        } else if (modelWithoutLabel == null && getItemCount() > 1) {
            String[] items = new String[getItemCount()];
            for (int i = 0; i < getItemCount(); i++) {
                items[i] = getItemAt(i);
            }
            setUpModels(items);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth() - 1;
        int height = getHeight() - 1;

        GeneralPath path = new GeneralPath();

        if (!isPopupVisible[0]) {
            path.moveTo(0, 5);
            path.quadTo(0, 0, 5, 0);
            path.lineTo(width - 5, 0);
            path.quadTo(width, 0, width, 5);
            path.lineTo(width, height - 5);
            path.quadTo(width, height, width - 5, height);
            path.lineTo(5, height);
            path.quadTo(0, height, 0, height - 5);
        } else {
            path.moveTo(0, 5);
            path.quadTo(0, 0, 5, 0);
            path.lineTo(width - 5, 0);
            path.quadTo(width, 0, width, 5);
            path.lineTo(width, height);
            path.lineTo(0, height);
        }

        path.closePath();

        g2.setColor(Color.WHITE);
        g2.fill(path);

        g2.setColor(new Color(200, 200, 200));
        g2.draw(path);

        super.paintComponent(g);
        g2.dispose();
    }

    private ListCellRenderer<? super String> createCustomRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                // Sembunyikan item pertama (kosong)
                if (index == 0 && (value == null || value.toString().isEmpty())) {
                    JPanel emptyPanel = new JPanel();
                    emptyPanel.setPreferredSize(new Dimension(0, 0));
                    return emptyPanel;
                }

                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                list.setSelectionBackground(Color.WHITE);
                list.setSelectionForeground(Color.BLACK);

                boolean isComboBoxDisplay = (index == -1);

                if (isComboBoxDisplay) {
                    label.setBackground(Color.WHITE);
                    label.setForeground(Color.BLACK);

                    if (isFirstItem(value)) {
                        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    } else {
                        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    }

                    label.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 5));
                    return label;
                }

                label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                if (isSelected) {
                    label.setBackground(new Color(245, 245, 245));
                    label.setForeground(Color.BLACK);
                } else {
                    label.setBackground(Color.WHITE);
                    label.setForeground(Color.BLACK);
                }

                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(180, 180, 180)),
                        BorderFactory.createEmptyBorder(5, 15, 5, 5)
                ));

                return label;
            }

            private boolean isFirstItem(Object value) {
                if (getItemCount() > 0 && value != null) {
                    return value.equals(getItemAt(0));
                }
                return false;
            }
        };
    }

    private ComboBoxUI createCustomUI() {
        return new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton() {
                    @Override
                    public void paint(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        g2.setColor(getBackground());
                        g2.fillRect(0, 0, getWidth(), getHeight());

                        g2.setColor(new Color(100, 100, 100));

                        int arrowSize = 8;
                        int centerX = getWidth() / 2;
                        int centerY = getHeight() / 2;

                        AffineTransform at = g2.getTransform();
                        g2.translate(centerX, centerY);
                        g2.rotate(Math.toRadians(arrowRotation[0]));

                        Path2D triangle = new Path2D.Double();
                        triangle.moveTo(-arrowSize / 2, -arrowSize / 2);
                        triangle.lineTo(arrowSize / 2, -arrowSize / 2);
                        triangle.lineTo(0, arrowSize / 2);
                        triangle.closePath();

                        g2.fill(triangle);

                        g2.setTransform(at);
                        g2.dispose();
                    }
                };

                button.setBackground(Color.WHITE);
                button.setFocusPainted(false);
                button.setBorderPainted(false);
                button.setContentAreaFilled(false);
                button.setPreferredSize(new Dimension(20, 20));

                return button;
            }

            @Override
            protected ComboPopup createPopup() {
                return new BasicComboPopup(comboBox) {
                    @Override
                    protected JScrollPane createScroller() {
                        JScrollPane scrollPane = super.createScroller();

                        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
                        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
                            @Override
                            protected void configureScrollBarColors() {
                                thumbColor = new Color(0, 0, 0, 0);
                                trackColor = new Color(0, 0, 0, 0);
                            }

                            @Override
                            protected JButton createDecreaseButton(int orientation) {
                                return createZeroButton();
                            }

                            @Override
                            protected JButton createIncreaseButton(int orientation) {
                                return createZeroButton();
                            }

                            private JButton createZeroButton() {
                                JButton button = new JButton();
                                button.setPreferredSize(new Dimension(0, 0));
                                button.setMinimumSize(new Dimension(0, 0));
                                button.setMaximumSize(new Dimension(0, 0));
                                return button;
                            }
                        });

                        scrollPane.setBorder(BorderFactory.createEmptyBorder());

                        return scrollPane;
                    }

                    @Override
                    protected void configurePopup() {
                        super.configurePopup();
                        setBorderPainted(false);
                    }

                    @Override
                    public void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        int width = getWidth() - 1;
                        int height = getHeight() - 1;

                        GeneralPath path = new GeneralPath();

                        if (!isPopupVisible[0]) {
                            path.moveTo(0, 5);
                            path.quadTo(0, 0, 5, 0);
                            path.lineTo(width - 5, 0);
                            path.quadTo(width, 0, width, 5);
                            path.lineTo(width, height - 5);
                            path.quadTo(width, height, width - 5, height);
                            path.lineTo(5, height);
                            path.quadTo(0, height, 0, height - 5);
                        } else {
                            path.moveTo(0, 5);
                            path.quadTo(0, 0, 5, 0);
                            path.lineTo(width - 5, 0);
                            path.quadTo(width, 0, width, 5);
                            path.lineTo(width, height);
                            path.lineTo(0, height);
                        }

                        path.closePath();

                        g2.setColor(Color.WHITE);
                        g2.fill(path);

                        // Tambahkan garis vertikal di sisi kanan
                        g2.setColor(new Color(200, 200, 200));
                        g2.draw(path);

                        // Garis vertikal di sisi kanan
                        g2.drawLine(width, 0, width, height);

                        super.paintComponent(g);
                        g2.dispose();
                    }

                    @Override
                    public void show() {
                        Dimension popupSize = new Dimension(
                                comboBox.getWidth(),
                                63
                        );
                        setPreferredSize(popupSize);

                        super.show();
                    }
                };
            }
        };
    }

    private Border createCustomBorder() {
        return new Border() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GeneralPath path = new GeneralPath();

                if (!isPopupVisible[0]) {
                    // Perbaikan koordinat untuk membuat border lebih rata
                    path.moveTo(x + 5, y);
                    path.quadTo(x, y, x, y + 5);
                    path.lineTo(x, y + height - 5);
                    path.quadTo(x, y + height, x + 5, y + height);
                    path.lineTo(x + width - 5, y + height);
                    path.quadTo(x + width, y + height, x + width, y + height - 5);
                    path.lineTo(x + width, y + 5);
                    path.quadTo(x + width, y, x + width - 5, y);
                } else {
                    path.moveTo(x + 5, y);
                    path.quadTo(x, y, x, y + 5);
                    path.lineTo(x, y + height);
                    path.lineTo(x + width, y + height);
                    path.lineTo(x + width, y + 5);
                    path.quadTo(x + width, y, x + width - 5, y);
                }

                path.closePath();

                g2.setColor(new Color(200, 200, 200));
                g2.draw(path);

                g2.dispose();
            }

            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(5, 15, 5, 15);
            }

            @Override
            public boolean isBorderOpaque() {
                return false;
            }
        };
    }

    private PopupMenuListener createPopupMenuListener() {
        return new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                isPopupVisible[0] = true;

                if (modelWithoutLabel != null) {
                    setModel(modelWithoutLabel);
                    // Selalu kembalikan ke item kosong
                    setSelectedIndex(0);
                }

                repaint();
                startRotationAnimation(0, 180);
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                isPopupVisible[0] = false;

                Object selectedItem = getSelectedItem();

                if (selectedItem != null && !selectedItem.equals("")) {
                    // Jika memilih item valid, tampilkan item tersebut
                    setModel(originalModel);
                    setSelectedItem(selectedItem);
                } else {
                    // Jika tidak memilih apa-apa, kembalikan ke "Merk"
                    setModel(originalModel);
                    setSelectedIndex(0);
                }

                startRotationAnimation(180, 0);
                repaint();
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                isPopupVisible[0] = false;

                // Kembalikan ke "Merk"
                setModel(originalModel);
                setSelectedIndex(0);

                startRotationAnimation(180, 0);
                repaint();
            }
        };
    }

    private void startRotationAnimation(double startAngle, double endAngle) {
        if (animationTimer[0] != null && animationTimer[0].isRunning()) {
            animationTimer[0].stop();
        }

        arrowRotation[0] = startAngle;

        final double totalFrames = 10;
        final double angleIncrement = (endAngle - startAngle) / totalFrames;
        final int[] frame = {0};

        animationTimer[0] = new Timer(15, e -> {
            frame[0]++;

            arrowRotation[0] += angleIncrement;

            repaint();

            if (frame[0] >= totalFrames) {
                arrowRotation[0] = endAngle;
                repaint();
                animationTimer[0].stop();
            }
        });

        animationTimer[0].start();
    }

    private ActionListener createActionListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isPopupVisible[0]) {
                    return;
                }

                // Jika item yang dipilih bukan item pertama (kosong)
                if (getSelectedIndex() > 0) {
                    // Tetap tampilkan item yang dipilih
                    return;
                }

                // Kembalikan ke item kosong/pertama
                setSelectedIndex(0);
                setBackground(Color.WHITE);
                repaint();
            }
        };
    }

    public void setMaxVisibleRows(int maxRows) {
        this.maxVisibleRows = maxRows;
        setMaximumRowCount(maxRows);
    }

    @Override
    public void setSelectedItem(Object anObject) {
        if (anObject == null || anObject.equals("")) {
            // Jika null atau kosong, kembalikan ke item pertama (Merk)
            super.setSelectedItem(getItemAt(0));
        } else {
            // Hanya set item jika bukan item kosong
            super.setSelectedItem(anObject);
        }
    }
}
