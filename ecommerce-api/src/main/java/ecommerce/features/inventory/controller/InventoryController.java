package ecommerce.features.inventory.controller;


import ecommerce.features.inventory.dto.InventoryAdjustmentRequest;
import ecommerce.features.inventory.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {


    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/adjustment")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> adjustStock(
            @Valid @RequestBody InventoryAdjustmentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        // Extraemos el username (o email) real del token/sesión activo
        String currentAdmin = userDetails.getUsername();

        inventoryService.registerMovement(
                request.getProductId(),
                request.getQuantity(),
                request.getReason(),
                currentAdmin
        );

        return ResponseEntity.ok().build();
    }

}
