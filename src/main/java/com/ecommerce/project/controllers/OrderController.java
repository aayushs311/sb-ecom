package com.ecommerce.project.controllers;

import com.ecommerce.project.payload.OrderDTO;
import com.ecommerce.project.payload.OrderRequestDTO;
import com.ecommerce.project.services.OrderService;
import com.ecommerce.project.utils.AuthUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;
    private final AuthUtils authUtils;

    public OrderController(OrderService orderService, AuthUtils authUtils) {
        this.orderService = orderService;
        this.authUtils = authUtils;
    }

    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> orderProducts(@PathVariable String paymentMethod, @RequestBody OrderRequestDTO orderRequestDTO) {
        String email = authUtils.loggedInEmail();
        OrderDTO order = orderService.placeOrder(
                email,
                orderRequestDTO.getAddressId(),
                paymentMethod,
                orderRequestDTO.getPgName(),
                orderRequestDTO.getPgPaymentId(),
                orderRequestDTO.getPgStatus(),
                orderRequestDTO.getPgResponseMessage()
        );
        return new ResponseEntity<>(order,HttpStatus.CREATED);
    }
}
