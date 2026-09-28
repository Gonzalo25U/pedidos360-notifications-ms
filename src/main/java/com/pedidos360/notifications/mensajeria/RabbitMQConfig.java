package com.pedidos360.notifications.mensajeria;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_PEDIDOS = "pedidos.eventos";
    public static final String ROUTING_KEY_PEDIDO_CREADO = "pedido.creado";
    public static final String QUEUE_ENVIAR_CORREO = "notificaciones.enviar-correo";

    @Bean
    public TopicExchange exchangePedidos() {
        return new TopicExchange(EXCHANGE_PEDIDOS, true, false);
    }

    @Bean
    public Queue colaEnviarCorreo() {
        return new Queue(QUEUE_ENVIAR_CORREO, true);
    }

    @Bean
    public Binding bindingEnviarCorreo(Queue colaEnviarCorreo, TopicExchange exchangePedidos) {
        return BindingBuilder.bind(colaEnviarCorreo).to(exchangePedidos).with(ROUTING_KEY_PEDIDO_CREADO);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
