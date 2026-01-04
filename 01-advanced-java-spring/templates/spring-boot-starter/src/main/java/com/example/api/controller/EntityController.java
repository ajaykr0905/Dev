package com.example.api.controller;

import com.example.api.domain.Entity;
import com.example.api.service.EntityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/entities")
@RequiredArgsConstructor
public class EntityController {
    
    private final EntityService service;
    
    @GetMapping
    public Flux<Entity> findAll() {
        return service.findAll();
    }
    
    @GetMapping("/active")
    public Flux<Entity> findActive() {
        return service.findActive();
    }
    
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Entity> stream() {
        return service.findAll();
    }
    
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Entity>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/search")
    public Flux<Entity> search(@RequestParam String q) {
        return service.search(q);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Entity> create(@Valid @RequestBody Entity entity) {
        return service.create(entity);
    }
    
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Entity>> update(
            @PathVariable Long id,
            @Valid @RequestBody Entity entity) {
        return service.update(id, entity)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        return service.delete(id);
    }
}

