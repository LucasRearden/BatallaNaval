package uy.edu.ort.batallanaval.dtos;

import uy.edu.ort.batallanaval.modelo.TipoBarco;

public class TipoBarcoDto {

    private String nombre;
    private int tamanio;

    public TipoBarcoDto(TipoBarco tipo) {
        this.nombre = tipo.getNombre();
        this.tamanio = tipo.getTamanio();
    }

    public String getNombre() {
        return nombre;
    }

    public int getTamanio() {
        return tamanio;
    }
}
