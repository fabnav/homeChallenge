package com.homechallenge.pokedex.controller;

import com.homechallenge.pokedex.dto.PokemonDTO;
import com.homechallenge.pokedex.service.PokemonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pokemon")
@RequiredArgsConstructor
@Slf4j
public class PokemonController {

    private final PokemonService pokemonService;
    
    @GetMapping("/{name}")
    public ResponseEntity<PokemonDTO> getPokemon(@PathVariable String name) {
        log.info("Received request for pokemon: {}", name);
        PokemonDTO pokemon = pokemonService.getPokemonByName(name);
        return ResponseEntity.ok(pokemon);
    }
    
    @GetMapping("/translated/{name}")
    public ResponseEntity<PokemonDTO> getTranslatedPokemon(@PathVariable String name) {
        log.info("Received request for translated pokemon: {}", name);
        PokemonDTO pokemon = pokemonService.getTranslatedPokemonByName(name);
        return ResponseEntity.ok(pokemon);
    }
}

