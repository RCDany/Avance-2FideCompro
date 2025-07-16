/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectofinal;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author nanil
 */
public class Usuario extends Persona implements Serializable{
    private String contrasena;
    private static final long serialVersionUID = 1L;
    public Usuario(String cedula, String nombre, String apellidos, String correo, String contrasena) {
        super(cedula, nombre, apellidos, correo);
        this.contrasena = contrasena;
    }

    
    public boolean iniciarSesion(String cedula, String contrasena){
        return this.cedula.equals(cedula) && this.contrasena.equals(contrasena);
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
    
    public static void EscribirUsuario(ArrayList<Usuario> listaUsuarios) {
        try {
            FileOutputStream ArchivoUsuarios = new FileOutputStream("Usuarios.ser");
            ObjectOutputStream output = new ObjectOutputStream(ArchivoUsuarios);
            output.writeObject(listaUsuarios);
            output.close();
            ArchivoUsuarios.close();
        }catch (IOException ex) {
            System.out.println("Exception: " + ex.getMessage());
            
        }
    }
    public static ArrayList<Usuario> LeerUsuarios() {
    ArrayList<Usuario> listaUsuarios = new ArrayList<>();
    try {
        FileInputStream archivoUsuarios = new FileInputStream("Usuarios.ser");
        ObjectInputStream input = new ObjectInputStream(archivoUsuarios);
        listaUsuarios = (ArrayList<Usuario>) input.readObject();
        input.close();
        archivoUsuarios.close();
    } catch (IOException | ClassNotFoundException ex) {
        System.out.println("Exception: " + ex.getMessage());
        }
    return listaUsuarios;
        }
    public static Usuario verificarCredenciales(String usuarioIngresado, String contrasenaIngresada){
        ArrayList<Usuario> listaUsuarios = LeerUsuarios();
        for (Usuario u :listaUsuarios) {
           if (u.getCedula().equals(usuarioIngresado) && u.getContrasena().equals(contrasenaIngresada)){
               return u;
           }
       }
        return null;
    }
    }