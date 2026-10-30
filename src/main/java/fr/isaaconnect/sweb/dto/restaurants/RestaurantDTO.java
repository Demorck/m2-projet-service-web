package fr.isaaconnect.sweb.dto.restaurants;


import fr.isaaconnect.sweb.dto.evaluations.EvaluationDTO;
import fr.isaaconnect.sweb.entities.RestaurantEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDTO {
    private UUID id;
    private String name;
    private String address;
    private List<EvaluationDTO> evaluations;
    private double mean;

    public static RestaurantDTO fromEntity(RestaurantEntity entity, double mean) {
        return RestaurantDTO.builder()
            .id(entity.getId())
            .name(entity.getName())
            .address(entity.getAddress())
            .evaluations(entity.getEvaluations().stream().map(EvaluationDTO::fromEntity).toList())
            .mean(mean)
            .build();
    }
}
