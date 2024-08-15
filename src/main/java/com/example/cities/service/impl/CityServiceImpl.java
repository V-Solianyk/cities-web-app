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
    private static final long TOTAL_COUNT_CITY_IN_UKRAINE = 270;
    private final CityRepository repository;
    private final CityMapper mapper;
    private final HttpServletRequest servletRequest;

    @Override
    public CityResponseDto startGame() {
        City randomCity = repository.findById(new Random().nextLong(TOTAL_COUNT_CITY_IN_UKRAINE))
                .orElseThrow(NoSuchElementException::new);
        setLastCity(randomCity.getName());
        repository.delete(randomCity);

        return mapper.toDto(randomCity);
    }

    @Override
    public CityResponseDto getNextCity(String name) {
        validateCityInput(name);
        City cityFromUser = repository.findCityByNameIgnoreCase(name)
                .orElseThrow(() -> new NoSuchElementException("Такого міста не існує"
                        + " або було вже використане."));
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
        if (name == null || name.isEmpty() || getLastCity() == null || getLastCity().isEmpty()) {
            throw new IllegalArgumentException("Невірний стан гри: одне з слів є пустим");
        }
        char lastCityLastCharacter = Character.toLowerCase(getLastCity()
                .charAt(getLastCity().length() - 1));
        char inputCityFirstCharacter = Character.toLowerCase(name.charAt(0));

        if (lastCityLastCharacter != inputCityFirstCharacter) {
            throw new CustomException("Гравець ввів слово не на ту літеру");
        }
    }

    private City getCityFromServer(String name) {
        City cityFromServer = repository.findCitiesStartingWith(String
                        .valueOf(name.charAt(name.length() - 1))).stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Такого міста не існує"
                        + " або було вже використане."));
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
