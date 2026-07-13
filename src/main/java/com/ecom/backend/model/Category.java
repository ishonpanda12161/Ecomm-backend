package com.ecom.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Entity(name = "categoryTable")
@Getter
@Setter
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3,max = 25,message = "Minimum 3 length and cannot exceed 20.")
    private String categoryName;

    @OneToMany(
            mappedBy = "category",
            cascade = {CascadeType.PERSIST,CascadeType.MERGE}
    )
    private List<Product> products;
}
