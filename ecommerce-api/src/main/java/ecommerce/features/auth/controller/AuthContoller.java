package ecommerce.features.auth.controller;

import ecommerce.features.auth.dto.LoginRequest;
import ecommerce.features.auth.dto.MessageResponse;
import ecommerce.features.auth.dto.SignupRequest;
import ecommerce.features.auth.dto.UserResponse;
import ecommerce.features.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthContoller {

    private final AuthService authService;

    public AuthContoller(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerUser(request));
    }

    @PostMapping("/signin")
    public ResponseEntity<UserResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {
        return ResponseEntity.ok(authService.authenticateUser(loginRequest, response));
    }

    @PostMapping("/signout")
    public ResponseEntity<MessageResponse> logoutUser(HttpServletResponse response) {
        authService.logoutUser(response);
        return ResponseEntity.ok(new MessageResponse("Sesión cerrada exitosamente"));
    }

}
