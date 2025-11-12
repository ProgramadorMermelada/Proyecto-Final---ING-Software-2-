package com.andina.trading.controller;

import com.andina.trading.model.Comisionista;
import com.andina.trading.model.ContratoNegociacion;
import com.andina.trading.model.OrdenCompraVenta;
import com.andina.trading.service.ComisionistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comisionistas")
public class ComisionistaController {

    @Autowired
    private ComisionistaService comisionistaService;

    // ========== GESTIÓN DE COMISIONISTAS (Módulo 1) ==========
    
    @PostMapping
    public ResponseEntity<Comisionista> crearComisionista(@RequestBody Comisionista comisionista) {
        try {
            System.out.println("========================================");
            System.out.println("📥 RECIBIDO en ComisionistaController:");
            System.out.println("   Nombre: " + comisionista.getNombre());
            System.out.println("   Email: " + comisionista.getEmail());
            System.out.println("   Teléfono: " + comisionista.getTelefono());
            System.out.println("   Ciudad: " + comisionista.getCiudad());
            System.out.println("   País: " + comisionista.getPais());
            System.out.println("   Número Licencia: " + comisionista.getNumeroLicencia());
            System.out.println("   Saldo Comisiones: " + comisionista.getSaldoComisiones());
            
            Comisionista creado = comisionistaService.crearComisionista(comisionista);
            
            System.out.println("✅ Comisionista creado con ID: " + creado.getId());
            System.out.println("========================================");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (Exception e) {
            System.err.println("❌ ERROR al crear comisionista:");
            System.err.println("   Tipo: " + e.getClass().getName());
            System.err.println("   Mensaje: " + e.getMessage());
            e.printStackTrace();
            System.out.println("========================================");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Comisionista>> obtenerTodos() {
        return ResponseEntity.ok(comisionistaService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comisionista> obtenerPorId(@PathVariable Long id) {
        return comisionistaService.obtenerPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pais/{pais}")
    public ResponseEntity<List<Comisionista>> obtenerPorPais(@PathVariable String pais) {
        return ResponseEntity.ok(comisionistaService.obtenerPorPais(pais));
    }

    @GetMapping("/ciudad/{ciudad}")
    public ResponseEntity<List<Comisionista>> obtenerPorCiudad(@PathVariable String ciudad) {
        return ResponseEntity.ok(comisionistaService.obtenerPorCiudad(ciudad));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comisionista> actualizar(@PathVariable Long id, @RequestBody Comisionista datos) {
        return comisionistaService.actualizar(id, datos)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/estadisticas")
    public ResponseEntity<ComisionistaService.EstadisticasComisionista> obtenerEstadisticas(@PathVariable Long id) {
        ComisionistaService.EstadisticasComisionista stats = comisionistaService.obtenerEstadisticas(id);
        if (stats == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(stats);
    }

    // ========== ENDPOINT PARA EJECUTAR TRANSACCIÓN DIRECTA ==========
    
    @PostMapping("/{comisionistaId}/ejecutar-transaccion")
    public ResponseEntity<?> ejecutarTransaccionDirecta(
            @PathVariable Long comisionistaId,
            @RequestParam Double monto) {
        
        boolean ejecutada = comisionistaService.ejecutarTransaccionDirecta(comisionistaId, monto);
        
        Map<String, Object> response = new HashMap<>();
        if (ejecutada) {
            response.put("mensaje", "Transacción ejecutada exitosamente");
            response.put("ejecutada", true);
            return ResponseEntity.ok(response);
        }
        
        response.put("error", "No se pudo ejecutar la transacción");
        response.put("ejecutada", false);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // ========== CONTRATOS DE NEGOCIACIÓN (Módulo 3) ==========
    
    @PostMapping("/{comisionistaId}/contratos")
    public ResponseEntity<?> crearContrato(
            @PathVariable Long comisionistaId,
            @RequestBody ContratoNegociacion contrato) {
        try {
            ContratoNegociacion creado = comisionistaService.crearContrato(comisionistaId, contrato);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/{comisionistaId}/contratos")
    public ResponseEntity<List<ContratoNegociacion>> obtenerContratos(@PathVariable Long comisionistaId) {
        return ResponseEntity.ok(comisionistaService.obtenerContratosComisionista(comisionistaId));
    }

    @PutMapping("/contratos/{contratoId}/activar")
    public ResponseEntity<?> activarContrato(@PathVariable Long contratoId) {
        boolean activado = comisionistaService.activarContrato(contratoId);
        if (activado) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Contrato activado exitosamente");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    // ========== ÓRDENES DE COMPRA/VENTA (Módulo 5) ==========
    
    @PostMapping("/ordenes")
    public ResponseEntity<?> crearOrden(@RequestBody OrdenCompraVenta orden) {
        try {
            OrdenCompraVenta creada = comisionistaService.crearOrden(orden);
            return ResponseEntity.status(HttpStatus.CREATED).body(creada);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/ordenes/{ordenId}/ejecutar")
    public ResponseEntity<?> ejecutarOrden(@PathVariable Long ordenId) {
        boolean ejecutada = comisionistaService.ejecutarOrden(ordenId);
        
        Map<String, Object> response = new HashMap<>();
        if (ejecutada) {
            response.put("mensaje", "Orden ejecutada exitosamente");
            response.put("ejecutada", true);
            return ResponseEntity.ok(response);
        }
        
        response.put("error", "No se pudo ejecutar la orden");
        response.put("ejecutada", false);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @GetMapping("/{comisionistaId}/ordenes")
    public ResponseEntity<List<OrdenCompraVenta>> obtenerOrdenes(@PathVariable Long comisionistaId) {
        return ResponseEntity.ok(comisionistaService.obtenerOrdenesComisionista(comisionistaId));
    }

    // ========== ENDPOINTS DE CONSOLIDACIÓN (Módulo 6) ==========
    
    @GetMapping("/consolidacion")
    public ResponseEntity<?> obtenerConsolidacion() {
        List<Comisionista> todos = comisionistaService.obtenerTodos();
        
        Map<String, Object> consolidacion = new HashMap<>();
        consolidacion.put("totalComisionistas", todos.size());
        consolidacion.put("comisionistasPorPais", agruparPorPais(todos));
        consolidacion.put("comisionistasActivos", contarActivos(todos));
        consolidacion.put("totalComisionesGeneradas", calcularTotalComisiones(todos));
        
        return ResponseEntity.ok(consolidacion);
    }
    
    // Métodos auxiliares para consolidación
    private Map<String, Long> agruparPorPais(List<Comisionista> comisionistas) {
        Map<String, Long> porPais = new HashMap<>();
        for (Comisionista c : comisionistas) {
            String pais = c.getPais() != null ? c.getPais() : "Sin país";
            porPais.put(pais, porPais.getOrDefault(pais, 0L) + 1);
        }
        return porPais;
    }
    
    private long contarActivos(List<Comisionista> comisionistas) {
        return comisionistas.stream().filter(Comisionista::getActivo).count();
    }
    
    private Double calcularTotalComisiones(List<Comisionista> comisionistas) {
        return comisionistas.stream()
            .mapToDouble(c -> c.getSaldoComisiones() != null ? c.getSaldoComisiones() : 0.0)
            .sum();
    }
}