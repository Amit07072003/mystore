package org.mystore.repositories;

import org.mystore.models.Order;
import org.mystore.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // ✅ Find the order by Razorpay reference/order ID (as String)
    Order findByRazorpayOrderId(String razorpayOrderId);

    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderItems i WHERE i.product.seller.id = :sellerId")
    List<Order> findOrdersBySellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.orderDate DESC")
    List<Order> findOrdersByUserId(@Param("userId") Long userId);




    Optional<Order> findById(Long id);

    // ✅ Custom query to fetch amount
    @Query("SELECT o.totalAmount FROM Order o WHERE o.razorpayOrderId = :razorpayOrderId")
    Long findAmountByRazorpayOrderId(@Param("razorpayOrderId") String razorpayOrderId);

    // ✅ Fetch user ID directly
    @Query("SELECT o.user.id FROM Order o WHERE o.razorpayOrderId = :razorpayOrderId")
    Long findUserIdByRazorpayOrderId(@Param("razorpayOrderId") String razorpayOrderId);
}
