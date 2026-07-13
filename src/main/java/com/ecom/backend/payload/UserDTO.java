package com.ecom.backend.payload;

import com.ecom.backend.model.Address;
import com.ecom.backend.model.Product;
import com.ecom.backend.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
public class UserDTO {

    private String name;
    private String username;
    private String email;
    private Set<Role> roles = new HashSet<>();
    private Set<Product> products = new HashSet<>();
    private List<Address> addresses = new ArrayList<>();

}
