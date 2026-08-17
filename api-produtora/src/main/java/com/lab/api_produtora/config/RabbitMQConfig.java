package com.lab.api_produtora.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String FILA_PEDIDOS = "pedidos.v1.fila-criacao";
    public static final String FILA_PEDIDOS_DLQ = "pedidos.v1.fila-criacao.dlq";
    public static final String FILA_PEDIDOS_DELAY = "pedidos.v1.fila-delay"; // <--- Adicionado aqui também
    
    public static final String EXCHANGE_PEDIDOS = "pedidos.v1.exchange-criacao";
    public static final String EXCHANGE_DLQ = "pedidos.v1.exchange-criacao.dlq";

    @Bean
    public Queue filaPedidos() {
        return QueueBuilder.durable(FILA_PEDIDOS)
                .withArgument("x-dead-letter-exchange", EXCHANGE_DLQ)
                .withArgument("x-dead-letter-routing-key", FILA_PEDIDOS_DLQ)
                .build();
    }

    @Bean
    public Queue filaPedidosDlq() {
        return QueueBuilder.durable(FILA_PEDIDOS_DLQ).build();
    }

    // --- NOVA FILA DE DELAY NA API ---
    @Bean
    public Queue filaPedidosDelay() {
        return QueueBuilder.durable(FILA_PEDIDOS_DELAY)
                .withArgument("x-message-ttl", 60000) // 1 minuto de atraso
                .withArgument("x-dead-letter-exchange", EXCHANGE_PEDIDOS) // Quando expirar, vai para a exchange principal
                .withArgument("x-dead-letter-routing-key", "")
                .build();
    }

    @Bean
    public FanoutExchange exchangePedidos() {
        return new FanoutExchange(EXCHANGE_PEDIDOS);
    }

    @Bean
    public FanoutExchange exchangeDlq() {
        return new FanoutExchange(EXCHANGE_DLQ);
    }

    @Bean
    public Binding bindingPedidos() {
        return BindingBuilder.bind(filaPedidos()).to(exchangePedidos());
    }

    @Bean
    public Binding bindingDlq() {
        return BindingBuilder.bind(filaPedidosDlq()).to(exchangeDlq());
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
