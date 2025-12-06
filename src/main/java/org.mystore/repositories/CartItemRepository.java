package org.mystore.repositories;

import org.mystore.models.Cart;
import org.mystore.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository



public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCartId(Long cartId);
    void deleteAllByProductId(Long productId);

    int countByCart(Cart cart);



}
