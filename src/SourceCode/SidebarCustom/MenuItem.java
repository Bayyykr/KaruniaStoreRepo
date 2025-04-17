/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SourceCode.SidebarCustom;

/**
 *
 * @author HP
 */
import SourceCode.SidebarCustom.Model_Menu;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

public class MenuItem extends javax.swing.JPanel {

    private final Model_Menu data;

    public MenuItem(Model_Menu data) {
        this.data = data;
        initComponents();
        setOpaque(false);
        if (data.getType() == Model_Menu.MenuType.MENU) {
            lbIcon.setIcon(data.toIcon());
            lbName.setText(data.getName());
        } else {
            lbName.setText(" ");
        }
    }

    public void setSelected(boolean selected) {
        if (data.getType() == Model_Menu.MenuType.MENU) {
            if (selected) {
                lbIcon.setIcon(data.toIconSelected());
                lbName.setForeground(new Color(0, 0, 0));
            } else {
                lbIcon.setIcon(data.toIcon());
                lbName.setForeground(new Color(255, 255, 255));
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbIcon = new javax.swing.JLabel();
        lbName = new javax.swing.JLabel();

        lbName.setForeground(new java.awt.Color(255, 255, 255));
        lbName.setFont(new Font("SansSerif", Font.PLAIN, 20));
        lbName.setText("Menu Name");
        
//        lbIcon.setBackground(Color.red);
//        lbIcon.setOpaque(true);
        lbIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                               .addComponent(lbIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lbName)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(15, 15, 15) // Add top internal padding
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                        .addComponent(lbIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lbName, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(20, 20, 20))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lbIcon;
    private javax.swing.JLabel lbName;
    // End of variables declaration//GEN-END:variables
}
