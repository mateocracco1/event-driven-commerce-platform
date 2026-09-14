package ecommerce.features.users.service;

import ecommerce.core.domain.User;
import ecommerce.core.exceptions.BusinessException;
import ecommerce.core.exceptions.ResourceNotFoundException;
import ecommerce.core.repository.UserRepository;
import ecommerce.features.users.dto.PasswordUpdateDto;
import ecommerce.features.users.dto.UserDto;
import ecommerce.features.users.dto.UserUpdateDto;
import ecommerce.features.users.mapper.UserManagementMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserManagementMapper userManagementMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("encodedPassword")
                .enabled(true)
                .build();

        userDto = new UserDto(1L, "john@example.com", "John", "Doe", Set.of("ROLE_USER"));
    }

    // ─── findAllByEnabledTrue ───────────────────────────────────────────────

    @Nested
    @DisplayName("findAllByEnabledTrue")
    class FindAllByEnabledTrue {

        @Test
        @DisplayName("returns mapped list when active users exist")
        void givenActiveUsers_whenFindAll_thenReturnsDtoList() {
            // Given
            when(userRepository.findAllByEnabledTrue()).thenReturn(List.of(user));
            when(userManagementMapper.toDto(user)).thenReturn(userDto);

            // When
            List<UserDto> result = userService.findAllByEnabledTrue();

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getEmail()).isEqualTo("john@example.com");
        }
    }

    // ─── findById ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("returns dto when user exists")
        void givenExistingId_whenFindById_thenReturnsDto() {
            // Given
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userManagementMapper.toDto(user)).thenReturn(userDto);

            // When
            UserDto result = userService.findById(1L);

            // Then
            assertThat(result.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when user does not exist")
        void givenMissingId_whenFindById_thenThrowsResourceNotFoundException() {
            // Given
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> userService.findById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    // ─── deleteUserById ────────────────────────────────────────────────────

    @Nested
    @DisplayName("deleteUserById")
    class DeleteUserById {

        @Test
        @DisplayName("soft-deletes user by setting enabled=false")
        void givenExistingId_whenDelete_thenSetsEnabledFalse() {
            // Given
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(user);

            // When
            userService.deleteUserById(1L);

            // Then
            assertThat(user.isEnabled()).isFalse();
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when user does not exist")
        void givenMissingId_whenDelete_thenThrowsResourceNotFoundException() {
            // Given
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> userService.deleteUserById(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(userRepository, never()).save(any());
        }
    }

    // ─── updatePassword ────────────────────────────────────────────────────

    @Nested
    @DisplayName("updatePassword")
    class UpdatePassword {

        @Test
        @DisplayName("updates password when current password matches")
        void givenCorrectCurrentPassword_whenUpdatePassword_thenSavesNewPassword() {
            // Given
            PasswordUpdateDto dto = new PasswordUpdateDto();
            dto.setCurrentPassword("rawPassword");
            dto.setNewPassword("newSecret123");

            when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);
            when(passwordEncoder.encode("newSecret123")).thenReturn("encodedNew");
            when(userRepository.save(any(User.class))).thenReturn(user);

            // When
            userService.updatePassword("john@example.com", dto);

            // Then
            assertThat(user.getPassword()).isEqualTo("encodedNew");
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("throws BusinessException when current password does not match")
        void givenWrongCurrentPassword_whenUpdatePassword_thenThrowsBusinessException() {
            // Given
            PasswordUpdateDto dto = new PasswordUpdateDto();
            dto.setCurrentPassword("wrongPassword");
            dto.setNewPassword("newSecret123");

            when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

            // When / Then
            assertThatThrownBy(() -> userService.updatePassword("john@example.com", dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("incorrect");
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when email does not exist")
        void givenUnknownEmail_whenUpdatePassword_thenThrowsResourceNotFoundException() {
            // Given
            PasswordUpdateDto dto = new PasswordUpdateDto();
            dto.setCurrentPassword("any");
            dto.setNewPassword("newSecret123");

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> userService.updatePassword("ghost@example.com", dto))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // ─── updateProfileByEmail ──────────────────────────────────────────────

    @Nested
    @DisplayName("updateProfileByEmail")
    class UpdateProfileByEmail {

        @Test
        @DisplayName("updates name and lastName and returns mapped dto")
        void givenValidUpdate_whenUpdateProfileByEmail_thenReturnsMappedDto() {
            // Given
            UserUpdateDto updateDto = new UserUpdateDto();
            updateDto.setName("Jane");
            updateDto.setLastName("Smith");

            when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(user);
            when(userManagementMapper.toDto(user)).thenReturn(userDto);

            // When
            UserDto result = userService.updateProfileByEmail("john@example.com", updateDto);

            // Then
            assertThat(user.getName()).isEqualTo("Jane");
            assertThat(user.getLastName()).isEqualTo("Smith");
            assertThat(result).isNotNull();
        }
    }
}
