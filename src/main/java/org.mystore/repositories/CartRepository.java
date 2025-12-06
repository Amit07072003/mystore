package org.mystore.repositories;

import org.mystore.models.Cart;
import org.mystore.models.CartItem;
import org.mystore.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);


//    Optional<Cart> findByUser(User user);
    Cart findByUser(User user);



//    @Query("SELECT SUM(c.quantity) FROM CartItem c WHERE c.user.id = :userId")
//    Integer countItemsByUserId(@Param("userId") Long userId);

}