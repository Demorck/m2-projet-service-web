package fr.isaaconnect.sweb.repositories;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.auth.aws.signer.RegionSet;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URI;
import java.time.Duration;


@Repository
public class S3Repository {
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public S3Repository(
        @Value("${s3.access-key}") String accessKey,
        @Value("${s3.secret-key}") String secretKey
    ) {
        this.s3Client = S3Client.builder()
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)))
            .endpointOverride(URI.create("https://s3.bober.ovh"))
            .region(Region.US_EAST_1)
            .sigv4aSigningRegionSet(RegionSet.GLOBAL)
            .forcePathStyle(true)
            .build();

        S3Configuration configuration = S3Configuration.builder()
            .pathStyleAccessEnabled(true)
            .build();

        this.s3Presigner = S3Presigner.builder()
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.builder()
                    .accessKeyId(accessKey)
                    .secretAccessKey(secretKey)
                    .build())
            )
            .endpointOverride(URI.create("https://s3.bober.ovh"))
            .serviceConfiguration(configuration)
            .region(Region.US_EAST_1)
            .build();
    }

    // PRE-SIGN LINKS

    /// Generates a presigned link so the client can upload directly on the S3
    /// @param bucketName The name of the bucket ({@link RESTAURANT_BUCKET} or {@link EVALUATION_BUCKET})
    /// @param filePath the path on the bucket
    /// @return the url
    public String getUploadFileUrl(String bucketName, String filePath) {
        PutObjectRequest request = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(filePath)
            .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(10))
            .putObjectRequest(request)
            .build();

        return this.s3Presigner.presignPutObject(presignRequest).url().toExternalForm();
    }

    /// Generates a presigned link so the client can download directly from the S3
    /// @param bucketName The name of the bucket ({@link RESTAURANT_BUCKET} or {@link EVALUATION_BUCKET})
    /// @param filePath the path on the bucket
    /// @return the url
    public String getFileUrl(String bucketName, String filePath) {
        GetObjectRequest request = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(filePath)
            .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(69))
            .getObjectRequest(request)
            .build();

        return this.s3Presigner.presignGetObject(presignRequest).url().toExternalForm();
    }

    /// Test if the file exists on the specified bucket
    /// @param bucketName The name of the bucket ({@link RESTAURANT_BUCKET} or {@link EVALUATION_BUCKET})
    /// @param filePath the path on the bucket
    /// @return True if the file exists
    public boolean fileExists(String bucketName, String filePath) {
        try {
            HeadObjectRequest request = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(filePath)
                .build();

            this.s3Client.headObject(request);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void bucket() {
        this.s3Client.listBuckets();
    }
}
