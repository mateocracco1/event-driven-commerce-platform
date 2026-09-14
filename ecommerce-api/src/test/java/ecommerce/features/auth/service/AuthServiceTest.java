package ecommerce.features.auth.service;

import ecommerce.core.domain.Role;
import ecommerce.core.domain.User;
import ecommerce.core.exceptions.BusinessException;
import ecommerce.core.exceptions.ResourceNotFoundException;
import ecommerce.core.repository.RoleRepository;
import ecommerce.core.repository.UserRepository;
import ecommerce.features.auth.dto.SignupRequest;
import ecommerce.features.auth.dto.UserResponse;
import ecommerce.features.auth.mapper.UserMapper;
import ecommerce.security.JwtUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    private User user;
    private Role roleUser;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        roleUser = new Role();
        roleUser.setName("ROLE_USER");

        user = User.builder()
                .id(1L)
                .name("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("encodedPassword")
                .enabled(true)
                .roles(new HashSet<>(Set.of(roleUser)))
                .build();

        userResponse = new UserResponse(1L, "john@example.com", "John", "Doe", Set.of("ROLE_USER"));
    }

    // ─── registerUser ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("registerUser")
    class RegisterUser {

        @Test
        @DisplayName("registers user and returns response when email is not taken")
        void givenFreshEmail_whenRegister_thenReturnsUserResponse() {
            // Given
            SignupRequest request = new SignupRequest("John", "Doe", "john@example.com", "secret123");

            when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
            when(userMapper.toEntity(request)).thenReturn(user);
            when(encoder.encode("secret123")).thenReturn("encodedPassword");
            when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(roleUser));
            when(userRepository.save(user)).thenReturn(user);
            when(userMapper.toResponse(user)).thenReturn(userResponse);

            // When
            UserResponse result = authService.registerUser(request);

            // Then
            assertThat(result.getEmail()).isEqualTo("john@example.com");
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("throws BusinessException when email is already registered")
        void givenDuplicateEmail_whenRegister_thenThrowsBusinessException() {
            // Given
            SignupRequest request = new SignupRequest("John", "Doe", "john@example.com", "secret123");
            when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

            // When / Then
            assertThatThrownBy(() -> authService.registerUser(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("already registered");
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when ROLE_USER is missing from DB")
        void givenMissingRole_whenRegister_thenThrowsResourceNotFoundException() {
            // Given
            SignupRequest request = new SignupRequest("John", "Doe", "john@example.com", "secret123");

            when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
            when(userMapper.toEntity(request)).thenReturn(user);
            when(encoder.encode("secret123")).thenReturn("encodedPassword");
            when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> authService.registerUser(request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("ROLE_USER");
            verify(userRepository, never()).save(any());
        }
    }

    // ─── logoutUser ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("logoutUser")
    class LogoutUser {

        @Test
        @DisplayName("adds clean cookie header to response")
        void givenValidRequest_whenLogout_thenAddsCleanCookieHeader() {
            // Given
            HttpServletResponse response = mock(HttpServletResponse.class);
            ResponseCookie cleanCookie = ResponseCookie.from("jwt", "").maxAge(0).build();
            when(jwtUtils.getCleanJwtCookie()).thenReturn(cleanCookie);

            // When
            authService.logoutUser(response);

            // Then
            verify(jwtUtils).getCleanJwtCookie();
            verify(response).addHeader("Set-Cookie", cleanCookie.toString());
        }
    }
}
