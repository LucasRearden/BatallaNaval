package uy.edu.ort.batallanaval.servicios;

import java.util.ArrayList;
import java.util.List;

import uy.edu.ort.batallanaval.excepciones.BatallaNavalException;
import uy.edu.ort.batallanaval.modelo.Batalla;

public class ServicioBatallas {

    private List<Batalla> batallas = new ArrayList<>();

    public synchronized void agregar(Batalla batalla) throws BatallaNavalException {
        if (existeLugar(batalla.getLugar())) {
            throw new BatallaNavalException("No se puede librar más de una batalla en el mismo lugar, cambie de nombre");
        }
        batallas.add(batalla);
    }

    private boolean existeLugar(String lugar) {
        for (Batalla b : batallas) {
            if (b.tieneLugar(lugar)) {
                return true;
            }
        }
        return false;
    }

    public synchronized List<Batalla> getBatallas() {
        return new ArrayList<>(batallas);
    }

    public synchronized int getCantidadGanadas() {
        int cantidad = 0;
        for (Batalla b : batallas) {
            if (b.getEstado().esGanada()) {
                cantidad++;
            }
        }
        return cantidad;
    }

    public synchronized int getCantidadPerdidas() {
        int cantidad = 0;
        for (Batalla b : batallas) {
            if (b.getEstado().esPerdida()) {
                cantidad++;
            }
        }
        return cantidad;
    }

    /** Batallas ganadas sobre el total de batallas, en porcentaje. */
    public synchronized int getPorcentajeExito() {
        if (batallas.isEmpty()) {
            return 0;
        }
        return Math.round(getCantidadGanadas() * 100f / batallas.size());
    }
}
