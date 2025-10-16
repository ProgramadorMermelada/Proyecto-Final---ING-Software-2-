package com.andina.trading.controller;

import com.andina.trading.model.Comisionista;
import com.andina.trading.repository.ComisionistaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/comisionistas")
public class ComisionistaController {

    @Autowired
    private ComisionistaRepository comisionistaRepository;

    @PostMapping
    public Comisionista createComisionista(@RequestBody Comisionista comisionista) {
        return comisionistaRepository.save(comisionista);
    }

    @GetMapping("/{id}")
    public Optional<Comisionista> getComisionista(@PathVariable Long id) {
        return comisionistaRepository.findById(id);
    }

    @GetMapping
    public List<Comisionista> getAllComisionistas() {
        return comisionistaRepository.findAll();
    }

    @PutMapping("/{id}")
    public Comisionista updateComisionista(@PathVariable Long id, @RequestBody Comisionista comisionista) {
        return comisionistaRepository.findById(id)
                .map(existingComisionista -> {
                    existingComisionista.setNombre(comisionista.getNombre());
                    existingComisionista.setComision(comisionista.getComision());
                    return comisionistaRepository.save(existingComisionista);
                })
                .orElseGet(() -> {
                    comisionista.setId(id);
                    return comisionistaRepository.save(comisionista);
                });
    }

    @DeleteMapping("/{id}")
    public void deleteComisionista(@PathVariable Long id) {
        comisionistaRepository.deleteById(id);
    }
}
