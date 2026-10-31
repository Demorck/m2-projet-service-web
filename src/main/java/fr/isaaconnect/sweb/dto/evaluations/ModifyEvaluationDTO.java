package fr.isaaconnect.sweb.dto.evaluations;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ModifyEvaluationDTO {
    @Range(min = 0, max = 3, message = "{evaluation.note.size}")
    @Pattern(regexp = ".*\\S.*", message = "{general.fields.not_blank}")
    private Integer note;

    @Size(max = 255, message = "{evaluation.text.size}")
    @Pattern(regexp = ".*\\S.*", message = "{general.fields.not_blank}")
    private String text;
}
