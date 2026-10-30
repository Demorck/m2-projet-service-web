package fr.isaaconnect.sweb.dto.restaurants;


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
    private String name;

    @Size(max = 255, message = "{restaurant.address.size}")
    private String address;
}
