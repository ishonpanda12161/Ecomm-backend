package com.ecom.backend.mapper;

import com.ecom.backend.model.Order;
import com.ecom.backend.payload.OrderDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = {OrderItemMapper.class, PaymentMapper.class}
)
public interface OrderMapper {

    @Mapping(source = "orderItemList",target = "orderItemsDTOList")
    @Mapping(source = "payment",target = "paymentDTO")
    @Mapping(target = "addressId",ignore = true)
    OrderDTO toDTO(Order order);
}
