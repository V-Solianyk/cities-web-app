package com.example.cities.mapper;

import com.example.cities.config.MapperConfig;
import com.example.cities.dto.CityResponseDto;
import com.example.cities.entity.City;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface CityMapper {
    CityResponseDto toDto(City city);
}
