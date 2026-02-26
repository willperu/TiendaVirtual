package reportes;

import servicios.ServicioVentas;
import tiendavirtual.Usuario;

import servicios.ServicioReportesVentas;

import java.time.LocalDate;
import java.util.Scanner;
import java.time.format.DateTimeFormatter;

public class ControladorReportes {

    // Ventas por fecha
    public static void ventasPorFecha(
            Usuario usuario,
            ServicioVentas servicio
        ) {

            if (!usuario.getRol().equalsIgnoreCase("ADMIN")) {
                System.out.println("❌ Acceso denegado. Solo administradores.");
                return;
            }

            Scanner sc = new Scanner(System.in);

            try {
                DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd/MM/yyyy");

                System.out.print("Fecha inicio (DD/MM/YYYY): ");
                LocalDate inicio = LocalDate.parse(sc.nextLine(), formatter);

                System.out.print("Fecha fin (DD/MM/YYYY): ");
                LocalDate fin = LocalDate.parse(sc.nextLine(), formatter);

                if (fin.isBefore(inicio)) {
                System.out.println("❌ La fecha fin no puede ser anterior a la fecha inicio.");
                return;
                }

                var ventas = servicio.obtenerVentasPorFecha(inicio, fin);
                double total = servicio.obtenerTotalPorFecha(inicio, fin);

                ReporteVentasPorFecha.mostrarVentasPorFechaAdmin(
                        usuario,
                        ventas,
                        total
                );

            } catch (Exception e) {
                System.out.println("❌ Formato inválido.");
            }
        }

        //Metodo exportar ventas por fecha
        public static void exportarVentasPorFecha(
            Usuario usuario,
            ServicioReportesVentas servicioReportes
        ) {
        if (!usuario.getRol().equalsIgnoreCase("ADMIN")) {
            System.out.println("❌ Acceso denegado. Solo administradores.");
            return;
        }

        Scanner sc = new Scanner(System.in);
        DateTimeFormatter formatoEntrada = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatoArchivo = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate inicio = null;
        LocalDate fin = null;

        // Pedir fecha inicio
        while (inicio == null) {
            try {
                System.out.print("Fecha inicio (DD/MM/YYYY): ");
                inicio = LocalDate.parse(sc.nextLine(), formatoEntrada);
            } catch (Exception e) {
                System.out.println("❌ Formato inválido. Intente de nuevo.");
            }
        }

        // Pedir fecha fin
        while (fin == null) {
            try {
                System.out.print("Fecha fin (DD/MM/YYYY): ");
                fin = LocalDate.parse(sc.nextLine(), formatoEntrada);
                if (fin.isBefore(inicio)) {
                    System.out.println("❌ La fecha fin no puede ser anterior a la fecha inicio.");
                    fin = null;
                }
            } catch (Exception e) {
                System.out.println("❌ Formato inválido. Intente de nuevo.");
            }
        }

        // Pedir nombre base del archivo
        System.out.print("Nombre archivo CSV: ");
        String nombreArchivoBase = sc.nextLine().trim().replace(" ", "_");

        // Generar nombre final con fechas
        String nombreArchivoFinal = nombreArchivoBase + "_" + inicio.format(formatoArchivo) 
                                    + "_a_" + fin.format(formatoArchivo) + ".csv";

        // Llamar al servicio DAO para generar CSV
        boolean ok = servicioReportes.exportarVentasPorFechaCSV(inicio, fin, nombreArchivoFinal);

        if (ok) {
            System.out.println("✅ Archivo CSV exportado correctamente → " + nombreArchivoFinal);
        } else {
            System.out.println("❌ Error al generar el archivo CSV");
        }
    }
}
