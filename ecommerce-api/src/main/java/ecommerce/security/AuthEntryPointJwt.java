package ecommerce.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        logger.error("Error de no autorizado: {}", authException.getMessage());
        // Esto te dirá exactamente QUÉ URL y QUÉ MÉTODO causó el 401:
        logger.error("401 NO AUTORIZADO en [{} {}]: {}",
                request.getMethod(),
                request.getRequestURI(),
                authException.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: No autorizado");
    }
}