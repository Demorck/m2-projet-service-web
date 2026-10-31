package fr.isaaconnect.sweb.dto.evaluations;

import fr.isaaconnect.sweb.entities.EvaluationEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationIndexDTO {
    private UUID id;
    private UUID restaurantId;
    private String author;
    private String text;
    private int note;

    public static EvaluationIndexDTO fromEntity(EvaluationEntity entity) {
        return EvaluationIndexDTO.builder()
            .id(entity.getId())
            .author(entity.getAuthor())
            .text(entity.getText())
            .note(entity.getNote())
            .restaurantId(entity.getRestaurant().getId())
            .build();
    }
}
