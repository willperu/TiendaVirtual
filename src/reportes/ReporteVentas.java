package reportes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import tiendavirtual.Usuario;
import tiendavirtual.ResumenVentas;
import DAO.VentaDAO;


public class ReporteVentas {

    /**
     * Muestra un resumen de ventas para un período dado.
     * Este método no solicita fechas ni interactúa con la consola más allá de imprimir resultados.
     *
     * @param usuario Usuario que realiza la consulta (para validar permisos)
     * @param ventaDAO DAO para acceder a ventas
     * @param formatoFecha Formato para mostrar fechas
     * @param inicio Fecha de inicio del período
     * @param fin Fecha de fin del período
     */
    public static void mostrarResumenPeriodoAdmin(
            Usuario usuario,
            VentaDAO ventaDAO,
            DateTimeFormatter formatoFecha,
            LocalDate inicio,
            LocalDate fin) {

        // Validar permisos
        if (!usuario.getRol().equalsIgnoreCase("ADMIN")) {
            System.out.println("❌ Acceso denegado. Solo administradores.");
            return;
        }

        // Validar rango de fechas
        if (fin.isBefore(inicio)) {
            System.out.println("❌ La fecha fin no puede ser anterior a la fecha inicio.");
            return;
        }

        // Obtener resumen del período
        ResumenVentas resumen = ventaDAO.consultarResumenPeriodo(inicio, fin);
        if (resumen == null) {
            System.out.println("📭 No hay ventas en ese período.");
            return;
        }

        // Día con mayor venta
        Map.Entry<LocalDate, Double> mejorDia = ventaDAO.obtenerDiaConMayorVenta(inicio, fin);

        // Imprimir resumen
        System.out.println();
        System.out.println("====================================");
        System.out.println("📊 RESUMEN DEL PERÍODO");
        System.out.println("====================================");
        System.out.println("Período: " + inicio.format(formatoFecha) +
                           " → " + fin.format(formatoFecha));
        System.out.println();

        System.out.printf("%-25s $%.2f%n", "Total vendido:", resumen.getTotalVendido());
        System.out.printf("%-25s %d%n", "Cantidad de ventas:", resumen.getCantidadVentas());
        System.out.printf("%-25s $%.2f%n", "Promedio por venta:", resumen.getPromedioVenta());

        if (mejorDia != null) {
            System.out.printf("Día con mayor venta: %s ($%.2f)%n",
                    mejorDia.getKey().format(formatoFecha), mejorDia.getValue());
        }

        System.out.println("\nProductos vendidos en el período:");
        System.out.println("-------------------------------------");

        Map<String, Integer> productos = ventaDAO.consultarProductosMasVendidos(inicio, fin);

        if (productos.isEmpty()) {
            System.out.println("No hubo ventas en ese período.");
        } else {
            productos.forEach((nombre, cantidad) ->
                System.out.printf("%-25s %5d%n", nombre, cantidad)
            );
        }
    }
}
