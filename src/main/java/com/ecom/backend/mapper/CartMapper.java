package com.ecom.backend.mapper;

import com.ecom.backend.model.Cart;
import com.ecom.backend.model.CartItem;
import com.ecom.backend.payload.CartDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = CartItemMapper.class
)
public interface CartMapper {

    @Mapping(source = "cartItems",target = "cartItems")
    @Mapping(target = "totalPrice",expression = "java(calculateTotalPrice(cart))")
    CartDTO getCartDto(Cart cart);

    List<CartDTO> getCartDtoList(List<Cart> carts);

    default Double calculateTotalPrice(Cart cart)
    {
        List<CartItem> cartItems = cart.getCartItems();
        return cartItems.stream().mapToDouble(item -> item.getProduct().getSpecialPrice()*item.getQuantity()).sum();
    }

}

