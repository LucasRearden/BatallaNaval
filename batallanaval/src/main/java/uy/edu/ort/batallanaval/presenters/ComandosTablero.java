package uy.edu.ort.batallanaval.presenters;

import java.util.ArrayList;
import java.util.List;

import uy.edu.ort.batallanaval.dtos.InicializarDto;
import uy.edu.ort.batallanaval.dtos.ValorDto;
import uy.edu.ort.batallanaval.modelo.Barco;
import uy.edu.ort.batallanaval.modelo.Batalla;
import uy.edu.ort.batallanaval.modelo.Celda;
import uy.edu.ort.batallanaval.modelo.Oceano;

/**
 * Arma los comandos que entiende BattleShipBoard.js.
 * Lo usan los presentadores de despliegue (CU02) y de combate (CU03),
 * así no se repite este código en cada uno.
 */
public class ComandosTablero {

    public static Command inicializar(Batalla batalla, String escuchadorPropio, String escuchadorEnemigo) {
        return new Command("inicializar", new InicializarDto(batalla.getCeldasVerticales(),
                batalla.getCeldasHorizontales(), escuchadorPropio, escuchadorEnemigo));
    }

    public static List<Command> aguasPropias(Oceano oceano) {
        return celdas(oceano, "mostrarAguasPropias", true);
    }

    public static List<Command> aguasEnemigas(Oceano oceano, boolean revelarBarcos) {
        return celdas(oceano, "mostrarAguasEnemigas", revelarBarcos);
    }

    /** Formato pedido: "{5} {3} {1}" en el orden en que fueron hundidos. */
    public static Command bajasEnemigas(Oceano aguasEnemigas) {
        List<String> bajas = new ArrayList<>();
        for (Barco barco : aguasEnemigas.getHundidos()) {
            bajas.add("{" + barco.getTamanio() + "}");
        }
        return new Command("bajasEnemigas", bajas.isEmpty() ? "Ninguna" : String.join(" ", bajas));
    }

    private static List<Command> celdas(Oceano oceano, String comando, boolean mostrarBarcos) {
        List<Command> comandos = new ArrayList<>();
        for (Celda celda : oceano.getCeldas()) {
            String valor = celda.getContenido(mostrarBarcos).name().toLowerCase();   // nada, barco, agua, impacto
            comandos.add(new Command(comando, new ValorDto(celda.getFila(), celda.getColumna(), valor)));
        }
        return comandos;
    }
}
