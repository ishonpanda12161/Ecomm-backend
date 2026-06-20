package com.ecom.backend.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    private Integer quantity;

    @NotNull
    private Double price;

    @NotNull
    private Double discount;

    private Double specialPrice;
}
