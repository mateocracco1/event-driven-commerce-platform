package ecommerce.features.users.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateDto {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;

    @NotBlank(message = "El apellido no puede estar vacío")
    private String lastName;
}