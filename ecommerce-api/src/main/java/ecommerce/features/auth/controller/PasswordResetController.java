package ecommerce.features.auth.controller;

import ecommerce.core.repository.UserRepository;
import ecommerce.features.auth.dto.ForgotPasswordRequest;
import ecommerce.features.auth.dto.ResetPasswordRequest;
import ecommerce.features.auth.repository.PasswordResetTokenRepository;
import ecommerce.features.auth.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;


    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.createPasswordResetToken(request.email());
        return ResponseEntity.ok("Si el correo existe, se ha enviado un enlace de recuperación. :D ");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
            passwordResetService.resetPassword(request);
        return ResponseEntity.ok("Contraseña actualizada con éxito.");
    }


}
