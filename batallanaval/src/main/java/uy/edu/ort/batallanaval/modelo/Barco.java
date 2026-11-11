package uy.edu.ort.batallanaval.modelo;

import java.util.ArrayList;
import java.util.List;

public class Barco {

    private TipoBarco tipo;
    private List<Celda> celdas;

    Barco(TipoBarco tipo, List<Celda> celdas) {
        this.tipo = tipo;
        this.celdas = new ArrayList<>(celdas);
        for (Celda celda : celdas) {
            celda.setBarco(this);
        }
    }

    public TipoBarco getTipo() {
        return tipo;
    }

    public int getTamanio() {
        return tipo.getTamanio();
    }

    public int getPartesSanas() {
        int sanas = 0;
        for (Celda celda : celdas) {
            if (!celda.fueDisparada()) {
                sanas++;
            }
        }
        return sanas;
    }

    public boolean estaHundido() {
        return getPartesSanas() == 0;
    }
}
