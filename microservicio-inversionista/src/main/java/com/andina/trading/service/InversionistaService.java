package com.andina.trading.service;

import com.andina.trading.model.Inversionista;
import com.andina.trading.repository.InversionistaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InversionistaService {

    @Autowired
    private InversionistaRepository inversionistaRepository;

    // ========== MÉTODOS ORIGINALES ==========
    
    
    
    @Transactional
    public Inversionista crearInversionista(String nombre, Double saldoDisponible) {
        System.out.println("📝 Creando inversionista: " + nombre);
        
        Inversionista inversionista = new Inversionista(nombre, saldoDisponible);
        Inversionista guardado = inversionistaRepository.save(inversionista);
        
        System.out.println("✅ Inversionista creado con ID: " + guardado.getId());
        return guardado;
    }

    public List<Inversionista> obtenerTodosInversionistas() {
        return inversionistaRepository.findAll();
    }

    public Optional<Inversionista> obtenerInversionistaPorId(Long id) {
        return inversionistaRepository.findById(id);
    }

    @Transactional
    public boolean comprarAcciones(Long inversionistaId, double monto) {
        System.out.println("💰 Comprando acciones para inversionista ID: " + inversionistaId);
        System.out.println("💵 Monto: $" + monto);
        
        Optional<Inversionista> optInversionista = inversionistaRepository.findById(inversionistaId);
        
        if (optInversionista.isEmpty()) {
            throw new IllegalArgumentException("Inversionista no encontrado con ID: " + inversionistaId);
        }

        Inversionista inversionista = optInversionista.get();

        if (!inversionista.tieneSaldoSuficiente(monto)) {
            System.out.println("❌ Saldo insuficiente");
            return false;
        }

        inversionista.descontarSaldo(monto);
        inversionistaRepository.save(inversionista);
        
        System.out.println("✅ Compra exitosa");
        System.out.println("💳 Nuevo saldo: $" + inversionista.getSaldoDisponible());
        return true;
    }

    // ========== NUEVOS MÉTODOS PARA INTEGRACIÓN ==========
    
    /**
     * Vincular inversionista con comisionista
     */
    @Transactional
    public boolean vincularComisionista(Long inversionistaId, Long comisionistaId, Long contratoId) {
        System.out.println("🔗 Vinculando inversionista " + inversionistaId + 
                          " con comisionista " + comisionistaId);
        
        Optional<Inversionista> optInversionista = inversionistaRepository.findById(inversionistaId);
        
        if (optInversionista.isEmpty()) {
            System.out.println("❌ Inversionista no encontrado");
            return false;
        }
        
        Inversionista inversionista = optInversionista.get();
        inversionista.vincularComisionista(comisionistaId, contratoId);
        inversionistaRepository.save(inversionista);
        
        System.out.println("✅ Vinculación exitosa");
        return true;
    }
    
    /**
     * Descontar saldo (para órdenes de compra)
     */
    @Transactional
    public boolean descontarSaldo(Long inversionistaId, Double monto) {
        System.out.println("💸 Descontando $" + monto + " al inversionista " + inversionistaId);
        
        Optional<Inversionista> optInversionista = inversionistaRepository.findById(inversionistaId);
        
        if (optInversionista.isEmpty()) {
            System.out.println("❌ Inversionista no encontrado");
            return false;
        }
        
        Inversionista inversionista = optInversionista.get();
        
        if (!inversionista.tieneSaldoSuficiente(monto)) {
            System.out.println("❌ Saldo insuficiente");
            return false;
        }
        
        inversionista.descontarSaldo(monto);
        inversionistaRepository.save(inversionista);
        
        System.out.println("✅ Saldo descontado. Nuevo saldo: $" + inversionista.getSaldoDisponible());
        return true;
    }
    
    /**
     * Agregar saldo (para órdenes de venta)
     */
    @Transactional
    public boolean agregarSaldo(Long inversionistaId, Double monto) {
        System.out.println("💰 Agregando $" + monto + " al inversionista " + inversionistaId);
        
        Optional<Inversionista> optInversionista = inversionistaRepository.findById(inversionistaId);
        
        if (optInversionista.isEmpty()) {
            System.out.println("❌ Inversionista no encontrado");
            return false;
        }
        
        Inversionista inversionista = optInversionista.get();
        inversionista.agregarSaldo(monto);
        inversionistaRepository.save(inversionista);
        
        System.out.println("✅ Saldo agregado. Nuevo saldo: $" + inversionista.getSaldoDisponible());
        return true;
    }
    
    /**
     * Obtener inversionistas por comisionista
     */
    public List<Inversionista> obtenerPorComisionista(Long comisionistaId) {
        return inversionistaRepository.findByComisionistaId(comisionistaId);
    }
}