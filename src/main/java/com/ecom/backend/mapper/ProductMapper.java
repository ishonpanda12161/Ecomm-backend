package com.ecom.backend.mapper;

import com.ecom.backend.model.Product;
import com.ecom.backend.payload.ProductDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDTO toDTO(Product product);

    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "cartItemList", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    Product toModel(ProductDTO productDTO);

    List<ProductDTO> toDTOList(List<Product> products);
}
