package Form;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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

/**
 *
 * @author HP
 */
public class diagramkaryawan extends JPanel {
    
    private JFreeChart chart;
    private RoundedChartPanel chartPanel;
    private Map<String, Color> employeeColors; // Map untuk menyimpan warna per karyawan
    private CustomBarRenderer renderer; // Custom renderer untuk warna berbeda
    
    public diagramkaryawan() {
        // Initialize panel
        this.setBackground(Color.white);
        this.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        this.setPreferredSize(new Dimension(800, 500));
        
        // Inisialisasi map warna karyawan
        employeeColors = new HashMap<>();
        
        // Create dataset
        DefaultCategoryDataset dataset = createDataset();
        
        // Create chart
        chart = createChart(dataset);
        
        // Create rounded panel for chart
        chartPanel = new RoundedChartPanel(chart, 20); // 20px rounded corners
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel.setBackground(Color.white);
        chartPanel.setPreferredSize(new Dimension(550, 320));
        
        // Add chart panel to this panel
        this.add(chartPanel);
        
        // Set auto resize untuk chart panel
        adjustChartSize();
    }
    
    // Custom renderer untuk warna berbeda pada setiap batang
    private class CustomBarRenderer extends BarRenderer {
        private Map<String, Color> colorMap;
        
        public CustomBarRenderer(Map<String, Color> colorMap) {
            this.colorMap = colorMap;
            setShadowVisible(false);
            setDrawBarOutline(false);
            setBarPainter(new StandardBarPainter());
        }
        
        @Override
        public Paint getItemPaint(int row, int column) {
            CategoryPlot plot = (CategoryPlot) chart.getPlot();
            DefaultCategoryDataset dataset = (DefaultCategoryDataset) plot.getDataset();
            String employeeName = (String) dataset.getColumnKey(column);
            
            if (colorMap.containsKey(employeeName)) {
                return colorMap.get(employeeName);
            } else {
                // Generate a new color if not in map
                Color newColor = generateRandomColor();
                colorMap.put(employeeName, newColor);
                return newColor;
            }
        }
    }
    
    // Custom chart panel with rounded corners
    private class RoundedChartPanel extends ChartPanel {
        private int cornerRadius;
        
        public RoundedChartPanel(JFreeChart chart, int cornerRadius) {
            super(chart);
            this.cornerRadius = cornerRadius;
            setOpaque(false);
        }
        
        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Create rounded rectangle for background
            RoundRectangle2D roundedRect = new RoundRectangle2D.Double(
                    0, 0, getWidth()-1, getHeight()-1, cornerRadius, cornerRadius);
            
            // Save original clip
            java.awt.Shape oldClip = g2d.getClip();
            
            // Draw background
            g2d.setColor(getBackground());
            g2d.fill(roundedRect);
            
            // Set clip to rounded rectangle
            g2d.clip(roundedRect);
            
            // Call parent's paintComponent to paint the chart
            super.paintComponent(g2d);
            
            // Restore original clip
            g2d.setClip(oldClip);
            
