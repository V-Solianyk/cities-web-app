package com.example.cities.service.impl;

import com.example.cities.dto.CityResponseDto;
import com.example.cities.entity.City;
import com.example.cities.exception.CustomException;
import com.example.cities.mapper.CityMapper;
import com.example.cities.repository.CityRepository;
import com.example.cities.service.CityService;
import java.util.NoSuchElementException;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {
    private static final long TOTAL_COUNT_CITY_IN_UKRAINE = 280;
    private final CityRepository repository;
    private final CityMapper mapper;
    private String lastCity;

    @Override
    public CityResponseDto startGame() {
        City randomCity = repository.findById(new Random().nextLong(TOTAL_COUNT_CITY_IN_UKRAINE))
                .orElseThrow(NoSuchElementException::new);
        lastCity = randomCity.getName();
        repository.delete(randomCity);
        return mapper.toDto(randomCity);
    }

    @Override
    public CityResponseDto getNextCity(String name) {
        if (name == null || name.isEmpty() || lastCity == null || lastCity.isEmpty()) {
            throw new IllegalArgumentException("Невірний стан гри: одне з слів є пустим");
        }
        char lastCityLastCharacter = Character.toLowerCase(lastCity.charAt(lastCity.length() - 1));
        char inputCityFirstCharacter = Character.toLowerCase(name.charAt(0));

        if (lastCityLastCharacter != inputCityFirstCharacter) {
            throw new CustomException("Гравець ввів слово не на ту літеру");
        }
        City cityFromUser = repository.findCityByNameIgnoreCase(name)
                .orElseThrow(() -> new NoSuchElementException("Такого міста не існує"
                        + " або було вже використане."));
        repository.delete(cityFromUser);
        lastCity = cityFromUser.getName();
        return mapper.toDto(getCity());
    }

    @Override
    @Transactional
    public String finishGame() {
        repository.resetAllIsDeletedFlags();
        return "Спасибі за гру";
    }

    private City getCity() {
        City cityFromServer = repository.findCitiesStartingWith(String
                        .valueOf(lastCity.charAt(lastCity.length() - 1))).stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Такого міста не існує"
                        + " або було вже використане."));
        repository.delete(cityFromServer);
        lastCity = cityFromServer.getName();
        return cityFromServer;
    }
}
