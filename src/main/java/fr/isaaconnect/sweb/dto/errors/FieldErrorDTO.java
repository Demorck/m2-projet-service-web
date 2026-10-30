package fr.isaaconnect.sweb.dto.errors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.FieldError;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FieldErrorDTO {
    private String fieldName;
    private String message;

    public static FieldErrorDTO fromFieldError(FieldError error) {
        return FieldErrorDTO.builder()
            .fieldName(error.getField())
            .message(error.getDefaultMessage())
            .build();
    }
}
