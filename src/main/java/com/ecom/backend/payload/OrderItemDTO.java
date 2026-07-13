package com.ecom.backend.payload;

import lombok.Data;

@Data
public class OrderItemDTO {

    private Long id;
    private ProductDTO productDTO;
    private Integer quantity;
    private Double discount;
    private Double price;

}
