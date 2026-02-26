package servicios;

import DAO.VentaDAO;
import tiendavirtual.ResumenVentas;
import tiendavirtual.Venta;

import reportes.ExportadorCSV;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public class ServicioReportesVentas {

    private final VentaDAO ventaDAO;

    public ServicioReportesVentas(VentaDAO ventaDAO) {
        this.ventaDAO = ventaDAO;
    }

    // ===============================
    // VALIDACIÓN DE RANGO DE FECHAS
    // ===============================
    private void validarFechas(LocalDate inicio, LocalDate fin) {
        if (inicio == null || fin == null)
            throw new IllegalArgumentException("Las fechas no pueden ser null");

        if (inicio.isAfter(fin))
            throw new IllegalArgumentException("La fecha inicio no puede ser mayor que fin");
    }

    // ===============================
    // REPORTE: VENTAS ENTRE FECHAS
    // ===============================
    public List<Venta> obtenerVentasPorFecha(LocalDate inicio, LocalDate fin) {
        validarFechas(inicio, fin);
        return ventaDAO.obtenerVentasPorFecha(inicio, fin);
    }

    // ===============================
    // REPORTE: TOTAL PERIODO
    // ===============================
    public double obtenerTotalPorFecha(LocalDate inicio, LocalDate fin) {
        validarFechas(inicio, fin);
        return ventaDAO.obtenerTotalPorFecha(inicio, fin);
    }

    // ===============================
    // REPORTE: RESUMEN PERIODO
    // ===============================
    public ResumenVentas obtenerResumenPeriodo(LocalDate inicio, LocalDate fin) {
        validarFechas(inicio, fin);
        return ventaDAO.consultarResumenPeriodo(inicio, fin);
    }

    // ===============================
    // REPORTE: TOTALES POR DIA
    // ===============================
    public Map<LocalDate, Double> obtenerTotalesPorDia(LocalDate inicio, LocalDate fin) {
        validarFechas(inicio, fin);
        return ventaDAO.obtenerTotalesPorDia(inicio, fin);
    }
    
    // Metodo para exportar totales por dia a CSV
    public boolean exportarTotalesPorDiaCSV(LocalDate inicio, LocalDate fin, String archivo) {

        validarFechas(inicio, fin);

        Map<LocalDate, Double> datos =
                ventaDAO.obtenerTotalesPorDia(inicio, fin);

        if (datos.isEmpty()) {
            System.out.println("⚠ No hay datos para exportar.");
            return false;
        }

        try {
            ExportadorCSV.exportarTotalesPorDia(datos, archivo);
            return true;

        } catch (Exception e) {
            System.out.println("❌ Error al exportar CSV: " + e.getMessage());
            return false;
        }
    }

    // ===============================
    // REPORTE: TOTALES POR MES
    // ===============================
    public Map<YearMonth, Double> obtenerTotalesPorMes(LocalDate inicio, LocalDate fin) {
        validarFechas(inicio, fin);
        return ventaDAO.obtenerTotalesPorMes(inicio, fin);
    }
    
    public boolean exportarTotalesPorMesCSV(
        LocalDate inicio,
        LocalDate fin,
        String ruta) {

        validarFechas(inicio, fin);

        try {
            Map<YearMonth, Double> datos =
                    ventaDAO.obtenerTotalesPorMes(inicio, fin);

            ExportadorCSV.exportarTotalesPorMes(datos, ruta);
            return true;

        } catch (Exception e) {
            System.out.println("❌ Error exportando CSV: " + e.getMessage());
            return false;
        }
    }

    // ===============================
    // REPORTE: DIA CON MAYOR VENTA
    // ===============================
    public Map.Entry<LocalDate, Double> obtenerDiaConMayorVenta(LocalDate inicio, LocalDate fin) {
        validarFechas(inicio, fin);
        return ventaDAO.obtenerDiaConMayorVenta(inicio, fin);
    }

    // ===============================
    // REPORTE: PRODUCTOS MAS VENDIDOS
    // ===============================
    public Map<String, Integer> obtenerProductosMasVendidos(LocalDate inicio, LocalDate fin) {
        validarFechas(inicio, fin);
        return ventaDAO.consultarProductosMasVendidos(inicio, fin);
    }
    
    // ===============================--------
    // REPORTE: EXPORTAR VENTAS POR FECHA CSV
    // ===============================--------
   public boolean exportarVentasPorFechaCSV(LocalDate inicio, LocalDate fin, String nombreArchivo) {
        try {
            List<Venta> ventas = ventaDAO.obtenerVentasPorFecha(inicio, fin);
            ExportadorCSV.exportarVentasPorFecha(ventas, nombreArchivo);
            return true;
        } catch (Exception e) {
            System.out.println("❌ Error exportando CSV: " + e.getMessage());
            return false;
        }
    }
}
