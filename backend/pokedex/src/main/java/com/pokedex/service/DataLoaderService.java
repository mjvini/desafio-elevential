package com.pokedex.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokedex.dto.PokemonDTO;
import com.pokedex.model.Pokemon;
import com.pokedex.model.Tipo;
import com.pokedex.repository.PokemonRepository;
import com.pokedex.repository.TipoRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataLoaderService {

    private final TipoRepository tipoRepository;
    private final PokemonRepository pokemonRepository;
    private final ObjectMapper objectMapper;

    @PostConstruct
    @Transactional
    public void init() {
        if (pokemonRepository.count() == 0) {
            log.info("Banco vazio. Carregando Pokémons...");
            carregarTipos();
            carregarPokemons();
        } else {
            log.info("Banco já populado com {} Pokémons", pokemonRepository.count());
        }
    }

    private void carregarTipos() {
        String[] tiposNomes = {
                "normal", "fire", "water", "electric", "grass", "ice",
                "fighting", "poison", "ground", "flying", "psychic",
                "bug", "rock", "ghost", "dark", "dragon", "steel", "fairy"
        };

        for (String nome : tiposNomes) {
            if (!tipoRepository.existsByNome(nome)) {
                Tipo tipo = new Tipo();
                tipo.setNome(nome);
                tipoRepository.save(tipo);
            }
        }
        log.info("{} tipos carregados", tiposNomes.length);
    }

    private void carregarPokemons() {
        try {
            ClassPathResource resource = new ClassPathResource("dados/dados_iniciais.json");

            if (!resource.exists()) {
                log.warn("Arquivo JSON não encontrado");
                return;
            }

            try (InputStream inputStream = resource.getInputStream()) {
                List<PokemonDTO> pokemonsDTO = objectMapper.readValue(
                        inputStream,
                        new TypeReference<List<PokemonDTO>>() {}
                );

                Map<String, Tipo> tiposCache = new HashMap<>();
                int inseridos = 0;

                for (PokemonDTO dto : pokemonsDTO) {
                    if (criarPokemonSeNaoExistir(dto, tiposCache)) {
                        inseridos++;
                    }
                }

                log.info("{}/{} Pokémons carregados", inseridos, pokemonsDTO.size());

            }
        } catch (Exception e) {
            log.error("Erro ao carregar Pokémons", e);
        }
    }

    private boolean criarPokemonSeNaoExistir(PokemonDTO dto, Map<String, Tipo> tiposCache) {
        if (pokemonRepository.existsByCodigo(dto.getCodigo()) ||
                pokemonRepository.existsByNome(dto.getNome())) {
            log.debug("Pokémon já existe: {} (código: {})", dto.getNome(), dto.getCodigo());
            return false;
        }

        try {
            Pokemon pokemon = new Pokemon();
            pokemon.setCodigo(dto.getCodigo());
            pokemon.setNome(dto.getNome());

            Tipo tipoPrimario = tiposCache.computeIfAbsent(dto.getTipoPrimario(),
                    nome -> tipoRepository.findByNomeIgnoreCase(nome)
                            .orElseThrow(() -> new RuntimeException("Tipo não encontrado: " + nome))
            );
            pokemon.setTipoPrimario(tipoPrimario);

            if (dto.getTipoSecundario() != null &&
                    !dto.getTipoSecundario().equalsIgnoreCase("null")) {
                Tipo tipoSecundario = tiposCache.computeIfAbsent(dto.getTipoSecundario(),
                        nome -> tipoRepository.findByNomeIgnoreCase(nome).orElse(null)
                );
                pokemon.setTipoSecundario(tipoSecundario);
            }

            pokemonRepository.save(pokemon);
            log.debug("Pokémon criado: {} (código: {})", dto.getNome(), dto.getCodigo());
            return true;

        } catch (Exception e) {
            log.warn("Não foi possível criar Pokémon {} (código: {}): {}",
                    dto.getNome(), dto.getCodigo(), e.getMessage());
            return false;
        }
    }
}
