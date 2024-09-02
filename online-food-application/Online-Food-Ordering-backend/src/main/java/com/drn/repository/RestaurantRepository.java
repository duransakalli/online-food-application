package com.drn.repository;

import com.drn.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    @Query("SELECT r FROM Restaurant r WHERE r.name LIKE %:query% OR r.description LIKE %:query%")
    List<Restaurant> findBySearchQuery(@Param("query") String query);

    Restaurant findByOwnerId(Long userId);

}
