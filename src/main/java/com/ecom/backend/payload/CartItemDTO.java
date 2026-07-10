package com.ecom.backend.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CartItemDTO {

    private Long id;
    private ProductDTO productDto;
    private Integer quantity;
    private Integer availableQuantity;

}
