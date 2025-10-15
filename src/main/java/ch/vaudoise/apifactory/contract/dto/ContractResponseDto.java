package ch.vaudoise.apifactory.contract.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
/**
 * Read model returned by contract endpoints.
 * <p>
 * Exposes primary identifiers, dates, and monetary values needed by clients.
 */

public record ContractResponseDto(
        Long id,
        Long clientId,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal costAmount
) { }
