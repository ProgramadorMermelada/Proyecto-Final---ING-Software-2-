package com.andina.trading.controller;

import com.andina.trading.model.Accion;
import com.andina.trading.service.AccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/acciones")
@CrossOrigin(origins = "http://localhost:3000")
public class AccionController {
    
    @Autowired
    private AccionService accionService;
    
    // Obtener todas las acciones disponibles
    @GetMapping
    public List<Accion> obtenerTodasAcciones() {
        return accionService.obtenerTodasAcciones();
    }
    
    // Obtener acciones por país
    @GetMapping("/pais/{pais}")
    public List<Accion> obtenerAccionesPorPais(@PathVariable String pais) {
        return accionService.obtenerAccionesPorPais(pais);
    }
    
    // Obtener una acción específica
    @GetMapping("/{id}")
    public ResponseEntity<Accion> obtenerAccionPorId(@PathVariable Long id) {
        return accionService.obtenerAccionPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Buscar acción por símbolo
    @GetMapping("/simbolo/{simbolo}")
    public ResponseEntity<Accion> obtenerAccionPorSimbolo(@PathVariable String simbolo) {
        return accionService.obtenerAccionPorSimbolo(simbolo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Crear nueva acción (solo para admin)
    @PostMapping
    public Accion crearAccion(@RequestBody Accion accion) {
        return accionService.crearAccion(accion);
    }
    
    // Actualizar precio de acción (simulación de mercado)
    @PutMapping("/{id}/actualizar-precio")
    public ResponseEntity<Accion> actualizarPrecio(
            @PathVariable Long id,
            @RequestParam double nuevoPrecio) {
        return accionService.actualizarPrecio(id, nuevoPrecio)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Inicializar acciones de prueba
    @PostMapping("/inicializar-demo")
    public ResponseEntity<String> inicializarAccionesDemo() {
        accionService.inicializarAccionesDemo();
        return ResponseEntity.ok("Acciones demo creadas exitosamente");
    }
}
