// backend/src/main/java/com/pokedex/service/TypeService.java
package com.pokedex.service;

import com.pokedex.model.Tipo;
import com.pokedex.repository.PokemonRepository;
import com.pokedex.repository.TipoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class TipoService {

    @Autowired
    private TipoRepository tipoRepository;

    @Autowired
    private PokemonRepository pokemonRepository;

    /**
     * Valida os dados básicos de um Tipo
     */
    public Map<String, String> validateTipo(Tipo tipo) {
        Map<String, String> errors = new HashMap<>();

        if (tipo.getNome() == null || tipo.getNome().trim().isEmpty()) {
            errors.put("nome", "O nome do tipo é obrigatório");
        } else if (tipo.getNome().length() > 50) {
            errors.put("nome", "O nome não pode ter mais de 50 caracteres");
        }

        return errors;
    }

    /**
     * Valida regras de negócio para tipo
     */
    public ResponseEntity<?> validateRegras(Tipo tipo, Long codigo) {
        if (codigo != null) {
            Optional<Tipo> tipoExistente = tipoRepository.findById(codigo);
            if (tipoExistente.isPresent() &&
                    !tipoExistente.get().getNome().equals(tipo.getNome()) &&
                    tipoRepository.existsByNome(tipo.getNome())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Já existe um tipo com o nome: " + tipo.getNome());
            }
        } else {
            if (tipoRepository.existsByNome(tipo.getNome())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Já existe um tipo com o nome: " + tipo.getNome());
            }
        }

        return null;
    }

    /**
     * Cria um novo tipo
     */
    public ResponseEntity<?> createTipo(Tipo tipo) {
        Map<String, String> errors = validateTipo(tipo);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        ResponseEntity<?> regrasError = validateRegras(tipo, null);
        if (regrasError != null) {
            return regrasError;
        }

        Tipo savedType = tipoRepository.save(tipo);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedType);
    }

    /**
     * Atualiza um tipo
     */
    public ResponseEntity<?> updateTipo(Long codigo, Tipo tipoModificado) {
        Optional<Tipo> optionalTipo = tipoRepository.findById(codigo);
        if (optionalTipo.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Tipo tipo = optionalTipo.get();

        Map<String, String> errors = validateTipo(tipoModificado);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        ResponseEntity<?> regrasError = validateRegras(tipoModificado, codigo);
        if (regrasError != null) {
            return regrasError;
        }

        tipo.setNome(tipoModificado.getNome());

        Tipo updatedTipo = tipoRepository.save(tipo);

        return ResponseEntity.ok(updatedTipo);
    }

    /**
     * Deleta um Tipo
     */
    public ResponseEntity<?> deleteTipo(Long codigo) {
        Optional<Tipo> optionalTipo = tipoRepository.findById(codigo);
        if (optionalTipo.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Tipo tipo = optionalTipo.get();

        if (pokemonRepository.existsByTipoPrimarioCodigo(codigo) ||
                pokemonRepository.existsByTipoSecundarioCodigo(codigo)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Não é possível excluir o tipo '" + tipo.getNome() +
                            "' pois ele está sendo usado por um ou mais Pokémons.");
        }

        tipoRepository.delete(tipo);
        return ResponseEntity.noContent().build();
    }
}