# 🚀 Laboratório de Microsserviços: Arquitetura Orientada a Eventos

Este repositório contém a implementação de uma arquitetura de microsserviços baseada em mensageria (Event-Driven Architecture). O projeto simula o fluxo assíncrono de um sistema, focando em desacoplamento, resiliência e processamento em background utilizando o ecossistema Spring Boot.

Toda a infraestrutura foi projetada para rodar em um cluster **Docker Swarm** hospedado no tier *Always Free* da **Oracle Cloud Infrastructure (OCI)**, utilizando instâncias com arquitetura **ARM64**.

---

## 🏗️ Arquitetura do Projeto

O sistema é composto por três blocos principais que se comunicam de forma assíncrona:

*   **1. API Gateway / Produtor (Java & Spring Boot):**
    *   Ponto de entrada da aplicação.
    *   Recebe requisições HTTP (ex: `POST /pedidos`) dos usuários finais via `@RestController`.
    *   Valida o payload e utiliza o `RabbitTemplate` para publicar o evento no Message Broker.
    *   Retorna rapidamente um status HTTP `202 Accepted` para o cliente, liberando a conexão.

*   **2. Message Broker (RabbitMQ):**
    *   O "coração" da comunicação assíncrona.
    *   Recebe as mensagens (eventos) da API Gateway e as enfileira de forma segura até que o Worker esteja pronto para consumi-las.

*   **3. Worker de Processamento (Java & Spring Boot):**
    *   Serviço em background utilizando a anotação `@RabbitListener`.
    *   Consome as mensagens da fila automaticamente e simula o processamento das regras de negócio pesadas.

---

## 🎯 Objetivos de Aprendizado

*   **Desacoplamento de Serviços:** Entender como a falha no Worker não afeta a disponibilidade da API principal.
*   **Mensageria com Spring AMQP:** Implementação robusta de produtores e consumidores de mensagens.
*   **Orquestração de Contêineres:** Deploy e gerenciamento de microsserviços utilizando o **Docker Swarm**.
*   **Infraestrutura ARM64:** Construção de imagens Docker multi-stage (usando JRE e JDK) nativamente compatíveis com processadores Ampere A1.

---

## 🛠️ Stack Tecnológica

| Componente | Tecnologia | Função no Sistema |
| :--- | :--- | :--- |
| **API Backend** | Java (Spring Boot) | Recepção de chamadas REST e publicação de eventos |
| **Mensageria** | RabbitMQ | Gerenciamento de filas, exchanges e roteamento |
| **Worker Consumidor** | Java (Spring Boot) | Processamento assíncrono das tarefas da fila |
| **Orquestração** | Docker Swarm | Distribuição e resiliência dos contêineres |
| **Infraestrutura** | OCI (Oracle Cloud) | Hospedagem em instâncias ARM (Always Free) |

---

## 🗺️ Roadmap de Implementação

- [ ] Fase 1: Criação da infraestrutura na OCI (VCN, Instâncias, Regras de Firewall).
- [ ] Fase 2: Instalação e configuração do cluster Docker Swarm.
- [ ] Fase 3: Desenvolvimento da API Produtora (Spring Boot).
- [ ] Fase 4: Configuração do Broker de Mensageria (Contêiner RabbitMQ).
- [ ] Fase 5: Desenvolvimento do Worker Consumidor (Spring Boot).
- [ ] Fase 6: Criação dos Dockerfiles (`eclipse-temurin`) para ARM64.
- [ ] Fase 7: Criação do `docker-compose.yml` e deploy da stack no Swarm.
- [ ] Fase 8: Testes de carga e validação de resiliência.
