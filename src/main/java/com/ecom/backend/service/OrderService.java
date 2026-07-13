package com.ecom.backend.service;

import com.ecom.backend.payload.OrderDTO;
import com.ecom.backend.payload.OrderRequestDTO;
import org.springframework.transaction.annotation.Transactional;

public interface OrderService {
    @Transactional
    OrderDTO placeOrder(OrderRequestDTO orderRequestDTO, String paymentMethod);
}
