package com.ecom.backend.payload;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductDTO {
    private Long id;

    @NotBlank
    @Size(min = 3, message = "Must contain at least 3 characters.")
    private String productName;

    @NotBlank
    @Size(min = 5, message = "Must contain at least 5 characters.")
    private String description;

    private String image;

    @NotNull
    @Positive
    private Integer quantity;

    @NotNull
    @Positive
    private Double price;

    @NotNull
    private Double discount;

    private Double specialPrice;
}
