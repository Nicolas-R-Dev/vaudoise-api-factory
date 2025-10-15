package ch.vaudoise.apifactory.contract.dto;

import java.math.BigDecimal;

public record SumResponseDto(Long clientId, BigDecimal activeSum) { }
