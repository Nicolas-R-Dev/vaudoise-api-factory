package ch.vaudoise.apifactory.client.dto;

import ch.vaudoise.apifactory.client.domain.ClientType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ClientResponseDto(
        Long id,
        ClientType type,
        String name,
        String email,
        String phone,
        LocalDate birthdate,        // null if company
        String companyIdentifier,   // null if person
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) { }
