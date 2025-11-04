package com.homechallenge.pokedex.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PokemonDTO {
    private Long id;
    
    private String name;
    
    private String description;
    
    private String habitat;
    
    private boolean isLegendary;
}

