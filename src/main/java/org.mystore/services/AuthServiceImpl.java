package org.mystore.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import org.antlr.v4.runtime.misc.Pair;
import org.mystore.dtos.UserDTO;
import org.mystore.exception.PasswordMissmatchException;
import org.mystore.exception.UserNotSignedUpException;
import org.mystore.models.*;
import org.mystore.repositories.RoleRepo;
import org.mystore.repositories.SessionRepo;
import org.mystore.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements IAuthService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SecretKey secretKey;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private SessionRepo sessionRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Override
    public User signup(String name, String email, String password, String phoneNumber) {
        if (userRepo.findByEmail(email).isPresent()) throw new RuntimeException("Email already in use");

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setPhoneNumber(phoneNumber);

        Role role = roleRepo.findByRoleName("USER");
        if (role == null) {
            role = new Role();
            role.setRoleName("USER");
            roleRepo.save(role);
        }
        user.setRoleList(Collections.singletonList(role));
        userRepo.save(user);
        return user;
    }

    @Override
    public Pair<User, String> login(String email, String password) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotSignedUpException("Please create an account first"));

        if (!bCryptPasswordEncoder.matches(password, user.getPassword()))
            throw new PasswordMissmatchException("Password mismatch");

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());
        claims.put("scope", user.getRoleList());
        claims.put("gen", System.currentTimeMillis());
        claims.put("exp", System.currentTimeMillis() + 100000);

        String token = Jwts.builder()
                .setClaims(claims)
                .signWith(secretKey)
                .compact();

        Session session = new Session();
        session.setToken(token);
        session.setUser(user);
        session.setState(SessionState.ACTIVE);
        sessionRepo.save(session);

        return new Pair<>(user, token);
    }

    @Override
    public String validateToken(String token) {
        Optional<Session> sessionOpt = sessionRepo.findByToken(token);
        if (sessionOpt.isEmpty()) return "token not found";

        Session session = sessionOpt.get();

        try {
            String[] parts = token.split("\\.");
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            Map<String, Object> claims = new ObjectMapper().readValue(payload, Map.class);

            Long exp = ((Number) claims.get("exp")).longValue();
            if (System.currentTimeMillis() > exp) {
                session.setState(SessionState.EXPIRED);
                sessionRepo.save(session);
                return "invalid token";
            }
            return (String) claims.get("email");
        } catch (Exception e) {
            return "invalid token";
        }
    }

//    @Override
//    public UserDTO getUserFromToken(String token) {
//        try {
//            String payload = new String(Base64.getUrlDecoder().decode(token.split("\\.")[1]));
//            Map<String, Object> claims = new ObjectMapper().readValue(payload, Map.class);
//            UserDTO user = new UserDTO();
//            user.setId(((Number) claims.get("userId")).longValue());
//            user.setEmail((String) claims.get("email"));
//            user.setRoles((List<String>) claims.get("roleName"));
//
//
//            return user;
//        } catch (Exception e) {
//            return null;
//        }
//    }
@Override
public UserDTO getUserFromToken(String token) {
    try {
        String payload = new String(Base64.getUrlDecoder().decode(token.split("\\.")[1]));
        Map<String, Object> claims = new ObjectMapper().readValue(payload, Map.class);

        UserDTO user = new UserDTO();
        user.setId(((Number) claims.get("userId")).longValue());
        user.setEmail((String) claims.get("email"));

        // ✅ Extract roles from "scope"
        List<Map<String, Object>> scope = (List<Map<String, Object>>) claims.get("scope");
        if (scope != null) {
            List<String> roles = scope.stream()
                    .map(item -> (String) item.get("roleName"))
                    .collect(Collectors.toList());
            user.setRoles(roles);
        } else {
            user.setRoles(Collections.emptyList());
        }

        return user;
    } catch (Exception e) {
        return null;
    }
}

}
