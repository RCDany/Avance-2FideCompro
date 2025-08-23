/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Servidor.dao;
import Servidor.db.Db;
import Servidor.Usuario;
import java.sql.*;
/**
 *
 * @author nanil
 */
public class UsuariosDAO {
    public boolean existeCedula(String cedula) throws SQLException{
        String sql = "SELECT 1 FROM usuarios WHERE cedula = ?";
        try(Connection c = Db.get();PreparedStatement ps= c.prepareStatement(sql)){
            ps.setString(1, cedula);
            try (ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        }
    }
    
    public void registrar(Usuario u) throws SQLException {
        String sql = "INSERT INTO usuarios(cedula, nombre, apellidos, correo, contrasena) VALUES(?,?,?,?,?)";
        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getCedula());
            ps.setString(2, u.getNombre());
            ps.setString(3, u.getApellidos());
            ps.setString(4, u.getCorreo());
            ps.setString(5, u.getContrasena());
            ps.executeUpdate();
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState()))
                throw new SQLException("CEDULA_DUPLICADA", e.getSQLState());
            throw e;
        }
    }
    public boolean verificarCredenciales(String cedula, String password) throws SQLException {
        String sql = "SELECT 1 FROM usuarios WHERE cedula = ? AND contrasena = ?";
        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cedula);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
