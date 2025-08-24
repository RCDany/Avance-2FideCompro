/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Servidor.dao;
import Servidor.db.Db;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author nanil
 */
public class ProductosDAO {
    public boolean existeCodigo(String codigo) throws SQLException {
        String sql = "SELECT 1 FROM productos WHERE codigo = ?";
        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void registrar(String codigo, String nombre, String descripcion,
                          double precio, int cantidad) throws SQLException {
        String sql = "INSERT INTO productos(codigo, nombre, descripcion, precio, cantidad) VALUES(?,?,?,?,?)";
        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, codigo);
            ps.setString(2, nombre);
            ps.setString(3, descripcion);
            ps.setBigDecimal(4, java.math.BigDecimal.valueOf(precio));
            ps.setInt(5, cantidad);
            ps.executeUpdate();
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) throw new SQLException("CODIGO_DUPLICADO", e.getSQLState());
            throw e;
        }
    }

    public List<String[]> listar() throws SQLException {
        String sql = "SELECT codigo, nombre, descripcion, precio, cantidad FROM productos ORDER BY nombre";
        List<String[]> out = new ArrayList<>();
        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new String[]{
                    rs.getString(1), rs.getString(2), rs.getString(3),
                    rs.getBigDecimal(4).toPlainString(), String.valueOf(rs.getInt(5))
                });
            }
        }
        return out;
    }
}
