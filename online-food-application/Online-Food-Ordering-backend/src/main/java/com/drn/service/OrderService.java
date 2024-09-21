package com.drn.service;

import com.drn.model.Order;
import com.drn.model.OrderStatus;
import com.drn.model.User;
import com.drn.request.OrderRequest;
import org.aspectj.weaver.ast.Or;

import java.util.List;

public interface OrderService {

    public Order createOrder(OrderRequest order, User user) throws Exception;

    public Order updateOrder(Long orderId, OrderStatus orderStatus) throws Exception;

    public void cancelOrder(Long orderId) throws Exception;

    public List<Order> getUserOrder(Long userId) throws Exception;

    public List<Order> getRestaurantOrder(Long restaurantId, OrderStatus orderStatus) throws Exception;

    public Order findOrderById(Long orderId) throws Exception;
}
