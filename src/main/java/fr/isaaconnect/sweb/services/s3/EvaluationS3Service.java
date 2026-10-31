package fr.isaaconnect.sweb.services.s3;

import fr.isaaconnect.sweb.repositories.S3Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EvaluationS3Service {
    private final S3Repository s3Repository;
    private static final String BUCKET = "evaluation";

    public String getUploadUrl(UUID restaurantId, UUID evaluationId, UUID fileId) {
        String filePath = createFilePathFromUUID(restaurantId, evaluationId, fileId);
        return this.s3Repository.getUploadFileUrl(BUCKET, filePath);
    }

    public String getDownloadUrl(UUID restaurantId, UUID evaluationId, UUID fileId) {
        String filePath = createFilePathFromUUID(restaurantId, evaluationId, fileId);
        return this.s3Repository.getFileUrl(BUCKET, filePath);
    }

    public boolean exists(UUID restaurantId, UUID evaluationId, UUID fileId) {
        String filePath = createFilePathFromUUID(restaurantId, evaluationId, fileId);
        return this.s3Repository.fileExists(BUCKET, filePath);
    }

    private String createFilePathFromUUID(UUID restaurantId, UUID evaluationId, UUID uuid) {
        return "maximilien-7f0ed9/" + restaurantId + "/" + evaluationId + "/" + uuid;
    }
}
