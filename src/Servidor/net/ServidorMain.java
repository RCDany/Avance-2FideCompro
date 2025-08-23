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
import java.util.concurrent.*;
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
