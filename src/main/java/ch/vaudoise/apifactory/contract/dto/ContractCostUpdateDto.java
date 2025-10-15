package ch.vaudoise.apifactory.contract.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
/**
 * Patch payload to update only the {@code costAmount} of a contract.
 * <p>
 * Triggers an update of the internal {@code lastUpdatedAt} timestamp.
 */

public record ContractCostUpdateDto(
        @NotNull @DecimalMin("0.01") @Digits(integer = 12, fraction = 2)
        BigDecimal costAmount
) { }
