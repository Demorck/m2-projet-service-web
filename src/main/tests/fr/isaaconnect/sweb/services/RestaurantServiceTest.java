package fr.isaaconnect.sweb.services;

import fr.isaaconnect.sweb.entities.EvaluationEntity;
import fr.isaaconnect.sweb.entities.RestaurantEntity;
import fr.isaaconnect.sweb.exceptions.NotFoundException;
import fr.isaaconnect.sweb.repositories.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTest
{
    @InjectMocks
    private RestaurantService service;

    @Mock
    private RestaurantRepository repository;

    private UUID restaurantID;
    private RestaurantEntity restaurantEntity;

    @BeforeEach
    void setUp() {
        this.restaurantID = UUID.randomUUID();
        this.restaurantEntity = RestaurantEntity.builder()
            .id(this.restaurantID)
            .name("Restaurant 1")
            .address("Adresse 1")
            .evaluations(new ArrayList<>())
            .build();
    }

    
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

        when(this.repository.findById(id)).thenReturn(Optional.of(entity));

        RestaurantEntity returnedEntity = this.service.getRestaurantById(id);
        assertEquals(entity.getName(), returnedEntity.getName());
        assertEquals(entity.getAddress(), returnedEntity.getAddress());
    }

    @Test
    void get_restaurant_not_found_should_throw_an_error() {
        UUID id = UUID.randomUUID();
        when(this.repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> this.service.getRestaurantById(id));
    }

    @Test
    void modify_one_field_only_modifies_this_field() {
        UUID id = UUID.randomUUID();
        RestaurantEntity entity = RestaurantEntity.builder()
                .id(id)
                .name("Restaurant 1")
                .address("Address 1")
                .build();

        when(this.repository.findById(id)).thenReturn(Optional.of(entity));
        this.service.modifyRestaurant(id, "Restaurant 2", null);
        assertEquals("Restaurant 2", entity.getName());

        this.service.modifyRestaurant(id, null, "Adresse 2");
        assertEquals("Adresse 2", entity.getAddress());
    }

    @Test
    void modify_zero_field_should_not_modify_entity() {
        UUID id = UUID.randomUUID();
        RestaurantEntity entity = RestaurantEntity.builder()
                .id(id)
                .name("Restaurant 1")
                .address("Address 1")
                .build();

        when(this.repository.findById(id)).thenReturn(Optional.of(entity));
        this.service.modifyRestaurant(id, null, null);
        assertEquals("Restaurant 1", entity.getName());
        assertEquals("Address 1", entity.getAddress());
    }

    @Test
    void database_should_be_updated_once() {
        this.modify_one_field_only_modifies_this_field();

        verify(repository, times(2)).findById(any(UUID.class));
    }

    // Tests on noteMean(RestaurantEntity)
    @Test
    void note_mean_without_evaluation_should_return_minus_one() {
        Double mean = this.service.noteMean(this.restaurantEntity);
        assertEquals(-1.0, mean);
    }

    @Test
    void note_mean_with_evaluations_should_return_correct_average() {
        EvaluationEntity eval1 = EvaluationEntity.builder().note(3).build();
        EvaluationEntity eval2 = EvaluationEntity.builder().note(2).build();

        this.restaurantEntity.setEvaluations(List.of(eval1, eval2));
        Double mean = this.service.noteMean(this.restaurantEntity);

        assertEquals(2.5, mean);
    }
}
