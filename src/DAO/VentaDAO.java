package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.Map;
import java.util.LinkedHashMap;
import java.sql.Date;
import java.time.YearMonth;
import tiendavirtual.Carrito;
import tiendavirtual.ConexionDB;
import tiendavirtual.DetalleVenta;
import tiendavirtual.ItemCarrito;
import tiendavirtual.Producto;
import tiendavirtual.ResumenVentas;
import tiendavirtual.Venta;


public class VentaDAO {
    public int guardarVenta(double total) {

        String sql = "INSERT INTO Ventas (total) VALUES (?)";

        try (
            Connection conn = ConexionDB.getConnection();
            PreparedStatement stmt = conn.prepareStatement
            (sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ) {
            stmt.setDouble(1, total);
            stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1); // ID de la venta
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

        return -1;
    }
    //Metodo Guardar Detalle
    public void guardarDetalle(
        int ventaId,
        int productoId,
        int cantidad,
        double precio
    ) {
        String sql = """
            INSERT INTO detalle_venta
            (venta_id, producto_id, cantidad, precio)
            VALUES (?, ?, ?, ?)
        """;

        try (
            Connection conn = ConexionDB.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
        ) {

            stmt.setInt(1, ventaId);
            stmt.setInt(2, productoId);
            stmt.setInt(3, cantidad);
            stmt.setDouble(4, precio);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    } 
        
    //Metodo obtener todas
    public List<Venta> obtenerTodas() {
    List<Venta> ventas = new ArrayList<>();

    String sql = "SELECT id, fecha, total FROM Ventas ORDER BY fecha DESC";

        try (
            Connection conn = ConexionDB.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
        ) {
            while (rs.next()) {
                Venta v = new Venta(
                    rs.getInt("id"),
                    rs.getTimestamp("fecha"),
                    rs.getDouble("total")
                );                
                
                ventas.add(v);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ventas;
    }

    //Metodo obtener detalles
    
    public List<DetalleVenta> obtenerDetalles(int ventaId) {
        List<DetalleVenta> detalles = new ArrayList<>();

        String sql = """
              SELECT
                  p.id AS id,
                  p.nombre,
                  SUM(d.cantidad) AS cantidad,
                  d.precio,
                  SUM(d.cantidad * d.precio) AS subtotal
              FROM detalle_venta d
              JOIN productos p ON d.producto_id = p.id
              WHERE d.venta_id = ?
              GROUP BY p.id, p.nombre, d.precio
              """; 
                  
        try (
            Connection conn = ConexionDB.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            stmt.setInt(1, ventaId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                // Creamos el Producto
                Producto producto = new Producto();
                producto.setId(rs.getInt("id"));        // ahora sí funciona gracias a AS id
                producto.setNombre(rs.getString("nombre"));
                producto.setPrecio(rs.getDouble("precio"));

                int cantidad = rs.getInt("cantidad");
                double subtotal = rs.getDouble("subtotal");  // ya calculado en SQL

                // Creamos DetalleVenta con cantidad y subtotal
                DetalleVenta d = new DetalleVenta(producto, cantidad);
                d.setSubtotal(subtotal);
                detalles.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return detalles;
    }
    
    // Metodo Registrar venta
    public boolean registrarVenta(Carrito carrito,ProductoDAO productoDAO) {

    String sqlVenta = "INSERT INTO Ventas (total) VALUES (?)";
    String sqlDetalle = """
        INSERT INTO DetalleVenta
        (venta_id, producto_id, cantidad, precio)
        VALUES (?, ?, ?, ?)
    """;

    Connection conn = null;

    try {
        conn = ConexionDB.getConnection();
        conn.setAutoCommit(false); // 🔒 INICIA TRANSACCIÓN

        // 1️⃣ Guardar venta
        PreparedStatement psVenta = conn.prepareStatement(
            sqlVenta,
            PreparedStatement.RETURN_GENERATED_KEYS
        );
        psVenta.setDouble(1, carrito.getTotal());
        psVenta.executeUpdate();

        ResultSet rs = psVenta.getGeneratedKeys();
        if (!rs.next()) {
            throw new SQLException("No se pudo obtener ID de la venta");
        }

        int ventaId = rs.getInt(1);

        // 2️⃣ Guardar detalles + descontar stock
        PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle);

        for (ItemCarrito item : carrito.getItems()) {

            // detalle
            psDetalle.setInt(1, ventaId);
            psDetalle.setInt(2, item.getProducto().getId());
            psDetalle.setInt(3, item.getCantidad());
            psDetalle.setDouble(4, item.getProducto().getPrecio());
            psDetalle.executeUpdate();

            // stock
            boolean ok = productoDAO.descontarStock(
                item.getProducto().getId(),
                item.getCantidad()
            );

            if (!ok) {
                throw new SQLException("Error al descontar stock");
            }
        }

        conn.commit(); // ✅ TODO OK
        carrito.vaciar();
        return true;

        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback(); // ❌ DESHACE TODO
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println("❌ Venta cancelada: " + e.getMessage());
            return false;

        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }    
    
    // Metodo registrarVentaCompleta
    public boolean registrarVentaCompleta(
        int usuarioId,
        List<ItemCarrito> items,
        double total
    ) {
      
    String sqlVenta = "INSERT INTO ventas (total, usuario_id) VALUES (?, ?)";
    
           
    String sqlDetalle = """
        INSERT INTO detalle_venta (venta_id, producto_id, cantidad, precio)
        VALUES (?, ?, ?, ?)
        """;
    String sqlStock = "UPDATE productos SET stock = stock - ? WHERE id = ?";

    try (Connection conn = ConexionDB.getConnection()) {

        // 🔐 Iniciar transacción
        conn.setAutoCommit(false);
        // valicacion
        String sqlCheckStock = "SELECT stock FROM productos WHERE id = ? FOR UPDATE";

        for (ItemCarrito item : items) {

            try (PreparedStatement stmtCheck = conn.prepareStatement(sqlCheckStock)) {

                stmtCheck.setInt(1, item.getProducto().getId());
                ResultSet rs = stmtCheck.executeQuery();

                if (!rs.next()) {
                    conn.rollback();
                    System.out.println("❌ Producto no existe ID: " + item.getProducto().getId());
                    return false;
                }

                int stockActual = rs.getInt("stock");

                if (item.getCantidad() > stockActual) {
                    conn.rollback();
                    System.out.println("❌ Stock insuficiente para producto: "
                        + item.getProducto().getNombre());
                    return false;
                }
            }
        }   
                
        int ventaId;

        // 1️⃣ Guardar venta
        try (PreparedStatement stmtVenta =
            conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {

            stmtVenta.setDouble(1, total);
            stmtVenta.setInt(2, usuarioId); 
            stmtVenta.executeUpdate();

            ResultSet rs = stmtVenta.getGeneratedKeys();
            if (!rs.next()) {
                conn.rollback();
                return false;
            }
            ventaId = rs.getInt(1);
        }

        // 2️⃣ Guardar detalles
        for (ItemCarrito item : items) {
            try (PreparedStatement stmtDetalle = conn.prepareStatement(sqlDetalle)) {
                stmtDetalle.setInt(1, ventaId);
                stmtDetalle.setInt(2, item.getProducto().getId());
                stmtDetalle.setInt(3, item.getCantidad());
                stmtDetalle.setDouble(4, item.getProducto().getPrecio());
                stmtDetalle.executeUpdate();
            }
        }

        // 3️⃣ Descontar stock
        for (ItemCarrito item : items) {
            try (PreparedStatement stmtStock = conn.prepareStatement(sqlStock)) {
                stmtStock.setInt(1, item.getCantidad());
                stmtStock.setInt(2, item.getProducto().getId());
                stmtStock.executeUpdate();
            }
        }

        // ✅ Confirmar todo
        conn.commit();
        return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Metodo Obter Ventas  por Usuario
    public List<Venta> obtenerVentasPorUsuario(int usuarioId) {
    List<Venta> ventas = new ArrayList<>();

    String sql = """
        SELECT id, fecha, total
        FROM ventas
        WHERE usuario_id = ?
        ORDER BY fecha DESC
        """;

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
            Venta venta = new Venta(
                rs.getInt("id"),
                rs.getTimestamp("fecha"),
                rs.getDouble("total")
            );
            ventas.add(venta);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ventas;
    }
    
    // Metodo Obtener Ventas Por Fecha Admin
    public List<Venta> obtenerVentasPorFecha(LocalDate inicio, LocalDate fin) {

        List<Venta> ventas = new ArrayList<>();

        String sql = """
            SELECT id, fecha, total, usuario_id
            FROM ventas
            WHERE DATE(fecha) BETWEEN ? AND ?
            ORDER BY fecha
        """;

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(inicio));
            ps.setDate(2, Date.valueOf(fin));

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Venta v = new Venta(
                    rs.getInt("id"),
                    rs.getTimestamp("fecha"),
                    rs.getDouble("total")
                );
                ventas.add(v);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ventas;
    }
    
    // Metodo Obtener Total por Fecha
    public double obtenerTotalPorFecha(LocalDate inicio, LocalDate fin) {

        String sql = """
            SELECT SUM(total) AS total_periodo
            FROM ventas
            WHERE DATE(fecha) BETWEEN ? AND ?
        """;

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(inicio));
            ps.setDate(2, java.sql.Date.valueOf(fin));

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total_periodo");
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al calcular total por fecha");
            e.printStackTrace();
        }

        return 0;
    }
    
    // Metodo obtener totales por Dia
    public Map<LocalDate, Double> obtenerTotalesPorDia(LocalDate inicio, LocalDate fin) {

        Map<LocalDate, Double> totales = new LinkedHashMap<>();

        String sql = """
            SELECT DATE(fecha) AS dia, SUM(total) AS total_dia
            FROM ventas
            WHERE DATE(fecha) BETWEEN ? AND ?
            GROUP BY DATE(fecha)
            ORDER BY dia
        """;

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(inicio));
            ps.setDate(2, java.sql.Date.valueOf(fin));

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                LocalDate dia = rs.getDate("dia").toLocalDate();
                double total = rs.getDouble("total_dia");
                totales.put(dia, total);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totales;
    }
    
    // Metodo Obtener Totales por Mes
    public Map<YearMonth, Double> obtenerTotalesPorMes(LocalDate inicio, LocalDate fin) {

        Map<YearMonth, Double> totales = new LinkedHashMap<>();
    
        String sql = """
            SELECT 
                YEAR(fecha) AS anio,
                MONTH(fecha) AS mes,
                SUM(total) AS total_mes
            FROM ventas
            WHERE DATE(fecha) BETWEEN ? AND ?
            GROUP BY YEAR(fecha), MONTH(fecha)
            ORDER BY anio, mes
        """;

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(inicio));
            ps.setDate(2, java.sql.Date.valueOf(fin));

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int anio = rs.getInt("anio");
                int mes = rs.getInt("mes");
                double total = rs.getDouble("total_mes");

                YearMonth yearMonth = YearMonth.of(anio, mes);
                totales.put(yearMonth, total);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totales;
    }    
    
    // Metodo Obtener Resumen Periodo
    public ResumenVentas consultarResumenPeriodo(LocalDate inicio, LocalDate fin) {

        String sql = """
            SELECT 
                COUNT(*) AS cantidad_ventas,
                SUM(total) AS total_vendido,
                AVG(total) AS promedio_venta
            FROM ventas
            WHERE DATE(fecha) BETWEEN ? AND ?
        """;

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(inicio));
            ps.setDate(2, java.sql.Date.valueOf(fin));

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new ResumenVentas(
                    rs.getInt("cantidad_ventas"),
                    rs.getDouble("total_vendido"),
                    rs.getDouble("promedio_venta")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    
    // Metodo Obtener Dia con Mayor Venta
    public Map.Entry<LocalDate, Double> obtenerDiaConMayorVenta(LocalDate inicio, LocalDate fin) {

        String sql = """
            SELECT DATE(fecha) AS dia, SUM(total) AS total_dia
            FROM ventas
            WHERE DATE(fecha) BETWEEN ? AND ?
            GROUP BY DATE(fecha)
            ORDER BY total_dia DESC
            LIMIT 1
        """;

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(inicio));
            ps.setDate(2, java.sql.Date.valueOf(fin));

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                LocalDate dia = rs.getDate("dia").toLocalDate();
                double total = rs.getDouble("total_dia");
                return Map.entry(dia, total);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
            
        // Metodo Obter Total Venta
        public double obtenerTotalVenta(int ventaId) {
        double total = -1;    
        String sql = "SELECT total FROM ventas WHERE id = ?";        

            try (Connection conn = ConexionDB.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, ventaId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    total = rs.getDouble("total");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

           return total;
        }
        
        // Metodo Venta Pertenece a Usuario
        public boolean ventaPerteneceAUsuario(int ventaId, int usuarioId) {
        String sql = "SELECT id FROM ventas WHERE id = ? AND usuario_id = ?";

            try (Connection conn = ConexionDB.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, ventaId);
                ps.setInt(2, usuarioId);

                ResultSet rs = ps.executeQuery();
                return rs.next();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return false;
        }
        
        // Metodo Obtener Producto mas Vendido
        public Map<String, Integer> consultarProductosMasVendidos(
        LocalDate inicio, LocalDate fin) {

        Map<String, Integer> resultado = new LinkedHashMap<>();

        String sql = """
            SELECT p.nombre, SUM(dv.cantidad) AS total_vendido
            FROM detalle_venta dv
            JOIN productos p ON dv.producto_id = p.id
            JOIN ventas v ON dv.venta_id = v.id
            WHERE DATE(v.fecha) BETWEEN ? AND ?
            GROUP BY p.nombre
            ORDER BY total_vendido DESC
        """;

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(inicio));
            ps.setDate(2, java.sql.Date.valueOf(fin));

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String nombre = rs.getString("nombre");
                int total = rs.getInt("total_vendido");
                resultado.put(nombre, total);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultado;
    }
        
     // Metodo insertar ventas demo   
    public boolean insertarVentasDemo() {

        String sqlVenta = "INSERT INTO ventas (total, usuario_id) VALUES (?, ?)";
        String sqlDetalle = """
            INSERT INTO detalle_venta (venta_id, producto_id, cantidad, precio)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = ConexionDB.getConnection()) {

            conn.setAutoCommit(false);

            int usuarioDemo = 1; // puedes cambiarlo

            for (int i = 1; i <= 3; i++) {

                // crear venta
                int ventaId;

                try (PreparedStatement ps = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setDouble(1, 100 * i);
                    ps.setInt(2, usuarioDemo);
                    ps.executeUpdate();

                    ResultSet rs = ps.getGeneratedKeys();
                    rs.next();
                    ventaId = rs.getInt(1);
                }

                // agregar detalles
                for (int prodId = 1; prodId <= 3; prodId++) {

                    try (PreparedStatement ps = conn.prepareStatement(sqlDetalle)) {
                        ps.setInt(1, ventaId);
                        ps.setInt(2, prodId);
                        ps.setInt(3, i);
                        ps.setDouble(4, 10 * prodId);
                        ps.executeUpdate();
                    }
                }
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }           
    
}
