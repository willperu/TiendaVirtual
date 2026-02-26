package servicios;

import DAO.VentaDAO;
import java.time.LocalDate;
import java.util.Map;

public class ServicioReportes {

    private VentaDAO ventaDAO;

    public ServicioReportes(VentaDAO ventaDAO){
        this.ventaDAO = ventaDAO;
    }
    
    public Map<String, Integer> obtenerProductosMasVendidos(LocalDate inicio, LocalDate fin){
        return ventaDAO.consultarProductosMasVendidos(inicio, fin);
    }
}    
    
