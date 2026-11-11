package uy.edu.ort.batallanaval.modelo;

import java.util.Random;

/**
 * Hacia dónde crece el barco a partir de su proa.
 * Horizontal: la proa es el extremo izquierdo, crece hacia la derecha.
 * Vertical: la proa es el extremo inferior, crece hacia arriba (la fila 0 está abajo).
 */
public enum Orientacion {

    HORIZONTAL("horizontal", 0, 1),
    VERTICAL("vertical", 1, 0);

    private final String nombre;
    private final int pasoFila;
    private final int pasoColumna;

    Orientacion(String nombre, int pasoFila, int pasoColumna) {
        this.nombre = nombre;
        this.pasoFila = pasoFila;
        this.pasoColumna = pasoColumna;
    }

    public String getNombre() {
        return nombre;
    }

    public int filaDeParte(int filaProa, int parte) {
        return filaProa + parte * pasoFila;
    }

    public int columnaDeParte(int columnaProa, int parte) {
        return columnaProa + parte * pasoColumna;
    }

    public static Orientacion alAzar(Random random) {
        return values()[random.nextInt(values().length)];
    }
}
