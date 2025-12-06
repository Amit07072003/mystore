package org.mystore.controllers;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.mystore.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Slf4j
@RestController
@RequestMapping("/razorpayWebhook")
public class WebhookController {

    @Autowired
    private OrderService orderService;

    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    @PostMapping
    public void listenToEvents(
            @RequestBody String payload,
            @RequestHeader(value = "X-Razorpay-Signature", required = false) String razorpaySignature) {

        try {
            log.info("===== 📩 Received Razorpay Webhook Event =====");

            // Verify Razorpay signature
            if (!isSignatureValid(payload, razorpaySignature)) {
                log.warn("⚠️ Invalid Razorpay Webhook signature!");
                return;
            }

            JSONObject json = new JSONObject(payload);
            String event = json.getString("event");

            if ("payment.captured".equals(event)) {
                JSONObject paymentEntity = json.getJSONObject("payload")
                        .getJSONObject("payment")
                        .getJSONObject("entity");

                // Extract custom reference ID from notes
                JSONObject notes = paymentEntity.optJSONObject("notes");
                String referenceId = null;
                Long userId = 0L;
                if (notes != null) {
                    referenceId = notes.optString("razorpay_order_id", null);
                    userId = notes.optLong("user_id", 0L);
                }

                Long amount = paymentEntity.getLong("amount"); // paise
                String status = paymentEntity.getString("status");

                log.info("✅ Payment Captured | ReferenceId: {} | Amount: {} | Status: {}",
                        referenceId, amount, status);

                // Process the order using the reference ID
                if (referenceId != null && !referenceId.isEmpty()) {
                    orderService.processSuccessfulPayment(referenceId, amount, userId);
                } else {
                    log.warn("⚠️ No reference ID found in payment notes!");
                }

            } else {
                log.info("ℹ️ Unhandled event type: {}", event);
            }

        } catch (Exception e) {
            log.error("❌ Error handling Razorpay webhook: ", e);
        }
    }

    // Verify Webhook Signature
    private boolean isSignatureValid(String payload, String razorpaySignature) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(webhookSecret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            byte[] hash = sha256_HMAC.doFinal(payload.getBytes());
            String generatedSignature = bytesToHex(hash);

            return generatedSignature.equals(razorpaySignature);
        } catch (Exception e) {
            log.error("❌ Error verifying signature: ", e);
            return false;
        }
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
