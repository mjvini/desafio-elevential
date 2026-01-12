package com.pokedex.controller;

import com.pokedex.model.Tipo;
import com.pokedex.repository.TipoRepository;
import com.pokedex.service.TipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tipos")
@CrossOrigin(origins = "*")
public class TipoController {

    @Autowired
    private TipoService tipoService;

    @Autowired
    private TipoRepository tipoRepository;

    @PostMapping
    public ResponseEntity<?> createTipo(@RequestBody Tipo tipo) {
        return tipoService.createTipo(tipo);
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<?> updateType(
            @PathVariable Long codigo,
            @RequestBody Tipo tipoModificado) {
        return tipoService.updateTipo(codigo, tipoModificado);
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<?> deleteTipo(@PathVariable Long codigo) {
        return tipoService.deleteTipo(codigo);
    }
    @GetMapping
    public ResponseEntity<List<Tipo>> getAllTypes() {
        List<Tipo> types = tipoRepository.findAll();
        return ResponseEntity.ok(types);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Tipo> getTypeById(@PathVariable Long codigo) {
        Optional<Tipo> type = tipoRepository.findById(codigo);
        return type.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}