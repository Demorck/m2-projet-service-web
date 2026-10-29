package fr.isaaconnect.sweb.dto.uploads;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageURLResponseDTO {
    @Getter
    @Setter
    @NotBlank
    public String url;
}
