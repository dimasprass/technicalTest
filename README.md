# Inventory Order API

Spring Boot application for managing **Items**, **Inventory** (Top Up / Withdrawal), and **Orders** with stock validation.

## Features

- **Item**: CRUD with remaining stock shown on get/list
- **Inventory**: T (Top Up) increases stock, W (Withdrawal) decreases stock
- **Order**: Create order with insufficient-stock check; order deducts item stock
- H2 in-memory database with JPA
- Bean Validation on mandatory fields
- Global exception handler (`@ControllerAdvice`)
- Pagination on all list endpoints
- JUnit unit tests
- Postman collection for API testing

## Requirements

- Java 17+
- Maven 3.6+

## Run

```bash
mvn spring-boot:run
```

Server: `http://localhost:8080`

## API Base URL

- **Items**: `GET/POST /api/items`, `GET/PUT/DELETE /api/items/{id}`
- **Inventories**: `GET/POST /api/inventories`, `GET/PUT/DELETE /api/inventories/{id}`
- **Orders**: `GET/POST /api/orders`, `GET/PUT/DELETE /api/orders/{id}`

List endpoints support `?page=0&size=10&sortBy=id`.

## Postman

Import `postman/Inventory-Order-API.postman_collection.json` into Postman. Set `baseUrl` to `http://localhost:8080` (default). Create an item first, then use its `id` for inventory and order requests.

## Tests

```bash
mvn test
```

## H2 Console

When the app is running: `http://localhost:8080/h2-console`  
JDBC URL: `jdbc:h2:mem:inventorydb`, User: `sa`, Password: (empty)
