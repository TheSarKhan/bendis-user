package com.sarkhan.backend.service.impl;

import com.sarkhan.backend.dto.cart.CartItemRequestDTO;
import com.sarkhan.backend.dto.order.OrderRequest;
import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.model.product.items.Color;
import com.sarkhan.backend.payment.service.PaymentService;
import com.sarkhan.backend.repository.product.ProductRepository;
import com.sarkhan.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ProductRepository productRepository;
    private final PaymentService paymentService;

    public String createOrder(OrderRequest orderRequest,String token) {
        List<CartItemRequestDTO> items = orderRequest.getItems();

        for (CartItemRequestDTO item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Ürün bulunamadı: ID = " + item.getProductId()));

            List<Color> colorVariants = product.getColors();
            Optional<Color> matchedColor = colorVariants.stream()
                    .filter(c -> c.getColor().equalsIgnoreCase(item.getColor()))
                    .findFirst();

            if (matchedColor.isPresent()) {
                Color colorVariant = matchedColor.get();
                if (item.getQuantity() <= colorVariant.getStock()) {
                    colorVariant.setStock(colorVariant.getStock() - item.getQuantity());
                    productRepository.save(product);
                    System.out.println("Payment Service ise dusdu");
                   return paymentService.createInvoice(orderRequest,token);
                } else {
                    System.err.println("Yetersiz stok: " + item.getColor() + " için istenen = "
                            + item.getQuantity() + ", mevcut = " + colorVariant.getStock());
                }
            } else {
                System.err.println("Renk bulunamadı: " + item.getColor() + " (Ürün ID: " + item.getProductId() + ")");
            }
        }

        System.out.println("Order created successfully");
        return "Success";
    }
}
