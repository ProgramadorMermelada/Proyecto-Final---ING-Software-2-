package com.andina.trading.repository;

import com.andina.trading.model.Accion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccionRepository extends JpaRepository<Accion, Long> {
    
    List<Accion> findByPais(String pais);
    
    Optional<Accion> findBySimbolo(String simbolo);
    
    List<Accion> findByCiudad(String ciudad);
}