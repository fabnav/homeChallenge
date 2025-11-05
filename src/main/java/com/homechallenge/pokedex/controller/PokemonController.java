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

/**
 * REST controller for Pokemon-related endpoints. Provides access to Pokemon information and
 * translated descriptions.
 */
@RestController
@RequestMapping("/pokemon")
@RequiredArgsConstructor
@Slf4j
public class PokemonController {

  private final PokemonService pokemonService;

  /**
   * Retrieves basic Pokemon information by name.
   *
   * @param name the name of the Pokemon (case-insensitive)
   * @return ResponseEntity containing the Pokemon data with standard description
   */
  @GetMapping("/{name}")
  public ResponseEntity<PokemonDTO> getPokemon(@PathVariable String name) {
    log.info("Received request for pokemon: {}", name);
    PokemonDTO pokemon = pokemonService.getPokemonByName(name);
    return ResponseEntity.ok(pokemon);
  }

  /**
   * Retrieves Pokemon information with a fun-translated description. Uses Yoda translator for
   * legendary/cave Pokemon, Shakespeare translator for others.
   *
   * @param name the name of the Pokemon (case-insensitive)
   * @return ResponseEntity containing the Pokemon data with translated description
   */
  @GetMapping("/translated/{name}")
  public ResponseEntity<PokemonDTO> getTranslatedPokemon(@PathVariable String name) {
    log.info("Received request for translated pokemon: {}", name);
    PokemonDTO pokemon = pokemonService.getTranslatedPokemonByName(name);
    return ResponseEntity.ok(pokemon);
  }
}
