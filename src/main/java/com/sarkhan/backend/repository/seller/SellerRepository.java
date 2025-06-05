package com.sarkhan.backend.repository.seller;

import com.sarkhan.backend.model.user.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller,Long> {
}
