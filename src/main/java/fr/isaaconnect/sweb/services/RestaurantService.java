package fr.isaaconnect.sweb.services;

import fr.isaaconnect.sweb.entities.RestaurantEntity;
import fr.isaaconnect.sweb.repositories.RestaurantRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RestaurantService
{
    private final RestaurantRepository repository;

    public List<RestaurantEntity> getRestaurants() {
        return this.repository.findAll();
    }

    @Transactional
    public void addRestaurant(String name, String address) {
        RestaurantEntity entity = RestaurantEntity.builder()
                .name(name)
                .address(address)
                .build();

        this.repository.save(entity);
    }
}
