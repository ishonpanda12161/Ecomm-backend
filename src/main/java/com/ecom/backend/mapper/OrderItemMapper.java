package com.ecom.backend.mapper;

import com.ecom.backend.model.OrderItem;
import com.ecom.backend.payload.OrderItemDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = ProductMapper.class
)
public interface OrderItemMapper {

    @Mapping(source = "product",target = "productDTO")
    OrderItemDTO toDTO(OrderItem orderItem);
}
