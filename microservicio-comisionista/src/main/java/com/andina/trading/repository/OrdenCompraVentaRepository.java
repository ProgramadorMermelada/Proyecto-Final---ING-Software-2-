package com.andina.trading.repository;

import com.andina.trading.model.OrdenCompraVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrdenCompraVentaRepository extends JpaRepository<OrdenCompraVenta, Long> {

    // Buscar órdenes por comisionista
    List<OrdenCompraVenta> findByComisionistaId(Long comisionistaId);

    // Buscar órdenes por inversionista
    List<OrdenCompraVenta> findByInversionistaId(Long inversionistaId);

    // Buscar órdenes por estado
    List<OrdenCompraVenta> findByEstado(OrdenCompraVenta.EstadoOrden estado);

    // Buscar órdenes por tipo (COMPRA o VENTA)
    List<OrdenCompraVenta> findByTipoOrden(OrdenCompraVenta.TipoOrden tipoOrden);
}