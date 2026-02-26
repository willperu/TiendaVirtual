package reportes;

import tiendavirtual.Venta;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.time.LocalDate;
import java.time.YearMonth;

public class ExportadorCSV {

    public static void exportarVentasPorFecha(List<Venta> ventas, String rutaArchivo) {

        if (ventas.isEmpty()) {
            System.out.println("No hay datos para exportar.");
            return;
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaArchivo))) {

            pw.println("ID,Fecha,Total");

            for (Venta v : ventas) {
                pw.printf("%d,%s,%.2f%n",
                        v.getId(),
                        v.getFecha(),
                        v.getTotal());
            }

            System.out.println("✅ Archivo CSV exportado correctamente → " + rutaArchivo);

        } catch (Exception e) {
           // System.out.println("❌ Error al exportar CSV: " + e.getMessage());
           throw new RuntimeException("Error exportando CSV", e);
        }
    }
    // Metodo <Exportar Totales por Dia CVS
    public static void exportarTotalesPorDia(Map<LocalDate, Double> datos, String rutaArchivo) {

        if (datos.isEmpty()) {
            System.out.println("No hay datos para exportar.");
            return;
        }

        try (PrintWriter pw = new PrintWriter(
            new OutputStreamWriter( new FileOutputStream(rutaArchivo),
                    StandardCharsets.UTF_8))) {

            pw.println("fecha,total");

            for (Map.Entry<LocalDate, Double> entry : datos.entrySet()) {
                pw.printf(Locale.US, "%s,%.2f%n",
                        entry.getKey(),
                        entry.getValue());
            }

            System.out.println("CSV generado correctamente → " + rutaArchivo);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Exportar totales por mes
    public static void exportarTotalesPorMes(Map<YearMonth, Double> datos, String rutaArchivo) {

        if (datos.isEmpty()) {
            System.out.println("No hay datos para exportar.");
            return;
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaArchivo))) {

            pw.println("mes,total");

            for (Map.Entry<YearMonth, Double> entry : datos.entrySet()) {
                pw.printf("%s,%.2f%n",
                        entry.getKey(),
                        entry.getValue());
            }

            System.out.println("✅ CSV mensual generado → " + rutaArchivo);

        } catch (Exception e) {
            System.out.println("❌ Error exportando CSV: " + e.getMessage());
        }
    }
}
