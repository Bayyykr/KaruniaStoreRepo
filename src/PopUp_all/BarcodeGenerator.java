/*
 * Barcode Generator Application
 * Program ini memungkinkan pengguna untuk membuat barcode dari teks yang dimasukkan
 * Menggunakan library Barbecue untuk membuat barcode
 */

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

public class BarcodeGenerator extends JFrame {
    private JTextField txtInput;
    private JButton btnGenerate;
    private JButton btnSave;
    private JPanel barcodePanel;
    private Barcode barcode;
    
    public BarcodeGenerator() {
        // Set up the frame
        super("Barcode Generator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        
        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create input panel
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        JLabel lblInput = new JLabel("Masukkan Teks:");
        txtInput = new JTextField(20);
        inputPanel.add(lblInput, BorderLayout.WEST);
        inputPanel.add(txtInput, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnGenerate = new JButton("Generate Barcode");
        btnSave = new JButton("Simpan Barcode");
        btnSave.setEnabled(false);
        
        buttonPanel.add(btnGenerate);
        buttonPanel.add(btnSave);
        
        // Create barcode panel
        barcodePanel = new JPanel(new BorderLayout());
        barcodePanel.setBorder(BorderFactory.createEtchedBorder());
        
        // Add panels to main panel
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(barcodePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Add action listeners
        btnGenerate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateBarcode();
            }
        });
        
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveBarcode();
            }
        });
    }
    
    private void generateBarcode() {
        String input = txtInput.getText().trim();
        
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Silahkan masukkan teks terlebih dahulu!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Clear the barcode panel
            barcodePanel.removeAll();
            
            // Create barcode object
            barcode = BarcodeFactory.createCode128(input);
            barcode.setBarWidth(2);
//            barcode.setBarHeight(80);
            
            // Add barcode to panel
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            panel.add(barcode);
            barcodePanel.add(panel, BorderLayout.CENTER);
            
            // Add label below barcode
            JLabel lblData = new JLabel(input, JLabel.CENTER);
            barcodePanel.add(lblData, BorderLayout.SOUTH);
            
            // Enable save button
            btnSave.setEnabled(true);
            
            // Refresh the panel
            barcodePanel.revalidate();
            barcodePanel.repaint();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error saat membuat barcode: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void saveBarcode() {
        if (barcode == null) {
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan Barcode");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG Images", "png"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            // Add .png extension if not specified
            if (!file.getName().toLowerCase().endsWith(".png")) {
                file = new File(file.getAbsolutePath() + ".png");
            }
            
            try {
                // Create an image of the barcode
                BufferedImage image = BarcodeImageHandler.getImage(barcode);
                
                // Save the image
                ImageIO.write(image, "PNG", file);
                
                JOptionPane.showMessageDialog(this, 
                    "Barcode berhasil disimpan ke " + file.getAbsolutePath(), 
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error saat menyimpan barcode: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Launch application
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                BarcodeGenerator app = new BarcodeGenerator();
                app.setVisible(true);
            }
        });
    }
}