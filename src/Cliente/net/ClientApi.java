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
    public boolean clienteCheck(String cedula) throws IOException {
        String resp = send("CLIENTE_CHECK|" + cedula);
        if (resp.startsWith("EXISTS|")) return Boolean.parseBoolean(resp.substring("EXISTS|".length()));
        throw new IOException("Respuesta inesperada: " + resp);
    }

    public Resultado clienteRegistrar(String ced, String nom, String ape, String mail, String tipo, String empresa) throws IOException {
        String line = String.join("|", "CLIENTE_REGISTRAR", ced, nom, ape, mail, tipo, empresa == null ? "" : empresa);
        String resp = send(line);
        return resp.startsWith("OK|") ? new Resultado(true, "Registrado") : new Resultado(false, resp);
    }
    public boolean productoCheck(String codigo) throws IOException {
    String resp = send("PRODUCTO_CHECK|" + codigo);
    if (resp.startsWith("EXISTS|")) return Boolean.parseBoolean(resp.substring("EXISTS|".length()));
    throw new IOException("Respuesta inesperada: " + resp);
    }

    public Resultado productoRegistrar(String cod, String nom, String desc, double precio, int cant) throws IOException {
        String line = String.join("|", "PRODUCTO_REGISTRAR", cod, nom, desc, String.valueOf(precio), String.valueOf(cant));
        String resp = send(line);
        if (resp.startsWith("OK|")) return new Resultado(true, "Registrado");
        return new Resultado(false, resp);
    }

    public java.util.List<ProductoItem> productoListar() throws IOException {
        String resp = send("PRODUCTO_LISTAR");
        if (!resp.startsWith("OK|")) throw new IOException("Respuesta inesperada: " + resp);
        String payload = resp.substring("OK|".length());
        java.util.List<ProductoItem> out = new java.util.ArrayList<>();
        if (payload.isBlank()) return out;

        for (String item : payload.split(";")) {
            if (item.isBlank()) continue;
            if (item.startsWith("PROD|")) item = item.substring("PROD|".length());
            String[] f = item.split("\\^", -1);
            if (f.length < 5) continue;
            ProductoItem pi = new ProductoItem(f[0], f[1], f[2],
                    Double.parseDouble(f[3]),
                    Integer.parseInt(f[4]));
            out.add(pi);
        }
        return out;
    }

    public static class ProductoItem {
        public final String codigo, nombre, descripcion;
        public final double precio;
        public final int cantidad;
        public ProductoItem(String c, String n, String d, double p, int cant){
            this.codigo=c; this.nombre=n; this.descripcion=d; this.precio=p; this.cantidad=cant;
        }
    }
}
