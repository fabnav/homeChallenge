package com.homechallenge.pokedex.service;

import com.homechallenge.pokedex.dto.PokemonDTO;
import com.homechallenge.pokedex.exception.PokemonNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PokemonService {
    
    private final RestClient restClient;
    private final RestClient translationRestClient;
    
    public PokemonService(
            @Qualifier("pokeApiRestClient") RestClient pokeapiRestClient,
            @Qualifier("translationRestClient") RestClient translationRestClient
    ) {
        this.restClient = pokeapiRestClient;
        this.translationRestClient = translationRestClient;
    }

    public PokemonDTO getPokemonByName(String name) {
        log.info("Fetching pokemon data for: {}", name);
        
        Map<String, Object> response = restClient.get()
                .uri("/pokemon-species/{name}", name)
                .retrieve()
                .body(Map.class);
        if(response == null || response.isEmpty()) {
            throw new PokemonNotFoundException(name);
        }
        return mapToPokemonDTO(response);
    }
    
    public PokemonDTO getTranslatedPokemonByName(String name) {
        log.info("Fetching translated pokemon data for: {}", name);
        PokemonDTO pokemon = getPokemonByName(name);
        
        if (pokemon.getDescription() != null && !pokemon.getDescription().isEmpty()) {
            String translationType = determineTranslationType(pokemon);
            String translatedDescription = translate(pokemon.getDescription(), translationType);
            pokemon.setDescription(translatedDescription);
        }
        
        return pokemon;
    }
    
    private String determineTranslationType(PokemonDTO pokemon) {
        if (pokemon.isLegendary() || "cave".equalsIgnoreCase(pokemon.getHabitat())) {
            return "yoda";
        }
        return "shakespeare";
    }
    
    private String translate(String text, String translationType) {
        log.info("Translating text to {}: {}", translationType, text);
        
        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("text", text);
            
            Map<String, Object> response = translationRestClient.post()
                    .uri("/translate/{type}.json", translationType)
                    .body(formData)
                    .retrieve()
                    .body(Map.class);
            
            if (response != null && response.containsKey("contents")) {
                Map<String, Object> contents = (Map<String, Object>) response.get("contents");
                if (contents != null && contents.containsKey("translated")) {
                    String translated = (String) contents.get("translated");
                    log.info("Translation successful: {}", translated);
                    return translated;
                }
            }
        } catch (Exception e) {
            log.warn("Failed to translate text, using original description. Error: {}", e.getMessage());
        }
        
        return text;
    }
    
    private PokemonDTO mapToPokemonDTO(Map<String, Object> apiResponse) {
        PokemonDTO dto = new PokemonDTO();
        
        if (apiResponse.get("id") != null) {
            dto.setId(((Number) apiResponse.get("id")).longValue());
        }
        
        if (apiResponse.get("name") != null) {
            dto.setName((String) apiResponse.get("name"));
        }
        
        if (apiResponse.get("is_legendary") != null) {
            dto.setLegendary((Boolean) apiResponse.get("is_legendary"));
        }
        
        Map<String, Object> habitat = (Map<String, Object>) apiResponse.get("habitat");
        if (habitat != null) {
            dto.setHabitat((String) habitat.get("name"));
        }
        
        List<Map<String, Object>> flavorTextEntries =
            (List<Map<String, Object>>) apiResponse.get("flavor_text_entries");
        
        if (flavorTextEntries != null && !flavorTextEntries.isEmpty()) {
            Map<String, Object> firstEntry = flavorTextEntries.stream().findFirst().orElse(null);
            String flavorText = firstEntry.get("flavor_text").toString();
            // Remove \n and \f characters from the description
            String cleanedDescription = flavorText.replace("\n", " ").replace("\f", " ");
            dto.setDescription(cleanedDescription);
        }
        
        log.info("Mapped Pokemon: {}", dto.getName());
        return dto;
    }
}

