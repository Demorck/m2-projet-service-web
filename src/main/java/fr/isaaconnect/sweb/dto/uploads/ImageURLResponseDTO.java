package fr.isaaconnect.sweb.dto.uploads;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ImageURLResponseDTO {
    private String url;
}
