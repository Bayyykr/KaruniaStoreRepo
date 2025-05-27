package Form;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

public class ExcelExporter {
    
    // Default directory untuk menyimpan file
    private static final String DEFAULT_OUTPUT_DIR = "reports";
    
    // Class untuk menyimpan data laporan
    public static class LaporanData {
        private String nama;
        private String departemen;
        private Date tanggal;
        private double nilai;
        private String status;
        
        public LaporanData(String nama, String departemen) {
            this.nama = nama;
            this.departemen = departemen;
            this.tanggal = tanggal;
            this.nilai = nilai;
            this.status = status;
        }
        
        // Getters
        public String getNama() { return nama; }
        public String getDepartemen() { return departemen; }
        public Date getTanggal() { return tanggal; }
        public double getNilai() { return nilai; }
        public String getStatus() { return status; }
    }
    
    // Method untuk membuat direktori jika belum ada
    private static void createDirectoryIfNotExists(String dirPath) throws IOException {
        Path path = Paths.get(dirPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            System.out.println("Direktori dibuat: " + path.toAbsolutePath());
        }
    }
    
    // Method dengan path lengkap (tanpa nama user)
    public static void exportToExcel(List<LaporanData> dataList, String fullPath) {
        exportToExcelWithPath(dataList, null, null, fullPath);
    }
    
//    // Method dengan direktori dan nama file terpisah (tanpa nama user)
//    public static void exportToExcel(List<LaporanData> dataList, String directory, String fileName) {
//        exportToExcelWithPath(dataList, null, directory, fileName);
//    }
//    
    // Method dengan nama user - path lengkap
    public static void exportToExcel(List<LaporanData> dataList, String namaUser, String fullPath) {
        exportToExcelWithPath(dataList, namaUser, null, fullPath);
    }
    
    // Method dengan nama user - direktori dan file terpisah
    public static void exportToExcel(List<LaporanData> dataList, String namaUser, String directory, String fileName) {
        exportToExcelWithPath(dataList, namaUser, directory, fileName);
    }
    
    // Method utama untuk export dengan manajemen path
    private static void exportToExcelWithPath(List<LaporanData> dataList, String namaUser, String directory, String fileName) {
        Workbook workbook = null;
        String fullFilePath;
        
        try {
            // Menentukan path lengkap file
            if (directory != null) {
                // Jika directory dan fileName terpisah
                createDirectoryIfNotExists(directory);
                fullFilePath = Paths.get(directory, fileName).toString();
            } else {
                // Jika fileName sudah berupa path lengkap
                String parentDir = Paths.get(fileName).getParent() != null ? 
                    Paths.get(fileName).getParent().toString() : DEFAULT_OUTPUT_DIR;
                
                // Membuat direktori parent jika diperlukan
                if (!Paths.get(fileName).isAbsolute() && !fileName.contains(File.separator)) {
                    // Jika hanya nama file, gunakan default directory
                    createDirectoryIfNotExists(DEFAULT_OUTPUT_DIR);
                    fullFilePath = Paths.get(DEFAULT_OUTPUT_DIR, fileName).toString();
                } else {
                    // Jika sudah ada path
                    if (Paths.get(fileName).getParent() != null) {
                        createDirectoryIfNotExists(Paths.get(fileName).getParent().toString());
                    }
                    fullFilePath = fileName;
                }
            }
            
            workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Laporan Data");
            
            // Membuat style untuk header
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);
            
            int rowNum = 0;
            
            // Membuat judul laporan
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("LAPORAN DATA KEUANGAN");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
            
            // Baris kosong
            rowNum++;
            
            // Informasi tanggal export
            Row dateRow = sheet.createRow(rowNum++);
            Cell dateCell = dateRow.createCell(0);
            dateCell.setCellValue("Tanggal Laporan: " + new Date());
            dateCell.setCellStyle(dataStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 4));
            
            // Informasi nama user (jika ada)
            if (namaUser != null && !namaUser.isEmpty()) {
                Row nameRow = sheet.createRow(rowNum++);
                Cell nameCell = nameRow.createCell(0);
                nameCell.setCellValue("Nama: " + namaUser);
                nameCell.setCellStyle(dataStyle);
                sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 4));
            }
            
            // Baris kosong
            rowNum++;
            
            // Membuat header tabel
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"No", "Keterangan", "Total"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Mengisi data
            int no = 1;
            for (LaporanData data : dataList) {
                Row row = sheet.createRow(rowNum++);
                
                // No urut
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(no++);
                cell0.setCellStyle(dataStyle);
                
                // Keterangan (menggunakan nama sebagai keterangan)
                Cell cell1 = row.createCell(1);
                cell1.setCellValue(data.getNama());
                cell1.setCellStyle(dataStyle);
                
                // Total (menggunakan departemen sebagai total sementara)
                Cell cell2 = row.createCell(2);
                cell2.setCellValue(data.getDepartemen());
                cell2.setCellStyle(dataStyle);
            }
            
            // Mengatur lebar kolom
            sheet.setColumnWidth(0, 8 * 256);    // No - 8 karakter
            sheet.setColumnWidth(1, 20 * 256);   // Keterangan - 20 karakter
            sheet.setColumnWidth(2, 15 * 256);   // Total - 15 karakter
            
            // Menyimpan file
            FileOutputStream fileOut = new FileOutputStream(fullFilePath);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();
            
            System.out.println("File Excel berhasil dibuat: " + Paths.get(fullFilePath).toAbsolutePath());
            
        } catch (IOException e) {
            System.err.println("Error saat membuat file Excel: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    // Method untuk membuat style header
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    // Method untuk membuat style judul
    private static CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
    
    // Method untuk membuat style data
    private static CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
    
    // Method untuk membuat style angka
    private static CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setAlignment(HorizontalAlignment.RIGHT);
        
        // Format angka dengan 2 desimal
        CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.00"));
        return style;
    }
    
    // Method utama untuk testing dengan berbagai contoh
    public static void main(String[] args) {
        // Disable Log4j warnings
        System.setProperty("log4j2.loggerContextFactory", 
            "org.apache.logging.log4j.simple.SimpleLoggerContextFactory");
            
        // Membuat data contoh sesuai dengan gambar
        List<LaporanData> dataList = new ArrayList<>();
        dataList.add(new LaporanData("Pemasukan", "Rp. 96,800,000"));
        dataList.add(new LaporanData("Pengeluaran", "- Rp. 2,430,000"));
        dataList.add(new LaporanData("Laba", "Rp. 94,370,000"));
        
        // Contoh 1: Dengan nama user - path terpisah
        exportToExcel(dataList, "John Doe", "output/reports", "laporan_dengan_nama.xlsx");
        
        // Contoh 2: Dengan nama user - path lengkap
        exportToExcel(dataList, "Jane Smith", "laporan_full_path.xlsx");
        
        // Contoh 3: Tanpa nama user (backward compatibility)
        exportToExcel(dataList, "laporan_tanpa_nama.xlsx");
        
        // Contoh 4: Data keuangan dengan nama user
        exportToExcel(dataList, "Administrator", "reports/keuangan", "laporan_keuangan_2025.xlsx");
    }
}