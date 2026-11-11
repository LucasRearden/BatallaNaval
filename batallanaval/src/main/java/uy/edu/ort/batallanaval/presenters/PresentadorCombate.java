package uy.edu.ort.batallanaval.presenters;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;

import uy.edu.ort.batallanaval.excepciones.BatallaNavalException;
import uy.edu.ort.batallanaval.modelo.Batalla;
import uy.edu.ort.batallanaval.modelo.Orientacion;
import uy.edu.ort.batallanaval.utils.ConexionNavegador;

/** CU03 - Librar batalla (también sirve para ver batallas ganadas o perdidas). */
@RestController
@RequestMapping("/combate")
@Scope("session")
public class PresentadorCombate extends PresentadorConBatalla {

    public PresentadorCombate(ConexionNavegador conexionNavegador) {
        super(conexionNavegador);
    }

    @GetMapping("/vistaConectada")
    public Commands inicializarVista(@SessionAttribute(name = "batalla", required = false) Batalla enSesion) {
        if (enSesion == null) {
            return Commands.create(navegar("index.html"));
        }
        observar(enSesion);
        if (batalla.getEstado().permiteDespliegue()) {
            return Commands.create(navegar("despliegue.html"));
        }
        Commands comandos = Commands.create(
                ComandosTablero.inicializar(batalla, "/combate/clickAguasPropias", "/combate/clickAguasEnemigas"),
                new Command("lugar", batalla.getLugar()));
        return comandos.add(situacion());
    }

    /** Una ronda completa: el disparo del humano y la respuesta de la máquina. */
    @PostMapping("/clickAguasEnemigas")
    public Commands clickAguasEnemigas(@RequestParam int coordenadaVertical, @RequestParam int coordenadaHorizontal)
            throws BatallaNavalException {
        batallaActual().disparar(coordenadaVertical, coordenadaHorizontal);
        return Commands.create(situacion());
    }

    @PostMapping("/clickAguasPropias")
    public Commands clickAguasPropias(@RequestParam int coordenadaVertical, @RequestParam int coordenadaHorizontal)
            throws BatallaNavalException {
        // en combate (o terminada) el estado rechaza la acción con el mensaje correspondiente
        batallaActual().ubicarBarco(coordenadaVertical, coordenadaHorizontal, Orientacion.HORIZONTAL);
        return Commands.create(situacion());
    }

    @Override
    protected void batallaCambio() {
        conexionNavegador.enviarJSON(Commands.create(situacion()));
    }

    private Command[] situacion() {
        boolean revelar = batalla.getEstado().revelaFlotaEnemiga();
        List<Command> comandos = new ArrayList<>();
        comandos.add(new Command("ronda", batalla.getRonda()));
        comandos.add(new Command("estado", batalla.getEstado().getNombre()));
        comandos.addAll(ComandosTablero.aguasPropias(batalla.getAguasPropias()));
        comandos.addAll(ComandosTablero.aguasEnemigas(batalla.getAguasEnemigas(), revelar));
        comandos.add(ComandosTablero.bajasEnemigas(batalla.getAguasEnemigas()));
        return comandos.toArray(new Command[0]);
    }
}
