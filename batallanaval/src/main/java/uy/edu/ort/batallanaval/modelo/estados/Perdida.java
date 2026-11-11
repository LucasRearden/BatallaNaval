package uy.edu.ort.batallanaval.modelo.estados;

public class Perdida extends Finalizada {

    public Perdida() {
        super("Perdida");
    }

    @Override
    public boolean esPerdida() {
        return true;
    }

    @Override
    public boolean revelaFlotaEnemiga() {
        return true;
    }
}
