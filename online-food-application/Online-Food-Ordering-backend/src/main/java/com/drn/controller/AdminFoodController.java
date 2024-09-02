package com.drn.controller;

import com.drn.model.Food;
import com.drn.model.Restaurant;
import com.drn.model.User;
import com.drn.request.CreateFoodRequest;
import com.drn.response.MessageResponse;
import com.drn.service.FoodService;
import com.drn.service.RestaurantService;
import com.drn.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/food")
public class AdminFoodController {

    private FoodService foodService;
    private UserService userService;
    private RestaurantService restaurantService;

    @Autowired
    public AdminFoodController(FoodService foodService,
                               UserService userService,
                               RestaurantService restaurantService) {
        this.foodService = foodService;
        this.userService = userService;
        this.restaurantService = restaurantService;
    }

    @PostMapping
    public ResponseEntity<Food> createFood(@RequestBody CreateFoodRequest request,
                                           @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        Restaurant restaurant = restaurantService.findRestaurantById(request.getRestaurantId());
        Food food = foodService.createFood(request, request.getCategory(), restaurant);

        return new ResponseEntity<>(food, HttpStatus.CREATED);
    }

    @DeleteMapping("/{foodId}")
    public ResponseEntity<MessageResponse> deleteFood(@PathVariable Long foodId,
                                           @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        foodService.deleteFood(foodId);

        MessageResponse response = MessageResponse.builder()
                .message("Food deleted successfully")
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{foodId}")
    public ResponseEntity<Food> updateFoodAvailabilityStatus(@PathVariable Long foodId,
                                                             @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        Food food = foodService.updateAvailability(foodId);

        return new ResponseEntity<>(food, HttpStatus.OK);
    }

}
