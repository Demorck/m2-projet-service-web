package fr.isaaconnect.sweb.services;

import fr.isaaconnect.sweb.entities.EvaluationEntity;
import fr.isaaconnect.sweb.entities.RestaurantEntity;
import fr.isaaconnect.sweb.exceptions.NotFoundException;
import fr.isaaconnect.sweb.repositories.EvaluationRepository;
import fr.isaaconnect.sweb.repositories.RestaurantRepository;
import fr.isaaconnect.sweb.services.s3.EvaluationS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EvaluationService {
    private final EvaluationRepository evaluationRepository;
    private final RestaurantRepository restaurantRepository;
    private final EvaluationS3Service s3;


    @Transactional
    public List<EvaluationEntity> getEvaluationByAuthor(UUID authorId) {
        return this.evaluationRepository.findByAuthorId(authorId);
    }


    @Transactional
    public EvaluationEntity addEvaluation(UUID restaurantId, String author, String text, int note, UUID authorId) {
        RestaurantEntity restaurant = this.restaurantRepository.findById(restaurantId).orElseThrow(() ->
            new NotFoundException("Restaurant with id " + restaurantId + " not found")
        );

        EvaluationEntity evaluation = EvaluationEntity.builder()
            .author(author)
            .text(text)
            .note(note)
            .restaurant(restaurant)
            .authorId(authorId)
            .imageId(new ArrayList<>())
            .build();

        restaurant.getEvaluations().add(evaluation);

        return this.evaluationRepository.save(evaluation);
    }

    @Transactional
    public void removeEvaluation(UUID restaurantId, UUID evaluationId) {
        RestaurantEntity restaurant = this.restaurantRepository.findById(restaurantId).orElseThrow(() ->
            new NotFoundException("Restaurant with id " + restaurantId + " not found")
        );

        EvaluationEntity entity = restaurant.getEvaluations().stream()
            .filter(evaluation -> evaluation.getId().equals(evaluationId))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("Evaluation with id " + evaluationId + " not found"));

        this.evaluationRepository.delete(entity);
    }

    @Transactional
    public String getUploadImage(UUID evaluationId) {
        EvaluationEntity entity = this.evaluationRepository.findById(evaluationId).orElseThrow(
            () -> new NotFoundException("Evaluation with id " + evaluationId + " not found")
        );

        UUID restaurantId = entity.getRestaurant().getId();
        UUID imageId = UUID.randomUUID();

        entity.getImageId().add(imageId);

        this.evaluationRepository.save(entity);
        return this.s3.getUploadUrl(restaurantId, evaluationId, imageId);
    }

    @Transactional
    public List<String> getImages(UUID evaluationId) {
        EvaluationEntity entity = this.evaluationRepository.findById(evaluationId).orElseThrow(
            () -> new NotFoundException("Evaluation with id " + evaluationId + " not found")
        );

        UUID restaurantId = entity.getRestaurant().getId();
        List<UUID> imagesId = entity.getImageId();

        List<UUID> validImageId = imagesId.stream()
            .filter(id -> this.s3.exists(restaurantId, evaluationId, id))
            .toList();

        if (validImageId.size() < imagesId.size()) {
            entity.setImageId(new ArrayList<>(validImageId));
            this.evaluationRepository.save(entity);
        }

        return validImageId.stream()
            .map(uuid -> this.s3.getDownloadUrl(restaurantId, evaluationId, uuid))
            .toList();
    }
}
