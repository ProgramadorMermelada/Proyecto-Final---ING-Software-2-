package com.andina.trading.service;

import com.andina.trading.client.InversionistaClient;
import com.andina.trading.model.Comisionista;
import com.andina.trading.model.ContratoNegociacion;
import com.andina.trading.model.OrdenCompraVenta;
import com.andina.trading.repository.ComisionistaRepository;
import com.andina.trading.repository.ContratoRepository;
import com.andina.trading.repository.OrdenCompraVentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ComisionistaService {
    
    @Autowired
    private ComisionistaRepository comisionistaRepository;
    
    @Autowired
    private ContratoRepository contratoRepository;
    
    @Autowired
    private OrdenCompraVentaRepository ordenRepository;
    
    @Autowired
    private InversionistaClient inversionistaClient;
    
    // ========== GESTIÓN DE COMISIONISTAS (Módulo 1) ==========
    
    @Transactional
    public Comisionista crearComisionista(Comisionista comisionista) {
        System.out.println("📝 Creando comisionista: " + comisionista.getNombre());
        
        // Generar número de licencia automático
        if (comisionista.getNumeroLicencia() == null) {
            comisionista.setNumeroLicencia("LIC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            comisionista.setFechaLicencia(LocalDateTime.now());
        }
        
        Comisionista guardado = comisionistaRepository.save(comisionista);
        System.out.println("✅ Comisionista creado con ID: " + guardado.getId());
        
        return guardado;
    }
    
    public List<Comisionista> obtenerTodos() {
        return comisionistaRepository.findAll();
    }
    
    public Optional<Comisionista> obtenerPorId(Long id) {
        return comisionistaRepository.findById(id);
    }
    
    public List<Comisionista> obtenerPorPais(String pais) {
        return comisionistaRepository.findByPais(pais);
    }
    
    public List<Comisionista> obtenerPorCiudad(String ciudad) {
        return comisionistaRepository.findByCiudad(ciudad);
    }
    
    @Transactional
    public Optional<Comisionista> actualizar(Long id, Comisionista datos) {
        return comisionistaRepository.findById(id).map(comisionista -> {
            if (datos.getNombre() != null) comisionista.setNombre(datos.getNombre());
            if (datos.getEmail() != null) comisionista.setEmail(datos.getEmail());
            if (datos.getTelefono() != null) comisionista.setTelefono(datos.getTelefono());
            if (datos.getCiudad() != null) comisionista.setCiudad(datos.getCiudad());
            if (datos.getPais() != null) comisionista.setPais(datos.getPais());
            if (datos.getPorcentajeComision() != null) comisionista.setPorcentajeComision(datos.getPorcentajeComision());
            if (datos.getActivo() != null) comisionista.setActivo(datos.getActivo());
            
            return comisionistaRepository.save(comisionista);
        });
    }
    
    // ========== CONTRATOS DE NEGOCIACIÓN (Módulo 3) ==========
    
    @Transactional
    public ContratoNegociacion crearContrato(Long comisionistaId, ContratoNegociacion contrato) {
        System.out.println("📄 Creando contrato para comisionista ID: " + comisionistaId);
        
        Optional<Comisionista> optComisionista = comisionistaRepository.findById(comisionistaId);
        
        if (optComisionista.isEmpty()) {
            throw new RuntimeException("Comisionista no encontrado");
        }
        
        Comisionista comisionista = optComisionista.get();
        
        if (!comisionista.puedeOperar()) {
            throw new RuntimeException("Comisionista no puede operar (inactivo o sin licencia)");
        }
        
        // Validar que el inversionista existe
        Map<String, Object> inversionista = inversionistaClient.obtenerInversionista(contrato.getInversionistaId());
        if (inversionista == null) {
            throw new RuntimeException("Inversionista no encontrado en el sistema");
        }
        
        // Generar número de contrato único
        contrato.setNumeroContrato("CTR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        contrato.setComisionista(comisionista);
        contrato.setSaldoCuenta(contrato.getMontoInicial());
        contrato.setComisionAcordada(comisionista.getPorcentajeComision());
        
        // Generar login para portal del inversionista (Módulo 4)
        contrato.setLoginPortal("INV-" + contrato.getInversionistaId());
        
        ContratoNegociacion guardado = contratoRepository.save(contrato);
        
        // Vincular inversionista con comisionista en el microservicio de inversionistas
        boolean vinculado = inversionistaClient.vincularComisionista(
            contrato.getInversionistaId(), 
            comisionistaId, 
            guardado.getId()
        );
        
        if (!vinculado) {
            System.out.println("⚠️ Advertencia: No se pudo vincular en el microservicio de inversionistas");
        }
        
        System.out.println("✅ Contrato creado: " + guardado.getNumeroContrato());
        
        return guardado;
    }
    
    public List<ContratoNegociacion> obtenerContratosComisionista(Long comisionistaId) {
        return contratoRepository.findByComisionistaId(comisionistaId);
    }
    
    @Transactional
    public boolean activarContrato(Long contratoId) {
        Optional<ContratoNegociacion> optContrato = contratoRepository.findById(contratoId);
        
        if (optContrato.isEmpty()) {
            return false;
        }
        
        ContratoNegociacion contrato = optContrato.get();
        contrato.activarContrato();
        contratoRepository.save(contrato);
        
        System.out.println("✅ Contrato activado: " + contrato.getNumeroContrato());
        return true;
    }
    
    // ========== ÓRDENES DE COMPRA/VENTA (Módulo 5) ==========
    
    @Transactional
    public OrdenCompraVenta crearOrden(OrdenCompraVenta orden) {
        System.out.println("📊 Creando orden de " + orden.getTipoOrden());
        
        // Validar que el comisionista existe
        Optional<Comisionista> optComisionista = comisionistaRepository.findById(orden.getComisionistaId());
        if (optComisionista.isEmpty()) {
            throw new RuntimeException("Comisionista no encontrado");
        }
        
        Comisionista comisionista = optComisionista.get();
        
        // Validar que el contrato existe y está activo
        Optional<ContratoNegociacion> optContrato = contratoRepository.findById(orden.getContratoId());
        if (optContrato.isEmpty() || optContrato.get().getEstado() != ContratoNegociacion.EstadoContrato.ACTIVO) {
            throw new RuntimeException("Contrato no válido o inactivo");
        }
        
        // Generar número de orden
        orden.setNumeroOrden("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        orden.setPorcentajeComisionAplicado(comisionista.getPorcentajeComision());
        
        OrdenCompraVenta guardada = ordenRepository.save(orden);
        System.out.println("✅ Orden creada: " + guardada.getNumeroOrden());
        
        return guardada;
    }
    
    @Transactional
    public boolean ejecutarOrden(Long ordenId) {
        System.out.println("⚡ Ejecutando orden ID: " + ordenId);
        
        Optional<OrdenCompraVenta> optOrden = ordenRepository.findById(ordenId);
        
        if (optOrden.isEmpty()) {
            System.out.println("❌ Orden no encontrada");
            return false;
        }
        
        OrdenCompraVenta orden = optOrden.get();
        
        if (!orden.puedeEjecutarse()) {
            System.out.println("❌ Orden no puede ejecutarse (estado: " + orden.getEstado() + ")");
            return false;
        }
        
        // Validar saldo del inversionista antes de ejecutar
        boolean tieneSaldo = inversionistaClient.validarSaldo(
            orden.getInversionistaId(), 
            orden.getMontoTotal()
        );
        
        if (!tieneSaldo && orden.getTipoOrden() == OrdenCompraVenta.TipoOrden.COMPRA) {
            System.out.println("❌ Inversionista no tiene saldo suficiente");
            orden.setEstado(OrdenCompraVenta.EstadoOrden.RECHAZADA);
            orden.setObservaciones("Saldo insuficiente");
            ordenRepository.save(orden);
            return false;
        }
        
        // Ejecutar la orden
        orden.ejecutarOrden();
        ordenRepository.save(orden);
        
        // Actualizar saldo del inversionista según el tipo de orden
        boolean saldoActualizado = false;
        if (orden.getTipoOrden() == OrdenCompraVenta.TipoOrden.COMPRA) {
            // Descontar monto + comisión
            Double montoTotal = orden.getMontoTotal() + orden.getComisionGenerada();
            saldoActualizado = inversionistaClient.descontarSaldo(orden.getInversionistaId(), montoTotal);
        } else if (orden.getTipoOrden() == OrdenCompraVenta.TipoOrden.VENTA) {
            // Agregar monto - comisión
            Double montoNeto = orden.getMontoTotal() - orden.getComisionGenerada();
            saldoActualizado = inversionistaClient.agregarSaldo(orden.getInversionistaId(), montoNeto);
        }
        
        if (!saldoActualizado) {
            System.out.println("⚠️ Advertencia: No se pudo actualizar el saldo del inversionista");
        }
        
        // Actualizar comisiones del comisionista
        Optional<Comisionista> optComisionista = comisionistaRepository.findById(orden.getComisionistaId());
        if (optComisionista.isPresent()) {
            Comisionista comisionista = optComisionista.get();
            comisionista.registrarTransaccion(orden.getMontoTotal());
            comisionistaRepository.save(comisionista);
            
            System.out.println("💰 Comisión generada: $" + orden.getComisionGenerada());
        }
        
        System.out.println("✅ Orden ejecutada exitosamente");
        return true;
    }
    
    public List<OrdenCompraVenta> obtenerOrdenesComisionista(Long comisionistaId) {
        return ordenRepository.findByComisionistaId(comisionistaId);
    }
    
    // ========== EJECUTAR TRANSACCIÓN DIRECTA ==========
    
    /**
     * Ejecutar una transacción directa y calcular comisión
     * (Para testing o transacciones manuales)
     */
    @Transactional
    public boolean ejecutarTransaccionDirecta(Long comisionistaId, Double monto) {
        System.out.println("💼 Ejecutando transacción directa para comisionista ID: " + comisionistaId);
        System.out.println("💰 Monto de la transacción: $" + monto);
        
        Optional<Comisionista> optComisionista = comisionistaRepository.findById(comisionistaId);
        
        if (optComisionista.isEmpty()) {
            System.out.println("❌ Comisionista no encontrado");
            return false;
        }
        
        Comisionista comisionista = optComisionista.get();
        
        if (!comisionista.getActivo()) {
            System.out.println("❌ Comisionista inactivo");
            return false;
        }
        
        // Calcular y registrar la comisión
        Double comisionCalculada = comisionista.registrarTransaccion(monto);
        comisionistaRepository.save(comisionista);
        
        System.out.println("💵 Comisión calculada (" + comisionista.getPorcentajeComision() + "%): $" + comisionCalculada);
        System.out.println("✅ Transacción ejecutada exitosamente");
        System.out.println("📊 Nuevo saldo de comisiones: $" + comisionista.getSaldoComisiones());
        System.out.println("📈 Total de transacciones: " + comisionista.getNumeroTransacciones());
        
        return true;
    }
    
    // ========== ESTADÍSTICAS Y REPORTES (Módulo 7) ==========
    
    public EstadisticasComisionista obtenerEstadisticas(Long id) {
        Optional<Comisionista> optComisionista = comisionistaRepository.findById(id);
        
        if (optComisionista.isEmpty()) {
            return null;
        }
        
        Comisionista comisionista = optComisionista.get();
        List<ContratoNegociacion> contratos = contratoRepository.findByComisionistaId(id);
        List<OrdenCompraVenta> ordenes = ordenRepository.findByComisionistaId(id);
        
        long ordenesEjecutadas = ordenes.stream()
            .filter(o -> o.getEstado() == OrdenCompraVenta.EstadoOrden.EJECUTADA)
            .count();
        
        return new EstadisticasComisionista(
            comisionista.getSaldoComisiones(),
            comisionista.getNumeroTransacciones(),
            contratos.size(),
            (int) ordenesEjecutadas,
            comisionista.getNumeroTransacciones() > 0 
                ? comisionista.getSaldoComisiones() / comisionista.getNumeroTransacciones()
                : 0.0
        );
    }
    
    // DTO para estadísticas
    public static class EstadisticasComisionista {
        private Double totalComisiones;
        private Integer totalTransacciones;
        private Integer totalContratos;
        private Integer ordenesEjecutadas;
        private Double promedioComisionPorTransaccion;
        
        public EstadisticasComisionista(Double totalComisiones, Integer totalTransacciones, 
                                       Integer totalContratos, Integer ordenesEjecutadas,
                                       Double promedioComisionPorTransaccion) {
            this.totalComisiones = totalComisiones;
            this.totalTransacciones = totalTransacciones;
            this.totalContratos = totalContratos;
            this.ordenesEjecutadas = ordenesEjecutadas;
            this.promedioComisionPorTransaccion = promedioComisionPorTransaccion;
        }
        
        // Getters
        public Double getTotalComisiones() { return totalComisiones; }
        public Integer getTotalTransacciones() { return totalTransacciones; }
        public Integer getTotalContratos() { return totalContratos; }
        public Integer getOrdenesEjecutadas() { return ordenesEjecutadas; }
        public Double getPromedioComisionPorTransaccion() { return promedioComisionPorTransaccion; }
    }
}