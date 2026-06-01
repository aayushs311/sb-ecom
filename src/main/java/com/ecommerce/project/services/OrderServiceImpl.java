package com.ecommerce.project.services;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.MyResourceNotFoundException;
import com.ecommerce.project.models.*;
import com.ecommerce.project.payload.OrderDTO;
import com.ecommerce.project.payload.OrderItemDTO;
import com.ecommerce.project.repositories.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService{
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final PaymentRepository paymentRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final ModelMapper modelMapper;

    public OrderServiceImpl(OrderRepository orderRepository, CartRepository cartRepository, AddressRepository addressRepository, PaymentRepository paymentRepository, OrderItemRepository orderItemRepository, ProductRepository productRepository, CartService cartService, ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.addressRepository = addressRepository;
        this.paymentRepository = paymentRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public OrderDTO placeOrder(String email, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage) {

        Cart cart = cartRepository.findCartByEmail(email);
        if(cart == null) {
            throw new MyResourceNotFoundException("Cart", "email", email);
        }

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new MyResourceNotFoundException("Address", "addressId", addressId));

        Order order = new Order();
        order.setEmail(email);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus("Order Accepted");
        order.setAddress(address);

        Payment payment = new Payment(paymentMethod, pgStatus, pgResponseMessage, pgName, pgPaymentId);
        payment.setOrder(order);
        payment = paymentRepository.save(payment);
        order.setPayment(payment);

        Order savedOrder = orderRepository.save(order);

        List<CartItem> cartItems = cart.getCartItems();
        if(cartItems.isEmpty()) {
            throw new APIException("Cart is empty");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        for(CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderedProductPrice(cartItem.getProductPrice());
            orderItem.setOrder(savedOrder);
            orderItems.add(orderItem);
        }

        orderItems = orderItemRepository.saveAll(orderItems);

        cart.getCartItems().forEach(item -> {
            int quantity = item.getQuantity();
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() - quantity);
            productRepository.save(product);

            cartService.deleteProductFromCart(cart.getCartId(), item.getProduct().getProductId());
        });

        OrderDTO orderDto = modelMapper.map(savedOrder, OrderDTO.class);
        orderItems.forEach(item -> {
            orderDto.getOrderItems().add(
                    modelMapper.map(item, OrderItemDTO.class)
            );
        });
        orderDto.setAddressId(addressId);
        return orderDto;
    }
}
