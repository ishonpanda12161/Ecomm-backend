package com.ecom.backend.repository;

import com.ecom.backend.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id=?1 AND ci.product.id=?2")
    Optional<CartItem> findByCartIdAndProductId(Long CartId, Long productId);
    void deleteByProductId(Long productId);
}
