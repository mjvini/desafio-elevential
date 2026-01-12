package com.pokedex.dto;

import lombok.Data;

@Data
public class PokemonDTO {
    private Long codigo;
    private String nome;
    private String tipoPrimario;
    private String tipoSecundario;
}