            // Optional: draw a subtle border
            g2d.setColor(new Color(230, 230, 230));
            g2d.draw(roundedRect);
        }
    }
    
    // Metode untuk menghasilkan warna acak yang tidak terlalu gelap
    private Color generateRandomColor() {
        Random random = new Random();
        int r = random.nextInt(156) + 100; // 100-255
        int g = random.nextInt(156) + 100; // 100-255
        int b = random.nextInt(156) + 100; // 100-255
        return new Color(r, g, b);
    }
    
    // Metode untuk mendapatkan warna untuk karyawan
    private Color getColorForEmployee(String employeeName) {
        // Jika karyawan sudah memiliki warna, gunakan warna yang sama
        if (employeeColors.containsKey(employeeName)) {
            return employeeColors.get(employeeName);
        }
        
        // Jika belum, buat warna baru dan simpan
        Color newColor = generateRandomColor();
        employeeColors.put(employeeName, newColor);
        return newColor;
    }
    
    private DefaultCategoryDataset createDataset() {
        // Create dataset for chart - Employee attendance
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Menambahkan data kehadiran karyawan
        dataset.addValue(20, "Kehadiran", "Habibi Zayne");
        dataset.addValue(21, "Kehadiran", "Greyson");
        dataset.addValue(18, "Kehadiran", "Ahmad");
        dataset.addValue(23, "Kehadiran", "Budi");
        dataset.addValue(16, "Kehadiran", "1");
        dataset.addValue(16, "Kehadiran", "2");
        dataset.addValue(16, "Kehadiran", "4");
        dataset.addValue(16, "Kehadiran", "3");
        dataset.addValue(16, "Kehadiran", "Ci4tra");
        dataset.addValue(16, "Kehadiran", "Ci2tra");
        dataset.addValue(16, "Kehadiran", "Ci1tra");
        
        return dataset;
    }
    
    private JFreeChart createChart(DefaultCategoryDataset dataset) {
        // Create chart (use false for shadow to get flat 2D look)
        JFreeChart chart = ChartFactory.createBarChart(
                "*Kehadiran karyawan ditampilkan berdasarkan bulan dan tahun saja.",  // chart title
                "",                // x-axis label
                "",                // y-axis label (kosong)
                dataset,           // data
                PlotOrientation.VERTICAL, // orientation
                true,             // show legend
                true,             // tooltips
                false             // URLs
        );
        
        // Customize chart appearance
        chart.setBackgroundPaint(Color.white);
        
        // Hilangkan border pada legend
chart.getLegend().setFrame(BlockBorder.NONE);
        
        // Mengatur margin untuk plot
        chart.getTitle().setMargin(10, 0, 10, 0);
        
        // Get reference to plot
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        
        // Customize plot background - use lighter colors
        plot.setBackgroundPaint(Color.white);
        plot.setDomainGridlinePaint(new Color(240, 240, 240));
        plot.setRangeGridlinePaint(new Color(240, 240, 240));
        plot.setOutlineVisible(false); // Remove outline
        
        // Inisialisasi warna untuk setiap karyawan
        int categoryCount = dataset.getColumnCount();
        for (int i = 0; i < categoryCount; i++) {
            String employeeName = (String) dataset.getColumnKey(i);
            getColorForEmployee(employeeName); // Ini akan menyimpan warna ke dalam map
        }
        
        // Set custom renderer yang akan menangani warna berbeda
        renderer = new CustomBarRenderer(employeeColors);
        plot.setRenderer(renderer);
        
        // Adjust bar width
        renderer.setItemMargin(0.4); // Adjust spacing between bars in same category
        
        // Customize domain axis (horizontal)
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryMargin(0.2); // Adjust spacing between categories
        domainAxis.setLowerMargin(0.1);
        domainAxis.setUpperMargin(0.1);
        
        // Customize range axis (vertical)
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setUpperMargin(0.1);  // To give some space at the top
        rangeAxis.setLowerBound(0); // Start from 0
        rangeAxis.setUpperBound(30); // Set maximum to 30
        
        // Customize fonts
        Font titleFont = new Font("Dialog", Font.PLAIN, 12);
        chart.getTitle().setFont(titleFont);
        chart.getTitle().setPaint(Color.RED); // Teks judul berwarna merah
        
        Font labelFont = new Font("Dialog", Font.PLAIN, 12);
        domainAxis.setLabelFont(labelFont);
        rangeAxis.setLabelFont(labelFont);
        
        return chart;
    }
    
    // Metode untuk menyesuaikan ukuran diagram berdasarkan jumlah data
    private void adjustChartSize() {
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        DefaultCategoryDataset dataset = (DefaultCategoryDataset) plot.getDataset();
        int dataCount = dataset.getColumnCount();
        
        // Sesuaikan ukuran diagram berdasarkan jumlah data
        int baseWidth = 550;
        int baseHeight = 320;

        // Sesuaikan juga lebar bar berdasarkan jumlah data
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        double barMargin = Math.max(0.1, 0.4 - (dataCount * 0.05)); // Semakin banyak data, margin semakin kecil
        renderer.setItemMargin(barMargin);
        
        chartPanel.revalidate();
    }
    
    // Method untuk memperbarui dataset dan warna
    public void updateData(DefaultCategoryDataset newDataset) {
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setDataset(newDataset);
        
        // Sesuaikan warna untuk karyawan baru dalam dataset
        int categoryCount = newDataset.getColumnCount();
        for (int i = 0; i < categoryCount; i++) {
            String employeeName = (String) newDataset.getColumnKey(i);
            if (!employeeColors.containsKey(employeeName)) {
                employeeColors.put(employeeName, generateRandomColor());
            }
        }
        
        // Perbarui renderer dengan map warna yang telah diperbarui
        renderer = new CustomBarRenderer(employeeColors);
        plot.setRenderer(renderer);
        
        // Sesuaikan ukuran diagram berdasarkan jumlah data baru
        adjustChartSize();
        
        chartPanel.repaint();
    }
    
    // Method untuk menambahkan karyawan baru dengan data kehadirannya
    public void addEmployee(String name, int attendance) {
        DefaultCategoryDataset dataset = (DefaultCategoryDataset) ((CategoryPlot) chart.getPlot()).getDataset();
        dataset.addValue(attendance, "Kehadiran", name);
        
        // Perbarui warna dan ukuran diagram
        updateData(dataset);
    }
    
    // Method untuk menghapus karyawan dari dataset
    public void removeEmployee(String name) {
        DefaultCategoryDataset dataset = (DefaultCategoryDataset) ((CategoryPlot) chart.getPlot()).getDataset();
        dataset.removeValue("Kehadiran", name);
        employeeColors.remove(name); // Hapus juga warna dari map
        
        // Perbarui warna dan ukuran diagram
        updateData(dataset);
    }
    
    // Method untuk menghapus semua data dan warna
    public void resetData() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        employeeColors.clear();
        updateData(dataset);
    }
}