package com.ecom.backend.service;

import com.ecom.backend.model.Cart;
import com.ecom.backend.payload.CartDTO;
import com.ecom.backend.payload.CartItemDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CartService {

    @Transactional
    CartItemDTO addToCart(Long productId, Integer quantity);

    @Transactional(readOnly = true)
    List<CartDTO> getAllCarts();

    @Transactional(readOnly = true)
    CartDTO getUserCart();

    @Transactional
    CartDTO updateCart(Long productId, int operation);

    @Transactional
    CartDTO deleteProduct(Long productId);
}
