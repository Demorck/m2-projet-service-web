package fr.isaaconnect.sweb.controllers;

import fr.isaaconnect.sweb.dto.restaurants.RestaurantDTO;
import fr.isaaconnect.sweb.services.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController
{
    private final RestaurantService service;

    @GetMapping
    public List<RestaurantDTO> getRestaurants() {
        return this.service.getRestaurants().stream().map(RestaurantDTO::fromEntity).toList();
    }
}
