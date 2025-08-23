/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Servidor;
import java.util.ArrayList;
/**
 *
 * @author nanil
 */
public class Factura {
    private Cliente cliente;
    private ArrayList<Producto> productos;
    private ArrayList<Integer> cantidades;
    
    private double subtotal;
    private double impuesto;
    private double total;

    public Factura(Cliente cliente, ArrayList<Producto> productos, ArrayList<Integer> cantidades, double subtotal, double impuesto, double total) {
        this.cliente = cliente;
        this.productos = productos;
        this.cantidades = cantidades;
        this.subtotal = subtotal;
        this.impuesto = impuesto;
        this.total = total;
    }
    
    
    
}
