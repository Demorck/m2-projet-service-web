package fr.isaaconnect.sweb.services.s3;

import fr.isaaconnect.sweb.repositories.S3Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EvaluationS3ServiceTest {

    @InjectMocks
    private EvaluationS3Service service;

    @Mock
    private S3Repository s3;

    private UUID restaurantId;
    private UUID evaluationId;
    private UUID imageId;
    private static final String S3_MOCK_URL = "https://s3.url/mock";

    @BeforeEach
    void setUp() {
        this.restaurantId = UUID.randomUUID();
        this.evaluationId = UUID.randomUUID();
        this.imageId = UUID.randomUUID();
    }

    // Test createFilePathFromUUID through getUploadUrl(UUID, UUID, UUID)
    @Test
    void get_url_should_build_correct_path_and_call_repository() {
        String path = "maximilien-7f0ed9/" + this.restaurantId + "/" + this.evaluationId + "/" + this.imageId;

        when(this.s3.getUploadFileUrl("evaluation", path))
            .thenReturn(S3_MOCK_URL);

        String url = this.service.getUploadUrl(this.restaurantId, this.evaluationId, this.imageId);
        assertEquals(S3_MOCK_URL, url);
    }

    // Test createFilePathFromUUID through exists(UUID, UUID, UUID) (idk if it's useful though)
    @Test
    void exists_should_check_file_with_correct_path() {
        String path = "maximilien-7f0ed9/" + this.restaurantId + "/" + this.evaluationId + "/" + this.imageId;

        when(this.s3.fileExists("evaluation", path))
            .thenReturn(true);

        boolean result = this.service.exists(this.restaurantId, this.evaluationId, this.imageId);

        assertTrue(result);
    }

}
