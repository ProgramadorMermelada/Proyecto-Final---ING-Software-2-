package com.andina.trading.repository;

import com.andina.trading.model.Comisionista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComisionistaRepository extends JpaRepository<Comisionista, Long> {

    // Buscar comisionistas por país
    List<Comisionista> findByPais(String pais);

    // Buscar comisionistas por ciudad
    List<Comisionista> findByCiudad(String ciudad);

    // Buscar comisionistas por estado activo/inactivo
    List<Comisionista> findByActivo(Boolean activo);
}