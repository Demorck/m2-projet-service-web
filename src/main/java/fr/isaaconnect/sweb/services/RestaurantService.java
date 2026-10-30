package fr.isaaconnect.sweb.services;

import fr.isaaconnect.sweb.entities.EvaluationEntity;
import fr.isaaconnect.sweb.entities.RestaurantEntity;
import fr.isaaconnect.sweb.exceptions.NotFoundException;
import fr.isaaconnect.sweb.repositories.RestaurantRepository;
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

    public Double noteMean(RestaurantEntity entity) {
        IntSummaryStatistics stats = entity.getEvaluations().stream()
            .mapToInt(EvaluationEntity::getNote)
            .summaryStatistics();

        return stats.getCount() == 0 ? stats.getAverage() : -1;
    }
}
