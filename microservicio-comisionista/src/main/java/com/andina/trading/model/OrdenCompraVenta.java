package com.andina.trading.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orden_compra_venta")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OrdenCompraVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_orden", unique = true, nullable = false)
    private String numeroOrden;

    @Column(name = "comisionista_id", nullable = false)
    private Long comisionistaId;

    @Column(name = "inversionista_id", nullable = false)
    private Long inversionistaId;

    @Column(name = "contrato_id")
    private Long contratoId;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "simbolo_accion", nullable = false)
    private String simboloAccion;

    @Column(name = "cantidad_acciones", nullable = false)
    private Integer cantidadAcciones;

    @Column(name = "precio_unitario", nullable = false)
    private Double precioUnitario;

    @Column(name = "monto_total", nullable = false)
    private Double montoTotal;

    @Column(name = "tipo_orden", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoOrden tipoOrden;

    @Column(name = "estado", nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoOrden estado;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_ejecucion")
    private LocalDateTime fechaEjecucion;

    @Column(name = "comision_generada")
    private Double comisionGenerada;

    @Column(name = "porcentaje_comision_aplicado")
    private Double porcentajeComisionAplicado;

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    public enum TipoOrden {
        COMPRA,
        VENTA
    }

    public enum EstadoOrden {
        PENDIENTE,
        EN_PROCESO,
        EJECUTADA,
        CANCELADA,
        RECHAZADA
    }

    // Constructores
    public OrdenCompraVenta() {
        this.fechaCreacion = LocalDateTime.now();
        this.estado = EstadoOrden.PENDIENTE;
    }

    // Métodos de negocio
    public void ejecutarOrden() {
        this.estado = EstadoOrden.EJECUTADA;
        this.fechaEjecucion = LocalDateTime.now();
        this.montoTotal = this.cantidadAcciones * this.precioUnitario;
        this.comisionGenerada = this.montoTotal * (this.porcentajeComisionAplicado / 100);
    }

    public void cancelarOrden(String motivo) {
        this.estado = EstadoOrden.CANCELADA;
        this.observaciones = motivo;
    }

    public boolean puedeEjecutarse() {
        return this.estado == EstadoOrden.PENDIENTE || this.estado == EstadoOrden.EN_PROCESO;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeroOrden() { return numeroOrden; }
    public void setNumeroOrden(String numeroOrden) { this.numeroOrden = numeroOrden; }

    public Long getComisionistaId() { return comisionistaId; }
    public void setComisionistaId(Long comisionistaId) { this.comisionistaId = comisionistaId; }

    public Long getInversionistaId() { return inversionistaId; }
    public void setInversionistaId(Long inversionistaId) { this.inversionistaId = inversionistaId; }

    public Long getContratoId() { return contratoId; }
    public void setContratoId(Long contratoId) { this.contratoId = contratoId; }

    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }

    public String getSimboloAccion() { return simboloAccion; }
    public void setSimboloAccion(String simboloAccion) { this.simboloAccion = simboloAccion; }

    public Integer getCantidadAcciones() { return cantidadAcciones; }
    public void setCantidadAcciones(Integer cantidadAcciones) { this.cantidadAcciones = cantidadAcciones; }

    public Double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }

    public Double getMontoTotal() { return montoTotal; }
    public void setMontoTotal(Double montoTotal) { this.montoTotal = montoTotal; }

    public TipoOrden getTipoOrden() { return tipoOrden; }
    public void setTipoOrden(TipoOrden tipoOrden) { this.tipoOrden = tipoOrden; }

    public EstadoOrden getEstado() { return estado; }
    public void setEstado(EstadoOrden estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaEjecucion() { return fechaEjecucion; }
    public void setFechaEjecucion(LocalDateTime fechaEjecucion) { this.fechaEjecucion = fechaEjecucion; }

    public Double getComisionGenerada() { return comisionGenerada; }
    public void setComisionGenerada(Double comisionGenerada) { this.comisionGenerada = comisionGenerada; }

    public Double getPorcentajeComisionAplicado() { return porcentajeComisionAplicado; }
    public void setPorcentajeComisionAplicado(Double porcentajeComisionAplicado) { this.porcentajeComisionAplicado = porcentajeComisionAplicado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}