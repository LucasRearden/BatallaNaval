package uy.edu.ort.batallanaval.modelo.tacticas;

import uy.edu.ort.batallanaval.modelo.Celda;
import uy.edu.ort.batallanaval.modelo.Oceano;

/**
 * Strategy: cada táctica decide a qué celda del océano rival dispara la máquina.
 * Para agregar una táctica nueva alcanza con crear otra subclase.
 */
public abstract class Tactica {

    private String nombre;

    public Tactica(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    /** Devuelve una celda que todavía no fue disparada. */
    public abstract Celda elegirDisparo(Oceano oceanoRival);

    @Override
    public String toString() {
        return nombre;
    }
}
