# notifications-ms — Microservicio de Notificaciones (Pedidos360)

Envía correos de confirmación de pedido, disparados automáticamente por un
evento de RabbitMQ, y expone además un endpoint para probar el envío
directamente desde Postman.

## Stack
- Java 21, Spring Boot 4.1.1
- `spring-boot-starter-mail` (JavaMailSender, SMTP de Gmail)
- `spring-boot-starter-amqp` (consume el evento `pedido.creado`)
- Seguridad OAuth2 Resource Server (mismo esquema multi-tenant que el resto)

## Variables de entorno

| Variable | Descripción |
|---|---|
| `GMAIL_USERNAME` | Tu correo de Gmail (el remitente) |
| `GMAIL_APP_PASSWORD` | La "contraseña de aplicación" de 16 caracteres (NO tu contraseña normal) — genera una en https://myaccount.google.com/apppasswords |
| `RABBITMQ_HOST` | IP privada de `pedidos360-rabbitmq` |
| `RABBITMQ_PASSWORD` | Password del usuario de RabbitMQ |
| `AZURE_CLIENT_ID` | Client ID de `Pedidos360-API` |
| `FRONTEND_URL` | Origen permitido por CORS |

## Requisito previo en tu cuenta de Google

1. Activa la verificación en 2 pasos: https://myaccount.google.com/security
2. Genera una contraseña de aplicación: https://myaccount.google.com/apppasswords
3. Usa esa contraseña (no la de tu cuenta) en `GMAIL_APP_PASSWORD`

## Ejecución con Docker

```bash
docker build -t pedidos360/notifications-ms .
docker run -d --restart unless-stopped --name notifications-ms -p 8084:8084 \
  -e AZURE_CLIENT_ID=11ca102e-9f51-438c-94d6-5c0bf54920b7 \
  -e FRONTEND_URL=https://wwg5c7u2qk.execute-api.us-east-1.amazonaws.com \
  -e RABBITMQ_HOST=<ip-privada-rabbitmq> \
  -e RABBITMQ_PASSWORD=<password-rabbitmq> \
  -e GMAIL_USERNAME=tu_correo@gmail.com \
  -e GMAIL_APP_PASSWORD=xxxxxxxxxxxxxxxx \
  pedidos360/notifications-ms
```

## Modo local (sin Azure AD)

```bash
docker run -d --name notifications-ms -p 8084:8084 \
  -e SPRING_PROFILES_ACTIVE=local \
  -e RABBITMQ_HOST=<ip-privada-rabbitmq> \
  -e RABBITMQ_PASSWORD=<password-rabbitmq> \
  -e GMAIL_USERNAME=tu_correo@gmail.com \
  -e GMAIL_APP_PASSWORD=xxxxxxxxxxxxxxxx \
  pedidos360/notifications-ms
```

## Endpoints

| Método | Ruta | Autorización |
|---|---|---|
| POST | `/api/notificaciones/enviar` | Cualquier usuario autenticado |

## Prueba directa desde Postman (requisito del enunciado)

```json
POST http://<ip>:8084/api/notificaciones/enviar
Authorization: Bearer <token>
Content-Type: application/json

{
  "to": "destinatario@ejemplo.com",
  "subject": "Prueba desde Postman",
  "body": "Este es un correo de prueba enviado directamente, sin pasar por RabbitMQ."
}
```

## Flujo automático (vía RabbitMQ)

1. `pedidos-ms` confirma un pedido → publica `pedido.creado`
2. `notifications-ms` consume el evento desde la cola
   `notificaciones.enviar-correo`
3. Envía un correo HTML con el detalle del pedido al `emailUsuario` del
   evento

Si el envío falla (ej. correo inválido), se registra el error en el log —
**no** se reintenta indefinidamente ni se revierte el pedido.

## Notas de arquitectura

- Reutiliza el patrón `EmailService`/`EmailController` del ejemplo visto en
  clase, solo adaptado a Spring Boot 4 y con `application.yml` en vez de
  `.properties`.
- No tiene base de datos propia — es un microservicio sin estado, solo envía
  correos.
- El endpoint de prueba y el listener usan el mismo `EmailService`, pero uno
  manda texto plano (`enviarCorreo`) y el otro HTML (`enviarCorreoHtml`).
