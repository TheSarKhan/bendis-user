package com.sarkhan.backend.repository.cart;

import com.sarkhan.backend.model.cart.Cart;
import com.sarkhan.backend.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {
    Optional<Cart> findByUser(User user);

}
