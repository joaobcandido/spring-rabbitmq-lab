package com.lab.api_produtora.controller;

import com.lab.api_produtora.config.RabbitMQConfig;
import com.lab.api_produtora.dto.PedidoDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final RabbitTemplate rabbitTemplate;

    public PedidoController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping
    public ResponseEntity<PedidoDTO> criarPedido(@RequestBody PedidoDTO pedidoRecebido) {
        String novoId = UUID.randomUUID().toString();
        PedidoDTO pedidoComId = new PedidoDTO(novoId, pedidoRecebido.cliente(), pedidoRecebido.valor());
        
        rabbitTemplate.convertAndSend(RabbitMQConfig.FILA_PEDIDOS, pedidoComId);
        return ResponseEntity.ok(pedidoComId);
    }

    @GetMapping("/consumir-fila")
    public ResponseEntity<PedidoDTO> consumirFilaPrincipal() {
        PedidoDTO pedido = (PedidoDTO) rabbitTemplate.receiveAndConvert(RabbitMQConfig.FILA_PEDIDOS);
        if (pedido == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/consumir-dlq")
    public ResponseEntity<PedidoDTO> consumirFilaDeErros() {
        PedidoDTO pedidoErro = (PedidoDTO) rabbitTemplate.receiveAndConvert(RabbitMQConfig.FILA_PEDIDOS_DLQ);
        if (pedidoErro == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pedidoErro);
    }
}
