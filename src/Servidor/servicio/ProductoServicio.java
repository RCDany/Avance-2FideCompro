/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Servidor.servicio;
import Servidor.dao.ProductosDAO;
import java.util.List;
/**
 *
 * @author nanil
 */
public class ProductoServicio {
    private final ProductosDAO dao = new ProductosDAO();

    public boolean checkCodigo(String codigo) throws Exception {
        if (codigo == null) throw new IllegalArgumentException("Código vacío");
        codigo = codigo.trim().toUpperCase();
        if (!codigo.matches("^PRD-\\d{3}$"))
            throw new IllegalArgumentException("Formato de código inválido (ej: PRD-001)");
        return dao.existeCodigo(codigo);
    }

    public void registrar(String codigo, String nombre, String desc, double precio, int cantidad) throws Exception {
        codigo = codigo.trim().toUpperCase();
        if (!codigo.matches("^PRD-\\d{3}$"))
            throw new IllegalArgumentException("Formato de código inválido (ej: PRD-001)");
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("Nombre vacío");
        if (precio < 0) throw new IllegalArgumentException("Precio negativo");
        if (cantidad < 0) throw new IllegalArgumentException("Cantidad negativa");
        dao.registrar(codigo, nombre.trim(), desc == null ? "" : desc.trim(), precio, cantidad);
    }

    public List<String[]> listar() throws Exception {
        return dao.listar();
    }
    public String[] get(String codigo) throws Exception {
        if (codigo == null) throw new IllegalArgumentException("Código vacío");
        codigo = codigo.trim().toUpperCase();
        if (!codigo.matches("PRD-\\d{3}")) throw new IllegalArgumentException("Código inválido");
        return dao.obtenerPorCodigo(codigo);
    }
    public void ajustarStock(String codigo, int delta) throws Exception {
        if (codigo == null || !codigo.matches("PRD-\\d{3}")) throw new IllegalArgumentException("Código inválido");
        try (var cn = Servidor.db.Db.get();
             var ps = cn.prepareStatement(
                "UPDATE productos SET cantidad = cantidad + ? WHERE codigo=? AND (cantidad + ?) >= 0")) {
            ps.setInt(1, delta);
            ps.setString(2, codigo);
            ps.setInt(3, delta);
            int n = ps.executeUpdate();
            if (n == 0) throw new IllegalStateException("STOCK_NEGATIVO");
        }
    }

    public void eliminar(String codigo) throws Exception {
        if (codigo == null || !codigo.matches("PRD-\\d{3}")) throw new IllegalArgumentException("Código inválido");
        try (var cn = Servidor.db.Db.get()) {
            try (var chk = cn.prepareStatement("SELECT COUNT(*) FROM detalle_factura WHERE producto_codigo=?")) {
                chk.setString(1, codigo);
                try (var rs = chk.executeQuery()) { rs.next(); if (rs.getInt(1) > 0) throw new IllegalStateException("TIENE_FACTURAS"); }
            }
            try (var del = cn.prepareStatement("DELETE FROM productos WHERE codigo=?")) {
                del.setString(1, codigo);
                int n = del.executeUpdate();
                if (n == 0) throw new IllegalStateException("NO_EXISTE");
            }
        }
    }

    public java.util.List<String[]> buscarPorNombre(String patron) throws Exception {
        if (patron == null || patron.isBlank()) throw new IllegalArgumentException("Patrón vacío");
        var out = new java.util.ArrayList<String[]>();
        try (var cn = Servidor.db.Db.get();
             var ps = cn.prepareStatement(
                "SELECT codigo,nombre,descripcion,precio,cantidad FROM productos WHERE UPPER(nombre) LIKE ? ORDER BY nombre")) {
            ps.setString(1, "%"+patron.toUpperCase()+"%");
            try (var rs = ps.executeQuery()) {
                while (rs.next()) out.add(new String[]{
                    rs.getString(1), rs.getString(2), rs.getString(3),
                    rs.getBigDecimal(4).toPlainString(), String.valueOf(rs.getInt(5))
                });
            }
        }
        return out;
    }

    private boolean isBlank(String s){ return s == null || s.trim().isEmpty(); }
}
