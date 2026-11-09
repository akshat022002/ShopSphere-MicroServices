#  ShopSphere Microservices

ShopSphere is a modular e-commerce backend built with **Java** and **Spring Boot**. Instead of one large application, the platform is split into small, independently deployable services, each owning a single business domain such as authentication, catalog, cart, orders, payments or inventory.

Services are exposed through a single **API Gateway**, discover each other through **Eureka**, and communicate asynchronously through **Apache Kafka** for auditing and notifications.

---

##  Overview

The project demonstrates how a real-world online store can be designed with a microservices architecture:

- Every business capability lives in its own service and can be developed, deployed and scaled independently.
- Clients talk to one entry point (the API Gateway) and never to individual services directly.
- Services register themselves with a central registry, so no hard-coded addresses are needed.
- Non-blocking concerns such as audit logging and user notifications are handled through events, so they never slow down the main request path.

---

##  Features

**Core shopping flow**
- User authentication and secure access to protected APIs
- Product catalog with browsing and management
- Shopping cart per user
- Coupon and discount handling
- Order placement and order lifecycle management
- Payment processing
- Inventory and stock tracking

**Customer engagement**
- Product reviews
- Wishlist
- AI-powered features through a dedicated AI service
- Dashboards for insights and reporting

**Platform capabilities**
- Single entry point through an API Gateway
- Service registration and discovery with Eureka
- Event-driven auditing: important actions are recorded as an audit trail
- Event-driven notifications: users are notified without blocking the main flow
- Independently deployable services with clear boundaries

---

##  Tech Stack

| Layer | Technology |
| --- | --- |
| Language | Java |
| Framework | Spring Boot |
| Service discovery | Spring Cloud Netflix Eureka |
| API routing | Spring Cloud API Gateway |
| Messaging | Apache Kafka |
| Architecture | Microservices, event-driven |

---

##  Architecture

```
                         ┌──────────────────┐
          Client ──────▶ │   API Gateway    │
                         └────────┬─────────┘
                                  │  routes to registered services
                         ┌────────▼─────────┐
                         │  Eureka Server   │  (service registry)
                         └────────┬─────────┘
                                  │
   ┌─────────┬─────────┬─────────┬┴────────┬──────────┬───────────┐
   ▼         ▼         ▼         ▼         ▼          ▼           ▼
 auth     catalog     cart     coupon    order     payment    inventory

   review   wishlist     ai     dashboard
                                  │
                 services publish events
                                  ▼
                        ┌──────────────────┐
                        │  Apache Kafka    │
                        └────────┬─────────┘
                       ┌─────────┴──────────┐
                       ▼                    ▼
                audit-service      notification-service
```

### How it works

1. A client sends a request to the **API Gateway**.
2. The gateway looks up the target service in **Eureka** and forwards the request.
3. The service handles the request using its own business logic and data.
4. When something important happens (for example an order is placed or a payment completes), the service publishes an event to **Kafka**.
5. The **audit-service** and **notification-service** consume these events independently and in the background.

---

##  Project Structure

Each folder at the root is a standalone Spring Boot service.

```
ShopSphere-MicroServices/
├── eureka-server/          # Service registry
├── api-gateway/            # Single entry point
├── auth-service/           # Authentication
├── catalog-service/        # Products and categories
├── cart-service/           # Shopping cart
├── coupon-service/         # Discounts and coupons
├── order-service/          # Orders
├── payment-service/        # Payments
├── inventory-service/      # Stock management
├── review-service/         # Product reviews
├── wishlist-service/       # Wishlists
├── ai-service/             # AI features
├── dashboard-service/      # Dashboards and insights
├── audit-service/          # Audit trail (Kafka consumer)
├── notification-service/   # Notifications (Kafka consumer)
└── .gitignore
```

---

##  Services in Detail

### Infrastructure services

| Service | Description |
| --- | --- |
| **eureka-server** | Central service registry. Every service registers here on startup so others can discover it by name instead of by address. |
| **api-gateway** | The only public entry point. Routes incoming requests to the right service and is the natural place for cross-cutting concerns such as authentication checks. |

### Business services

| Service | Responsibility |
| --- | --- |
| **auth-service** | Handles user identity: registration, login and verification of who is calling the APIs. |
| **catalog-service** | Manages the product catalog, including products and their categories. |
| **cart-service** | Stores the items a user has added to their cart before checkout. |
| **coupon-service** | Manages discount codes and validates them when applied to a cart or order. |
| **order-service** | Creates orders from carts and manages them through their lifecycle. |
| **payment-service** | Processes payments for orders and tracks payment status. |
| **inventory-service** | Tracks available stock so products are not oversold. |
| **review-service** | Lets customers rate and review products. |
| **wishlist-service** | Lets customers save products to buy later. |
| **ai-service** | Provides AI-powered capabilities to enhance the shopping experience. |
| **dashboard-service** | Aggregates data into dashboards for monitoring and business insight. |

### Event-driven services

| Service | Responsibility |
| --- | --- |
| **audit-service** | Listens to Kafka events and records an audit trail of important actions across the platform. |
| **notification-service** | Listens to Kafka events and sends notifications to users, such as order updates. |

---

## 📨 Event-Driven Design

ShopSphere uses **Apache Kafka** so that services can react to business events without being tightly coupled.

- **Producers** publish events when something meaningful happens.
- **Consumers** (audit and notification services) process those events on their own schedule.
- If a consumer is slow or temporarily down, the main shopping flow is not affected.
- New consumers can be added later without changing the producing services.

---

##  Design Principles

- **Single responsibility:** each service owns one business domain.
- **Loose coupling:** services interact through the gateway, service discovery and events rather than direct dependencies.
- **Independent deployment:** a change to one service does not require redeploying the others.
- **Scalability:** busy services such as catalog or order can be scaled on their own.
- **Resilience:** asynchronous messaging keeps background work off the critical path.

---

##  Future Improvements

- Containerization with Docker and Docker Compose
- Centralized configuration management
- Distributed tracing and monitoring
- CI/CD pipeline
- API documentation with Swagger / OpenAPI

---

##  Author

**Akshat**
GitHub: [@akshat022002](https://github.com/akshat022002)
