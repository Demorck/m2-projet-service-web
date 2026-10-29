package fr.isaaconnect.sweb.services;

import fr.isaaconnect.sweb.entities.EvaluationEntity;
import fr.isaaconnect.sweb.entities.RestaurantEntity;
import fr.isaaconnect.sweb.exceptions.NotFoundException;
import fr.isaaconnect.sweb.repositories.EvaluationRepository;
import fr.isaaconnect.sweb.repositories.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EvaluationService
{
    private final EvaluationRepository evaluationRepository;
    private final RestaurantRepository restaurantRepository;

    @Transactional
    public void addEvaluation(UUID id, String author, String text, int note) {
        RestaurantEntity restaurant = this.restaurantRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Restaurant with id " + id + " not found")
        );

        EvaluationEntity evaluation = EvaluationEntity.builder()
                .author(author)
                .text(text)
                .note(note)
                .restaurant(restaurant)
                .build();

        restaurant.getEvaluations().add(evaluation);
        this.evaluationRepository.save(evaluation);
        this.restaurantRepository.save(restaurant);
    }
}
