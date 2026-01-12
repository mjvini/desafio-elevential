package com.pokedex.controller;

import com.pokedex.dto.PokemonDTO;
import com.pokedex.model.Pokemon;
import com.pokedex.service.PokemonService;
import lombok.RequiredArgsConstructor;
import com.pokedex.repository.PokemonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pokemons")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class PokemonController {

    @Autowired
    private PokemonService pokemonService;

    @Autowired
    private PokemonRepository pokemonRepository;

    @GetMapping
    public ResponseEntity<List<PokemonDTO>> getAllPokemons() {
        List<PokemonDTO> pokemons = pokemonRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(pokemons);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<PokemonDTO> getPokemonByCodigo(@PathVariable Long codigo) {
        return pokemonRepository.findByCodigo(codigo)
                .map(pokemon -> ResponseEntity.ok(convertToDTO(pokemon)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<PokemonDTO>> searchPokemons(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String tipo) {

        List<Pokemon> pokemons;

        if (nome != null && tipo != null) {
            pokemons = pokemonRepository.findByNomeAndTipo(nome, tipo);
        } else if (nome != null) {
            pokemons = pokemonRepository.findByNomeContainingIgnoreCase(nome);
        } else if (tipo != null) {
            pokemons = pokemonRepository.findByTipo(tipo);
        } else {
            pokemons = pokemonRepository.findAll();
        }

        List<PokemonDTO> response = pokemons.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> createPokemon(@RequestBody Pokemon pokemon) {
        return pokemonService.createPokemon(pokemon);
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<?> updatePokemon(
            @PathVariable Long codigo,
            @RequestBody Pokemon pokemonModificado) {
        return pokemonService.updatePokemon(codigo, pokemonModificado);
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<?> deletePokemon(@PathVariable Long codigo) {
        return pokemonService.deletePokemon(codigo);
    }

    private PokemonDTO convertToDTO(Pokemon pokemon) {
        PokemonDTO dto = new PokemonDTO();
        dto.setCodigo(pokemon.getCodigo());
        dto.setNome(pokemon.getNome());
        dto.setTipoPrimario(pokemon.getTipoPrimario().getNome());

        if (pokemon.getTipoSecundario() != null) {
            dto.setTipoSecundario(pokemon.getTipoSecundario().getNome());
        }

        return dto;
    }
}