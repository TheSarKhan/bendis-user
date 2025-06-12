package com.sarkhan.backend.repository.cart;

import com.sarkhan.backend.model.cart.Cart;
import com.sarkhan.backend.model.cart.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProductIdAndColor(Cart cart, Long productId, String color);
}
