package com.andina.trading.repository;

import com.andina.trading.model.ContratoNegociacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContratoRepository extends JpaRepository<ContratoNegociacion, Long> {

    // Buscar contratos por comisionista
    List<ContratoNegociacion> findByComisionistaId(Long comisionistaId);

    // Buscar contratos por inversionista
    List<ContratoNegociacion> findByInversionistaId(Long inversionistaId);

    // Buscar contratos por estado
    List<ContratoNegociacion> findByEstado(ContratoNegociacion.EstadoContrato estado);
}