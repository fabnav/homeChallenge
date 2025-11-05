package com.homechallenge.pokedex.service;

import static com.homechallenge.pokedex.util.PokemonUtils.HABITAT_CAVE;
import static com.homechallenge.pokedex.util.PokemonUtils.POKEMON_SPECIES_PATH;
import static com.homechallenge.pokedex.util.PokemonUtils.TRANSLATE_PATH;
import static com.homechallenge.pokedex.util.PokemonUtils.TRANSLATION_TYPE_SHAKESPEARE;
import static com.homechallenge.pokedex.util.PokemonUtils.TRANSLATION_TYPE_YODA;

import com.homechallenge.pokedex.dto.PokemonDTO;
import com.homechallenge.pokedex.exception.PokemonNotFoundException;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class PokemonService {

  private final RestClient restClient;
  private final RestClient translationRestClient;

  public PokemonService(
      @Qualifier("pokeApiRestClient") RestClient pokeapiRestClient,
      @Qualifier("translationRestClient") RestClient translationRestClient) {
    this.restClient = pokeapiRestClient;
    this.translationRestClient = translationRestClient;
  }

  public PokemonDTO getPokemonByName(String name) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Pokemon name cannot be null or empty");
    }

    log.info("Fetching pokemon data for: {}", name.toLowerCase());

    try {
      Map<String, Object> response =
          restClient.get().uri(POKEMON_SPECIES_PATH, name.toLowerCase()).retrieve().body(Map.class);
      if (response == null || response.isEmpty()) {
        throw new PokemonNotFoundException(name);
      }
      return mapToPokemonDTO(response);
    } catch (PokemonNotFoundException e) {
      log.error("Error fetching pokemon data for: {}", name, e);
      throw new PokemonNotFoundException(name);
    }
  }

  public PokemonDTO getTranslatedPokemonByName(String name) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Pokemon name cannot be null or empty");
    }

    log.info("Fetching translated pokemon data for: {}", name);
    PokemonDTO pokemon = getPokemonByName(name);

    if (pokemon != null
        && pokemon.getDescription() != null
        && !pokemon.getDescription().isEmpty()) {
      String translationType = determineTranslationType(pokemon);
      String translatedDescription = translate(pokemon.getDescription(), translationType);
      pokemon.setDescription(translatedDescription);
    }

    return pokemon;
  }

  private String determineTranslationType(PokemonDTO pokemon) {
    if (pokemon.isLegendary() || HABITAT_CAVE.equalsIgnoreCase(pokemon.getHabitat())) {
      return TRANSLATION_TYPE_YODA;
    }
    return TRANSLATION_TYPE_SHAKESPEARE;
  }

  private String translate(String text, String translationType) {
    log.info("Translating text to {}: {}", translationType, text);

    try {
      MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
      formData.add("text", text);

      Map<String, Object> response =
          translationRestClient
              .post()
              .uri(TRANSLATE_PATH, translationType)
              .body(formData)
              .retrieve()
              .body(Map.class);

      if (response != null && response.containsKey("contents")) {
        Map<String, Object> contents = (Map<String, Object>) response.get("contents");
        if (contents != null && contents.containsKey("translated")) {
          String translated = (String) contents.get("translated");
          if (translated != null && !translated.trim().isEmpty()) {
            log.info("Translation successful: {}", translated);
            return translated;
          }
        }
      }
      log.warn("Translation response did not contain valid translated text");
    } catch (Exception e) {
      log.warn("Failed to translate text, using original description. Error: {}", e.getMessage());
    }

    return text;
  }

  private PokemonDTO mapToPokemonDTO(Map<String, Object> apiResponse) {
    PokemonDTO dto = new PokemonDTO();

    try {
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
      if (habitat != null && habitat.get("name") != null) {
        dto.setHabitat((String) habitat.get("name"));
      }

      List<Map<String, Object>> flavorTextEntries =
          (List<Map<String, Object>>) apiResponse.get("flavor_text_entries");

      if (flavorTextEntries != null && !flavorTextEntries.isEmpty()) {
        Map<String, Object> firstEntry =
            flavorTextEntries.stream()
                .filter(
                    f -> {
                      if (f == null || f.get("language") == null) {
                        return false;
                      }
                      Map<String, Object> language = (Map<String, Object>) f.get("language");
                      return language.get("name") != null
                          && "en".equalsIgnoreCase(language.get("name").toString());
                    })
                .findFirst()
                .orElse(null);

        if (firstEntry != null && firstEntry.get("flavor_text") != null) {
          String flavorText = firstEntry.get("flavor_text").toString();
          String cleanedDescription = flavorText.replaceAll("[\\n\\f]", " ");
          dto.setDescription(cleanedDescription);
        } else {
          log.warn("No English text found for pokemon");
          dto.setDescription("");
        }
      } else {
        log.warn("No text entries found for pokemon");
        dto.setDescription("");
      }
    } catch (Exception e) {
      log.error("Unexpected error mapping Pokemon DTO", e);
      throw new RuntimeException("Error mapping Pokemon data", e);
    }

    log.info("Mapped Pokemon: {}", dto.getName());
    return dto;
  }
}
