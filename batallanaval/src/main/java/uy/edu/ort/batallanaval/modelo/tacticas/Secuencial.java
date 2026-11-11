package uy.edu.ort.batallanaval.modelo.tacticas;

import uy.edu.ort.batallanaval.modelo.Celda;
import uy.edu.ort.batallanaval.modelo.Oceano;

/** Recorre el océano de izquierda a derecha y de abajo hacia arriba, sin saltear celdas. */
public class Secuencial extends Tactica {

    public Secuencial() {
        super("Secuencial");
    }

    @Override
    public Celda elegirDisparo(Oceano oceanoRival) {
        return oceanoRival.getCeldasSinDisparar().get(0);
    }
}
