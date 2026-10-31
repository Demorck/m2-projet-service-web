package fr.isaaconnect.sweb.services;

import fr.isaaconnect.sweb.entities.EvaluationEntity;
import fr.isaaconnect.sweb.entities.RestaurantEntity;
import fr.isaaconnect.sweb.exceptions.NotFoundException;
import fr.isaaconnect.sweb.repositories.EvaluationRepository;
import fr.isaaconnect.sweb.repositories.RestaurantRepository;
import fr.isaaconnect.sweb.services.s3.EvaluationS3Service;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EvaluationServiceTest {
    @InjectMocks
    private EvaluationService service;

    @Mock
    private EvaluationRepository evaluationRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private EvaluationS3Service s3;

    private UUID restaurantID;
    private RestaurantEntity restaurantEntity;
    private static final String RESTAURANT_NAME = "Restaurant 1";
    private static final String RESTAURANT_ADDRESS = "Adresse 1";

    private UUID evaluationID;
    private EvaluationEntity evaluationEntity;
    private static final int EVALUATION_NOTE = 2;
    private static final String EVALUATION_MESSAGE = "Super évaluation de fou";

    private UUID authorId;
    private String AUTHOR_NAME = "Demorck";

    private static final String S3_MOCK_URL = "https://s3.url/mock";

    @BeforeEach
    void setUp() {
        this.restaurantID = UUID.randomUUID();
        this.evaluationID = UUID.randomUUID();
        this.authorId     = UUID.randomUUID();

        this.evaluationEntity = EvaluationEntity.builder()
            .id(this.evaluationID)
            .note(EVALUATION_NOTE)
            .text(EVALUATION_MESSAGE)
            .imageId(new ArrayList<>())
            .author(AUTHOR_NAME)
            .authorId(this.authorId)
            .build();

        this.restaurantEntity = RestaurantEntity.builder()
            .id(this.restaurantID)
            .name(RESTAURANT_NAME)
            .address(RESTAURANT_ADDRESS)
            .evaluations(new ArrayList<>(List.of(this.evaluationEntity)))
            .build();

        this.evaluationEntity.setRestaurant(this.restaurantEntity);
    }

    // Test on getEvaluationByAuthor(UUID)
    @Test
    void get_evaluation_by_author_should_return_a_list() {
        when(this.evaluationRepository.findByAuthorId(this.authorId))
            .thenReturn(List.of(this.evaluationEntity));

        List<EvaluationEntity> result = this.service.getEvaluationByAuthor(this.authorId);

        assertEquals(1, result.size());
        assertEquals(this.authorId, result.getFirst().getAuthorId());
        assertEquals(EVALUATION_MESSAGE, result.getFirst().getText());
    }

    @Test
    void get_evaluation_by_author_with_no_evaluation_should_return_an_empty_list() {
        when(this.evaluationRepository.findByAuthorId(this.authorId))
            .thenReturn(List.of());

        List<EvaluationEntity> result = this.service.getEvaluationByAuthor(this.authorId);

        assertEquals(0, result.size());
    }

    // Test on addEvaluation(UUID, String, String, int, UUID)
    @Test
    void add_evaluation_should_save_and_return() {
        when(this.restaurantRepository.findById(this.restaurantID))
            .thenReturn(Optional.of(this.restaurantEntity));
        when(this.evaluationRepository.save(any(EvaluationEntity.class)))
            .thenReturn(this.evaluationEntity);

        EvaluationEntity result = this.service.addEvaluation(
            this.restaurantID,
            AUTHOR_NAME,
            EVALUATION_MESSAGE,
            EVALUATION_NOTE,
            this.authorId);

        assertNotNull(result);
        assertEquals(this.evaluationID, result.getId());
        assertEquals(EVALUATION_MESSAGE, result.getText());

        verify(this.evaluationRepository, times(1))
            .save(any(EvaluationEntity.class));
    }



    // Test on getUploadImage(UUID)
    @Test
    void get_upload_image_should_return_url() {
        when(this.evaluationRepository.findById(this.evaluationID))
            .thenReturn(Optional.of(this.evaluationEntity));
        when(this.evaluationRepository.save(any(EvaluationEntity.class)))
            .thenReturn(this.evaluationEntity);
        when(this.s3.getUploadUrl(eq(this.restaurantID), eq(this.evaluationID), any(UUID.class)))
            .thenReturn(S3_MOCK_URL);

        String url = this.service.getUploadImage(this.evaluationID);

        assertEquals(S3_MOCK_URL, url);
    }

    // Test on getImages(UUID)
    @Test
    void get_images_should_return_list() {
        UUID image = UUID.randomUUID();
        this.evaluationEntity.getImageId().add(image);

        when(this.evaluationRepository.findById(this.evaluationID))
            .thenReturn(Optional.of(this.evaluationEntity));
        when(this.s3.exists(this.restaurantID, this.evaluationID, image))
            .thenReturn(true);
        when(this.s3.getDownloadUrl(this.restaurantID, this.evaluationID, image))
            .thenReturn(S3_MOCK_URL);

        List<String> url = this.service.getImages(this.evaluationID);

        assertEquals(1, url.size());
        assertEquals(S3_MOCK_URL, url.getFirst());
    }

    @Test
    void get_images_should_that_does_not_exist_on_s3_should_update_the_database() {
        UUID image = UUID.randomUUID();
        this.evaluationEntity.getImageId().add(image);

        when(this.evaluationRepository.findById(this.evaluationID))
            .thenReturn(Optional.of(this.evaluationEntity));
        when(this.s3.exists(this.restaurantID, this.evaluationID, image))
            .thenReturn(false);

        List<String> url = this.service.getImages(this.evaluationID);

        assertEquals(0, url.size());
        verify(this.evaluationRepository, times(1))
            .save(any(EvaluationEntity.class));
    }

}
