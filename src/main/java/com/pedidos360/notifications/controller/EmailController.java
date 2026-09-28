package com.pedidos360.notifications.controller;

import com.pedidos360.notifications.dto.EnviarCorreoRequest;
import com.pedidos360.notifications.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Endpoint para probar el envio de correo directamente desde Postman, sin
 * pasar por RabbitMQ - requisito explicito del enunciado. En produccion, el
 * envio real ocurre automaticamente via PedidoCreadoListener al confirmarse
 * un pedido; este endpoint es solo para pruebas manuales.
 */
@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/enviar")
    public ResponseEntity<Map<String, String>> enviarCorreoPrueba(@Valid @RequestBody EnviarCorreoRequest request) {
        try {
            emailService.enviarCorreo(request.getTo(), request.getSubject(), request.getBody());
            return ResponseEntity.ok(Map.of("mensaje", "Correo enviado exitosamente a " + request.getTo()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "No se pudo enviar el correo: " + e.getMessage()));
        }
    }
}
