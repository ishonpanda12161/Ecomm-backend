package com.ecom.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long id;

    @NotBlank
    @Size(min = 3,message = "Street name must be at least 3 characters.")
    private String street;

    @NotBlank
    @Size(min = 3,message = "Building name must be at least 3 characters.")
    private String buildingName;

    @NotBlank
    @Size(min = 3,message = "City name must be at least 3 characters.")
    private String city;

    @NotBlank
    @Size(min = 3,message = "State name must be at least 3 characters.")
    private String stateName;

    @NotBlank
    @Size(min = 3,message = "Country name must be at least 3 characters.")
    private String country;

    @NotBlank
    @Size(min = 6,message = "Pin-code name must be at least 3 characters.")
    private String pinCode;

    @ManyToMany(mappedBy = "addresses")
    @ToString.Exclude
    private List<User> users = new ArrayList<>();

    public Address(String street, String buildingName, String city, String stateName, String country, String pinCode) {
        this.street = street;
        this.buildingName = buildingName;
        this.city = city;
        this.stateName = stateName;
        this.country = country;
        this.pinCode = pinCode;
    }

}
