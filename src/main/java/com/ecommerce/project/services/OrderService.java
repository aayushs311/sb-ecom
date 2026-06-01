package com.ecommerce.project.services;

import com.ecommerce.project.payload.OrderDTO;
import jakarta.transaction.Transactional;

public interface OrderService {
    @Transactional
    OrderDTO placeOrder(String email, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage);
}
