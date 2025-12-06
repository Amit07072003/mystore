package org.mystore.dtos;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InitiatePaymentRequestDto {
    private Long amount;       // in rupees, we’ll convert to paise inside service
    private String currency;  // e.g., "INR"
    private String name;      // customer name
    private String email;     // customer email
    private String phone;     // customer phone
    private Long orderId; // unique order reference
}
