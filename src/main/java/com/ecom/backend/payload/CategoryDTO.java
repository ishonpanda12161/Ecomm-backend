package com.ecom.backend.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    @NotNull
    private Long categoryId;
    @NotBlank
    @Size(min = 3,max = 25,message = "Minimum 3 length and cannot exceed 20.")
    private String categoryName;
}
