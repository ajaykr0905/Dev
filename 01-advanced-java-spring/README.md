# 01 - Advanced Java & Spring Boot

## Purpose

Advanced Java and Spring Boot learning materials, projects, and reusable components. Master these patterns for enterprise-grade microservices development.

---

## Learning Objectives

1. Master Reactive Programming - Spring WebFlux, Project Reactor
2. Implement Advanced Microservices - Distributed transactions, saga patterns
3. Optimize Performance - JVM tuning, async processing, caching
4. Apply Enterprise Patterns - DDD, CQRS, Event Sourcing
5. Integrate AI Services - Claude API, OpenAI, Spring AI

---

## Directory Structure

```
01-advanced-java-spring/
├── learning-path/
│   ├── advanced-spring-boot/
│   ├── reactive-programming/
│   ├── microservices-patterns/
│   └── enterprise-integration/
├── projects/
│   ├── ai-powered-api-gateway/
│   ├── healthcare-appointment-service/
│   ├── fintech-transaction-engine/
│   └── ecommerce-product-catalog/
├── templates/
│   ├── spring-boot-microservice-starter/
│   ├── spring-webflux-reactive-starter/
│   └── spring-ai-integration-template/
└── libraries/
    ├── common-exception-handler/
    ├── audit-logging-library/
    └── ai-client-wrapper/
```

---

## Core Topics

### Advanced Spring Boot Patterns
- Spring Boot 3.x features
- Custom auto-configuration
- Custom starters
- Actuator & metrics
- GraalVM native compilation

### Reactive Programming with WebFlux
- Project Reactor fundamentals
- Mono and Flux operators
- Backpressure handling
- R2DBC for reactive database access

### Microservices Patterns
- Distributed transactions (Saga pattern)
- Service discovery with Spring Cloud
- API Gateway patterns
- Circuit breaker with Resilience4j
- Distributed tracing

### AI Integration
- Spring AI framework
- LLM integration (Claude, OpenAI)
- RAG pipeline implementation
- Semantic search with embeddings

---

## Code Examples

### Reactive Controller

```java
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    
    private final ProductService productService;
    
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Product> streamProducts() {
        return productService.streamAllProducts()
            .delayElements(Duration.ofMillis(100));
    }
    
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Product>> getProduct(@PathVariable String id) {
        return productService.findById(id)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
```

### Saga Pattern

```java
@Service
@RequiredArgsConstructor
public class OrderSagaOrchestrator {
    
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final InventoryService inventoryService;
    
    @Transactional
    public Mono<OrderResult> processOrder(OrderRequest request) {
        return Mono.just(request)
            .flatMap(orderService::createPendingOrder)
            .flatMap(order -> inventoryService.reserve(order)
                .onErrorResume(e -> rollbackOrder(order, e)))
            .flatMap(order -> paymentService.process(order)
                .onErrorResume(e -> rollbackInventory(order, e)))
            .flatMap(orderService::confirmOrder)
            .map(OrderResult::success)
            .onErrorResume(e -> Mono.just(OrderResult.failure(e.getMessage())));
    }
}
```

### AI Integration

```java
@Service
@RequiredArgsConstructor
public class AICustomerSupportService {
    
    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    
    public Mono<SupportResponse> handleQuery(SupportRequest request) {
        return Mono.fromCallable(() -> {
            List<Document> relevantDocs = vectorStore.similaritySearch(
                SearchRequest.query(request.getMessage())
                    .withTopK(5)
                    .withSimilarityThreshold(0.7)
            );
            
            String context = relevantDocs.stream()
                .map(Document::getContent)
                .collect(Collectors.joining("\n"));
            
            String response = chatClient.prompt()
                .system(buildSystemPrompt(context))
                .user(request.getMessage())
                .call()
                .content();
            
            return SupportResponse.builder()
                .message(response)
                .build();
        });
    }
}
```

---

## Revenue Projects

### Healthcare Appointment System
**$15,000 - $35,000**
- Intelligent scheduling with conflict detection
- Natural language booking via chatbot
- HIPAA-compliant data handling

### Fintech Transaction Engine
**$25,000 - $50,000**
- Sub-100ms transaction processing
- Real-time fraud detection
- Event sourcing for audit trail

### E-commerce Personalization API
**$10,000 - $25,000**
- Real-time product recommendations
- Customer behavior tracking
- ML-powered search ranking

---

## Key Dependencies

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.1</version>
</parent>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-r2dbc</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>io.github.resilience4j</groupId>
        <artifactId>resilience4j-spring-boot3</artifactId>
    </dependency>
</dependencies>
```

---

## Resources

- Spring Boot 3.x Reference Documentation
- "Reactive Spring" by Josh Long
- "Microservices Patterns" by Chris Richardson
- Spring AI Documentation
- Spring Academy (official)
