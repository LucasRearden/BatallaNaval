package uy.edu.ort.batallanaval.dtos;

import uy.edu.ort.batallanaval.modelo.Batalla;

/** Un renglón de la lista de batallas de la Sala de operaciones. */
public class BatallaSalaDto {

    private String lugar;
    private String dimensiones;
    private String estado;
    private String integridadPropia;
    private String integridadEnemiga;

    public BatallaSalaDto(Batalla batalla) {
        this.lugar = batalla.getLugar();
        this.dimensiones = batalla.getCeldasHorizontales() + " x " + batalla.getCeldasVerticales();
        this.estado = batalla.getEstado().getNombre();
        this.integridadPropia = batalla.getPartesSanasPropias() + " / " + batalla.getTamanioFlota();
        this.integridadEnemiga = batalla.getPartesSanasEnemigas() + " / " + batalla.getTamanioFlota();
    }

    public String getLugar() {
        return lugar;
    }

    public String getDimensiones() {
        return dimensiones;
    }

    public String getEstado() {
        return estado;
    }

    public String getIntegridadPropia() {
        return integridadPropia;
    }

    public String getIntegridadEnemiga() {
        return integridadEnemiga;
    }
}
