/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectofinal;

/**
 *
 * @author nanil
 */
public class Sesion {
    private static Usuario usuarioActual;
    
    public static void iniciarSesion(Usuario usuario){
        usuarioActual = usuario;
    }
    
    public static Usuario getUsuarioActual(){
        return usuarioActual;
    }
    
    public static void cerrarSesion(){
        usuarioActual = null;
    }
}
