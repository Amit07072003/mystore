package org.mystore.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableMethodSecurity(prePostEnabled = true) // important for @PreAuthorize
public class AuthConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // ---------- Public endpoints ----------

                                // ---------- Public endpoints ----------
                        .requestMatchers(
                                "/auth/**",
                                "/api/auth/**",
                                "/login",
                                "/register",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/validateToken",
                                "/razorpayWebhook",
                                "/home",
                                "/cart",
                                "/product-detail",
                                
                                "/search",
                                "/about", 
                            "/swagger-ui/**",
                            "/v3/api-docs/**",
                            "/v2/api-docs/**",
                                
                                 "/api/payment/webhook",
                                
                            
                             "/api/payment/success",
        "/api/payment/success/**",
        "/api/payment/webhook",
        "/api/payment/webhook/**",
        "/api/payment/callback",
        "/api/payment/callback/**"
                        ).permitAll()



                        // ---------- Forgot/reset password (both GET + POST) ----------
                        .requestMatchers("/forgot-password/**").permitAll()
                        .requestMatchers("/resetpassword/**").permitAll()
                        .requestMatchers("/test-email").permitAll()

                        // ---------- Public read-only APIs ----------
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/categories/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/search/**").permitAll()

                        // ---------- Product management (Admin & Seller) ----------
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasAnyRole("ADMIN","SELLER")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAnyRole("ADMIN","SELLER")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAnyRole("ADMIN","SELLER")
                        .requestMatchers(HttpMethod.GET, "/products/view/create").hasAnyRole("ADMIN","SELLER")
                        .requestMatchers(HttpMethod.POST, "/products/view/create").hasAnyRole("ADMIN","SELLER")
                        .requestMatchers("/products/view/edit/**").hasAnyRole("ADMIN","SELLER")
                        .requestMatchers("/products/view/**").authenticated()

                        // ---------- Category management (Admin only) ----------
                        .requestMatchers(HttpMethod.POST, "/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/categories/**").hasRole("ADMIN")

                        // ---------- Cart operations ----------
                        .requestMatchers(HttpMethod.GET,"/api/cart/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/cart/count/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/cart/add/**").hasRole("USER")
                        .requestMatchers(HttpMethod.POST,"/api/cart/remove/**").hasRole("USER")
                        .requestMatchers(HttpMethod.POST,"/api/cart/increase/**").hasRole("USER")
                        .requestMatchers(HttpMethod.POST,"/api/cart/decrease/**").hasRole("USER")
                        .requestMatchers(HttpMethod.POST,"/api/payment/checkout").hasRole("USER")

                        // ---------- Admin endpoints ----------
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/admin/dashboard").hasRole("ADMIN")
                        .requestMatchers("/buyer/dashboard").hasRole("USER")
                        .requestMatchers("/seller/dashboard").hasRole("SELLER")

                        // ---------- All others require authentication ----------
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    // CORS config
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(
                                 "http://localhost:8080",
    "https://evelyn-unrewardable-siena.ngrok-free.dev",
    "https://mystore-2aa1.onrender.com"
                        )
                        .allowedMethods("GET","POST","DELETE","PUT")
                        .allowCredentials(true);
            }
        };
    }
}
