package produk;

import SourceCode.RoundedButton;
import SourceCode.RoundedPanelProduk;
import SourceCode.RoundedTextField;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.io.File;
import java.lang.reflect.Field;
import Form.Productt;
import PopUp_all.Popup_keluaraddnewproduct;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import javax.imageio.ImageIO;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeFactory;
import db.conn;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.*;
import PopUp_all.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

public class EditProductPanel extends JPanel {

    // Komponen form
    private RoundedTextField txtNamaProduct, txtHargaJual, txtHargaBeli, txtMerk, txtUkuran, txtStok;
    private JRadioButton rbMale, rbFemale, rbUnisex;
    private ButtonGroup genderGroup;
    private ComboboxCustom cbCategory;
    private JLabel lblStyle;
    private Barcode barcode;
    private JButton btnAddProduct, btnCancel, btnBrowseFile;
    private JButton btnGenerateBarcode, btnPrint;
    private JButton btnPrevStyle, btnNextStyle;
    private RoundedPanelProduk panelGeneral, panelImages, panelBarcode, panelCategory, barcodeArea;
    private JPanel dragDropPanel;
    private JLabel lblPhotoCount;
    private JFrame parentFrame; // Reference to parent frame for closing
    private Productt mainFrame; // Tambahkan referensi ke main frame
    private File selectedImageFile;
    private ImageIcon displayedImage;
    private JLabel imagePreview;
    private JButton btnRemoveImage;
    private JPanel uploadInstructionsPanel;
    private String defaultImagePath = "/SourceImage/gambar_sepatu.png";

    // Current style options and index
    private String[] currentStyleOptions = {""};
    private int currentStyleIndex = 0;

    // Definisi ukuran ikon yang lebih kecil
    private static final int ICON_SIZE = 16;
    private int cornerRadius = 15;
    private Connection con;
    private String kode;
    private String[] currentStyleIds;

    public EditProductPanel() {
    }

    public void setMainFrame(Productt frame) {
        this.mainFrame = frame;
    }

