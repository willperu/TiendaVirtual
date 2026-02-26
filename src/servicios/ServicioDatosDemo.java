package servicios;

import DAO.VentaDAO;

public class ServicioDatosDemo {

    private final VentaDAO ventaDAO;

    public ServicioDatosDemo(VentaDAO ventaDAO) {
        this.ventaDAO = ventaDAO;
    }

    public boolean insertarVentasDemo() {
        return ventaDAO.insertarVentasDemo();
    }
}