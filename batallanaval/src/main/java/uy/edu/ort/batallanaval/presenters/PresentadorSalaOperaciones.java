package uy.edu.ort.batallanaval.presenters;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.servlet.http.HttpSession;
import uy.edu.ort.batallanaval.dtos.BatallaSalaDto;
import uy.edu.ort.batallanaval.dtos.ResumenSalaDto;
import uy.edu.ort.batallanaval.excepciones.BatallaNavalException;
import uy.edu.ort.batallanaval.fachada.Fachada;
import uy.edu.ort.batallanaval.modelo.Batalla;
import uy.edu.ort.batallanaval.observador.Observable;
import uy.edu.ort.batallanaval.observador.Observador;
import uy.edu.ort.batallanaval.utils.ConexionNavegador;

/**
 * CU01 - Sala de operaciones.
 * Observa a la Fachada (se agregó una batalla) y a cada Batalla (cambió algo)
 * y empuja la información actualizada al navegador por SSE.
 */
@RestController
@RequestMapping("/sala")
@Scope("session")
public class PresentadorSalaOperaciones implements Observador {

    private final ConexionNavegador conexionNavegador;
    private List<Batalla> batallas = new ArrayList<>();

    public PresentadorSalaOperaciones(ConexionNavegador conexionNavegador) {
        this.conexionNavegador = conexionNavegador;
    }

    @GetMapping(value = "/registrarSSE", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter registrarSSE() {
        conexionNavegador.conectarSSE();
        return conexionNavegador.getConexionSSE();
    }

    @GetMapping("/vistaConectada")
    public Commands inicializarVista() {
        Fachada.getInstancia().agregarObservador(this);
        return Commands.create(resumen(), batallas());
    }

    @PostMapping("/entrar")
    public Commands entrar(@RequestParam int posBatalla, HttpSession sesion) throws BatallaNavalException {
        if (posBatalla < 0 || posBatalla >= batallas.size()) {
            throw new BatallaNavalException("Seleccione una batalla");
        }
        Batalla batalla = batallas.get(posBatalla);
        sesion.setAttribute("batalla", batalla);
        String vista = batalla.getEstado().permiteDespliegue() ? "despliegue.html" : "batalla.html";
        return Commands.create(new Command("navegar", vista));
    }

    @PostMapping("/nuevaBatalla")
    public Commands nuevaBatalla() {
        return Commands.create(new Command("navegar", "nuevaBatalla.html"));
    }

    @Override
    public void actualizar(Object evento, Observable origen) {
        conexionNavegador.enviarJSON(Commands.create(resumen(), batallas()));
    }

    private Command resumen() {
        Fachada f = Fachada.getInstancia();
        return new Command("resumen",
                new ResumenSalaDto(f.getCantidadGanadas(), f.getCantidadPerdidas(), f.getPorcentajeExito()));
    }

    /** También se suscribe a cada batalla (agregarObservador no lo registra dos veces). */
    private Command batallas() {
        batallas = Fachada.getInstancia().getBatallas();
        List<BatallaSalaDto> dtos = new ArrayList<>();
        for (Batalla b : batallas) {
            b.agregarObservador(this);
            dtos.add(new BatallaSalaDto(b));
        }
        return new Command("batallas", dtos);
    }
}
