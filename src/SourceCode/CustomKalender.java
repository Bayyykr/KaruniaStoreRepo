package calendar;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.text.SimpleDateFormat;

public class CustomKalender extends JPanel {

    private JLabel monthLabel;
    private JPanel calendarPanel;
    private JButton prevButton, nextButton;
    private Calendar currentCalendar;
    private JLabel[] dayLabels = new JLabel[42]; // 6 rows, 7 columns
    private JPanel[] dayPanels = new JPanel[42]; // Menyimpan panel untuk setiap tanggal
    private JLabel selectedDateLabel;
    private Color selectedDateColor = new Color(0, 123, 255);
    private Color todayColor = new Color(255, 193, 7);
    private Color headerBackground = new Color(20, 20, 20);
    private Color panelBackground = new Color(20, 20, 20);
    private Color weekdayColor = new Color(150, 150, 150);
    private Color weekendColor = new Color(64, 156, 255);
    private Color normalDayColor = Color.WHITE;
    private Color otherMonthColor = new Color(80, 80, 80);
    private Color navButtonHoverColor = new Color(0, 123, 255); // Warna saat tombol navigasi di-hover/klik
    private Color weekNumberBackground = new Color(50, 50, 50); // Warna background untuk nomor minggu

    private Date selectedDate;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public CustomKalender() {
        initComponents();
        updateCalendar();
        // Pastikan repaint dipanggil untuk menerapkan sudut lengkung
        repaint();
    }

