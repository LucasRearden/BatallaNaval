package uy.edu.ort.batallanaval.modelo.estados;

import uy.edu.ort.batallanaval.excepciones.BatallaNavalException;
import uy.edu.ort.batallanaval.modelo.Batalla;
import uy.edu.ort.batallanaval.modelo.Orientacion;

/**
 * State: cada estado decide qué acciones permite la batalla
 * y qué pasa cuando el usuario hace click en cada océano.
 */
public abstract class EstadoBatalla {

    private String nombre;

    public EstadoBatalla(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    /** Click en aguas propias. */
    public abstract void ubicarBarco(Batalla batalla, int fila, int columna, Orientacion orientacion)
            throws BatallaNavalException;

    /** Click en aguas enemigas. */
    public abstract void disparar(Batalla batalla, int fila, int columna) throws BatallaNavalException;

    /** Botón "Librar batalla". */
    public abstract void librarBatalla(Batalla batalla) throws BatallaNavalException;

    /** Si al entrar a la batalla se sigue desplegando la flota (CU02) en vez de combatir (CU03). */
    public boolean permiteDespliegue() {
        return false;
    }

    public boolean esGanada() {
        return false;
    }

    public boolean esPerdida() {
        return false;
    }

    /** Si al mostrar las aguas enemigas se ven los barcos no descubiertos. */
    public boolean revelaFlotaEnemiga() {
        return false;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
