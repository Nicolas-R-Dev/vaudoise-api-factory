package ch.vaudoise.apifactory.contract.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
/**
 * Request payload to create a contract.
 * <p>
 * {@code startDate} may be omitted (defaults to today). {@code endDate} may be null.
 * {@code costAmount} must be positive and uses standard decimal constraints.
 */

public record ContractCreateDto(
        LocalDate startDate,        // si null -> today côté service
        LocalDate endDate,
        @NotNull @DecimalMin("0.01") @Digits(integer = 12, fraction = 2)
        BigDecimal costAmount
) { }
