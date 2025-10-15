package ch.vaudoise.apifactory.contract.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ContractCostUpdateDto(
        @NotNull @DecimalMin("0.01") @Digits(integer = 12, fraction = 2)
        BigDecimal costAmount
) { }
