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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class PokemonServiceTest {
    private RestClient pokeApiRestClient;
    private RestClient translationRestClient;
    private RestClient.RequestHeadersUriSpec<?> pokeApiRequestHeadersUriSpec;
    private RestClient.RequestBodyUriSpec translationRequestBodyUriSpec;
    private RestClient.RequestBodySpec translationRequestBodySpec;
    private RestClient.ResponseSpec pokeApiResponseSpec;
    private RestClient.ResponseSpec translationResponseSpec;
    private PokemonService pokemonService;
    private HttpRequestHelper pokeApiHttpRequestHelper;
    private HttpRequestHelper translationHttpRequestHelper;
    
    @BeforeMethod
    public void setUp() {
        pokeApiRestClient = mock(RestClient.class);
        pokeApiRequestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        pokeApiResponseSpec = mock(RestClient.ResponseSpec.class);
        pokeApiHttpRequestHelper = HttpRequestHelper.forGet(pokeApiRestClient, pokeApiRequestHeadersUriSpec, pokeApiResponseSpec);
        
        translationRestClient = mock(RestClient.class);
        translationRequestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        translationRequestBodySpec = mock(RestClient.RequestBodySpec.class);
        translationResponseSpec = mock(RestClient.ResponseSpec.class);
        translationHttpRequestHelper = HttpRequestHelper.forPost(translationRestClient, translationRequestBodyUriSpec, translationRequestBodySpec, translationResponseSpec);
        
        pokemonService = new PokemonService(pokeApiRestClient, translationRestClient);
    }

    @Test
    public void testGetPokemonByName_Success() {
        // Given
        String pokemonName = "mewtwo";
        PokemonDTO expected = new PokemonDTO(150L, "mewtwo",
                "It was created by a scientist after years of horrific gene splicing and DNA engineering experiments.", "rare", true);
        Map<String, Object> apiResponse = createPokemonApiResponse(expected);
        pokeApiHttpRequestHelper.mockGetRequestMap("/pokemon-species/{name}", pokemonName, apiResponse);
        
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
        pokeApiHttpRequestHelper.mockGetRequestMap("/pokemon-species/{name}", pokemonName, apiResponse);
        
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
        pokeApiHttpRequestHelper.mockGetRequestMap("/pokemon-species/{name}", pokemonName, emptyResponse);
        
        // When
        pokemonService.getPokemonByName(pokemonName);
        
        // Then
    }
    
    @Test
    public void testGetTranslatedPokemonByName_LegendaryUsesYoda() {
        // Given
        String pokemonName = "mewtwo";
        PokemonDTO pokemon = new PokemonDTO(150L, "mewtwo",
                "It was created by a scientist.", "rare", true);
        Map<String, Object> apiResponse = createPokemonApiResponse(pokemon);
        pokeApiHttpRequestHelper.mockGetRequestMap("/pokemon-species/{name}", pokemonName, apiResponse);
        
        String translatedText = "Created by a scientist,  it was.";
        Map<String, Object> translationResponse = createTranslationResponse(translatedText);
        translationHttpRequestHelper.mockPostRequestMap("/translate/{type}.json", "yoda", translationResponse);
        
        // When
        PokemonDTO result = pokemonService.getTranslatedPokemonByName(pokemonName);
        
        // Then
        assertNotNull(result);
        assertEquals(result.getDescription(), translatedText);
        verify(translationRestClient, times(1)).post();
    }
    
    @Test
    public void testGetTranslatedPokemonByName_CaveHabitatUsesYoda() {
        // Given
        String pokemonName = "zubat";
        PokemonDTO pokemon = new PokemonDTO(41L, "zubat",
                "Forms colonies in dark places.", "cave", false);
        Map<String, Object> apiResponse = createPokemonApiResponse(pokemon);
        pokeApiHttpRequestHelper.mockGetRequestMap("/pokemon-species/{name}", pokemonName, apiResponse);
        
        String translatedText = "In dark places,  colonies forms.";
        Map<String, Object> translationResponse = createTranslationResponse(translatedText);
        translationHttpRequestHelper.mockPostRequestMap("/translate/{type}.json", "yoda", translationResponse);
        
        // When
        PokemonDTO result = pokemonService.getTranslatedPokemonByName(pokemonName);
        
        // Then
        assertNotNull(result);
        assertEquals(result.getDescription(), translatedText);
        verify(translationRestClient, times(1)).post();
    }
    
    @Test
    public void testGetTranslatedPokemonByName_OtherUsesShakespeare() {
        // Given
        String pokemonName = "pikachu";
        PokemonDTO pokemon = new PokemonDTO(25L, "pikachu",
                "It stores electricity in its cheeks.", "forest", false);
        Map<String, Object> apiResponse = createPokemonApiResponse(pokemon);
        pokeApiHttpRequestHelper.mockGetRequestMap("/pokemon-species/{name}", pokemonName, apiResponse);
        
        String translatedText = "'t stores electricity in its cheeks.";
        Map<String, Object> translationResponse = createTranslationResponse(translatedText);
        translationHttpRequestHelper.mockPostRequestMap("/translate/{type}.json", "shakespeare", translationResponse);
        
        // When
        PokemonDTO result = pokemonService.getTranslatedPokemonByName(pokemonName);
        
        // Then
        assertNotNull(result);
        assertEquals(result.getDescription(), translatedText);
        verify(translationRestClient, times(1)).post();
    }
    
    @Test
    public void testGetTranslatedPokemonByName_TranslationFails_ReturnsOriginal() {
        // Given
        String pokemonName = "pikachu";
        String originalDescription = "It stores electricity in its cheeks.";
        PokemonDTO pokemon = new PokemonDTO(25L, "pikachu", originalDescription, "forest", false);
        Map<String, Object> apiResponse = createPokemonApiResponse(pokemon);
        pokeApiHttpRequestHelper.mockGetRequestMap("/pokemon-species/{name}", pokemonName, apiResponse);
        translationHttpRequestHelper.mockPostRequestMap("/translate/{type}.json", "shakespeare", new HashMap<>());
        
        // When
        PokemonDTO result = pokemonService.getTranslatedPokemonByName(pokemonName);
        
        // Then
        assertNotNull(result);
        assertEquals(result.getDescription(), originalDescription);
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
        
        verify(pokeApiRestClient, times(1)).get();
        verify(pokeApiRequestHeadersUriSpec, times(1)).uri(eq("/pokemon-species/{name}"), eq(result.getName()));
        verify(pokeApiRequestHeadersUriSpec, times(1)).retrieve();
        verify(pokeApiResponseSpec, times(1)).body(eq(Map.class));
    }
    
    private Map<String, Object> createTranslationResponse(String translatedText) {
        Map<String, Object> contents = new HashMap<>();
        contents.put("translated", translatedText);
        contents.put("text", "original text");
        contents.put("translation", "yoda");
        
        Map<String, Object> response = new HashMap<>();
        response.put("contents", contents);
        
        return response;
    }
}
