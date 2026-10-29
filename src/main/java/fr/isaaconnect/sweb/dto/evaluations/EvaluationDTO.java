package fr.isaaconnect.sweb.dto.evaluations;

import fr.isaaconnect.sweb.entities.EvaluationEntity;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationDTO
{

    @Size(max = 50, message = "{evaluation.author.size}")
    private String author;

    @Size(max = 255, message = "{evaluation.text.size}")
    private String text;

    @Size(min = 0, max = 3, message = "{evaluation.note.size}")
    private int note;

    public static EvaluationDTO fromEntity(EvaluationEntity entity) {
        return EvaluationDTO.builder()
                .author(entity.getAuthor())
                .text(entity.getText())
                .note(entity.getNote())
                .build();
    }
}
