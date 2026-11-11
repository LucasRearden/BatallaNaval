package uy.edu.ort.batallanaval.modelo.tacticas;

import java.util.List;
import java.util.Random;

import uy.edu.ort.batallanaval.modelo.Celda;
import uy.edu.ort.batallanaval.modelo.Oceano;

/**
 * Caza: dispara al azar hasta conseguir un impacto.
 * Ataque: mientras haya un barco tocado y no hundido, dispara a las celdas vecinas;
 * si ya hay dos impactos alineados, sigue en esa dirección.
 * El modo sale del propio océano (hay o no impactos sin hundir), por eso no guarda estado.
 */
public class CazaYAtaque extends Tactica {

    private static final int[][] DIRECCIONES = { {0, 1}, {1, 0}, {0, -1}, {-1, 0} };

    private final Random random = new Random();

    public CazaYAtaque() {
        super("Caza y ataque");
    }

    @Override
    public Celda elegirDisparo(Oceano oceanoRival) {
        List<Celda> heridos = oceanoRival.getImpactosSinHundir();
        if (heridos.isEmpty()) {
            return cazar(oceanoRival);
        }
        Celda objetivo = seguirLinea(oceanoRival, heridos);
        if (objetivo == null) {
            objetivo = probarVecinas(oceanoRival, heridos);
        }
        return objetivo != null ? objetivo : cazar(oceanoRival);
    }

    private Celda cazar(Oceano oceano) {
        List<Celda> libres = oceano.getCeldasSinDisparar();
        return libres.get(random.nextInt(libres.size()));
    }

    /** Con dos impactos alineados ya se conoce la orientación: se avanza hasta el extremo libre. */
    private Celda seguirLinea(Oceano oceano, List<Celda> heridos) {
        for (Celda herido : heridos) {
            for (int[] d : DIRECCIONES) {
                if (oceano.esImpactoSinHundir(herido.getFila() - d[0], herido.getColumna() - d[1])) {
                    Celda extremo = extremoLibre(oceano, herido, d);
                    if (extremo != null) {
                        return extremo;
                    }
                }
            }
        }
        return null;
    }

    private Celda extremoLibre(Oceano oceano, Celda desde, int[] d) {
        int fila = desde.getFila();
        int columna = desde.getColumna();
        while (oceano.esImpactoSinHundir(fila, columna)) {
            fila += d[0];
            columna += d[1];
        }
        return oceano.celdaSinDisparar(fila, columna);
    }

    /** Un solo impacto: se prueba alrededor para descubrir la orientación. */
    private Celda probarVecinas(Oceano oceano, List<Celda> heridos) {
        for (Celda herido : heridos) {
            for (int[] d : DIRECCIONES) {
                Celda vecina = oceano.celdaSinDisparar(herido.getFila() + d[0], herido.getColumna() + d[1]);
                if (vecina != null) {
                    return vecina;
                }
            }
        }
        return null;
    }
}
