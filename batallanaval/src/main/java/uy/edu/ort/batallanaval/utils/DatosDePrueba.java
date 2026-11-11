package uy.edu.ort.batallanaval.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import uy.edu.ort.batallanaval.excepciones.BatallaNavalException;
import uy.edu.ort.batallanaval.fachada.Fachada;
import uy.edu.ort.batallanaval.modelo.Batalla;
import uy.edu.ort.batallanaval.modelo.Celda;
import uy.edu.ort.batallanaval.modelo.Oceano;
import uy.edu.ort.batallanaval.modelo.Orientacion;
import uy.edu.ort.batallanaval.modelo.TipoBarco;
import uy.edu.ort.batallanaval.modelo.tacticas.CazaYAtaque;
import uy.edu.ort.batallanaval.modelo.tacticas.Espiral;
import uy.edu.ort.batallanaval.modelo.tacticas.Secuencial;
import uy.edu.ort.batallanaval.modelo.tacticas.Tactica;

public class DatosDePrueba {

    private Fachada f = Fachada.getInstancia();

    private TipoBarco portaaviones = new TipoBarco("Portaaviones", 5);
    private TipoBarco acorazado = new TipoBarco("Acorazado", 4);
    private TipoBarco crucero = new TipoBarco("Crucero", 3);
    private TipoBarco submarino = new TipoBarco("Submarino", 2);
    private TipoBarco destructor = new TipoBarco("Destructor", 1);

    private Tactica secuencial = new Secuencial();
    private Tactica espiral = new Espiral();
    private Tactica cazaYAtaque = new CazaYAtaque();

    public void cargarDatosDePrueba() throws BatallaNavalException {
        f.agregar(portaaviones);
        f.agregar(acorazado);
        f.agregar(crucero);
        f.agregar(submarino);
        f.agregar(destructor);

        f.agregar(secuencial);
        f.agregar(espiral);
        f.agregar(cazaYAtaque);

        f.agregar(batallaEnPreparacion());
        f.agregar(batallaEnCombate());
        f.agregar(batallaGanada());
        f.agregar(batallaPerdida());

        for (Batalla b : f.getBatallas()) {
            System.out.println("Precarga: " + b.getLugar() + " -> " + b.getEstado()
                    + " (ronda " + b.getRonda() + ")");
        }
    }

    // ---------------- Una batalla por estado ----------------

    private Batalla batallaEnPreparacion() throws BatallaNavalException {
        return new Batalla("Golfo de Vizcaya", espiral, 12, 8,
                flota(portaaviones, 1, submarino, 2, destructor, 1));
    }

    private Batalla batallaEnCombate() throws BatallaNavalException {
        Batalla b = new Batalla("Midway", cazaYAtaque, 10, 10,
                flota(acorazado, 1, crucero, 1, destructor, 2));
        b.ubicarBarco(1, 1, Orientacion.HORIZONTAL);   // acorazado
        b.ubicarBarco(4, 6, Orientacion.VERTICAL);     // crucero
        b.ubicarBarco(8, 1, Orientacion.HORIZONTAL);   // destructor
        b.ubicarBarco(8, 8, Orientacion.HORIZONTAL);   // destructor
        b.librarBatalla();
        b.disparar(5, 5);
        b.disparar(9, 0);
        b.disparar(2, 7);
        return b;
    }

    /** El humano apunta directo a los barcos enemigos y gana antes de que la máquina llegue a los suyos. */
    private Batalla batallaGanada() throws BatallaNavalException {
        Batalla b = new Batalla("Río de la Plata", secuencial, 6, 6,
                flota(crucero, 1, destructor, 1, null, 0));
        b.ubicarBarco(5, 0, Orientacion.HORIZONTAL);   // crucero, fila de arriba
        b.ubicarBarco(5, 5, Orientacion.HORIZONTAL);   // destructor
        b.librarBatalla();
        for (int i = 0; i < b.getTamanioFlota(); i++) {
            disparar(b, true);
        }
        return b;
    }

    /** La máquina (secuencial) hunde los barcos de la fila 0 mientras el humano tira al agua. */
    private Batalla batallaPerdida() throws BatallaNavalException {
        Batalla b = new Batalla("Mar del Coral", secuencial, 8, 8,
                flota(submarino, 1, destructor, 1, null, 0));
        b.ubicarBarco(0, 0, Orientacion.HORIZONTAL);   // submarino
        b.ubicarBarco(0, 3, Orientacion.HORIZONTAL);   // destructor
        b.librarBatalla();
        for (int i = 0; i < 4; i++) {
            disparar(b, false);
        }
        return b;
    }

    // ---------------- Auxiliares ----------------

    private Map<TipoBarco, Integer> flota(TipoBarco t1, int c1, TipoBarco t2, int c2, TipoBarco t3, int c3) {
        Map<TipoBarco, Integer> cantidades = new LinkedHashMap<>();
        cantidades.put(t1, c1);
        cantidades.put(t2, c2);
        if (t3 != null) {
            cantidades.put(t3, c3);
        }
        return cantidades;
    }

    /** Dispara a la primera celda enemiga sin disparar que tenga (o no) barco. */
    private void disparar(Batalla b, boolean aUnBarco) throws BatallaNavalException {
        Oceano enemigo = b.getAguasEnemigas();
        for (Celda celda : enemigo.getCeldasSinDisparar()) {
            if (celda.tieneBarco() == aUnBarco) {
                b.disparar(celda.getFila(), celda.getColumna());
                return;
            }
        }
    }
}