    public EditProductPanel(JFrame parentFrame, String kode) {
        this.parentFrame = parentFrame;
        setLayout(null);
        setBackground(new Color(255, 255, 255));

        con = conn.getConnection();
        this.kode = kode;

        // Judul Form
        JLabel lblTitle = new JLabel("Edit Product");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBounds(20, 20, 200, 25);
        add(lblTitle);
        // Panel Informasi Umum
        createGeneralInfoPanel();
        // Panel Upload Gambar
        createUploadImagesPanel();
        // Panel Gender
        createGenderPanel();
        // Panel Ukuran dan Stok
        createSizeStockPanel();
        // Panel Barcode
        createBarcodePanel();
        // Panel Kategori
        createCategoryPanel();

        // Tombol Add Product
        btnAddProduct = createStyledButton("Save Edit", new Color(52, 58, 64), Color.WHITE, 40);
        btnAddProduct.setBounds(856, 18, 150, 35);
        btnAddProduct.addActionListener(e->{
            updateProduct();
        });

        // Ikon Add Product diperkecil
        ImageIcon origAddIcon = new ImageIcon(getClass().getResource("/SourceImage/icon/document.png"));
        int iconAddSize = 15;
        Image scaledAddImage = origAddIcon.getImage().getScaledInstance(iconAddSize, iconAddSize, Image.SCALE_SMOOTH);
        ImageIcon scaledAddIcon = new ImageIcon(scaledAddImage);
        btnAddProduct.setIcon(scaledAddIcon);
        btnAddProduct.setIconTextGap(15);  // Jarak antara ikon dan teks
        add(btnAddProduct);

        // Memuat dan mengubah ukuran ikon
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/SourceImage/icon/close.png"));
        int iconCloseSize = 15; // Sesuaikan ukuran sesuai kebutuhan
        Image scaledImage = originalIcon.getImage().getScaledInstance(iconCloseSize, iconCloseSize, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        // Membuat tombol dengan ikon
        btnCancel = new RoundedCircularButton("", scaledIcon);
        btnCancel.setBackground(new Color(220, 53, 69)); // Warna merah untuk latar belakang
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setBounds(1015, 18, 35, 35);
        add(btnCancel);

        initEventListeners();
        getData();

    }

    private void createGeneralInfoPanel() {
        panelGeneral = new RoundedPanelProduk();
        panelGeneral.setBackground(new Color(240, 240, 240));
        panelGeneral.setBounds(20, 80, 730, 260);
        add(panelGeneral);
        panelGeneral.setLayout(null);

        JLabel lblGeneralInfo = new JLabel("General Information");
        lblGeneralInfo.setFont(new Font("Arial", Font.BOLD, 14));
        lblGeneralInfo.setBounds(20, 15, 200, 20);
        panelGeneral.add(lblGeneralInfo);

        // Nama Product
        JLabel lblNamaProduct = new JLabel("Nama Produk");
        lblNamaProduct.setBounds(20, 50, 100, 20);
        panelGeneral.add(lblNamaProduct);

        txtNamaProduct = new RoundedTextField();
        txtNamaProduct.setBounds(20, 75, 690, 35);
        panelGeneral.add(txtNamaProduct);

        // Harga Jual
        JLabel lblHargaJual = new JLabel("Harga Jual");
        lblHargaJual.setBounds(20, 150, 100, 20);
        panelGeneral.add(lblHargaJual);
        
        txtHargaJual = new RoundedTextField();
        txtHargaJual.setBounds(100, 145, 250, 35);
        panelGeneral.add(txtHargaJual);
        setRpField(txtHargaJual);

        // Merk
        JLabel lblMerk = new JLabel("Merk");
        lblMerk.setBounds(405, 150, 100, 20);
        panelGeneral.add(lblMerk);

        txtMerk = new RoundedTextField();
        txtMerk.setBounds(450, 145, 260, 35);
        panelGeneral.add(txtMerk);

        // Harga Beli
        JLabel lblHargaBeli = new JLabel("Harga Beli");
        lblHargaBeli.setBounds(20, 220, 100, 20);
        panelGeneral.add(lblHargaBeli);

        txtHargaBeli = new RoundedTextField();
        txtHargaBeli.setBounds(100, 215, 250, 35);
        panelGeneral.add(txtHargaBeli);
        setRpField(txtHargaBeli);

//        // Set Discount
//        JLabel lblDiscount = new JLabel("Set Diskon");
//        lblDiscount.setBounds(405, 220, 100, 20);
//        panelGeneral.add(lblDiscount);
//        String[] discountOptions = {"0 %", "5 %", "10 %", "15 %", "20 %", "25 %", "30 %"};
//        cbDiscount = new ComboboxCustom(discountOptions);
//        cbDiscount.setBounds(480, 215, 230, 35);
//        panelGeneral.add(cbDiscount);
    }
    
      private void setRpField(final JTextField textField) {
    final String PREFIX = "Rp. ";
    final NumberFormat formatter = NumberFormat.getInstance(new Locale("id", "ID"));
    if (formatter instanceof DecimalFormat) {
        ((DecimalFormat) formatter).applyPattern("#,###");
    }

    // Inisialisasi dengan prefix
    if (!textField.getText().startsWith(PREFIX)) {
        textField.setText(PREFIX);
    }

    ((AbstractDocument) textField.getDocument()).setDocumentFilter(new DocumentFilter() {
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {

            // Blok edit pada prefix
            if (offset < PREFIX.length()) {
                return;
            }

            // Hanya izinkan digit
            if (!text.matches("\\d*")) {
                return;
            }

            Document doc = fb.getDocument();
            StringBuilder sb = new StringBuilder(doc.getText(0, doc.getLength()));
            sb.replace(offset, offset + length, text);

            // Ekstrak hanya angka (hapus prefix dan format)
            String numericText = sb.toString().substring(PREFIX.length()).replaceAll("[.,]", "");

            // Batasi maksimal 10 digit (maksimal 2.147.483.647 untuk INT)
            if (numericText.length() > 10) {
                Toolkit.getDefaultToolkit().beep(); // Beri feedback
                return;
            }

            try {
                if (numericText.isEmpty()) {
                    super.replace(fb, 0, doc.getLength(), PREFIX, attrs);
                    return;
                }

                // Parse sebagai long untuk validasi
                long value = Long.parseLong(numericText);
                
                // Batasi nilai maksimal INT (2.147.483.647)
                if (value > Integer.MAX_VALUE) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }

                // Format teks
                String formattedText = PREFIX + formatter.format(value);
                super.replace(fb, 0, doc.getLength(), formattedText, attrs);

                // Hitung posisi kursor
                SwingUtilities.invokeLater(() -> {
                    try {
                        int newPos = Math.min(offset + text.length(), formattedText.length());
                        // Sesuaikan untuk karakter pemisah
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
            // Blok penghapusan prefix
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

    // Handle focus dan navigasi
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

private int getNumericValue(JTextField textField, String prefix) {
    String text = textField.getText().substring(prefix.length()).replaceAll("[.,]", "");
    return Integer.parseInt(text); 
}

// Method utility untuk mengatur nilai numerik ke textfield
   private void setNumericValue(JTextField textField, long value, String prefix) {
    // Hapus DocumentFilter sementara
    textField.setDocument(new PlainDocument());
    
    NumberFormat formatter = NumberFormat.getInstance(new Locale("id", "ID"));
    if (formatter instanceof DecimalFormat) {
        ((DecimalFormat) formatter).applyPattern("#,###");
    }
    textField.setText(prefix + formatter.format(value));
    
    // Setel kembali DocumentFilter
    setRpField(textField);
    
    // Setel posisi kursor
    SwingUtilities.invokeLater(() -> {
        textField.setCaretPosition(textField.getText().length());
    });
}

    private void resetPriceFields() {
    // Hapus DocumentFilter yang ada terlebih dahulu
    txtHargaJual.setDocument(new PlainDocument());
    txtHargaBeli.setDocument(new PlainDocument());
    
    // Setel ulang dengan format Rp.
    setRpField(txtHargaJual);
    setRpField(txtHargaBeli);
    
    // Setel nilai default
    txtHargaJual.setText("Rp. ");
    txtHargaBeli.setText("Rp. ");
    
    // Setel posisi kursor
    SwingUtilities.invokeLater(() -> {
        txtHargaJual.setCaretPosition(4);
        txtHargaBeli.setCaretPosition(4);
    });
}
    
    private void createUploadImagesPanel() {
        // Main panel
        panelImages = new RoundedPanelProduk();
        panelImages.setBackground(new Color(240, 240, 240));
        panelImages.setBounds(770, 80, 280, 310); // Tetap mempertahankan posisi dan ukuran
        panelImages.setLayout(null);
        add(panelImages);

        // Label for the main panel
        JLabel lblImageUpload = new JLabel("Uploud Images");
        lblImageUpload.setFont(new Font("Arial", Font.BOLD, 14));
        lblImageUpload.setBounds(20, 10, 240, 30);
        panelImages.add(lblImageUpload);

        // Inner rounded panel for content - Tetap menggunakan RoundedPanelProduk
        RoundedPanelProduk contentPanel = new RoundedPanelProduk();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBounds(20, 50, 240, 240);
        contentPanel.setLayout(null);
        panelImages.add(contentPanel);

        // Panel untuk instruksi upload (visible di awal)
        uploadInstructionsPanel = new JPanel(null);
        uploadInstructionsPanel.setBounds(0, 0, 240, 240);
        uploadInstructionsPanel.setBackground(Color.WHITE);
        uploadInstructionsPanel.setOpaque(false); // Penting: agar tidak menutupi rounded corner dari parent
        contentPanel.add(uploadInstructionsPanel);

        // Upload Icon (scaled)
        ImageIcon origUploadIcon = new ImageIcon(getClass().getResource("/SourceImage/icon/Icon_uploud.png"));
        Image scaledUpImage = origUploadIcon.getImage().getScaledInstance(ICON_SIZE * 3, ICON_SIZE * 2, Image.SCALE_SMOOTH);
        ImageIcon scaledUploadIcon = new ImageIcon(scaledUpImage);
        JLabel uploadIcon = new JLabel(scaledUploadIcon);
        uploadIcon.setBounds(95, 50, scaledUploadIcon.getIconWidth(), scaledUploadIcon.getIconHeight());
        uploadInstructionsPanel.add(uploadIcon);

        // First Label (Drag and Drop text)
        JLabel lblDragDrop = new JLabel("Drag and drop files here");
        lblDragDrop.setFont(new Font("Arial", Font.BOLD, 12));
        lblDragDrop.setBounds(20, 100, 200, 20);
        lblDragDrop.setHorizontalAlignment(JLabel.CENTER);
        uploadInstructionsPanel.add(lblDragDrop);

        // Second Label (Supported Formats)
        JLabel lblFileSupported = new JLabel("Files supported JPG, JPEG, PNG");
        lblFileSupported.setFont(new Font("Arial", Font.PLAIN, 10));
        lblFileSupported.setBounds(20, 120, 200, 20);
        lblFileSupported.setHorizontalAlignment(JLabel.CENTER);
        uploadInstructionsPanel.add(lblFileSupported);

        // OR Label
        JLabel lblor = new JLabel("OR");
        lblor.setFont(new Font("Arial", Font.BOLD, 14));
        lblor.setBounds(20, 140, 200, 20);
        lblor.setHorizontalAlignment(JLabel.CENTER);
        uploadInstructionsPanel.add(lblor);

        // Browse File Button
        btnBrowseFile = createStyledButton("Browse File", Color.WHITE, Color.BLACK, 30);
        btnBrowseFile.setBounds(70, 170, 100, 30);
        uploadInstructionsPanel.add(btnBrowseFile);

        // Photo Count Label
        lblPhotoCount = new JLabel("foto maks (0/1)");
        lblPhotoCount.setFont(new Font("Arial", Font.PLAIN, 10));
        lblPhotoCount.setBounds(20, 200, 200, 20);
        lblPhotoCount.setHorizontalAlignment(JLabel.CENTER);
        uploadInstructionsPanel.add(lblPhotoCount);

        // Label untuk preview gambar
        imagePreview = new JLabel();
        imagePreview.setBounds(20, 20, 200, 200);
        imagePreview.setHorizontalAlignment(JLabel.CENTER);
        imagePreview.setVisible(false);
        contentPanel.add(imagePreview);

        // Tombol remove image (X)
        btnRemoveImage = new RoundedCircularButton("X");
        btnRemoveImage.setFont(new Font("Arial", Font.BOLD, 10));
        btnRemoveImage.setBackground(new Color(220, 53, 69)); // Warna merah
        btnRemoveImage.setForeground(Color.WHITE);
        btnRemoveImage.setBounds(200, 10, 25, 25);
        btnRemoveImage.setVisible(false); // Awalnya tidak terlihat
        contentPanel.add(btnRemoveImage);
        contentPanel.setComponentZOrder(btnRemoveImage, 0); 

        // Tambahkan action listener untuk tombol Browse File
        btnBrowseFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                browseForImage();
            }
        });

        // Tambahkan action listener untuk tombol Remove Image
        btnRemoveImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeSelectedImage();
            }
        });

        // Setup drag and drop di contentPanel
        setupDragAndDrop(contentPanel);
    }

    private void browseForImage() {
        JFileChooser fileChooser = new JFileChooser();
        // Set filter untuk hanya menerima file gambar
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }
                String name = f.getName().toLowerCase();
                return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png");
            }

            @Override
            public String getDescription() {
                return "Image Files (*.jpg, *.jpeg, *.png)";
            }
        });

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = fileChooser.getSelectedFile();
            displaySelectedImage(selectedImageFile);
        }
    }

    private void displaySelectedImage(File file) {
        try {
            // Baca gambar
            BufferedImage originalImage = ImageIO.read(file);
            if (originalImage != null) {
                // Resize gambar agar pas dengan area preview
                Image resizedImage = originalImage.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                displayedImage = new ImageIcon(resizedImage);

                // Tampilkan gambar
                imagePreview.setIcon(displayedImage);

                // Update tampilan
                uploadInstructionsPanel.setVisible(false);
                imagePreview.setVisible(true);
                btnRemoveImage.setVisible(true);

                // Update label foto maks
                lblPhotoCount.setText("foto maks (1/1)");
            } else {
                JOptionPane.showMessageDialog(this, "Format file tidak didukung. Gunakan JPG, JPEG, atau PNG.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Gagal membuka file gambar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeSelectedImage() {
        // Hapus referensi gambar
        selectedImageFile = null;
        displayedImage = null;
        imagePreview.setIcon(null);

        // Kembalikan tampilan ke mode upload
        uploadInstructionsPanel.setVisible(true);
        imagePreview.setVisible(false);
        btnRemoveImage.setVisible(false);

        // Reset label foto maks
        lblPhotoCount.setText("foto maks (0/1)");
    }

    private void setupDragAndDrop(JPanel targetPanel) {
        // Setup drag and drop handler
        targetPanel.setDropTarget(new DropTarget() {
            @Override
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

                    if (!droppedFiles.isEmpty()) {
                        File droppedFile = droppedFiles.get(0);
                        String fileName = droppedFile.getName().toLowerCase();
                        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png")) {
                            selectedImageFile = droppedFile;
                            displaySelectedImage(selectedImageFile);
                        } else {
                            JOptionPane.showMessageDialog(targetPanel, "Format file tidak didukung. Gunakan JPG, JPEG, atau PNG.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (UnsupportedFlavorException | IOException e) {
                    JOptionPane.showMessageDialog(targetPanel, "Error saat menerima file: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void createGenderPanel() {
        JLabel lblGender = new JLabel("Gender");
        lblGender.setBounds(20, 350, 100, 20);
        add(lblGender);

        JLabel lblPickGender = new JLabel("Pick Available Gender");
        lblPickGender.setFont(new Font("Arial", Font.PLAIN, 10));
        lblPickGender.setBounds(20, 370, 150, 15);
        add(lblPickGender);

        // Radio buttons
        genderGroup = new ButtonGroup();

        rbMale = new JRadioButton("Male");
        rbMale.setBounds(20, 390, 70, 30);
        rbMale.setSelected(true);
        rbMale.setBackground(Color.WHITE);
        rbMale.setFocusPainted(false);  // Menghilangkan kotak fokus
        rbMale.setBorderPainted(false); // Menghilangkan border
        rbMale.setContentAreaFilled(false); // Membuat latar belakang transparan
        add(rbMale);
        genderGroup.add(rbMale);

        rbFemale = new JRadioButton("Female");
        rbFemale.setBounds(90, 390, 80, 30);
        rbFemale.setBackground(Color.WHITE);
        rbFemale.setFocusPainted(false);
        rbFemale.setBorderPainted(false);
        rbFemale.setContentAreaFilled(false);
        add(rbFemale);
        genderGroup.add(rbFemale);

        rbUnisex = new JRadioButton("Unisex");
        rbUnisex.setBounds(170, 390, 80, 30);
        rbUnisex.setBackground(Color.WHITE);
        rbUnisex.setFocusPainted(false);
        rbUnisex.setBorderPainted(false);
        rbUnisex.setContentAreaFilled(false);
        add(rbUnisex);
        genderGroup.add(rbUnisex);
    }

    private void createSizeStockPanel() {
        // Ukuran / Size
        JLabel lblUkuran = new JLabel("Ukuran / Size");
        lblUkuran.setBounds(300, 355, 100, 20);
        add(lblUkuran);

        txtUkuran = new RoundedTextField();
        txtUkuran.setBounds(300, 390, 200, 35);
        add(txtUkuran);

        // Stok
        JLabel lblStok = new JLabel("Stok");
        lblStok.setBounds(550, 355, 100, 20);
        add(lblStok);

        txtStok = new RoundedTextField();
        txtStok.setBounds(550, 390, 200, 35);
        add(txtStok);
    }

    private void createBarcodePanel() {
        panelBarcode = new RoundedPanelProduk();
        panelBarcode.setBackground(new Color(240, 240, 240));
        panelBarcode.setBounds(20, 450, 730, 180);
        add(panelBarcode);
        panelBarcode.setLayout(null);
        JLabel lblBarcode = new JLabel("Generate Barcode");
        lblBarcode.setFont(new Font("Arial", Font.BOLD, 14));
        lblBarcode.setBounds(20, 15, 200, 20);
        panelBarcode.add(lblBarcode);
        barcodeArea = new RoundedPanelProduk();
        barcodeArea.setBounds(20, 45, 450, 120);
        barcodeArea.setBackground(Color.WHITE);
        barcodeArea.setBorder(new RoundedBorder(15, Color.BLACK));
        panelBarcode.add(barcodeArea);
        btnGenerateBarcode = createStyledButton("GENERATE BARCODE", new Color(52, 58, 64), Color.WHITE, 15);
        btnGenerateBarcode.setBounds(500, 45, 210, 40);
        panelBarcode.add(btnGenerateBarcode);
        btnGenerateBarcode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PindahanAntarPopUp.showEditProductGenerateBarcodeTidakBisaDiGanti(parentFrame);
            }
        });

        btnPrint = createStyledButton("PRINT", new Color(52, 58, 64), Color.WHITE, 15);
        btnPrint.setBounds(500, 100, 210, 40);
        ImageIcon origPrintIcon = new ImageIcon(getClass().getResource("/SourceImage/icon/Icon_print.png"));
        Image scaledPrintImage = origPrintIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        ImageIcon scaledPrintIcon = new ImageIcon(scaledPrintImage);
        btnPrint.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printBarcode();
            }
        });
        btnPrint.setIcon(scaledPrintIcon);
        btnPrint.setHorizontalAlignment(SwingConstants.CENTER);
        btnPrint.setHorizontalTextPosition(SwingConstants.LEFT);
        btnPrint.setIconTextGap(10);
        btnPrint.setMargin(new Insets(0, 0, 0, 0));
        panelBarcode.add(btnPrint);
    }

    private void generateBarcode() {
        try {
            barcodeArea.removeAll();

            String barcodeValue = generateRandom16DigitNumber();
            Barcode barcode = BarcodeFactory.createCode128(barcodeValue);

            barcode.setBarWidth(2);

            barcodeArea.setLayout(new BorderLayout());

            JPanel barcodeImagePanel = new JPanel();
            barcodeImagePanel.setOpaque(false);

            barcodeImagePanel.setLayout(new BoxLayout(barcodeImagePanel, BoxLayout.Y_AXIS));

            barcodeImagePanel.add(Box.createVerticalStrut(25));

            JPanel barcodeContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
            barcodeContainer.setOpaque(false);

            Component barcodeComp = barcode;
            int newHeight = 80;

            JPanel heightPanel = new JPanel(new BorderLayout()) {
                @Override
                public Dimension getPreferredSize() {
                    Dimension dim = super.getPreferredSize();
                    return new Dimension(dim.width, newHeight);
                }
            };
            heightPanel.setOpaque(false);
            heightPanel.add(barcodeComp, BorderLayout.CENTER);

            barcodeContainer.add(heightPanel);

            barcodeImagePanel.add(barcodeContainer);

            barcodeArea.add(barcodeImagePanel, BorderLayout.CENTER);

            // Perbarui tampilan
            barcodeArea.revalidate();
            barcodeArea.repaint();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error saat membuat barcode: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String generateRandom16DigitNumber() {
        StringBuilder sb = new StringBuilder(16);
        Random random = new Random();

        sb.append(random.nextInt(9) + 1);

        for (int i = 0; i < 15; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }

    private void printBarcode() {
        try {
            if (barcodeArea.getComponentCount() == 0) {
                JOptionPane.showMessageDialog(this,
                        "Silakan generate barcode terlebih dahulu sebelum mencetak.",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            PrinterJob job = PrinterJob.getPrinterJob();
            job.setJobName("Cetak Barcode");

            barcodeArea.setBorder(new RoundedBorder(15, Color.WHITE));

            // Set Printable
            job.setPrintable(new Printable() {
                @Override
                public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                    if (pageIndex > 0) {
                        return Printable.NO_SUCH_PAGE;
                    }

                    Graphics2D g2d = (Graphics2D) graphics;

                    // Translate ke area yang bisa dicetak
                    g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                    // Skala untuk mencetak barcode dengan ukuran yang tepat
                    double scaleX = pageFormat.getImageableWidth() / barcodeArea.getWidth() * 0.8;
                    double scaleY = pageFormat.getImageableHeight() / barcodeArea.getHeight() * 0.3;
                    double scale = Math.min(scaleX, scaleY);

                    g2d.scale(scale, scale);

                    // Tengahkan di halaman
                    double xPos = (pageFormat.getImageableWidth() / scale - barcodeArea.getWidth()) / 2;
                    g2d.translate(xPos, 0);

                    // Render barcode
                    barcodeArea.paint(g2d);

                    return Printable.PAGE_EXISTS;
                }
            });

            // Tampilkan dialog print
            if (job.printDialog()) {
                // Informasi kepada user
                JOptionPane.showMessageDialog(this,
                        "Barcode sedang dikirim ke printer.",
                        "Informasi", JOptionPane.INFORMATION_MESSAGE);

                // Jalankan pencetakan
                job.print();
            }
        } catch (PrinterException pe) {
            JOptionPane.showMessageDialog(this,
                    "Error saat mencetak barcode: " + pe.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createCategoryPanel() {
        panelCategory = new RoundedPanelProduk();
        panelCategory.setBackground(new Color(240, 240, 240));
        panelCategory.setBounds(770, 450, 280, 180);
        add(panelCategory);
        panelCategory.setLayout(null);

        JLabel lblCategory = new JLabel("Kategori");
        lblCategory.setFont(new Font("Arial", Font.BOLD, 14));
        lblCategory.setBounds(20, 15, 200, 20);
        panelCategory.add(lblCategory);

        JLabel lblProductCategory = new JLabel("Kategori Produk");
        lblProductCategory.setFont(new Font("Arial", Font.PLAIN, 10));
        lblProductCategory.setBounds(20, 35, 100, 15);
        panelCategory.add(lblProductCategory);

        // Combobox Kategori with placeholder
        String[] categoryOptions = {"Pilih Kategori", "Sepatu", "Sandal", "Kaos Kaki", "Lainnya"};
        cbCategory = new ComboboxCustom(categoryOptions);
        cbCategory.setBounds(20, 55, 240, 35);
        panelCategory.add(cbCategory);

        setupCategoryListener();

        // Panel untuk Style dengan Label dan Button
        JPanel stylePanel = new RoundedPanelProduk(5);
        stylePanel.setLayout(null);
        stylePanel.setBounds(20, 110, 240, 35);
        stylePanel.setBackground(new Color(255, 255, 255));
        stylePanel.setVisible(false); // Initially hidden
        stylePanel.setBorder(new RoundedBorder(5, Color.LIGHT_GRAY, 1)); // Add rounded border
        panelCategory.add(stylePanel);

        // Style value label
        lblStyle = new JLabel("Jaket");
        lblStyle.setFont(new Font("Arial", Font.BOLD, 12));
        lblStyle.setHorizontalAlignment(JLabel.LEFT);
        lblStyle.setBounds(30, 10, 160, 15); // Moved further to the left
        stylePanel.add(lblStyle);

        // Navigation buttons with circular shape
        btnNextStyle = new RoundedCircularButton(">");
        btnNextStyle.setBounds(210, 5, 25, 25); // Smaller and slightly adjusted position
        stylePanel.add(btnNextStyle);

        btnPrevStyle = new RoundedCircularButton("<");
        btnPrevStyle.setBounds(180, 5, 25, 25); // Smaller and slightly adjusted position
        stylePanel.add(btnPrevStyle);

        cbCategory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) cbCategory.getSelectedItem();
                if (selected != null && !selected.equals("Pilih Kategori")) {
                    // Mengubah opsi style berdasarkan kategori
                    updateStyleOptions(selected);

                    // Tampilkan style panel
                    Component[] components = panelCategory.getComponents();
                    for (Component c : components) {
                        if (c instanceof JPanel && c != cbCategory) {
                            c.setVisible(true);
                        }
                    }
                } else {
                    // Sembunyikan style panel
                    Component[] components = panelCategory.getComponents();
                    for (Component c : components) {
                        if (c instanceof JPanel && c != cbCategory) {
                            c.setVisible(false);
                        }
                    }
                }
            }
        });
    }

    private void initEventListeners() {
        // Modify the existing category combobox item listener
        cbCategory.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String selected = (String) cbCategory.getSelectedItem();
                    if (selected != null && !selected.equals("Pilih Kategori")) {
                        // Mengubah opsi style berdasarkan kategori
                        updateStyleOptions(selected);
                        // Show the style panel
                        Component[] components = panelCategory.getComponents();
                        for (Component c : components) {
                            if (c instanceof JPanel && c != cbCategory) {
                                c.setVisible(true);
                            }
                        }
                    } else {
                        // Hide the style panel
                        Component[] components = panelCategory.getComponents();
                        for (Component c : components) {
                            if (c instanceof JPanel && c != cbCategory) {
                                c.setVisible(false);
                            }
                        }
                    }
                }
            }
        });

        btnNextStyle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Tambahkan pengecekan panjang array dan batas index
                if (currentStyleIndex < currentStyleOptions.length - 1 && currentStyleOptions.length > 1) {
                    currentStyleIndex++;
                    updateStyleLabel();
                    updateStyleNavigationButtons();
                    playButtonClickEffect(btnNextStyle);
                }
            }
        });

        btnPrevStyle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Tambahkan pengecekan panjang array dan batas index
                if (currentStyleIndex > 0 && currentStyleOptions.length > 1) {
                    currentStyleIndex--;
                    updateStyleLabel();
                    updateStyleNavigationButtons();
                    playButtonClickEffect(btnPrevStyle);
                }
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Reset the form before showing the exit popup
//                prepareForPanelChange();
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(EditProductPanel.this);
                Popup_keluaraddnewproduct dialog = new Popup_keluaraddnewproduct(parentFrame);
                dialog.setVisible(true);
            }
        });
    }

    private void setupCategoryListener() {
        cbCategory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedCategory = (String) cbCategory.getSelectedItem();
                // Only update style options if an actual category is selected (not "Pilih Kategori")
                if (selectedCategory != null && !selectedCategory.equals("Pilih Kategori")) {
                    updateStyleOptions(selectedCategory);
                } else {
                    // Clear style if no valid category is selected
                    currentStyleOptions = new String[]{""};
                    currentStyleIds = new String[]{""};
                    currentStyleIndex = 0;
                    updateStyleLabel();
                    updateStyleNavigationButtons();
                }
            }
        });
    }

    private void updateStyleOptions(String category) {
        try {
            // Map category name to style IDs for database query
            String categoryStyleFilter = "";
            // Based on the product database, it seems styles are assigned based on category
            switch (category) {
                case "sepatu":
                    categoryStyleFilter = "00001, 00002, 00003, 00004"; // Running, Casual, Formal, Sport
                    break;
                case "sandal":
                    categoryStyleFilter = "00002, 00003, 00005, 00006"; // Casual, Formal, Outdoor, Pantai
                    break;
                case "kaos kaki":
                    categoryStyleFilter = "00007, 00003, 00002, 00008"; // Olahraga, Formal, Casual, Ankle
                    break;
                case "lainnya":
                    categoryStyleFilter = "00009, 00010, 00011, 00012"; // Vintage, Modern, Klasik, Trendy
                    break;
                default:
                    categoryStyleFilter = "";
                    break;
            }
            if (!categoryStyleFilter.isEmpty()) {
                // Query to get styles for the selected category
                String sql = "SELECT id_style, nama_style FROM style WHERE id_style IN (" + categoryStyleFilter + ") ORDER BY id_style ASC";

                try (PreparedStatement stmt = con.prepareStatement(sql)) {
                    ResultSet rs = stmt.executeQuery();

                    List<String> styleIdsList = new ArrayList<>();
                    List<String> styleNamesList = new ArrayList<>();

                    while (rs.next()) {
                        styleIdsList.add(rs.getString("id_style"));
                        styleNamesList.add(rs.getString("nama_style"));
                    }

                    if (!styleIdsList.isEmpty()) {
                        currentStyleIds = styleIdsList.toArray(new String[0]);
                        currentStyleOptions = styleNamesList.toArray(new String[0]);
                    } else {
                        // Fallback if no styles found
                        setDefaultStylesForCategory(category);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading style options: " + e.getMessage());
            e.printStackTrace();
            setDefaultStylesForCategory(category);
        }
        // Reset index to first item
        currentStyleIndex = 0;
        updateStyleLabel();
        updateStyleNavigationButtons();
    }

    private void setDefaultStylesForCategory(String category) {
        switch (category) {
            case "Sepatu":
                currentStyleOptions = new String[]{"Running", "Casual", "Formal", "Sport"};
                currentStyleIds = new String[]{"00001", "00002", "00003", "00004"};
                break;
            case "Sandal":
                currentStyleOptions = new String[]{"Casual", "Formal", "Outdoor", "Pantai"};
                currentStyleIds = new String[]{"00002", "00003", "00005", "00006"};
                break;
            case "Kaos Kaki":
                currentStyleOptions = new String[]{"Olahraga", "Formal", "Casual", "Ankle"};
                currentStyleIds = new String[]{"00007", "00003", "00002", "00008"};
                break;
            case "Lainnya":
                currentStyleOptions = new String[]{"Vintage", "Modern", "Klasik", "Trendy"};
                currentStyleIds = new String[]{"00009", "00010", "00011", "00012"};
                break;
            default:
                currentStyleOptions = new String[]{""};
                currentStyleIds = new String[]{""};
                break;
        }
    }

    private void updateStyleLabel() {
        if (currentStyleOptions.length > 0 && currentStyleIndex >= 0 && currentStyleIndex < currentStyleOptions.length) {
            lblStyle.setText(currentStyleOptions[currentStyleIndex]);
        } else {
            lblStyle.setText("");
        }
    }

    private void clearForm() {
        // Reset text fields
        txtNamaProduct.setText("");
        txtHargaJual.setText("");
        txtHargaBeli.setText("");
        txtMerk.setText("");
        txtUkuran.setText("");
        txtStok.setText("");

        cbCategory.setSelectedIndex(0);

        // Hide style panel
        Component[] components = panelCategory.getComponents();
        for (Component c : components) {
            if (c instanceof JPanel && c != cbCategory) {
                c.setVisible(false);
            }
        }

        // Reset radio buttons
        rbMale.setSelected(true);

        // Reset photo count label
        lblPhotoCount.setText("foto maks (0/1)");

        // Reset style-related variables
        currentStyleOptions = new String[]{""};
        currentStyleIndex = 0;
        updateStyleLabel();
        updateStyleNavigationButtons();
    }

    public void prepareForPanelChange() {
        clearForm();
        // Additional cleanup if needed
    }

    public class RoundedBorder implements Border {

        private int radius;
        private Color borderColor;
        private int thickness;

        // Konstruktor dengan default thickness 1
        public RoundedBorder(int radius, Color borderColor) {
            this(radius, borderColor, 1);
        }

        // Konstruktor lengkap
        public RoundedBorder(int radius, Color borderColor, int thickness) {
            this.radius = radius;
            this.borderColor = borderColor;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();

            // Aktifkan anti-aliasing untuk tepi yang halus
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Set warna dan ketebalan border
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(thickness));

            // Gambar border rounded
            RoundRectangle2D roundedRectangle = new RoundRectangle2D.Double(
                    x, y, width - 1, height - 1, radius * 2, radius * 2
            );
            g2.draw(roundedRectangle);

            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius, radius, radius, radius);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

        // Getter dan setter untuk memungkinkan penyesuaian lebih lanjut
        public int getRadius() {
            return radius;
        }

        public void setRadius(int radius) {
            this.radius = radius;
        }

        public Color getBorderColor() {
            return borderColor;
        }

        public void setBorderColor(Color borderColor) {
            this.borderColor = borderColor;
        }

        public int getThickness() {
            return thickness;
        }

        public void setThickness(int thickness) {
            this.thickness = thickness;
        }
    }

    public class RoundedCircularButton extends JButton {

        private Color borderColor = Color.black;
        private int borderThickness = 1;
        private ImageIcon icon;

        public RoundedCircularButton(String text) {
            super(text);
            setupButtonStyle();
        }

        public RoundedCircularButton(String text, ImageIcon icon) {
            super(text);
            this.icon = icon;
            setupButtonStyle();
        }

        public void setCustomIcon(ImageIcon icon) {
            this.icon = icon;
            repaint();
        }

        public ImageIcon getCustomIcon() {
            return icon;
        }

        private void setupButtonStyle() {
            setBackground(Color.WHITE);
            setForeground(Color.BLACK);
            setFont(new Font("Arial", Font.BOLD, 12)); // Slightly smaller font
            setBorderPainted(false);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setPreferredSize(new Dimension(25, 25)); // Smaller preferred size
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Create circular button with slightly smaller size
            Ellipse2D circle = new Ellipse2D.Double(1, 1, getWidth() - 2, getHeight() - 2);

            // Set background color based on enabled state
            g2.setColor(isEnabled() ? getBackground() : Color.LIGHT_GRAY);
            g2.fill(circle);

            // Draw border based on enabled state
            g2.setColor(isEnabled() ? borderColor : Color.GRAY);
            g2.setStroke(new BasicStroke(borderThickness));
            g2.draw(circle);

            // Draw icon if available
            if (icon != null) {
                int iconWidth = icon.getIconWidth();
                int iconHeight = icon.getIconHeight();

                // Calculate center position for icon
                int x = (getWidth() - iconWidth) / 2;
                int y = (getHeight() - iconHeight) / 2;

                // Draw icon with transparency if disabled
                if (!isEnabled()) {
                    AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
                    g2.setComposite(alphaComposite);
                }
                icon.paintIcon(this, g2, x, y);
            }

            // Draw text (if needed)
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
    }

    private void updateStyleNavigationButtons() {
        // Reset button state each time this is called
        if (currentStyleOptions.length <= 1) {
            // If only one or no style, disable both buttons
            btnPrevStyle.setEnabled(false);
            btnNextStyle.setEnabled(false);
        } else {
            // Set button status based on current index
            btnPrevStyle.setEnabled(currentStyleIndex > 0);
            btnNextStyle.setEnabled(currentStyleIndex < currentStyleOptions.length - 1);
        }
        // Set button text color based on status
        btnPrevStyle.setForeground(btnPrevStyle.isEnabled() ? Color.BLACK : Color.GRAY);
        btnNextStyle.setForeground(btnNextStyle.isEnabled() ? Color.BLACK : Color.GRAY);
    }

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

    private JButton createStyledButton(String text, Color bgColor, Color fgColor, int roundedRadius) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fill rounded rectangle
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), roundedRadius, roundedRadius);

                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Optional: draw border
                g2.setColor(getBackground().darker());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, roundedRadius, roundedRadius);

                g2.dispose();
            }
        };

        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(false);

        return button;
    }

    private void getData() {
        try {
            String sql = "SELECT p.*, s.nama_style FROM produk p LEFT JOIN style s ON p.id_style = s.id_style WHERE p.id_produk = ?";
            try (PreparedStatement st = con.prepareStatement(sql)) {
                st.setString(1, kode);
                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                // Load basic product information
                txtNamaProduct.setText(rs.getString("nama_produk"));
                
                // Perbaikan untuk harga jual
                long hargaJual = rs.getLong("harga_jual");
                // Hapus DocumentFilter sementara
                txtHargaJual.setDocument(new PlainDocument());
                txtHargaJual.setText("Rp. " + NumberFormat.getNumberInstance(Locale.US).format(hargaJual));
                // Setel kembali DocumentFilter
                setRpField(txtHargaJual);
                
                // Perbaikan untuk harga beli
                long hargaBeli = rs.getLong("harga_beli");
                // Hapus DocumentFilter sementara
                txtHargaBeli.setDocument(new PlainDocument());
                txtHargaBeli.setText("Rp. " + NumberFormat.getNumberInstance(Locale.US).format(hargaBeli));
                // Setel kembali DocumentFilter
                setRpField(txtHargaBeli);
                
                txtMerk.setText(rs.getString("merk"));
                txtUkuran.setText(rs.getString("size"));

                    // Get stock information
                    String sqlstok = "SELECT produk_sisa FROM kartu_stok WHERE id_produk = ? ORDER BY tanggal_transaksi DESC LIMIT 1";
                    try (PreparedStatement stok = con.prepareStatement(sqlstok)) {
                        stok.setString(1, kode);
                        ResultSet rsstok = stok.executeQuery();
                        if (rsstok.next()) {
                            // Get the latest stock value
                            int latestStock = rsstok.getInt("produk_sisa");
                            txtStok.setText(String.valueOf(latestStock));
                        } else {
                            txtStok.setText("0");
                        }
                    }

                    // Set gender selection
                    String gender = rs.getString("gender");
                    if (gender != null) {
                        switch (gender.toLowerCase()) {
                            case "cowok":
                                rbMale.setSelected(true);
                                break;
                            case "cewek":
                                rbFemale.setSelected(true);
                                break;
                            case "unisex":
                                rbUnisex.setSelected(true);
                                break;
                        }
                    }

                    // Get category and style ID
                    String category = rs.getString("jenis_produk");
                    String styleId = rs.getString("id_style");

                    if (category != null && !category.isEmpty()) {
                        // First update the ComboBox - this is crucial!
                        // Remove ActionListener temporarily to prevent unwanted updates
                        ActionListener[] listeners = cbCategory.getActionListeners();
                        for (ActionListener listener : listeners) {
                            cbCategory.removeActionListener(listener);
                        }

                        // Set the selected category
                        for (int i = 0; i < cbCategory.getItemCount(); i++) {
                            String item = (String) cbCategory.getItemAt(i);
                            if (category.equalsIgnoreCase(item)) {
                                cbCategory.setSelectedIndex(i);
                                break;
                            }
                        }

                        updateStyleOptions(category);

                        // Now try to find the matching style ID
                        if (styleId != null && !styleId.isEmpty()) {
                            for (int i = 0; i < currentStyleIds.length; i++) {
                                if (styleId.equals(currentStyleIds[i])) {
                                    currentStyleIndex = i;
                                    break;
                                }
                            }
                            updateStyleLabel();
                            updateStyleNavigationButtons();
                        }

                        // Add back the ActionListener
                        for (ActionListener listener : listeners) {
                            cbCategory.addActionListener(listener);
                        }
                    }

                    // Handle product image
                    try {
                        // Try to get image as BLOB
                        Blob imageBlob = rs.getBlob("gambar");

                        if (imageBlob != null && imageBlob.length() > 0) {
                            try (InputStream is = imageBlob.getBinaryStream()) {
                                BufferedImage originalImage = ImageIO.read(is);
                                if (originalImage != null) {
                                    // Resize image to fit preview area
                                    Image resizedImage = originalImage.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                                    displayedImage = new ImageIcon(resizedImage);

                                    // Show image
                                    imagePreview.setIcon(displayedImage);

                                    // Update UI
                                    uploadInstructionsPanel.setVisible(false);
                                    imagePreview.setVisible(true);
                                    btnRemoveImage.setVisible(true);

                                    // Update photo count label
                                    lblPhotoCount.setText("foto maks (1/1)");

                                    System.out.println("Successfully loaded image from BLOB");
                                } else {
                                    removeSelectedImage();
                                }
                            } catch (Exception e) {
                                removeSelectedImage();
                                e.printStackTrace();
                            }
                        } else {
                            removeSelectedImage();
                        }
                    } catch (SQLException e) {
                        removeSelectedImage();
                        e.printStackTrace();
                    }

                    // Generate barcode if needed
                    String barcode = rs.getString("id_produk");
                    if (barcode != null && !barcode.isEmpty()) {
                        generateBarcodeFromProductId(barcode);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Produk tidak ditemukan", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saat mengambil data produk: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void generateBarcodeFromProductId(String productId) {
        try {
            barcodeArea.removeAll();

            // Use the product ID as the barcode value instead of random number
            String barcodeValue = productId;
            Barcode barcode = BarcodeFactory.createCode128(barcodeValue);

            barcode.setBarWidth(2);

            barcodeArea.setLayout(new BorderLayout());

            JPanel barcodeImagePanel = new JPanel();
            barcodeImagePanel.setOpaque(false);

            barcodeImagePanel.setLayout(new BoxLayout(barcodeImagePanel, BoxLayout.Y_AXIS));

            barcodeImagePanel.add(Box.createVerticalStrut(25));

            JPanel barcodeContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
            barcodeContainer.setOpaque(false);

            Component barcodeComp = barcode;
            int newHeight = 80;

            JPanel heightPanel = new JPanel(new BorderLayout()) {
                @Override
                public Dimension getPreferredSize() {
                    Dimension dim = super.getPreferredSize();
                    return new Dimension(dim.width, newHeight);
                }
            };
            heightPanel.setOpaque(false);
            heightPanel.add(barcodeComp, BorderLayout.CENTER);

            barcodeContainer.add(heightPanel);

            barcodeImagePanel.add(barcodeContainer);
            barcodeArea.add(barcodeImagePanel, BorderLayout.CENTER);

            // Update the display
            barcodeArea.revalidate();
            barcodeArea.repaint();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error saat membuat barcode: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public String getCurrentStyleId() {
        if (currentStyleIds != null && currentStyleIndex >= 0 && currentStyleIndex < currentStyleIds.length) {
            return currentStyleIds[currentStyleIndex];
        }
        return "";
    }

 private void updateProduct() {
    // Show confirmation popup first
    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(EditProductPanel.this);
    PopUp_TambahProdukApakahAndaYakinInginEditProduk dialog = new PopUp_TambahProdukApakahAndaYakinInginEditProduk(parentFrame);
    
    // Add action listener for OK button
    dialog.addOKButtonActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // This code will execute when OK button is clicked
            try {
                String hargaJualText = txtHargaJual.getText().trim()
                        .replaceAll("(?i)rp\\.?\\s?", "")  // Hilangkan semua variasi Rp
                        .replaceAll("\\.", "")             // Hilangkan titik
                        .replaceAll(",", "");              // Hilangkan koma
                
                String hargaBeliText = txtHargaBeli.getText().trim()
                        .replaceAll("(?i)rp\\.?\\s?", "")
                        .replaceAll("\\.", "")
                        .replaceAll(",", "");
                
                if (txtNamaProduct.getText().trim().isEmpty() ||
                    hargaJualText.isEmpty() || 
                    hargaBeliText.isEmpty() ||
                    txtMerk.getText().trim().isEmpty() ||
                    txtUkuran.getText().trim().isEmpty()) {

                    JOptionPane.showMessageDialog(EditProductPanel.this,
                            "Semua field harus diisi",
                            "Validasi Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Get values from form
                String namaProduk = txtNamaProduct.getText().trim();
                int hargaJual = Integer.parseInt(hargaJualText); // Gunakan teks yang sudah dibersihkan
                int hargaBeli = Integer.parseInt(hargaBeliText); // Gunakan teks yang sudah dibersihkan
                String merk = txtMerk.getText().trim();
                String ukuran = txtUkuran.getText().trim();

                // Get gender selection
                String gender = "";
                if (rbMale.isSelected()) {
                    gender = "Cowok";
                } else if (rbFemale.isSelected()) {
                    gender = "Cewek";
                } else if (rbUnisex.isSelected()) {
                    gender = "Unisex";
                }

                // Get category and style
                String category = (String) cbCategory.getSelectedItem();
                String styleId = currentStyleIds[currentStyleIndex];

                // Create SQL statement for updating the product
                String sql = "UPDATE produk SET "
                        + "nama_produk = ?, "
                        + "harga_jual = ?, "
                        + "harga_beli = ?, "
                        + "merk = ?, "
                        + "size = ?, "
                        + "gender = ?, "
                        + "jenis_produk = ?, "
                        + "id_style = ? ";

                // Check if we need to update the image
                if (displayedImage != null) {
                    sql += ", gambar = ? ";
                }

                sql += "WHERE id_produk = ?";

                try (PreparedStatement stmt = con.prepareStatement(sql)) {
                    // Set values in prepared statement
                    stmt.setString(1, namaProduk);
                    stmt.setInt(2, hargaJual);
                    stmt.setInt(3, hargaBeli);
                    stmt.setString(4, merk);
                    stmt.setString(5, ukuran);
                    stmt.setString(6, gender);
                    stmt.setString(7, category);
                    stmt.setString(8, styleId);

                    int paramIndex = 9;
                    
                    // Handle image if it exists
                    if (displayedImage != null) {
                        // Convert ImageIcon to byte array
                        Image img = ((ImageIcon) displayedImage).getImage();
                        BufferedImage bufferedImage = new BufferedImage(
                                img.getWidth(null),
                                img.getHeight(null),
                                BufferedImage.TYPE_INT_RGB
                        );

                        Graphics g = bufferedImage.createGraphics();
                        g.drawImage(img, 0, 0, null);
                        g.dispose();

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(bufferedImage, "jpg", baos);
                        byte[] imageBytes = baos.toByteArray();

                        stmt.setBytes(paramIndex++, imageBytes);
                    }

                    // Set product ID as the last parameter
                    stmt.setString(paramIndex, kode);

                    // Execute update
                    int rowsAffected = stmt.executeUpdate();

                    if (rowsAffected > 0) {
                        // Update stock if necessary (if stock value changed)
                        updateStockIfNeeded();
                        
                        PindahanAntarPopUp.showEditProductBerhasilDiEdit(parentFrame);
                        Productt mainFrame = Productt.getMainFrame();
                        if (mainFrame != null) {
                            // Pindah ke panel product
                            mainFrame.switchBackToProductPanel();
                        }
                        // Optionally refresh the form or close it
                        // refreshForm(); or dispose();
                    } else {
                        JOptionPane.showMessageDialog(EditProductPanel.this,
                                "Gagal mengupdate produk",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(EditProductPanel.this,
                        "Harga harus berupa angka",
                        "Validasi Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(EditProductPanel.this,
                        "Database error: " + ex.getMessage(),
                        "SQL Error",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(EditProductPanel.this,
                        "Error: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    });
    dialog.setVisible(true);
}

    private void updateStockIfNeeded() {
        try {
            int newStock = Integer.parseInt(txtStok.getText().trim());

            // Get current stock from database
            String sqlGetStock = "SELECT produk_sisa FROM kartu_stok WHERE id_produk = ? ORDER BY tanggal_transaksi DESC LIMIT 1";
            try (PreparedStatement stmt = con.prepareStatement(sqlGetStock)) {
                stmt.setString(1, kode);
                ResultSet rs = stmt.executeQuery();

                int currentStock = 0;
                if (rs.next()) {
                    currentStock = rs.getInt("produk_sisa");
                }

                // If stock value changed, add a new entry in kartu_stok
                if (newStock != currentStock) {
                    String sqlInsertStock = "INSERT INTO kartu_stok (id_produk, tanggal_transaksi, produk_masuk, produk_keluar, produk_sisa) "
                            + "VALUES (?, NOW(), ?, ?, ?)";

                    try (PreparedStatement stmtInsert = con.prepareStatement(sqlInsertStock)) {
                        stmtInsert.setString(1, kode);

                        int produkMasuk = 0;
                        int produkKeluar = 0;

                        if (newStock > currentStock) {
                            produkMasuk = newStock - currentStock;
                        } else {
                            produkKeluar = currentStock - newStock;
                        }

                        stmtInsert.setInt(2, produkMasuk);
                        stmtInsert.setInt(3, produkKeluar);
                        stmtInsert.setInt(4, newStock);

                        stmtInsert.executeUpdate();
                    }
                }
            }
        } catch (NumberFormatException e) {
            // Handle invalid stock number
            JOptionPane.showMessageDialog(this,
                    "Stok harus berupa angka",
                    "Validasi Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
