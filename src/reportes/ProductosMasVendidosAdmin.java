package reportes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.Map;
import java.util.Locale;
import java.util.Scanner;

import DAO.VentaDAO;
import tiendavirtual.Usuario;

import servicios.ServicioReportesVentas;



public class ProductosMasVendidosAdmin {
    
   private static final DateTimeFormatter FORMATO_FECHA =
        DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("es", "ES")); 

        private static final VentaDAO ventaDAO = new VentaDAO();
        private static final ServicioReportesVentas servicio =
            new ServicioReportesVentas(ventaDAO);
   

    public static void mostrarProductosMasVendidosAdmin(Usuario usuarioLogueado) {

        if (!usuarioLogueado.getRol().equalsIgnoreCase("ADMIN")) {
            System.out.println("❌ Acceso denegado. Solo administradores.");
            return;
        }

        Scanner sc = new Scanner(System.in);
        DateTimeFormatter entrada = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        System.out.print("Ingrese fecha inicio (DD/MM/YYYY): ");
        LocalDate inicio = LocalDate.parse(sc.nextLine(), entrada);

        System.out.print("Ingrese fecha fin (DD/MM/YYYY): ");
        LocalDate fin = LocalDate.parse(sc.nextLine(), entrada);

        Map<String, Integer> productos =
                servicio.obtenerProductosMasVendidos(inicio, fin);

        if (productos.isEmpty()) {
            System.out.println("📭 No hay ventas en ese período.");
            return;
        }

        System.out.println("====================================");
        System.out.println("🏆 PRODUCTOS MÁS VENDIDOS");
        System.out.println("====================================");
        System.out.println("Período: " + inicio.format(FORMATO_FECHA) +
                           " → " + fin.format(FORMATO_FECHA));
        System.out.println();

        int pos = 1;
        for (Map.Entry<String, Integer> entry : productos.entrySet()) {
            int cantidad = entry.getValue();
            String unidad = (cantidad == 1) ? "unidad" : "unidades";

            System.out.printf("%d️⃣ %-15s → %d %s%n",
                    pos++, entry.getKey(), cantidad, unidad);
        }
    }
}

