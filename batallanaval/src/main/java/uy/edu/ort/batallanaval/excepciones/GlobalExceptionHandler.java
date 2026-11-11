package uy.edu.ort.batallanaval.excepciones;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice

public class GlobalExceptionHandler {
    private final int errorCodeStatus = 299; //cambiar si se quiere otro codigo para indicar excepcion de aplicacion

    @ExceptionHandler(BatallaNavalException.class)
    public ResponseEntity<String> manejarException(BatallaNavalException ex) {

        return ResponseEntity.status(errorCodeStatus).body(ex.getMessage());
    }
}