package reportes;

import tiendavirtual.Usuario;
import tiendavirtual.Venta;
import java.util.List;

public class ReporteVentasPorFecha {

    /**
     * Muestra en consola las ventas filtradas por fecha.
     */
    public static void mostrarVentasPorFechaAdmin(Usuario usuario, List<Venta> ventas, double totalPeriodo) {

        if (!usuario.getRol().equalsIgnoreCase("ADMIN")) {
            System.out.println("❌ Acceso denegado. Solo administradores.");
            return;
        }

        if (ventas.isEmpty()) {
            System.out.println("📭 No hay ventas en ese período.");
            return;
        }

        System.out.println("\n===== VENTAS ENTRE FECHAS =====");
        for (Venta v : ventas) {
            System.out.printf(
                "ID: %d | Fecha: %s | Total: $%.2f%n",
                v.getId(), v.getFecha(), v.getTotal()
            );
        }

        System.out.println("----------------------------------");
        System.out.printf("TOTAL DEL PERÍODO: $%.2f%n", totalPeriodo);
    }
}

