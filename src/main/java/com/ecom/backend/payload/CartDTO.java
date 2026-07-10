package com.ecom.backend.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CartDTO {

    private Long id;
    private Double totalPrice;
    private List<CartItemDTO> cartItems;


}
