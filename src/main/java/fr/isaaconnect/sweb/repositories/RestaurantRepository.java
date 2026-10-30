package fr.isaaconnect.sweb.repositories;

import fr.isaaconnect.sweb.entities.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RestaurantRepository extends JpaRepository<RestaurantEntity, UUID> {
}
