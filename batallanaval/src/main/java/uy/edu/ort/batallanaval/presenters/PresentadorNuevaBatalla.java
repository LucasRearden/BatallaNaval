package uy.edu.ort.batallanaval.presenters;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import uy.edu.ort.batallanaval.dtos.TipoBarcoDto;
import uy.edu.ort.batallanaval.excepciones.BatallaNavalException;
import uy.edu.ort.batallanaval.fachada.Fachada;
import uy.edu.ort.batallanaval.modelo.Batalla;
import uy.edu.ort.batallanaval.modelo.TipoBarco;
import uy.edu.ort.batallanaval.modelo.tacticas.Tactica;

/** CU02 - primer paso: datos básicos de la nueva batalla. */
@RestController
@RequestMapping("/nuevaBatalla")
@Scope("session")
public class PresentadorNuevaBatalla {

    private List<Tactica> tacticas = new ArrayList<>();
    private List<TipoBarco> tiposBarco = new ArrayList<>();

    @GetMapping("/vistaConectada")
    public Commands inicializarVista() {
        return Commands.create(tacticas(), tiposBarco());
    }

    @PostMapping("/continuar")
    public Commands continuar(@RequestParam(defaultValue = "") String lugar,
                              @RequestParam(defaultValue = "-1") int posTactica,
                              @RequestParam(defaultValue = "") String celdasHorizontales,
                              @RequestParam(defaultValue = "") String celdasVerticales,
                              @RequestParam(defaultValue = "") String cantidades,
                              HttpSession sesion) throws BatallaNavalException {
        Tactica tactica = (posTactica >= 0 && posTactica < tacticas.size()) ? tacticas.get(posTactica) : null;
        Batalla batalla = new Batalla(lugar, tactica,
                numero(celdasHorizontales, "celdas horizontales"),
                numero(celdasVerticales, "celdas verticales"),
                flota(cantidades));
        Fachada.getInstancia().agregar(batalla);
        sesion.setAttribute("batalla", batalla);
        return Commands.create(new Command("navegar", "despliegue.html"));
    }

    @PostMapping("/volver")
    public Commands volver() {
        return Commands.create(new Command("navegar", "index.html"));
    }

    private Map<TipoBarco, Integer> flota(String cantidades) throws BatallaNavalException {
        String[] partes = cantidades.split(",", -1);
        if (partes.length != tiposBarco.size()) {
            throw new BatallaNavalException("Ingrese la cantidad de cada tipo de barco");
        }
        Map<TipoBarco, Integer> flota = new LinkedHashMap<>();
        for (int i = 0; i < tiposBarco.size(); i++) {
            TipoBarco tipo = tiposBarco.get(i);
            flota.put(tipo, numero(partes[i], "la cantidad de " + tipo.getNombre()));
        }
        return flota;
    }

    private int numero(String texto, String campo) throws BatallaNavalException {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new BatallaNavalException("Ingrese un número válido en " + campo);
        }
    }

    private Command tacticas() {
        tacticas = new ArrayList<>(Fachada.getInstancia().getTacticas());
        List<String> nombres = new ArrayList<>();
        for (Tactica t : tacticas) {
            nombres.add(t.getNombre());
        }
        return new Command("tacticas", nombres);
    }

    private Command tiposBarco() {
        tiposBarco = new ArrayList<>(Fachada.getInstancia().getTiposBarco());
        List<TipoBarcoDto> dtos = new ArrayList<>();
        for (TipoBarco t : tiposBarco) {
            dtos.add(new TipoBarcoDto(t));
        }
        return new Command("tiposBarco", dtos);
    }
}
