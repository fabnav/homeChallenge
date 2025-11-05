package com.homechallenge.pokedex.exception;

public class PokemonNotFoundException extends RuntimeException {

  public PokemonNotFoundException(String pokemonName) {
    super("Pokemon not found: " + pokemonName);
  }

  public PokemonNotFoundException(String pokemonName, Throwable cause) {
    super("Pokemon not found: " + pokemonName, cause);
  }
}
