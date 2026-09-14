package ecommerce.features.resellers.service;

import ecommerce.core.domain.User;
import ecommerce.core.repository.UserRepository;
import ecommerce.features.resellers.domain.ResellerProfile;
import ecommerce.features.resellers.dto.ResellerRegistrationDto;
import ecommerce.features.resellers.repository.ResellerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResellerService {

    private final UserRepository userRepository;
    private final ResellerProfileRepository resellerRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(ResellerRegistrationDto dto) {

        User user = new User();
        user.setName(dto.getName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEnabled(true);

        user = userRepository.save(user);

        ResellerProfile profile = new ResellerProfile();
        profile.setUser(user);
        profile.setCompany(dto.isCompany());
        profile.setTaxId(dto.getTaxId());
        profile.setBusinessName(dto.isCompany() ? dto.getBusinessName() : null);

        resellerRepository.save(profile);

    }

}
// Punto a tener en cuenta para el futuro:
// En el registro creás el usuario básico y el perfil, pero no
// le asignás roles explícitos en user.getRoles().add(...) (como ROLE_USER).
// Cuando agregues la feature de aprobación del admin,
// podrás asignarle el rol correspondiente (ROLE_RESELLER).