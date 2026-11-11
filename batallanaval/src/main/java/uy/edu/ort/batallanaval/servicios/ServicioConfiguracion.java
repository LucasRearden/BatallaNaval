package uy.edu.ort.batallanaval.servicios;

import java.util.ArrayList;

import uy.edu.ort.batallanaval.modelo.TipoBarco;
import uy.edu.ort.batallanaval.modelo.tacticas.Tactica;

public class ServicioConfiguracion {

    private ArrayList<TipoBarco> tiposBarco = new ArrayList<>();
    private ArrayList<Tactica> tacticas = new ArrayList<>();

    public void agregar(TipoBarco tipo) {
        tiposBarco.add(tipo);
    }

    public ArrayList<TipoBarco> getTiposBarco() {
        return tiposBarco;
    }

    public void agregar(Tactica tactica) {
        tacticas.add(tactica);
    }

    public ArrayList<Tactica> getTacticas() {
        return tacticas;
    }
}
