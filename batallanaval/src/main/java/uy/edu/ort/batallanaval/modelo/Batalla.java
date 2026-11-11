package uy.edu.ort.batallanaval.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import uy.edu.ort.batallanaval.excepciones.BatallaNavalException;
import uy.edu.ort.batallanaval.modelo.estados.EnPreparacion;
import uy.edu.ort.batallanaval.modelo.estados.EstadoBatalla;
import uy.edu.ort.batallanaval.modelo.estados.Ganada;
import uy.edu.ort.batallanaval.modelo.estados.Perdida;
import uy.edu.ort.batallanaval.modelo.tacticas.Tactica;
import uy.edu.ort.batallanaval.observador.Observable;

public class Batalla extends Observable {

    public enum Eventos { cambioEnBatalla }

    private static final int MIN_CELDAS = 6;
    private static final int MAX_CELDAS = 12;
    private static final int MAX_POR_TIPO = 6;
    private static final double DENSIDAD_MAXIMA = 0.25;

    private String lugar;
    private Tactica tactica;
    private int celdasHorizontales;
    private int celdasVerticales;
    private List<TipoBarco> flota;            // ordenada de mayor a menor
    private List<TipoBarco> barcosPorUbicar;  // los que el humano todavía no ubicó
    private Oceano aguasPropias;
    private Oceano aguasEnemigas;
    private EstadoBatalla estado;
    private int ronda;

    public Batalla(String lugar, Tactica tactica, int celdasHorizontales, int celdasVerticales,
                   Map<TipoBarco, Integer> cantidades) throws BatallaNavalException {
        this.lugar = (lugar == null || lugar.isBlank()) ? lugarAlAzar() : lugar.trim();
        this.tactica = tactica;
        this.celdasHorizontales = celdasHorizontales;
        this.celdasVerticales = celdasVerticales;
        validarConfiguracion();
        this.flota = armarFlota(cantidades);
        validarDensidad();
        this.barcosPorUbicar = new ArrayList<>(flota);
        this.aguasPropias = new Oceano(celdasVerticales, celdasHorizontales);
        this.aguasEnemigas = new Oceano(celdasVerticales, celdasHorizontales);
        this.aguasEnemigas.ubicarAlAzar(flota);
        this.estado = new EnPreparacion();
        this.ronda = 1;
    }

    // ---------------- Validaciones de creación (CU02 paso 3) ----------------

    private void validarConfiguracion() throws BatallaNavalException {
        if (tactica == null) {
            throw new BatallaNavalException("Debe seleccionar una táctica de juego");
        }
        validarRango(celdasHorizontales, "horizontales");
        validarRango(celdasVerticales, "verticales");
    }

    private void validarRango(int cantidad, String eje) throws BatallaNavalException {
        if (cantidad < MIN_CELDAS || cantidad > MAX_CELDAS) {
            throw new BatallaNavalException("La cantidad de celdas " + eje + " debe estar entre "
                    + MIN_CELDAS + " y " + MAX_CELDAS);
        }
    }

    private List<TipoBarco> armarFlota(Map<TipoBarco, Integer> cantidades) throws BatallaNavalException {
        List<TipoBarco> resultado = new ArrayList<>();
        for (Map.Entry<TipoBarco, Integer> entrada : cantidades.entrySet()) {
            agregarBarcos(resultado, entrada.getKey(), entrada.getValue());
        }
        if (resultado.isEmpty()) {
            throw new BatallaNavalException("La flota debe tener al menos un barco");
        }
        resultado.sort((a, b) -> b.getTamanio() - a.getTamanio());
        return resultado;
    }

    private void agregarBarcos(List<TipoBarco> lista, TipoBarco tipo, int cantidad) throws BatallaNavalException {
        if (cantidad < 0 || cantidad > MAX_POR_TIPO) {
            throw new BatallaNavalException("La cantidad de " + tipo.getNombre()
                    + " debe estar entre 0 y " + MAX_POR_TIPO);
        }
        for (int i = 0; i < cantidad; i++) {
            lista.add(tipo);
        }
    }

