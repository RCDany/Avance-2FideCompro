/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Servidor.servicio;
import Servidor.dao.ClientesDAO;
/**
 *
 * @author nanil
 */
public class ClienteServicio {
    private final ClientesDAO dao = new ClientesDAO();

    public boolean checkCedula(String cedula) throws Exception {
        cedula = norm(cedula);
        if (!cedula.matches("\\d{9,10}")) throw new IllegalArgumentException("Cédula inválida");
        return dao.existeCedula(cedula);
    }
    public void registrar(String ced, String nom, String ape, String correo, String tipo, String empresa) throws Exception {
        ced = norm(ced);
        if (ced == null || !ced.matches("\\d{9,10}")) throw new IllegalArgumentException("Cédula inválida");
        if (isBlank(nom) || isBlank(ape) || isBlank(correo) || isBlank(tipo)) throw new IllegalArgumentException("Campos vacíos");
        if (!correo.contains("@") || !correo.contains(".")) throw new IllegalArgumentException("Correo inválido");
        if ("Juridica".equalsIgnoreCase(tipo) && isBlank(empresa))
            throw new IllegalArgumentException("Nombre de empresa requerido para cédula Jurídica");
        dao.registrar(ced, nom, ape, correo, tipo, isBlank(empresa) ? "" : empresa.trim());
    }
    public String[] obtenerCliente(String cedula) throws Exception {
        cedula = norm(cedula);
        if (!cedula.matches("\\d{9,10}")) throw new IllegalArgumentException("Cédula inválida");
        return dao.obtenerPorCedula(cedula);
    }
    public java.util.List<String[]> listar() throws Exception {
        return dao.listar(); 
    }
    public java.util.List<String[]> buscarPorNombre(String nombre, String apellidos) throws Exception {
        nombre = norm(nombre);
        apellidos = norm(apellidos);
        return dao.buscarPorNombre(nombre, apellidos);
    }
    public void actualizar(String ced, String nom, String ape, String correo, String empresa) throws Exception {
        ced = norm(ced); nom = norm(nom); ape = norm(ape); correo = norm(correo);
        if (!ced.matches("\\d{9,10}")) throw new IllegalArgumentException("Cédula inválida");
        if (isBlank(nom) || isBlank(correo)) throw new IllegalArgumentException("Campos vacíos");
        if (!correo.contains("@") || !correo.contains(".")) throw new IllegalArgumentException("Correo inválido");
        String[] actual = dao.obtenerPorCedula(ced);
        if (actual == null) throw new IllegalStateException("NO_ENCONTRADO");
        String tipo = actual[4]; 

        if ("Juridica".equalsIgnoreCase(tipo) && isBlank(empresa))
            throw new IllegalArgumentException("Nombre de empresa requerido para cédula Jurídica");
        if (!"Juridica".equalsIgnoreCase(tipo) && isBlank(ape))
            throw new IllegalArgumentException("Apellidos requeridos para cédula Física");

        dao.actualizar(ced, nom, ape, correo, isBlank(empresa) ? "" : empresa.trim());
    }

    public void eliminar(String cedula) throws Exception {
        cedula = norm(cedula);
        if (!cedula.matches("\\d{9,10}")) throw new IllegalArgumentException("Cédula inválida");
        dao.eliminar(cedula);
    }
    private String norm(String s) { return s == null ? "" : s.trim(); }
    private boolean isBlank(String s){ return s == null || s.trim().isEmpty(); }
}
