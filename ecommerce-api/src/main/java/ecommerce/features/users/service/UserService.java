package ecommerce.features.users.service;

import ecommerce.core.domain.User;
import ecommerce.core.repository.UserRepository;
import ecommerce.features.users.dto.PasswordUpdateDto;
import ecommerce.features.users.dto.UserDto;
import ecommerce.features.users.dto.UserUpdateDto;
import ecommerce.features.users.mapper.UserManagementMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserManagementMapper userManagementMapper;
    private  final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UserDto> findAllByEnabledTrue(){
        return userRepository.findAllByEnabledTrue().stream()
                .map(userManagementMapper::toDto)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userManagementMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional (readOnly = true)
    public UserDto findById(Long userId){
        User user = findUserOrThrow(userId);
        return userManagementMapper.toDto(user);
    }



    @Transactional
    public UserDto updateUserProfile(Long id, UserUpdateDto updateDto) {
        User user = findUserOrThrow(id);
        userManagementMapper.updateEntityFromDto(updateDto, user);
        User updatedUser = userRepository.save(user);
        return userManagementMapper.toDto(updatedUser);
    }


    @Transactional
    public void deleteUserById(Long userId) {
        User user = findUserOrThrow(userId);
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserDto findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
        return userManagementMapper.toDto(user);
    }


    @Transactional
    public void updatePassword(String email, PasswordUpdateDto passwordDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (!passwordEncoder.matches(passwordDto.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }
        user.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public UserDto updateProfileByEmail(String email, UserUpdateDto updateDto) {
        // 1. Buscás al usuario por el email (que es único)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setName(updateDto.getName());
        user.setLastName(updateDto.getLastName());

        return userManagementMapper.toDto(userRepository.save(user));
    }


    @Transactional(readOnly = true)
    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));
    }

}
