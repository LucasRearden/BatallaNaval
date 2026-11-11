package uy.edu.ort.batallanaval.modelo.estados;

import uy.edu.ort.batallanaval.excepciones.BatallaNavalException;
import uy.edu.ort.batallanaval.modelo.Batalla;
import uy.edu.ort.batallanaval.modelo.Orientacion;

public class EnCombate extends EstadoBatalla {

    public EnCombate() {
        super("En combate");
    }

    @Override
    public void ubicarBarco(Batalla batalla, int fila, int columna, Orientacion orientacion)
            throws BatallaNavalException {
        throw new BatallaNavalException("No puede disparar a sus propias aguas");
    }

    @Override
    public void disparar(Batalla batalla, int fila, int columna) throws BatallaNavalException {
        batalla.jugarRonda(fila, columna);
    }

    @Override
    public void librarBatalla(Batalla batalla) {
        // ya está en combate: no hay nada que hacer
    }
}
