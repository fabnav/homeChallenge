package com.homechallenge.pokedex.controller;

import com.homechallenge.pokedex.service.PokemonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pokemon")
@RequiredArgsConstructor
@Slf4j
public class PokemonController {

    private final PokemonService pokemonService;
    
    
}

