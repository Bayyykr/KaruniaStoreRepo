package produk;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;
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
import PopUp_all.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.awt.dnd.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Date;
import db.conn;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Locale;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

public class AddNewProductFormm extends JPanel {

    private JTextField txtInput;
    private JButton btnGenerate;
    private JButton btnSave;
    private JPanel barcodePanel;
    private Barcode barcode;

    private RoundedTextField txtNamaProduct, txtHargaJual, txtHargaBeli, txtMerk, txtUkuran, txtStok;
    private JRadioButton rbMale, rbFemale, rbUnisex;
    private ButtonGroup genderGroup;
    private ComboboxCustom cbCategory;
    private JLabel lblStyle;
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

    private String[] currentStyleOptions = {""};
    private int currentStyleIndex = 0;

    private static final int ICON_SIZE = 16;
    private int cornerRadius = 15;
    private Connection con;
    private String currentBarcodeValue = "";

    public AddNewProductFormm() {
        this(null);
    }

    public void setMainFrame(Productt frame) {
        this.mainFrame = frame;
    }

    public AddNewProductFormm(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(null);
        setBackground(new Color(255, 255, 255));

        con = conn.getConnection();

        // Judul Form
        JLabel lblTitle = new JLabel("Add New Product");
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
        btnAddProduct = createStyledButton("Add Product", new Color(52, 58, 64), Color.WHITE, 40);
        btnAddProduct.setBounds(856, 18, 150, 35);

        // Ikon Add Product diperkecil
        ImageIcon origAddIcon = new ImageIcon(getClass().getResource("/SourceImage/icon/centang.png"));
        int iconAddSize = 15;
        Image scaledAddImage = origAddIcon.getImage().getScaledInstance(iconAddSize, iconAddSize, Image.SCALE_SMOOTH);
        ImageIcon scaledAddIcon = new ImageIcon(scaledAddImage);
        btnAddProduct.setIcon(scaledAddIcon);
        btnAddProduct.setIconTextGap(15);  // Jarak antara ikon dan teks
        add(btnAddProduct);

        btnAddProduct.addActionListener(e -> {
            addProduct();
        });

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
        setRpField(txtHargaJual);
        panelGeneral.add(txtHargaJual);

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
        setRpField(txtHargaBeli);
        panelGeneral.add(txtHargaBeli);

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
        return Integer.parseInt(text); // Akan throw exception jika melebihi INT.MAX_VALUE
    }

// Method utility untuk mengatur nilai numerik ke textfield
    private void setNumericValue(JTextField textField, long value, String prefix) {
        String ValueStr = String.valueOf(value);
        if (ValueStr.length() > 12) {
            value = Long.parseLong(ValueStr.substring(0, 12));
        }
        NumberFormat formatter = NumberFormat.getInstance(new Locale("id", "ID"));
        if (formatter instanceof DecimalFormat) {
            ((DecimalFormat) formatter).applyPattern("#,###");
        }
        textField.setText(prefix + formatter.format(value));
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

        rbMale = new JRadioButton("Cowok");
        rbMale.setBounds(20, 390, 70, 30);
        rbMale.setSelected(true);
        rbMale.setBackground(Color.WHITE);
        rbMale.setFocusPainted(false);  // Menghilangkan kotak fokus
        rbMale.setBorderPainted(false); // Menghilangkan border
        rbMale.setContentAreaFilled(false); // Membuat latar belakang transparan
        add(rbMale);
        genderGroup.add(rbMale);

        rbFemale = new JRadioButton("Cewek");
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
        // Area barcode menggunakan RoundedPanelProduk dengan RoundedBorder
        barcodeArea = new RoundedPanelProduk(); // Gunakan variabel kelas
        barcodeArea.setBounds(20, 45, 450, 120);
        barcodeArea.setBackground(Color.WHITE);
        // Tambahkan RoundedBorder dengan radius 15, warna abu-abu muda, ketebalan 1
        barcodeArea.setBorder(new RoundedBorder(15, Color.BLACK));
        panelBarcode.add(barcodeArea);
        // Button Generate Barcode (dengan RoundedButton)
        btnGenerateBarcode = createStyledButton("GENERATE BARCODE", new Color(52, 58, 64), Color.WHITE, 15);
        btnGenerateBarcode.setBounds(500, 45, 210, 40);
        panelBarcode.add(btnGenerateBarcode);
        btnGenerateBarcode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateBarcode();
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

            // Generate and store the barcode value
            currentBarcodeValue = generateRandom16DigitNumber();
            Barcode barcode = BarcodeFactory.createCode128(currentBarcodeValue);

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
        StringBuilder sb = new StringBuilder(26);
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
                PindahanAntarPopUp.showTambahProdukGenerateBarcodeTerlebihDahulu(parentFrame);
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
                prepareForPanelChange();

                // Gunakan SwingUtilities untuk mencari parent frame
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(AddNewProductFormm.this);
                Popup_keluaraddnewproduct dialog = new Popup_keluaraddnewproduct(parentFrame);
                dialog.setVisible(true);
                removeSelectedImage();
            }
        });
    }

    private void updateStyleOptions(String category) {
        switch (category) {
            case "Sepatu":
                currentStyleOptions = new String[]{"Casual", "Formal", "Sport", "Running"};
                break;
            case "Sandal":
                currentStyleOptions = new String[]{"Casual", "Formal", "Outdoor", "Pantai"};
                break;
            case "Kaos Kaki":
                currentStyleOptions = new String[]{"Olahraga", "Formal", "Casual", "Ankle"};
                break;
            case "Lainnya":
                currentStyleOptions = new String[]{"Vintage", "Modern", "Klasik", "Trendy"};
                break;
            default:
                currentStyleOptions = new String[]{""};
                break;
        }

        // Reset index dan update label
        currentStyleIndex = 0;
        updateStyleLabel();
        updateStyleNavigationButtons(); // Tambahkan ini untuk mereset navigasi tombol
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
        resetPriceFields();
        txtHargaJual.setText("");
        txtHargaBeli.setText("");
        txtMerk.setText("");
        txtUkuran.setText("");
        txtStok.setText("");

        // Reset combo boxes
//        cbDiscount.setSelectedIndex(0);
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

        removeSelectedImage();
        // Reset photo count label
        lblPhotoCount.setText("foto maks (0/1)");

        // Reset style-related variables
        currentStyleOptions = new String[]{""};
        currentStyleIndex = 0;
        updateStyleLabel();
        updateStyleNavigationButtons();

        currentBarcodeValue = ""; // Reset barcode value
        barcodeArea.removeAll();
        barcodeArea.revalidate();
        barcodeArea.repaint();

        txtNamaProduct.requestFocus();
    }

