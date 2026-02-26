package servicios;

import DAO.ProductoDAO;
import DAO.VentaDAO;

import java.time.LocalDate;
import java.util.List;

import tiendavirtual.Carrito;
import tiendavirtual.Producto;
import tiendavirtual.Venta;
import tiendavirtual.Usuario;


public class ServicioVentas {

    private final VentaDAO ventaDAO;
    private final ProductoDAO productoDAO;

    public ServicioVentas(VentaDAO ventaDAO, ProductoDAO productoDAO) {
        this.ventaDAO = ventaDAO;
        this.productoDAO = productoDAO;
    }
    
     public boolean registrarVenta(Carrito carrito) {
        validarCarrito(carrito);
        return ventaDAO.registrarVenta(carrito, productoDAO);
    }

    private void validarCarrito(Carrito carrito){
        if(carrito == null || carrito.getItems().isEmpty()){
            throw new IllegalArgumentException("El carrito está vacío");
        }
    }
    
    // 🔹 MÉTODO PRIVADO DE VALIDACIÓN
    private void validarFechas(LocalDate inicio, LocalDate fin) {
    if (inicio == null || fin == null)
        throw new IllegalArgumentException("Fechas nulas");

    if (fin.isBefore(inicio))
        throw new IllegalArgumentException("Rango inválido");
    }
    
    // 🔹 MÉTODO PÚBLICO DEL SERVICIO
    public List<Venta> obtenerVentasPorFecha(LocalDate inicio, LocalDate fin) {
        validarFechas(inicio, fin);
        return ventaDAO.obtenerVentasPorFecha(inicio, fin);
    }
  
    /**
     * Devuelve el total del período.
     */
    public double obtenerTotalPorFecha(LocalDate inicio, LocalDate fin) {
        return ventaDAO.obtenerTotalPorFecha(inicio, fin);
    }
    
    public ReporteVentasDTO obtenerReporteVentasPorFecha(LocalDate inicio, LocalDate fin) {

    List<Venta> ventas = ventaDAO.obtenerVentasPorFecha(inicio, fin);
    double total = ventaDAO.obtenerTotalPorFecha(inicio, fin);

    return new ReporteVentasDTO(ventas, total);
    }
    
     // Método seguro para agregar al carrito (test)
    public synchronized boolean agregarProductoAlCarrito(Carrito carrito, Producto producto, int cantidad) {
        Producto p = productoDAO.buscarPorId(producto.getId());

        if (p == null) return false; // Producto no existe

        int stockDisponible = p.getStock();
        int enCarrito = carrito.getCantidadProducto(p.getId());
        int totalSolicitado = enCarrito + cantidad;

        if (totalSolicitado > stockDisponible) {
            return false; // No hay suficiente stock
        }

        carrito.agregarProductoUnificado(p, cantidad);
        return true; // Agregado exitosamente
    }    
    

    // ✅ Método seguro para finalizar compra (test)
    public synchronized boolean finalizarCompra(Carrito carrito, int usuarioId) {
        if (carrito.estaVacio()) return false;

        // Verificar stock antes de registrar
        for (var item : carrito.getItems()) {
            Producto p = productoDAO.buscarPorId(item.getProducto().getId());
            if (p == null || item.getCantidad() > p.getStock()) return false;
        }

        // Registrar venta y disminuir stock
        boolean ok = ventaDAO.registrarVentaCompleta(usuarioId, carrito.getItems(), carrito.getTotal());
        if (ok) {
            for (var item : carrito.getItems()) {
                Producto p = productoDAO.buscarPorId(item.getProducto().getId());
                p.setStock(p.getStock() - item.getCantidad());
            }
            carrito.vaciar();
        }
        return ok;
    }
    
    
        // ===== MÉTODOS SIMULADOS PARA TEST =====

    /**
     * Simula agregar un producto al carrito, solo en memoria.
     */
    public boolean agregarProductoAlCarritoSimulado(Carrito carrito, Producto producto, int cantidad) {
        if (producto == null || cantidad <= 0) return false;

        int cantidadEnCarrito = carrito.getCantidadProducto(producto.getId());
        // Simulamos que no hay límite de stock real, o puedes controlar con producto.getStock()
        if (cantidadEnCarrito + cantidad > producto.getStock()) return false;

        carrito.agregarProductoUnificado(producto, cantidad);
        return true;
    }

    /**
     * Simula finalizar compra en memoria, sin tocar la base de datos.
     */
    public boolean finalizarCompraSimulado(Carrito carrito, Usuario usuario) {
        if (carrito.estaVacio()) {
            System.out.println("❌ " + usuario.getUsuario() + " no pudo comprar: carrito vacío");
            return false;
        }

        double total = carrito.getTotal();
        carrito.vaciar();

        System.out.println("✅ " + usuario.getUsuario() + " compró con éxito. Total: $" + total);
        return true;
    }     
}