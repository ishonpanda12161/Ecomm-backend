package com.ecom.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"product_name","category_id"}
                )
        }
)
@ToString
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3,message = "Must contain at least 3 characters.")
    private String productName;
    @NotBlank
    @Size(min = 5,message = "Must contain at least 5 characters.")
    private String description;
    private String image;
    @NotNull
    @Min(0)
    private Integer quantity;
    @NotNull
    private Double price;
    @NotNull
    private Double discount;
    private Double specialPrice;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product",cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REMOVE},orphanRemoval = true)
    private List<CartItem> cartItemList = new ArrayList<>();

    @ManyToOne()
    @JoinColumn(name = "seller_id")
    @ToString.Exclude
    private User user;

}
