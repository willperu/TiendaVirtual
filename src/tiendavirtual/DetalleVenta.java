package tiendavirtual;

public class DetalleVenta {
    private Producto producto;
    private int cantidad;
    private double subtotal;

    public DetalleVenta(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }
    
    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public String toString() {
        return producto.getNombre() + "\t" + cantidad + "\t$" 
               + String.format("%.2f", producto.getPrecio()) + "\t$" 
               + String.format("%.2f", subtotal);
    }
}
