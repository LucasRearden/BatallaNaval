package uy.edu.ort.batallanaval.utils;


import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope("session")
public class ConexionNavegador {

    private List<SseEmitter> conexiones = new ArrayList<>();

    public void conectarSSE() {
        long timeOut = 30 * 60 * 1000; //30 minutos de timeOut
        SseEmitter nuevaConexion = new SseEmitter(timeOut);

        nuevaConexion.onCompletion(() -> {
            synchronized (conexiones) {
                conexiones.remove(nuevaConexion);
            }
        });

        nuevaConexion.onTimeout(() -> {
            synchronized (conexiones) {
                conexiones.remove(nuevaConexion);
            }
        });

        nuevaConexion.onError((e) -> {
            synchronized (conexiones) {
                conexiones.remove(nuevaConexion);
            }
        });

        synchronized (conexiones) {
            conexiones.add(nuevaConexion);
        }
    }

    public void cerrarConexion() {
        synchronized (conexiones) {
            for (SseEmitter emiter : new ArrayList<>(conexiones)) {
                try {
                    emiter.complete();
                } catch (Exception e) {
                }
            }
            conexiones.clear();
        }
    }

    public SseEmitter getConexionSSE() {
        synchronized (conexiones) {
            return conexiones.isEmpty() ? null : conexiones.get(conexiones.size() - 1);
        }
    }

    public void enviarJSON(Object informacion) {
        try {
            String json = new ObjectMapper().writeValueAsString(informacion);
            enviarMensaje(json);

        } catch (JacksonException e) {
            System.out.println("Error al convertir a JSON:" + e.getMessage());

        }

    }

    public void enviarMensaje(String mensaje) {
        List<SseEmitter> aRemover = new ArrayList<>();
        List<SseEmitter> copiaConexiones;

        synchronized (conexiones) {
            copiaConexiones = new ArrayList<>(conexiones);
        }

        for (SseEmitter emiter : copiaConexiones) {
            try {
                emiter.send(mensaje);
            } catch (Throwable e) {
                System.out.println("Error al enviar mensaje:" + e.getMessage());
                aRemover.add(emiter);
            }
        }

        if (!aRemover.isEmpty()) {
            synchronized (conexiones) {
                conexiones.removeAll(aRemover);
            }
        }
    }
}
