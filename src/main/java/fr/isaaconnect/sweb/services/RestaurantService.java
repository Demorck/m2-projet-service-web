package fr.isaaconnect.sweb.services;

import fr.isaaconnect.sweb.entities.RestaurantEntity;
import fr.isaaconnect.sweb.repositories.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService
{
    private final RestaurantRepository repository;

    public List<RestaurantEntity> getRestaurants() {
        return this.repository.findAll();
    }
}
