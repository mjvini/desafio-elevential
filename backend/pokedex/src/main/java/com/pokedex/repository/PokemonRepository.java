package com.pokedex.repository;

import com.pokedex.model.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PokemonRepository extends JpaRepository<Pokemon, Long> {

    Optional<Pokemon> findByNome(String nome);
    Optional<Pokemon> findByCodigo(Long codigo);
    boolean existsByCodigo(Long codigo);

    boolean existsByTipoPrimarioCodigo(Long tipoCodigo);
    boolean existsByTipoSecundarioCodigo(Long tipoCodigo);

    default List<Pokemon> findByNomeContainingIgnoreCase(String nome) {
        return findAll().stream()
                .filter(p -> p.getNome().toLowerCase().contains(nome.toLowerCase()))
                .toList();
    }

    @Query("SELECT p FROM Pokemon p WHERE p.tipoPrimario.nome = :tipo OR p.tipoSecundario.nome = :tipo")
    List<Pokemon> findByTipo(@Param("tipo") String tipo);

    @Query("SELECT p FROM Pokemon p WHERE " +
            "LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%')) AND " +
            "(:tipo IS NULL OR p.tipoPrimario.nome = :tipo OR p.tipoSecundario.nome = :tipo)")
    List<Pokemon> findByNomeAndTipo(@Param("nome") String nome, @Param("tipo") String tipo);

    default boolean existsByNome(String nome) {
        return findByNome(nome).isPresent();
    }
}