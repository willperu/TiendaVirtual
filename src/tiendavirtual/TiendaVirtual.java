package tiendavirtual;

import DAO.VentaDAO;
import DAO.ProductoDAO;
import DAO.UsuarioDAO;

import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;
import java.util.Locale;
import java.time.format.DateTimeFormatter;

import reportes.TotalesPorDiaAdmin;
import reportes.TotalesPorMesAdmin;
import reportes.ProductosMasVendidosAdmin;
import reportes.VentasPorUsuarioAdmin;
import reportes.ReporteVentas;
import reportes.ControladorReportes;

import servicios.ServicioVentas;
import servicios.ServicioReportesVentas;
import servicios.ServicioDatosDemo;

public class TiendaVirtual {

    // ===== VARIABLES GLOBALES =====
    //DAOs
    static final ProductoDAO productoDAO = new ProductoDAO();
    static final VentaDAO ventaDAO = new VentaDAO();
    static final UsuarioDAO usuarioDAO = new UsuarioDAO();
    
    //Servicios
    static final ServicioVentas servicioVentas = new ServicioVentas(ventaDAO,productoDAO);
    static ServicioDatosDemo servicioDatosDemo = new ServicioDatosDemo(ventaDAO);
    
    static Scanner sc = new Scanner(System.in);
    static Carrito carrito = new Carrito();
       
    
    static Usuario usuarioLogueado = null;

