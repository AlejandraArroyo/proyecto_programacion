/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clases;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author Alejandra Arroyo
 */
public class Conexion {
       private static final String URL = "jdbc:postgresql://ep-white-snowflake-a42g7l1l-pooler.us-east-1.aws.neon.tech:5432/proyecto_progra?sslmode=require";
    private static final String USUARIO = "proyecto_progra_owner";
    private static final String PASSWORD = "npg_MaER4r6neVWD";

    public static Connection getConexion() {
        try {
            return DriverManager.getConnection(URL, USUARIO, PASSWORD);
        } catch (SQLException e) {
            System.err.println("❌ Error de conexión: " + e.getMessage());
            return null;
        }
    }
    
}
