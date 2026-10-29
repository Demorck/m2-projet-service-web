package fr.isaaconnect.sweb.services;

import fr.isaaconnect.sweb.entities.RestaurantEntity;
import fr.isaaconnect.sweb.exceptions.NotFoundException;
import fr.isaaconnect.sweb.repositories.RestaurantRepository;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.HibernateException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTest
{
    @InjectMocks
    private RestaurantService service;

    @Mock
    private RestaurantRepository repository;
    
    @Test
    void get_restaurants_should_return_everything() {
        ArrayList<RestaurantEntity> restaurants = new ArrayList<>();
        for (int i = 0; i < 6; i++)
        {
            UUID id = UUID.randomUUID();
            RestaurantEntity entity = RestaurantEntity.builder()
                    .id(id)
                    .name("Restaurant " + i)
                    .address("Address " + i)
                    .build();

            restaurants.add(entity);
        }

        when(this.repository.findAll())
            .thenReturn(restaurants);

        List<RestaurantEntity> returnRestaurants = this.service.getRestaurants();
        assertEquals(returnRestaurants.size(), restaurants.size());
    }

    @Test
    void get_restaurant_found_should_return_restaurant() {
        UUID id = UUID.randomUUID();
        RestaurantEntity entity = RestaurantEntity.builder()
                .id(id)
                .name("Restaurant 1")
                .address("Address 1")
                .build();

        when(this.repository.findById(id)).thenReturn(Optional.ofNullable(entity));

        RestaurantEntity returnedEntity = this.service.getRestaurantById(id);
        assertEquals(entity.getName(), returnedEntity.getName());
        assertEquals(entity.getAddress(), returnedEntity.getAddress());
    }

    @Test
    void get_restaurant_not_found_should_throw_an_error() {
        UUID id = UUID.randomUUID();
        when(this.repository.findById(id)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> this.service.getRestaurantById(id));
    }
}
