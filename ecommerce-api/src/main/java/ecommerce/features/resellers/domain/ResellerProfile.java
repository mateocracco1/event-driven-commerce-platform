package ecommerce.features.resellers.domain;

import ecommerce.core.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "reseller_profiles")
@Getter @Setter
public class ResellerProfile {

    @Id
    private Long id; // No lleva @GeneratedValue porque usaremos @MapsId

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private String businessName; // Si es empresa, sino queda vacío
    private String taxId;      // CUIT/CUIL
    private boolean isCompany;

    @Enumerated(EnumType.STRING)
    private ResellerStatus status = ResellerStatus.PENDING;
}