package com.homechallenge.pokedex.controller;

import com.homechallenge.pokedex.dto.PokemonDTO;
import com.homechallenge.pokedex.exception.PokemonNotFoundException;
import com.homechallenge.pokedex.service.PokemonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class PokemonControllerTest {
    
    private PokemonService pokemonService;
    private PokemonController pokemonController;
    
    @BeforeMethod
    public void setUp() {
        pokemonService = mock(PokemonService.class);
        pokemonController = new PokemonController(pokemonService);
    }
    
    @Test
    public void testGetPokemon_Success() {
        // Given
        String pokemonName = "mewtwo";
        PokemonDTO expectedDto = new PokemonDTO(
            150L,
            "mewtwo",
            "It was created by a scientist after years of horrific gene splicing and DNA engineering experiments.",
            "rare",
            true
        );
        
        when(pokemonService.getPokemonByName(pokemonName)).thenReturn(expectedDto);
        
        // When
        ResponseEntity<PokemonDTO> response = pokemonController.getPokemon(pokemonName);
        
        // Then
        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), expectedDto);
        assertEquals(response.getBody().getName(), "mewtwo");
        assertTrue(response.getBody().isLegendary());
        
        verify(pokemonService, times(1)).getPokemonByName(pokemonName);
    }
    
    @Test
    public void testGetPokemon_DifferentPokemon() {
        // Given
        String pokemonName = "pikachu";
        PokemonDTO expectedDto = new PokemonDTO(
            25L,
            "pikachu",
            "When several of these Pokémon gather, their electricity could build and cause lightning storms.",
            "forest",
            false
        );
        
        when(pokemonService.getPokemonByName(pokemonName)).thenReturn(expectedDto);
        
        // When
        ResponseEntity<PokemonDTO> response = pokemonController.getPokemon(pokemonName);
        
        // Then
        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getName(), "pikachu");
        assertFalse(response.getBody().isLegendary());
        
        verify(pokemonService, times(1)).getPokemonByName(pokemonName);
    }
    
    @Test(expectedExceptions = PokemonNotFoundException.class, expectedExceptionsMessageRegExp = "Pokemon not found: nonexistent")
    public void testGetPokemon_NotFound() {
        // Given
        String pokemonName = "nonexistent";
        when(pokemonService.getPokemonByName(pokemonName)).thenThrow(new PokemonNotFoundException(pokemonName));
        
        // When
        pokemonController.getPokemon(pokemonName);
        
        // Then - Exception is expected to be thrown
    }
}