    private void validarDensidad() throws BatallaNavalException {
        double densidad = (double) getTamanioFlota() / (celdasHorizontales * celdasVerticales);
        if (densidad >= DENSIDAD_MAXIMA) {
            throw new BatallaNavalException("La flota no entra en el océano: ocupa el "
                    + Math.round(densidad * 100) + "% de las celdas y debe ser menor al 25%");
        }
    }

    private String lugarAlAzar() {
        Random random = new Random();
        StringBuilder nombre = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            nombre.append((char) ('A' + random.nextInt(26)));
        }
        return nombre.toString();
    }

    // ---------------- Acciones del usuario: las resuelve el estado ----------------

    public synchronized void ubicarBarco(int fila, int columna, Orientacion orientacion) throws BatallaNavalException {
        estado.ubicarBarco(this, fila, columna, orientacion);
        avisar(Eventos.cambioEnBatalla);
    }

    public synchronized void disparar(int fila, int columna) throws BatallaNavalException {
        estado.disparar(this, fila, columna);
        avisar(Eventos.cambioEnBatalla);
    }

    public synchronized void librarBatalla() throws BatallaNavalException {
        estado.librarBatalla(this);
        avisar(Eventos.cambioEnBatalla);
    }

    // ---------------- Usados por los estados ----------------

    public void ubicarProximoBarco(int fila, int columna, Orientacion orientacion) throws BatallaNavalException {
        TipoBarco proximo = getProximoBarco();
        if (proximo == null) {
            throw new BatallaNavalException("Todos sus barcos ya están posicionados");
        }
        aguasPropias.ubicar(proximo, fila, columna, orientacion);
        barcosPorUbicar.remove(0);
    }

    /** Una ronda: dispara el humano y, si no ganó, responde la máquina. */
    public void jugarRonda(int fila, int columna) throws BatallaNavalException {
        aguasEnemigas.recibirDisparo(fila, columna);
        if (aguasEnemigas.flotaHundida()) {
            cambiarEstado(new Ganada());
            return;
        }
        Celda objetivo = tactica.elegirDisparo(aguasPropias);
        aguasPropias.recibirDisparo(objetivo.getFila(), objetivo.getColumna());
        if (aguasPropias.flotaHundida()) {
            cambiarEstado(new Perdida());
            return;
        }
        ronda++;
    }

    public void cambiarEstado(EstadoBatalla nuevoEstado) {
        this.estado = nuevoEstado;
    }

    // ---------------- Consultas ----------------

    public String getLugar() {
        return lugar;
    }

    public Tactica getTactica() {
        return tactica;
    }

    public int getCeldasHorizontales() {
        return celdasHorizontales;
    }

    public int getCeldasVerticales() {
        return celdasVerticales;
    }

    public EstadoBatalla getEstado() {
        return estado;
    }

    public int getRonda() {
        return ronda;
    }

    public Oceano getAguasPropias() {
        return aguasPropias;
    }

    public Oceano getAguasEnemigas() {
        return aguasEnemigas;
    }

    /** El próximo barco a ubicar, o null si ya están todos. */
    public TipoBarco getProximoBarco() {
        return barcosPorUbicar.isEmpty() ? null : barcosPorUbicar.get(0);
    }

    public List<TipoBarco> getBarcosPorUbicar() {
        return new ArrayList<>(barcosPorUbicar);
    }

    public int getTamanioFlota() {
        int total = 0;
        for (TipoBarco tipo : flota) {
            total += tipo.getTamanio();
        }
        return total;
    }

    public int getPartesSanasPropias() {
        return getTamanioFlota() - aguasPropias.getImpactos();
    }

    public int getPartesSanasEnemigas() {
        return getTamanioFlota() - aguasEnemigas.getImpactos();
    }

    public boolean tieneLugar(String otroLugar) {
        return lugar.equalsIgnoreCase(otroLugar.trim());
    }
}
