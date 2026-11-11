package uy.edu.ort.batallanaval;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import uy.edu.ort.batallanaval.excepciones.BatallaNavalException;
import uy.edu.ort.batallanaval.utils.DatosDePrueba;

@SpringBootApplication
public class BatallanavalApplication {

    public static void main(String[] args) throws BatallaNavalException {
        SpringApplication.run(BatallanavalApplication.class, args);
        new DatosDePrueba().cargarDatosDePrueba();
    }
}
