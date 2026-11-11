package uy.edu.ort.batallanaval.modelo.estados;

public class Ganada extends Finalizada {

    public Ganada() {
        super("Ganada");
    }

    @Override
    public boolean esGanada() {
        return true;
    }
}
