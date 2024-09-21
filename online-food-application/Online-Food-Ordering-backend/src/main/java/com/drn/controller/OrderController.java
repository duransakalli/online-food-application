package com.drn.controller;

import com.drn.model.CartItem;
import com.drn.model.Order;
import com.drn.model.User;
import com.drn.request.AddCartItemRequest;
import com.drn.request.OrderRequest;
import com.drn.service.OrderService;
import com.drn.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @Autowired
    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @PostMapping("/order")
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest request,
                                             @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserByJwtToken(jwt);
        Order order = orderService.createOrder(request, user);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @GetMapping("/order/user")
    public ResponseEntity<List<Order> > getOrderHistory(@RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserByJwtToken(jwt);
        List<Order> orders = orderService.getUserOrder(user.getId());
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }
}
