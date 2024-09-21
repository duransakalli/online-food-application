package com.drn.service;

import com.drn.dto.RestaurantDto;
import com.drn.model.Address;
import com.drn.model.Restaurant;
import com.drn.model.User;
import com.drn.repository.AddressRepository;
import com.drn.repository.RestaurantRepository;
import com.drn.repository.UserRepository;
import com.drn.request.CreateRestaurantRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Autowired
    public RestaurantServiceImpl(RestaurantRepository restaurantRepository,
                                 AddressRepository addressRepository,
                                 UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Restaurant createRestaurant(CreateRestaurantRequest request, User user) {

        Address address = addressRepository.save(request.getAddress());
        Restaurant restaurant = Restaurant.builder()
                .address(address)
                .contactInformation(request.getContactInformation())
                .cuisineType(request.getCuisineType())
                .description(request.getDescription())
                .images(request.getImages())
                .name(request.getName())
                .openingHours(request.getOpeningHours())
                .registrationDate(LocalDateTime.now())
                .owner(user)
                .build();

        return restaurantRepository.save(restaurant);
    }

    @Override
    public Restaurant updateRestaurant(Long restaurantId, CreateRestaurantRequest updatedRestaurant) throws Exception {

        Restaurant restaurant = findRestaurantById(restaurantId);

        Restaurant updated = Restaurant.builder()
                .id(restaurant.getId())
                .owner(restaurant.getOwner())
                .name(updatedRestaurant.getName() != null ? updatedRestaurant.getName() : restaurant.getName())
                .description(updatedRestaurant.getDescription() != null ? updatedRestaurant.getDescription() : restaurant.getDescription())
                .cuisineType(updatedRestaurant.getCuisineType() != null ? updatedRestaurant.getCuisineType() : restaurant.getCuisineType())
                .address(updatedRestaurant.getAddress() != null ? updatedRestaurant.getAddress() : restaurant.getAddress())
                .contactInformation(updatedRestaurant.getContactInformation() != null ? updatedRestaurant.getContactInformation() : restaurant.getContactInformation())
                .openingHours(updatedRestaurant.getOpeningHours() != null ? updatedRestaurant.getOpeningHours() : restaurant.getOpeningHours())
                .images(updatedRestaurant.getImages() != null ? updatedRestaurant.getImages() : restaurant.getImages())
                .registrationDate(restaurant.getRegistrationDate())
                .open(restaurant.isOpen())
                .orders(restaurant.getOrders())
                .foods(restaurant.getFoods())
                .build();

        return restaurantRepository.save(updated);
    }

    @Override
    public void deleteRestaurant(Long restaurantId) throws Exception {
        Restaurant restaurant = findRestaurantById(restaurantId);
        restaurantRepository.delete(restaurant);
    }

    @Override
    public List<Restaurant> getAllRestaurant() {
        return restaurantRepository.findAll();
    }

    @Override
    public List<Restaurant> searchRestaurant(String query) {
        return restaurantRepository.findBySearchQuery(query);
    }

    @Override
    public Restaurant findRestaurantById(Long restaurantId) throws Exception {

        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(restaurantId);

        if(optionalRestaurant.isEmpty()) {
            throw new Exception("Restaurant not found with id: "+restaurantId);
        }

        return optionalRestaurant.get();
    }

    @Override
    public Restaurant getRestaurantByUserId(Long userId) throws Exception {
        Restaurant restaurant = restaurantRepository.findByOwnerId(userId);

        if(Objects.isNull(restaurant)) {
            throw new Exception("Restaurant not found with owner id: "+userId);
        }

        return restaurant;
    }

    @Override
    public RestaurantDto addToFavorites(Long restaurantId, User user) throws Exception {

        Restaurant restaurant = findRestaurantById(restaurantId);

        RestaurantDto restaurantDto = RestaurantDto.builder()
                .title(restaurant.getName())
                .images(restaurant.getImages())
                .description(restaurant.getDescription())
                .id(restaurantId)
                .build();

        List<RestaurantDto> favorites = user.getFavorites();

        Optional<RestaurantDto> existFavorite = favorites.stream()
                        .filter(favorite -> favorite.getId().equals(restaurantId))
                        .findFirst();

        if(existFavorite.isPresent()) {
            favorites.removeIf(favorite -> favorite.getId().equals(restaurantId));
        } else {
            favorites.add(restaurantDto);
        }

        userRepository.save(user);
        return restaurantDto;
    }

    @Override
    public Restaurant updateRestaurantStatus(Long restaurantId) throws Exception {
        Restaurant restaurant = findRestaurantById(restaurantId);
        restaurant.setOpen(!restaurant.isOpen());
        return restaurantRepository.save(restaurant);
    }
}
