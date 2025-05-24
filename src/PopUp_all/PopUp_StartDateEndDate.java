package PopUp_all;

import SourceCode.RoundedBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import java.text.SimpleDateFormat;
import javax.swing.Timer;

public class PopUp_StartDateEndDate extends JDialog {

    private JComponent glassPane;
    private JFrame parentFrame;
    private JPanel contentPanel;
    private JButton batalButton, prosesButton;
    private JPanel quickOptionPanel;
    private JPanel calendarContainer;
    private JPanel monthPanel1, monthPanel2;
    private JPanel daysPanel1, daysPanel2;
    private JLabel monthYearLabel1, monthYearLabel2;
    private JButton prevButton, nextButton;
    private JPanel selectionPanel;
    private JLabel rangeLabel;
    private JTextField yearField;

    private final int RADIUS = 20;
    private final int FINAL_WIDTH = 700;
    private final int FINAL_HEIGHT = 500;
    private final int ANIMATION_DURATION = 300;
    private final int ANIMATION_DELAY = 10;
    private float currentScale = 0.01f;
    private boolean animationStarted = false;
    private Timer animationTimer;
    private Timer closeAnimationTimer;

    private static boolean isShowingPopup = false;

    // Date selection
    private Calendar currentCalendar = Calendar.getInstance();
    private Calendar displayedMonth1;
    private Calendar displayedMonth2;
    private Date startDate = null;
    private Date endDate = null;
    private boolean isSelectingStartDate = true;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy");
    private SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy");
    private final String[] MONTH_NAMES = {"January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"};
    private final String[] DAY_NAMES = {"S", "M", "T", "W", "T", "F", "S"};

    // Warna-warna yang digunakan
    private final Color SELECTED_COLOR = new Color(66, 133, 244);
    private final Color SELECTED_TEXT_COLOR = Color.WHITE;
    private final Color HOVER_COLOR = new Color(220, 230, 255);
    private final Color TODAY_COLOR = new Color(245, 245, 245);

    public interface DateRangeCallback {

        void onDateRangeSelected(Date startDate, Date endDate);
    }

    private DateRangeCallback dateRangeCallback;

    public void setDateRangeCallback(DateRangeCallback callback) {
        this.dateRangeCallback = callback;
    }

    // Constructor without parameters
    public PopUp_StartDateEndDate() {
        this(null);
    }

