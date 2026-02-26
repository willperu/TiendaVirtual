package tiendavirtual;

import java.util.ArrayList;
import java.util.List;

public class Carrito {

    private List<ItemCarrito> items = new ArrayList<>();

    public void agregarProducto(Producto producto, int cantidad) {
        items.add(new ItemCarrito(producto, cantidad));
    }    
    
    // Metodo Cantidad
    public int getCantidadProducto(int productoId) {
        for (ItemCarrito item : items) {
            if (item.getProducto().getId() == productoId) {
                return item.getCantidad();
            }
        }
        return 0;
    }
    
    public void agregarProductoUnificado(Producto producto, int cantidad) {
    for (ItemCarrito item : items) {
        if (item.getProducto().getId() == producto.getId()) {
            // Producto ya existe → sumar cantidad
            item.setCantidad(item.getCantidad() + cantidad);
            return; // listo, no agregamos nuevo
        }
    }

    // Producto no existe → agregar nuevo
    items.add(new ItemCarrito(producto, cantidad));
}

    // Metodo Calcular Total
    public double calcularTotal() {
    double total = 0;
        for (ItemCarrito item : items) { // items = lista de productos en el carrito
            total += item.getProducto().getPrecio() * item.getCantidad();
        }
        return total;
    }
    
    public double getTotal() {
    double total = 0;

       for (ItemCarrito item : items) {
            total += item.getSubtotal();
        }

        return total; 
    }
    
    public boolean estaVacio() {
        return items.isEmpty();
    }

    public void vaciar() {
        items.clear();
    }

    public List<ItemCarrito> getItems() {
        return items;
    }
    
    // Obtener item por índice (para menú)
    public ItemCarrito getItem(int index) {
        if (index < 0 || index >= items.size()) {
            return null;
        }
        return items.get(index);
    }

    // Eliminar item completo
    public void eliminarItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
        }
    }

// Reducir cantidad de un item
    public void reducirCantidad(int index, int cantidad) {
        if (cantidad <= 0) return;

        ItemCarrito item = getItem(index);
        if (item == null) return;

        int nuevaCantidad = item.getCantidad() - cantidad;

        if (nuevaCantidad <= 0) {
            items.remove(index);
        } else {
            item.setCantidad(nuevaCantidad);
        }
    }    
    
}
