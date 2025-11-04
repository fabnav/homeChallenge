package com.homechallenge.pokedex.service;

import com.homechallenge.pokedex.dto.PokemonDTO;
import com.homechallenge.pokedex.exception.PokemonNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PokemonService {
    
    private final RestClient restClient;

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

