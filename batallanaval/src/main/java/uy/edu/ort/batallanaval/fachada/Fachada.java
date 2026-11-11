package uy.edu.ort.batallanaval.fachada;

import java.util.ArrayList;
import java.util.List;

import uy.edu.ort.batallanaval.excepciones.BatallaNavalException;
import uy.edu.ort.batallanaval.modelo.Batalla;
import uy.edu.ort.batallanaval.modelo.TipoBarco;
import uy.edu.ort.batallanaval.modelo.tacticas.Tactica;
import uy.edu.ort.batallanaval.observador.Observable;
import uy.edu.ort.batallanaval.servicios.ServicioBatallas;
import uy.edu.ort.batallanaval.servicios.ServicioConfiguracion;

public class Fachada extends Observable {

    public enum Eventos { cambioListaBatallas }

    private static Fachada instancia;

    private ServicioConfiguracion sConfiguracion;
    private ServicioBatallas sBatallas;

    private Fachada() {
        sConfiguracion = new ServicioConfiguracion();
        sBatallas = new ServicioBatallas();
    }

    public static synchronized Fachada getInstancia() {
        if (instancia == null) {
            instancia = new Fachada();
        }
        return instancia;
    }

    // ---------- Configuración ----------

    public void agregar(TipoBarco tipo) {
        sConfiguracion.agregar(tipo);
    }

    public ArrayList<TipoBarco> getTiposBarco() {
        return sConfiguracion.getTiposBarco();
    }

    public void agregar(Tactica tactica) {
        sConfiguracion.agregar(tactica);
    }

    public ArrayList<Tactica> getTacticas() {
        return sConfiguracion.getTacticas();
    }

    // ---------- Batallas ----------

    public void agregar(Batalla batalla) throws BatallaNavalException {
        sBatallas.agregar(batalla);
        avisar(Eventos.cambioListaBatallas);
    }

    public List<Batalla> getBatallas() {
        return sBatallas.getBatallas();
    }

    public int getCantidadGanadas() {
        return sBatallas.getCantidadGanadas();
    }

    public int getCantidadPerdidas() {
        return sBatallas.getCantidadPerdidas();
    }

    public int getPorcentajeExito() {
        return sBatallas.getPorcentajeExito();
    }
}
