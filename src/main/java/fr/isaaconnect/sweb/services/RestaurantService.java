package fr.isaaconnect.sweb.services;

import fr.isaaconnect.sweb.entities.EvaluationEntity;
import fr.isaaconnect.sweb.entities.RestaurantEntity;
import fr.isaaconnect.sweb.exceptions.NotFoundException;
import fr.isaaconnect.sweb.repositories.RestaurantRepository;
import fr.isaaconnect.sweb.services.s3.RestaurantS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RestaurantService {
    private final RestaurantRepository repository;
    private final RestaurantS3Service s3;

    public List<RestaurantEntity> getRestaurants() {
        return this.repository.findAll();
    }

    public RestaurantEntity getRestaurantById(UUID id) {
        return this.repository.findById(id).orElseThrow(
            () -> new NotFoundException("Restaurant with id " + id + " not found")
        );
    }

    @Transactional
    public RestaurantEntity addRestaurant(String name, String address) {
        RestaurantEntity entity = RestaurantEntity.builder()
            .name(name)
            .address(address)
            .build();

        return this.repository.save(entity);
    }

    @Transactional
    public void modifyRestaurant(UUID id, String name, String address) {
        RestaurantEntity entity = this.getRestaurantById(id);

        if (name != null) entity.setName(name);
        if (address != null) entity.setAddress(address);

        this.repository.save(entity);
    }

    @Transactional
    public String getUploadImage(UUID restaurantId) {
        RestaurantEntity entity = this.repository.findById(restaurantId).orElseThrow(
            () -> new NotFoundException("Restaurant with id " + restaurantId + " not found")
        );

        UUID imageId = entity.getImageId();

        if (imageId == null) {
            imageId = UUID.randomUUID();
            entity.setImageId(imageId);
            this.repository.save(entity);
        }

        return this.s3.getUploadUrl(restaurantId, imageId);
    }


    public String getImage(UUID restaurantId) {
        UUID imageId = this.repository.findById(restaurantId).orElseThrow(
            () -> new NotFoundException("Restaurant with id " + restaurantId + " not found")
        ).getImageId();

        if (imageId == null || !this.s3.exists(restaurantId, imageId))
            throw new NotFoundException("This restaurant does not have an image");


        return this.s3.getDownloadUrl(restaurantId, imageId);
    }

    public Double noteMean(RestaurantEntity entity) {
        IntSummaryStatistics stats = entity.getEvaluations().stream()
            .mapToInt(EvaluationEntity::getNote)
            .summaryStatistics();

        return stats.getCount() == 0 ? -1 : stats.getAverage();
    }
}
