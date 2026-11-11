package uy.edu.ort.batallanaval.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uy.edu.ort.batallanaval.excepciones.BatallaNavalException;

/**
 * Experto en las celdas y los barcos de un jugador:
 * sabe ubicar barcos respetando las reglas y recibir disparos.
 */
public class Oceano {

    private static final int INTENTOS_POR_FLOTA = 500;
    private static final int INTENTOS_POR_BARCO = 200;

    private int filas;
    private int columnas;
    private Celda[][] celdas;
    private List<Barco> barcos = new ArrayList<>();
    private List<Barco> hundidos = new ArrayList<>();   // en orden cronológico

    public Oceano(int filas, int columnas) {
        this.filas = filas;
        this.columnas = columnas;
        crearCeldas();
    }

    private void crearCeldas() {
        celdas = new Celda[filas][columnas];
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                celdas[f][c] = new Celda(f, c);
            }
        }
    }

    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }

    public boolean estaDentro(int fila, int columna) {
        return fila >= 0 && fila < filas && columna >= 0 && columna < columnas;
    }

    public Celda getCelda(int fila, int columna) throws BatallaNavalException {
        if (!estaDentro(fila, columna)) {
            throw new BatallaNavalException("La celda (" + fila + ", " + columna + ") está fuera del océano");
        }
        return celdas[fila][columna];
    }

    // ---------------- Ubicar barcos ----------------

    public void ubicar(TipoBarco tipo, int fila, int columna, Orientacion orientacion) throws BatallaNavalException {
        List<Celda> lugar = celdasParaBarco(tipo, fila, columna, orientacion);
        validarSinSuperposicion(lugar);
        validarSinContacto(lugar);
        barcos.add(new Barco(tipo, lugar));
    }

    private List<Celda> celdasParaBarco(TipoBarco tipo, int fila, int columna, Orientacion orientacion)
            throws BatallaNavalException {
        List<Celda> lugar = new ArrayList<>();
        for (int parte = 0; parte < tipo.getTamanio(); parte++) {
            int f = orientacion.filaDeParte(fila, parte);
            int c = orientacion.columnaDeParte(columna, parte);
            if (!estaDentro(f, c)) {
                throw new BatallaNavalException("El barco queda fuera de los límites del océano");
            }
            lugar.add(celdas[f][c]);
        }
        return lugar;
    }

    private void validarSinSuperposicion(List<Celda> lugar) throws BatallaNavalException {
        for (Celda celda : lugar) {
            if (celda.tieneBarco()) {
                throw new BatallaNavalException("El barco se superpone con un barco ya ubicado");
            }
        }
    }

    private void validarSinContacto(List<Celda> lugar) throws BatallaNavalException {
        for (Celda celda : lugar) {
            if (tocaUnBarco(celda)) {
                throw new BatallaNavalException("El barco no puede tocar a otro barco, debe quedar agua entre ellos");
            }
        }
    }

    private boolean tocaUnBarco(Celda celda) {
        for (int f = celda.getFila() - 1; f <= celda.getFila() + 1; f++) {
            for (int c = celda.getColumna() - 1; c <= celda.getColumna() + 1; c++) {
                if (estaDentro(f, c) && celdas[f][c].tieneBarco()) {
                    return true;
                }
            }
        }
        return false;
    }

    // ---------------- Ubicación aleatoria (flota de la máquina) ----------------

    public void ubicarAlAzar(List<TipoBarco> flota) throws BatallaNavalException {
        Random random = new Random();
        for (int intento = 0; intento < INTENTOS_POR_FLOTA; intento++) {
            vaciar();
            if (intentarUbicarFlota(flota, random)) {
                return;
            }
        }
        throw new BatallaNavalException("No fue posible ubicar la flota en el océano");
    }

    private boolean intentarUbicarFlota(List<TipoBarco> flota, Random random) {
        for (TipoBarco tipo : flota) {
            if (!intentarUbicarBarco(tipo, random)) {
                return false;
            }
        }
        return true;
    }

    private boolean intentarUbicarBarco(TipoBarco tipo, Random random) {
        for (int intento = 0; intento < INTENTOS_POR_BARCO; intento++) {
            try {
                ubicar(tipo, random.nextInt(filas), random.nextInt(columnas), Orientacion.alAzar(random));
                return true;
            } catch (BatallaNavalException e) {
                // posición inválida: se prueba otra
            }
        }
        return false;
    }

    private void vaciar() {
        barcos.clear();
        hundidos.clear();
        crearCeldas();
    }

    // ---------------- Disparos ----------------

    public ResultadoDisparo recibirDisparo(int fila, int columna) throws BatallaNavalException {
        Celda celda = getCelda(fila, columna);
        if (celda.fueDisparada()) {
            throw new BatallaNavalException("Ya disparó a la celda (" + fila + ", " + columna + ")");
        }
        ResultadoDisparo resultado = celda.recibirDisparo();
        if (resultado == ResultadoDisparo.HUNDIDO) {
            hundidos.add(celda.getBarco());
        }
        return resultado;
    }

    public boolean flotaHundida() {
        return !barcos.isEmpty() && hundidos.size() == barcos.size();
    }

    /** Todas las celdas, de izquierda a derecha y de abajo hacia arriba. */
    public List<Celda> getCeldas() {
        List<Celda> resultado = new ArrayList<>();
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                resultado.add(celdas[f][c]);
            }
        }
        return resultado;
    }

    /** Celdas que todavía no recibieron disparos, en el mismo orden que getCeldas(). */
    public List<Celda> getCeldasSinDisparar() {
        List<Celda> resultado = new ArrayList<>();
        for (Celda celda : getCeldas()) {
            if (!celda.fueDisparada()) {
                resultado.add(celda);
            }
        }
        return resultado;
    }

    /** La celda si está dentro del océano y todavía no recibió disparos; si no, null. */
    public Celda celdaSinDisparar(int fila, int columna) {
        if (!estaDentro(fila, columna) || celdas[fila][columna].fueDisparada()) {
            return null;
        }
        return celdas[fila][columna];
    }

    /** true si en esa celda hubo un impacto a un barco que todavía no se hundió. */
    public boolean esImpactoSinHundir(int fila, int columna) {
        if (!estaDentro(fila, columna)) {
            return false;
        }
        Celda celda = celdas[fila][columna];
        return celda.fueDisparada() && celda.tieneBarco() && !celda.getBarco().estaHundido();
    }

    public List<Celda> getImpactosSinHundir() {
        List<Celda> resultado = new ArrayList<>();
        for (Celda celda : getCeldas()) {
            if (esImpactoSinHundir(celda.getFila(), celda.getColumna())) {
                resultado.add(celda);
            }
        }
        return resultado;
    }

    // ---------------- Integridad y bajas ----------------

    /** Partes de barco alcanzadas. */
    public int getImpactos() {
        int impactos = 0;
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                if (celdas[f][c].fueDisparada() && celdas[f][c].tieneBarco()) {
                    impactos++;
                }
            }
        }
        return impactos;
    }

    public List<Barco> getHundidos() {
        return new ArrayList<>(hundidos);
    }
}
