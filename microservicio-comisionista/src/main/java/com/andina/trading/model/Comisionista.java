package com.andina.trading.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comisionista")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Comisionista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String email;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "ciudad")
    private String ciudad;

    @Column(name = "pais")
    private String pais;

    @Column(name = "saldo_comisiones", nullable = false)
    private Double saldoComisiones = 0.0;

    @Column(name = "porcentaje_comision")
    private Double porcentajeComision = 0.5;

    @Column(name = "numero_transacciones")
    private Integer numeroTransacciones = 0;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @Column(name = "ultima_transaccion")
    private LocalDateTime ultimaTransaccion;

    @Column(name = "numero_licencia")
    private String numeroLicencia;

    @Column(name = "fecha_licencia")
    private LocalDateTime fechaLicencia;

    @OneToMany(mappedBy = "comisionista", cascade = CascadeType.ALL)
    private List<ContratoNegociacion> contratos = new ArrayList<>();

    // Constructores
    public Comisionista() {
        this.fechaRegistro = LocalDateTime.now();
    }

    public Comisionista(String nombre) {
        this();
        this.nombre = nombre;
    }

    public Comisionista(String nombre, Double porcentajeComision) {
        this(nombre);
        this.porcentajeComision = porcentajeComision;
    }

    public Comisionista(String nombre, String email, String ciudad, String pais) {
        this();
        this.nombre = nombre;
        this.email = email;
        this.ciudad = ciudad;
        this.pais = pais;
    }

    // Métodos de negocio
    public Double registrarTransaccion(Double montoTransaccion) {
        Double comision = montoTransaccion * (this.porcentajeComision / 100);
        this.saldoComisiones += comision;
        this.numeroTransacciones++;
        this.ultimaTransaccion = LocalDateTime.now();
        return comision;
    }

    public boolean puedeOperar() {
        return this.activo && this.numeroLicencia != null;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public Double getSaldoComisiones() { return saldoComisiones; }
    public void setSaldoComisiones(Double saldoComisiones) { this.saldoComisiones = saldoComisiones; }

    public Double getPorcentajeComision() { return porcentajeComision; }
    public void setPorcentajeComision(Double porcentajeComision) { this.porcentajeComision = porcentajeComision; }

    public Integer getNumeroTransacciones() { return numeroTransacciones; }
    public void setNumeroTransacciones(Integer numeroTransacciones) { this.numeroTransacciones = numeroTransacciones; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public LocalDateTime getUltimaTransaccion() { return ultimaTransaccion; }
    public void setUltimaTransaccion(LocalDateTime ultimaTransaccion) { this.ultimaTransaccion = ultimaTransaccion; }

    public String getNumeroLicencia() { return numeroLicencia; }
    public void setNumeroLicencia(String numeroLicencia) { this.numeroLicencia = numeroLicencia; }

    public LocalDateTime getFechaLicencia() { return fechaLicencia; }
    public void setFechaLicencia(LocalDateTime fechaLicencia) { this.fechaLicencia = fechaLicencia; }

    public List<ContratoNegociacion> getContratos() { return contratos; }
    public void setContratos(List<ContratoNegociacion> contratos) { this.contratos = contratos; }
}