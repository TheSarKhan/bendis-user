package com.sarkhan.backend.service.impl;

import com.sarkhan.backend.dto.cart.CartItemRequestDTO;
import com.sarkhan.backend.dto.order.OrderRequest;
import com.sarkhan.backend.exception.DataNotFoundException;
import com.sarkhan.backend.jwt.JwtService;
import com.sarkhan.backend.model.order.OrderStatus;
import com.sarkhan.backend.model.order.Order;
import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.model.product.items.ColorAndSize;
import com.sarkhan.backend.model.user.Seller;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.payment.service.PaymentService;
import com.sarkhan.backend.repository.order.OrderRepository;
import com.sarkhan.backend.repository.product.ProductRepository;
import com.sarkhan.backend.repository.user.UserRepository;
import com.sarkhan.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final ProductRepository productRepository;
    private final PaymentService paymentService;
    private final OrderRepository orderRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public String createOrder(OrderRequest orderRequest, String token) {
        List<CartItemRequestDTO> items = orderRequest.getItems();

        for (CartItemRequestDTO item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Ürün bulunamadı: ID = " + item.getProductId()));

            List<ColorAndSize> colorAndSizeVariants = product.getColorAndSizes();
            Optional<ColorAndSize> matchedColor = colorAndSizeVariants.stream()
                    .filter(c -> c.getColorAndSize().equalsIgnoreCase(item.getColor()))
                    .findFirst();

            if (matchedColor.isPresent()) {
                ColorAndSize colorAndSizeVariant = matchedColor.get();
                if (item.getQuantity() <= colorAndSizeVariant.getStock()) {
                    colorAndSizeVariant.setStock(colorAndSizeVariant.getStock() - item.getQuantity());
                    productRepository.save(product);
                    System.out.println("Payment Service ise dusdu");
                    return paymentService.createInvoice(orderRequest, token);
                } else {
                    System.err.println("Yetersiz stok: " + item.getColor() + " için istenen = "
                                       + item.getQuantity() + ", mevcut = " + colorAndSizeVariant.getStock());
                }
            } else {
                System.err.println("Renk bulunamadı: " + item.getColor() + " (Ürün ID: " + item.getProductId() + ")");
            }
        }

        System.out.println("Order created successfully");
        return "Success";
    }

    public List<Order> getOrdersBySellersAndFilters(String token, OrderStatus status, LocalDate orderDate) throws DataNotFoundException {
        String email = jwtService.extractEmail(token);
        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            log.error("User can not found");
            return new DataNotFoundException("User can not found");
        });
        Seller seller = user.getSeller();
        if (seller == null) {
            throw new DataNotFoundException("Seller can not found");
        }
        return orderRepository.findBySellerAndFilters(seller.getId(), status, orderDate);
    }
}
