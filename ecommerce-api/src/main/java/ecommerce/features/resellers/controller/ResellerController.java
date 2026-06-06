package ecommerce.features.resellers.controller;


import ecommerce.features.resellers.dto.ResellerRegistrationDto;
import ecommerce.features.resellers.service.ResellerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/resellers")
@RequiredArgsConstructor
public class ResellerController {


    private final ResellerService resellerService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody ResellerRegistrationDto dto) {
        resellerService.register(dto);
        return ResponseEntity.ok("Solicitud de revendedor registrada con éxito");
    }
}
