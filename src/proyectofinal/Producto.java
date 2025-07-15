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
public class Producto implements Serializable {
    private String codigo;
    private String nombre;
    private String descripcion;
    private double precio;
    private int cantidad;
    private static final long serialVersionUID = 1L;
    public Producto(String codigo, String nombre, String descripcion, double precio, int cantidad) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    
    public static void EscribirProducto(ArrayList<Producto> listaProductos) {
        try {
            FileOutputStream ArchivoProductos = new FileOutputStream("Productos.ser");
            ObjectOutputStream output = new ObjectOutputStream(ArchivoProductos);
            output.writeObject(listaProductos);
            output.close();
            ArchivoProductos.close();
        }catch (IOException ex) {
            System.out.println("Exception: " + ex.getMessage());
            
        }
    }
    public static ArrayList<Producto> LeerProductos() {
    ArrayList<Producto> listaProductos = new ArrayList<>();
    try {
        FileInputStream archivoProductos = new FileInputStream("Productos.ser");
        ObjectInputStream input = new ObjectInputStream(archivoProductos);
        listaProductos = (ArrayList<Producto>) input.readObject();
        input.close();
        archivoProductos.close();
    } catch (IOException | ClassNotFoundException ex) {
        System.out.println("Exception: " + ex.getMessage());
    }
    return listaProductos;
    }
    
}
