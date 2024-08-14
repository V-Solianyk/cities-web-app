package com.example.cities.repository;

import com.example.cities.entity.City;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    @Query("SELECT c FROM City c WHERE LOWER(c.city) LIKE LOWER(CONCAT('%', :endingCharacter))")
    List<City> findCitiesEndingWith(@Param("endingCharacter") String endingCharacter);

}
