/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cliente.net;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
/**
 *
 * @author nanil
 */
public class ClientApi {
    private final String host;
    private final int port;
    private final int timeoutMillis = 4000;

    public ClientApi() { this("127.0.0.1", 5000); }
    public ClientApi(String host, int port) { this.host = host; this.port = port; }

    private String send(String line) throws IOException {
        Socket s = new Socket();
        s.connect(new InetSocketAddress(host, port), timeoutMillis);
        s.setSoTimeout(timeoutMillis);
        try (s;
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8));
             BufferedReader in  = new BufferedReader(new InputStreamReader(s.getInputStream(),  StandardCharsets.UTF_8))) {
            out.write(line);
            out.newLine();
            out.flush();
            String resp = in.readLine();
            if (resp == null) throw new IOException("Sin respuesta del servidor");
            return resp;
        }
    }

    public boolean checkCedula(String cedula) throws IOException {
        String resp = send("CHECK_CEDULA|" + cedula);
        //la respuesta debe ser "EXISTS|true" o "EXISTS|false"
        if (resp.startsWith("EXISTS|")) {
            return Boolean.parseBoolean(resp.substring("EXISTS|".length()));
        }
        throw new IOException("Respuesta inesperada: " + resp);
    }

    public Resultado registrarUsuario(String ced, String nom, String ape, String mail, String pass) throws IOException {
        String safe = String.join("|", "REGISTRAR_USUARIO", ced, nom, ape, mail, pass);
        String resp = send(safe);
        if (resp.startsWith("OK|")) return new Resultado(true, "Registrado");
        return new Resultado(false, resp);
    }

    public Resultado login(String ced, String pass) throws IOException {
        String resp = send("LOGIN|" + ced + "|" + pass);
        if (resp.startsWith("OK|LOGIN")) return new Resultado(true, "Login OK");
        return new Resultado(false, resp);
    }

    public static class Resultado {
        public final boolean ok;
        public final String mensaje;
        public Resultado(boolean ok, String mensaje) { this.ok = ok; this.mensaje = mensaje; }
    }
}
