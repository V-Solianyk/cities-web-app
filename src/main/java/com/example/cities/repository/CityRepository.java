package com.example.cities.repository;

import com.example.cities.entity.City;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    Optional<City> findCityByNameIgnoreCase(String name);

    @Modifying
    @Query("UPDATE City c SET c.isDeleted = false")
    void resetAllIsDeletedFlags();

    @Query("SELECT c FROM City c WHERE LOWER(c.name) LIKE LOWER(CONCAT(:startingCharacter, '%'))")
    List<City> findCitiesStartingWith(@Param("startingCharacter") String startingCharacter);

}
