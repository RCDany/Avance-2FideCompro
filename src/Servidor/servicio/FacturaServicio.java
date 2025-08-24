/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Servidor.servicio;
import Servidor.db.Db;
import java.sql.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.Base64;
import java.math.RoundingMode;

/**
 *
 * @author nanil
 */
public class FacturaServicio {
    private static final BigDecimal IVA = new BigDecimal("0.13");

    public static class Item {
        public final String codigo;
        public final int cantidad;
        public Item(String codigo, int cantidad) { this.codigo = codigo; this.cantidad = cantidad; }
    }

    public static class Resultado {
        public final int id;
        public final BigDecimal subtotal, impuesto, total;
        public final String textoBase64;
        public Resultado(int id, BigDecimal sub, BigDecimal imp, BigDecimal tot, String b64) {
            this.id = id; this.subtotal=sub; this.impuesto=imp; this.total=tot; this.textoBase64=b64;
        }
    }

    public Resultado crearFactura(String clienteCed, String usuarioCed, List<Item> items) throws Exception {
        if (clienteCed == null || !clienteCed.matches("\\d{9,10}")) throw new IllegalArgumentException("Cédula cliente inválida");
        if (usuarioCed == null || !usuarioCed.matches("\\d{9,10}")) throw new IllegalArgumentException("Cédula usuario inválida");
        if (items == null || items.isEmpty()) throw new IllegalArgumentException("La factura no tiene ítems");

        try (Connection cn = Db.get()) {
            cn.setAutoCommit(false);
            try {
                
                if (!existe(cn, "SELECT 1 FROM clientes WHERE cedula = ?", clienteCed))
                    throw new IllegalStateException("CLIENTE_NO_ENCONTRADO");
                if (!existe(cn, "SELECT 1 FROM usuarios WHERE cedula = ?", usuarioCed))
                    throw new IllegalStateException("USUARIO_NO_ENCONTRADO");

                
                BigDecimal subtotal = BigDecimal.ZERO;
                List<Linea> lineas = new ArrayList<>();

                for (Item it : items) {
                    String cod = it.codigo.trim().toUpperCase();
                    if (it.cantidad <= 0) throw new IllegalArgumentException("Cantidad inválida para " + cod);

                    // obtener nombre y precio
                    String nombre = null;
                    BigDecimal precio = null;
                    try (PreparedStatement ps = cn.prepareStatement(
                            "SELECT nombre, precio, cantidad FROM productos WHERE codigo = ?")) {
                        ps.setString(1, cod);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next()) throw new IllegalStateException("PRODUCTO_NO_EXISTE:" + cod);
                            nombre = rs.getString(1);
                            precio = rs.getBigDecimal(2);
                            // Descontar solo si hay las que se piden
                        }
                    }

                    
                    try (PreparedStatement ps = cn.prepareStatement(
                            "UPDATE productos SET cantidad = cantidad - ? WHERE codigo = ? AND cantidad >= ?")) {
                        ps.setInt(1, it.cantidad);
                        ps.setString(2, cod);
                        ps.setInt(3, it.cantidad);
                        int upd = ps.executeUpdate();
                        if (upd == 0) throw new IllegalStateException("STOCK_INSUFICIENTE:" + cod);
                    }

                    BigDecimal cantBD = new BigDecimal(it.cantidad);
                    BigDecimal subLinea = precio.multiply(cantBD);
                    subtotal = subtotal.add(subLinea);
                    lineas.add(new Linea(cod, nombre, it.cantidad, precio, subLinea));
                }

                BigDecimal impuesto = subtotal.multiply(IVA).setScale(2, RoundingMode.HALF_UP);
                BigDecimal total = subtotal.add(impuesto);

                
                int facturaId;
                try (PreparedStatement ps = cn.prepareStatement(
                        "INSERT INTO facturas (cliente_cedula, usuario_cedula, subtotal, impuesto, total) VALUES (?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, clienteCed);
                    ps.setString(2, usuarioCed);
                    ps.setBigDecimal(3, subtotal);
                    ps.setBigDecimal(4, impuesto);
                    ps.setBigDecimal(5, total);
                    ps.executeUpdate();
                    try (ResultSet gk = ps.getGeneratedKeys()) {
                        if (!gk.next()) throw new SQLException("No se obtuvo ID de factura");
                        facturaId = gk.getInt(1);
                    }
                }

                // meter detalles
                try (PreparedStatement ps = cn.prepareStatement(
                        "INSERT INTO detalle_factura (factura_id, producto_codigo, cantidad, precio_unitario, subtotal_linea) VALUES (?,?,?,?,?)")) {
                    for (Linea ln : lineas) {
                        ps.setInt(1, facturaId);
                        ps.setString(2, ln.codigo);
                        ps.setInt(3, ln.cantidad);
                        ps.setBigDecimal(4, ln.precio);
                        ps.setBigDecimal(5, ln.subtotal);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                
                cn.commit();

                
                String texto = armarTextoFactura(facturaId, clienteCed, usuarioCed, lineas, subtotal, impuesto, total);
                String b64 = Base64.getEncoder().encodeToString(texto.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                return new Resultado(facturaId, subtotal, impuesto, total, b64);

            } catch (Exception e) {
                cn.rollback();
                throw e;
            } finally {
                cn.setAutoCommit(true);
            }
        }
    }

    private boolean existe(Connection cn, String sql, String ced) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, ced);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    private static class Linea {
        final String codigo, nombre; final int cantidad; final BigDecimal precio, subtotal;
        Linea(String c, String n, int q, BigDecimal p, BigDecimal s){ codigo=c; nombre=n; cantidad=q; precio=p; subtotal=s; }
    }

    private String armarTextoFactura(int id, String clienteCed, String usuarioCed,
                                     List<Linea> lineas, BigDecimal subtotal,
                                     BigDecimal impuesto, BigDecimal total) {
        StringBuilder sb = new StringBuilder();
        sb.append("FideCompro - Factura\n");
        sb.append("ID: ").append(id).append("\n");
        sb.append("Cliente: ").append(clienteCed).append("\n");
        sb.append("Vendedor: ").append(usuarioCed).append("\n");
        sb.append("--------------------------------------------------\n");
        sb.append(String.format("%-10s %-18s %5s %10s %10s%n", "Código","Nombre","Cant","Precio","Subtotal"));
        for (Linea ln : lineas) {
            sb.append(String.format("%-10s %-18s %5d %10.2f %10.2f%n",
                    ln.codigo, acorta(ln.nombre,18), ln.cantidad, ln.precio, ln.subtotal));
        }
        sb.append("--------------------------------------------------\n");
        sb.append(String.format("Subtotal: %.2f%n", subtotal));
        sb.append(String.format("Impuesto (13%%): %.2f%n", impuesto));
        sb.append(String.format("TOTAL: %.2f%n", total));
        return sb.toString();
    }
    private String acorta(String s, int n){ if (s==null) return ""; return s.length()<=n? s : s.substring(0,n-1)+"…"; }
}
