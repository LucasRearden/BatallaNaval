package uy.edu.ort.batallanaval.presenters;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import uy.edu.ort.batallanaval.excepciones.BatallaNavalException;
import uy.edu.ort.batallanaval.modelo.Batalla;
import uy.edu.ort.batallanaval.observador.Observable;
import uy.edu.ort.batallanaval.observador.Observador;
import uy.edu.ort.batallanaval.utils.ConexionNavegador;

/**
 * Lo común a los presentadores que trabajan sobre la batalla elegida
 * (despliegue de la flota y combate): conexión SSE, observar la batalla,
 * volver a la sala. Cada subclase decide qué mostrar cuando la batalla cambia.
 */
public abstract class PresentadorConBatalla implements Observador {

    protected final ConexionNavegador conexionNavegador;
    protected Batalla batalla;

    public PresentadorConBatalla(ConexionNavegador conexionNavegador) {
        this.conexionNavegador = conexionNavegador;
    }

    @GetMapping(value = "/registrarSSE", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter registrarSSE() {
        conexionNavegador.conectarSSE();
        return conexionNavegador.getConexionSSE();
    }

    @PostMapping("/volver")
    public Commands volver() {
        return Commands.create(navegar("index.html"));
    }

    @Override
    public void actualizar(Object evento, Observable origen) {
        if (origen == batalla) {
            batallaCambio();
        }
    }

    /** Otro navegador (o este mismo) modificó la batalla que se está mostrando. */
    protected abstract void batallaCambio();

    protected void observar(Batalla nueva) {
        if (batalla != null && batalla != nueva) {
            batalla.quitarObservador(this);
        }
        batalla = nueva;
        batalla.agregarObservador(this);
    }

    protected Batalla batallaActual() throws BatallaNavalException {
        if (batalla == null) {
            throw new BatallaNavalException("No hay una batalla seleccionada");
        }
        return batalla;
    }

    protected Command navegar(String url) {
        return new Command("navegar", url);
    }
}
