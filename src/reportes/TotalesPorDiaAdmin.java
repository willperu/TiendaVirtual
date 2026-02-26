package reportes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import tiendavirtual.Usuario;
import DAO.VentaDAO;

public class TotalesPorDiaAdmin {

    private static final VentaDAO ventaDAO = new VentaDAO();

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("es", "ES"));

    public static void mostrarTotalesPorDiaAdmin(Usuario usuarioLogueado) {

        if (!usuarioLogueado.getRol().equalsIgnoreCase("ADMIN")) {
            System.out.println("❌ Acceso denegado. Solo administradores.");
            return;
        }

        Scanner sc = new Scanner(System.in);
        DateTimeFormatter entrada = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        System.out.print("Ingrese fecha (DD/MM/YYYY): ");
        LocalDate fecha = LocalDate.parse(sc.nextLine(), entrada);

        Map<LocalDate, Double> totales =
                ventaDAO.obtenerTotalesPorDia(fecha, fecha);

        if (totales.isEmpty()) {
            System.out.println("📭 No hay ventas en ese período.");
            return;
        }

        System.out.println("====================================");
        System.out.println("===== TOTALES POR DÍA =====");
        System.out.println("====================================");

        for (Map.Entry<LocalDate, Double> entry : totales.entrySet()) {
            System.out.printf("%s → $%.2f%n",
                    entry.getKey().format(FORMATO_FECHA),
                    entry.getValue());
        }
    }
}
 