// Method to reset form when panel is changed or closed
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
        // Reset button state setiap kali dipanggil
        if (currentStyleOptions.length <= 1) {
            // Jika hanya satu atau tidak ada style, nonaktifkan kedua tombol
            btnPrevStyle.setEnabled(false);
            btnNextStyle.setEnabled(false);
        } else {
            // Atur status tombol berdasarkan indeks saat ini
            btnPrevStyle.setEnabled(currentStyleIndex > 0);
            btnNextStyle.setEnabled(currentStyleIndex < currentStyleOptions.length - 1);
        }

        // Atur warna teks tombol sesuai status
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

    private void addProduct() {
        // First validate the form
        if (!validateForm()) {
            return;
        }

        // Check barcode
        if (currentBarcodeValue == null || currentBarcodeValue.isEmpty()) {
            PindahanAntarPopUp.showTambahProdukGenerateBarcodeTerlebihDahulu(parentFrame);
            return;
        }

        // Validate stock amount
        int stockAmount;
        try {
            stockAmount = Integer.parseInt(txtStok.getText().trim());
            if (stockAmount < 0) {
                JOptionPane.showMessageDialog(this,
                        "Jumlah stok harus lebih dari atau sama dengan 0.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Jumlah stok harus berupa angka yang valid.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Show confirmation dialog
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(AddNewProductFormm.this);
        PopUp_TambahProdukApakahAndaYakinTambahProduk dialog = new PopUp_TambahProdukApakahAndaYakinTambahProduk(parentFrame);

        // Add action listener for OK button
        dialog.addOKButtonActionListener(e -> {
            try {
                // Prepare product data
                String productId = currentBarcodeValue;
                String category = convertCategory((String) cbCategory.getSelectedItem());
                String gender = getSelectedGender();
                String imagePath = selectedImageFile != null ? saveImageToServer(selectedImageFile) : "";
                // Parse prices
                double hargaJual = parsePrice(txtHargaJual.getText().trim(), "Harga jual tidak boleh kosong.");
                double hargaBeli = parsePrice(txtHargaBeli.getText().trim(), "Harga beli tidak boleh kosong.");

                insertProduct(productId, txtMerk.getText().trim(), category,
                        txtUkuran.getText().trim(), gender,
                        selectedImageFile != null ? imagePath : "default.jpg",
                        txtNamaProduct.getText().trim(), hargaJual, hargaBeli,
                        getStyleIdFromName(currentStyleOptions[currentStyleIndex]));

                // Insert stock
                insertStock(stockAmount, productId);

                // Show success message and clear form
                PindahanAntarPopUp.showTambahProdukBerhasilDiTambahkan(parentFrame);
                clearForm();

                Productt mainFrame = Productt.getMainFrame();
                if (mainFrame != null) {
                    // Pindah ke panel product
                    mainFrame.switchBackToProductPanel();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(AddNewProductFormm.this,
                        "Gagal menambahkan produk: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }

// Helper methods for the addProduct function
    private String convertCategory(String category) {
        switch (category) {
            case "Sandal":
                return "sandal";
            case "Sepatu":
                return "sepatu";
            case "Kaos Kaki":
                return "kaos kaki";
            case "Lainnya":
                return "lainnya";
            default:
                return "";
        }
    }

    private String getSelectedGender() {
        if (rbFemale.isSelected()) {
            return "Cewek";
        }
        if (rbUnisex.isSelected()) {
            return "Unisex";
        }
        return "Cowok";
    }

    private double parsePrice(String priceText, String errorMessage) throws Exception {
        String cleanedPrice = priceText.replace("Rp. ", "").replace(".", "").replace(",", "").trim();
        if (cleanedPrice.isEmpty()) {
            throw new NumberFormatException(errorMessage);
        }
        return Double.parseDouble(cleanedPrice);
    }

    private void insertStock(int stockAmount, String productId) throws Exception {
        String stockSql = "INSERT INTO kartu_stok (produk_masuk, produk_keluar, tanggal_transaksi, produk_sisa, id_produk) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stockSt = con.prepareStatement(stockSql)) {
            stockSt.setInt(1, stockAmount);
            stockSt.setInt(2, 0);
            stockSt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            stockSt.setInt(4, stockAmount);
            stockSt.setString(5, productId);

            if (stockSt.executeUpdate() <= 0) {
                throw new Exception("Gagal menambahkan informasi stok.");
            }
        }
    }

    private boolean validateForm() {
        if (txtNamaProduct.getText().trim().isEmpty()) {
            PindahanAntarPopUp.showTambahProdukNamaTidakBolehKosong(parentFrame);
            txtNamaProduct.requestFocus();
            return false;
        }

        if (txtHargaJual.getText().trim().isEmpty()) {
            PindahanAntarPopUp.showTambahProdukHargaJualTidakBolehKosong(parentFrame);
            txtHargaJual.requestFocus();
            return false;
        }

        if (txtHargaBeli.getText().trim().isEmpty()) {
            PindahanAntarPopUp.showTambahProdukHargaBeliTidakBolehKosong(parentFrame);
            txtHargaBeli.requestFocus();
            return false;
        }
        if (txtMerk.getText().trim().isEmpty()) {
            PindahanAntarPopUp.showTambahProdukMerkTidakBolehKosong(parentFrame);
            txtMerk.requestFocus();
            return false;
        }

        if (txtUkuran.getText().trim().isEmpty()) {
            PindahanAntarPopUp.showTambahProdukUkuranTidakBolehKosong(parentFrame);
            txtUkuran.requestFocus();
            return false;
        }

        if (txtStok.getText().trim().isEmpty()) {
            PindahanAntarPopUp.showTambahProdukStokTidakBolehKosong(parentFrame);
            txtStok.requestFocus();
            return false;
        }

        // Validate category selection
        if (cbCategory.getSelectedIndex() == 0) {
            PindahanAntarPopUp.showTambahProdukPilihKategoriDulu(parentFrame);
            cbCategory.requestFocus();
            return false;
        }

        try {
            // Persiapkan input harga untuk konversi dengan menghapus format mata uang
            String hargaJualText = txtHargaJual.getText().trim().replace("Rp. ", "").replace("Rp.", "")
                    .replace("Rp ", "").replace("rp. ", "").replace("rp ", "").replace("rp.", "")
                    .replace(".", "").replace(",", "").trim();

            String hargaBeliText = txtHargaBeli.getText().trim().replace("Rp. ", "").replace("Rp.", "")
                    .replace("Rp ", "").replace("rp. ", "").replace("rp ", "").replace("rp.", "")
                    .replace(".", "").replace(",", "").trim();

            // Validasi format harga yang sudah dibersihkan
            if (hargaJualText.isEmpty()) {
                PindahanAntarPopUp.showTambahProdukHargaJualTidakBolehKosong(parentFrame);
                txtHargaJual.requestFocus();
                return false;
            }

            if (hargaBeliText.isEmpty()) {
                PindahanAntarPopUp.showTambahProdukHargaBeliTidakBolehKosong(parentFrame);
                txtHargaBeli.requestFocus();
                return false;
            }

            // Konversi ke nilai numerik
            double hargaJual = Double.parseDouble(hargaJualText);
            double hargaBeli = Double.parseDouble(hargaBeliText);
            int stok = Integer.parseInt(txtStok.getText().trim());

            // Validasi nilai-nilai numerik
            if (hargaJual <= 0) {
                PindahanAntarPopUp.showTambahProdukHargaJualLebihDari0(parentFrame);
                txtHargaJual.requestFocus();
                return false;
            }

            if (hargaBeli <= 0) {
                PindahanAntarPopUp.showTambahProdukHargaBeliLebihDari0(parentFrame);
                txtHargaBeli.requestFocus();
                return false;
            }

            if (hargaJual <= hargaBeli) {
                PindahanAntarPopUp.showTambahProdukhargaJualHarusLebihDariHargaBeli(parentFrame);
                txtHargaJual.requestFocus();
                return false;
            }

            if (stok < 0) {
                PindahanAntarPopUp.showTambahProdukStokTidakBolehNegatif(parentFrame);
                txtStok.requestFocus();
                return false;
            }

        } catch (NumberFormatException e) {
            PindahanAntarPopUp.showTambahProdukHargaDanStokHarusBerupaAngka(parentFrame);
            return false;
        }
        return true;
    }

    private String saveImageToServer(File imageFile) {
        if (imageFile == null) {
            return ""; // Kembalikan string kosong
        }

        try {
            BufferedImage originalImage = ImageIO.read(imageFile);
            if (originalImage == null) {
                throw new IOException("Format file tidak didukung");
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(originalImage, "jpg", baos);

            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memproses gambar: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return "";
        }
    }

    private void insertProduct(String productId, String merk, String category, String size,
            String gender, String imageData, String namaProduk,
            double hargaJual, double hargaBeli, String styleId) throws Exception {

        String sql = "INSERT INTO produk (id_produk, merk, jenis_produk, size, gender, gambar, nama_produk, "
                + "harga_jual, harga_beli, id_style, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, productId);
            st.setString(2, merk);
            st.setString(3, category);
            st.setString(4, size);
            st.setString(5, gender);

            // Handle image data
            if (imageData != null && !imageData.isEmpty()) {
                try {
                    byte[] imageBytes = Base64.getDecoder().decode(imageData);
                    st.setBytes(6, imageBytes);
                } catch (IllegalArgumentException e) {
                    // Jika decode gagal, simpan path default
                    st.setString(6, generateDefaultImagePath());
                }
            } else {
                // Simpan path default jika tidak ada gambar
                st.setString(6, generateDefaultImagePath());
            }

            st.setString(7, namaProduk);
            st.setDouble(8, hargaJual);
            st.setDouble(9, hargaBeli);
            st.setString(10, styleId);
            st.setString(11, "dijual");

            if (st.executeUpdate() <= 0) {
                throw new Exception("Gagal menambahkan produk.");
            }
        }
    }

    private String getStyleIdFromName(String styleName) {
        String styleId = "00001";

        try {
            String sql = "SELECT id_style FROM style WHERE nama_style = ?";
            try (PreparedStatement st = con.prepareStatement(sql)) {
                st.setString(1, styleName);
                ResultSet rs = st.executeQuery();

                if (rs.next()) {
                    styleId = rs.getString("id_style");
                } else {
                    System.out.println("Style not found: " + styleName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return styleId;
    }

    private String generateDefaultImagePath() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());
        return "SourceCode/products/" + timestamp + "gambar_sepatu.png";
    }
}
