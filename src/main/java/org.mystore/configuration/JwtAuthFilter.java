package org.mystore.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final SecretKey secretKey;

    public JwtAuthFilter(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = null;
        String authHeader = request.getHeader("Authorization");

        // 1️⃣ Check Authorization header
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        // 2️⃣ Check cookie
        else if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token != null) {
            try {
                // Simple JWT parsing (without library)
                String[] parts = token.split("\\.");
                if (parts.length != 3) throw new RuntimeException("Invalid JWT");

                String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
                String signature = parts[2];

                // Verify signature
                Mac hmac = Mac.getInstance("HmacSHA256");
                hmac.init(secretKey);
                String signedData = parts[0] + "." + parts[1];
                byte[] hash = hmac.doFinal(signedData.getBytes(StandardCharsets.UTF_8));
                String expectedSignature = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);

                if (!expectedSignature.equals(signature)) {
                    throw new RuntimeException("Invalid JWT signature");
                }

                // Parse claims
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> claims = mapper.readValue(payload, Map.class);

                String email = (String) claims.get("email");

                // Correct role extraction
                List<Map<String, Object>> scopeList = (List<Map<String, Object>>) claims.get("scope");
                List<GrantedAuthority> authorities = new ArrayList<>();
                if (scopeList != null) {
                    for (Map<String, Object> roleObj : scopeList) {
                        String roleName = (String) roleObj.get("roleName");
                        if (roleName != null) {
                            authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));
                        }
                    }
                }

                // Set authentication in context
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(email, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception e) {
                System.out.println("JWT validation failed: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
