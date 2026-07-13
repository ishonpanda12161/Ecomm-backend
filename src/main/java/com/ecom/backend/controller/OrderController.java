package com.ecom.backend.controller;

import com.ecom.backend.payload.OrderDTO;
import com.ecom.backend.payload.OrderRequestDTO;
import com.ecom.backend.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/public/order/payment/{paymentMethod}")
    public ResponseEntity<OrderDTO> orderProduct(
            @RequestBody @Valid OrderRequestDTO orderRequestDTO,
            @PathVariable String paymentMethod
            )
    {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.placeOrder(orderRequestDTO,paymentMethod));
    }
}
