package ecommerce.features.payment.infrastructure.mercadopago;

import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import ecommerce.features.order.domain.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MercadoPagoPreferenceService {

    @Value("${mercadopago.webhook-url}")
    private String webhookUrl;

    public String createPaymentPreference(Order order) {
        PreferenceClient client = new PreferenceClient();

        List<PreferenceItemRequest> items = order.getItems().stream()
                .map(item -> PreferenceItemRequest.builder()
                        .id(item.getProductId().toString())
                        .title(item.getProductName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .currencyId("ARS")
                        .build())
                .toList();

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success("http://localhost:4200/checkout/success")
                .pending("http://localhost:4200/checkout/pending")
                .failure("http://localhost:4200/checkout/failure")
                .build();

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(items)
                .externalReference(order.getId().toString())
                .notificationUrl(webhookUrl)
                .backUrls(backUrls)
                .build();

        try {
            Preference preference = client.create(preferenceRequest);

            // Retorna init_point (o sandboxInitPoint si estás en pruebas)
            return preference.getSandboxInitPoint();
        } catch (MPException | MPApiException e) {
            System.out.println("ERROR DE MERCADO PAGO: " + e.getMessage());
            if (e instanceof MPApiException) {
                System.out.println("DETALLE: " + ((MPApiException) e).getApiResponse().getContent());
            }
            throw new RuntimeException("Error al comunicarse con Mercado Pago", e);
        }
    }

}
