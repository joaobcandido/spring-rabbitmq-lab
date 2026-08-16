package com.lab.worker_consumidor.consumer;

import com.lab.worker_consumidor.config.RabbitMQConfig;
import com.lab.worker_consumidor.dto.PedidoDTO;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PedidoConsumer {

    @RabbitListener(queues = RabbitMQConfig.FILA_PEDIDOS)
    public void processarPedido(PedidoDTO pedido) {
        if (pedido.valor() < 0) {
            System.out.println("=====================================");
            System.out.println("[X] ERRO GRAVE! REJEITANDO PEDIDO");
            System.out.println("ID do Pedido : " + pedido.id());
            System.out.println("Cliente      : " + pedido.cliente());
            System.out.println("Valor        : R$ " + pedido.valor() + " (INVÁLIDO)");
            System.out.println("Ação         : Enviando para a DLQ...");
            System.out.println("=====================================\n");
            
            throw new AmqpRejectAndDontRequeueException("Valor do pedido não pode ser negativo");
        }

        System.out.println("=====================================");
        System.out.println("[V] MENSAGEM CONSUMIDA COM SUCESSO!");
        System.out.println("ID do Pedido : " + pedido.id());
        System.out.println("Cliente      : " + pedido.cliente());
        System.out.println("Valor        : R$ " + pedido.valor());
        System.out.println("=====================================\n");
    }

    public void processarMensagemMorta(PedidoDTO pedido) {
        System.out.println("=====================================");
        System.out.println("🧟 RESGATANDO MENSAGEM DA DLQ!");
        System.out.println("ID do Pedido : " + pedido.id());
        System.out.println("Ação         : Analisando o erro ou salvando no banco de dados...");
        System.out.println("=====================================\n");
    }
}
