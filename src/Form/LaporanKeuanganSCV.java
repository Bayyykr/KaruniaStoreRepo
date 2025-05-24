/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Form;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LaporanKeuanganSCV {
    
    // Kelas untuk menyimpan data transaksi
    static class Transaksi {
        private String tanggal;
        private String keterangan;
        private double debet;
        private double kredit;
        private double saldo;
        
        public Transaksi(String tanggal, String keterangan, double debet, double kredit, double saldo) {
            this.tanggal = tanggal;
            this.keterangan = keterangan;
            this.debet = debet;
            this.kredit = kredit;
            this.saldo = saldo;
        }
        
        // Getter methods
        public String getTanggal() { return tanggal; }
        public String getKeterangan() { return keterangan; }
        public double getDebet() { return debet; }
        public double getKredit() { return kredit; }
        public double getSaldo() { return saldo; }
    }
    
    public static void main(String[] args) {
        // Membuat list transaksi sesuai dengan data pada gambar
        List<Transaksi> transaksiList = new ArrayList<>();
        
        transaksiList.add(new Transaksi("01/12/2014", "Saldo Awal", 2000000, 0, 2000000));
        transaksiList.add(new Transaksi("02/12/2014", "Beli ATK", 0, 350000, 1650000));
        transaksiList.add(new Transaksi("03/12/2014", "Bayar Listrik & Telepon", 0, 150000, 1500000));
        transaksiList.add(new Transaksi("05/12/2014", "Ambil Uang dari BANK", 1500000, 0, 3000000));
        transaksiList.add(new Transaksi("07/12/2014", "Beli Printer", 0, 850000, 2150000));
        
        // Menghitung total
        double totalDebet = transaksiList.stream().mapToDouble(Transaksi::getDebet).sum();
        double totalKredit = transaksiList.stream().mapToDouble(Transaksi::getKredit).sum();
        double saldoAkhir = transaksiList.get(transaksiList.size() - 1).getSaldo();
        
        // Membuat file CSV yang rapi untuk Excel Indonesia
        buatFileCSVRapi(transaksiList, totalDebet, totalKredit, saldoAkhir);
        
        // Membuat file Excel Indonesia dengan format yang lebih aman
        buatFileExcelIndonesia(transaksiList, totalDebet, totalKredit, saldoAkhir);
        
        // Menampilkan preview di console
        tampilkanPreview(transaksiList, totalDebet, totalKredit, saldoAkhir);
        
        System.out.println("\nFile CSV berhasil dibuat:");
        System.out.println("1. laporan_keuangan_sederhana.csv (semicolon delimiter)");
        System.out.println("2. laporan_keuangan_excel_indonesia.csv (format Indonesia)");
        System.out.println("\nTips:");
        System.out.println("- Jika masih rusak, coba buka dengan 'Data > From Text/CSV' di Excel");
        System.out.println("- Atau ubah regional setting Windows ke English (US) sementara");
    }
    
    public static void buatFileCSVRapi(List<Transaksi> transaksiList, double totalDebet, double totalKredit, double saldoAkhir) {
        String namaFile = "laporan_keuangan_sederhana.csv";
        
        try (FileWriter writer = new FileWriter(namaFile, java.nio.charset.StandardCharsets.UTF_8)) {
            // Menulis BOM untuk UTF-8 agar Excel bisa membaca dengan benar
            writer.write('\ufeff');
            
            // Menulis header CSV dengan SEMICOLON sebagai pemisah (untuk Excel Indonesia)
            writer.append("No;Tanggal;Keterangan;Debet;Kredit;Saldo\n");
            
            // Menulis data transaksi
            for (int i = 0; i < transaksiList.size(); i++) {
                Transaksi t = transaksiList.get(i);
                
                // No
                writer.append(String.valueOf(i + 1)).append(";");
                
                // Tanggal
                writer.append(t.getTanggal()).append(";");
                
                // Keterangan
                writer.append(t.getKeterangan()).append(";");
                
                // Debet (kosong jika 0)
                if (t.getDebet() > 0) {
                    writer.append(String.valueOf((long)t.getDebet()));
                }
                writer.append(";");
                
                // Kredit (kosong jika 0)
                if (t.getKredit() > 0) {
                    writer.append(String.valueOf((long)t.getKredit()));
                }
                writer.append(";");
                
                // Saldo
                writer.append(String.valueOf((long)t.getSaldo()));
                writer.append("\n");
            }
            
            // Baris kosong (6-10)
            for (int i = 6; i <= 10; i++) {
                writer.append(String.valueOf(i)).append(";;;;;\n");
            }
            
            // Baris total
            writer.append(";;;Jumlah;");
            writer.append(String.valueOf((long)totalDebet)).append(";");
            writer.append(String.valueOf((long)totalKredit)).append(";");
            writer.append(String.valueOf((long)saldoAkhir));
            writer.append("\n");
            
        } catch (IOException e) {
            System.err.println("Error saat membuat file CSV: " + e.getMessage());
        }
    }
    
    // Method untuk menampilkan preview di console dengan format yang rapi
    public static void tampilkanPreview(List<Transaksi> transaksiList, double totalDebet, double totalKredit, double saldoAkhir) {
        NumberFormat formatter = new DecimalFormat("#,###");
        
        System.out.println("\n=== LAPORAN KEUANGAN SEDERHANA ===");
        System.out.println("┌─────┬────────────┬─────────────────────────┬─────────────┬─────────────┬─────────────┐");
        System.out.println("│ No  │   Tanggal  │       Keterangan        │    Debet    │   Kredit    │    Saldo    │");
        System.out.println("├─────┼────────────┼─────────────────────────┼─────────────┼─────────────┼─────────────┤");
        
        for (int i = 0; i < transaksiList.size(); i++) {
            Transaksi t = transaksiList.get(i);
            System.out.printf("│ %-3d │ %-10s │ %-23s │ %11s │ %11s │ %11s │%n",
                            i + 1,
                            t.getTanggal(),
                            t.getKeterangan(),
                            t.getDebet() > 0 ? formatter.format(t.getDebet()) : "",
                            t.getKredit() > 0 ? formatter.format(t.getKredit()) : "",
                            formatter.format(t.getSaldo()));
        }
        
        // Baris kosong
        for (int i = 6; i <= 10; i++) {
            System.out.printf("│ %-3d │            │                         │             │             │             │%n", i);
        }
        
        System.out.println("├─────┼────────────┼─────────────────────────┼─────────────┼─────────────┼─────────────┤");
        System.out.printf("│     │            │        JUMLAH           │ %11s │ %11s │ %11s │%n",
                         formatter.format(totalDebet),
                         formatter.format(totalKredit),
                         formatter.format(saldoAkhir));
        System.out.println("└─────┴────────────┴─────────────────────────┴─────────────┴─────────────┴─────────────┘");
    }
    
    // Method untuk membuat file yang bisa dibuka dengan rapi di Excel Indonesia
    public static void buatFileExcelIndonesia(List<Transaksi> transaksiList, double totalDebet, double totalKredit, double saldoAkhir) {
        String namaFile = "laporan_keuangan_excel_indonesia.csv";
        
        try (FileWriter writer = new FileWriter(namaFile, java.nio.charset.StandardCharsets.UTF_8)) {
            // BOM untuk UTF-8
            writer.write('\ufeff');
            
            // Header dengan semicolon dan quotes untuk keamanan
            writer.append("\"No\";\"Tanggal\";\"Keterangan\";\"Debet\";\"Kredit\";\"Saldo\"\n");
            
            // Data transaksi dengan quotes dan semicolon
            for (int i = 0; i < transaksiList.size(); i++) {
                Transaksi t = transaksiList.get(i);
                
                writer.append("\"").append(String.valueOf(i + 1)).append("\";");
                writer.append("\"").append(t.getTanggal()).append("\";");
                writer.append("\"").append(t.getKeterangan()).append("\";");
                writer.append("\"").append(t.getDebet() > 0 ? String.valueOf((long)t.getDebet()) : "").append("\";");
                writer.append("\"").append(t.getKredit() > 0 ? String.valueOf((long)t.getKredit()) : "").append("\";");
                writer.append("\"").append(String.valueOf((long)t.getSaldo())).append("\"\n");
            }
            
            // Baris kosong
            for (int i = 6; i <= 10; i++) {
                writer.append("\"").append(String.valueOf(i)).append("\";\"\";\"\";\"\";\"\";\"\"\n");
            }
            
            // Total
            writer.append("\"\";\"\";\"Jumlah\";");
            writer.append("\"").append(String.valueOf((long)totalDebet)).append("\";");
            writer.append("\"").append(String.valueOf((long)totalKredit)).append("\";");
            writer.append("\"").append(String.valueOf((long)saldoAkhir)).append("\"\n");
            
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}