    public PopUp_StartDateEndDate(JFrame parent) {
        super(parent, "Manual", true);
        this.parentFrame = parent;
        setModal(true);
        setPreferredSize(new Dimension(FINAL_WIDTH, FINAL_HEIGHT));

        if (isShowingPopup) {
            dispose();
            return;
        }
        isShowingPopup = true;

        displayedMonth1 = Calendar.getInstance();
        displayedMonth2 = Calendar.getInstance();
        displayedMonth2.add(Calendar.MONTH, 1);

        glassPane = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 180));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        glassPane.setOpaque(false);
        if (parent != null) {
            glassPane.setBounds(0, 0, parent.getWidth(), parent.getHeight());
            parentLayeredPane().add(glassPane, JLayeredPane.POPUP_LAYER);
        }

        setUndecorated(true);
        setSize(FINAL_WIDTH, FINAL_HEIGHT);
        setLocationRelativeTo(parent);
        setBackground(new Color(0, 0, 0, 0));

        contentPanel = new RoundedPanel(RADIUS);
        contentPanel.setLayout(null);
        contentPanel.setBounds(0, 0, FINAL_WIDTH, FINAL_HEIGHT);
        contentPanel.setBackground(Color.WHITE);
        add(contentPanel);

        createComponents();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanupAndClose();
            }
        });

        startScaleAnimation();
    }

    private void createComponents() {
        // Form Title
        JLabel titleLabel = new JLabel("Manual");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(80, 15, FINAL_WIDTH, 30);
        contentPanel.add(titleLabel);

        JLabel titleLabelOpsi = new JLabel("Opsi Cepat");
        titleLabelOpsi.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabelOpsi.setHorizontalAlignment(SwingConstants.LEFT);
        titleLabelOpsi.setBounds(30, 15, FINAL_WIDTH, 30);
        contentPanel.add(titleLabelOpsi);

        // Quick Option Panel (Left side)
        createQuickOptionsPanel();

        // Calendar Container (Right side)
        createCalendarPanel();

        // Divider
        JSeparator separator = new JSeparator(JSeparator.VERTICAL);
        separator.setBounds(160, 50, 1, FINAL_HEIGHT - 130);
        separator.setForeground(Color.GRAY);
        contentPanel.add(separator);

        // Selection Panel (Bottom)
        createSelectionPanel();

        // Buttons
        createButtons();

        // Update calendar display
        updateCalendarDisplay();
    }

    private void createQuickOptionsPanel() {
        quickOptionPanel = new JPanel();
        quickOptionPanel.setLayout(null);
        quickOptionPanel.setBounds(10, 50, 150, FINAL_HEIGHT - 130);
        quickOptionPanel.setBackground(Color.WHITE);

        // Quick options
        String[] options = {"Minggu Ini", "Minggu Lalu", "Bulan Ini", "Bulan Lalu", "Tahun Ini", "Tahun Lalu", "Ketik Tahun"};

        for (int i = 0; i < options.length; i++) {
            final int index = i;
            JPanel optionPanel = new JPanel();
            optionPanel.setLayout(null);
            optionPanel.setBounds(0, i * 40, 150, 30);
            optionPanel.setBackground(Color.WHITE);
            optionPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel optionLabel = new JLabel(options[i]);
            optionLabel.setBounds(10, 0, 140, 30);
            optionLabel.setFont(new Font("Arial", Font.PLAIN, 12));

            optionPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    optionPanel.setBackground(HOVER_COLOR);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    optionPanel.setBackground(Color.WHITE);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    handleQuickOption(index);
                }
            });

            optionPanel.add(optionLabel);
            quickOptionPanel.add(optionPanel);
        }

        // Custom year text field
        yearField = new JTextField();
        yearField.setBounds(10, options.length * 40 + 10, 130, 30);
        yearField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        yearField.addActionListener(e -> handleYearInput());
        quickOptionPanel.add(yearField);

        contentPanel.add(quickOptionPanel);
    }

    private void handleYearInput() {
        String yearInput = yearField.getText().trim();
        String[] years = yearInput.split("-");

        if (years.length == 1) {
            // Handle input satu tahun
            try {
                int year = Integer.parseInt(years[0]);
                if (year >= 1900 && year <= 2100) {
                    Calendar startCal = Calendar.getInstance();
                    Calendar endCal = Calendar.getInstance();

                    // Set untuk tanggal 1 Januari tahun yang dimasukkan
                    startCal.set(Calendar.YEAR, year);
                    startCal.set(Calendar.MONTH, Calendar.JANUARY);
                    startCal.set(Calendar.DAY_OF_MONTH, 1);

                    // Set untuk tanggal 31 Desember tahun yang dimasukkan
                    endCal.set(Calendar.YEAR, year);
                    endCal.set(Calendar.MONTH, Calendar.DECEMBER);
                    endCal.set(Calendar.DAY_OF_MONTH, 31);

                    startDate = startCal.getTime();
                    endDate = endCal.getTime();

                    // Update tampilan kalender ke tahun yang dipilih
                    displayedMonth1.setTime(startDate);
                    displayedMonth2.setTime(startDate);
                    displayedMonth2.add(Calendar.MONTH, 1);

                    updateCalendarDisplay();
                } else {
                    PindahanAntarPopUp.showStartnDateMasukkanrentangtahun(parentFrame);
                }
            } catch (NumberFormatException ex) {
                    PindahanAntarPopUp.showStartnDateMasukkanangkatahunyangvalid(parentFrame);
            }
        } else if (years.length == 2) {
            // Handle input rentang tahun
            try {
                int startYear = Integer.parseInt(years[0]);
                int endYear = Integer.parseInt(years[1]);

                if (startYear >= 1900 && startYear <= 2100 && endYear >= 1900 && endYear <= 2100 && startYear <= endYear) {
                    Calendar startCal = Calendar.getInstance();
                    Calendar endCal = Calendar.getInstance();

                    // Set untuk tanggal 1 Januari tahun awal
                    startCal.set(Calendar.YEAR, startYear);
                    startCal.set(Calendar.MONTH, Calendar.JANUARY);
                    startCal.set(Calendar.DAY_OF_MONTH, 1);

                    // Set untuk tanggal 31 Desember tahun akhir
                    endCal.set(Calendar.YEAR, endYear);
                    endCal.set(Calendar.MONTH, Calendar.DECEMBER);
                    endCal.set(Calendar.DAY_OF_MONTH, 31);

                    startDate = startCal.getTime();
                    endDate = endCal.getTime();

                    // Update tampilan kalender ke tahun awal
                    displayedMonth1.setTime(startDate);
                    displayedMonth2.setTime(startDate);
                    displayedMonth2.add(Calendar.MONTH, 1);

                    updateCalendarDisplay();
                } else {
                    PindahanAntarPopUp.showStartnDateMasukkanrentangtahun(parentFrame);
                }
            } catch (NumberFormatException ex) {
                PindahanAntarPopUp.showStartnDateMasukkanangkatahunyangvalid(parentFrame);
            }
        } else {
            // Handle format input yang tidak valid
            PindahanAntarPopUp.showStartnDateMasukkanangkatahunyangvalid(parentFrame);
        }
    }

    private void createCalendarPanel() {
        calendarContainer = new JPanel();
        calendarContainer.setLayout(null);
        calendarContainer.setBounds(170, 50, FINAL_WIDTH - 180, FINAL_HEIGHT - 130);
        calendarContainer.setBackground(Color.WHITE);
        calendarContainer.setOpaque(false);

        // Create navigation panel
        JPanel navigationPanel = new JPanel(new BorderLayout());
        navigationPanel.setBounds(0, 0, FINAL_WIDTH - 180, 30);
        navigationPanel.setBackground(Color.WHITE);

        // Prev button
        prevButton = new JButton("<");
        prevButton.setFocusPainted(false);
        prevButton.setContentAreaFilled(false);
        prevButton.setBorderPainted(false);
        prevButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        prevButton.addActionListener(e -> {
            displayedMonth1.add(Calendar.MONTH, -1);
            displayedMonth2.add(Calendar.MONTH, -1);
            updateCalendarDisplay();
        });
        navigationPanel.add(prevButton, BorderLayout.WEST);

        // Month names panel
        JPanel monthNamesPanel = new JPanel(new GridLayout(1, 2));
        monthNamesPanel.setBackground(Color.WHITE);

        monthYearLabel1 = new JLabel("January 2025", SwingConstants.CENTER);
        monthYearLabel1.setFont(new Font("Arial", Font.BOLD, 12));
        monthYearLabel2 = new JLabel("February 2025", SwingConstants.CENTER);
        monthYearLabel2.setFont(new Font("Arial", Font.BOLD, 12));

        monthNamesPanel.add(monthYearLabel1);
        monthNamesPanel.add(monthYearLabel2);
        navigationPanel.add(monthNamesPanel, BorderLayout.CENTER);

        // Next button
        nextButton = new JButton(">");
        nextButton.setFocusPainted(false);
        nextButton.setContentAreaFilled(false);
        nextButton.setBorderPainted(false);
        nextButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        nextButton.addActionListener(e -> {
            displayedMonth1.add(Calendar.MONTH, 1);
            displayedMonth2.add(Calendar.MONTH, 1);
            updateCalendarDisplay();
        });
        navigationPanel.add(nextButton, BorderLayout.EAST);

        calendarContainer.add(navigationPanel);

        // Create calendar panels
        monthPanel1 = createMonthPanel();
        monthPanel1.setBounds(0, 35, (FINAL_WIDTH - 180) / 2 - 10, FINAL_HEIGHT - 180);
        calendarContainer.add(monthPanel1);

        monthPanel2 = createMonthPanel();
        monthPanel2.setBounds((FINAL_WIDTH - 180) / 2 + 10, 35, (FINAL_WIDTH - 180) / 2 - 10, FINAL_HEIGHT - 180);
        calendarContainer.add(monthPanel2);

        contentPanel.add(calendarContainer);

    }

    private JPanel createMonthPanel() {
        JPanel monthPanel = new JPanel(new BorderLayout());
        monthPanel.setBackground(Color.WHITE);

        // Days of week header
        JPanel weekDaysPanel = new JPanel(new GridLayout(1, 7));
        weekDaysPanel.setBackground(Color.WHITE);

        for (String day : DAY_NAMES) {
            JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
            dayLabel.setFont(new Font("Arial", Font.BOLD, 12));
            weekDaysPanel.add(dayLabel);
        }

        monthPanel.add(weekDaysPanel, BorderLayout.NORTH);

        JPanel daysGrid = new JPanel(new GridLayout(6, 7));
        daysGrid.setBackground(Color.WHITE);
        monthPanel.add(daysGrid, BorderLayout.CENTER);

        return monthPanel;
    }

    private void updateCalendarDisplay() {
        updateMonth(monthPanel1, displayedMonth1, true);
        updateMonth(monthPanel2, displayedMonth2, false);

        // Update month year labels
        monthYearLabel1.setText(MONTH_NAMES[displayedMonth1.get(Calendar.MONTH)] + " " + displayedMonth1.get(Calendar.YEAR));
        monthYearLabel2.setText(MONTH_NAMES[displayedMonth2.get(Calendar.MONTH)] + " " + displayedMonth2.get(Calendar.YEAR));

        // Update range label
        updateRangeLabel();
    }

    private void updateMonth(JPanel monthPanel, Calendar calendar, boolean isFirstMonth) {
        // Remove previous days grid if exists
        Component[] components = monthPanel.getComponents();
        if (components.length > 1) {
            monthPanel.remove(components[1]);
        }

        // Create new days grid
        JPanel daysGrid = new JPanel(new GridLayout(6, 7));
        daysGrid.setBackground(Color.WHITE);

        Calendar tempCalendar = (Calendar) calendar.clone();
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        int daysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Fill with empty cells before first day
        for (int i = 0; i < firstDayOfWeek; i++) {
            daysGrid.add(createEmptyDayPanel());
        }

        // Add days of the month
        for (int day = 1; day <= daysInMonth; day++) {
            tempCalendar.set(Calendar.DAY_OF_MONTH, day);
            JPanel dayPanel = createDayPanel(day, tempCalendar, isFirstMonth);
            daysGrid.add(dayPanel);
        }

        // Fill remaining cells
        int remainingCells = 42 - (firstDayOfWeek + daysInMonth);
        for (int i = 0; i < remainingCells; i++) {
            daysGrid.add(createEmptyDayPanel());
        }

        monthPanel.add(daysGrid, BorderLayout.CENTER);
        monthPanel.revalidate();
        monthPanel.repaint();
    }

    private JPanel createEmptyDayPanel() {
        JPanel dayPanel = new JPanel();
        dayPanel.setBackground(Color.WHITE);
        return dayPanel;
    }

    private JPanel createDayPanel(int day, Calendar dayCalendar, boolean isFirstMonth) {
        final Calendar tempCalendar = (Calendar) dayCalendar.clone();

        // Use BorderLayout.CENTER to ensure the label is centered
        JPanel dayPanel = new JPanel(new BorderLayout()) {
            // Override paintComponent to create circular background for selected dates
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (getBackground() == SELECTED_COLOR || getBackground() == HOVER_COLOR) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(getBackground());

                    // Create circular shape instead of filling the entire panel
                    int size = Math.min(getWidth(), getHeight()) - 4;
                    int x = (getWidth() - size) / 2;
                    int y = (getHeight() - size) / 2;
                    g2d.fillOval(x, y, size, size);
                    g2d.dispose();
                }
            }
        };

        dayPanel.setOpaque(false); // Make panel transparent to show the circle
        dayPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        JLabel dayLabel = new JLabel(String.valueOf(day), SwingConstants.CENTER);
        dayLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        // Check if this day is today
        Calendar today = Calendar.getInstance();
        boolean isToday = today.get(Calendar.YEAR) == tempCalendar.get(Calendar.YEAR)
                && today.get(Calendar.MONTH) == tempCalendar.get(Calendar.MONTH)
                && today.get(Calendar.DAY_OF_MONTH) == tempCalendar.get(Calendar.DAY_OF_MONTH);

        // Check if this day is selected
        boolean isSelected = false;
        boolean isInRange = false;

        if (startDate != null && endDate != null) {
            Date dayDate = tempCalendar.getTime();
            isSelected = dayDate.equals(startDate) || dayDate.equals(endDate);
            isInRange = dayDate.after(startDate) && dayDate.before(endDate);
        } else if (startDate != null) {
            isSelected = tempCalendar.getTime().equals(startDate);
        }

        // Set appearance based on state
        if (isSelected) {
            dayPanel.setBackground(SELECTED_COLOR);
            dayLabel.setForeground(SELECTED_TEXT_COLOR);
        } else if (isInRange) {
            dayPanel.setBackground(HOVER_COLOR);
            dayLabel.setForeground(Color.BLACK);
        } else if (isToday) {
            dayPanel.setBackground(TODAY_COLOR);
            dayLabel.setForeground(Color.BLACK);
        } else {
            dayPanel.setBackground(Color.WHITE);
            dayLabel.setForeground(Color.BLACK);
        }

        // Make selectable
        dayPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Simpan nilai-nilai boolean untuk digunakan dalam listener
        final boolean finalIsSelected = isSelected;
        final boolean finalIsInRange = isInRange;
        final boolean finalIsToday = isToday;

        dayPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!finalIsSelected && !finalIsInRange) {
                    dayPanel.setBackground(HOVER_COLOR);
                    dayPanel.repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!finalIsSelected && !finalIsInRange) {
                    if (finalIsToday) {
                        dayPanel.setBackground(TODAY_COLOR);
                    } else {
                        dayPanel.setBackground(Color.WHITE);
                    }
                    dayPanel.repaint();
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                handleDateSelection(tempCalendar);
            }
        });

        dayPanel.add(dayLabel, BorderLayout.CENTER);
        return dayPanel;
    }

    private void handleDateSelection(Calendar selectedCalendar) {
        Date selectedDate = selectedCalendar.getTime();

        if (isSelectingStartDate || startDate == null || selectedDate.before(startDate)) {
            startDate = selectedDate;
            endDate = null;
            isSelectingStartDate = false;
        } else {
            endDate = selectedDate;
            isSelectingStartDate = true;
        }

        updateCalendarDisplay();
    }

    private void handleQuickOption(int optionIndex) {
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();

        switch (optionIndex) {
            case 0: // Minggu Ini
                startCal.set(Calendar.DAY_OF_WEEK, startCal.getFirstDayOfWeek());
                endCal.set(Calendar.DAY_OF_WEEK, startCal.getFirstDayOfWeek() + 6);
                break;
            case 1: // Minggu Lalu
                startCal.add(Calendar.WEEK_OF_YEAR, -1);
                startCal.set(Calendar.DAY_OF_WEEK, startCal.getFirstDayOfWeek());
                endCal.add(Calendar.WEEK_OF_YEAR, -1);
                endCal.set(Calendar.DAY_OF_WEEK, startCal.getFirstDayOfWeek() + 6);
                break;
            case 2: // Bulan Ini
                startCal.set(Calendar.DAY_OF_MONTH, 1);
                endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
                break;
            case 3: // Bulan Lalu
                startCal.add(Calendar.MONTH, -1);
                startCal.set(Calendar.DAY_OF_MONTH, 1);
                endCal.set(Calendar.DAY_OF_MONTH, 1);
                endCal.add(Calendar.DATE, -1);
                break;
            case 4: // Tahun Ini
                startCal.set(Calendar.DAY_OF_YEAR, 1);
                endCal.set(Calendar.MONTH, 11);
                endCal.set(Calendar.DAY_OF_MONTH, 31);
                break;
            case 5: // Tahun Lalu
                startCal.add(Calendar.YEAR, -1);
                startCal.set(Calendar.DAY_OF_YEAR, 1);
                endCal.add(Calendar.YEAR, -1);
                endCal.set(Calendar.MONTH, 11);
                endCal.set(Calendar.DAY_OF_MONTH, 31);
                break;
            case 6: // Ketik Tahun (handled separately through text field)
                return;
        }

        startDate = startCal.getTime();
        endDate = endCal.getTime();

        // Update the calendar view to show the selected period
        displayedMonth1.setTime(startDate);
        displayedMonth2.setTime(startDate);
        displayedMonth2.add(Calendar.MONTH, 1);

        updateCalendarDisplay();
    }

    private void createSelectionPanel() {
        selectionPanel = new JPanel(null);

        selectionPanel.setBounds(10, FINAL_HEIGHT - 105, FINAL_WIDTH - 20, 30);

        // Warna latar yang lebih lembut
        selectionPanel.setBackground(new Color(250, 250, 250));
        selectionPanel.setOpaque(false);

        selectionPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));

        // Label dengan format yang sama
        rangeLabel = new JLabel("Pilih rentang tanggal", SwingConstants.CENTER);
        rangeLabel.setBounds(-80, 5, FINAL_WIDTH - 20, 20);
        rangeLabel.setForeground(Color.BLACK);
        rangeLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        selectionPanel.add(rangeLabel);
        contentPanel.add(selectionPanel);
        contentPanel.setComponentZOrder(selectionPanel, 0);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void updateRangeLabel() {
        if (rangeLabel == null) {
            return;
        }
        if (startDate != null && endDate != null) {
            rangeLabel.setText(displayFormat.format(startDate) + " - " + displayFormat.format(endDate));
        } else if (startDate != null) {
            rangeLabel.setText(displayFormat.format(startDate) + " - (Pilih tanggal akhir)");
        } else {
            rangeLabel.setText("Pilih rentang tanggal");
        }
    }

    private void createButtons() {
        batalButton = createButton("Batal", new Color(255, 255, 255), Color.BLACK);
        batalButton.setBounds(FINAL_WIDTH / 2 + 100, FINAL_HEIGHT - 50, 110, 30);
        batalButton.setBorder(new RoundedBorder(10, Color.BLACK, 1));
        batalButton.addActionListener(e -> startCloseAnimation());
        contentPanel.add(batalButton);
        prosesButton = createButton("Proses", new Color(64, 72, 82), Color.WHITE);
        prosesButton.setBorder(new RoundedBorder(10, Color.DARK_GRAY, 2));
        prosesButton.setBounds(FINAL_WIDTH / 2 + 230, FINAL_HEIGHT - 50, 110, 30);
        prosesButton.addActionListener(e -> processDateSelection());
        contentPanel.add(prosesButton);
    }

    private JButton createButton(String text, Color background, Color foreground) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(background);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setForeground(foreground);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void processDateSelection() {
        if (startDate != null && endDate != null) {
            if (dateRangeCallback != null) {
                dateRangeCallback.onDateRangeSelected(startDate, endDate);
            }
            startCloseAnimation();
        } else {
            PindahanAntarPopUp.showStartnDatePilihRentangTanggalLengkap(parentFrame);
        }
    }

    private void startScaleAnimation() {
        if (animationStarted || (animationTimer != null && animationTimer.isRunning())) {
            return;
        }

        animationStarted = true;
        final int totalFrames = ANIMATION_DURATION / ANIMATION_DELAY;
        final int[] currentFrame = {0};

        animationTimer = new Timer(ANIMATION_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentFrame[0]++;

                float progress = (float) currentFrame[0] / totalFrames;
                float easedProgress = 1 - (1 - progress) * (1 - progress) * (1 - progress);

                currentScale = 0.01f + 0.99f * easedProgress;

                repaint();

                if (currentFrame[0] >= totalFrames) {
                    animationTimer.stop();
                    currentScale = 1.0f;
                    repaint();
                }
            }
        });

        animationTimer.start();
    }

    private void startCloseAnimation() {
        if (closeAnimationTimer != null && closeAnimationTimer.isRunning()) {
            closeAnimationTimer.stop();
        }

        final int totalFrames = ANIMATION_DURATION / ANIMATION_DELAY;
        final int[] currentFrame = {0};

        closeAnimationTimer = new Timer(ANIMATION_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentFrame[0]++;

                float progress = (float) currentFrame[0] / totalFrames;
                float easedProgress = progress * progress;

                currentScale = 1.0f - 0.99f * easedProgress;

                repaint();

                if (currentFrame[0] >= totalFrames) {
                    closeAnimationTimer.stop();
                    cleanupAndClose();
                }
            }
        });

        closeAnimationTimer.start();
    }

    private void cleanupAndClose() {
        isShowingPopup = false;
        closePopup();
        dispose();
    }

    private void closePopup() {
        if (parentFrame != null) {
            parentLayeredPane().remove(glassPane);
            parentLayeredPane().repaint();
        }
    }

    public java.sql.Date getSqlStartDate() {
        return startDate != null ? new java.sql.Date(startDate.getTime()) : null;
    }

    public java.sql.Date getSqlEndDate() {
        return endDate != null ? new java.sql.Date(endDate.getTime()) : null;
    }

    private JLayeredPane parentLayeredPane() {
        return parentFrame.getLayeredPane();
    }

    class RoundedPanel extends JPanel {

        private int radius;

        public RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (animationStarted) {
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;

                AffineTransform originalTransform = g2.getTransform();
                g2.translate(centerX, centerY);
                g2.scale(currentScale, currentScale);
                g2.translate(-centerX, -centerY);

                // Draw background with rounded corners
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

                g2.setTransform(originalTransform);
            } else {
                // Draw background with rounded corners
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            }

            g2.dispose();
        }

        @Override
        protected void paintChildren(Graphics g) {
            if (animationStarted && currentScale < 0.3) {
                return;
            }

            if (animationStarted) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;

                g2d.translate(centerX, centerY);
                g2d.scale(currentScale, currentScale);
                g2d.translate(-centerX, -centerY);

                super.paintChildren(g2d);
                g2d.dispose();
            } else {
                super.paintChildren(g);
            }
        }
    }
}