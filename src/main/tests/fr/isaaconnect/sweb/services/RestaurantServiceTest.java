package fr.isaaconnect.sweb.services;

import fr.isaaconnect.sweb.entities.RestaurantEntity;
import fr.isaaconnect.sweb.repositories.RestaurantRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}
