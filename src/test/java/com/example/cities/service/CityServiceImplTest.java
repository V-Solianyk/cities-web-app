package com.example.cities.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.cities.dto.CityResponseDto;
import com.example.cities.entity.City;
import com.example.cities.exception.CustomException;
import com.example.cities.mapper.CityMapper;
import com.example.cities.repository.CityRepository;
import com.example.cities.service.impl.CityServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class CityServiceImplTest {
    private static final String CITY_NAME = "Рівне";
    private static final String LAST_CITY = "lastCity";
    private City testCity;
    private CityServiceImpl cityService;
    private CityRepository cityRepository;
    private CityMapper cityMapper;
    private HttpServletRequest servletRequest;
    private HttpSession session;

    @BeforeEach
    void setUp() {
        cityRepository = Mockito.mock(CityRepository.class);
        cityMapper = Mockito.mock(CityMapper.class);
        servletRequest = Mockito.mock(HttpServletRequest.class);
        session = Mockito.mock(HttpSession.class);
        cityService = new CityServiceImpl(cityRepository, cityMapper, servletRequest);

        testCity = new City();
        testCity.setName(CITY_NAME);

        when(servletRequest.getSession()).thenReturn(session);
    }

    @Test
    void startGame_ok() {
        when(cityRepository.findById(anyLong())).thenReturn(Optional.of(testCity));
        when(cityMapper.toDto(testCity)).thenReturn(new CityResponseDto(CITY_NAME));

        CityResponseDto response = cityService.startGame();

        assertEquals(CITY_NAME, response.getName());
        verify(cityRepository).delete(testCity);
        verify(session).setAttribute(LAST_CITY, testCity.getName());
    }

    @Test
    void startGame_cityNotFound_notOk() {
        when(cityRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> cityService.startGame());
    }

    @Test
    void startGame_invalidCityEnding_firstVersionOfSymbol_notOk() {
        City city = new City("Нікополь");
        String name = city.getName();
        char lastCharacter = name.charAt(name.length() - 1);

        when(cityRepository.findById(anyLong())).thenReturn(Optional.of(city));

        CustomException exception = assertThrows(CustomException.class,
                () -> cityService.startGame());
        assertEquals("Назва введеного міста " + name + " закінчується на " + lastCharacter
                        + ", і жодне місто не починається з цього символу. Почніть гру заново.",
                exception.getMessage());
    }

    @Test
    void startGame_invalidCityEnding_secondVersionOfSymbol_notOk() {
        City city = new City("Лубни");
        String name = city.getName();
        char lastCharacter = city.getName().charAt(city.getName().length() - 1);

        when(cityRepository.findById(anyLong())).thenReturn(Optional.of(city));

        CustomException exception = assertThrows(CustomException.class,
                () -> cityService.startGame());
        assertEquals("Назва введеного міста " + name + " закінчується на " + lastCharacter
                        + ", і жодне місто не починається з цього символу. Почніть гру заново.",
                exception.getMessage());
    }

    @Test
    void getNextCity_ok() {
        City lastCity = new City("Енергодар");
        City cityFromUser = new City("Рівне");
        City cityFromServer = new City("Нікополь");
        when(session.getAttribute(LAST_CITY)).thenReturn(lastCity.getName());
        when(cityRepository.findCityByNameIgnoreCase(cityFromUser
                .getName())).thenReturn(Optional.of(cityFromUser));
        when(cityRepository.findCitiesStartingWith(String
                .valueOf(cityFromUser.getName().charAt(cityFromUser.getName().length() - 1))))
                .thenReturn(List.of(cityFromServer));
        when(cityMapper.toDto(cityFromServer))
                .thenReturn(new CityResponseDto(cityFromServer.getName()));

        CityResponseDto response = cityService.getNextCity(cityFromUser.getName());

        assertEquals(cityFromServer.getName(), response.getName());
        verify(cityRepository).delete(cityFromUser);
        verify(cityRepository).delete(cityFromServer);
        verify(session).setAttribute(LAST_CITY, cityFromServer.getName());
    }

    @Test
    void getNextCity_cityNotFound_notOk() {
        City city = new City("енеІснуючеМісто");
        when(session.getAttribute(LAST_CITY)).thenReturn(CITY_NAME);
        when(cityRepository.findCityByNameIgnoreCase(city.getName()))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> cityService.getNextCity(city.getName()));
    }

    @Test
    void finishGame_ok() {
        when(servletRequest.getSession(false)).thenReturn(session);

        String response = cityService.finishGame();

        verify(cityRepository).resetAllIsDeletedFlags();
        verify(session).removeAttribute(LAST_CITY);
        assertEquals("Спасибі за гру", response);
    }

    @Test
    void validateCityInput_wrongLetter_notOk() {
        String cityName = "Київ";
        when(getLastCity()).thenReturn("Львів");

        CustomException exception = assertThrows(CustomException.class,
                () -> cityService.getNextCity(cityName));

        assertEquals("Ви ввели слово не на ту літеру. Попереднє місто: "
                + getLastCity() + " Ви ввели: " + cityName, exception.getMessage());
    }

    @Test
    void validateCityInput_emptyName_notOk() {
        String cityName = "";
        when(getLastCity()).thenReturn("Львів");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cityService.getNextCity(cityName));

        assertEquals("Назва міста не може бути пустою: " + cityName, exception.getMessage());
    }

    @Test
    void validateCityInput_nullName_notOk() {
        String cityName = null;
        when(getLastCity()).thenReturn("Львів");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cityService.getNextCity(null));

        assertEquals("Назва міста не може бути пустою: " + cityName, exception.getMessage());
    }

    @Test
    void validateCityInput_emptyLastCity_notOk() {
        String lastCity = "";
        when(getLastCity()).thenReturn(lastCity);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cityService.getNextCity("Kyiv"));
        assertEquals("Останнє місто не може бути пустим: " + lastCity, exception.getMessage());
    }

    @Test
    void validateCityInput_lastCityNull_notOk() {
        when(getLastCity()).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cityService.getNextCity("Київ"));
        assertEquals("Останнє місто не може бути пустим: " + null, exception.getMessage());
    }

    @Test
    void validateCityInput_invalidCityEnding_firstVersionOfSymbol_notOk() {
        City cityFromUser = new City("Печенеги");
        String name = cityFromUser.getName();
        char lastCharacter = name.charAt(name.length() - 1);
        when(getLastCity()).thenReturn("Ліп");

        CustomException exception = assertThrows(CustomException.class,
                () -> cityService.getNextCity(name));

        assertEquals("Назва введеного міста " + name + " закінчується на " + lastCharacter
                        + ", і жодне місто не починається з цього символу. Почніть гру заново.",
                exception.getMessage());
    }

    @Test
    void validateCityInput_invalidCityEnding_secondVersionOfSymbol_notOk() {
        City cityFromUser = new City("Печень");
        String name = cityFromUser.getName();
        char lastCharacter = name.charAt(name.length() - 1);
        when(getLastCity()).thenReturn("Ліп");

        CustomException exception = assertThrows(CustomException.class,
                () -> cityService.getNextCity(name));

        assertEquals("Назва введеного міста " + name + " закінчується на " + lastCharacter
                        + ", і жодне місто не починається з цього символу. Почніть гру заново.",
                exception.getMessage());
    }

    private String getLastCity() {
        return ((String) servletRequest.getSession().getAttribute(LAST_CITY));
    }
}
