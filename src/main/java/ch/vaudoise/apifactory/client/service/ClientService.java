package ch.vaudoise.apifactory.client.service;

import ch.vaudoise.apifactory.client.dto.*;

import java.util.List;

/**
 * Application service for client use-cases.
 * <p>
 * Encapsulates business rules and delegates persistence to repositories.
 * Exposes operations to create, read, update, delete, and batch-create clients.
 */

public interface ClientService {
    /** Creates a new client and returns its identifier. */
    Long create(ClientCreateDto dto);

    /** Retrieves a client by id or throws {@code NotFoundException} if absent. */
    ClientResponseDto get(Long id);
    /**
     * Updates mutable fields of a client (name, email, phone) and returns the updated view.
     * Immutable fields (birthdate, companyIdentifier) are intentionally not part of the update DTO.
     */
    ClientResponseDto update(Long id, ClientUpdateDto dto);

    /**
     * Deletes a client after closing and removing its contracts.
     * <p>
     * Business rule: all active contracts are first closed (endDate = today),
     * then contracts are deleted, then the client is deleted to preserve referential integrity.
     */
    void delete(Long id);
}
