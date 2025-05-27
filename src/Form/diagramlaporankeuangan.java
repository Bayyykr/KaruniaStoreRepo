package Form;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
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
import db.conn;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author HP
 */
public class diagramlaporankeuangan extends JPanel {

    private JFreeChart chart;
    private RoundedChartPanel chartPanel;
    private Connection con;

    public diagramlaporankeuangan() {
        // Initialize panel
        con = conn.getConnection();
        this.setBackground(Color.white);
        this.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        this.setPreferredSize(new Dimension(800, 500));

        // Create dataset
        DefaultCategoryDataset dataset = createDataset();

        // Create chart
        chart = createChart(dataset);

        // Create rounded panel for chart
        chartPanel = new RoundedChartPanel(chart, 20); // 20px rounded corners
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel.setBackground(Color.white);
        chartPanel.setPreferredSize(new Dimension(550, 350));

        // Add chart panel to this panel
        this.add(chartPanel);
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
                    0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);

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

    private DefaultCategoryDataset createDataset() {
        // Create dataset for chart
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Map<String, Double> pemasukanMap = new HashMap<>();
        Map<String, Double> pengeluaranMap = new HashMap<>();

        dataset.addValue(0, "Pemasukan", "Senin");
        dataset.addValue(0, "Pemasukan", "Selasa");
        dataset.addValue(0, "Pemasukan", "Rabu");
        dataset.addValue(0, "Pemasukan", "Kamis");
        dataset.addValue(0, "Pemasukan", "Jum'at");
        dataset.addValue(0, "Pemasukan", "Sabtu");
        dataset.addValue(0, "Pemasukan", "Minggu");

        try {
            String sql = "SELECT DAYNAME(tanggal_transaksi) AS hari, SUM(total_harga) AS total "
                    + "FROM transaksi_jual tj "
                    + "JOIN detail_transaksijual dt ON tj.id_transaksijual = dt.id_transaksijual "
                    + "GROUP BY DAYNAME(tanggal_transaksi)";
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String hari = rs.getString("hari").toLowerCase();
                double total = rs.getDouble("total");
                pemasukanMap.put(hari, total);
                dataset.addValue(total, "Pemasukan", ubahHariKeIndo(hari));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        dataset.addValue(0, "Pengeluaran", "Senin");
        dataset.addValue(0, "Pengeluaran", "Selasa");
        dataset.addValue(0, "Pengeluaran", "Rabu");
        dataset.addValue(0, "Pengeluaran", "Kamis");
        dataset.addValue(0, "Pengeluaran", "Jum'at");
        dataset.addValue(0, "Pengeluaran", "Sabtu");
        dataset.addValue(0, "Pengeluaran", "Minggu");

        try {
            String sql = "SELECT DAYNAME(tanggal_transaksi) AS hari, SUM(total_harga) AS total "
                    + "FROM transaksi_beli tb "
                    + "JOIN detail_transaksibeli dtb ON tb.id_transaksibeli = dtb.id_transaksibeli "
                    + "GROUP BY DAYNAME(tanggal_transaksi)";
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String hari = rs.getString("hari").toLowerCase();
                double total = rs.getDouble("total");
                pengeluaranMap.put(hari, total);
                dataset.addValue(total, "Pengeluaran", ubahHariKeIndo(hari));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        dataset.addValue(0, "Laba", "Senin");
        dataset.addValue(0, "Laba", "Selasa");
        dataset.addValue(0, "Laba", "Rabu");
        dataset.addValue(0, "Laba", "Kamis");
        dataset.addValue(0, "Laba", "Jum'at");
        dataset.addValue(0, "Laba", "Sabtu");
        dataset.addValue(0, "Laba", "Minggu");

        String[] semuaHari = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};

        for (String hari : semuaHari) {
            double pemasukan = pemasukanMap.getOrDefault(hari, 0.0);
            double pengeluaran = pengeluaranMap.getOrDefault(hari, 0.0);
            double laba = pemasukan - pengeluaran;
            dataset.addValue(laba, "Laba", ubahHariKeIndo(hari));
        }

        return dataset;
    }

    private String ubahHariKeIndo(String hariInggris) {
        return switch (hariInggris.toLowerCase()) {
            case "monday" ->
                "Senin";
            case "tuesday" ->
                "Selasa";
            case "wednesday" ->
                "Rabu";
            case "thursday" ->
                "Kamis";
            case "friday" ->
                "Jum'at";
            case "saturday" ->
                "Sabtu";
            case "sunday" ->
                "Minggu";
            default ->
                hariInggris;
        };
    }

    private JFreeChart createChart(DefaultCategoryDataset dataset) {
        // Create chart (use false for shadow to get flat 2D look)
        JFreeChart chart = ChartFactory.createBarChart(
                "", // chart title
                "", // x-axis label
                "Nilai", // y-axis label
                dataset, // data
                PlotOrientation.VERTICAL, // orientation
                true, // show legend
                true, // tooltips
                false // URLs
        );

        // Customize chart appearance
        chart.setBackgroundPaint(Color.white);

        // Hilangkan border pada legend
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
    public void updateData(DefaultCategoryDataset newDataset) {
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setDataset(newDataset);
        chartPanel.repaint();
    }
}