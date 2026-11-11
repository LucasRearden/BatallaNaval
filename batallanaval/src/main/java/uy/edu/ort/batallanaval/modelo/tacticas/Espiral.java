package uy.edu.ort.batallanaval.modelo.tacticas;

import uy.edu.ort.batallanaval.modelo.Celda;
import uy.edu.ort.batallanaval.modelo.Oceano;

/**
 * Empieza en una celda central y sigue una espiral que se expande
 * (derecha 1, arriba 1, izquierda 2, abajo 2, derecha 3, ...)
 * como si el océano fuese cuadrado: las posiciones que caen fuera se saltean.
 */
public class Espiral extends Tactica {

    private static final int[][] DIRECCIONES = { {0, 1}, {1, 0}, {0, -1}, {-1, 0} };  // {fila, columna}

    public Espiral() {
        super("Espiral");
    }

    @Override
    public Celda elegirDisparo(Oceano oceanoRival) {
        int fila = (oceanoRival.getFilas() - 1) / 2;
        int columna = (oceanoRival.getColumnas() - 1) / 2;
        Celda candidata = oceanoRival.celdaSinDisparar(fila, columna);
        int lado = 2 * Math.max(oceanoRival.getFilas(), oceanoRival.getColumnas()) + 1;
        int largo = 1;
        int direccion = 0;
        for (int recorridas = 1; candidata == null && recorridas < lado * lado; ) {
            for (int paso = 0; paso < largo && candidata == null; paso++, recorridas++) {
                fila += DIRECCIONES[direccion][0];
                columna += DIRECCIONES[direccion][1];
                candidata = oceanoRival.celdaSinDisparar(fila, columna);
            }
            direccion = (direccion + 1) % 4;
            if (direccion % 2 == 0) {
                largo++;    // cada dos giros el tramo se alarga
            }
        }
        return candidata;
    }
}
