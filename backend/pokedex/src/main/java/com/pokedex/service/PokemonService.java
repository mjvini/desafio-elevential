// backend/src/main/java/com/pokedex/service/PokemonService.java
package com.pokedex.service;

import com.pokedex.model.Pokemon;
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
public class PokemonService {

    @Autowired
    private PokemonRepository pokemonRepository;

    @Autowired
    private TipoRepository tipoRepository;

    /**
     * Valida os dados do pokémon
     */
    public Map<String, String> validatePokemon(Pokemon pokemon) {
        Map<String, String> errors = new HashMap<>();

        if (pokemon.getCodigo() == null || pokemon.getCodigo().toString().isEmpty()) {
            errors.put("codigo", "O código do Pokemon é obrigatório");
        } else if (pokemon.getCodigo().toString().length() > 10) {
            errors.put("codigo", "O codigo não pode ter mais de 10 caracteres");
        }

        if (pokemon.getNome() == null || pokemon.getNome().trim().isEmpty()) {
            errors.put("nome", "O nome do Pokémon é obrigatório");
        } else if (pokemon.getNome().length() > 100) {
            errors.put("nome", "O nome não pode ter mais de 100 caracteres");
        }

        if (pokemon.getTipoPrimario() == null || pokemon.getTipoPrimario().getCodigo() == null) {
            errors.put("tipoPrimario", "Tipo primário é obrigatório");
        }

        return errors;
    }

    /**
     * Valida regras
     */
    public ResponseEntity<?> validateRegras(Pokemon pokemon, Long codigoExistente) {
        if (codigoExistente != null) {
            Optional<Pokemon> pokemonExistente = pokemonRepository.findByCodigo(codigoExistente);
            if (pokemonExistente.isPresent() &&
                    !pokemonExistente.get().getCodigo().equals(pokemon.getCodigo()) &&
                    pokemonRepository.existsByCodigo(pokemon.getCodigo())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Já existe um Pokémon com o código: " + pokemon.getCodigo());
            }
        } else {
            if (pokemonRepository.existsByCodigo(pokemon.getCodigo())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Já existe um Pokémon com o código: " + pokemon.getCodigo());
            }
        }

        Optional<Tipo> tipoPrimario = tipoRepository.findById(pokemon.getTipoPrimario().getCodigo());
        if (tipoPrimario.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Tipo primário não encontrado com ID: " + pokemon.getTipoPrimario().getCodigo());
        }
        pokemon.setTipoPrimario(tipoPrimario.get());

        if (pokemon.getTipoSecundario() != null && pokemon.getTipoSecundario().getCodigo() != null) {
            Optional<Tipo> tipoSecundario = tipoRepository.findById(pokemon.getTipoSecundario().getCodigo());
            if (tipoSecundario.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Tipo secundaŕio não encontrado com ID: " + pokemon.getTipoSecundario().getCodigo());
            }
            pokemon.setTipoSecundario(tipoSecundario.get());

            if (pokemon.getTipoPrimario().getCodigo().equals(pokemon.getTipoSecundario().getCodigo())) {
                return ResponseEntity.badRequest()
                        .body("Tipo primário e secundário não podem ser iguais");
            }
        } else {
            pokemon.setTipoSecundario(null);
        }

        return null;
    }

    /**
     * Cria um novo pokemon
     */
    public ResponseEntity<?> createPokemon(Pokemon pokemon) {
        Map<String, String> errors = validatePokemon(pokemon);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        ResponseEntity<?> regrasError = validateRegras(pokemon, null);
        if (regrasError != null) {
            return regrasError;
        }

        Pokemon savedPokemon = pokemonRepository.save(pokemon);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPokemon);
    }

    /**
     * Atualiza um pokémon existente
     */
    public ResponseEntity<?> updatePokemon(Long codigo, Pokemon pokemonModificado) {
        Optional<Pokemon> optionalPokemon = pokemonRepository.findByCodigo(codigo);
        if (optionalPokemon.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Pokemon pokemon = optionalPokemon.get();

        Map<String, String> errors = validatePokemon(pokemonModificado);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        if (!pokemon.getCodigo().equals(pokemonModificado.getCodigo()) &&
                pokemonRepository.existsByCodigo(pokemonModificado.getCodigo())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Já existe um Pokémon com o código: " + pokemonModificado.getCodigo());
        }

        pokemon.setCodigo(pokemonModificado.getCodigo());
        pokemon.setNome(pokemonModificado.getNome());
        pokemon.setTipoPrimario(pokemonModificado.getTipoPrimario());
        pokemon.setTipoSecundario(pokemonModificado.getTipoSecundario());

        Pokemon updatedPokemon = pokemonRepository.save(pokemon);
        return ResponseEntity.ok(updatedPokemon);
    }


    /**
     * Deleta um Pokémon
     */
    public ResponseEntity<?> deletePokemon(Long codigo) {
        Optional<Pokemon> optionalPokemon = pokemonRepository.findByCodigo(codigo);
        if (optionalPokemon.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        pokemonRepository.delete(optionalPokemon.get());
        return ResponseEntity.noContent().build();
    }
}