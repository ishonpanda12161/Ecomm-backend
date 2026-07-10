package com.ecom.backend.mapper;

import com.ecom.backend.model.CartItem;
import com.ecom.backend.payload.CartItemDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = ProductMapper.class
)
public interface CartItemMapper {

    @Mapping(source = "product",target = "productDto")
    @Mapping(source = "product.quantity",target = "availableQuantity")
    CartItemDTO getCartItemDto(CartItem cartItem);

    List<CartItemDTO> getCartItemDtoList(List<CartItem> cartItems);

}
