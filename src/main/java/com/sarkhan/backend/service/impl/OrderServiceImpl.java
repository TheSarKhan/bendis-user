package com.sarkhan.backend.service.impl;

import com.sarkhan.backend.dto.cart.CartItemRequestDTO;
import com.sarkhan.backend.dto.order.OrderRequest;
import com.sarkhan.backend.exception.NotEnoughQuantityException;
import com.sarkhan.backend.handler.exception.ResourceNotFoundException;
import com.sarkhan.backend.jwt.JwtService;
import com.sarkhan.backend.model.address.Address;
import com.sarkhan.backend.model.cart.Cart;
import com.sarkhan.backend.model.cart.CartItem;
import com.sarkhan.backend.model.enums.OrderStatus;
import com.sarkhan.backend.model.order.Order;
import com.sarkhan.backend.model.order.OrderItem;
import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.model.product.items.Color;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.payment.service.PaymentService;
import com.sarkhan.backend.repository.address.AddressRepository;
import com.sarkhan.backend.repository.cart.CartItemRepository;
import com.sarkhan.backend.repository.cart.CartRepository;
import com.sarkhan.backend.repository.order.OrderItemRepository;
import com.sarkhan.backend.repository.order.OrderRepository;
import com.sarkhan.backend.repository.product.ProductRepository;
import com.sarkhan.backend.repository.user.UserRepository;
import com.sarkhan.backend.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final ProductRepository productRepository;
    private final PaymentService paymentService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;

    private User extractUser(String token) {
        String email = jwtService.extractEmail(token);
        return userRepository.findByEmail(email).orElseThrow(() -> {
            log.error("User can not found with this email:" + email);
            return new ResourceNotFoundException("User can not found with this email");
        });
    }

    @Override
    @Transactional
    public String createOrder(OrderRequest orderRequest, String token) throws NotEnoughQuantityException {
        User user = extractUser(token);
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> {
            log.error("Cart can not found for this user:" + user.getId());
            return new ResourceNotFoundException("Cart can not found for this user");
        });
        Address address = Address.builder()
                .finCode(orderRequest.getAddressRequestDto().getFinCode())
                .city(orderRequest.getAddressRequestDto().getCity())
                .region(orderRequest.getAddressRequestDto().getRegion())
                .street(orderRequest.getAddressRequestDto().getStreet())
                .postalCode(orderRequest.getAddressRequestDto().getPostalCode())
                .user(user)
                .build();
        addressRepository.save(address);

        List<CartItemRequestDTO> items = orderRequest.getItems();

        for (CartItemRequestDTO item : items) {
            Product product = productRepository.findById(item.getProductId()).orElseThrow(() -> {
                log.error("Product can not found");
                return new ResourceNotFoundException("Product can not found: ID = " + item.getProductId());
            });

            List<Color> colorVariants = product.getColors();
            Color colorVariant = colorVariants.stream()
                    .filter(c -> c.getColor().equalsIgnoreCase(item.getColor()))
                    .findFirst().orElseThrow(() -> {
                        log.error("Color can not found");
                        return new ResourceNotFoundException("Color can not found: ID = " + item.getColor());
                    });

            if (item.getQuantity() > colorVariant.getStock()) {
                log.error("Not enough quantity:" + item.getQuantity());
                throw new NotEnoughQuantityException("Not enough quantity");
            }
            colorVariant.setStock(colorVariant.getStock() - item.getQuantity());
            product.setSalesCount(product.getSalesCount() == null ?
                    item.getQuantity() : product.getSalesCount() + item.getQuantity());
            productRepository.save(product);
        }
        double sum = cart.getCartItems().stream().mapToDouble(i -> i.getTotalPrice().doubleValue()).sum();

        Order order = Order.builder()
                .address(address)
                .totalPrice(sum)
                .cart(cart)
                .orderStatus(OrderStatus.PENDING)
                .build();

        orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productId(cartItem.getProductId())
                    .quantity(cartItem.getQuantity())
                    .totalPrice(cartItem.getTotalPrice())
                    .color(cartItem.getColor())
                    .build();
            orderItems.add(orderItem);
        }
        orderItemRepository.saveAll(orderItems);

        cartItemRepository.deleteAll(cart.getCartItems());

        return paymentService.createInvoice(orderRequest, token);
    }

}
