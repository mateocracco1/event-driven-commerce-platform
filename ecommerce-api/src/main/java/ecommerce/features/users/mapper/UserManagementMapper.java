package ecommerce.features.users.mapper;
import ecommerce.features.users.dto.UserDto;
import ecommerce.core.domain.User;
import ecommerce.features.users.dto.UserUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserManagementMapper {


    // Para el GET (Listar y buscar por ID)
    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRoles")
    UserDto toDto(User user);

    // Para el PUT (Actualizar perfil)
    // El @MappingTarget hace que MapStruct actualice el usuario que ya existe en la DB
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    void updateEntityFromDto(UserUpdateDto dto, @MappingTarget User user);

    @Named("mapRoles")
    default Set<String> mapRoles(Set<ecommerce.core.domain.Role> roles) {
        if (roles == null) return null;
        return roles.stream()
                .map(role -> role.getName()) // Asumiendo quegetName() es el String
                .collect(Collectors.toSet());
    }

}
