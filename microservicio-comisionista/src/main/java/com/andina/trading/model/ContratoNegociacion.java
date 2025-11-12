package com.andina.trading.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "contrato_negociacion")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ContratoNegociacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_contrato", unique = true, nullable = false)
    private String numeroContrato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comisionista_id", nullable = false)
    private Comisionista comisionista;

    @Column(name = "inversionista_id", nullable = false)
    private Long inversionistaId;

    @Column(name = "nombre_inversionista")
    private String nombreInversionista;

    @Column(name = "monto_inicial", nullable = false)
    private Double montoInicial;

    @Column(name = "saldo_cuenta", nullable = false)
    private Double saldoCuenta;

    @Column(name = "fecha_contrato", nullable = false)
    private LocalDateTime fechaContrato;

    @Column(name = "fecha_vencimiento")
    private LocalDateTime fechaVencimiento;

    @Column(name = "estado")
    @Enumerated(EnumType.STRING)
    private EstadoContrato estado;

    @Column(name = "login_portal")
    private String loginPortal;

    @Column(name = "fecha_ultimo_acceso")
    private LocalDateTime fechaUltimoAcceso;

    @Column(name = "terminos_aceptados")
    private Boolean terminosAceptados = false;

    @Column(name = "comision_acordada")
    private Double comisionAcordada;

    public enum EstadoContrato {
        ACTIVO,
        SUSPENDIDO,
        FINALIZADO,
        PENDIENTE_APROBACION
    }

    // Constructores
    public ContratoNegociacion() {
        this.fechaContrato = LocalDateTime.now();
        this.estado = EstadoContrato.PENDIENTE_APROBACION;
        this.saldoCuenta = 0.0;
    }

    // Métodos de negocio
    public void depositarFondos(Double monto) {
        this.saldoCuenta += monto;
    }

    public boolean retirarFondos(Double monto) {
        if (this.saldoCuenta >= monto) {
            this.saldoCuenta -= monto;
            return true;
        }
        return false;
    }

    public void activarContrato() {
        this.estado = EstadoContrato.ACTIVO;
        this.terminosAceptados = true;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeroContrato() { return numeroContrato; }
    public void setNumeroContrato(String numeroContrato) { this.numeroContrato = numeroContrato; }

    public Comisionista getComisionista() { return comisionista; }
    public void setComisionista(Comisionista comisionista) { this.comisionista = comisionista; }

    public Long getInversionistaId() { return inversionistaId; }
    public void setInversionistaId(Long inversionistaId) { this.inversionistaId = inversionistaId; }

    public String getNombreInversionista() { return nombreInversionista; }
    public void setNombreInversionista(String nombreInversionista) { this.nombreInversionista = nombreInversionista; }

    public Double getMontoInicial() { return montoInicial; }
    public void setMontoInicial(Double montoInicial) { this.montoInicial = montoInicial; }

    public Double getSaldoCuenta() { return saldoCuenta; }
    public void setSaldoCuenta(Double saldoCuenta) { this.saldoCuenta = saldoCuenta; }

    public LocalDateTime getFechaContrato() { return fechaContrato; }
    public void setFechaContrato(LocalDateTime fechaContrato) { this.fechaContrato = fechaContrato; }

    public LocalDateTime getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDateTime fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public EstadoContrato getEstado() { return estado; }
    public void setEstado(EstadoContrato estado) { this.estado = estado; }

    public String getLoginPortal() { return loginPortal; }
    public void setLoginPortal(String loginPortal) { this.loginPortal = loginPortal; }

    public LocalDateTime getFechaUltimoAcceso() { return fechaUltimoAcceso; }
    public void setFechaUltimoAcceso(LocalDateTime fechaUltimoAcceso) { this.fechaUltimoAcceso = fechaUltimoAcceso; }

    public Boolean getTerminosAceptados() { return terminosAceptados; }
    public void setTerminosAceptados(Boolean terminosAceptados) { this.terminosAceptados = terminosAceptados; }

    public Double getComisionAcordada() { return comisionAcordada; }
    public void setComisionAcordada(Double comisionAcordada) { this.comisionAcordada = comisionAcordada; }
}