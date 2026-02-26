package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class FechasUtils {

    private static Scanner scanner = new Scanner(System.in);

    // Método para pedir una fecha al usuario
    public static Date pedirFecha(String tipo) {
        Date fecha = null;
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        formato.setLenient(false);
        while (fecha == null) {
            try {
                System.out.print("Ingrese " + tipo + " (dd/MM/yyyy): ");
                String input = scanner.nextLine();
                fecha = formato.parse(input);
            } catch (ParseException e) {
                System.out.println("Fecha inválida. Intente nuevamente.");
            }
        }
        return fecha;
    }

    // Método para formatear una fecha de manera amigable
    public static String formatearFecha(Date fecha) {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        return formato.format(fecha);
    }

    // Método para formatear solo el mes y año
    public static String formatearMes(Date fecha) {
        SimpleDateFormat formato = new SimpleDateFormat("MMMM yyyy");
        return formato.format(fecha);
    }
}
