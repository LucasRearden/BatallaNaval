package uy.edu.ort.batallanaval.modelo;

public class Celda {

    private int fila;
    private int columna;
    private Barco barco;
    private boolean disparada;

    public Celda(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    public Barco getBarco() {
        return barco;
    }

    void setBarco(Barco barco) {
        this.barco = barco;
    }

    public boolean tieneBarco() {
        return barco != null;
    }

    public boolean fueDisparada() {
        return disparada;
    }

    ResultadoDisparo recibirDisparo() {
        disparada = true;
        if (!tieneBarco()) {
            return ResultadoDisparo.AGUA;
        }
        return barco.estaHundido() ? ResultadoDisparo.HUNDIDO : ResultadoDisparo.IMPACTO;
    }

    /** @param mostrarBarcos true en aguas propias (o al revelar la flota enemiga) */
    public Contenido getContenido(boolean mostrarBarcos) {
        if (disparada) {
            return tieneBarco() ? Contenido.IMPACTO : Contenido.AGUA;
        }
        return mostrarBarcos && tieneBarco() ? Contenido.BARCO : Contenido.NADA;
    }
}
