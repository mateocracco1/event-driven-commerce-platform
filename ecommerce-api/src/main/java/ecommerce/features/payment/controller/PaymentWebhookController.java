package ecommerce.features.payment.controller;

import ecommerce.features.payment.dto.MercadoPagoWebhookNotification;
import ecommerce.features.payment.service.PaymentProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentWebhookController {

    private final PaymentProcessingService paymentProcessingService;

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody(required = false) MercadoPagoWebhookNotification body,
            @RequestParam(value = "type", required = false) String paramType,
            @RequestParam(value = "data.id", required = false) String paramDataId) {

        log.info("Notificación recibida de Mercado Pago: body={}, type={}, data.id={}", body, paramType, paramDataId);

        String type = (body != null && body.type() != null) ? body.type() : paramType;
        String idString = (body != null && body.data() != null && body.data().id() != null)
                ? body.data().id()
                : paramDataId;

        // MP envía eventos de distintos tipos (merchant_order, payment, chargeback, etc.)
        if ("payment".equalsIgnoreCase(type) && idString != null) {
            try {
                Long paymentId = Long.parseLong(idString);
                paymentProcessingService.processWebhookPayment(paymentId);
            } catch (NumberFormatException e) {
                log.error("ID de pago inválido recibido: {}", idString);
            } catch (Exception e) {
                log.error("Error al procesar el pago desde el webhook: {}", e.getMessage(), e);
                // Opcional: devolver 200 de todas formas si querés evitar reintentos por bugs de código,
                // o 500 para que MP reintente.
            }
        }

        // Siempre responder 200 OK a Mercado Pago
        return ResponseEntity.ok().build();
    }
}