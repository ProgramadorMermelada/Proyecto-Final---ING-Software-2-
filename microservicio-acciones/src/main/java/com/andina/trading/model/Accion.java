package com.andina.trading.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "acciones")
public class Accion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombreEmpresa;
    
    @Column(nullable = false)
    private String simbolo; // Ej: "AAPL", "GOOGL"
    
    @Column(nullable = false)
    private double precioActual;
    
    @Column(nullable = false)
    private double variacionDia; // Porcentaje de cambio en el día
    
    @Column(nullable = false)
    private String pais;
    
    @Column(nullable = false)
    private String ciudad;
    
    @Column(nullable = false)
    private LocalDateTime ultimaActualizacion;
    
    // Constructores
    public Accion() {
        this.ultimaActualizacion = LocalDateTime.now();
    }
    
    public Accion(String nombreEmpresa, String simbolo, double precioActual, 
                  double variacionDia, String pais, String ciudad) {
        this.nombreEmpresa = nombreEmpresa;
        this.simbolo = simbolo;
        this.precioActual = precioActual;
        this.variacionDia = variacionDia;
        this.pais = pais;
        this.ciudad = ciudad;
        this.ultimaActualizacion = LocalDateTime.now();
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNombreEmpresa() {
        return nombreEmpresa;
    }
    
    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }
    
    public String getSimbolo() {
        return simbolo;
    }
    
    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }
    
    public double getPrecioActual() {
        return precioActual;
    }
    
    public void setPrecioActual(double precioActual) {
        this.precioActual = precioActual;
    }
    
    public double getVariacionDia() {
        return variacionDia;
    }
    
    public void setVariacionDia(double variacionDia) {
        this.variacionDia = variacionDia;
    }
    
    public String getPais() {
        return pais;
    }
    
    public void setPais(String pais) {
        this.pais = pais;
    }
    
    public String getCiudad() {
        return ciudad;
    }
    
    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }
    
    public LocalDateTime getUltimaActualizacion() {
        return ultimaActualizacion;
    }
    
    public void setUltimaActualizacion(LocalDateTime ultimaActualizacion) {
        this.ultimaActualizacion = ultimaActualizacion;
    }
}
