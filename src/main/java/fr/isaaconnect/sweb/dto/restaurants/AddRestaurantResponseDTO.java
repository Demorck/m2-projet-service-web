package fr.isaaconnect.sweb.dto.restaurants;

import fr.isaaconnect.sweb.entities.RestaurantEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter @Builder
public class AddRestaurantResponseDTO
{
    private UUID id;

    public static AddRestaurantResponseDTO fromEntity(RestaurantEntity restaurant) {
        return AddRestaurantResponseDTO.builder()
                .id(restaurant.getId())
                .build();
    }
}
