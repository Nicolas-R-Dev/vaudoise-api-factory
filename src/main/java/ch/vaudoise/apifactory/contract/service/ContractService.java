package ch.vaudoise.apifactory.contract.service;

import ch.vaudoise.apifactory.contract.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ContractService {
    Long create(Long clientId, ContractCreateDto dto);
    ContractResponseDto updateCost(Long contractId, BigDecimal newCost);
    Page<ContractResponseDto> listForClient(Long clientId, boolean activeOnly, LocalDateTime updatedSince, Pageable p);
    BigDecimal sumActive(Long clientId);
}
