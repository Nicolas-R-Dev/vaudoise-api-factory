package ch.vaudoise.apifactory.client.controller;

import ch.vaudoise.apifactory.client.dto.ClientCreateDto;
import ch.vaudoise.apifactory.client.dto.ClientResponseDto;
import ch.vaudoise.apifactory.client.dto.ClientUpdateDto;
import ch.vaudoise.apifactory.client.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static org.springframework.http.HttpStatus.NO_CONTENT;
/**
 * REST controller exposing endpoints to manage clients (PERSON and COMPANY).
 * <p>
 * Handles creation, retrieval, update, and deletion, plus batch operations.
 * Validation is performed via Jakarta Validation, and all errors are handled centrally.
 */
@Validated
@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService service;


    /**
     * Creates a new client.
     * <p>
     * Business rules:
     * <ul>
     *   <li>PERSON requires a non-null birthdate</li>
     *   <li>COMPANY requires a non-null companyIdentifier</li>
     *   <li>Email must be unique</li>
     * </ul>
     *
     * @param dto payload describing the client to create
     * @return {@code 201 Created} with a {@code Location} header to the new resource
     */
    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody ClientCreateDto dto) {
        Long id = service.create(dto);
        return ResponseEntity.created(URI.create("/api/clients/" + id)).build();
    }

    /**
     * Batch-create multiple clients in a single request.
     * <p>
     * The operation is transactional: if one element is invalid,
     * the entire batch is rolled back.
     *
     * @param clients list of client payloads to create
     * @return {@code 201 Created}
     */
    @PostMapping("/batch")
    public ResponseEntity<Void> createMany(@RequestBody List<@Valid ClientCreateDto> clients) {
        clients.forEach(service::create);
        return ResponseEntity.status(201).build();
    }

    /**
     * Retrieves a client by its identifier.
     *
     * @param id client identifier
     * @return {@link ClientResponseDto} representing the full client
     * @throws ChangeSetPersister.NotFoundException if the client does not exist
     */
    @GetMapping("/{id}")
    public ClientResponseDto get(@PathVariable Long id) {
        return service.get(id);
    }

    /**
     * Updates mutable fields of a client (name, email, phone).
     * <p>
     * Immutable fields (birthdate and companyIdentifier) are not exposed
     * in the {@link ClientUpdateDto}.
     *
     * @param id  client identifier
     * @param dto payload containing new values for mutable fields
     * @return updated {@link ClientResponseDto}
     */
    @PutMapping("/{id}")
    public ClientResponseDto update(@PathVariable Long id, @Valid @RequestBody ClientUpdateDto dto) {
        return service.update(id, dto);
    }

    /**
     * Deletes a client.
     * <p>
     * Business rules:
     * <ul>
     *   <li>All active contracts for this client are first closed (endDate = today)</li>
     *   <li>Contracts are then deleted</li>
     *   <li>Finally, the client itself is deleted</li>
     * </ul>
     * This ensures referential integrity in the database.
     *
     * @param id client identifier
     * @return {@code 204 No Content}
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
