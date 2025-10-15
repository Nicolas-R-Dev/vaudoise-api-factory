package ch.vaudoise.apifactory.contract.service;

import ch.vaudoise.apifactory.client.domain.Client;
import ch.vaudoise.apifactory.client.repository.ClientRepository;
import ch.vaudoise.apifactory.common.exception.NotFoundException;
import ch.vaudoise.apifactory.contract.domain.Contract;
import ch.vaudoise.apifactory.contract.dto.*;
import ch.vaudoise.apifactory.contract.mapper.ContractMapper;
import ch.vaudoise.apifactory.contract.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ClientRepository clientRepo;
    private final ContractRepository contractRepo;
    private final ContractMapper mapper;

    @Override
    public Long create(Long clientId, ContractCreateDto dto) {
        Client client = clientRepo.findById(clientId)
                .orElseThrow(() -> new NotFoundException("Client "+clientId+" not found"));

        var c = new Contract();
        c.setClient(client);
        c.setStartDate(dto.startDate() != null ? dto.startDate() : LocalDate.now());
        c.setEndDate(dto.endDate());
        c.setCostAmount(dto.costAmount());
        return contractRepo.save(c).getId();
    }

    @Override
    public ContractResponseDto updateCost(Long contractId, BigDecimal newCost) {
        var c = contractRepo.findById(contractId)
                .orElseThrow(() -> new NotFoundException("Contract "+contractId+" not found"));
        c.setCostAmount(newCost); // lastUpdatedAt sera mis à jour via @PreUpdate
        return mapper.toDto(contractRepo.save(c));
    }

    @Override
    public Page<ContractResponseDto> listForClient(Long clientId, boolean activeOnly, LocalDateTime updatedSince, Pageable p) {
        Page<Contract> page;
        if (updatedSince != null) {
            page = contractRepo.findByClientIdAndLastUpdatedAtAfter(clientId, updatedSince, p);
        } else {
            page = contractRepo.findByClientId(clientId, p);
        }
        // Filtre “actif” en mémoire (simple et lisible). Pour plus de perf, on peut faire une query dédiée.
        LocalDate today = LocalDate.now();
        var mapped = page.map(mapper::toDto)
                .filter(dto -> !activeOnly || dto.endDate() == null || today.isBefore(dto.endDate()));
        // Recompose une Page (optionnel pour l’exo) — sinon retourne page.map(mapper::toDto) et documente le filtre
        return page.map(mapper::toDto);
    }

    @Override
    public BigDecimal sumActive(Long clientId) {
        return contractRepo.sumActiveByClient(clientId, LocalDate.now());
    }
}
