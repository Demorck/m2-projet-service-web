package fr.isaaconnect.sweb.services.s3;

import fr.isaaconnect.sweb.entities.RestaurantEntity;
import fr.isaaconnect.sweb.repositories.RestaurantRepository;
import fr.isaaconnect.sweb.repositories.S3Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RestaurantS3ServiceTest {

    @InjectMocks
    private RestaurantS3Service service;

    @Mock
    private S3Repository s3;

    private UUID restaurantId;
    private UUID imageId;
    private static final String S3_MOCK_URL = "https://s3.url/mock";

    @BeforeEach
    void setUp() {
        this.restaurantId = UUID.randomUUID();
        this.imageId = UUID.randomUUID();
    }

    // Test createFilePathFromUUID through getUploadUrl(UUID, UUID)
    @Test
    void get_url_should_build_correct_path_and_call_repository() {
        String path = "maximilien-7f0ed9/" + this.restaurantId + "/" + this.imageId + "-cover";

        when(this.s3.getUploadFileUrl("restaurant", path))
            .thenReturn(S3_MOCK_URL);

        String url = this.service.getUploadUrl(this.restaurantId, this.imageId);
        assertEquals(S3_MOCK_URL, url);
    }

    // Test createFilePathFromUUID through exists(UUID, UUID) (idk if it's useful though)
    @Test
    void exists_should_check_file_with_correct_path() {
        String path = "maximilien-7f0ed9/" + this.restaurantId + "/" + this.imageId + "-cover";

        when(this.s3.fileExists("restaurant", path))
            .thenReturn(true);

        boolean result = this.service.exists(this.restaurantId, this.imageId);

        assertTrue(result);
    }

}
