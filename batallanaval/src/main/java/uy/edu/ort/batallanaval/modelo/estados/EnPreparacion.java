package uy.edu.ort.batallanaval.modelo.estados;

import uy.edu.ort.batallanaval.excepciones.BatallaNavalException;
import uy.edu.ort.batallanaval.modelo.Batalla;
import uy.edu.ort.batallanaval.modelo.Orientacion;

public class EnPreparacion extends EstadoBatalla {

    public EnPreparacion() {
        super("En preparación");
    }

    @Override
    public boolean permiteDespliegue() {
        return true;
    }

    @Override
    public void ubicarBarco(Batalla batalla, int fila, int columna, Orientacion orientacion)
            throws BatallaNavalException {
        batalla.ubicarProximoBarco(fila, columna, orientacion);
    }

    @Override
    public void disparar(Batalla batalla, int fila, int columna) throws BatallaNavalException {
        throw new BatallaNavalException("No puede ubicar barcos en aguas enemigas");
    }

    @Override
    public void librarBatalla(Batalla batalla) throws BatallaNavalException {
        if (batalla.getProximoBarco() != null) {
            throw new BatallaNavalException("Debe posicionar todos sus barcos primero");
        }
        batalla.cambiarEstado(new EnCombate());
    }
}
