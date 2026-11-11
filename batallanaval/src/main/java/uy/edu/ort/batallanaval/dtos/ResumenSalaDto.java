package uy.edu.ort.batallanaval.dtos;

/** Totales que se muestran arriba de la Sala de operaciones. */
public class ResumenSalaDto {

    private int ganadas;
    private int perdidas;
    private int exito;

    public ResumenSalaDto(int ganadas, int perdidas, int exito) {
        this.ganadas = ganadas;
        this.perdidas = perdidas;
        this.exito = exito;
    }

    public int getGanadas() {
        return ganadas;
    }

    public int getPerdidas() {
        return perdidas;
    }

    public int getExito() {
        return exito;
    }
}
