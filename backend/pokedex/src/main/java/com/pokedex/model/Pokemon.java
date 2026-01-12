package com.pokedex.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Pokemon")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pokemon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", unique = true, nullable = false)
    private Long codigo;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @ManyToOne
    @JoinColumn(name = "tipo_primario", nullable = false)
    private Tipo tipoPrimario;

    @ManyToOne
    @JoinColumn(name = "tipo_secundario")
    private Tipo tipoSecundario;
}
