package org.mystore.dtos;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {
    private Long id;
    private String razorpayOrderId;
    private LocalDateTime orderDate;
    private Long totalAmount;
    private String status;

    private Long userId;
    private String userEmail;

    private List<OrderItemDTO> items;
}
