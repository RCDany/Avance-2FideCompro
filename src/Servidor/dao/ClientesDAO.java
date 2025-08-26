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
                      String correo, String tipoCedula, String empresaNombre) throws SQLException {
        String sql = "INSERT INTO clientes(cedula, nombre, apellidos, correo, tipo_cedula, empresa_nombre) VALUES(?,?,?,?,?,?)";
        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cedula);
            ps.setString(2, nombre);
            ps.setString(3, apellidos);
            ps.setString(4, correo);
            ps.setString(5, tipoCedula);
            ps.setString(6, empresaNombre);
            ps.executeUpdate();
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) throw new SQLException("CEDULA_DUPLICADA", e.getSQLState());
            throw e;
        }
    }
    public String[] obtenerPorCedula(String cedula) throws SQLException {
        String sql = "SELECT cedula, nombre, apellidos, correo, tipo_cedula, empresa_nombre " +
                     "FROM clientes WHERE cedula = ?";
        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cedula);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new String[]{
                    rs.getString(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6)
                };
            }
        }
    }
    public void actualizar(String ced, String nom, String ape, String correo, String empresa) throws Exception {
        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE clientes SET nombre=?, apellidos=?, correo=?, empresa_nombre=? WHERE cedula=?")) {
            ps.setString(1, nom);
            ps.setString(2, ape);
            ps.setString(3, correo);
            ps.setString(4, empresa);
            ps.setString(5, ced);
            int n = ps.executeUpdate();
            if (n == 0) throw new IllegalStateException("NO_ENCONTRADO");
        }
    }

    
        public void eliminar(String ced) throws Exception {
            try (Connection c = Db.get()) {
                try (PreparedStatement chk = c.prepareStatement(
                        "SELECT COUNT(*) FROM facturas WHERE cliente_cedula=?")) {
                    chk.setString(1, ced);
                    try (ResultSet rs = chk.executeQuery()) {
                        rs.next();
                        if (rs.getInt(1) > 0) throw new IllegalStateException("TIENE_FACTURAS");
                    }
                }
                try (PreparedStatement ps = c.prepareStatement("DELETE FROM clientes WHERE cedula=?")) {
                    ps.setString(1, ced);
                    int n = ps.executeUpdate();
                    if (n == 0) throw new IllegalStateException("NO_ENCONTRADO");
                }
            }
        }

        
        public java.util.List<String[]> listar() throws Exception {
            java.util.List<String[]> out = new java.util.ArrayList<>();
            String sql = "SELECT cedula,nombre,apellidos,correo,tipo_cedula,COALESCE(empresa_nombre,'') " +
                         "FROM clientes ORDER BY nombre,apellidos";
            try (Connection c = Db.get();
                 PreparedStatement ps = c.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new String[]{
                            rs.getString(1), rs.getString(2), rs.getString(3),
                            rs.getString(4), rs.getString(5), rs.getString(6)
                    });
                }
            }
            return out;
        }

        
        public java.util.List<String[]> buscarPorNombre(String nombre, String apellidos) throws Exception {
            java.util.List<String[]> out = new java.util.ArrayList<>();
            boolean tieneAp = apellidos != null && !apellidos.trim().isEmpty();
            String sql = "SELECT cedula,nombre,apellidos,correo,tipo_cedula,COALESCE(empresa_nombre,'') " +
                         "FROM clientes WHERE UPPER(nombre) LIKE UPPER(?) " +
                         (tieneAp ? "AND UPPER(apellidos) LIKE UPPER(?) " : "") +
                         "ORDER BY nombre,apellidos";
            try (Connection c = Db.get();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, "%" + (nombre == null ? "" : nombre.trim()) + "%");
                if (tieneAp) ps.setString(2, "%" + apellidos.trim() + "%");
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        out.add(new String[]{
                                rs.getString(1), rs.getString(2), rs.getString(3),
                                rs.getString(4), rs.getString(5), rs.getString(6)
                        });
                    }
                }
            }
            return out;
        }
    

}
