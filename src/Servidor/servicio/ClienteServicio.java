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
        if (cedula == null || !cedula.matches("\\d{9,10}")) throw new IllegalArgumentException("Cédula inválida");
        return dao.existeCedula(cedula);
    }

    public void registrar(String ced, String nom, String ape, String correo, String tipo) throws Exception {
        if (ced == null || !ced.matches("\\d{9,10}")) throw new IllegalArgumentException("Cédula inválida");
        if (isBlank(nom) || isBlank(ape) || isBlank(correo) || isBlank(tipo)) throw new IllegalArgumentException("Campos vacíos");
        if (!correo.contains("@") || !correo.contains(".")) throw new IllegalArgumentException("Correo inválido");
        dao.registrar(ced, nom, ape, correo, tipo);
    }

    private boolean isBlank(String s){ return s == null || s.trim().isEmpty(); }
}
