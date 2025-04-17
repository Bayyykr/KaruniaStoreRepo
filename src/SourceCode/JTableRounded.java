package SourceCode;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.ListSelectionModel;

/**
 * A custom JPanel containing a rounded JTable with a custom look and feel. The
 * table has rounded corners at the top and bottom.
 */
public class JTableRounded extends JPanel {

    private final int CORNER_RADIUS = 5;
    private JTable table;
    private JScrollPane scrollPane;
    private DefaultTableModel tableModel;

    /**
     * Constructor for RoundedTable
     *
     * @param columnLabels Array of column labels for the table
     */
    public JTableRounded(String[] columnLabels) {
        initComponents(columnLabels);
    }

    /**
     * Constructor for RoundedTable with custom width and height
     *
     * @param columnLabels Array of column labels for the table
     * @param width Width of the table panel
     * @param height Height of the table panel
     */
    public JTableRounded(String[] columnLabels, int width, int height) {
        initComponents(columnLabels);
        setSize(width, height);
    }

    /**
     * Initializes the table components
     *
     * @param columnLabels Array of column labels for the table
     */
    private void initComponents(String[] columnLabels) {
        setLayout(null);
        setBackground(new Color(240, 240, 240));

        // Create empty model with the provided column labels
        tableModel = new DefaultTableModel(null, columnLabels);

        // Create table with custom rendering
        table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Menonaktifkan edit pada kolom ke-6 (indeks 5)
                if (column == 6) {
                    return false;
                }
                // Allow editing for button columns (assuming edit and delete are the last columns)
                return column >= getColumnCount() - 1;
            }

            // Override to create a custom header
            @Override
            protected JTableHeader createDefaultTableHeader() {
                return new JTableHeader(columnModel) {
                    @Override
                    public void paintComponent(Graphics g) {
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        // Create a path with rounded top corners only
                        Path2D path = new Path2D.Double();
                        path.moveTo(0, getHeight());
                        path.lineTo(0, CORNER_RADIUS);
                        path.quadTo(0, 0, CORNER_RADIUS, 0);
                        path.lineTo(getWidth() - CORNER_RADIUS, 0);
                        path.quadTo(getWidth(), 0, getWidth(), CORNER_RADIUS);
                        path.lineTo(getWidth(), getHeight());
                        path.closePath();

                        // Fill the background with the path
                        g2d.setColor(new Color(185, 185, 185));
                        g2d.fill(path);

                        // Paint each column header
                        int x = 0;
                        for (int i = 0; i < getColumnModel().getColumnCount(); i++) {
                            int w = getColumnModel().getColumn(i).getWidth();
                            String title = getColumnModel().getColumn(i).getHeaderValue().toString();

                            // Draw text
                            g2d.setColor(Color.WHITE);
                            g2d.setFont(getFont().deriveFont(java.awt.Font.BOLD));

                            // Center the text in the cell
                            java.awt.FontMetrics fm = g2d.getFontMetrics();
                            int textWidth = fm.stringWidth(title);
                            int textHeight = fm.getHeight();

                            g2d.drawString(title, x + (w - textWidth) / 2,
                                    (getHeight() - textHeight) / 2 + fm.getAscent());

                            x += w;
                        }

                        g2d.dispose();
                    }
                    
                    // Override untuk menonaktifkan pemindahan kolom
                    @Override
                    public void setDraggedColumn(TableColumn column) {
                        // Nonaktifkan drag column dengan mengembalikan null
                        super.setDraggedColumn(null);
                    }
                    
                    // Override untuk menonaktifkan pemindahan distance
                    @Override 
                    public void setDraggedDistance(int distance) {
                        // Tetapkan distance ke 0 untuk mencegah geseran
                        super.setDraggedDistance(0);
                    }
                };
            }
            
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                
                // Ensure the component is not transparent to capture mouse events
                if (comp instanceof JComponent) {
                    ((JComponent) comp).setOpaque(true);
                }
                
