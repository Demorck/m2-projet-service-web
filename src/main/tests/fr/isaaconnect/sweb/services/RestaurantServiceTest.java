package fr.isaaconnect.sweb.services;

import fr.isaaconnect.sweb.entities.EvaluationEntity;
import fr.isaaconnect.sweb.entities.RestaurantEntity;
import fr.isaaconnect.sweb.exceptions.NotFoundException;
import fr.isaaconnect.sweb.repositories.RestaurantRepository;
import fr.isaaconnect.sweb.services.s3.RestaurantS3Service;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTest
{
    @InjectMocks
    private RestaurantService service;

    @Mock
    private RestaurantRepository repository;

    @Mock
    private RestaurantS3Service s3;

    private UUID restaurantID;
    private RestaurantEntity restaurantEntity;

    private static final String RESTAURANT_NAME = "Restaurant 1";
    private static final String RESTAURANT_ADDRESS = "Adresse 1";
    private static final String S3_MOCK_URL = "https://s3.url/mock";

    @BeforeEach
    void setUp() {
        this.restaurantID = UUID.randomUUID();
        this.restaurantEntity = RestaurantEntity.builder()
            .id(this.restaurantID)
            .name(RESTAURANT_NAME)
            .address(RESTAURANT_ADDRESS)
            .evaluations(new ArrayList<>())
            .build();
    }

    // Test on getRestaurants()
    @Test
    void get_restaurants_should_return_list() {
        when(this.repository.findAll())
            .thenReturn(List.of(this.restaurantEntity));

        List<RestaurantEntity> result = this.service.getRestaurants();

        assertEquals(1, result.size());
        assertEquals(this.restaurantID, result.getFirst().getId());
    }

    // Test on getRestaurantById(UUID)
    @Test
    void get_restaurant_by_id_should_return_restaurant() {
        when(this.repository.findById(this.restaurantID))
            .thenReturn(Optional.of(this.restaurantEntity));

        RestaurantEntity result = this.service.getRestaurantById(this.restaurantID);

        assertNotNull(result);
        assertEquals(this.restaurantID, result.getId());
    }

    @Test
    void get_restaurant_by_id_not_found_should_throw_an_error() {
        when(this.repository.findById(this.restaurantID))
            .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> this.service.getRestaurantById(this.restaurantID));
    }

    // Test on addRestaurant(String, String)
    @Test
    void add_restaurant_should_save_and_return() {
        when(this.repository.save(any(RestaurantEntity.class)))
            .thenReturn(this.restaurantEntity);

        RestaurantEntity result = this.service.addRestaurant(RESTAURANT_NAME, RESTAURANT_ADDRESS);

        assertNotNull(result);
        assertEquals(this.restaurantID, result.getId());
        assertEquals(RESTAURANT_NAME, result.getName());

        // Checking if the DB has been called only once
        verify(this.repository, times(1))
            .save(any(RestaurantEntity.class));
    }

    // Test on modifyRestaurant(UUID, String, String)
    @Test
    void modify_restaurant_with_new_values_should_update() {
        when(this.repository.findById(this.restaurantID))
            .thenReturn(Optional.of(this.restaurantEntity));
        when(this.repository.save(any(RestaurantEntity.class)))
            .thenReturn(this.restaurantEntity);

        this.service.modifyRestaurant(this.restaurantID, "New name", null);

        assertEquals("New name", this.restaurantEntity.getName());
        assertEquals(RESTAURANT_ADDRESS, this.restaurantEntity.getAddress());

        // Checking if the DB has been called only once
        verify(this.repository, times(1))
            .save(any(RestaurantEntity.class));
    }

    // Test on getUploadImage(UUID)
    @Test
    void get_upload_image_when_image_id_null_should_generate_one_and_return_url() {
        when(this.repository.findById(this.restaurantID))
            .thenReturn(Optional.of(this.restaurantEntity));
        when(this.repository.save(any(RestaurantEntity.class)))
            .thenReturn(this.restaurantEntity);
        when(this.s3.getUploadUrl(eq(this.restaurantID), any(UUID.class)))
            .thenReturn(S3_MOCK_URL);

        String url = this.service.getUploadImage(this.restaurantID);

        assertEquals(S3_MOCK_URL, url);
    }

    @Test
    void get_upload_image_when_image_id_exists_should_return_url() {
        UUID imageId = UUID.randomUUID();
        this.restaurantEntity.setImageId(imageId);

        when(this.repository.findById(this.restaurantID))
            .thenReturn(Optional.of(this.restaurantEntity));
        when(this.s3.getUploadUrl(this.restaurantID, imageId))
            .thenReturn(S3_MOCK_URL);

        String url = this.service.getUploadImage(this.restaurantID);

        assertEquals(S3_MOCK_URL, url);
        verify(this.repository, times(0))
            .save(any(RestaurantEntity.class));
    }

    // Test on getImage(UUID)
    @Test
    void get_image_when_exists_should_return_download_url() {
        UUID image = UUID.randomUUID();
        this.restaurantEntity.setImageId(image);
        when(this.repository.findById(this.restaurantID))
            .thenReturn(Optional.of(this.restaurantEntity));
        when(this.s3.exists(this.restaurantID, image))
            .thenReturn(true);
        when(this.s3.getDownloadUrl(eq(this.restaurantID), any(UUID.class)))
            .thenReturn(S3_MOCK_URL);

        String url = this.service.getImage(this.restaurantID);

        assertEquals(S3_MOCK_URL, url);
    }

    @Test
    void get_image_when_image_id_is_null_should_throw_not_found() {
        when(this.repository.findById(this.restaurantID))
            .thenReturn(Optional.of(this.restaurantEntity));

        assertThrows(NotFoundException.class, () -> this.service.getImage(this.restaurantID));
    }

    @Test
    void get_image_when_not_exists_in_s3_should_throw_not_found() {
        UUID image = UUID.randomUUID();
        this.restaurantEntity.setImageId(image);
        when(this.repository.findById(this.restaurantID))
            .thenReturn(Optional.of(this.restaurantEntity));
        when(this.s3.exists(this.restaurantID, image))
            .thenReturn(false);

        assertThrows(NotFoundException.class, () -> this.service.getImage(this.restaurantID));
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
