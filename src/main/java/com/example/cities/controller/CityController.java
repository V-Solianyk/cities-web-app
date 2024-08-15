package com.example.cities.controller;

import com.example.cities.dto.CityResponseDto;
import com.example.cities.service.CityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "City Game", description = "API для гри 'Міста'")
public class CityController {
    private final CityService service;

    @GetMapping("/begin")
    @Operation(summary = "Почати гру", description = "Система пропонує місто для початку гри.")
    public ResponseEntity<CityResponseDto> beginGame() {
        return ResponseEntity.ok(service.startGame());
    }

    @GetMapping("/next")
    @Operation(summary = "Відповісти на слово", description = "Гравець вводить слово, "
            + "система відповідає містом на останню літеру слова гравця.")
    public ResponseEntity<CityResponseDto> getNextCity(@RequestParam("word") String word) {
        return ResponseEntity.ok(service.getNextCity(word));
    }

    @PostMapping("/end")
    @Operation(summary = "Закінчити гру", description = "Завершує поточну гру і очищає дані.")
    public ResponseEntity<String> finishGame() {
        return ResponseEntity.ok(service.finishGame());
    }
}
