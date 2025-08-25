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

    public java.util.List<ProductoDet> productoListar() throws java.io.IOException {
        String resp = send("PRODUCTO_LISTAR");
        if (!resp.startsWith("OK|")) throw new java.io.IOException(resp);

        String payload = resp.substring(3); // después de "OK|"
        java.util.List<ProductoDet> out = new java.util.ArrayList<>();
        if (payload.isBlank()) return out;

        for (String block : payload.split(";")) {
            if (block.isBlank()) continue;
            if (!block.startsWith("PROD|")) continue;
            String[] f = block.substring(5).split("\\^", -1);
            out.add(new ProductoDet(
                f[0],                   // codigo
                f[1],                    // nombre
                f[2],                     // desc
                Double.parseDouble(f[3]),  // precio
                Integer.parseInt(f[4])    // stock
            ));
        }
        out.sort(java.util.Comparator.comparing(p -> p.codigo));
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
    public static class ProductoDet {
        public final String codigo, nombre, descripcion;
        public final double precio;
        public final int stock;
        public ProductoDet(String c, String n, String d, double p, int s) {
            codigo=c; nombre=n; descripcion=d; precio=p; stock=s;
        }
    }

    public ProductoDet productoGet(String codigo) throws java.io.IOException {
        String resp = send("PRODUCTO_GET|" + codigo.trim().toUpperCase());
        if (!resp.startsWith("OK|PROD|")) throw new java.io.IOException(resp);
        String[] p = resp.substring("OK|PROD|".length()).split("\\^", -1);
        return new ProductoDet(p[0], p[1], p[2], Double.parseDouble(p[3]), Integer.parseInt(p[4]));
    }

    public static class ResultadoFactura {
        public final boolean ok; public final int id;
        public final String mensaje, textoBase64;
        public ResultadoFactura(boolean ok, int id, String msg, String b64){ this.ok=ok; this.id=id; this.mensaje=msg; this.textoBase64=b64; }
    }

    public ResultadoFactura facturaCrear(String clienteCed, String usuarioCed, java.util.List<String> itemsCodCant) throws java.io.IOException {
        String items = String.join(";", itemsCodCant); 
        String resp = send("FACTURA_CREAR|" + clienteCed + "|" + usuarioCed + "|" + items);
        if (resp.startsWith("OK|FACTURA|")) {
            String[] p = resp.split("\\|", 8);
            return new ResultadoFactura(true, Integer.parseInt(p[2]), "OK", p[7]); 
        }
        return new ResultadoFactura(false, -1, resp, null);
    }
    public boolean productoAjustar(String codigo, int delta) throws Exception {
        String r = send("PRODUCTO_AJUSTAR|" + codigo + "|" + delta);
        if (r.startsWith("OK|")) return true;
        if (r.startsWith("ERROR|STOCK_NEGATIVO")) throw new IllegalArgumentException("El stock no puede quedar negativo");
        throw new Exception(r);
    }

    public boolean productoEliminar(String codigo) throws Exception {
        String r = send("PRODUCTO_ELIMINAR|" + codigo);
        if (r.startsWith("OK|")) return true;
        if (r.startsWith("ERROR|TIENE_FACTURAS")) throw new IllegalStateException("No se puede eliminar: ya tiene facturas");
        if (r.startsWith("ERROR|NO_EXISTE")) throw new IllegalStateException("Código no existe");
        throw new Exception(r);
    }

    public java.util.List<ProductoDet> productoBuscarNombre(String patron) throws Exception {
        String r = send("PRODUCTO_BUSCAR_NOMBRE|" + patron);
        if (!r.startsWith("OK|")) throw new Exception(r);
        java.util.List<ProductoDet> out = new java.util.ArrayList<>();
        String body = r.substring(3);
        if (body.isBlank()) return out;
        for (String tok : body.split(";")) {
            if (!tok.startsWith("PROD|")) continue;
            String[] f = tok.substring(5).split("\\^", -1);
            out.add(new ProductoDet(f[0], f[1], f[2], Double.parseDouble(f[3]), Integer.parseInt(f[4])));
        }
        return out;
    }
    public static class ClienteDet {
        public final String cedula, nombre, apellidos, correo, tipoCedula, empresa;
        public ClienteDet(String ced, String nom, String ape, String cor, String tipo, String emp){
            this.cedula=ced; this.nombre=nom; this.apellidos=ape; this.correo=cor; this.tipoCedula=tipo; this.empresa=emp;
        }
        public boolean esJuridica(){ return "JURIDICA".equalsIgnoreCase(tipoCedula); }
        @Override public String toString(){
            return cedula + " | " + nombre + " " + apellidos + " | " + correo +
                   (esJuridica() ? " | " + empresa : "");
        }
    }
    public ClienteDet clienteGet(String cedula) throws Exception {
        String resp = send("CLIENTE_GET|" + cedula);
        if (resp.startsWith("OK|CLIENTE|")) {
            String[] t = resp.substring("OK|CLIENTE|".length()).split("\\^", -1);
            
            if (t.length >= 6) return new ClienteDet(t[0], t[1], t[2], t[3], t[4], t[5]);
            throw new IllegalStateException("FORMATO_CLIENTE_GET");
        }
        if (resp.startsWith("ERROR|NO_ENCONTRADO")) return null;
        throw new IllegalStateException(resp);
    }
    public java.util.List<ClienteDet> clienteListar() throws Exception {
        String resp = send("CLIENTE_LISTAR");
        if (!resp.startsWith("OK|")) throw new IllegalStateException(resp);
        java.util.List<ClienteDet> out = new java.util.ArrayList<>();
        String body = resp.substring("OK|".length());
        if (body.isBlank()) return out;
        for (String row : body.split(";", -1)) {
            if (row.isBlank()) continue;
            String[] t = row.replaceFirst("^CLI\\|", "").split("\\^", -1);
            if (t.length >= 6) out.add(new ClienteDet(t[0], t[1], t[2], t[3], t[4], t[5]));
        }
        return out;
    }
    public java.util.List<ClienteDet> clienteBuscarNombre(String nombre, String apellidos) throws Exception {
        String n = sanitize(nombre), a = sanitize(apellidos==null?"":apellidos);
        String resp = send("CLIENTE_BUSCAR_NOMBRE|" + n + "|" + a);
        if (!resp.startsWith("OK|")) throw new IllegalStateException(resp);
        java.util.List<ClienteDet> out = new java.util.ArrayList<>();
        String body = resp.substring("OK|".length());
        if (body.isBlank()) return out;
        for (String row : body.split(";", -1)) {
            if (row.isBlank()) continue;
            String[] t = row.replaceFirst("^CLI\\|", "").split("\\^", -1);
            if (t.length >= 6) out.add(new ClienteDet(t[0], t[1], t[2], t[3], t[4], t[5]));
        }
        return out;
    }
    public boolean clienteActualizar(String cedula, String nombre, String apellidos,
                                     String correo, String empresa) throws Exception {
        String payload = String.join("|",
                "CLIENTE_ACTUALIZAR",
                sanitize(cedula), sanitize(nombre), sanitize(apellidos),
                sanitize(correo), sanitize(empresa==null?"":empresa));
        String resp = send(payload);
        if (resp.startsWith("OK|ACTUALIZADO")) return true;
        if (resp.startsWith("ERROR|VALIDACION|")) throw new IllegalArgumentException(resp);
        throw new IllegalStateException(resp);
    }
    public boolean clienteEliminar(String cedula) throws Exception {
        String resp = send("CLIENTE_ELIMINAR|" + sanitize(cedula));
        if (resp.startsWith("OK|ELIMINADO")) return true;
        if (resp.contains("FACTURAS")) throw new IllegalStateException("CLIENTE_TIENE_FACTURAS");
        throw new IllegalStateException(resp);
    }
    private static String sanitize(String s){
        return s==null? "" : s.replace("|"," ").replace("^"," ").replace(";"," ");
    }
   
    
        
    
}
