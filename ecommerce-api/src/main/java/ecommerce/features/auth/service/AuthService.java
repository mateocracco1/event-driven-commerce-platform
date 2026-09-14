package ecommerce.features.auth.service;

import ecommerce.core.domain.Role;
import ecommerce.core.domain.User;
import ecommerce.core.exceptions.BusinessException;
import ecommerce.core.exceptions.ResourceNotFoundException;
import ecommerce.features.auth.dto.LoginRequest;
import ecommerce.features.auth.dto.SignupRequest;
import ecommerce.features.auth.dto.UserResponse;
import ecommerce.features.auth.mapper.UserMapper;
import ecommerce.core.repository.RoleRepository;
import ecommerce.core.repository.UserRepository;
import ecommerce.security.JwtUtils;
import ecommerce.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Transactional
    public UserResponse registerUser(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("The email is already registered: " + request.getEmail());
        }

        User user = userMapper.toEntity(request);
        user.setEnabled(true);
        user.setPassword(encoder.encode(request.getPassword()));

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new ResourceNotFoundException("ROLE_USER not found"));
        roles.add(userRole);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    public UserResponse authenticateUser(LoginRequest loginRequest, HttpServletResponse response) {

        // 1. Autenticación: El Manager busca al usuario (por email) y valida password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        // 2. Guardar en el contexto de seguridad
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Obtener el usuario principal (UserDetailsImpl)
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // 4. Generar la Cookie con el JWT
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        // 5. Agregar la cookie a la respuesta HTTP
        response.addHeader("Set-Cookie", jwtCookie.toString());

        // 6. Retornar el DTO de respuesta para el Front
        // Buscamos la entidad para mapearla a UserResponse
        User user = userRepository.findByEmail(userDetails.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userDetails.getEmail()));

        return userMapper.toResponse(user);
    }

    public void logoutUser(HttpServletResponse response) {
        // 1. Limpiamos el contexto de seguridad de Spring (opcional pero recomendado)
        SecurityContextHolder.clearContext();

        // 2. Generamos una cookie "vacía" y con tiempo de vida 0
        // Usamos el método que ya tenés en JwtUtils
        ResponseCookie cleanCookie = jwtUtils.getCleanJwtCookie();

        // 3. La adjuntamos al sobre (response)
        response.addHeader(HttpHeaders.SET_COOKIE, cleanCookie.toString());
    }

}