package test;

import tiendavirtual.Producto;
import tiendavirtual.Carrito;
import tiendavirtual.Usuario;
import servicios.ServicioVentas;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SimulacionConcurrente {

    public static void main(String[] args) {

        // ===== Crear producto limitado =====
        Producto producto = new Producto();
        producto.setId(1);
        producto.setNombre("Producto Único");
        producto.setPrecio(100.0);
        producto.setStock(5); // stock inicial
        AtomicInteger stockRestante = new AtomicInteger(producto.getStock());

        // ===== Crear servicio de ventas simulado =====
        ServicioVentas servicioVentas = new ServicioVentas(null, null); // DAOs null, solo lógica

        // ===== Crear clientes ficticios =====
        int numClientes = 10;
        List<Integer> clientesQueCompraron = new ArrayList<>();
        List<Integer> clientesQueNoCompraron = new ArrayList<>();

        List<Thread> hilos = new ArrayList<>();

        for (int i = 1; i <= numClientes; i++) {
            final int idCliente = 100 + i;
            Thread t = new Thread(() -> {
                Carrito carrito = new Carrito();

                // Usar constructor por defecto y setters
                Usuario usuario = new Usuario();
                usuario.setId(idCliente);
                usuario.setUsuario("Cliente" + idCliente);
                usuario.setRol("CLIENTE");

                // Intentar agregar al carrito
                boolean agregado;
                synchronized (stockRestante) { // bloqueo para evitar race condition
                    if (stockRestante.get() >= 1) {
                        carrito.agregarProductoUnificado(producto, 1);
                        stockRestante.decrementAndGet();
                        agregado = true;
                    } else {
                        agregado = false;
                    }
                }

                if (agregado) {
                    // "Finalizar compra" solo en memoria
                    boolean comprado = servicioVentas.finalizarCompraSimulado(carrito, usuario);
                    if (comprado) {
                        synchronized (clientesQueCompraron) {
                            clientesQueCompraron.add(idCliente);
                        }
                    } else {
                        synchronized (clientesQueNoCompraron) {
                            clientesQueNoCompraron.add(idCliente);
                        }
                    }
                } else {
                    synchronized (clientesQueNoCompraron) {
                        clientesQueNoCompraron.add(idCliente);
                    }
                }
            });

            hilos.add(t);
        }

        // ===== Ejecutar hilos =====
        hilos.forEach(Thread::start);
        hilos.forEach(t -> {
            try { t.join(); } catch (InterruptedException e) { e.printStackTrace(); }
        });

        // ===== Resultados finales =====
        System.out.println("\n===== RESULTADOS FINALES =====");
        System.out.println("Clientes que compraron: " + clientesQueCompraron);
        System.out.println("Clientes que NO pudieron comprar: " + clientesQueNoCompraron);
        System.out.println("Stock final de '" + producto.getNombre() + "': " + stockRestante.get());
        System.out.println("Simulación terminada ✅");
    }
}
