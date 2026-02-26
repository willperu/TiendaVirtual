package tiendavirtual;

public class ResumenVentas {
    private int cantidadVentas;
    private double totalVendido;
    private double promedioVenta;

    public ResumenVentas(int cantidadVentas, double totalVendido, double promedioVenta) {
        this.cantidadVentas = cantidadVentas;
        this.totalVendido = totalVendido;
        this.promedioVenta = promedioVenta;
    }

    public int getCantidadVentas() {
        return cantidadVentas;
    }

    public double getTotalVendido() {
        return totalVendido;
    }

    public double getPromedioVenta() {
        return promedioVenta;
    } 
}
