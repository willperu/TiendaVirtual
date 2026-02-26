package reportes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import tiendavirtual.Usuario;
import tiendavirtual.Venta;
import tiendavirtual.DetalleVenta;

import DAO.UsuarioDAO;
import DAO.VentaDAO;


public class VentasPorUsuarioAdmin {
   public static void mostrarVentasPorUsuarioAdmin(
        Usuario usuarioLogueado,
        VentaDAO ventaDAO,
        Scanner sc){

        if (!usuarioLogueado.getRol().equalsIgnoreCase("ADMIN")) {
            System.out.println("❌ Acceso denegado. Solo administradores.");
            return;
        }

        //Scanner sc = new Scanner(System.in);
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        List<Usuario> usuarios = usuarioDAO.listarUsuarios();

        if (usuarios.isEmpty()) {
            System.out.println("📭 No hay usuarios registrados.");
            return;
        }

        System.out.println("\n===== USUARIOS REGISTRADOS =====");
        System.out.printf("%-5s %-15s\n", "ID", "Usuario");
        System.out.println("---------------------------");

        for (Usuario u : usuarios) {
            System.out.printf("%-5d %-15s\n", u.getId(), u.getUsuario());
        }

        System.out.print("Ingrese el ID del usuario: ");
        int usuarioId = Integer.parseInt(sc.nextLine());
        
        String nombreUsuario = "";

        for (Usuario u : usuarios) {
            if (u.getId() == usuarioId) {
                nombreUsuario = u.getUsuario();
                break;
            }
        }

        if (nombreUsuario.isEmpty()) {
            System.out.println("❌ Usuario no encontrado.");
            return;
        }        

        List<Venta> ventas = ventaDAO.obtenerVentasPorUsuario(usuarioId);

        if (ventas.isEmpty()) {
            System.out.println("ℹ️ Este usuario no tiene ventas.");
            return;
        }

        System.out.println("\n===== VENTAS DEL USUARIO " + nombreUsuario + " =====");

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Venta v : ventas) {
            LocalDateTime fecha = v.getFecha().toLocalDateTime();

            System.out.println(
                    "ID: " + v.getId() +
                    " | Fecha: " + fecha.format(formatter) +
                    " | Total: $" + v.getTotal()
            );
        }
        
        int ventaId;

        do {
            System.out.print("\nIngrese ID de la venta para ver detalle (0 para volver): ");

            try {
                ventaId = Integer.parseInt(sc.nextLine());

                if (ventaId == 0)
                    break;

                double total = ventaDAO.obtenerTotalVenta(ventaId);

                if (total == -1) {
                    System.out.println("❌ No existe una venta con ese ID.");
                } else {
                    mostrarDetalleVenta(ventaId, ventaDAO);
                }

            } catch (NumberFormatException e) {
                System.out.println("⚠️ Debes ingresar un número válido.");
                ventaId = -1;
            }

        } while (ventaId != 0);                  
        
    }
   
   // Metodo Mostrar Detalle Venta
   private static void mostrarDetalleVenta(int ventaId, VentaDAO ventaDAO) {

    List<DetalleVenta> detalles = ventaDAO.obtenerDetalles(ventaId);

        if (detalles.isEmpty()) {
            System.out.println("⚠️ No se encontraron detalles para esta venta.");
            return;
        }

        System.out.println("\n===== DETALLE DE VENTA " + ventaId + " =====");

        System.out.printf("%-15s %-8s %-15s %-10s%n",
                "Producto", "Cantidad", "Precio Unitario", "Subtotal");
        System.out.println("------------------------------------------------");

        for (DetalleVenta d : detalles) {
            System.out.printf("%-15s %-8d $%-14.2f $%.2f%n",
                    d.getProducto().getNombre(),
                    d.getCantidad(),
                    d.getProducto().getPrecio(),
                    d.getSubtotal());
        }

        System.out.println("------------------------------------------------");
        System.out.printf("TOTAL: $%.2f%n", ventaDAO.obtenerTotalVenta(ventaId));
    }   
   
}
