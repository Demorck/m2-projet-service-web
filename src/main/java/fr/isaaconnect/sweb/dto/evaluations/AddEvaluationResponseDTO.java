package fr.isaaconnect.sweb.dto.evaluations;

import fr.isaaconnect.sweb.entities.EvaluationEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class AddEvaluationResponseDTO {
    private UUID id;

    public static AddEvaluationResponseDTO fromEntity(EvaluationEntity evaluation) {
        return AddEvaluationResponseDTO.builder()
            .id(evaluation.getId())
            .build();
    }
}
