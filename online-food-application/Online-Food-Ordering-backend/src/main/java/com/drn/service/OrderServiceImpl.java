package com.drn.service;

import com.drn.model.*;
import com.drn.repository.*;
import com.drn.request.OrderRequest;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final RestaurantService restaurantService;
    private final CartService cartService;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            AddressRepository addressRepository,
                            UserRepository userRepository,
                            RestaurantService restaurantService,
                            CartService cartService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.restaurantService = restaurantService;
        this.cartService = cartService;
    }

    @Override
    public Order createOrder(OrderRequest order, User user) throws Exception {
        Address shipAddress = order.getDeliverAddress();
        Address savedAddress = addressRepository.save(shipAddress);

        if(!user.getAddresses().contains(savedAddress)){
            user.getAddresses().add(savedAddress);
            userRepository.save(user);
        }

        Restaurant restaurant = restaurantService.findRestaurantById(order.getRestaurantId());
        Order createOrder = Order.builder()
                .customer(user)
                .createdAt(new Date())
                .orderStatus(OrderStatus.PENDING)
                .deliveryAddress(savedAddress)
                .restaurant(restaurant)
                .build();

        Cart cart = cartService.findCartByUserId(user.getId());

        List<OrderItem> orderItems = new ArrayList<>();

        for(CartItem cartItem : cart.getItem()) {
            OrderItem orderItem = OrderItem.builder()
                    .food(cartItem.getFood())
                    .ingredients(cartItem.getIngredients())
                    .quantity(cartItem.getQuantity())
                    .totalPrice(cartItem.getTotalPrice())
                    .build();
            OrderItem savedOrderItem = orderItemRepository.save(orderItem);
            orderItems.add(savedOrderItem);
        }
        Long totalPrice = cartService.calculateCartTotal(cart);

        createOrder.setItems(orderItems);
        createOrder.setTotalPrice(totalPrice);

        Order savedOrder = orderRepository.save(createOrder);
        restaurant.getOrders().add(savedOrder);

        return createOrder;
    }

    @Override
    public Order updateOrder(Long orderId, OrderStatus orderStatus) throws Exception {
        Order order = findOrderById(orderId);
        if(orderStatus.equals(OrderStatus.PENDING) ||
                orderStatus.equals(OrderStatus.DELIVERED) ||
                orderStatus.equals(OrderStatus.COMPLETED) ||
                orderStatus.equals(OrderStatus.OUT_FOR_DELIVERY)) {
            order.setOrderStatus(orderStatus);
            return orderRepository.save(order);
        }
        throw new Exception("Please select a valid order status");
    }

    @Override
    public void cancelOrder(Long orderId) throws Exception {
        Order order = findOrderById(orderId);
        orderRepository.deleteById(orderId);
    }

    @Override
    public List<Order> getUserOrder(Long userId) throws Exception {
        return orderRepository.findByCustomerId(userId);
    }

    @Override
    public List<Order> getRestaurantOrder(Long restaurantId, OrderStatus orderStatus) throws Exception {
        List<Order> orders = orderRepository.findByRestaurantId(restaurantId);
        if(orderStatus != null) {
            orders = orders.stream().filter(order ->
                        order.getOrderStatus().equals(orderStatus)).collect(Collectors.toList());
        }
        return orders;
    }

    @Override
    public Order findOrderById(Long orderId) throws Exception {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if(optionalOrder.isEmpty()) {
            throw new Exception("Order not found!");
        }
        return optionalOrder.get();
    }
}