    // Formateadores de fecha globales
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("es", "ES"));
    private static final DateTimeFormatter FORMATO_MES =
            DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("es", "ES"));

    // ===== MÉTODOS AUXILIARES ===== 
    private static void separador() {
        System.out.println("\n====================================");
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String input = sc.nextLine();
            if (!input.matches("\\d+")) {
                System.out.println("❌ Debe ingresar solo números positivos");
                continue;
            }
            return Integer.parseInt(input);
        }
    }

    private static double leerDouble(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Double.parseDouble(sc.nextLine().replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.println("❌ Debe ingresar un número válido");
            }
        }
    }

    public static String leerString(String mensaje) {
        System.out.print(mensaje);
        return sc.nextLine();
    }

    // ===== MÉTODO PRINCIPAL =====
    public static void main(String[] args) {
        usuarioLogueado = login();
        if (usuarioLogueado == null) {
            System.out.println("❌ Usuario o contraseña incorrectos");
            return;
        }

        System.out.println("Bienvenido " + usuarioLogueado.getUsuario());
        System.out.println("Rol: " + usuarioLogueado.getRol());

        if (usuarioLogueado.getRol().equalsIgnoreCase("ADMIN")) {
            menuAdmin();
        } else {
            menuCliente();
        }
    }

    private static Usuario login() {
        System.out.print("Usuario: ");
        String user = sc.nextLine();
        return usuarioDAO.login(user);
    }

    // ===== MENÚ ADMIN =====
    private static void menuAdmin() {
    if (!usuarioLogueado.getRol().equalsIgnoreCase("ADMIN")) {
        System.out.println("❌ Acceso denegado. Solo administradores.");
        return;
    }

    int opcion;
    do {
        separador();
        System.out.println("""
        ===== MENÚ ADMIN =====
        1 - Ver productos
        2 - Agregar producto
        3 - Reportes
        4 - Ver ventas por usuario
        0 - Salir
        """);

        opcion = leerEntero("Seleccione una opción: ");

        switch (opcion) {
            case 1 -> mostrarProductos();
            case 2 -> agregarProductoAdmin();
            case 3 -> menuReportesAdmin(); // llama al submenú de reportes
            case 4 -> VentasPorUsuarioAdmin.mostrarVentasPorUsuarioAdmin(usuarioLogueado, ventaDAO, sc);
            case 0 -> System.out.println("👋 Saliendo...");
            default -> System.out.println("❌ Opción inválida");
        }

    } while (opcion != 0);
}

    // ===== MENÚ REPORTES =====
    private static void menuReportesAdmin() {
    if (!usuarioLogueado.getRol().equalsIgnoreCase("ADMIN")) {
        System.out.println("❌ Acceso denegado. Solo administradores.");
        return;
    }

    int opcion;

    do {
        separador();
        System.out.println("""
        ===== MENÚ REPORTES =====
        1 - Resumen por Período
        2 - Totales por Día
        3 - Totales por Mes
        4 - Productos Más Vendidos
        5 - Ventas por Fecha
        6 - Exportar ventas por fecha (CSV)
       99 - Generar datos demo
        0 - Volver
        """);

        System.out.print("Seleccione una opción: ");
        try {
            opcion = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            opcion = -1;
        }

        switch (opcion) {
            // 1️⃣ Resumen por período
            case 1 -> {
                DateTimeFormatter formatoEntrada = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate inicio = null;
                LocalDate fin = null;

                while (inicio == null) {
                    try {
                        System.out.print("Ingrese fecha inicio (dd/MM/yyyy): ");
                        inicio = LocalDate.parse(sc.nextLine(), formatoEntrada);
                    } catch (Exception e) {
                        System.out.println("❌ Fecha inválida.");
                    }
                }

                while (fin == null) {
                    try {
                        System.out.print("Ingrese fecha fin (dd/MM/yyyy): ");
                        fin = LocalDate.parse(sc.nextLine(), formatoEntrada);
                        if (fin.isBefore(inicio)) {
                            System.out.println("❌ La fecha fin no puede ser anterior a la fecha inicio.");
                            fin = null;
                        }
                    } catch (Exception e) {
                        System.out.println("❌ Fecha inválida.");
                    }
                }

                ReporteVentas.mostrarResumenPeriodoAdmin(
                    usuarioLogueado, // usuario actual
                    ventaDAO,        // DAO existente
                    formatoEntrada,  // formato de fecha
                    inicio,
                    fin
                );
            }

            case 2 -> TotalesPorDiaAdmin.mostrarTotalesPorDiaAdmin(usuarioLogueado);
            case 3 -> TotalesPorMesAdmin.mostrarTotalesPorMesAdmin(usuarioLogueado, ventaDAO);
            case 4 -> ProductosMasVendidosAdmin.mostrarProductosMasVendidosAdmin(usuarioLogueado);
            case 5 -> ControladorReportes.ventasPorFecha(usuarioLogueado, servicioVentas);
            case 6 -> {
            try {
                // ✅ Crear instancia del servicio de reportes pasando el DAO
                ServicioReportesVentas servicioReportesVentas = new ServicioReportesVentas(ventaDAO);

                // ✅ Llamar al controlador, que se encargará de pedir fechas y exportar CSV
                ControladorReportes.exportarVentasPorFecha(usuarioLogueado, servicioReportesVentas);

                } catch (Exception e) {
                    System.out.println("❌ Error al exportar reporte: " + e.getMessage());
                }
            }
            case 0 -> System.out.println("↩ Volviendo al menú ADMIN...");
            case 99 -> generarDatosDemo();
            default -> System.out.println("❌ Opción inválida");
        }

    } while (opcion != 0);
}

    // ===== MENÚ CLIENTE =====
    private static void menuCliente() {
        int opcion;
        do {
            separador();
            System.out.println("""
                    ===== MENÚ CLIENTE =====
                    1 - Ver productos
                    2 - Comprar producto
                    3 - Ver carrito
                    4 - Modificar carrito
                    5 - Finalizar compra
                    6 - Ver mis compras
                    0 - Salir
                    """);

            opcion = leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1 -> mostrarProductos();
                case 2 -> comprarProducto();
                case 3 -> mostrarCarrito();
                case 4 -> modificarCarrito();
                case 5 -> finalizarCompra();
                case 6 -> mostrarMisVentas(usuarioLogueado);
                case 0 -> System.out.println("👋 Saliendo...");
                default -> System.out.println("❌ Opción inválida");
            }
        } while (opcion != 0);
    }

    // ===== PRODUCTOS =====
    public static void mostrarProductos() {
        List<Producto> lista = productoDAO.obtenerTodos();
        separador();
        System.out.println("===== PRODUCTOS DISPONIBLES =====");
        for (Producto p : lista) {
            System.out.println(p.getId() + " - " + p.getNombre() + " - $" + p.getPrecio() + " - Stock: " + p.getStock());
        }
    }

    private static void agregarProductoAdmin() {
        if (!usuarioLogueado.getRol().equalsIgnoreCase("ADMIN")) {
            System.out.println("❌ Acceso denegado. Solo administradores.");
            return;
        }

        separador();
        System.out.println("=== AGREGAR PRODUCTO (ADMIN) ===");

        System.out.print("Nombre: ");
        String nombre = sc.nextLine();

        double precio = leerDouble("Precio: ");
        int stock = leerEntero("Stock: ");

        Producto p = new Producto();
        p.setNombre(nombre);
        p.setPrecio(precio);
        p.setStock(stock);

        productoDAO.insertar(p);
        System.out.println("✅ Producto agregado al sistema");
    }

    // ===== CARRITO Y COMPRAS =====
    
    // Metodo MOstrar Carrito
    public static void mostrarCarrito() {
        if (carrito.estaVacio()) {
            System.out.println("🛒 El carrito está vacío");
            return;
        }

        // 1️⃣ Calcular ancho dinámico de la columna Producto
        int maxNombreLength = "Producto".length();
        for (ItemCarrito item : carrito.getItems()) {
            int len = item.getProducto().getNombre().length();
            if (len > maxNombreLength) maxNombreLength = len;
        }
        separador();
        System.out.println("===== CARRITO =====");
        // Cabecera
        System.out.printf(
            "%-5s %-" + maxNombreLength + "s %-10s %-15s %-10s%n",
            "ID", "Producto", "Cantidad", "Precio Unitario", "Subtotal"
        );
        // Separador
        for (int i = 0; i < maxNombreLength + 10 + 15 + 10 + 3; i++) System.out.print("-");
        System.out.println();

        // 2️⃣ Mostrar productos
        for (ItemCarrito item : carrito.getItems()) {
            Producto p = item.getProducto();
            int cantidad = item.getCantidad();
            double subtotal = cantidad * p.getPrecio();

            System.out.printf(
                "%-5d %-" + maxNombreLength + "s %-10d $%-14.2f $%-10.2f%n",
                p.getId(),
                p.getNombre(),
                cantidad,
                p.getPrecio(),
                subtotal
            );
        }

        // 3️⃣ Total
        System.out.println("--------------------------------------------------------");
        System.out.printf("TOTAL: $%.2f%n", carrito.getTotal());
    }    
    
    //Metodo Modificar Carrito
    public static void modificarCarrito() {

    if (carrito.estaVacio()) {
        System.out.println("🛒 El carrito está vacío");
        return;
    }

    mostrarCarrito();

    // 1️⃣ Pedir ID real
    int idProducto = leerEntero("Ingrese el ID del producto: ");

    // 2️⃣ Buscar el item por ID
    ItemCarrito itemSeleccionado = null;

    for (ItemCarrito item : carrito.getItems()) {
        if (item.getProducto().getId() == idProducto) {
            itemSeleccionado = item;
            break;
        }
    }

    if (itemSeleccionado == null) {
        System.out.println("❌ Producto no encontrado en el carrito");
        return;
    }

    // 3️⃣ Submenú
    System.out.println("1 - Quitar producto");
    System.out.println("2 - Reducir cantidad");
    int accion = leerEntero("Opción: ");

        switch (accion) {
            case 1 -> {
                carrito.getItems().remove(itemSeleccionado);
                System.out.println("✅ Producto eliminado del carrito");
            }

            case 2 -> {
                int cant = leerEntero("Cantidad a reducir: ");

                if (cant <= 0 || cant >= itemSeleccionado.getCantidad()) {
                    System.out.println("❌ Cantidad inválida");
                    return;
                }

                itemSeleccionado.setCantidad(
                    itemSeleccionado.getCantidad() - cant
                );

                System.out.println("✅ Carrito actualizado");
            }

            default -> System.out.println("❌ Opción inválida");
        }
    }
    
    // Metodo Comprar Producto
    public static void comprarProducto() {

    mostrarProductos();

    int id;
    int cantidad;

        try {
            System.out.print("Ingrese el ID del producto: ");
            id = Integer.parseInt(sc.nextLine());

            System.out.print("Ingrese cantidad: ");
            cantidad = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("❌ Debe ingresar solo números");
            return;
        }

        if (cantidad <= 0) {
            System.out.println("❌ Cantidad inválida");
            return;
        }

        Producto producto = productoDAO.buscarPorId(id);

        if (producto == null) {
            System.out.println("❌ Producto no encontrado");
            return;
        }

        int cantidadEnCarrito = carrito.getCantidadProducto(id);
        int totalSolicitado = cantidadEnCarrito + cantidad;

        if (totalSolicitado > producto.getStock()) {
            System.out.println(
                "❌ Stock insuficiente. Disponible: "
                + producto.getStock()
                + " | En carrito: "
                + cantidadEnCarrito
            );
            return;
        }

        carrito.agregarProductoUnificado(producto, cantidad);
        System.out.println("✅ Producto agregado al carrito");
    }    
    
    //Metodo Finalizar Compra
    public static void finalizarCompra() {

        if (carrito.estaVacio()) {
            System.out.println("❌ No se puede registrar una venta sin productos");
            return;
        }

        // 1️⃣ Verificar stock ANTES de vender
        for (ItemCarrito item : carrito.getItems()) {
            Producto p = productoDAO.buscarPorId(item.getProducto().getId());

            if (p == null) {
                System.out.println("❌ Producto no encontrado: " + item.getProducto().getNombre());
                return;
            }

            if (item.getCantidad() > p.getStock()) {
                System.out.println(
                    "❌ Stock insuficiente para: " + p.getNombre() + ". Disponible: " + p.getStock()
                );
                return;
            }
        }

        // 2️⃣ Mostrar carrito y total
        mostrarCarrito();
        
        // 3️⃣ Confirmar compra con entrada segura
        String op = leerString("¿Confirmar compra? (S/N): ").trim().toUpperCase();

        if (!op.equals("S")) {
            System.out.println("❌ Compra cancelada");
            return;
        }

        // 4️⃣ Registrar venta COMPLETA
        boolean ok = ventaDAO.registrarVentaCompleta(
            usuarioLogueado.getId(),
            carrito.getItems(),
            carrito.getTotal()
        );

        if (ok) {
            carrito.vaciar();
            System.out.println("✅ Venta realizada con éxito");
        } else {
            System.out.println("❌ Error al procesar la venta");
        }
    }     

    // ===== VENTAS =====
    private static void mostrarMisVentas(Usuario usuarioLogueado){
    List<Venta> ventas = ventaDAO.obtenerVentasPorUsuario(usuarioLogueado.getId());

        if (ventas.isEmpty()) {
        System.out.println("ℹ️ No hay ventas registradas todavía.");
        return;
        }
        separador();
        System.out.println("===== HISTORIAL DE VENTAS  =====");

        for (Venta v : ventas) {
            System.out.println("ID: " + v.getId() + " | Fecha: " + v.getFecha() + " | Total: $" + v.getTotal());

            // Mostrar detalles de cada venta
            List<DetalleVenta> detalles = ventaDAO.obtenerDetalles(v.getId());
            if (detalles.isEmpty()) {
            System.out.println("    ⚠️ (Sin detalles registrados)");
            } else {
                System.out.println("===== DETALLE DE VENTA =====");
                System.out.printf("%-15s %-8s %-15s %-10s%n",
                                   "Producto", "Cantidad", "Precio Unitario", "Subtotal"
            );
               System.out.println("------------------------------------------------");

                for (DetalleVenta d : detalles) {
                    String nombre = d.getProducto().getNombre();
                    int cantidad = d.getCantidad();
                    double precioUnitario = d.getProducto().getPrecio();
                    double subtotal = d.getSubtotal();

                    System.out.printf("%-15s %-8d $%-14.2f $%.2f%n", nombre, cantidad, precioUnitario, subtotal);
                }

                // Mostrar total de la venta
                double total = detalles.stream().mapToDouble(DetalleVenta::getSubtotal).sum();
                System.out.println("----------------------------------------------");
                System.out.printf("TOTAL: $%.2f%n", total);
            } 
        }
            System.out.println("-----------------------------");
    }
           
    // Metodo Generar Datos Demo
    private static void generarDatosDemo() {

        if (!usuarioLogueado.getRol().equalsIgnoreCase("ADMIN")) {
            System.out.println("❌ Solo admin puede generar datos demo");
            return;
        }

        boolean ok = servicioDatosDemo.insertarVentasDemo();

        if (ok)
            System.out.println("✅ Datos demo generados correctamente");
        else
            System.out.println("❌ Error generando datos demo");
    }

}