    private void initComponents() {
        // PENTING: Pastikan layout dan border diatur dengan benar
        setLayout(new BorderLayout());
        // Hapus border default yang mungkin menimpa efek sudut lengkung
        setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        setBackground(panelBackground);

        // SANGAT PENTING: Atur opaque menjadi false agar sudut lengkung terlihat
        setOpaque(false);

        // Header Panel with month and navigation buttons
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            // Tambahkan sudut lengkung ke header panel (hanya bagian atas)
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(headerBackground);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight() + 20, 20, 20);
                g2d.dispose();
            }
        };
        headerPanel.setOpaque(false); // Penting untuk sudut lengkung
        headerPanel.setBackground(headerBackground);
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        monthLabel = new JLabel("", JLabel.LEFT);
        monthLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        monthLabel.setForeground(Color.WHITE);
        monthLabel.setBorder(new EmptyBorder(0, 10, 0, 0));

        // Tombol navigasi dengan simbol panah
        prevButton = createNavigationButton("◀");
        nextButton = createNavigationButton("▶");

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        buttonPanel.setOpaque(false); // Penting agar latar belakang transparan
        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);

        headerPanel.add(monthLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        // Days of week header with month numbers
        JPanel daysHeader = new JPanel(new GridLayout(1, 8)) {
            // Biarkan transparansi untuk bagian tengah
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(panelBackground);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        daysHeader.setOpaque(false);
        daysHeader.setBackground(panelBackground);
        daysHeader.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Month number label (01-12)
        JLabel monthNumberLabel = new JLabel("", JLabel.CENTER);
        monthNumberLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        monthNumberLabel.setForeground(Color.GRAY);
        daysHeader.add(monthNumberLabel);

        String[] daysOfWeek = {"Mon", "Tus", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int i = 0; i < daysOfWeek.length; i++) {
            JLabel dayLabel = new JLabel(daysOfWeek[i], JLabel.CENTER);
            dayLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

            if (i >= 5) { // Weekend
                dayLabel.setForeground(weekendColor);
            } else { // Weekday
                dayLabel.setForeground(weekdayColor);
            }

            daysHeader.add(dayLabel);
        }

        // Calendar grid - memperkecil kolom nomor baris dan menambahkan jarak
        calendarPanel = new JPanel(new GridBagLayout()) {
            // Tambahkan sudut lengkung ke panel kalender (hanya bagian bawah)
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(panelBackground);
                g2d.fillRoundRect(0, -20, getWidth(), getHeight() + 20, 20, 20);
                g2d.dispose();
            }
        };
        calendarPanel.setOpaque(false); // Penting untuk sudut lengkung
        calendarPanel.setBackground(panelBackground);
        calendarPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        
        // Tambahkan padding untuk semua sel di grid
        gbc.insets = new Insets(1, 1, 1, 1); // Jarak atas, kiri, bawah, kanan

        for (int row = 0; row < 6; row++) {
            // Nomor minggu (otomatis) dengan sudut lengkung - dengan jarak yang lebih baik
            JPanel weekNumberPanel = new JPanel(new BorderLayout());
            weekNumberPanel.setOpaque(false);
            weekNumberPanel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5)); // Menambahkan jarak internal
            
            JLabel rowLabel = createRoundedRowLabel("");  // Kosong dulu, akan diisi pada updateCalendar()
            weekNumberPanel.add(rowLabel, BorderLayout.CENTER);
            
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0.25; // Memperkecil kolom nomor baris
            calendarPanel.add(weekNumberPanel, gbc);

            for (int col = 0; col < 7; col++) {
                final int index = row * 7 + col;

                JPanel dayPanel = new JPanel(new BorderLayout());
                dayPanel.setOpaque(false);
                dayPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

                // Panel dalam dengan sudut lengkung untuk semua tanggal
                JPanel innerPanel = new JPanel(new BorderLayout()) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        
                        // Menggunakan warna background panel
                        g2d.setColor(getBackground());
                        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);  // Sudut lengkung 10px
                        
                        g2d.dispose();
                    }
                };
                innerPanel.setOpaque(false);  // Penting untuk sudut lengkung
                innerPanel.setBackground(panelBackground);
                innerPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                innerPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1)); // Tambahkan ini untuk mengurangi padding internal
                dayPanels[index] = innerPanel;  // Simpan referensi panel

                dayLabels[index] = new JLabel("", JLabel.CENTER);
                dayLabels[index].setFont(new Font("SansSerif", Font.PLAIN, 11));
                dayLabels[index].setForeground(normalDayColor);
                dayLabels[index].setOpaque(false);  // Penting agar background panel terlihat

                innerPanel.add(dayLabels[index], BorderLayout.CENTER);
                dayPanel.add(innerPanel, BorderLayout.CENTER);

                // Adding click listener dengan perbaikan untuk hover pada tanggal hari ini
                final int dayIndex = index;
                innerPanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (!dayLabels[dayIndex].getText().isEmpty()) {
                            selectDate(dayIndex);
                        }
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (!dayLabels[dayIndex].getText().isEmpty() && dayLabels[dayIndex] != selectedDateLabel) {
                            // Simpan warna asli terlebih dahulu
                            Color originalColor = innerPanel.getBackground();
                            
                            // Hanya ubah warna jika bukan tanggal hari ini (dicek berdasarkan warna background)
                            if (!originalColor.equals(todayColor)) {
                                innerPanel.setBackground(new Color(50, 50, 50));
                                repaint();  // Penting untuk update tampilan
                            }
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        if (dayLabels[dayIndex] != selectedDateLabel) {
                            // Tentukan warna yang tepat untuk dikembalikan
                            Calendar today = Calendar.getInstance();
                            int firstDayOfMonth = getFirstDayOfMonth();
                            int todayIndex = firstDayOfMonth + today.get(Calendar.DAY_OF_MONTH) - 1;
                            
                            // Jika ini adalah hari ini, kembalikan ke warna todayColor
                            if (dayIndex == todayIndex && 
                                today.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
                                today.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)) {
                                innerPanel.setBackground(todayColor);
                            } else {
                                innerPanel.setBackground(panelBackground);
                            }
                            repaint();  // Penting untuk update tampilan
                        }
                    }
                });

                gbc.gridx = col + 1;
                gbc.gridy = row;
                gbc.weightx = 1.0; // Normal weight for date cells
                calendarPanel.add(dayPanel, gbc);
            }
        }

        // Add event listeners for navigation buttons with color change effect
        prevButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Ubah warna saat ditekan
                prevButton.setBackground(navButtonHoverColor);
                prevButton.repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                // Kembalikan warna setelah ditekan
                prevButton.setBackground(headerBackground);
                prevButton.repaint();
                
                // Pindah ke bulan sebelumnya
                currentCalendar.add(Calendar.MONTH, -1);
                updateCalendar();
            }
        });

        nextButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Ubah warna saat ditekan
                nextButton.setBackground(navButtonHoverColor);
                nextButton.repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                // Kembalikan warna setelah ditekan
                nextButton.setBackground(headerBackground);
                nextButton.repaint();
                
                // Pindah ke bulan berikutnya
                currentCalendar.add(Calendar.MONTH, 1);
                updateCalendar();
            }
        });

        // Initialize calendar with current date
        currentCalendar = Calendar.getInstance();

        // Container untuk seluruh kalender dengan sudut lengkung 20px
        JPanel mainContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(panelBackground);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.dispose();
            }
        };
        mainContainer.setOpaque(false);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

        // Add components to main container
        mainContainer.add(headerPanel, BorderLayout.NORTH);
        mainContainer.add(daysHeader, BorderLayout.CENTER);
        mainContainer.add(calendarPanel, BorderLayout.SOUTH);

        // Add main container to this panel
        add(mainContainer, BorderLayout.CENTER);
    }

    // Metode helper baru untuk mendapatkan first day of month
    private int getFirstDayOfMonth() {
        Calendar calendar = (Calendar) currentCalendar.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        
        int firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK) - 2;
        if (firstDayOfMonth < 0) {
            firstDayOfMonth += 7; // Adjust for Monday as first day
        }
        
        return firstDayOfMonth;
    }

    // KUNCI: Pastikan panel kalender utama juga memiliki sudut lengkung
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(panelBackground);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        g2d.dispose();
        // JANGAN panggil super.paintComponent() - kita gambar manual
    }

    // Membuat tombol navigasi yang lebih terlihat
    private JButton createNavigationButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(Color.WHITE);
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

                // Menggambar teks panah dengan lebih jelas
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();
                g2d.setColor(Color.WHITE);
                g2d.drawString(text, (getWidth() - textWidth) / 2, (getHeight() - textHeight) / 2 + fm.getAscent());

                g2d.dispose();
            }
        };

        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setBackground(headerBackground);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(30, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    // Helper method to create rounded row label - dengan ukuran yang lebih kecil
    private JLabel createRoundedRowLabel(String text) {
        JLabel label = new JLabel(text, JLabel.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(weekNumberBackground);
                
                // Menggunakan margin untuk membuat space di dalam kotak nomor minggu
                int margin = 0;
                g2d.fillRoundRect(margin, margin, getWidth() - 2*margin, getHeight() - 2*margin, 10, 10);
                
                super.paintComponent(g2d);
                g2d.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                // Membuat label row number lebih kecil
                return new Dimension(20, super.getPreferredSize().height);
            }

            @Override
            public Dimension getMinimumSize() {
                return new Dimension(20, super.getMinimumSize().height);
            }

            @Override
            public Dimension getMaximumSize() {
                return new Dimension(20, super.getMaximumSize().height);
            }
        };

        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(Color.GRAY);
        label.setOpaque(false);
        
        // Tambahkan margin di sekitar teks
        label.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        
        return label;
    }

    private void updateCalendar() {
        // Update month/year label
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy");
        monthLabel.setText(monthFormat.format(currentCalendar.getTime()));

        // Update month number (01-12)
        SimpleDateFormat monthNumberFormat = new SimpleDateFormat("MM");

        // Perbaikan: Cari label bulan dengan cara yang lebih aman
        // Pastikan dulu kita memiliki struktur komponen yang diharapkan
        if (getComponentCount() > 0) {
            Component mainContainer = getComponent(0);
            if (mainContainer instanceof Container) {
                Component daysHeaderComponent = ((Container) mainContainer).getComponent(1);
                if (daysHeaderComponent instanceof Container) {
                    Component[] headerComponents = ((Container) daysHeaderComponent).getComponents();
                    if (headerComponents.length > 0 && headerComponents[0] instanceof JLabel) {
                        ((JLabel) headerComponents[0]).setText(monthNumberFormat.format(currentCalendar.getTime()));
                    }
                }
            }
        }

        // Clear previous state
        for (int i = 0; i < dayLabels.length; i++) {
            dayLabels[i].setText("");
            dayLabels[i].setForeground(normalDayColor);
            if (dayPanels[i] != null) {
                dayPanels[i].setBackground(panelBackground);
            }
        }

        // Get current month's first day and number of days
        Calendar calendar = (Calendar) currentCalendar.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK) - 2;
        if (firstDayOfMonth < 0) {
            firstDayOfMonth += 7; // Adjust for Monday as first day
        }
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Update week numbers (otomatis)
        // Ambil referensi ke panel kalender
        if (getComponentCount() > 0) {
            Component mainContainer = getComponent(0);
            if (mainContainer instanceof Container) {
                Component calendarComponent = ((Container) mainContainer).getComponent(2);
                if (calendarComponent instanceof Container) {
                    for (int row = 0; row < 6; row++) {
                        // Hitung minggu untuk baris ini
                        Calendar weekCal = (Calendar) calendar.clone();
                        weekCal.set(Calendar.DAY_OF_MONTH, 1); // Mulai dari hari pertama bulan
                        weekCal.add(Calendar.DAY_OF_MONTH, row * 7); // Tambahkan jumlah hari sesuai baris
                        
                        // Dapatkan minggu dalam bulan
                        int weekOfMonth = weekCal.get(Calendar.WEEK_OF_MONTH);
                        
                        // Cari komponen label minggu (sekarang dalam panel container)
                        Component rowComp = ((Container) calendarComponent).getComponent(row * 8);
                        if (rowComp instanceof JPanel && ((JPanel) rowComp).getComponentCount() > 0) {
                            Component label = ((JPanel) rowComp).getComponent(0);
                            if (label instanceof JLabel) {
                                ((JLabel) label).setText(String.valueOf(weekOfMonth));
                            }
                        }
                    }
                }
            }
        }

        // Fill the days of the current month
        for (int i = 0; i < daysInMonth; i++) {
            int day = i + 1;
            int position = firstDayOfMonth + i;

            dayLabels[position].setText(String.valueOf(day));

            // Set weekend color
            int dayOfWeek = (position % 7) + 1;
            if (dayOfWeek == 6 || dayOfWeek == 7) { // Saturday or Sunday
                dayLabels[position].setForeground(weekendColor);
            } else {
                dayLabels[position].setForeground(normalDayColor);
            }
        }

        // Fill days from previous month
        calendar.add(Calendar.MONTH, -1);
        int daysInPrevMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < firstDayOfMonth; i++) {
            int day = daysInPrevMonth - firstDayOfMonth + i + 1;
            dayLabels[i].setText(String.valueOf(day));
            dayLabels[i].setForeground(otherMonthColor);
        }

        // Fill days from next month
        int nextMonthDay = 1;
        for (int i = firstDayOfMonth + daysInMonth; i < dayLabels.length; i++) {
            dayLabels[i].setText(String.valueOf(nextMonthDay++));
            dayLabels[i].setForeground(otherMonthColor);
        }

        // Highlight today's date
        Calendar today = Calendar.getInstance();
        if (today.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR)
                && today.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)) {

            int todayIndex = firstDayOfMonth + today.get(Calendar.DAY_OF_MONTH) - 1;
            highlightDate(todayIndex, todayColor);
        }

        // Restore selected date highlight if in current month view
        if (selectedDate != null) {
            Calendar selectedCal = Calendar.getInstance();
            selectedCal.setTime(selectedDate);

            if (selectedCal.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR)
                    && selectedCal.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)) {

                int selectedIndex = firstDayOfMonth + selectedCal.get(Calendar.DAY_OF_MONTH) - 1;
                highlightDate(selectedIndex, selectedDateColor);
                selectedDateLabel = dayLabels[selectedIndex];
            }
        }

        // Penting: Minta repaint untuk memperbarui tampilan
        repaint();
    }

    private void selectDate(int index) {
        // Remove previous selection
        if (selectedDateLabel != null && selectedDateLabel.getParent() instanceof JPanel) {
            ((JPanel) selectedDateLabel.getParent()).setBackground(panelBackground);
        }

        // Highlight new selection
        highlightDate(index, selectedDateColor);
        selectedDateLabel = dayLabels[index];

        // Store selected date
        Calendar tempCal = (Calendar) currentCalendar.clone();
        tempCal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dayLabels[index].getText()));

        // Adjust month if selecting day from previous/next month
        if (index < 7 && Integer.parseInt(dayLabels[index].getText()) > 20) {
            tempCal.add(Calendar.MONTH, -1);
        } else if (index >= 28 && Integer.parseInt(dayLabels[index].getText()) < 15) {
            tempCal.add(Calendar.MONTH, 1);
        }

        selectedDate = tempCal.getTime();

        // Fire event to notify selection
        firePropertyChange("selectedDate", null, selectedDate);
    }

    private void highlightDate(int index, Color color) {
        if (index >= 0 && index < dayLabels.length) {
            if (dayPanels[index] != null) {
                dayPanels[index].setBackground(color);
                dayLabels[index].setForeground(Color.WHITE);
                repaint();  // Penting untuk memperbarui tampilan
            }
        }
    }

    // Getter for selected date
    public Date getSelectedDate() {
        return selectedDate;
    }

    // Setter for selected date
    public void setSelectedDate(Date date) {
        this.selectedDate = date;

        // Update calendar to show the month containing the selected date
        if (date != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            currentCalendar.set(Calendar.YEAR, cal.get(Calendar.YEAR));
            currentCalendar.set(Calendar.MONTH, cal.get(Calendar.MONTH));

            updateCalendar();
        }
    }

    // Format the selected date as a string
    public String getFormattedDate() {
        return selectedDate != null ? dateFormat.format(selectedDate) : "";
    }

    // Set date format
    public void setDateFormat(String format) {
        this.dateFormat = new SimpleDateFormat(format);
    }

    // Change the theme colors
    public void setThemeColors(Color headerBg, Color panelBg, Color selectedColor, Color todayHighlight) {
        this.headerBackground = headerBg;
        this.panelBackground = panelBg;
        this.selectedDateColor = selectedColor;
        this.todayColor = todayHighlight;

        updateCalendar();
        repaint();
    }
}