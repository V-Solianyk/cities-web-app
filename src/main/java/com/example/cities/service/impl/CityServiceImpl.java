package com.example.cities.service.impl;

import com.example.cities.dto.CityResponseDto;
import com.example.cities.entity.City;
import com.example.cities.mapper.CityMapper;
import com.example.cities.repository.CityRepository;
import com.example.cities.service.CityService;
import com.example.cities.util.Generator;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {
    private static final List<String> INITIAL_CITES = Arrays.asList(
            "New York", "London", "Paris", "Tokyo", "Beijing", "Sydney",
            "Rome", "Ivano-Frankivsk", "Berlin", "Dubai", "Istanbul", "Rio de Janeiro",
            "Mumbai", "Los Angeles", "Shanghai", "Singapore", "Toronto", "Buenos Aires",
            "Madrid", "Johannesburg"); //todo - or just Random words( not only city)
    private final CityRepository repository;
    private final CityMapper mapper;
    private final Generator generator;

    @Override
    public CityResponseDto startGame() {
        String randomlyCity = INITIAL_CITES.get(new Random().nextInt(INITIAL_CITES.size()));
        City cityFromServer = new City();
        cityFromServer.setCity(randomlyCity);

        return mapper.toDto(repository.save(cityFromServer));
    }

    @Override
    public Optional<CityResponseDto> getNextCity(String city) {
        return repository.findCitiesEndingWith(String.valueOf(city.charAt(0))).stream()
                .findFirst()
                .map((cityFromDb -> {
                    String newWord = generator.generateRandomWord(city.charAt(city.length() - 1));
                    return mapper.toDto(repository.save(new City(newWord)));
                }));
    }

    @Override
    public String finishGame() {
        repository.deleteAll();
        return "Спасибі за гру";
    }
}
