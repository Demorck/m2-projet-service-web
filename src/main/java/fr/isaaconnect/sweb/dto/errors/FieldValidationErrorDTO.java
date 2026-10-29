package fr.isaaconnect.sweb.dto.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;


@SuperBuilder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FieldValidationErrorDTO extends ErrorResponseDTO {
    private List<FieldErrorDTO> fieldErrors;
}
