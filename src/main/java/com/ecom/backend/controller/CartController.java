package com.ecom.backend.controller;


import com.ecom.backend.model.Cart;
import com.ecom.backend.payload.CartDTO;
import com.ecom.backend.payload.CartItemDTO;
import com.ecom.backend.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/public/cart/add/product/{productId}/quantity/{quantity}")
    public ResponseEntity<CartItemDTO> addToCart(
            @PathVariable Long productId,
            @PathVariable Integer quantity
    )
    {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.addToCart(productId,quantity));
    }

    @GetMapping("/public/cart/user")
    public ResponseEntity<CartDTO> getUserCart()
    {
        return ResponseEntity.ok().body(cartService.getUserCart());
    }

    @PutMapping("/public/cart/udpate/product/{productId}/operation/{operation}")
    public ResponseEntity<CartDTO> updateUserCart(
            @PathVariable Long productId,
            @PathVariable String operation
    )
    {
        return ResponseEntity.ok().body(cartService.updateCart(productId,operation.equalsIgnoreCase("delete")?-1:1));
    }

    @DeleteMapping("/public/cart/delete/product/{productId}")
    public ResponseEntity<CartDTO> updateUserCart(
            @PathVariable Long productId
    )
    {
        return ResponseEntity.ok().body(cartService.deleteProduct(productId));
    }

    @GetMapping("/admin/carts")
    public ResponseEntity<List<CartDTO>> getAllCarts()
    {
        return ResponseEntity.ok().body(cartService.getAllCarts());
    }

}

