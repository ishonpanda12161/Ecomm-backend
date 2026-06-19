package com.ecom.backend.mapper;

import com.ecom.backend.model.Product;
import com.ecom.backend.payload.ProductDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDTO toDTO(Product product);
    Product toModel(ProductDTO productDTO);
    List<ProductDTO> toDTOList(List<Product> products);
}
