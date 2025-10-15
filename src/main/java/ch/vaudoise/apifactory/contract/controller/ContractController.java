package ch.vaudoise.apifactory.contract.controller;

import ch.vaudoise.apifactory.contract.dto.*;
import ch.vaudoise.apifactory.contract.service.ContractService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.NO_CONTENT;

/**
 * REST controller exposing endpoints to manage contracts for a given client.
 * <p>
 * Supports single and batch creation, filtered listing (active / updatedSince),
 * partial update of cost, and deletion.
 * Validation is handled via Jakarta Validation and centralized error handling.
 */

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService service;

    /**
     * Creates a contract for a client.
     * <p>
     * If {@code startDate} is null, it defaults to today's date. {@code endDate} may be null.
     *
     * @param clientId the owner client id
     * @param dto      contract creation payload
     * @return {@code 201 Created} with a {@code Location} header to the new resource
     */

    @PostMapping("/clients/{clientId}/contracts")
    public ResponseEntity<Void> create(
            @PathVariable Long clientId,
            @Valid @RequestBody ContractCreateDto dto
    ) {
        Long id = service.create(clientId, dto);
        return ResponseEntity.created(URI.create("/api/contracts/" + id)).build();
    }

    /**
     * Updates only the cost amount of a contract.
     * <p>
     * Also updates the internal {@code lastUpdatedAt} timestamp.
     *
     * @param id     contract id
     * @param dto    payload containing the new cost amount
     * @return {@code 200 OK} with the updated representation
     */

    @PatchMapping("/contracts/{id}/cost")
    public ContractResponseDto updateCost(
            @PathVariable Long id,
            @Valid @RequestBody ContractCostUpdateDto dto
    ) {
        BigDecimal newCost = dto.costAmount();
        return service.updateCost(id, newCost);
    }

    /**
     * Batch-creates multiple contracts for a client in a single transaction.
     * <p>
     * The operation is all-or-nothing: if any item is invalid, the entire batch is rolled back.
     *
     * @param clientId owner client id
     * @param items    list of contract payloads to create
     * @return {@code 201 Created} with a body listing created ids
     */

    @PostMapping("/clients/{clientId}/contracts/batch")
    public ResponseEntity<Map<String, Object>> createContractsBatch(
            @PathVariable Long clientId,
            @RequestBody List<@Valid ContractCreateDto> items
    ) {
        List<Long> ids = service.createBatch(clientId, items);
        return ResponseEntity.status(201).body(Map.of("createdIds", ids));
    }

    /**
     * Lists contracts for a client with optional filters.
     * <ul>
     *   <li>{@code active=true} (default): only active contracts (endDate is null or in the future)</li>
     *   <li>{@code updatedSince}: only contracts whose {@code lastUpdatedAt} is after the given timestamp</li>
     *   <li>Supports pagination and sorting through {@code Pageable}</li>
     * </ul>
     *
     * @param clientId     owner client id
     * @param active       filter for active contracts (defaults to {@code true})
     * @param updatedSince optional timestamp to filter by last update
     * @param pageable     pagination configuration
     * @return a page of {@code ContractResponseDto}
     */

    @GetMapping("/clients/{clientId}/contracts")
    public Page<ContractResponseDto> listForClient(
            @PathVariable Long clientId,
            @RequestParam(defaultValue = "true") boolean active,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime updatedSince,
            Pageable pageable
    ) {
        return service.listForClient(clientId, active, updatedSince, pageable);
    }

    /** Very performant endpoint: sum of costAmount of active contracts for one client */
    @GetMapping("/clients/{clientId}/contracts/sum")
    public SumResponseDto sumActive(@PathVariable Long clientId) {
        return new SumResponseDto(clientId, service.sumActive(clientId));
    }

    /**
     * Deletes a contract by id.
     *
     * @param id contract identifier
     * @return {@code 204 No Content}
     */

    @DeleteMapping("/contracts/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

}
