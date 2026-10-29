package fr.isaaconnect.sweb.services;

import fr.isaaconnect.sweb.repositories.EvaluationRepository;
import fr.isaaconnect.sweb.repositories.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EvaluationService
{
    private final EvaluationRepository repository;
    private final RestaurantRepository restaurantRepository;
}
