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

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService service;

    /** Create a contract for a client with startDate defaults = today if null */
    @PostMapping("/clients/{clientId}/contracts")
    public ResponseEntity<Void> create(
            @PathVariable Long clientId,
            @Valid @RequestBody ContractCreateDto dto
    ) {
        Long id = service.create(clientId, dto);
        return ResponseEntity.created(URI.create("/api/contracts/" + id)).build();
    }

    /** Update only the cost and updates lastUpdatedAt internally (not exposed) */
    @PatchMapping("/contracts/{id}/cost")
    public ContractResponseDto updateCost(
            @PathVariable Long id,
            @Valid @RequestBody ContractCostUpdateDto dto
    ) {
        BigDecimal newCost = dto.costAmount();
        return service.updateCost(id, newCost);
    }

    /**
     * Get contracts for one client.
     * By default returns only active contracts (active=true).
     * Optional filter by update date (updatedSince).
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
}
