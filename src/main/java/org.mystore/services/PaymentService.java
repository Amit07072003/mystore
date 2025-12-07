package org.mystore.services;

import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.mystore.models.Order;
import org.mystore.models.User;
import org.mystore.repositories.OrderRepository;
import org.mystore.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private UserRepo userRepository;

    private final OrderRepository orderRepository;
    private final RazorpayClient client;

    public PaymentService(OrderRepository orderRepository,
                          @Value("${razorpay.key_id}") String keyId,
                          @Value("${razorpay.key_secret}") String keySecret) throws Exception {
        this.orderRepository = orderRepository;
        this.client = new RazorpayClient(keyId.trim(), keySecret.trim());
    }

    public String initiatePayment(Long amountInRupees, String phone, String email, String userName) {
        // fetch user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {

            System.out.println("amountInRupees in payment service: " + amountInRupees);
            // Generate unique reference
            String razorPayOrderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8);

            // Save order in DB in rupees
            Order order = new Order();
            order.setStatus("PENDING");
            order.setOrderDate(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
            order.setTotalAmount(amountInRupees); // save in rupees
            order.setUser(user);
            order = orderRepository.save(order);

            // Prepare Razorpay payment link (amount in paise)
            JSONObject paymentLinkRequest = new JSONObject();
            paymentLinkRequest.put("amount", amountInRupees * 100); // convert to paise for Razorpay
            paymentLinkRequest.put("currency", "INR");
           String uniqueRefId = "REF-" + UUID.randomUUID().toString().substring(0, 8);
paymentLinkRequest.put("reference_id", uniqueRefId);

            paymentLinkRequest.put("description", "Payment for Order #" + order.getId());

            JSONObject customer = new JSONObject();
            customer.put("name", userName);
            customer.put("contact", phone);
            customer.put("email", email);
            paymentLinkRequest.put("customer", customer);

            JSONObject notify = new JSONObject();
            notify.put("sms", true);
            notify.put("email", true);
            paymentLinkRequest.put("notify", notify);
            paymentLinkRequest.put("reminder_enable", true);

            JSONObject notes = new JSONObject();
            notes.put("razorpay_order_id", razorPayOrderId);
            notes.put("user_id", order.getUser().getId());
            paymentLinkRequest.put("notes", notes);

          paymentLinkRequest.put("callback_url", "https://mystore-2aa1.onrender.com/api/payment/success");
paymentLinkRequest.put("callback_method", "get");


            PaymentLink payment = client.paymentLink.create(paymentLinkRequest);

            // Update order with Razorpay ID
            order.setRazorpayOrderId(razorPayOrderId);
            orderRepository.save(order);

            return payment.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Failed to create payment link: " + e.getMessage() + "\"}";
        }
    }
}
