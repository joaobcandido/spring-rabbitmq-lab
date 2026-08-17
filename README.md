# 🚀 Laboratório de Microsserviços: Arquitetura Orientada a Eventos com RabbitMQ

Este repositório contém a implementação de uma arquitetura de microsserviços baseada em mensageria assíncrona (**Event-Driven Architecture**). O projeto simula o fluxo de processamento de pedidos, focando em desacoplamento, resiliência, tolerância a falhas com **Dead Letter Queue (DLQ)** e processamento em background utilizando **Spring Boot**.

Toda a infraestrutura foi projetada para rodar em um cluster **Docker Swarm** hospedado no tier *Always Free* da **Oracle Cloud Infrastructure (OCI)**, utilizando instâncias com arquitetura **ARM64**.

---

## 🏗️ Arquitetura do Sistema

O sistema é dividido em dois microsserviços independentes que se comunicam de forma assíncrona através de um Message Broker:

1. **`api-produtora` (API & Producer - Porta 8080):**
   * Ponto de entrada REST da aplicação (`POST /pedidos`).
   * Valida e gera um identificador único (`UUID`) para cada pedido.
   * Publica o evento no RabbitMQ de forma assíncrona.
   * Disponibiliza endpoints de auditoria (`GET`) para consumir mensagens manualmente da fila principal ou da DLQ.

2. **`worker-consumidor` (Background Worker - Porta 8081):**
   * Microsserviço independente utilizando `@RabbitListener`.
   * Consome os pedidos da fila em background de forma automática.
   * Aplica regras de negócio: identifica erros (ex: valores negativos) e rejeita explicitamente a mensagem (`AmqpRejectAndDontRequeueException`), roteando-a automaticamente para a **DLQ**.

3. **Message Broker (RabbitMQ):**
   * Gerencia as filas principais, Exchanges (`Fanout`) e o mecanismo de enfileiramento de mensagens mortas (DLX/DLQ).

---

## 🗺️ Fluxo de Resiliência (DLQ)

```text
[Cliente / Bruno] 
       │ (POST /pedidos)
       ▼
[api-produtora] ──(Publica)──> [ RabbitMQ Exchange ]
                                       │
            ┌──────────────────────────┴──────────────────────────┐
            ▼                                                     ▼
   [ Fila Principal ]                                   [ Dead Letter Queue (DLQ) ]
            │                                                     │
            ▼                                                     ▼
[worker-consumidor] ──(Erro / Valor < 0)──> [Rejeita / Envia para DLX]
```
---

## 🛠️ Stack Tecnológica

| Componente | Tecnologia | Função no Sistema |
| :--- | :--- | :--- |
| **API Backend** | Java 17+ / Spring Boot | Recepção de chamadas REST e publicação de eventos |
| **Worker Consumidor** | Java 17+ / Spring Boot | Processamento assíncrono e aplicação de regras de negócio |
| **Mensageria** | RabbitMQ (v3.12) | Gerenciamento de filas, exchanges e roteamento de DLQ |
| **Orquestração** | Docker Swarm | Distribuição e resiliência dos contêineres |
| **Infraestrutura** | OCI (Oracle Cloud) | Hospedagem em instâncias ARM (Always Free) |

---

## 🚀 Como Executar o Projeto Localmente

### Pré-requisitos
* Java 17 ou superior instalado.
* Docker e Docker Compose instalados.

### 1. Subir o RabbitMQ
Na raiz do projeto, suba o container do broker:
```bash
docker compose up -d

(O painel de gerenciamento estará disponível em http://localhost:15672 - usuário/senha: guest / guest)

2. Iniciar o Worker Consumidor
Abra um terminal na pasta do worker:

Bash
cd worker-consumidor
mvn spring-boot:run
3. Iniciar a API Produtora
Abra outro terminal na pasta da API:

Bash
cd api-produtora
mvn spring-boot:run
🧪 Testando os Endpoints
Você pode utilizar ferramentas como Bruno ou Postman para testar as requisições:

Criar Pedido Válido (Sucesso):

POST http://localhost:8080/pedidos

Body (JSON):

JSON
{
  "cliente": "Maria Souza",
  "valor": 250.0
}
Criar Pedido Inválido (Gera erro e vai para a DLQ):

POST http://localhost:8080/pedidos

Body (JSON):

JSON
{
  "cliente": "Carlos Negativo",
  "valor": -100.0
}
Consultar/Auditar a DLQ sob demanda:

GET http://localhost:8080/pedidos/consumir-dlq

📌 Roadmap do Projeto
[x] Fase 1: Criação da arquitetura base e conceitos de mensageria.

[x] Fase 2: Configuração do RabbitMQ com Exchanges, Filas e DLQ.

[x] Fase 3: Desenvolvimento da API Produtora com Spring Boot e UUID.

[x] Fase 4: Desenvolvimento do Worker Consumidor desacoplado com tratamento de exceções.

[x] Fase 5: Validação do fluxo assíncrono e consumo manual de DLQ.

[x] Fase 6: Criação de Dockerfiles otimizados (eclipse-temurin) para arquitetura ARM64.

[x] Fase 7: Deploy e orquestração da stack no Docker Swarm (OCI).
