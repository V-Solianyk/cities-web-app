package com.example.cities.service.impl;

import com.example.cities.dto.CityResponseDto;
import com.example.cities.entity.City;
import com.example.cities.exception.CustomException;
import com.example.cities.mapper.CityMapper;
import com.example.cities.repository.CityRepository;
import com.example.cities.service.CityService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.NoSuchElementException;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {
    private static final String LAST_CITY = "lastCity";
    private static final long TOTAL_COUNT_CITY_IN_UKRAINE_AVAILABLE_IN_APP = 350;
    private final CityRepository repository;
    private final CityMapper mapper;
    private final HttpServletRequest servletRequest;

    @Override
    public CityResponseDto startGame() {
        City randomCity = repository.findById(new Random()
                        .nextLong(TOTAL_COUNT_CITY_IN_UKRAINE_AVAILABLE_IN_APP))
                .orElseThrow(NoSuchElementException::new);
        String cityName = randomCity.getName();
        checkInvalidCityEnding(cityName, cityName.charAt(cityName.length() - 1));
        setLastCity(randomCity.getName());
        repository.delete(randomCity);

        return mapper.toDto(randomCity);
    }

    @Override
    public CityResponseDto getNextCity(String name) {
        validateCityInput(name);
        City cityFromUser = repository.findCityByNameIgnoreCase(name)
                .orElseThrow(() -> new NoSuchElementException("Такого міста не існує"
                        + " або було вже використане:" + name));
        repository.delete(cityFromUser);

        return mapper.toDto(getCityFromServer(name));
    }

    @Override
    @Transactional
    public String finishGame() {
        repository.resetAllIsDeletedFlags();
        HttpSession session = servletRequest.getSession(false);
        if (session != null) {
            session.removeAttribute(LAST_CITY);
        }

        return "Спасибі за гру";
    }

    private void validateCityInput(String name) {
        String lastCity = getLastCity();
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Назва міста не може бути пустою: " + name);
        }
        if (lastCity == null || lastCity.isEmpty()) {
            throw new IllegalArgumentException("Останнє місто не може бути пустим: " + lastCity);
        }
        char lastCityLastCharacter = Character.toLowerCase(lastCity
                .charAt(lastCity.length() - 1));
        char nameLastCharacter = Character.toLowerCase(name.charAt(name.length() - 1));
        checkInvalidCityEnding(name, nameLastCharacter);

        char inputCityFirstCharacter = Character.toLowerCase(name.charAt(0));
        if (lastCityLastCharacter != inputCityFirstCharacter) {
            throw new CustomException("Ви ввели слово не на ту літеру. Попереднє місто: "
                    + lastCity + " Ви ввели: " + name);
        }
    }

    private void checkInvalidCityEnding(String cityName, char lastCharacterCityName) {
        if (lastCharacterCityName == 'и' || lastCharacterCityName == 'ь') {
            throw new CustomException("Назва введеного міста " + cityName + " закінчується на "
                    + lastCharacterCityName + ", і жодне місто не починається з цього символу. "
                    + "Почніть гру заново.");
        }
    }

    private City getCityFromServer(String name) {
        City cityFromServer = repository.findCitiesStartingWith(String
                        .valueOf(name.charAt(name.length() - 1))).stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Ви виграли! Сервер не може знайти"
                        + " відповідне місто в базі."));
        repository.delete(cityFromServer);
        setLastCity(cityFromServer.getName());

        return cityFromServer;
    }

    private String getLastCity() {
        return ((String) servletRequest.getSession().getAttribute(LAST_CITY));
    }

    private void setLastCity(String name) {
        servletRequest.getSession().setAttribute(LAST_CITY, name);
    }
}
