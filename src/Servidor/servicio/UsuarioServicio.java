/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Servidor.servicio;
import Servidor.Usuario;
import Servidor.dao.UsuariosDAO;
/**
 *
 * @author nanil
 */
public class UsuarioServicio {
    private final UsuariosDAO dao = new UsuariosDAO();

    public boolean checkCedula(String cedula) throws Exception {
        if (cedula == null || !cedula.matches("\\d{9,10}"))
            throw new IllegalArgumentException("Cédula inválida");
        return dao.existeCedula(cedula);
    }

    public void registrar(Usuario u) throws Exception {
        if (u.getCedula() == null || !u.getCedula().matches("\\d{9,10}"))
            throw new IllegalArgumentException("Cédula inválida");
        if (u.getNombre().isBlank() || u.getApellidos().isBlank() ||
            u.getCorreo().isBlank() || u.getContrasena().isBlank())
            throw new IllegalArgumentException("Campos vacíos");

        dao.registrar(u); 
    }
    public boolean login(String cedula, String password) throws Exception {
        if (cedula == null || !cedula.matches("\\d{9,10}"))
            throw new IllegalArgumentException("Cédula inválida");
        if (password == null || password.isBlank())
            throw new IllegalArgumentException("Contraseña vacía");
        return dao.verificarCredenciales(cedula, password);
    }
}
