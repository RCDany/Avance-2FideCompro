/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Servidor.db;
import java.sql.*;
/**
 *
 * @author nanil
 */
public class Db {
    // Codigos de DB
    // X0Y32 este sale si existe
    
    private static final String URL = "jdbc:derby:./data/tienda;create=true";
    
    public static Connection get() throws SQLException {
        return DriverManager.getConnection(URL);
    }
    
    public static void ensureSchema() throws SQLException{
        try (Connection c = get(); Statement st = c.createStatement()) {
            try {
                st.executeUpdate("""
                    CREATE TABLE usuarios (
                        cedula VARCHAR(12) PRIMARY KEY,
                        nombre VARCHAR(100) NOT NULL,
                        apellidos VARCHAR(100) NOT NULL,
                        correo VARCHAR(120) NOT NULL,
                        contrasena VARCHAR(200) NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                """);
                System.out.println("[DB] Tabla 'usuarios' creada.");
            } catch (SQLException e) {
                if (!"X0Y32".equals(e.getSQLState())) throw e; 
                System.out.println("[DB] Tabla 'usuarios' ya existía.");
            }
            try {
                st.executeUpdate("""
                    CREATE TABLE clientes (
                        cedula VARCHAR(12) PRIMARY KEY,
                        nombre VARCHAR(100) NOT NULL,
                        apellidos VARCHAR(100) NOT NULL,
                        correo VARCHAR(120) NOT NULL,
                        tipo_cedula VARCHAR(20) NOT NULL,
                        empresa_nombre VARCHAR(150),
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                """);
                System.out.println("[DB] Tabla 'clientes' creada.");
            } catch (SQLException e) {
                if (!"X0Y32".equals(e.getSQLState())) throw e;
                System.out.println("[DB] Tabla 'clientes' ya existía.");
            }
            try {
                st.executeUpdate("""
                    CREATE TABLE productos (
                        codigo VARCHAR(20) PRIMARY KEY,
                        nombre VARCHAR(100) NOT NULL,
                        descripcion VARCHAR(200),
                        precio DECIMAL(10,2) NOT NULL,
                        cantidad INT NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                """);
                System.out.println("[DB] Tabla 'productos' creada.");
            } catch (SQLException e) {
                if (!"X0Y32".equals(e.getSQLState())) throw e; 
                System.out.println("[DB] Tabla 'productos' ya existía.");
            }
        }
    }
}
