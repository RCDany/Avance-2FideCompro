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

    private boolean isBlank(String s){ return s == null || s.trim().isEmpty(); }
}
