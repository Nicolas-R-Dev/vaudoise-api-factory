package ch.vaudoise.apifactory.contract.service;

import ch.vaudoise.apifactory.contract.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Application service for contract use-cases.
 * <p>
 * Provides creation (single/batch), filtered listing, cost update, sum of active contracts,
 * and deletion functionality.
 */
public interface ContractService {
    /** Creates a contract for the given client and returns its identifier. */
    Long create(Long clientId, ContractCreateDto dto);

    /** Batch-creates contracts in a single transaction and returns the created ids. */
    List<Long> createBatch(Long clientId, List<ContractCreateDto> items);

    /** Updates the cost amount of a contract and returns the updated view. */
    ContractResponseDto updateCost(Long contractId, BigDecimal newCost);
    /**
     * Lists contracts for a client with optional filters.
     * <p>
     * If {@code activeOnly} is true, returns only active contracts.
     * If {@code updatedSince} is not null, returns contracts modified after that timestamp.
     */
    Page<ContractResponseDto> listForClient(Long clientId, boolean activeOnly, LocalDateTime updatedSince, Pageable p);
    /** Computes the sum of {@code costAmount} over all active contracts for a client. */
    BigDecimal sumActive(Long clientId);
    /** Deletes a contract by id. */
    void delete(Long id);

}
