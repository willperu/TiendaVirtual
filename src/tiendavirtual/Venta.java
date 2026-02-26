package tiendavirtual;

import java.sql.Timestamp;
import java.util.List;

public class Venta {
    private int id;
    private Timestamp fecha;
    private double total;
    private List<DetalleVenta> detalles;

    public Venta(int id, Timestamp fecha, double total) {
        this.id = id;
        this.fecha = fecha;
        this.total = total;
    }

    public int getId() { return id; }
    public Timestamp getFecha() { return fecha; }
    public double getTotal() { return total; }
    
    public List<DetalleVenta> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
        
    }    
}
