package Form;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import db.conn; // Import koneksi database

/**
 * Kelas untuk membuat diagram batang dari data transaksi
 */
public class cobadiagram extends JPanel {
    
    private JFreeChart chart;
    private ChartPanel chartPanel;
    
    public cobadiagram() {
        // Initialize panel
        this.setBackground(Color.white);
        this.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        this.setPreferredSize(new Dimension(800, 500));
        
        // Create dataset from database
        DefaultCategoryDataset dataset = createDatasetFromDatabase();
        
        // Create chart
        chart = createChart(dataset);
        
        // Create panel for chart
        chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel.setBackground(Color.white);
        chartPanel.setPreferredSize(new Dimension(550, 350));
        
        // Add chart panel to this panel
        this.add(chartPanel);
    }
    
    private DefaultCategoryDataset createDatasetFromDatabase() {
        // Create dataset for chart
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Inisialisasi map untuk menyimpan data per bulan
        Map<Integer, Double> pembelianMap = new HashMap<>();
        Map<Integer, Double> penjualanMap = new HashMap<>();
        Map<Integer, Double> labaMap = new HashMap<>();
        
        try {
            // Dapatkan koneksi dari kelas conn
            Connection connection = conn.getConnection();
            
            // Query untuk mendapatkan total transaksi beli per bulan dari detail_transaksibeli
            String queryBeli = "SELECT " +
                "MONTH(j.tanggal_transaksi) AS bulan, " +
                "SUM(d.total_harga) AS total_beli " +
                "FROM transaksi_beli j " +
                "JOIN detail_transaksibeli d ON j.id_transaksibeli = d.id_transaksibeli " +
                "GROUP BY bulan " +
                "ORDER BY bulan";
            
            // Query untuk mendapatkan total transaksi jual per bulan dari detail_transaksijual
            String queryJual = "SELECT " +
                "MONTH(j.tanggal_transaksi) AS bulan, " +
                "SUM(d.total_harga) AS total_jual " +
                "FROM transaksi_jual j " +
                "JOIN detail_transaksijual d ON j.id_transaksijual = d.id_transaksijual " +
                "GROUP BY bulan " +
                "ORDER BY bulan";
            
            // Query untuk mendapatkan laba per bulan
            String queryLaba = "SELECT " +
                "MONTH(jual.tanggal_transaksi) AS bulan, " +
                "SUM(djual.total_harga - dbeli.total_harga) AS total_laba " +
                "FROM transaksi_jual jual " +
                "JOIN detail_transaksijual djual ON jual.id_transaksijual = djual.id_transaksijual " +
                "JOIN transaksi_beli beli ON MONTH(jual.tanggal_transaksi) = MONTH(beli.tanggal_transaksi) " +
                "JOIN detail_transaksibeli dbeli ON beli.id_transaksibeli = dbeli.id_transaksibeli " +
                "GROUP BY bulan " +
                "ORDER BY bulan";
            
            // Eksekusi query untuk transaksi beli
            Statement stmtBeli = connection.createStatement();
            ResultSet rsBeli = stmtBeli.executeQuery(queryBeli);
            
            // Tambahkan data beli ke map
            while (rsBeli.next()) {
                int bulan = rsBeli.getInt("bulan");
                double totalBeli = rsBeli.getDouble("total_beli");
                pembelianMap.put(bulan, totalBeli);
            }
            
            // Eksekusi query untuk transaksi jual
            Statement stmtJual = connection.createStatement();
            ResultSet rsJual = stmtJual.executeQuery(queryJual);
            
            // Tambahkan data jual ke map
            while (rsJual.next()) {
                int bulan = rsJual.getInt("bulan");
                double totalJual = rsJual.getDouble("total_jual");
                penjualanMap.put(bulan, totalJual);
            }
            
            // Eksekusi query untuk laba
            Statement stmtLaba = connection.createStatement();
            ResultSet rsLaba = stmtLaba.executeQuery(queryLaba);
            
            // Tambahkan data laba ke map
            while (rsLaba.next()) {
                int bulan = rsLaba.getInt("bulan");
                double totalLaba = rsLaba.getDouble("total_laba");
                labaMap.put(bulan, totalLaba);
            }
            
            // Tutup koneksi
            rsBeli.close();
            stmtBeli.close();
            rsJual.close();
            stmtJual.close();
            rsLaba.close();
            stmtLaba.close();
            
            // Tambahkan data ke dataset untuk setiap bulan
            for (int bulan = 1; bulan <= 12; bulan++) {
                // Tambahkan Pemasukan (Pembelian)
                dataset.addValue(
                    pembelianMap.getOrDefault(bulan, 0.0), 
                    "Pemasukan", 
                    getNamaBulan(bulan)
                );
                
                // Tambahkan Pengeluaran (Penjualan)
                dataset.addValue(
                    penjualanMap.getOrDefault(bulan, 0.0), 
                    "Pengeluaran", 
                    getNamaBulan(bulan)
                );
                
                // Tambahkan Laba
                dataset.addValue(
                    labaMap.getOrDefault(bulan, 0.0), 
                    "Laba", 
                    getNamaBulan(bulan)
                );
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return dataset;
    }
    
    // Metode untuk mengubah nomor bulan menjadi nama bulan
    private String getNamaBulan(int bulan) {
        String[] namaBulan = {
            "Januari", "Februari", "Maret", "April", 
            "Mei", "Juni", "Juli", "Agustus", 
            "September", "Oktober", "November", "Desember"
        };
        return namaBulan[bulan - 1];
    }
    
    private JFreeChart createChart(DefaultCategoryDataset dataset) {
        // Create chart (use false for shadow to get flat 2D look)
        JFreeChart chart = ChartFactory.createBarChart(
                "Diagram Laporan",      // chart title
                "Bulan",                // x-axis label
                "Total Transaksi",       // y-axis label
                dataset,                // data
                PlotOrientation.VERTICAL, // orientation
                true,                   // show legend
                true,                   // tooltips
                false                   // URLs
        );
        
        // Customize chart appearance
        chart.setBackgroundPaint(Color.white);
        chart.getLegend().setFrame(BlockBorder.NONE);
        
        // Get reference to plot
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        
        // Customize plot background - use lighter colors
        plot.setBackgroundPaint(Color.white);
        plot.setDomainGridlinePaint(new Color(240, 240, 240));
        plot.setRangeGridlinePaint(new Color(240, 240, 240));
        plot.setOutlineVisible(false); // Remove outline
        
        // Customize renderer - change to flat 2D look
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        renderer.setShadowVisible(false); // Remove shadow
        
        // Use StandardBarPainter to remove gradient effect (for flat 2D look)
        renderer.setBarPainter(new StandardBarPainter());
        
        // Customize bar colors
        renderer.setSeriesPaint(0, new Color(147, 112, 219)); // Purple for Income
        renderer.setSeriesPaint(1, new Color(255, 160, 122)); // Salmon for Expenses
        renderer.setSeriesPaint(2, new Color(32, 178, 170));  // Teal for Profit
        
        // Adjust bar width
        renderer.setItemMargin(0.05); // Adjust spacing between bars in same category
        
        // Customize domain axis
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryMargin(0.2); // Adjust spacing between month categories
        domainAxis.setLowerMargin(0.02);
        domainAxis.setUpperMargin(0.02);
        domainAxis.setCategoryLabelPositions(
                org.jfree.chart.axis.CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0)
        );
        
        // Customize range axis
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setUpperMargin(0.10);  // To give some space at the top
        rangeAxis.setLowerBound(0); // Start from 0
        
        // Customize fonts
        Font titleFont = new Font("Dialog", Font.BOLD, 18);
        chart.getTitle().setFont(titleFont);
        
        Font labelFont = new Font("Dialog", Font.PLAIN, 12);
        domainAxis.setLabelFont(labelFont);
        rangeAxis.setLabelFont(labelFont);
        
        return chart;
    }
    
    // Method to update data if needed
    public void updateData() {
        DefaultCategoryDataset newDataset = createDatasetFromDatabase();
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setDataset(newDataset);
        chartPanel.repaint();
    }
}