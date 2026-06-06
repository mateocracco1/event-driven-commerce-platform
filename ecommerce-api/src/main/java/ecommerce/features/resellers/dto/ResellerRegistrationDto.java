package ecommerce.features.resellers.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResellerRegistrationDto {
    // Datos de la cuenta (User)
    @NotBlank
    @Email
    private String email;
    @NotBlank @Size(min = 8)
    private String password;
    @NotBlank
    private String name;
    @NotBlank
    private String lastName;

    // Datos del negocio (ResellerProfile)
    private boolean isCompany;
    private String taxId;
    private String businessName;
}