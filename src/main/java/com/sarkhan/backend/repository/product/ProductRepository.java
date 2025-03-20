package com.sarkhan.backend.repository.product;

import com.sarkhan.backend.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< HEAD

=======
import org.springframework.stereotype.Repository;

@Repository
>>>>>>> 75aa8255132ebcfbcb66f2c9fae51d6015e630d9
public interface ProductRepository extends JpaRepository<Product, Long> {
}
