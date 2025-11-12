package com.andina.trading.repository;

import com.andina.trading.model.Inversionista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InversionistaRepository extends JpaRepository<Inversionista, Long> {

    // Buscar inversionistas por comisionista
    List<Inversionista> findByComisionistaId(Long comisionistaId);

    // Buscar inversionistas que tienen comisionista
    List<Inversionista> findByTieneComisionista(Boolean tieneComisionista);

    // Buscar inversionistas por contrato
    List<Inversionista> findByContratoId(Long contratoId);

}