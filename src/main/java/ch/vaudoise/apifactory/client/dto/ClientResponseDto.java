package ch.vaudoise.apifactory.client.dto;

import ch.vaudoise.apifactory.client.domain.ClientType;

import java.time.LocalDate;
import java.time.LocalDateTime;
/**
 * Response view for clients.
 * <p>
 * Includes both common and subtype-specific fields to meet the “return all fields” requirement.
 * You may use {@code @JsonInclude(Include.NON_NULL)} to hide null subtype fields if desired.
 */

public record ClientResponseDto(
        Long id,
        ClientType type,
        String name,
        String email,
        String phone,
        LocalDate birthdate,        // Null if company
        String companyIdentifier,   // Null if person
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) { }
