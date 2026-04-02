package com.example.api.service;

import com.example.api.domain.Entity;
import com.example.api.repository.EntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntityServiceTest {

    @Mock
    private EntityRepository repository;

    @InjectMocks
    private EntityService service;

    private Entity sampleEntity;

    @BeforeEach
    void setUp() {
        sampleEntity = Entity.builder()
                .id(1L)
                .name("Test Entity")
                .description("A test entity")
                .active(true)
                .build();
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {
        @Test
        @DisplayName("should return all entities")
        void returnsAllEntities() {
            Entity second = Entity.builder().id(2L).name("Second").active(false).build();
            when(repository.findAll()).thenReturn(Flux.just(sampleEntity, second));

            StepVerifier.create(service.findAll())
                    .expectNext(sampleEntity)
                    .expectNext(second)
                    .verifyComplete();

            verify(repository).findAll();
        }

        @Test
        @DisplayName("should return empty flux when no entities exist")
        void returnsEmptyWhenNone() {
            when(repository.findAll()).thenReturn(Flux.empty());

            StepVerifier.create(service.findAll())
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("findActive")
    class FindActive {
        @Test
        @DisplayName("should return only active entities")
        void returnsActiveOnly() {
            when(repository.findByActiveTrue()).thenReturn(Flux.just(sampleEntity));

            StepVerifier.create(service.findActive())
                    .expectNext(sampleEntity)
                    .verifyComplete();

            verify(repository).findByActiveTrue();
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {
        @Test
        @DisplayName("should return entity when found")
        void returnsEntityWhenFound() {
            when(repository.findById(1L)).thenReturn(Mono.just(sampleEntity));

            StepVerifier.create(service.findById(1L))
                    .expectNext(sampleEntity)
                    .verifyComplete();
        }

        @Test
        @DisplayName("should return empty when not found")
        void returnsEmptyWhenNotFound() {
            when(repository.findById(anyLong())).thenReturn(Mono.empty());

            StepVerifier.create(service.findById(999L))
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("create")
    class Create {
        @Test
        @DisplayName("should save and return created entity")
        void savesAndReturnsEntity() {
            Entity input = Entity.builder().name("New").description("New entity").active(true).build();
            Entity saved = Entity.builder().id(3L).name("New").description("New entity").active(true).build();
            when(repository.save(any(Entity.class))).thenReturn(Mono.just(saved));

            StepVerifier.create(service.create(input))
                    .expectNextMatches(e -> e.getId() == 3L && "New".equals(e.getName()))
                    .verifyComplete();

            verify(repository).save(input);
        }
    }

    @Nested
    @DisplayName("update")
    class Update {
        @Test
        @DisplayName("should update existing entity fields")
        void updatesExistingEntity() {
            Entity update = Entity.builder().name("Updated").description("Updated desc").active(false).build();
            Entity updated = Entity.builder().id(1L).name("Updated").description("Updated desc").active(false).build();

            when(repository.findById(1L)).thenReturn(Mono.just(sampleEntity));
            when(repository.save(any(Entity.class))).thenReturn(Mono.just(updated));

            StepVerifier.create(service.update(1L, update))
                    .expectNextMatches(e -> "Updated".equals(e.getName()) && !e.isActive())
                    .verifyComplete();
        }

        @Test
        @DisplayName("should return empty when entity not found for update")
        void returnsEmptyWhenNotFound() {
            when(repository.findById(anyLong())).thenReturn(Mono.empty());

            StepVerifier.create(service.update(999L, sampleEntity))
                    .verifyComplete();

            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {
        @Test
        @DisplayName("should delete entity by id")
        void deletesById() {
            when(repository.deleteById(1L)).thenReturn(Mono.empty());

            StepVerifier.create(service.delete(1L))
                    .verifyComplete();

            verify(repository).deleteById(1L);
        }
    }

    @Nested
    @DisplayName("search")
    class Search {
        @Test
        @DisplayName("should return matching entities")
        void returnsMatchingEntities() {
            when(repository.findByNameContainingIgnoreCase("test"))
                    .thenReturn(Flux.just(sampleEntity));

            StepVerifier.create(service.search("test"))
                    .expectNext(sampleEntity)
                    .verifyComplete();
        }

        @Test
        @DisplayName("should return empty for no matches")
        void returnsEmptyForNoMatches() {
            when(repository.findByNameContainingIgnoreCase("xyz"))
                    .thenReturn(Flux.empty());

            StepVerifier.create(service.search("xyz"))
                    .verifyComplete();
        }
    }
}
