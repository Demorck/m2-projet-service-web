package fr.isaaconnect.sweb.dto.restaurants;

import fr.isaaconnect.sweb.entities.RestaurantEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter @Builder
public class AddResponseRestaurantDTO
{
    private UUID id;

    public static AddResponseRestaurantDTO fromEntity(RestaurantEntity restaurant) {
        return AddResponseRestaurantDTO.builder()
                .id(restaurant.getId())
                .build();
    }
}
