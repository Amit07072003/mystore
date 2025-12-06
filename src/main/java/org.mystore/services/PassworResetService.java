package org.mystore.services;

import org.mystore.models.PasswordResetToken;
import org.mystore.models.User;
import org.mystore.repositories.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PassworResetService {
    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    public void createOrUpdatePasswordResetToken(User user, String token) {
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(1);

        PasswordResetToken existingToken = tokenRepository.findByUser(Optional.ofNullable(user)).orElse(null);

        if (existingToken != null) {
            existingToken.setToken(token);
            existingToken.setExpiryDate(expiryDate);
            tokenRepository.save(existingToken);
        } else {
            PasswordResetToken newToken = new PasswordResetToken(token, user, expiryDate);
            tokenRepository.save(newToken);
        }
    }
}
