package ecommerce.features.auth.mapper;

import ecommerce.core.domain.User; // Importa desde el core
import ecommerce.core.domain.Role; // Importa desde el core
import ecommerce.features.auth.dto.SignupRequest;
import ecommerce.features.auth.dto.UserResponse;
import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    User toEntity(SignupRequest request);

    // Este sigue siendo clave para la SALIDA
    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRoles")
    UserResponse toResponse(User user);

    @Named("mapRoles")
    default Set<String> mapRoles(Set<Role> roles) {
        if (roles == null)
            return null;
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
}