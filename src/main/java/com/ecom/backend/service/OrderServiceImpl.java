package com.ecom.backend.service;

import com.ecom.backend.config.AuthUtil;
import com.ecom.backend.exceptions.GenericAPIException;
import com.ecom.backend.exceptions.ResourceNotFoundException;
import com.ecom.backend.mapper.OrderItemMapper;
import com.ecom.backend.mapper.OrderMapper;
import com.ecom.backend.mapper.PaymentMapper;
import com.ecom.backend.model.*;
import com.ecom.backend.payload.OrderDTO;
import com.ecom.backend.payload.OrderRequestDTO;
import com.ecom.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final AuthUtil authUtil;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final PaymentMapper paymentMapper;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;

    @Transactional
    @Override
    public OrderDTO placeOrder(OrderRequestDTO orderRequestDTO, String paymentMethod) {
        //get User , Cart , Address
        User user = authUtil.loggedInUser();
        Cart cart = cartRepository.findCartByEmail(user.getEmail());
        if(cart==null)
        {
            throw new ResourceNotFoundException("Cart","email", user.getEmail());
        }

        Address address = addressRepository.findById(orderRequestDTO.getAddressId())
                .orElseThrow(()-> new ResourceNotFoundException("Address","id",orderRequestDTO.getAddressId()));

        if(!address.getUser().getId().equals(user.getId()))
        {
            throw new GenericAPIException("Address does not belong to the logged-in user");
        }
        List<CartItem> cartItemList = cart.getCartItems();
        if(cartItemList.isEmpty())
        {
            throw new GenericAPIException("Cart is empty.");
        }

        cart.getCartItems().forEach(cartItem ->
        {
            Product product = cartItem.getProduct();

            if (product.getQuantity() < cartItem.getQuantity()) {
                throw new GenericAPIException(
                        product.getProductName() + " has insufficient stock."
                );
            }
        });

        //Create Order
        Order order = new Order();
        order.setEmail(user.getEmail());
        order.setAddress(address);
        order.setTotal(cart.getCartItems().stream().mapToDouble(item -> item.getProduct().getSpecialPrice()*item.getQuantity()).sum());
        order.setStatus("Order Accepted");
        order.setDate(LocalDate.now());

        //Create Payment & save payment , order
        Payment payment = new Payment(
                paymentMethod
                ,orderRequestDTO.getPgPaymentId()
                ,orderRequestDTO.getPgStatus()
                ,orderRequestDTO.getPgResponseMessage()
                ,orderRequestDTO.getPgName());

//        removing it because order has cascading
//        payment = paymentRepository.save(payment);

        order.setPayment(payment);
        payment.setOrder(order);

        //save order & and set payment
        order = orderRepository.save(order);

        //create OrderItems
        List<OrderItem> orderItems = new ArrayList<>();

        for(CartItem cartItem : cartItemList)
        {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            orderItem.setDiscount(cartItem.getProduct().getDiscount());
            orderItem.setOrder(order);
            orderItems.add(orderItem);
        }

        orderItems = orderItemRepository.saveAll(orderItems);
        order.setOrderItemList(orderItems);

        for(CartItem cartItem : cartItemList)
        {
            Product product = cartItem.getProduct();
            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
        }
        cart.getCartItems().clear();
        cartRepository.save(cart);
        //create OrderDTO

        OrderDTO orderDTO = orderMapper.toDTO(order);
        orderDTO.setAddressId(address.getId());

        return orderDTO;
    }




}
