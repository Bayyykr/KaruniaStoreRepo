/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

/**
 *
 * @author HP
 */

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class conn {
    private static Connection conn;
    
    public static Connection getConnection(){
        if (conn == null) {
            try {
               String url = "jdbc:mysql://localhost:3306/karuniastore";
               String user = "root";
               String pass = "";
               DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
               conn = (Connection) DriverManager.getConnection(url, user, pass);
                System.out.println("berhasil");
            }catch(Exception e){
                Logger.getLogger(conn.class.getName()).log(Level.SEVERE,null, e);
            }
        }
        return conn;
}
}
