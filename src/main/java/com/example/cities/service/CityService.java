package com.example.cities.service;

import com.example.cities.dto.CityResponseDto;
import java.util.Optional;

public interface CityService {
    CityResponseDto startGame();

    Optional<CityResponseDto> getNextCity(String city);

    String finishGame();
}
