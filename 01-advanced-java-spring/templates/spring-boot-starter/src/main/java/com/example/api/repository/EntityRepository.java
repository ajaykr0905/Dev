package com.example.api.repository;

import com.example.api.domain.Entity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface EntityRepository extends ReactiveCrudRepository<Entity, Long> {
    
    Flux<Entity> findByActiveTrue();
    
    Flux<Entity> findByNameContainingIgnoreCase(String name);
}

