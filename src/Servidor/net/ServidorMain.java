/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Servidor.net;
import Servidor.db.Db;
import Servidor.servicio.UsuarioServicio;
import Servidor.Usuario;
import java.io.*;
import java.net.*;
import Servidor.servicio.ClienteServicio;
import java.util.concurrent.*;
import Servidor.servicio.ProductoServicio;
/**
 *
 * @author nanil
 */
public class ServidorMain {
    
    public static void main(String[] args) throws Exception {
        Db.ensureSchema(); // crea la BD/tabla si no existen
        ExecutorService pool = Executors.newFixedThreadPool(16);

        try (ServerSocket server = new ServerSocket(5000)) {
            System.out.println("[NET] Servidor escuchando en 5000");
            while (true) {
                Socket s = server.accept();
                pool.submit(new ClientHandler(s));
            }
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket s;
        private final UsuarioServicio service = new UsuarioServicio();
        private final ClienteServicio clienteServicio = new ClienteServicio();
        private final ProductoServicio productoServicio = new ProductoServicio();

        ClientHandler(Socket s) { this.s = s; }

        @Override public void run() {
            String who = s.getRemoteSocketAddress().toString();
            System.out.println("[NET] Conectado: " + who);
            try (s;
                 BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                 BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    String resp = procesar(line);
                    out.write(resp);
                    out.newLine();
                    out.flush();
                }
            } catch (Exception e) {
                System.out.println("[NET] " + who + " cerrado: " + e.getMessage());
            }
        }

        
        private String procesar(String line) {
            try {
                if (line.startsWith("CHECK_CEDULA|")) {
                    String cedula = line.split("\\|", 2)[1];
                    boolean exists = service.checkCedula(cedula);
                    return "EXISTS|" + exists;
                }
                if (line.startsWith("REGISTRAR_USUARIO|")) {
                    String[] p = line.split("\\|", 6);
                    if (p.length < 6) return "ERROR|FORMATO";
                    Usuario u = new Usuario(p[1], p[2], p[3], p[4], p[5]);
                    service.registrar(u);
                    return "OK|REGISTRADO";
                }
                if (line.startsWith("LOGIN|")) {
                    String[] p = line.split("\\|", 3);
                    if (p.length < 3) return "ERROR|FORMATO";
                    boolean ok = service.login(p[1], p[2]);
                    return ok ? "OK|LOGIN" : "ERROR|INVALIDO";
                }
                if (line.startsWith("CLIENTE_CHECK|")) {
                    String ced = line.split("\\|", 2)[1];
                    boolean exists = clienteServicio.checkCedula(ced);
                    return "EXISTS|" + exists;
                }
                if (line.startsWith("CLIENTE_REGISTRAR|")) {
                    //CLIENTE_REGISTRAR|cedula|nombre|apellidos|correo|tipo
                    String[] p = line.split("\\|", 6);
                    if (p.length < 6) return "ERROR|FORMATO";
                    try {
                        clienteServicio.registrar(p[1], p[2], p[3], p[4], p[5]);
                        return "OK|REGISTRADO";
                    } catch (IllegalArgumentException iae) {
                        return "ERROR|VALIDACION|" + iae.getMessage();
                    } catch (java.sql.SQLException sqle) {
                        if ("CEDULA_DUPLICADA".equals(sqle.getMessage())) return "ERROR|CEDULA_DUPLICADA";
                        return "ERROR|SQL";
                    } catch (Exception e) {
                        return "ERROR|" + e.getMessage();
                    }
                }
                if (line.startsWith("PRODUCTO_CHECK|")) {
                    String cod = line.split("\\|", 2)[1];
                    boolean exists = productoServicio.checkCodigo(cod);
                    return "EXISTS|" + exists;
                }

                if (line.startsWith("PRODUCTO_REGISTRAR|")) {
                    //PRODUCTO_REGISTRAR|codigo|nombre|desc|precio|cantidad
                    String[] p = line.split("\\|", 6);
                    if (p.length < 6) return "ERROR|FORMATO";
                    try {
                        double precio = Double.parseDouble(p[4]);
                        int cantidad = Integer.parseInt(p[5]);
                        productoServicio.registrar(p[1], p[2], p[3], precio, cantidad);
                        return "OK|REGISTRADO";
                    } catch (NumberFormatException nfe) {
                        return "ERROR|VALIDACION|Precio o cantidad inválidos";
                    } catch (IllegalArgumentException iae) {
                        return "ERROR|VALIDACION|" + iae.getMessage();
                    } catch (java.sql.SQLException sqle) {
                        if ("CODIGO_DUPLICADO".equals(sqle.getMessage())) return "ERROR|CODIGO_DUPLICADO";
                        return "ERROR|SQL";
                    } catch (Exception e) {
                        return "ERROR|" + e.getMessage();
                    }
                }

                if (line.equals("PRODUCTO_LISTAR")) {
                    try {
                        var lista = productoServicio.listar();
                       
                        StringBuilder sb = new StringBuilder("OK|");
                        for (int i = 0; i < lista.size(); i++) {
                            String[] it = lista.get(i);
                            // Sanitizar separadores básicos
                            for (int k=0;k<it.length;k++) {
                                if (it[k] == null) it[k] = "";
                                it[k] = it[k].replace("|"," ").replace("^"," ").replace(";"," ");
                            }
                            sb.append("PROD|")
                              .append(it[0]).append("^")
                              .append(it[1]).append("^")
                              .append(it[2]).append("^")
                              .append(it[3]).append("^")
                              .append(it[4]);
                            if (i < lista.size()-1) sb.append(";");
                        }
                        return sb.toString();
                    } catch (Exception e) {
                        return "ERROR|" + e.getMessage();
                    }
                }
                return "ERROR|COMANDO";
            } catch (IllegalArgumentException iae) {
                return "ERROR|VALIDACION|" + iae.getMessage();
            } catch (Exception ex) {
                if (ex instanceof java.sql.SQLException &&
                    "CEDULA_DUPLICADA".equals(ex.getMessage())) {
                    return "ERROR|CEDULA_DUPLICADA";
                }
                return "ERROR|" + ex.getMessage();
            }
        }
    }
}
