/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Form;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PDFExporter {
    
    public void exportToPDF(String namaToko, String filePath, String[][] dataTable) 
            throws IOException {
        
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);
        
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        
        try {
            float yPosition = 750;
            float margin = 50;
            float pageWidth = page.getMediaBox().getWidth();
            
            // Header toko - center
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
            float namaTokoWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(namaToko) / 1000 * 18;
            contentStream.newLineAtOffset((pageWidth - namaTokoWidth) / 2, yPosition);
            contentStream.showText(namaToko);
            contentStream.endText();
            
            // Tanggal - pojok kanan atas
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
            String currentDate = "Tanggal: " + dateFormat.format(new Date());
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            float dateWidth = PDType1Font.HELVETICA.getStringWidth(currentDate) / 1000 * 12;
            contentStream.newLineAtOffset(pageWidth - margin - dateWidth, yPosition); // Posisi kanan
            contentStream.showText(currentDate);
            contentStream.endText();
            
            yPosition -= 50; // Jarak antara header dan tabel
            
            // Tabel dengan lebar penuh
            drawTable(contentStream, margin, yPosition, pageWidth - (2 * margin), dataTable);
            
        } finally {
            contentStream.close();
        }
        
        document.save(filePath);
        document.close();
        System.out.println("PDF berhasil dibuat: " + filePath);
    }
    
    private void drawTable(PDPageContentStream contentStream, float x, float y, float tableWidth, String[][] data) 
            throws IOException {
        
        // Lebar kolom: No 10%, Keterangan 60%, Jumlah 30%
        float noWidth = tableWidth * 0.10f;
        float ketWidth = tableWidth * 0.60f;
        float jumlahWidth = tableWidth * 0.30f;
        float[] columnWidths = {noWidth, ketWidth, jumlahWidth};
        float cellHeight = 30;
        
        if (data == null || data.length < 1) {
            data = new String[][]{
                {"1", "Produk A", "Rp 50.000"},
                {"2", "Produk B", "Rp 75.000"},
                {"3", "Produk C", "Rp 100.000"}
            };
        }
        
        // Header tabel
        String[] headers = {"No", "Keterangan", "Jumlah"};
        drawTableRow(contentStream, x, y, columnWidths, cellHeight, headers, true);
        y -= cellHeight;
        
        // Data tabel
        for (int i = 0; i < data.length; i++) {
            String[] rowData = new String[3];
            rowData[0] = String.valueOf(i+1);
            rowData[1] = (data[i] != null && data[i].length > 0) ? data[i][0] : "";
            rowData[2] = (data[i] != null && data[i].length > 2) ? data[i][2] : 
                        ((data[i] != null && data[i].length > 1) ? data[i][1] : "");
            
            drawTableRow(contentStream, x, y, columnWidths, cellHeight, rowData, false);
            y -= cellHeight;
        }
    }
    
    private void drawTableRow(PDPageContentStream contentStream, float x, float y, 
                             float[] columnWidths, float cellHeight, String[] rowData, boolean isHeader) 
            throws IOException {
        
        float currentX = x;
        
        // Gambar border dan background
        for (int i = 0; i < columnWidths.length; i++) {
            if (isHeader) {
                contentStream.setNonStrokingColor(220, 220, 220);
                contentStream.addRect(currentX, y - cellHeight, columnWidths[i], cellHeight);
                contentStream.fill();
                contentStream.setNonStrokingColor(0, 0, 0);
            }
            
            contentStream.addRect(currentX, y - cellHeight, columnWidths[i], cellHeight);
            contentStream.stroke();
            currentX += columnWidths[i];
        }
        
        // Tulis text
        currentX = x;
        for (int i = 0; i < columnWidths.length; i++) {
            if (i < rowData.length && rowData[i] != null) {
                float textX = currentX;
                float textY = y - cellHeight/2 - 4;
                
                contentStream.beginText();
                contentStream.setFont(isHeader ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA, 10);
                
                if (isHeader) {
                    // Center align header text
                    float textWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(rowData[i]) / 1000 * 10;
                    textX = currentX + (columnWidths[i] - textWidth) / 2;
                } else {
                    // Left align data text with padding
                    textX = currentX + 5;
                    
                    // Center align "No" column data
                    if (i == 0) {
                        float textWidth = PDType1Font.HELVETICA.getStringWidth(rowData[i]) / 1000 * 10;
                        textX = currentX + (columnWidths[i] - textWidth) / 2;
                    }
                }
                
                contentStream.newLineAtOffset(textX, textY);
                contentStream.showText(rowData[i]);
                contentStream.endText();
            }
            currentX += columnWidths[i];
        }
    }
    
    public static void main(String[] args) {
        try {
            PDFExporter exporter = new PDFExporter();
            
            String[][] dataContoh = {
                {"Produk A", "100", "Rp 50.000"},
                {"Produk B", "150", "Rp 75.000"},
                {"Produk C", "200", "Rp 100.000"}
            };
            
            exporter.exportToPDF("TOKO SUMBER REJEKI", "laporan_toko.pdf", dataContoh);
            
        } catch (Exception e) {
            System.err.println("Error saat membuat PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }
}