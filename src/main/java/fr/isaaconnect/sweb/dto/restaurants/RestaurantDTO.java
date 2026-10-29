package fr.isaaconnect.sweb.dto.restaurants;


import fr.isaaconnect.sweb.dto.evaluations.EvaluationDTO;
import fr.isaaconnect.sweb.entities.EvaluationEntity;
import fr.isaaconnect.sweb.entities.RestaurantEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDTO
{
    @NotBlank
    @Size(max = 90, message = "{restaurant.name.size}")
    private String name;

    @NotBlank
    @Size(max = 255, message = "{restaurant.address.size}")
    private String address;

    private List<EvaluationDTO> evaluations;

    private double mean;

    public static RestaurantDTO fromEntity(RestaurantEntity entity) {
        IntSummaryStatistics stats = entity.getEvaluations().stream()
                .mapToInt(EvaluationEntity::getNote)
                .summaryStatistics();

        double mean = stats.getAverage();
        if (stats.getCount() == 0) mean = -1;

        return RestaurantDTO.builder()
                .name(entity.getName())
                .address(entity.getAddress())
                .evaluations(entity.getEvaluations().stream().map(EvaluationDTO::fromEntity).toList())
                .mean(mean)
                .build();
    }
}
