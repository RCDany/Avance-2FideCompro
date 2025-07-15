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
import java.util.ArrayList;

/**
 *
 * @author nanil
 */
public class Cliente extends Persona {

    public Cliente(String cedula, String nombre, String apellidos, String correo) {
        super(cedula, nombre, apellidos, correo);
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
     public static void EscribirCliente(ArrayList<Cliente> listaClientes) {
        try {
            FileOutputStream ArchivoClientes = new FileOutputStream("Clientes.ser");
            ObjectOutputStream output = new ObjectOutputStream(ArchivoClientes);
            output.writeObject(listaClientes);
            output.close();
            ArchivoClientes.close();
        }catch (IOException ex) {
            System.out.println("Exception: " + ex.getMessage());
            
        }
    }
    public static ArrayList<Cliente> LeerClientes() {
    ArrayList<Cliente> listaClientes = new ArrayList<>();
    try {
        FileInputStream archivoClientes = new FileInputStream("Clientes.ser");
        ObjectInputStream input = new ObjectInputStream(archivoClientes);
        listaClientes = (ArrayList<Cliente>) input.readObject();
        input.close();
        archivoClientes.close();
    } catch (IOException | ClassNotFoundException ex) {
        System.out.println("Exception: " + ex.getMessage());
    }
    return listaClientes;
    }
    
    
}