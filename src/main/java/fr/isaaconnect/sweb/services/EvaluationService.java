package fr.isaaconnect.sweb.services;

import fr.isaaconnect.sweb.entities.EvaluationEntity;
import fr.isaaconnect.sweb.entities.RestaurantEntity;
import fr.isaaconnect.sweb.exceptions.NotFoundException;
import fr.isaaconnect.sweb.repositories.EvaluationRepository;
import fr.isaaconnect.sweb.repositories.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EvaluationService {
    private final EvaluationRepository evaluationRepository;
    private final RestaurantRepository restaurantRepository;



    @Transactional
    public List<EvaluationEntity> getEvaluationByAuthor(UUID authorId) {
        return this.evaluationRepository.findByAuthorId(authorId);
    }


    @Transactional
    public EvaluationEntity addEvaluation(UUID id, String author, String text, int note, UUID authorId) {
        RestaurantEntity restaurant = this.restaurantRepository.findById(id).orElseThrow(() ->
            new NotFoundException("Restaurant with id " + id + " not found")
        );

        EvaluationEntity evaluation = EvaluationEntity.builder()
            .author(author)
            .text(text)
            .note(note)
            .restaurant(restaurant)
            .authorId(authorId)
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
}
