package com.andina.trading.service;

import com.andina.trading.model.Inversionista;
import com.andina.trading.repository.InversionistaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InversionistaService {

    @Autowired
    private InversionistaRepository inversionistaRepository;

    public Inversionista crearInversionista(String nombre, double saldoDisponible) {
        Inversionista inversionista = new Inversionista(nombre, saldoDisponible);
        return inversionistaRepository.save(inversionista);
    }

    public List<Inversionista> obtenerTodosInversionistas() {
        return inversionistaRepository.findAll();
    }

    public Optional<Inversionista> obtenerInversionistaPorId(Long id) {
        return inversionistaRepository.findById(id);
    }

    public boolean comprarAcciones(Long inversionistaId, double monto) {
        Inversionista inversionista = inversionistaRepository.findById(inversionistaId)
                .orElseThrow(() -> new IllegalArgumentException("Inversionista no encontrado"));

        if (inversionista.getSaldoDisponible() >= monto) {
            inversionista.setSaldoDisponible(inversionista.getSaldoDisponible() - monto);
            inversionistaRepository.save(inversionista);
            return true;
        }
        return false;
    }
}
