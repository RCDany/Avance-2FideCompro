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
            try {
                st.executeUpdate("""
                    CREATE TABLE facturas (
                        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                        fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        cliente_cedula VARCHAR(12) NOT NULL,
                        usuario_cedula VARCHAR(12) NOT NULL,
                        subtotal DECIMAL(12,2) NOT NULL,
                        impuesto DECIMAL(12,2) NOT NULL,
                        total DECIMAL(12,2) NOT NULL
                    )
                """);
                System.out.println("[DB] Tabla 'facturas' creada.");
            } catch (SQLException e) {
                if (!"X0Y32".equals(e.getSQLState())) throw e;
                System.out.println("[DB] Tabla 'facturas' ya existía.");
            }
            try {
                st.executeUpdate("""
                    CREATE TABLE detalle_factura (
                        factura_id INT NOT NULL,
                        producto_codigo VARCHAR(20) NOT NULL,
                        cantidad INT NOT NULL,
                        precio_unitario DECIMAL(10,2) NOT NULL,
                        subtotal_linea DECIMAL(12,2) NOT NULL,
                        PRIMARY KEY (factura_id, producto_codigo),
                        CONSTRAINT fk_df_factura FOREIGN KEY (factura_id) REFERENCES facturas(id),
                        CONSTRAINT fk_df_producto FOREIGN KEY (producto_codigo) REFERENCES productos(codigo)
                    )
                """);
                System.out.println("[DB] Tabla 'detalle_factura' creada.");
            } catch (SQLException e) {
                if (!"X0Y32".equals(e.getSQLState())) throw e;
                System.out.println("[DB] Tabla 'detalle_factura' ya existía.");
            }

        }
    }
}
