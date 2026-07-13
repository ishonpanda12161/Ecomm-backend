package com.ecom.backend.service;

import com.ecom.backend.config.AuthUtil;
import com.ecom.backend.exceptions.GenericAPIException;
import com.ecom.backend.exceptions.ResourceAlreadyExistsException;
import com.ecom.backend.exceptions.ResourceNotFoundException;
import com.ecom.backend.mapper.CartItemMapper;
import com.ecom.backend.mapper.CartMapper;
import com.ecom.backend.mapper.ProductMapper;
import com.ecom.backend.model.Cart;
import com.ecom.backend.model.CartItem;
import com.ecom.backend.model.Product;
import com.ecom.backend.model.User;
import com.ecom.backend.payload.CartDTO;
import com.ecom.backend.payload.CartItemDTO;
import com.ecom.backend.repository.CartItemRepository;
import com.ecom.backend.repository.CartRepository;
import com.ecom.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService{

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CartItemMapper cartItemMapper;
    private final CartRepository cartRepository;
    private final AuthUtil authUtil;
    private final CartMapper cartMapper;

    @Transactional
    @Override
    public CartItemDTO addToCart(Long productId, Integer quantity) {

        Cart cart = getCart();
        Product product = getProduct(productId,quantity);

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(),productId).orElse(null);

        if(cartItem!=null)
        {
            throw new ResourceAlreadyExistsException("Product",productId);
        }

        cartItem = cartItemRepository.save(new CartItem(product,quantity,cart));
        CartItemDTO cartItemDTO = cartItemMapper.getCartItemDto(cartItem);
        cartRepository.save(cart);

        return cartItemDTO;
    }

    @Transactional(readOnly = true)
    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        return cartMapper.getCartDtoList(carts);
    }

    @Transactional(readOnly = true)
    @Override
    public CartDTO getUserCart() {
        CartDTO cartDTO = cartMapper.getCartDto(getCart());
        return cartDTO;
    }

    @Transactional
    @Override
    public CartDTO updateCart(Long productId, int operation) {
        Cart cart = getCart();
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(),productId)
                .orElseThrow(()-> new ResourceNotFoundException("Item","id",productId));
        int newQuantity = cartItem.getQuantity() + operation;
        if(newQuantity<=0)
        {
            cart.getCartItems().remove(cartItem);
        }
        else {
            Product product = productRepository.findById(productId)
                    .orElseThrow(()->new ResourceNotFoundException("Product","productId",productId));
            if(product.getQuantity()==0)
            {
                cart.getCartItems().remove(cartItem);
            }
            else{
                cartItem.setProduct(product);
                cartItem.setQuantity(Math.min(newQuantity,product.getQuantity()));
                cartItemRepository.save(cartItem);
            }
        }

        return cartMapper.getCartDto(cartRepository.save(cart));

    }

    @Transactional
    @Override
    public CartDTO deleteProduct(Long productId) {
        Cart cart = getCart();
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(),productId)
                .orElseThrow(()-> new ResourceNotFoundException("Item","id",productId));
        cart.getCartItems().remove(cartItem);

        return cartMapper.getCartDto(cartRepository.save(cart));
    }


    private Product getProduct(Long productId,Integer quantity)
    {
        Product product = productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("Product","productId",productId));
        if(product.getQuantity()>=quantity)
        {
            return product;
        }
        else
        {
            throw new GenericAPIException("Quantity not available in stock.");
        }
    }


    private Cart getCart()
    {
        Cart cart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if(cart!=null)
        {
            return cart;
        }

        Cart newCart = new Cart();
        newCart.setUser(authUtil.loggedInUser());

        return cartRepository.save(newCart);

    }
}
