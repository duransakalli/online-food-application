package com.drn.service;

import com.drn.model.IngredientCategory;
import com.drn.model.IngredientItem;
import com.drn.model.Restaurant;
import com.drn.repository.IngredientCategoryRepository;
import com.drn.repository.IngredientItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IngredientServiceImpl implements IngredientService {

    private IngredientCategoryRepository ingredientCategoryRepository;
    private IngredientItemRepository ingredientItemRepository;
    private RestaurantService restaurantService;

    @Autowired
    public IngredientServiceImpl(IngredientCategoryRepository ingredientCategoryRepository,
                                 IngredientItemRepository ingredientItemRepository,
                                 RestaurantService restaurantService) {
        this.ingredientCategoryRepository = ingredientCategoryRepository;
        this.ingredientItemRepository = ingredientItemRepository;
        this.restaurantService = restaurantService;
    }

    @Override
    public IngredientCategory createIngredientCategory(String name, Long restaurantId) throws Exception {

        Restaurant restaurant = restaurantService.findRestaurantById(restaurantId);
        IngredientCategory category = IngredientCategory.builder()
                .restaurant(restaurant)
                .name(name)
                .build();

        return ingredientCategoryRepository.save(category);
    }

    @Override
    public IngredientCategory findIngredientCategoryById(Long id) throws Exception {
        Optional<IngredientCategory> optionalIngredientCategory = ingredientCategoryRepository.findById(id);

        if(optionalIngredientCategory.isEmpty()) {
            throw new Exception("Ingredient category not found");
        }

        return optionalIngredientCategory.get();
    }

    @Override
    public List<IngredientCategory> findIngredientCategoryByRestaurantId(Long id) throws Exception {
        restaurantService.findRestaurantById(id);
        return ingredientCategoryRepository.findByRestaurantId(id);
    }

    @Override
    public IngredientItem createIngredientItem(Long restaurantId, String ingredientName, Long categoryId) throws Exception {
        Restaurant restaurant = restaurantService.findRestaurantById(restaurantId);
        IngredientCategory category = findIngredientCategoryById(categoryId);
        IngredientItem ingredientItem = IngredientItem.builder()
                .name(ingredientName)
                .restaurant(restaurant)
                .category(category)
                .build();
        IngredientItem ingredient = ingredientItemRepository.save(ingredientItem);
        category.getIngredients().add(ingredientItem);

        return ingredient;
    }

    @Override
    public List<IngredientItem> findRestaurantIngredient(Long restaurantId) throws Exception {
        return ingredientItemRepository.findByRestaurantId(restaurantId);
    }

    @Override
    public IngredientItem updateStock(Long id) throws Exception {
        Optional<IngredientItem> optionalIngredientItem = ingredientItemRepository.findById(id);

        if(optionalIngredientItem.isEmpty()) {
            throw new Exception("Ingredient not found");
        }
        IngredientItem ingredientItem = optionalIngredientItem.get();
        ingredientItem.setInStock(!ingredientItem.isInStock());
        return ingredientItemRepository.save(ingredientItem);
    }
}
