package ecommerce.features.auth.service;

import ecommerce.core.domain.User;
import ecommerce.core.repository.UserRepository;
import ecommerce.features.auth.domain.PasswordResetToken;
import ecommerce.features.auth.dto.ResetPasswordRequest;
import ecommerce.features.auth.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void createPasswordResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No existe un usuario con ese email"));

        tokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();

        PasswordResetToken myToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(15)) // 15 minutos de vida
                .build();

        tokenRepository.save(myToken);

        // Enviamos el mail (Acá es donde usamos tu configuración de Gmail)
        sendResetEmail(user.getEmail(), token);
    }
    private void sendResetEmail(String userEmail, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(userEmail);
        message.setSubject("Recuperación de Contraseña - Ecommerce");
        message.setText("Para resetear tu contraseña, haz clic en el siguiente enlace: " +
                "http://localhost:4200/reset-password?token=" + token); // URL de tu Front

        mailSender.send(message);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {

        PasswordResetToken resetToken = tokenRepository.findByToken(request.token())
                .orElseThrow(() -> new RuntimeException("Token inválido"));


        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new RuntimeException("El token ha expirado");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        // 4. Borrar el token para que sea un solo uso
        tokenRepository.delete(resetToken);
    }
}
