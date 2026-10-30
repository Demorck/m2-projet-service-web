package fr.isaaconnect.sweb.dto.evaluations;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

@Getter
public class AddEvaluationRequestDTO {
    @Size(max = 50, message = "{evaluation.author.size}")
    @NotBlank(message = "{general.fields.not_blank}")
    private String author;

    @Size(max = 255, message = "{evaluation.text.size}")
    @NotBlank(message = "{general.fields.not_blank}")
    private String text;

    @Range(min = 0, max = 3, message = "{evaluation.note.size}")
    private int note;
}
