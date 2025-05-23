package PopUp_all;

import SourceCode.RoundedButton;
import SourceCode.ScrollPane;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.AffineTransform;
import java.sql.*;
import db.conn;
import java.util.ArrayList;
import java.util.List;

public class PopUp_aturdiskon extends JDialog {

    Component parentComponent = this;
    private JComponent glassPane;
    private JFrame parentFrame;
    private JPanel contentPanel;
    private JTextField totalDiskonField, namaDiskonField, kodePromo;
    private JLabel aturDiskonLabel, listDiskonLabel;
    private JPanel aturDiskonIndicator, listDiskonIndicator, listItemPanel;
    private JPanel currentActivePanel;
    private JPanel aturDiskonPanel, listDiskonPanel;
    private DefaultTableModel tableModel;
    private JTable diskonTable;

    private final int RADIUS = 20;
    private final int FINAL_WIDTH = 500;
    private final int FINAL_HEIGHT = 350;
    private final int ANIMATION_DURATION = 300;
    private final int ANIMATION_DELAY = 10;
    private float currentScale = 0.01f;
    private boolean animationStarted = false;
    private Timer animationTimer;
    private Timer closeAnimationTimer;
    // Variabel untuk melacak item yang sedang diedit
    private EditableListItem currentEditingItem = null;
    private boolean isEditing = false;
    // Menambahkan metode untuk menyimpan status edit saat berpindah tab
    private boolean pendingEdit = false;
    private String pendingEditValue = "";

    // Flag untuk menghindari penambahan glassPane berulang
    private static boolean isShowingPopup = false;
    private static final String ID_PREFIX = "DS_";
    private static final int PADDING_LENGTH = 2;
    private Connection con;

    // Konstruktor tanpa parameter
    public PopUp_aturdiskon() {
        this(null); // Memanggil konstruktor dengan parameter
    }

