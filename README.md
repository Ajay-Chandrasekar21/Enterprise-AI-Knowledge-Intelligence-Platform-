# EAKIP Backend Foundation
## Enterprise AI Knowledge Intelligence Platform

This repository hosts the modular clean-architecture Java Spring Boot backend foundation for the EAKIP system.

---

## 1. Submodule Responsibilities

*   **`eakip-parent`** (root `pom.xml`): Manages shared dependencies, plugins, and module profiles.
*   **`eakip-core`**: Core Domain layer containing Entity schemas, repositories (JPA/Mongo), domain exceptions, custom validation constraints, and generic DTO templates.
*   **`eakip-security`**: Handles JWT verification filters, Role-Based Access Control (RBAC) maps, and custom UserDetails authentication provider flows.
*   **`eakip-document-processing`**: Ingestion, text parser interfaces (Apache Tika, PDFBox), and OCR frameworks.
*   **`eakip-rag`**: Splitters, vector embedding transformers, vector database mapping structures, and search adapters.
*   **`eakip-agent-orchestrator`**: Coordinates multi-agent planning pipelines, routing message streams, and LLM communication APIs.
*   **`eakip-analytics`**: Formulates reading and inventory demand analytics calculations.
*   **`eakip-api`**: Hosts REST endpoints, WebSockets controllers, AMQP bindings, and structured logging filters.

---

## 2. Infrastructure Setup & Build

### Local Container Environment
Before executing the application, provision all supporting database services using Docker Compose:

```bash
docker-compose up -d
```

Containers launched:
*   **PostgreSQL 16** (with `pgvector` enabled) on port `5432`
*   **MongoDB 7.0** on port `27017`
*   **Redis 7.2** on port `6379`
*   **RabbitMQ 3.13** on port `5672` (management interface on port `15672`)
*   **Elasticsearch 8.13** on port `9200`

---

## 3. Running & Compiling

### Build the Project
Compile the parent Maven hierarchy and execute all test suits:

```bash
mvn clean install
```

### Run Eakip Application
Execute the API boot module using the default `dev` profile:

```bash
mvn spring-boot:run -pl eakip-api
```

Once running, verify interfaces:
*   **REST API Swagger Console**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
*   **OpenAPI Specs**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
*   **Actuator Prometheus Port**: [http://localhost:8080/actuator/prometheus](http://localhost:8080/actuator/prometheus)
*   **WebSocket Endpoints**:
    *   Notifications Feed: `ws://localhost:8080/ws/notifications`

---

## 4. Coding Standards

*   **SOLID & Clean Architecture**: Keep domain interfaces free of framework logic. Inject dependencies via constructors.
*   **Correlation Tracing**: Ensure incoming requests have the `X-Correlation-Id` header populated; logs automatically include `[CorrelationID: <id>]`.
*   **Type-Safe Validation**: Validate all inbound DTOs using `@Valid` with custom validators where applicable.
