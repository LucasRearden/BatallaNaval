package uy.edu.ort.batallanaval.presenters;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;

import uy.edu.ort.batallanaval.excepciones.BatallaNavalException;
import uy.edu.ort.batallanaval.modelo.Batalla;
import uy.edu.ort.batallanaval.modelo.Orientacion;
import uy.edu.ort.batallanaval.modelo.TipoBarco;
import uy.edu.ort.batallanaval.utils.ConexionNavegador;

/** CU02 - segundo paso: "Desplegando la flota". */
@RestController
@RequestMapping("/despliegue")
@Scope("session")
public class PresentadorDespliegue extends PresentadorConBatalla {

    private Orientacion orientacion = Orientacion.HORIZONTAL;

    public PresentadorDespliegue(ConexionNavegador conexionNavegador) {
        super(conexionNavegador);
    }

    @GetMapping("/vistaConectada")
    public Commands inicializarVista(@SessionAttribute(name = "batalla", required = false) Batalla enSesion) {
        if (enSesion == null) {
            return Commands.create(navegar("index.html"));
        }
        observar(enSesion);
        if (!batalla.getEstado().permiteDespliegue()) {
            return Commands.create(navegar("batalla.html"));
        }
        orientacion = Orientacion.HORIZONTAL;
        Commands comandos = Commands.create(
                ComandosTablero.inicializar(batalla, "/despliegue/clickAguasPropias", "/despliegue/clickAguasEnemigas"),
                new Command("lugar", batalla.getLugar()),
                new Command("orientacion", orientacion.getNombre()));
        return comandos.add(situacion());
    }

    @PostMapping("/orientacion")
    public Commands cambiarOrientacion(@RequestParam(defaultValue = "") String valor) throws BatallaNavalException {
        batallaActual();
        orientacion = orientacionLlamada(valor);
        return Commands.create(leyenda());
    }

    @PostMapping("/clickAguasPropias")
    public Commands clickAguasPropias(@RequestParam int coordenadaVertical, @RequestParam int coordenadaHorizontal)
            throws BatallaNavalException {
        batallaActual().ubicarBarco(coordenadaVertical, coordenadaHorizontal, orientacion);
        return Commands.create(situacion());
    }

    @PostMapping("/clickAguasEnemigas")
    public Commands clickAguasEnemigas(@RequestParam int coordenadaVertical, @RequestParam int coordenadaHorizontal)
            throws BatallaNavalException {
        // en preparación el estado responde "No puede ubicar barcos en aguas enemigas"
        batallaActual().disparar(coordenadaVertical, coordenadaHorizontal);
        return Commands.create(situacion());
    }

    @PostMapping("/librarBatalla")
    public Commands librarBatalla() throws BatallaNavalException {
        batallaActual().librarBatalla();
        return Commands.create(navegar("batalla.html"));
    }

    /** Otro navegador ubicó un barco o empezó el combate en esta misma batalla. */
    @Override
    protected void batallaCambio() {
        if (batalla.getEstado().permiteDespliegue()) {
            conexionNavegador.enviarJSON(Commands.create(situacion()));
        } else {
            conexionNavegador.enviarJSON(Commands.create(new Command("batallaIniciada", "batalla.html")));
        }
    }

    // ---------------- Auxiliares ----------------

    private Command[] situacion() {
        List<Command> comandos = new ArrayList<>();
        comandos.add(barcosDisponibles());
        comandos.add(leyenda());
        comandos.addAll(ComandosTablero.aguasPropias(batalla.getAguasPropias()));
        return comandos.toArray(new Command[0]);
    }

    private Command barcosDisponibles() {
        List<String> nombres = new ArrayList<>();
        for (TipoBarco tipo : batalla.getBarcosPorUbicar()) {
            nombres.add(tipo.getNombre() + " (" + tipo.getTamanio() + ")");
        }
        return new Command("barcos", nombres);
    }

    private Command leyenda() {
        TipoBarco proximo = batalla.getProximoBarco();
        if (proximo == null) {
            return new Command("leyenda", "Batalla preparada");
        }
        return new Command("leyenda", "Posicione su " + proximo.getNombre().toLowerCase()
                + " en forma " + orientacion.getNombre());
    }

    private Orientacion orientacionLlamada(String nombre) throws BatallaNavalException {
        for (Orientacion o : Orientacion.values()) {
            if (o.getNombre().equals(nombre)) {
                return o;
            }
        }
        throw new BatallaNavalException("Seleccione una orientación");
    }
}
