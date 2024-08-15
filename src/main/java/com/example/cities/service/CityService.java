package com.example.cities.service;

import com.example.cities.dto.CityResponseDto;

public interface CityService {
    CityResponseDto startGame();

    CityResponseDto getNextCity(String name);

    String finishGame();
}
