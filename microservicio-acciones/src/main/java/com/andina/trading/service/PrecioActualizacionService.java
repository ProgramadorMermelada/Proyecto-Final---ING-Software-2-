package com.andina.trading.service;

import com.andina.trading.model.Accion;
import com.andina.trading.repository.AccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class PrecioActualizacionService {

    @Autowired
    private AccionRepository accionRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private Random random = new Random();

    /**
     * Actualiza precios cada 3 segundos y notifica a clientes
     */
    @Scheduled(fixedRate = 3000) // Cada 3 segundos
    public void actualizarPreciosEnTiempoReal() {
        List<Accion> acciones = accionRepository.findAll();

        if (acciones.isEmpty()) {
            return; // No hacer nada si no hay acciones
        }

        for (Accion accion : acciones) {
            // Simular variación de precio (-2% a +2%)
            double variacion = (random.nextDouble() * 4) - 2;
            double precioAnterior = accion.getPrecioActual();
            double nuevoPrecio = precioAnterior * (1 + variacion / 100);

            // No permitir precios negativos
            if (nuevoPrecio < 0.01) {
                nuevoPrecio = 0.01;
            }

            accion.setPrecioActual(nuevoPrecio);
            accion.setVariacionDia(accion.getVariacionDia() + variacion);

            accionRepository.save(accion);
        }

        // Enviar todas las acciones actualizadas por WebSocket
        messagingTemplate.convertAndSend("/topic/acciones", acciones);
        
        System.out.println("📊 " + acciones.size() + " precios actualizados y enviados");
    }
}