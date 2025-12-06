package org.mystore.repositories;

import org.mystore.models.PasswordResetToken;
import org.mystore.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByUser(Optional<User> user);

    Optional<PasswordResetToken> findByToken(String token); // ✅ Add this line
}
