package com.ecommerce.project.services;

import com.ecommerce.project.payload.CartDTO;

public interface CartService {
    CartDTO addProductToCart(Long productId, Integer quantity);
}
