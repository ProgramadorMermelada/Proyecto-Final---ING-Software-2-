package com.andina.trading.controller;

import com.andina.trading.model.Inversionista;
import com.andina.trading.service.InversionistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/inversionistas")
public class InversionistaController {

    @Autowired
    private InversionistaService inversionistaService;

    // ========== ENDPOINTS ORIGINALES ==========
    
    @PostMapping
    public ResponseEntity<Inversionista> crearInversionista(@RequestBody Inversionista inversionista) {
        try {
            System.out.println("========================================");
            System.out.println("📥 RECIBIDO en InversionistaController:");
            System.out.println("   Nombre: " + inversionista.getNombre());
            System.out.println("   Email: " + inversionista.getEmail());
            System.out.println("   Teléfono: " + inversionista.getTelefono());
            System.out.println("   Saldo Disponible: " + inversionista.getSaldoDisponible());
            
            Inversionista creado = inversionistaService.crearInversionista(
                inversionista.getNombre(), 
                inversionista.getSaldoDisponible()
            );
            
            System.out.println("✅ Inversionista creado con ID: " + creado.getId());
            System.out.println("========================================");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (Exception e) {
            System.err.println("❌ ERROR al crear inversionista:");
            System.err.println("   Tipo: " + e.getClass().getName());
            System.err.println("   Mensaje: " + e.getMessage());
            e.printStackTrace();
            System.out.println("========================================");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Inversionista>> obtenerTodosInversionistas() {
        return ResponseEntity.ok(inversionistaService.obtenerTodosInversionistas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inversionista> obtenerInversionistaPorId(@PathVariable Long id) {
        Optional<Inversionista> inversionista = inversionistaService.obtenerInversionistaPorId(id);
        return inversionista.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("/{id}/comprar")
    public ResponseEntity<String> comprarAcciones(@PathVariable Long id, @RequestParam double monto) {
        try {
            boolean exito = inversionistaService.comprarAcciones(id, monto);
            if (exito) {
                return ResponseEntity.ok("Compra exitosa");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No hay suficiente saldo para la compra");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ========== NUEVOS ENDPOINTS PARA INTEGRACIÓN CON COMISIONISTA ==========
    
    /**
     * Vincular inversionista con comisionista (cuando se crea un contrato)
     */
    @PostMapping("/{id}/vincular-comisionista")
    public ResponseEntity<?> vincularComisionista(
            @PathVariable Long id,
            @RequestBody Map<String, Long> body) {
        
        try {
            Long comisionistaId = body.get("comisionistaId");
            Long contratoId = body.get("contratoId");
            
            boolean vinculado = inversionistaService.vincularComisionista(id, comisionistaId, contratoId);
            
            if (vinculado) {
                Map<String, Object> response = new HashMap<>();
                response.put("mensaje", "Comisionista vinculado exitosamente");
                response.put("inversionistaId", id);
                response.put("comisionistaId", comisionistaId);
                response.put("contratoId", contratoId);
                return ResponseEntity.ok(response);
            }
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Inversionista no encontrado"));
                
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Validar si el inversionista tiene saldo suficiente
     * (usado por el comisionista antes de ejecutar órdenes)
     */
    @GetMapping("/{id}/validar-saldo")
    public ResponseEntity<?> validarSaldo(
            @PathVariable Long id,
            @RequestParam Double monto) {
        
        Optional<Inversionista> optInversionista = inversionistaService.obtenerInversionistaPorId(id);
        
        if (optInversionista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Inversionista no encontrado"));
        }
        
        Inversionista inversionista = optInversionista.get();
        boolean tieneSaldo = inversionista.tieneSaldoSuficiente(monto);
        
        Map<String, Object> response = new HashMap<>();
        response.put("inversionistaId", id);
        response.put("saldoDisponible", inversionista.getSaldoDisponible());
        response.put("montoRequerido", monto);
        response.put("tieneSaldoSuficiente", tieneSaldo);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Descontar saldo del inversionista (cuando se ejecuta una orden de compra)
     */
    @PostMapping("/{id}/descontar-saldo")
    public ResponseEntity<?> descontarSaldo(
            @PathVariable Long id,
            @RequestBody Map<String, Double> body) {
        
        try {
            Double monto = body.get("monto");
            boolean descontado = inversionistaService.descontarSaldo(id, monto);
            
            if (descontado) {
                Optional<Inversionista> inv = inversionistaService.obtenerInversionistaPorId(id);
                Map<String, Object> response = new HashMap<>();
                response.put("mensaje", "Saldo descontado exitosamente");
                response.put("montoDescontado", monto);
                response.put("nuevoSaldo", inv.get().getSaldoDisponible());
                return ResponseEntity.ok(response);
            }
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Saldo insuficiente"));
                
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Agregar saldo al inversionista (cuando se ejecuta una orden de venta)
     */
    @PostMapping("/{id}/agregar-saldo")
    public ResponseEntity<?> agregarSaldo(
            @PathVariable Long id,
            @RequestBody Map<String, Double> body) {
        
        try {
            Double monto = body.get("monto");
            boolean agregado = inversionistaService.agregarSaldo(id, monto);
            
            if (agregado) {
                Optional<Inversionista> inv = inversionistaService.obtenerInversionistaPorId(id);
                Map<String, Object> response = new HashMap<>();
                response.put("mensaje", "Saldo agregado exitosamente");
                response.put("montoAgregado", monto);
                response.put("nuevoSaldo", inv.get().getSaldoDisponible());
                return ResponseEntity.ok(response);
            }
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Inversionista no encontrado"));
                
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Obtener inversionista con información de su comisionista
     */
    @GetMapping("/{id}/comisionista")
    public ResponseEntity<?> obtenerComisionista(@PathVariable Long id) {
        Optional<Inversionista> optInversionista = inversionistaService.obtenerInversionistaPorId(id);
        
        if (optInversionista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Inversionista no encontrado"));
        }
        
        Inversionista inversionista = optInversionista.get();
        
        Map<String, Object> response = new HashMap<>();
        response.put("inversionistaId", id);
        response.put("tieneComisionista", inversionista.getTieneComisionista());
        response.put("comisionistaId", inversionista.getComisionistaId());
        response.put("contratoId", inversionista.getContratoId());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Obtener estadísticas del inversionista
     */
    @GetMapping("/{id}/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas(@PathVariable Long id) {
        Optional<Inversionista> optInversionista = inversionistaService.obtenerInversionistaPorId(id);
        
        if (optInversionista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Inversionista no encontrado"));
        }
        
        Inversionista inv = optInversionista.get();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("saldoDisponible", inv.getSaldoDisponible());
        stats.put("totalInvertido", inv.getTotalInvertido());
        stats.put("numeroOperaciones", inv.getNumeroOperaciones());
        stats.put("tieneComisionista", inv.getTieneComisionista());
        
        return ResponseEntity.ok(stats);
    }
    
    @PostMapping("/{id}/recargar-saldo")
    public ResponseEntity<?> recargarSaldo(
            @PathVariable Long id,
            @RequestBody Map<String, Double> body) {
        
        try {
            Double monto = body.get("monto");
            
            if (monto == null || monto <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "El monto debe ser mayor a 0"));
            }
            
            boolean recargado = inversionistaService.agregarSaldo(id, monto);
            
            if (recargado) {
                Optional<Inversionista> inv = inversionistaService.obtenerInversionistaPorId(id);
                Map<String, Object> response = new HashMap<>();
                response.put("mensaje", "Saldo recargado exitosamente");
                response.put("montoRecargado", monto);
                response.put("nuevoSaldo", inv.get().getSaldoDisponible());
                return ResponseEntity.ok(response);
            }
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Inversionista no encontrado"));
                
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }
}