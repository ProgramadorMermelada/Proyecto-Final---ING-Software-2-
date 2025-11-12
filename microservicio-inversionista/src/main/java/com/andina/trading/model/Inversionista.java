package com.andina.trading.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inversionista")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Inversionista {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(name = "saldo_disponible", nullable = false)
    private Double saldoDisponible;
    
    // NUEVOS CAMPOS para integración con Comisionista
    
    @Column(name = "comisionista_id")
    private Long comisionistaId; // ID del comisionista asignado
    
    @Column(name = "contrato_id")
    private Long contratoId; // ID del contrato activo
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "telefono")
    private String telefono;
    
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;
    
    @Column(name = "tiene_comisionista")
    private Boolean tieneComisionista = false;
    
    @Column(name = "total_invertido")
    private Double totalInvertido = 0.0;
    
    @Column(name = "numero_operaciones")
    private Integer numeroOperaciones = 0;
    
    // Constructores
    public Inversionista() {
        this.fechaRegistro = LocalDateTime.now();
    }
    
    public Inversionista(String nombre, Double saldoDisponible) {
        this();
        this.nombre = nombre;
        this.saldoDisponible = saldoDisponible;
    }
    
    // Métodos de negocio
    public boolean tieneSaldoSuficiente(Double monto) {
        return this.saldoDisponible >= monto;
    }
    
    public void descontarSaldo(Double monto) {
        if (tieneSaldoSuficiente(monto)) {
            this.saldoDisponible -= monto;
            this.totalInvertido += monto;
            this.numeroOperaciones++;
        }
    }
    
    public void agregarSaldo(Double monto) {
        this.saldoDisponible += monto;
    }
    
    public void vincularComisionista(Long comisionistaId, Long contratoId) {
        this.comisionistaId = comisionistaId;
        this.contratoId = contratoId;
        this.tieneComisionista = true;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public Double getSaldoDisponible() {
        return saldoDisponible;
    }
    
    public void setSaldoDisponible(Double saldoDisponible) {
        this.saldoDisponible = saldoDisponible;
    }
    
    public Long getComisionistaId() {
        return comisionistaId;
    }
    
    public void setComisionistaId(Long comisionistaId) {
        this.comisionistaId = comisionistaId;
    }
    
    public Long getContratoId() {
        return contratoId;
    }
    
    public void setContratoId(Long contratoId) {
        this.contratoId = contratoId;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
    
    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
    
    public Boolean getTieneComisionista() {
        return tieneComisionista;
    }
    
    public void setTieneComisionista(Boolean tieneComisionista) {
        this.tieneComisionista = tieneComisionista;
    }
    
    public Double getTotalInvertido() {
        return totalInvertido;
    }
    
    public void setTotalInvertido(Double totalInvertido) {
        this.totalInvertido = totalInvertido;
    }
    
    public Integer getNumeroOperaciones() {
        return numeroOperaciones;
    }
    
    public void setNumeroOperaciones(Integer numeroOperaciones) {
        this.numeroOperaciones = numeroOperaciones;
    }
}