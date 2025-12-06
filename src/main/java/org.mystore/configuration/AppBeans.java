package org.mystore.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class AppBeans {

    @Bean
    public SecretKey secretKey() {
        // Must be at least 256-bit
        String key = "YOUR_SECRET_KEY_256_BIT_MINIMUM_CHANGE_THIS";
        return new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
