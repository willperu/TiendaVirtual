package DAO;

 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.util.List;
 import java.util.ArrayList;
 import java.sql.SQLException;
 import tiendavirtual.ConexionDB;
 import tiendavirtual.Usuario;


public class UsuarioDAO {
        
    public Usuario login(String usuario) {  // ya no necesitamos password aquí
    String sql = "SELECT * FROM usuarios WHERE usuario = ?";

        try (
            Connection conn = ConexionDB.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, usuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setUsuario(rs.getString("usuario"));
                u.setRol(rs.getString("rol"));  // ⚡ importante
                return u;
            }

        } catch (Exception e) {
            System.out.println("Error en login");
            e.printStackTrace();
        }

        return null;
    }
    
    //Metodo Listar Usuarios
    public List<Usuario> listarUsuarios() {
        List<Usuario> lista = new ArrayList<>();

        String sql = "SELECT id, usuario FROM usuarios ORDER BY id";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setUsuario(rs.getString("usuario"));                
                lista.add(u);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}
