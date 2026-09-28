package com.pedidos360.notifications.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EnviarCorreoRequest {

    @NotBlank(message = "to es obligatorio")
    @Email(message = "to debe ser un email valido")
    private String to;

    @NotBlank(message = "subject es obligatorio")
    private String subject;

    @NotBlank(message = "body es obligatorio")
    private String body;
}
