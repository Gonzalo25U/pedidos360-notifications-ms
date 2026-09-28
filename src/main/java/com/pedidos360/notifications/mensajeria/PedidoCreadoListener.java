package com.pedidos360.notifications.mensajeria;

import com.pedidos360.notifications.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PedidoCreadoListener {

    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_ENVIAR_CORREO)
    public void enviarConfirmacion(PedidoCreadoEvent evento) {
        if (evento.getEmailUsuario() == null || evento.getEmailUsuario().isBlank()) {
            log.warn("El pedido {} no trae email de usuario, no se puede notificar", evento.getPedidoId());
            return;
        }

        try {
            emailService.enviarCorreoHtml(
                    evento.getEmailUsuario(),
                    "Pedidos360 - Confirmación de tu pedido #" + evento.getPedidoId(),
                    construirHtml(evento)
            );
            log.info("Correo de confirmación enviado a {} para el pedido {}",
                    evento.getEmailUsuario(), evento.getPedidoId());
        } catch (Exception e) {
            // No se relanza la excepcion: un fallo de correo no debe hacer que
            // RabbitMQ reintente indefinidamente el mismo mensaje.
            log.error("No se pudo enviar el correo de confirmación del pedido {}: {}",
                    evento.getPedidoId(), e.getMessage());
        }
    }

    private String construirHtml(PedidoCreadoEvent evento) {
        StringBuilder filas = new StringBuilder();
        for (PedidoCreadoEvent.ItemEvento item : evento.getItems()) {
            filas.append("<tr>")
                    .append("<td>").append(item.getNombreProducto()).append("</td>")
                    .append("<td>").append(item.getCantidad()).append("</td>")
                    .append("<td>$").append(item.getPrecioUnitario()).append("</td>")
                    .append("</tr>");
        }

        return """
                <h2>¡Gracias por tu compra!</h2>
                <p>Tu pedido <strong>#%d</strong> fue confirmado exitosamente.</p>
                <table border="1" cellpadding="8" cellspacing="0">
                  <thead>
                    <tr><th>Producto</th><th>Cantidad</th><th>Precio unitario</th></tr>
                  </thead>
                  <tbody>
                    %s
                  </tbody>
                </table>
                <p><strong>Total: $%s</strong></p>
                <p>Pedidos360 - Este es un correo automático, por favor no respondas.</p>
                """.formatted(evento.getPedidoId(), filas, evento.getTotal());
    }
}
