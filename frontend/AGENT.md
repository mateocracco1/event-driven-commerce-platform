Actúa como un desarrollador Senior en Angular. Vamos a implementar la interfaz de usuario para el módulo de Pedidos (Orders) y Pagos de un E-commerce.

### Contexto del Backend (Ya implementado y operativo)
- Arquitectura asíncrona basada en Spring Boot 3.x, RabbitMQ y MySQL.
- Pasarela: Mercado Pago Checkout Pro (redirección externa).
- Ciclo de vida de Orden:
  - Creación: `POST /api/orders/checkout` -> Devuelve un objeto con `paymentUrl` (link de Mercado Pago).
  - Estados de Orden: `PENDING_PAYMENT`, `PAID`, `CANCELLED` (y opcionalmente `DELIVERED`).
  - El frontend NO procesa tarjetas ni formularios de pago. Solo redirige a `paymentUrl`.

### Endpoints disponibles
1. `POST /api/orders/checkout`: Recibe los ítems/carrito, crea la orden y retorna `{ orderId, paymentUrl, ... }`.
2. `GET /api/orders/my-orders`: Listado de órdenes del cliente logueado.
3. `GET /api/orders`: Listado general para Administrador.
4. `GET /api/orders/{id}`: Detalle de una orden específica.
5. `PATCH /api/orders/{id}/status`: Admin actualiza estado (por ejemplo a 'DELIVERED' o 'CANCELLED').
6. `PATCH /api/orders/{id}/cancel`: Cliente o Admin cancela orden (solo permitido si status es 'PENDING_PAYMENT').

### Alcance de lo que vamos a construir
1. Interfaces TypeScript (Models) y `OrderService`.
2. Páginas de feedback para las `backUrls` de Mercado Pago:
   - `/checkout/success`
   - `/checkout/failure`
   - `/checkout/pending`
3. Integración en el Carrito existente: Botón "Finalizar Compra" que llame al checkout y redirija con `window.location.href`.
4. Vista de historial de órdenes (Cliente) y gestión de órdenes (Admin con selector de estado y cancelación).

Antes de generar componentes visuales, inspecciona la estructura de carpetas actual de este proyecto Angular (dime qué versión de Angular, si usamos Standalone Components o NgModules, y qué librería UI/CSS está instalada: Tailwind, Bootstrap, Angular Material, etc.).