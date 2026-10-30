package fr.isaaconnect.sweb.dto.restaurants;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ModifyRestaurantDTO {
    @Size(max = 90, message = "{restaurant.name.size}")
    @Pattern(regexp = ".*\\S.*", message = "{general.fields.not_blank}")
    private String name;

    @Size(max = 255, message = "{restaurant.address.size}")
    @Pattern(regexp = ".*\\S.*", message = "{general.fields.not_blank}")
    private String address;
}