                return comp;
            }
            
            // Override untuk mencegah pemindahan kolom
            @Override
            public void moveColumn(int column, int targetColumn) {
                // Tidak melakukan apa-apa untuk mencegah perpindahan kolom
                return;
            }
        };

        // Allow row selection for better user interaction
        table.setRowSelectionAllowed(true);
        table.setCellSelectionEnabled(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Customize table appearance
        table.setRowHeight(30);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(220, 220, 220));
        table.setFocusable(true); // Enable focus on the table for better interaction
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); // Ensure columns use all available width

        // Customize header
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
        header.setReorderingAllowed(false); // Disable column reordering completely
        header.setResizingAllowed(false); // Disable column resizing completely

        // Untuk memastikan kolom tidak bisa dipindahkan
        table.getTableHeader().setUpdateTableInRealTime(false);
        
        // Custom renderer for cells
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                // Pass the real selection status for better interaction
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Set padding for cell content
                ((JComponent) comp).setBorder(new EmptyBorder(0, 10, 0, 10));

                return comp;
            }
        });

        // Calculate default sizes (can be overridden with setSize method)
        int width = 600;
        int height = 300;

        // Set the panel size
        setSize(width, height);

        // Create scroll pane with scrollbars for better access
        scrollPane = new JScrollPane(table);
        scrollPane.setBounds(0, 0, width, height - 40);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setViewportBorder(null);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED); // Enable vertical scrollbar
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED); // Enable horizontal scrollbar

        // Nonaktifkan geseran pada scrollPane
        scrollPane.getViewport().setEnabled(false);
        
        // Add a custom panel at the bottom of the table for the rounded bottom
        JPanel bottomPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Get background color from the last row
                Color backgroundColor = (table.getRowCount() % 2 == 0)
                        ? new Color(242, 242, 242) : Color.WHITE;

                // Create a path with rounded bottom corners
                Path2D path = new Path2D.Double();
                path.moveTo(0, 0);
                path.lineTo(getWidth(), 0);
                path.lineTo(getWidth(), getHeight() - CORNER_RADIUS);
                path.quadTo(getWidth(), getHeight(), getWidth() - CORNER_RADIUS, getHeight());
                path.lineTo(CORNER_RADIUS, getHeight());
                path.quadTo(0, getHeight(), 0, getHeight() - CORNER_RADIUS);
                path.closePath();

                // Fill the background
                g2d.setColor(backgroundColor);
                g2d.fill(path);

                g2d.dispose();
            }
        };

        bottomPanel.setBounds(0, height - 40, width, 40);
        bottomPanel.setOpaque(false);

        // Add the components to the panel
        add(scrollPane);
        add(bottomPanel);
    }

    /**
     * Add a row to the table
     *
     * @param rowData Array of data for the row
     */
    public void addRow(Object[] rowData) {
        tableModel.addRow(rowData);
    }

    /**
     * Clear all rows from the table
     */
    public void clearTable() {
        tableModel.setRowCount(0);
    }

    /**
     * Get the underlying JTable
     *
     * @return The JTable instance
     */
    public JTable getTable() {
        return table;
    }
    
    /**
     * Set a custom cell editor for a specific column
     * 
     * @param column Column index
     * @param editor Custom cell editor
     */
    public void setColumnEditor(int column, javax.swing.table.TableCellEditor editor) {
        if (column >= 0 && column < table.getColumnCount()) {
            TableColumn tableColumn = table.getColumnModel().getColumn(column);
            tableColumn.setCellEditor(editor);
        }
    }
    
    /**
     * Set a custom cell renderer for a specific column
     * 
     * @param column Column index
     * @param renderer Custom cell renderer
     */
    public void setColumnRenderer(int column, javax.swing.table.TableCellRenderer renderer) {
        if (column >= 0 && column < table.getColumnCount()) {
            TableColumn tableColumn = table.getColumnModel().getColumn(column);
            tableColumn.setCellRenderer(renderer);
        }
    }
    
    /**
     * Mengatur lebar kolom tabel
     * 
     * @param column Indeks kolom
     * @param width Lebar yang diinginkan
     */
    public void setColumnWidth(int column, int width) {
        if (column >= 0 && column < table.getColumnCount()) {
            TableColumn tableColumn = table.getColumnModel().getColumn(column);
            tableColumn.setPreferredWidth(width);
            tableColumn.setWidth(width);
            
            // Memastikan lebar kolom tidak berubah
            tableColumn.setMaxWidth(width);
            tableColumn.setMinWidth(width);
        }
    }
    
    /**
     * Mengatur apakah kolom bisa diubah ukurannya
     * 
     * @param resizable true jika kolom dapat diubah ukurannya, false jika tidak
     */
    public void setColumnsResizable(boolean resizable) {
        table.getTableHeader().setResizingAllowed(resizable);
    }
    
    /**
     * Mengatur auto resize mode untuk tabel
     * 
     * @param mode Mode auto resize (JTable.AUTO_RESIZE_*)
     */
    public void setTableAutoResizeMode(int mode) {
        table.setAutoResizeMode(mode);
    }
    
    /**
     * Mengunci posisi tabel agar tidak bisa digeser/dipindahkan
     */
    public void lockTablePosition() {
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setUpdateTableInRealTime(false);
        
        // Nonaktifkan scrolling jika diperlukan
        scrollPane.setWheelScrollingEnabled(false);
    }

    /**
     * Set the size of the rounded table component
     *
     * @param width Width of the component
     * @param height Height of the component
     */
    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);

        // Update the sizes of child components
        if (scrollPane != null) {
            scrollPane.setBounds(0, 0, width, height - 40);
        }

        // Update the bottom panel if it exists
        if (getComponentCount() > 1) {
            Component bottomPanel = getComponent(1);
            bottomPanel.setBounds(0, height - 40, width, 40);
        }
    }

    /**
     * Set the size of the rounded table component
     *
     * @param dimension The dimension object containing width and height
     */
    @Override
    public void setSize(Dimension dimension) {
        setSize(dimension.width, dimension.height);
    }
}