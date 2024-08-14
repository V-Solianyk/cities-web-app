package com.example.cities.controller;

import com.example.cities.dto.CityResponseDto;
import com.example.cities.service.CityService;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/game")
public class CityController {
    private final CityService service;

    @GetMapping("/begin")
    public ResponseEntity<CityResponseDto> beginGame() {
        return ResponseEntity.ok(service.startGame());
    }

    @GetMapping("/next")
    public ResponseEntity<CityResponseDto> getNextCity(@RequestParam("word") String word) {
        CityResponseDto cityResponseDto = service.getNextCity(word)
                .orElseThrow(() -> new NoSuchElementException("Гравець ввів"
                        + " слово не на ту літеру."));

        return ResponseEntity.ok(cityResponseDto);
    }

    @PostMapping("/end")
    public ResponseEntity<String> finishGame() {
        return ResponseEntity.ok(service.finishGame());
    }
}
