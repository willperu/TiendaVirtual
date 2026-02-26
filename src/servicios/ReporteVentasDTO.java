package servicios;

import tiendavirtual.Venta;
import java.util.List;

public class ReporteVentasDTO {

    private List<Venta> ventas;
    private double total;

    public ReporteVentasDTO(List<Venta> ventas, double total) {
        this.ventas = ventas;
        this.total = total;
    }

    public List<Venta> getVentas() {
        return ventas;
    }

    public double getTotal() {
        return total;
    }
}
