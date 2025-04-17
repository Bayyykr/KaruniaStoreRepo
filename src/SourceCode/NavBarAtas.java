/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SourceCode;

/**
 *
 * @author HP
 */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class NavBarAtas extends JPanel{
    private JLabel notificationLabel;
    private JLabel profileLabel;
    
    public NavBarAtas() {
        initComponents();
    }
    
    private void initComponents() {
        // Set panel properties
        setPreferredSize(new Dimension(800, 40));
        setBackground(new Color(0, 0, 0)); // Dark gray color from the image
        setLayout(new FlowLayout(FlowLayout.RIGHT, 30, 10)); 
        
        // Create notification bell icon
        notificationLabel = new JLabel();
        try {
            ImageIcon bellIcon = new ImageIcon(getClass().getResource("/SourceImage/notifications-icon.png"));
            notificationLabel.setIcon(bellIcon);
        } catch (Exception e) {
            // Fallback if image not found
            notificationLabel.setText("🔔");
            notificationLabel.setForeground(Color.WHITE);
            notificationLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        }
        
        // Create profile icon
        profileLabel = new JLabel();
        try {
            ImageIcon profileIcon = new ImageIcon(getClass().getResource("/SourceImage/user-icon.png"));
            profileLabel.setIcon(profileIcon);
        } catch (Exception e) {
            // Fallback if image not found
            profileLabel.setText("👤");
            profileLabel.setForeground(Color.WHITE);
            profileLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        }
        
        // Add components to panel
        add(notificationLabel);
        add(profileLabel);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }
    
    // Method to change notification status (can be extended)
    public void setNotificationStatus(boolean hasNotification) {
        // Implement notification indicator logic here
        // For example, change the icon or add a red dot
    }
}