    public PopUp_aturdiskon(JFrame parent) {
        super(parent, "Atur Diskon", true);
        this.parentFrame = parent;
        setModal(true);
        setPreferredSize(new Dimension(FINAL_WIDTH, FINAL_HEIGHT));
        con = conn.getConnection();

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

        JButton closeButton = createCloseButton();
        contentPanel.add(closeButton);

        createTabLabels();
        createPanels();

        // Tambahkan WindowListener untuk membersihkan saat popup ditutup
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanupAndClose();
            }
        });

        startScaleAnimation();
    }

    private void createTabLabels() {
        aturDiskonLabel = new JLabel("Atur Diskon");
        aturDiskonLabel.setFont(new Font("Arial", Font.BOLD, 14));
        aturDiskonLabel.setBounds(30, 20, 150, 30);
        aturDiskonLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        contentPanel.add(aturDiskonLabel);

        listDiskonLabel = new JLabel("List Diskon");
        listDiskonLabel.setFont(new Font("Arial", Font.BOLD, 14));
        listDiskonLabel.setBounds(180, 20, 150, 30);
        listDiskonLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        contentPanel.add(listDiskonLabel);

        aturDiskonIndicator = createTabIndicator(20);
        listDiskonIndicator = createTabIndicator(170);
        contentPanel.add(aturDiskonIndicator);
        contentPanel.add(listDiskonIndicator);

        addTabSwitchListeners();
    }

    private JPanel createTabIndicator(int x) {
        JPanel indicator = new JPanel();
        indicator.setBackground(Color.BLACK);
        indicator.setBounds(x, 50, 100, 2);
        indicator.setVisible(false);
        return indicator;
    }

    private void addTabSwitchListeners() {
        aturDiskonLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                switchToPanel(aturDiskonPanel, aturDiskonIndicator, listDiskonIndicator);
            }
        });

        listDiskonLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                switchToPanel(listDiskonPanel, listDiskonIndicator, aturDiskonIndicator);
            }
        });
    }

    private void switchToPanel(JPanel targetPanel, JPanel showIndicator, JPanel hideIndicator) {
        // Jika sedang dalam mode edit dan beralih ke panel Atur Diskon, hentikan mode edit
        if (isEditing && targetPanel == aturDiskonPanel) {
            // Simpan status edit sementara jika diperlukan
            pendingEdit = true;
            pendingEditValue = currentEditingItem != null
                    ? currentEditingItem.percentageEditField.getText() : "";

            // Temukan tombol Simpan dan Batal berdasarkan teks
            JButton saveButton = findButton("Simpan");
            JButton cancelButton = findButton("Batal");

            // Hentikan mode edit
            endEditing(saveButton, cancelButton);

            // Reset scroll pane di listDiskonPanel
            resetScrollPane(listDiskonPanel);
        }
        // Jika berpindah ke List Diskon, kosongkan field totalDiskonField & namaDiskonField
        if (targetPanel == listDiskonPanel) {
            totalDiskonField.setText("");
            namaDiskonField.setText("");
        }
        contentPanel.remove(currentActivePanel);
        contentPanel.add(targetPanel);
        showIndicator.setVisible(true);
        hideIndicator.setVisible(false);
        currentActivePanel = targetPanel;
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void resetScrollPane(JPanel panel) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) comp;
                JViewport viewport = scrollPane.getViewport();
                viewport.setViewPosition(new Point(0, 0)); // Reset ke posisi atas
            }
        }
    }

    private void createPanels() {
        aturDiskonPanel = createAturDiskonPanel();
        listDiskonPanel = createListDiskonPanel();

        currentActivePanel = aturDiskonPanel;
        contentPanel.add(currentActivePanel);
        aturDiskonIndicator.setVisible(true);
    }

    private JButton createCloseButton() {
        JButton closeButton = new JButton("×");
        closeButton.setBounds(460, 10, 35, 35);
        closeButton.setFont(new Font("Poppins", Font.BOLD, 20));
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setForeground(Color.BLACK);
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> startCloseAnimation());
        return closeButton;
    }

    private JButton createSimpanButton() {
        JButton simpanButton = new JButton("Simpan") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gambar background
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);

                // Tambahkan border
                g2.setColor(Color.BLACK); // Warna border
                g2.setStroke(new BasicStroke(1f)); // Ketebalan border
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 40, 40);

                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            public void updateUI() {
                super.updateUI();
                setContentAreaFilled(false);
                setFocusPainted(false);
                setBorderPainted(false);
            }
        };
        simpanButton.setBackground(new Color(52, 199, 89));
        simpanButton.setForeground(Color.WHITE);
        simpanButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return simpanButton;
    }

    private JTextField createInvisibleTextField(boolean withPercentSymbol) {
        JTextField textField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);

                // If percent symbol is required, draw a black rectangle on the right
                if (withPercentSymbol) {
                    g2.setColor(new Color(64, 72, 82));
                    int rightPadding = 10;
                    g2.fillRoundRect(getWidth() - 40, 5, 40 - rightPadding, getHeight() - 10, 15, 15);

                    // Draw percent symbol
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Poppins", Font.BOLD, 16));
                    FontMetrics fm = g2.getFontMetrics();
                    int symbolWidth = fm.stringWidth("%");
                    int symbolHeight = fm.getHeight();

                    g2.drawString("%",
                            getWidth() - 40 + (40 - symbolWidth) / 2 - 5,
                            (getHeight() + symbolHeight) / 2 - 5
                    );
                }

                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            public void updateUI() {
                super.updateUI();
                setOpaque(false);
                setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 50)); // Adjusted padding
            }
        };
        textField.setBackground(Color.WHITE);
        return textField;
    }

    private JPanel createAturDiskonPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(Color.WHITE);
        panel.setBounds(30, 70, 440, 250);

        // Total Diskon Label
        JLabel totalDiskonLabel = new JLabel("Total Diskon");
        totalDiskonLabel.setFont(new Font("Arial", Font.BOLD, 12));
        totalDiskonLabel.setBounds(0, 0, 150, 20);
        panel.add(totalDiskonLabel);

        kodePromo = new JTextField();
        kodePromo.setVisible(false);
        kodePromo.setText(generateNextTransaksiId());

        // Total Diskon Container (Hanya angka 0-100)
        totalDiskonField = createInvisibleTextField(true);
        totalDiskonField.setBounds(0, 25, 440, 45);
        totalDiskonField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                String currentText = totalDiskonField.getText();

                // Hanya angka yang diperbolehkan
                if (!Character.isDigit(c)) {
                    e.consume();
                    return;
                }

                // Mencegah angka 0 berurutan di awal
                if (currentText.equals("0") && c == '0') {
                    e.consume();
                    return;
                }

                // Jika text saat ini adalah "0" dan mengetik angka lain, ganti "0"
                if (currentText.equals("0") && c != '0') {
                    totalDiskonField.setText("");
                    return;
                }

                // Cek batasan 0-100
                String newText = currentText + c;
                if (!newText.isEmpty()) {
                    try {
                        int value = Integer.parseInt(newText);
                        if (value > 100) {
                            e.consume();
                        }
                    } catch (NumberFormatException ex) {
                        e.consume();
                    }
                }
            }
        });
        panel.add(totalDiskonField);

        // Nama Diskon Label
        JLabel namaDiskonLabel = new JLabel("Nama Diskon");
        namaDiskonLabel.setFont(new Font("Arial", Font.BOLD, 12));
        namaDiskonLabel.setBounds(0, 90, 150, 20);
        panel.add(namaDiskonLabel);

        // Nama Diskon Container (Maksimal 20 karakter)
        namaDiskonField = createInvisibleTextField(false);
        namaDiskonField.setBounds(0, 115, 440, 45);
        namaDiskonField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (namaDiskonField.getText().length() >= 30) {
                    e.consume(); // Mencegah input lebih dari 20 karakter
                }
            }
        });
        panel.add(namaDiskonField);

        JButton simpanButton = createSimpanButton();
        simpanButton.setBounds(140, 210, 160, 35);
        simpanButton.addActionListener(e -> simpanDiskon());
        panel.add(simpanButton);

        return panel;
    }

    private JPanel createListDiskonPanel() {
        JPanel panel = new RoundedPanel(20);
    panel.setBackground(Color.WHITE);
    panel.setBounds(30, 70, 440, 300);
    panel.setLayout(null);

    JButton saveButton = createRoundedButton("Simpan", new Color(52, 199, 89), Color.WHITE);
    saveButton.setBounds(270, 230, 160, 35);
    saveButton.setVisible(false);

    JButton cancelButton = createRoundedButton("Batal", Color.RED, Color.WHITE);
    cancelButton.setBounds(20, 230, 160, 35);
    cancelButton.setVisible(false);

    panel.add(saveButton);
    panel.add(cancelButton);

    listItemPanel = new JPanel();
    listItemPanel.setLayout(new BoxLayout(listItemPanel, BoxLayout.Y_AXIS));
    listItemPanel.setBackground(Color.WHITE);

    List<String[]> diskonList = fetchDiskonData();
    EditableListItem[] items = new EditableListItem[diskonList.size()];

    for (int i = 0; i < diskonList.size(); i++) {
        String[] diskon = diskonList.get(i);
        items[i] = createRoundedListItem(diskon[0], diskon[1], i == 0);
        listItemPanel.add(items[i].itemPanel);
  
        final EditableListItem currentItem = items[i];
        items[i].percentagePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isEditing || currentEditingItem == currentItem) {
                    startEditing(currentItem, saveButton, cancelButton);
                }
            }
        });
        
        if (i < diskonList.size() - 1) {
            listItemPanel.add(Box.createVerticalStrut(10));
        }
    }

    ScrollPane scrollPane = new ScrollPane(listItemPanel,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setBounds(0, 0, 440, 210);
    panel.add(scrollPane);

//        for (EditableListItem item : items) {
//            item.percentagePanel.addMouseListener(new MouseAdapter() {
//                @Override
//                public void mouseClicked(MouseEvent e) {
//                    // Cek apakah sedang mengedit item lain
//                    if (!isEditing || currentEditingItem == item) {
//                        // Mulai mode edit
//                        startEditing(item, saveButton, cancelButton);
//                    }
//                }
//            });
//        }

        saveButton.addActionListener(e -> {
    if (currentEditingItem != null) {
        String newPercentage = currentEditingItem.percentageEditField.getText().trim();
        
        if (newPercentage.isEmpty()) {
            PindahanAntarPopUp.showAturDiskonOwnerDiskonTidakBolehKosong(parentFrame);
            return;
        }
        
        try {
            int value = Integer.parseInt(newPercentage);
            if (value < 0 || value > 100) {
                PindahanAntarPopUp.showAturDiskonOwnerAngkaHarus0sampai100(parentFrame);
                return;
            }
        } catch (NumberFormatException ex) {
            PindahanAntarPopUp.showAturDiskonOwnerAngkaHarus0sampai100(parentFrame);
            return;
        }

        // Simpan perubahan
        currentEditingItem.percentageLabel.setText(newPercentage + " %");

        // Update database dengan nilai baru
        updateDiskonInDatabase(currentEditingItem.nameLabel.getText(), newPercentage);
        endEditing(saveButton, cancelButton);
    }
});

        cancelButton.addActionListener(e -> {
            if (currentEditingItem != null) {
                // Batal perubahan
                currentEditingItem.percentageEditField.setText(currentEditingItem.percentageLabel.getText().replace(" %", ""));
                endEditing(saveButton, cancelButton);
            }
        });

        panel.add(saveButton);
        panel.add(cancelButton);

        return panel;
    }
    
  private void Refresh() {
    listItemPanel.removeAll();
    
    List<String[]> diskonList = fetchDiskonData();
    EditableListItem[] items = new EditableListItem[diskonList.size()];
    
    JButton saveButton = findButton("Simpan");
    JButton cancelButton = findButton("Batal");

    for (int i = 0; i < diskonList.size(); i++) {
        String[] diskon = diskonList.get(i);
        items[i] = createRoundedListItem(diskon[0], diskon[1], i == 0);
        listItemPanel.add(items[i].itemPanel);
        
          // Tambahkan listener untuk item baru
        final EditableListItem currentItem = items[i];
        items[i].percentagePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isEditing || currentEditingItem == currentItem) {
                    startEditing(currentItem, saveButton, cancelButton);
                }
            }
        });
        
        if (i < diskonList.size() - 1) {
            listItemPanel.add(Box.createRigidArea(new Dimension(0, 10))); 
        }
    }
    
    listItemPanel.revalidate();
    listItemPanel.repaint();
}

    private List<String[]> fetchDiskonData() {
        List<String[]> diskonList = new ArrayList<>();
        try {
            String sql = "SELECT nama_diskon, total_diskon FROM diskon WHERE id_diskon != 'DS_00' AND status = 'dipakai'";

            try (PreparedStatement st = con.prepareStatement(sql)) {
                ResultSet rs = st.executeQuery();
                while (rs.next()) {
                    String namaDiskon = rs.getString("nama_diskon");
                    int totalDiskon = rs.getInt("total_diskon");
                    String total = String.valueOf(totalDiskon);
                    diskonList.add(new String[]{namaDiskon, total});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching diskon data: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        return diskonList;
    }

    private void updateDiskonInDatabase(String namaDiskon, String totalDiskon) {
        try {
            String sql = "UPDATE diskon SET total_diskon = ? WHERE nama_diskon = ?";
            try (PreparedStatement st = con.prepareStatement(sql)) {
                st.setInt(1, Integer.parseInt(totalDiskon));
                st.setString(2, namaDiskon);
                int rowsAffected = st.executeUpdate();

                if (rowsAffected > 0) {
                    PindahanAntarPopUp.showProdukDisplayDiskonSuksesDiPerbarui(parentFrame);
                } else {
                    JOptionPane.showMessageDialog(null, "Gagal memperbarui diskon. Item tidak ditemukan.",
                            "Update Failed", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating diskon: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

  private void startEditing(EditableListItem item, JButton saveButton, JButton cancelButton) {
    isEditing = true;
    currentEditingItem = item;
    item.percentagePanel.setVisible(false);
    item.percentageEditField.setVisible(true);

    String currentValue = item.percentageLabel.getText().replace(" %", "").trim();

    try {
        int value = Integer.parseInt(currentValue);
        if (value < 0) {
            item.percentageEditField.setText("0");
        } else if (value > 100) {
            item.percentageEditField.setText("100");
        } else {
            item.percentageEditField.setText(String.valueOf(value));
        }
    } catch (NumberFormatException e) {
        item.percentageEditField.setText("0");
    }

    // Hapus listener lama jika ada
    for (KeyListener listener : item.percentageEditField.getKeyListeners()) {
        item.percentageEditField.removeKeyListener(listener);
    }

    // Tambahkan listener untuk validasi input
    item.percentageEditField.addKeyListener(new KeyAdapter() {
        @Override
        public void keyTyped(KeyEvent e) {
            char c = e.getKeyChar();
            
            // Hanya menerima digit angka
            if (!Character.isDigit(c)) {
                e.consume();
                return;
            }
            
            String currentText = item.percentageEditField.getText();
            String newText = currentText + c;
            
            // Cek jika melebihi 3 digit (maksimal 100)
            if (newText.length() > 3) {
                e.consume();
                return;
            }
            
            // Cek jika nilai melebihi 100
            if (!newText.isEmpty()) {
                try {
                    int value = Integer.parseInt(newText);
                    if (value > 100) {
                        e.consume();
                        item.percentageEditField.setText("100");
                    }
                } catch (NumberFormatException ex) {
                    e.consume();
                }
            }
        }
    });

    saveButton.setVisible(true);
    cancelButton.setVisible(true);
    item.percentageEditField.requestFocusInWindow();
}
  
    private void endEditing(JButton saveButton, JButton cancelButton) {
        if (currentEditingItem != null) {
            currentEditingItem.percentageEditField.setVisible(false);
            currentEditingItem.percentagePanel.setVisible(true);
        }
        saveButton.setVisible(false);
        cancelButton.setVisible(false);
        isEditing = false;
        currentEditingItem = null;
    }

    public void onListDiskonTabSelected() {
        if (pendingEdit && currentEditingItem != null) {
            // Pulihkan status edit
            currentEditingItem.percentageEditField.setText(pendingEditValue);
            currentEditingItem.percentageEditField.setVisible(true);
            currentEditingItem.percentagePanel.setVisible(false);
            // Tampilkan tombol
            findButton("Simpan").setVisible(true);
            findButton("Batal").setVisible(true);
        }
    }

    private JButton findButton(String text) {
        // Pertama cari di listDiskonPanel
        for (Component comp : listDiskonPanel.getComponents()) {
            if (comp instanceof JButton && ((JButton) comp).getText().equals(text)) {
                return (JButton) comp;
            }
        }
        return null;
    }

    private EditableListItem createRoundedListItem(String name, String percentage, boolean isFirst) {
        // Use RoundedPanel for the main item panel with 30px radius
        JPanel itemPanel = new RoundedPanel(30) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.GRAY); // Border color
                g2.setStroke(new BasicStroke(1)); // Border thickness
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                g2.dispose();
            }
        };
        itemPanel.setLayout(null);
        itemPanel.setBackground(Color.WHITE);
        itemPanel.setPreferredSize(new Dimension(440, 45));

        // Edit label positioned between name and percentage
        JLabel editLabel = new JLabel("Edit:");
        editLabel.setFont(new Font("Arial", Font.BOLD, 12));
        editLabel.setForeground(Color.GRAY);
        editLabel.setHorizontalAlignment(SwingConstants.CENTER);
        editLabel.setBounds(300, 10, 50, 25);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setBounds(20, 10, 100, 25);

        // Create a rounded percentage panel
        JPanel percentagePanel = new RoundedPanel(15) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(64, 72, 82));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
            }
        };
        percentagePanel.setLayout(new BorderLayout());
        percentagePanel.setOpaque(false);
        percentagePanel.setBounds(350, 10, 50, 25);

        JLabel percentageLabel = new JLabel(percentage + " %");
        percentageLabel.setForeground(Color.WHITE);
        percentageLabel.setHorizontalAlignment(JLabel.CENTER);
        percentagePanel.add(percentageLabel, BorderLayout.CENTER);

        // Create editing components (initially hidden)
        JTextField percentageEditField = new JTextField(percentage) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            public void updateUI() {
                super.updateUI();
                setOpaque(false);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            }
        };
        percentageEditField.setBounds(350, 10, 50, 25);
        percentageEditField.setVisible(false);

        // Menggunakan RoundedButton dengan sudut 15px
        ImageIcon deleteIcon = new ImageIcon(getClass().getResource("/SourceImage/icon/icon_hapus.png"));
        itemPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JButton deleteButton = new JButton(deleteIcon);
        deleteButton.setBounds(405, 10, 25, 25);
        deleteButton.setBackground(new Color(231, 76, 60)); // Warna merah
        deleteButton.setBorderPainted(false);
        deleteButton.setFocusPainted(false);
        deleteButton.setContentAreaFilled(false);
        deleteButton.setOpaque(false);
        deleteButton.setUI(new RoundedButton());
        deleteButton.addActionListener(e -> {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(parentComponent);
            PopUp_ProdukDisplayKonfirmasiHapusDataDiskon dialog = new PopUp_ProdukDisplayKonfirmasiHapusDataDiskon(parentFrame, name);
            dialog.setLocationRelativeTo(parentFrame);
            dialog.setVisible(true);

            // Get confirmation result from dialog
            if (dialog.isConfirmed()) {
                // If user confirmed deletion, delete from database
                if (deleteDiskonFromDatabase(name)) {
                    // Remove this item from UI
                    Container parent = itemPanel.getParent();
                    if (parent != null) {
                        // Get the vertical strut component that follows this panel (if any)
                        Component verticalStrut = null;
                        Container grandParent = parent.getParent();
                        if (grandParent instanceof JPanel) {
                            int index = -1;
                            for (int i = 0; i < ((JPanel) grandParent).getComponentCount(); i++) {
                                if (((JPanel) grandParent).getComponent(i) == itemPanel) {
                                    index = i;
                                    break;
                                }
                            }
                            if (index != -1 && index + 1 < ((JPanel) grandParent).getComponentCount()) {
                                Component next = ((JPanel) grandParent).getComponent(index + 1);
                                if (next instanceof Box.Filler) {
                                    verticalStrut = next;
                                }
                            }
                        }

                        parent.remove(itemPanel);
                        if (verticalStrut != null) {
                            parent.remove(verticalStrut);
                        }
                        parent.revalidate();
                        parent.repaint();

                     PindahanAntarPopUp.showProdukDisplayDiskonSuksesDiHapus(parentFrame);
                    }
                }
            }
        });

        // Menambahkan komponen ke panel utama
        itemPanel.add(editLabel);
        itemPanel.add(nameLabel);
        itemPanel.add(percentagePanel);
        itemPanel.add(percentageEditField);
        itemPanel.add(deleteButton);

        // Return an object that contains the components needed for editing
        return new EditableListItem(itemPanel, percentagePanel, percentageEditField, percentageLabel, nameLabel);
    }

    private boolean deleteDiskonFromDatabase(String namaDiskon) {
        boolean success = false;

        try {
            String sql = "UPDATE diskon SET status = 'gadipakai' WHERE nama_diskon = ?";

            try (PreparedStatement st = con.prepareStatement(sql)) {
                st.setString(1, namaDiskon);
                int rowsAffected = st.executeUpdate();
                success = (rowsAffected > 0);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            PindahanAntarPopUp.showProdukDisplayDiskonGagalDiHapus(parentFrame);
        }
        return success;
    }

    private class EditableListItem {

        JPanel itemPanel;
        JPanel percentagePanel;
        JTextField percentageEditField;
        JLabel percentageLabel, nameLabel;

        EditableListItem(JPanel itemPanel, JPanel percentagePanel,
                JTextField percentageEditField, JLabel percentageLabel, JLabel nameLabel) {
            this.itemPanel = itemPanel;
            this.percentagePanel = percentagePanel;
            this.percentageEditField = percentageEditField;
            this.percentageLabel = percentageLabel;
            this.nameLabel = nameLabel;
        }
    }

    private JButton createRoundedButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gambar background
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);

                // Tambahkan border
                g2.setColor(Color.BLACK); // Warna border
                g2.setStroke(new BasicStroke(1f)); // Ketebalan border
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 40, 40);

                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            public void updateUI() {
                super.updateUI();
                setContentAreaFilled(false);
                setFocusPainted(false);
                setBorderPainted(false);
            }
        };
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void simpanDiskon() {
    String totalDiskon = totalDiskonField.getText().trim();
    String namaDiskon = namaDiskonField.getText().trim();
    String idPromo = kodePromo.getText();

    try {
        if (totalDiskon.isEmpty() || namaDiskon.isEmpty()) {
            PindahanAntarPopUp.showTambahKaryawanTIdakBolehKosong(parentFrame);
            return;
        }

        try {
            int diskonValue = Integer.parseInt(totalDiskon);
            if (diskonValue < 0 || diskonValue > 100) {
//                PindahanAntarPopUp.showProdukDisplayDiskonInvalidValue(parentFrame);
                return;
            }
        } catch (NumberFormatException e) {
//            PindahanAntarPopUp.showProdukDisplayDiskonInvalidValue(parentFrame);
            return;
        }

        String cekSql = "SELECT COUNT(*) FROM diskon WHERE nama_diskon = ? AND status = 'dipakai'";
        try (PreparedStatement cekSt = con.prepareStatement(cekSql)) {
            cekSt.setString(1, namaDiskon);
            ResultSet rs = cekSt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                PindahanAntarPopUp.showProdukDisplayDiskonNamaSudahAda(parentFrame);
                return;
            }
        }

        String sql = "INSERT INTO diskon (id_diskon, total_diskon, nama_diskon, status) VALUES (?, ?, ?, 'dipakai')";
        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, idPromo);
            st.setString(2, totalDiskon);
            st.setString(3, namaDiskon);

            int rowInserted = st.executeUpdate();
            if (rowInserted > 0) {
                PindahanAntarPopUp.showProdukDisplayDiskonSuksesDiTambahkan(parentFrame);
            }
        }

        totalDiskonField.setText("");
        namaDiskonField.setText("");
        kodePromo.setText(generateNextTransaksiId());
        Refresh();
        switchToPanel(listDiskonPanel, listDiskonIndicator, aturDiskonIndicator);
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error menyimpan diskon: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
    }
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

    private String generateNextTransaksiId() {
        String nextId = ID_PREFIX + "01"; // Default jika belum ada transaksi

        try {
            // Query untuk mendapatkan ID transaksi terakhir dari database
            String query = "SELECT id_diskon FROM diskon ORDER BY id_diskon DESC LIMIT 1";
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String lastId = rs.getString("id_diskon");
                if (lastId != null && lastId.startsWith(ID_PREFIX)) {
                    // Ekstrak nomor dari ID terakhir
                    String numberPart = lastId.substring(ID_PREFIX.length());
                    try {
                        int lastNumber = Integer.parseInt(numberPart);
                        // Increment nomor
                        int nextNumber = lastNumber + 1;
                        // Format nomor dengan padding nol di depan
                        String paddedNumber = String.format("%0" + PADDING_LENGTH + "d", nextNumber);
                        // Gabungkan prefix dengan nomor
                        nextId = ID_PREFIX + paddedNumber;
                    } catch (NumberFormatException e) {
                        System.err.println("Format ID transaksi tidak valid: " + lastId);
                    }
                }
            }

            rs.close();
            pst.close();
        } catch (SQLException e) {
            System.err.println("Error mengambil ID transaksi terakhir: " + e.getMessage());
            e.printStackTrace();
        }

        return nextId;
    }
}
