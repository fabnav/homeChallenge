package com.homechallenge.pokedex.service;

import com.homechallenge.pokedex.dto.PokemonDTO;
import com.homechallenge.pokedex.exception.PokemonNotFoundException;
import com.homechallenge.pokedex.helper.HttpRequestHelper;
import org.springframework.web.client.RestClient;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class PokemonServiceTest {
    private RestClient restClient;
    private RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;
    private RestClient.ResponseSpec responseSpec;
    private PokemonService pokemonService;
    private HttpRequestHelper httpRequestHelper;
    
    @BeforeMethod
    public void setUp() {
        restClient = mock(RestClient.class);
        requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        responseSpec = mock(RestClient.ResponseSpec.class);
        pokemonService = new PokemonService(restClient);
        httpRequestHelper = new HttpRequestHelper(restClient, requestHeadersUriSpec, responseSpec);
    }

    @Test
    public void testGetPokemonByName_Success() {
        // Given
        String pokemonName = "mewtwo";
        PokemonDTO expected = new PokemonDTO(150L, "mewtwo",
                "It was created by a scientist after years of horrific gene splicing and DNA engineering experiments.", "rare", true);
        Map<String, Object> apiResponse = createPokemonApiResponse(expected);
        httpRequestHelper.mockGetRequestMap("/pokemon-species/{name}", pokemonName, apiResponse);
        
        // When
        PokemonDTO result = pokemonService.getPokemonByName(pokemonName);
        
        // Then
        verifyResultAndMocks(result, expected);
    }
    
    @Test
    public void testGetPokemonByName_Pikachu() {
        // Given
        String pokemonName = "pikachu";
        PokemonDTO expected = new PokemonDTO(25L, "pikachu",
                "When several of these Pokémon gather, their electricity could build and cause lightning storms.", "forest", false);
        Map<String, Object> apiResponse = createPokemonApiResponse(expected);
        httpRequestHelper.mockGetRequestMap("/pokemon-species/{name}", pokemonName, apiResponse);
        
        // When
        PokemonDTO result = pokemonService.getPokemonByName(pokemonName);
        
        // Then
        verifyResultAndMocks(result, expected);
    }
    
    @Test(expectedExceptions = PokemonNotFoundException.class, expectedExceptionsMessageRegExp = "Pokemon not found: nonexistent")
    public void testGetPokemonByName_NotFound() {
        // Given
        String pokemonName = "nonexistent";
        Map<String, Object> emptyResponse = new HashMap<>();
        httpRequestHelper.mockGetRequestMap("/pokemon-species/{name}", pokemonName, emptyResponse);
        
        // When
        pokemonService.getPokemonByName(pokemonName);
        
        // Then - Exception is expected to be thrown
    }
    
    private Map<String, Object> createPokemonApiResponse(PokemonDTO dto) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", dto.getId());
        response.put("name", dto.getName());
        response.put("is_legendary", dto.isLegendary());
        
        Map<String, Object> habitat = new HashMap<>();
        habitat.put("name", dto.getHabitat());
        response.put("habitat", habitat);
        
        Map<String, Object> flavorTextEntry = new HashMap<>();
        flavorTextEntry.put("flavor_text", dto.getDescription());
        
        response.put("flavor_text_entries", List.of(flavorTextEntry));
        
        return response;
    }
    
    private void verifyResultAndMocks(PokemonDTO result, PokemonDTO expected) {
        assertNotNull(result);
        assertEquals(result, expected);
        
        verify(restClient, times(1)).get();
        verify(requestHeadersUriSpec, times(1)).uri(eq("/pokemon-species/{name}"), eq(result.getName()));
        verify(requestHeadersUriSpec, times(1)).retrieve();
        verify(responseSpec, times(1)).body(eq(Map.class));
    }
}
