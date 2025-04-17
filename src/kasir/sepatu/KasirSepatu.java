/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package kasir.sepatu;

/**
 *
 * @author HP
 */
import db.conn;
import java.sql.Connection;

public class KasirSepatu {

    /**
     * @param args the command line arguments
     */
    private static Connection con;

    public static void main(String[] args) {
        // TODO code application logic here
        con = conn.getConnection();

    }

}
