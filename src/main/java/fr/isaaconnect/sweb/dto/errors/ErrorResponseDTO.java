package fr.isaaconnect.sweb.dto.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@SuperBuilder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {
    private Integer code;
    private String message;
}
