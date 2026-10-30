package fr.isaaconnect.sweb.services.s3;

import fr.isaaconnect.sweb.repositories.S3Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RestaurantS3Service {
    private final S3Repository s3Repository;
    private static final String BUCKET = "restaurant";

    public String getUploadUrl(UUID restaurantId, UUID fileId) {
        String filePath = createFilePathFromUUID(restaurantId, fileId);
        return this.s3Repository.getUploadFileUrl(BUCKET, filePath);
    }

    public String getDownloadUrl(UUID restaurantId, UUID fileId) {
        String filePath = createFilePathFromUUID(restaurantId, fileId);
        return this.s3Repository.getFileUrl(BUCKET, filePath);
    }

    public boolean exists(UUID restaurantId, UUID fileId) {
        String filePath = createFilePathFromUUID(restaurantId, fileId);
        return this.s3Repository.fileExists(BUCKET, filePath);
    }

    private String createFilePathFromUUID(UUID restaurantId, UUID uuid) {
        return "maximilien-7f0ed9/" + restaurantId + "/" + uuid + "-cover";
    }
}
