package org.mystore.controllers;

import org.mystore.models.Order;
import org.mystore.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PaymentSuccessGet {

    private final OrderService orderService;

    @Autowired
    public PaymentSuccessGet(OrderService orderService) {
        this.orderService = orderService;
    }


    @GetMapping("/api/payment/success")
    public String paymentSuccess(
            @RequestParam("razorpay_payment_id") String razorpayPaymentId,
            @RequestParam("razorpay_payment_link_id") String razorpayPaymentLinkId,
            @RequestParam("razorpay_payment_link_reference_id") Long razorpayReferenceId,
            @RequestParam("razorpay_payment_link_status") String razorpayStatus,
            @RequestParam("razorpay_signature") String razorpaySignature,
            Model model
    ) {
        System.out.println("payment success controller");

        Long amount = 0L;
        Long userId = null;

        if ("paid".equalsIgnoreCase(razorpayStatus)) {
            Order order = orderService.getOrderById(razorpayReferenceId); // fetch order entity
            if (order != null) {
                amount = order.getTotalAmount();
//                userId = order.getUser().getId(); // assuming Order has a User object
//                orderService.processSuccessfulPayment(order.getId(), amount, userId);
            }
        }

        // Pass all values to Thymeleaf
        model.addAttribute("razorpayPaymentId", razorpayPaymentId);
        model.addAttribute("razorpayPaymentLinkId", razorpayPaymentLinkId);
        model.addAttribute("razorpayReferenceId", razorpayReferenceId);
        model.addAttribute("razorpayStatus", razorpayStatus);
        model.addAttribute("razorpaySignature", razorpaySignature);
        model.addAttribute("amount", amount);

        return "order-success";
    }
}
