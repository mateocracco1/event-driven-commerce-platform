# Directrices de Desarrollo - Backend E-Commerce

## Stack Tecnológico
- Java 17+, Spring Boot 3.x, Maven.
- Spring Data JPA, Spring Security, Bean Validation.
- Arquitectura: Package-by-Feature (`ecommerce.core.*`, `ecommerce.features.*`).

## Reglas de Dominio y Código
1. **Records para DTOs:** Siempre usar Java records para DTOs de entrada y salida con validaciones Jakarta.
2. **Excepciones de dominio:** Las excepciones heredan de `BusinessException`. Nunca contienen códigos HTTP ni lógica web.
3. **Manejo Web:** Todo código HTTP y mapeo de errores pertenece exclusivamente a `@RestControllerAdvice`.
4. **Imports:** Prohibido usar imports con comodín (`import java.util.*`). Siempre imports explícitos.
5. **Estilo:** Evitar código innecesario, comentarios redundantes o sobreingeniería.

## Protocolo de Trabajo del Agente
- NUNCA modificar archivos existentes sin previo aviso o plan.
- Al proponer cambios, mostrar las clases y rutas exactas antes de escribir en disco.
- Trabajar en pasos pequeños y atómicos (una sola responsabilidad por tarea).