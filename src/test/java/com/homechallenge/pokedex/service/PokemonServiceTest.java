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

import static com.homechallenge.pokedex.util.PokemonUtils.HABITAT_CAVE;
import static com.homechallenge.pokedex.util.PokemonUtils.POKEMON_SPECIES_PATH;
import static com.homechallenge.pokedex.util.PokemonUtils.TRANSLATE_PATH;
import static com.homechallenge.pokedex.util.PokemonUtils.TRANSLATION_TYPE_SHAKESPEARE;
import static com.homechallenge.pokedex.util.PokemonUtils.TRANSLATION_TYPE_YODA;
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
        String description = "It was created by\na scientist after\nyears of horrific\fgene splicing and\nDNA engineering\nexperiments.";
        String cleanedDescription = "It was created by a scientist after years of horrific gene splicing and DNA engineering experiments.";
        PokemonDTO expected = new PokemonDTO(150L, "mewtwo", description, "rare", true);
        Map<String, Object> apiResponse = createPokemonApiResponse(expected);
        expected.setDescription(cleanedDescription);
        pokeApiHttpRequestHelper.mockGetRequestMap(POKEMON_SPECIES_PATH, pokemonName, apiResponse);
        
        // When
        PokemonDTO result = pokemonService.getPokemonByName(pokemonName);
        
        // Then
        verifyResultAndMocks(result, expected);
    }
    
    @Test
    public void testGetPokemonByName_Pikachu() {
        // Given
        String pokemonName = "pikachu";
        String description = "When several of\nthese POKéMON\ngather, their\felectricity could\nbuild and cause\nlightning storms.";
        String cleanedDescription = "When several of these POKéMON gather, their electricity could build and cause lightning storms.";
        PokemonDTO expected = new PokemonDTO(25L, "pikachu", description, "forest", false);
        Map<String, Object> apiResponse = createPokemonApiResponse(expected);
        expected.setDescription(cleanedDescription);
        pokeApiHttpRequestHelper.mockGetRequestMap(POKEMON_SPECIES_PATH, pokemonName, apiResponse);
        
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
        pokeApiHttpRequestHelper.mockGetRequestMap(POKEMON_SPECIES_PATH, pokemonName, emptyResponse);
        
        // When
        pokemonService.getPokemonByName(pokemonName);
        
        // Then
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Pokemon name cannot be null or empty")
    public void testGetPokemonByName_NullName() {
        // Given
        String pokemonName = null;
        
        // When
        pokemonService.getPokemonByName(pokemonName);
        
        // Then - Exception is expected
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Pokemon name cannot be null or empty")
    public void testGetPokemonByName_EmptyName() {
        // Given
        String pokemonName = "";
        
        // When
        pokemonService.getPokemonByName(pokemonName);
        
        // Then - Exception is expected
    }
    
    @Test
    public void testGetTranslatedPokemonByName_LegendaryUsesYoda() {
        // Given
        String pokemonName = "mewtwo";
        String description = "It was created by\na scientist after\nyears of horrific\fgene splicing and\nDNA engineering\nexperiments.";
        String translatedText = "Created by a scientist,  it was.";
        PokemonDTO expected = new PokemonDTO(150L, "mewtwo", description, "rare", true);
        Map<String, Object> apiResponse = createPokemonApiResponse(expected);
        expected.setDescription(translatedText);
        pokeApiHttpRequestHelper.mockGetRequestMap(POKEMON_SPECIES_PATH, pokemonName, apiResponse);
        
        Map<String, Object> translationResponse = createTranslationResponse(translatedText, TRANSLATION_TYPE_YODA);
        translationHttpRequestHelper.mockPostRequestMap(TRANSLATE_PATH, TRANSLATION_TYPE_YODA, translationResponse);
        
        // When
        PokemonDTO result = pokemonService.getTranslatedPokemonByName(pokemonName);
        
        // Then
        verifyResultAndMocks(result, expected, true);
    }
    
    @Test
    public void testGetTranslatedPokemonByName_CaveHabitatUsesYoda() {
        // Given
        String pokemonName = "zubat";
        String description = "Forms colonies in\nperpetually dark\nplaces. Uses\fultrasonic waves\nto identify and\napproach targets.";
        String translatedText = "In dark places,  colonies forms.";
        PokemonDTO expected = new PokemonDTO(41L, "zubat", description, HABITAT_CAVE, false);
        Map<String, Object> apiResponse = createPokemonApiResponse(expected);
        expected.setDescription(translatedText);
        pokeApiHttpRequestHelper.mockGetRequestMap(POKEMON_SPECIES_PATH, pokemonName, apiResponse);
        
        Map<String, Object> translationResponse = createTranslationResponse(translatedText, TRANSLATION_TYPE_YODA);
        translationHttpRequestHelper.mockPostRequestMap(TRANSLATE_PATH, TRANSLATION_TYPE_YODA, translationResponse);
        
        // When
        PokemonDTO result = pokemonService.getTranslatedPokemonByName(pokemonName);
        
        // Then
        verifyResultAndMocks(result, expected, true);
    }
    
    @Test
    public void testGetTranslatedPokemonByName_OtherUsesShakespeare() {
        // Given
        String pokemonName = "pikachu";
        String description = "When several of\nthese POKéMON\ngather, their\felectricity could\nbuild and cause\nlightning storms.";
        String translatedText = "'t stores electricity in its cheeks.";
        PokemonDTO expected = new PokemonDTO(25L, "pikachu", description, "forest", false);
        Map<String, Object> apiResponse = createPokemonApiResponse(expected);
        expected.setDescription(translatedText);
        pokeApiHttpRequestHelper.mockGetRequestMap(POKEMON_SPECIES_PATH, pokemonName, apiResponse);
        
        Map<String, Object> translationResponse = createTranslationResponse(translatedText, TRANSLATION_TYPE_SHAKESPEARE);
        translationHttpRequestHelper.mockPostRequestMap(TRANSLATE_PATH, TRANSLATION_TYPE_SHAKESPEARE, translationResponse);
        
        // When
        PokemonDTO result = pokemonService.getTranslatedPokemonByName(pokemonName);
        
        // Then
        verifyResultAndMocks(result, expected, true);
    }
    
    @Test
    public void testGetTranslatedPokemonByName_TranslationFails_ReturnsOriginal() {
        // Given
        String pokemonName = "pikachu";
        String originalDescription = "When several of\nthese POKéMON\ngather, their\felectricity could\nbuild and cause\nlightning storms.";
        String cleanedDescription = "When several of these POKéMON gather, their electricity could build and cause lightning storms.";
        PokemonDTO expected = new PokemonDTO(25L, "pikachu", originalDescription, "forest", false);
        Map<String, Object> apiResponse = createPokemonApiResponse(expected);
        expected.setDescription(cleanedDescription);
        pokeApiHttpRequestHelper.mockGetRequestMap(POKEMON_SPECIES_PATH, pokemonName, apiResponse);
        translationHttpRequestHelper.mockPostRequestMap(TRANSLATE_PATH, TRANSLATION_TYPE_SHAKESPEARE, new HashMap<>());
        
        // When
        PokemonDTO result = pokemonService.getTranslatedPokemonByName(pokemonName);
        
        // Then
        verifyResultAndMocks(result, expected, true);
    }
    
    @Test(expectedExceptions = PokemonNotFoundException.class, expectedExceptionsMessageRegExp = "Pokemon not found: pikachu")
    public void testGetPokemonByName_NullResponse() {
        // Given
        String pokemonName = "pikachu";
        pokeApiHttpRequestHelper.mockGetRequestMap(POKEMON_SPECIES_PATH, pokemonName, null);
        
        // When
        pokemonService.getPokemonByName(pokemonName);
        
        // Then - Exception is expected
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Pokemon name cannot be null or empty")
    public void testGetTranslatedPokemonByName_NullName() {
        // Given
        String pokemonName = null;
        
        // When
        pokemonService.getTranslatedPokemonByName(pokemonName);
        
        // Then - Exception is expected
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Pokemon name cannot be null or empty")
    public void testGetTranslatedPokemonByName_EmptyName() {
        // Given
        String pokemonName = "";
        
        // When
        pokemonService.getTranslatedPokemonByName(pokemonName);
        
        // Then - Exception is expected
    }
    
    @Test
    public void testGetPokemonByName_MissingTextEntries() {
        // Given
        String pokemonName = "missingno";
        Map<String, Object> apiResponse = new HashMap<>();
        apiResponse.put("id", 0L);
        apiResponse.put("name", pokemonName);
        apiResponse.put("is_legendary", false);
        Map<String, Object> habitat = new HashMap<>();
        habitat.put("name", "unknown");
        apiResponse.put("habitat", habitat);
        apiResponse.put("flavor_text_entries", List.of());
        pokeApiHttpRequestHelper.mockGetRequestMap(POKEMON_SPECIES_PATH, pokemonName, apiResponse);
        
        // When
        PokemonDTO result = pokemonService.getPokemonByName(pokemonName);
        
        // Then
        assertNotNull(result);
        assertEquals(result.getName(), pokemonName);
        assertEquals(result.getDescription(), "");
    }
    
    @Test
    public void testGetPokemonByName_NoEnglishText() {
        // Given
        String pokemonName = "japanese-only";
        Map<String, Object> apiResponse = new HashMap<>();
        apiResponse.put("id", 999L);
        apiResponse.put("name", pokemonName);
        apiResponse.put("is_legendary", false);
        Map<String, Object> habitat = new HashMap<>();
        habitat.put("name", "urban");
        apiResponse.put("habitat", habitat);
        Map<String, Object> flavorTextEntry = new HashMap<>();
        flavorTextEntry.put("flavor_text", "日本語のテキスト");
        flavorTextEntry.put("language", Map.of("name", "ja"));
        apiResponse.put("flavor_text_entries", List.of(flavorTextEntry));
        pokeApiHttpRequestHelper.mockGetRequestMap(POKEMON_SPECIES_PATH, pokemonName, apiResponse);
        
        // When
        PokemonDTO result = pokemonService.getPokemonByName(pokemonName);
        
        // Then
        assertNotNull(result);
        assertEquals(result.getName(), pokemonName);
        assertEquals(result.getDescription(), ""); // Should be empty string
    }
    
    @Test
    public void testGetPokemonByName_NullHabitat() {
        // Given
        String pokemonName = "nohabitat";
        String description = "A pokemon with no habitat";
        Map<String, Object> apiResponse = new HashMap<>();
        apiResponse.put("id", 1000L);
        apiResponse.put("name", pokemonName);
        apiResponse.put("is_legendary", false);
        apiResponse.put("habitat", null);
        Map<String, Object> flavorTextEntry = new HashMap<>();
        flavorTextEntry.put("flavor_text", description);
        flavorTextEntry.put("language", Map.of("name", "en"));
        apiResponse.put("flavor_text_entries", List.of(flavorTextEntry));
        pokeApiHttpRequestHelper.mockGetRequestMap(POKEMON_SPECIES_PATH, pokemonName, apiResponse);
        
        // When
        PokemonDTO result = pokemonService.getPokemonByName(pokemonName);
        
        // Then
        assertNotNull(result);
        assertEquals(result.getName(), pokemonName);
        assertNull(result.getHabitat());
        assertEquals(result.getDescription(), description);
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
        flavorTextEntry.put("language", Map.of("name", "en"));
        
        response.put("flavor_text_entries", List.of(flavorTextEntry));
        
        return response;
    }
    
    private void verifyResultAndMocks(PokemonDTO result, PokemonDTO expected) {
        verifyResultAndMocks(result, expected, false);
    }
    
    private void verifyResultAndMocks(PokemonDTO result, PokemonDTO expected, boolean verifyTranslation) {
        assertNotNull(result);
        assertEquals(result, expected);
        
        verify(pokeApiRestClient, times(1)).get();
        verify(pokeApiRequestHeadersUriSpec, times(1)).uri(eq(POKEMON_SPECIES_PATH), eq(result.getName()));
        verify(pokeApiRequestHeadersUriSpec, times(1)).retrieve();
        verify(pokeApiResponseSpec, times(1)).body(eq(Map.class));
        
        if (verifyTranslation) {
            verify(translationRestClient, times(1)).post();
            String translationType = (expected.isLegendary() || HABITAT_CAVE.equalsIgnoreCase(expected.getHabitat())) ? TRANSLATION_TYPE_YODA : TRANSLATION_TYPE_SHAKESPEARE;
            verify(translationRequestBodyUriSpec, times(1)).uri(eq(TRANSLATE_PATH), eq(translationType));
            verify(translationRequestBodySpec, times(1)).retrieve();
            verify(translationResponseSpec, times(1)).body(eq(Map.class));
        }
    }
    
    private Map<String, Object> createTranslationResponse(String translatedText, String type) {
        Map<String, Object> contents = new HashMap<>();
        contents.put("translated", translatedText);
        contents.put("text", "original text");
        contents.put("translation", type);
        
        Map<String, Object> response = new HashMap<>();
        response.put("contents", contents);
        
        return response;
    }
}
