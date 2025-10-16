package com.andina.trading.controller;

import com.andina.trading.model.Inversionista;
import com.andina.trading.service.InversionistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/inversionistas")
@CrossOrigin(origins = "http://localhost:3000") 
public class InversionistaController {

    @Autowired
    private InversionistaService inversionistaService;

    @PostMapping
    public Inversionista crearInversionista(@RequestBody Inversionista inversionista) {
        return inversionistaService.crearInversionista(inversionista.getNombre(), inversionista.getSaldoDisponible());
    }

    @GetMapping
    public List<Inversionista> obtenerTodosInversionistas() {
        return inversionistaService.obtenerTodosInversionistas();
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
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No hay suficiente saldo para la compra");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}

