package com.drn.service;

import com.drn.model.Category;
import com.drn.model.Food;
import com.drn.model.Restaurant;
import com.drn.repository.FoodRepository;
import com.drn.request.CreateFoodRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FoodServiceImpl implements FoodService {

    private FoodRepository foodRepository;

    @Autowired
    public FoodServiceImpl(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    @Override
    public Food createFood(CreateFoodRequest request, Category category, Restaurant restaurant) {
        Food food = Food.builder()
                .foodCategory(category)
                .restaurant(restaurant)
                .description(request.getDescription())
                .images(request.getImages())
                .name(request.getName())
                .price(request.getPrice())
                .ingredients(request.getIngredients())
                .isSeasonal(request.isSeasonal())
                .isVegetarian(request.isVegetarian())
                .build();

        Food savedFood = foodRepository.save(food);
        restaurant.getFoods().add(savedFood);
        return savedFood;
    }

    @Override
    public void deleteFood(Long foodId) throws Exception {
        Food food = findFoodById(foodId);
        food.setRestaurant(null);
        foodRepository.save(food);
    }

    @Override
    public List<Food> getRestaurantsFood(Long restaurantId,
                                         boolean isVegetarian,
                                         boolean isNonveg,
                                         boolean isSeasonal,
                                         String foodCategory) {

        List<Food> foods = foodRepository.findByRestaurantId(restaurantId);

        if(isVegetarian) {
            foods = filterByVegetarian(foods, isVegetarian);
        }

        if(isNonveg) {
            foods = filterByNonveg(foods, isNonveg);
        }

        if(isSeasonal) {
            foods = filterBySeasonal(foods, isSeasonal);
        }

        if(Objects.nonNull(foodCategory) && !foodCategory.isEmpty()) {
            foods = filterByCategory(foods, foodCategory);
        }

        return foods;
    }

    @Override
    public List<Food> searchFood(String keyword) {
        return foodRepository.searchFood(keyword);
    }

    @Override
    public Food findFoodById(Long foodId) throws Exception {
        Optional<Food> optionalFood = foodRepository.findById(foodId);
        if(optionalFood.isEmpty()) {
            throw new Exception("Food not exist...");
        }
        return optionalFood.get();
    }

    @Override
    public Food updateAvailability(Long foodId) throws Exception {
        Food food = findFoodById(foodId);
        food.setAvailable(!food.isAvailable());
        return foodRepository.save(food);
    }

    private List<Food> filterByVegetarian(List<Food> foods, boolean isVegetarian) {
        return foods.stream()
                .filter(food -> food.isVegetarian() == isVegetarian)
                .collect(Collectors.toList());
    }

    private List<Food> filterByCategory(List<Food> foods, String foodCategory) {
        return foods.stream()
                .filter(food -> {
                    if(Objects.nonNull(food.getFoodCategory())) {
                        return food.getFoodCategory().getName().equals(foodCategory);
                    }
                    return false;
                }).collect(Collectors.toList());
    }

    private List<Food> filterBySeasonal(List<Food> foods, boolean isSeasonal) {
        return foods.stream()
                .filter(food -> food.isSeasonal() == isSeasonal).
                collect(Collectors.toList());
    }

    private List<Food> filterByNonveg(List<Food> foods, boolean isNonveg) {
        return foods.stream()
                .filter(food -> !food.isVegetarian())
                .collect(Collectors.toList());
    }
}
