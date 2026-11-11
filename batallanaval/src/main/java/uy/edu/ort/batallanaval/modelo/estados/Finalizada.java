package uy.edu.ort.batallanaval.modelo.estados;

import uy.edu.ort.batallanaval.excepciones.BatallaNavalException;
import uy.edu.ort.batallanaval.modelo.Batalla;
import uy.edu.ort.batallanaval.modelo.Orientacion;

/** Comportamiento común a Ganada y Perdida: ya no se puede jugar. */
public abstract class Finalizada extends EstadoBatalla {

    public Finalizada(String nombre) {
        super(nombre);
    }

    @Override
    public void ubicarBarco(Batalla batalla, int fila, int columna, Orientacion orientacion)
            throws BatallaNavalException {
        throw batallaFinalizada();
    }

    @Override
    public void disparar(Batalla batalla, int fila, int columna) throws BatallaNavalException {
        throw batallaFinalizada();
    }

    @Override
    public void librarBatalla(Batalla batalla) {
        // se puede entrar a ver la batalla terminada
    }

    private BatallaNavalException batallaFinalizada() {
        return new BatallaNavalException("La batalla ya finalizó: " + getNombre());
    }
}
