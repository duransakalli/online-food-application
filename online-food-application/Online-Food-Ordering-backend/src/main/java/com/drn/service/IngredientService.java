package com.drn.service;

import com.drn.model.IngredientCategory;
import com.drn.model.IngredientItem;

import java.util.List;

public interface IngredientService {

    public IngredientCategory createIngredientCategory(String name, Long restaurantId) throws Exception;

    public IngredientCategory findIngredientCategoryById(Long id) throws Exception;

    public List<IngredientCategory> findIngredientCategoryByRestaurantId(Long id) throws Exception;

    public IngredientItem createIngredientItem(Long restaurantId, String ingredientName, Long categoryId) throws Exception;

    public List<IngredientItem> findRestaurantIngredient(Long restaurantId) throws Exception;

    public IngredientItem updateStock(Long id) throws Exception;

}
