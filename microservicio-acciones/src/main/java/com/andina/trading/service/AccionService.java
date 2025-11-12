package com.andina.trading.service;

import com.andina.trading.model.Accion;
import com.andina.trading.repository.AccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AccionService {
    
    @Autowired
    private AccionRepository accionRepository;
    
    public List<Accion> obtenerTodasAcciones() {
        return accionRepository.findAll();
    }
    
    public List<Accion> obtenerAccionesPorPais(String pais) {
        return accionRepository.findByPais(pais);
    }
    
    public Optional<Accion> obtenerAccionPorId(Long id) {
        return accionRepository.findById(id);
    }
    
    public Optional<Accion> obtenerAccionPorSimbolo(String simbolo) {
        return accionRepository.findBySimbolo(simbolo);
    }
    
    public Accion crearAccion(Accion accion) {
        accion.setUltimaActualizacion(LocalDateTime.now());
        return accionRepository.save(accion);
    }
    
    public Optional<Accion> actualizarPrecio(Long id, double nuevoPrecio) {
        return accionRepository.findById(id).map(accion -> {
            double variacion = ((nuevoPrecio - accion.getPrecioActual()) / accion.getPrecioActual()) * 100;
            accion.setPrecioActual(nuevoPrecio);
            accion.setVariacionDia(variacion);
            accion.setUltimaActualizacion(LocalDateTime.now());
            return accionRepository.save(accion);
        });
    }
    
    public void inicializarAccionesDemo() {
        // Limpiar acciones existentes
        accionRepository.deleteAll();
        
        // Crear acciones demo para cada país
        List<Accion> accionesDemo = List.of(
            // Colombia
            new Accion("Bancolombia", "BCOL", 45000.50, 2.3, "Colombia", "Bogotá"),
            new Accion("Ecopetrol", "ECOPETROL", 3200.75, -1.5, "Colombia", "Bogotá"),
            new Accion("Grupo Aval", "AVAL", 1250.30, 0.8, "Colombia", "Bogotá"),
            
            // Ecuador
            new Accion("Banco Pichincha", "BPIC", 25.40, 1.2, "Ecuador", "Quito"),
            new Accion("Corporación Favorita", "CFAV", 18.90, -0.5, "Ecuador", "Quito"),
            
            // Perú
            new Accion("Credicorp", "BAP", 180.50, 3.1, "Perú", "Lima"),
            new Accion("Southern Copper", "SCCO", 95.20, 2.7, "Perú", "Lima"),
            new Accion("Buenaventura", "BVN", 12.80, -2.1, "Perú", "Lima"),
            
            // Venezuela
            new Accion("PDVSA", "PDVSA", 5.30, 1.5, "Venezuela", "Caracas"),
            new Accion("Banco Mercantil", "BMER", 8.75, 0.3, "Venezuela", "Caracas")
        );
        
        accionRepository.saveAll(accionesDemo);
    }
}
