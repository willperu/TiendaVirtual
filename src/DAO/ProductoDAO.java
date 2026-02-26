package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import tiendavirtual.ConexionDB;
import tiendavirtual.Producto;

public class ProductoDAO {

    public List<Producto> obtenerTodos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM Productos";

        try (Connection con = ConexionDB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Producto p = new Producto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getInt("stock")
                );
                lista.add(p);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener productos: " + e.getMessage());
        }

        return lista;
    }
    
    //Buscar por ID
    public Producto buscarPorId(int id) {

    String sql = """
        SELECT id, nombre, precio, stock
        FROM Productos
        WHERE id = ?
    """;

    try (
        Connection con = ConexionDB.getConnection();
        PreparedStatement ps = con.prepareStatement(sql)
    ) {

        ps.setInt(1, id);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new Producto(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getDouble("precio"),
                    rs.getInt("stock")
                );
            }
        }

        } catch (SQLException e) {
            System.out.println("❌ Error al buscar producto por ID (" + id + "): " + e.getMessage());
        }

        return null; // no encontrado
    }

    // Buscar por Nombre
    public Producto buscarPorNombre(String nombre) {
        String sql = "SELECT * FROM Productos WHERE nombre = ?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Producto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getInt("stock")
                );
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al buscar producto por nombre: " + e.getMessage());
        }
        return null;
    }
    
    // Insertar Producto
    public boolean insertar(Producto p) {
        String sql = "INSERT INTO Productos (nombre, precio, stock) VALUES (?, ?, ?)";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getNombre());
            ps.setDouble(2, p.getPrecio());
            ps.setInt(3, p.getStock());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al insertar producto: " + e.getMessage());
            return false;
        }
    }
    
    // Metodo para Descontar Stock
    public boolean descontarStock(int id, int cantidad) {
        String sql = "UPDATE productos SET stock = stock - ? WHERE id = ? AND stock >= ?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, cantidad);
            ps.setInt(2, id);
            ps.setInt(3, cantidad);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al descontar stock: " + e.getMessage());
            return false;
        }
    }
    
    //Método para sumar stock
    public boolean sumarStock(int id, int cantidad) {
        String sql = "UPDATE Productos SET stock = stock + ? WHERE id = ?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, cantidad);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al sumar stock: " + e.getMessage());
            return false;
        }
    }
    
    // Metodo para actualizar Stock
    public boolean actualizarStock(int id, int stock) {
    String sql = "UPDATE productos SET stock = ? WHERE id = ?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, stock);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 🔒 Método privado para mapear ResultSet → Producto
    private Producto mapearProducto(ResultSet rs) throws SQLException {
        return new Producto(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getDouble("precio"),
            rs.getInt("stock")
        );
    }   
    
}

