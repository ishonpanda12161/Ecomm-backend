package com.ecom.backend.service;

import com.ecom.backend.model.Cart;
import com.ecom.backend.payload.CartDTO;
import com.ecom.backend.payload.CartItemDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CartService {

    CartItemDTO addToCart(Long productId, Integer quantity);

    List<CartDTO> getAllCarts();

    CartDTO getUserCart();

    @Transactional
    CartDTO updateCart(Long productId, int operation);

    @Transactional
    CartDTO deleteProduct(Long productId);
}
