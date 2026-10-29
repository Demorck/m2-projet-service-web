package fr.isaaconnect.sweb.services;

import fr.isaaconnect.sweb.dto.restaurants.RestaurantDTO;
import fr.isaaconnect.sweb.entities.RestaurantEntity;
import fr.isaaconnect.sweb.exceptions.NotFoundException;
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

    public RestaurantEntity getRestaurantById(UUID id)
    {
        return this.repository.findById(id).orElseThrow(
                () -> new NotFoundException("Restaurant with id " + id + " not found")
        );
    }

    @Transactional
    public RestaurantEntity addRestaurant(String name, String address) {
        RestaurantEntity entity = RestaurantEntity.builder()
                .name(name)
                .address(address)
                .build();

        return this.repository.save(entity);
    }

    @Transactional
    public void modifyRestaurant(UUID id, String name, String address)
    {
        RestaurantEntity entity = this.getRestaurantById(id);

        if (name != null) entity.setName(name);
        if (address != null) entity.setAddress(address);

        this.repository.save(entity);
    }
}
