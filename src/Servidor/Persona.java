/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Servidor;

import java.io.Serializable;

/**
 *
 * @author nanil
 */
public class Persona implements Serializable {
    String cedula;
    String nombre;
    String apellidos;
    String correo;
    private static final long serialVersionUID = 1L;
    public Persona(String cedula, String nombre, String apellidos, String correo) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.correo = correo;
    }
    
    public boolean validarCorreo(){
        return correo.contains("@") && correo.contains(".");
    }
    public boolean validarCedula(){
        return cedula.matches("\\d{9,10}");
    }
    
}

