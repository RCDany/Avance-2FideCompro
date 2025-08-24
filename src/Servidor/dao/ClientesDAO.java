/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Servidor.dao;
import Servidor.db.Db;
import java.sql.*;
/**
 *
 * @author nanil
 */
public class ClientesDAO {
    public boolean existeCedula(String cedula) throws SQLException {
        String sql = "SELECT 1 FROM clientes WHERE cedula = ?";
        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cedula);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void registrar(String cedula, String nombre, String apellidos,
                          String correo, String tipoCedula) throws SQLException {
        String sql = "INSERT INTO clientes(cedula, nombre, apellidos, correo, tipo_cedula) VALUES(?,?,?,?,?)";
        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cedula);
            ps.setString(2, nombre);
            ps.setString(3, apellidos);
            ps.setString(4, correo);
            ps.setString(5, tipoCedula);
            ps.executeUpdate();
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) throw new SQLException("CEDULA_DUPLICADA", e.getSQLState());
            throw e;
        }
    }
}
