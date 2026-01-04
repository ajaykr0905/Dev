package com.example.api.service;

import com.example.api.domain.Entity;
import com.example.api.repository.EntityRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class EntityService {
    
    private final EntityRepository repository;
    
    public Flux<Entity> findAll() {
        return repository.findAll()
            .doOnNext(e -> log.debug("Found entity: {}", e.getId()));
    }
    
    public Flux<Entity> findActive() {
        return repository.findByActiveTrue();
    }
    
    public Mono<Entity> findById(Long id) {
        return repository.findById(id);
    }
    
    @Transactional
    public Mono<Entity> create(Entity entity) {
        return repository.save(entity)
            .doOnSuccess(e -> log.info("Created entity: {}", e.getId()));
    }
    
    @Transactional
    public Mono<Entity> update(Long id, Entity entity) {
        return repository.findById(id)
            .flatMap(existing -> {
                existing.setName(entity.getName());
                existing.setDescription(entity.getDescription());
                existing.setActive(entity.isActive());
                return repository.save(existing);
            })
            .doOnSuccess(e -> log.info("Updated entity: {}", e.getId()));
    }
    
    @Transactional
    public Mono<Void> delete(Long id) {
        return repository.deleteById(id)
            .doOnSuccess(v -> log.info("Deleted entity: {}", id));
    }
    
    @CircuitBreaker(name = "search", fallbackMethod = "searchFallback")
    public Flux<Entity> search(String query) {
        return repository.findByNameContainingIgnoreCase(query);
    }
    
    private Flux<Entity> searchFallback(String query, Throwable t) {
        log.warn("Search fallback triggered for query: {}", query, t);
        return Flux.empty();
    }
}

