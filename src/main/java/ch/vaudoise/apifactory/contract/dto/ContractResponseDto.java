package ch.vaudoise.apifactory.contract.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContractResponseDto(
        Long id,
        Long clientId,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal costAmount
) { }
