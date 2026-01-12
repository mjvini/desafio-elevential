package com.pokedex.repository;

import com.pokedex.model.Tipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoRepository extends JpaRepository<Tipo, Long> {

    Optional<Tipo> findByNome(String nome);

    default Optional<Tipo> findByNomeIgnoreCase(String nome) {
        return this.findAll().stream()
                .filter(t -> t.getNome().equalsIgnoreCase(nome))
                .findFirst();
    }

    default boolean existsByNome(String nome) {
        return findByNomeIgnoreCase(nome).isPresent();
    }
}