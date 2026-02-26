
package reportes;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import tiendavirtual.Usuario;
import DAO.VentaDAO;

public class TotalesPorMesAdmin {

    private static final DateTimeFormatter FORMATO_MES =
            DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("es", "ES"));

    public static void mostrarTotalesPorMesAdmin(Usuario usuarioLogueado, VentaDAO ventaDAO) {

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

        Map<YearMonth, Double> totales = ventaDAO.obtenerTotalesPorMes(inicio, fin);

        if (totales.isEmpty()) {
            System.out.println("📭 No hay ventas en ese período.");
            return;
        }

        System.out.println("===== TOTALES POR MES =====");
        totales.forEach((mes, total) ->
                System.out.println(mes.format(FORMATO_MES) + " → $" + String.format("%.2f", total))
        );
    }
}

