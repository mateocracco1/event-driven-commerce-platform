package ecommerce.features.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MercadoPagoWebhookNotification(
        String action,
        String type,
        @JsonProperty("data")
        Data data
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Data(
            String id
    ) {}
}