import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import produk.ComboboxCustom;

public class popUpAturTanggal extends JDialog {
    private ComboboxCustom monthComboBox;
    private ComboboxCustom yearComboBox;
    private int selectedMonth;
    private int selectedYear;
    private boolean confirmed = false;

    public popUpAturTanggal(JFrame parent, Calendar currentDate) {
        super(parent, "Atur Bulan dan Tahun", true);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel monthPanel = new JPanel(new BorderLayout());
        JLabel monthLabel = new JLabel("Atur Bulan/Month");
        monthLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        monthLabel.setBackground(null);
        monthPanel.add(monthLabel, BorderLayout.NORTH);

        String[] months = {"Pilih Bulan", "Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
        monthComboBox = new ComboboxCustom(months);
        monthComboBox.setSelectedIndex(0);
        monthComboBox.setPreferredSize(new Dimension(200, 40));
        monthPanel.add(monthComboBox, BorderLayout.CENTER);
        mainPanel.add(monthPanel);

       
        monthComboBox.addActionListener(e -> {
            if (monthComboBox.getSelectedIndex() == 0) {
                monthComboBox.setSelectedItem("");
            }
        });

        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel yearPanel = new JPanel(new BorderLayout());
        JLabel yearLabel = new JLabel("Atur Tahun/Year");
        yearLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        yearLabel.setBackground(null);
        yearPanel.add(yearLabel, BorderLayout.NORTH);

        String[] years = new String[11];
        years[0] = "Pilih Tahun";
        for (int i = 1; i < years.length; i++) {
            years[i] = String.valueOf(2023 + i);
        }
        
        yearComboBox = new ComboboxCustom(years);
        yearComboBox.setSelectedIndex(0);
        yearComboBox.setPreferredSize(new Dimension(200, 40));
        yearPanel.add(yearComboBox, BorderLayout.CENTER);
        mainPanel.add(yearPanel);

       
        yearComboBox.addActionListener(e -> {
            if (yearComboBox.getSelectedIndex() == 0) {
                yearComboBox.setSelectedItem("");
            }
        });

        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton batalButton = new JButton("BATAL");
        JButton terapkanButton = new JButton("TERAPKAN");

        batalButton.setPreferredSize(new Dimension(100, 40));
        terapkanButton.setPreferredSize(new Dimension(100, 40));

        batalButton.addActionListener(e -> dispose());

        terapkanButton.addActionListener(e -> {
            if (monthComboBox.getSelectedIndex() > 0 && yearComboBox.getSelectedIndex() > 0) {
                selectedMonth = monthComboBox.getSelectedIndex() - 1;
                selectedYear = Integer.parseInt((String) yearComboBox.getSelectedItem());
                confirmed = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Harap pilih bulan dan tahun terlebih dahulu.",
                        "Peringatan",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        buttonPanel.add(batalButton);
        buttonPanel.add(terapkanButton);
        mainPanel.add(buttonPanel);

        add(mainPanel);
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    public int getSelectedMonth() {
        return selectedMonth;
    }

    public int getSelectedYear() {
        return selectedYear;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}