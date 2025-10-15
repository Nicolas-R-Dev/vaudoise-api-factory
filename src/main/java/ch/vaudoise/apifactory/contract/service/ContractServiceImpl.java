package ch.vaudoise.apifactory.contract.service;

import ch.vaudoise.apifactory.client.domain.Client;
import ch.vaudoise.apifactory.client.repository.ClientRepository;
import ch.vaudoise.apifactory.common.exception.BadRequestException;
import ch.vaudoise.apifactory.common.exception.NotFoundException;
import ch.vaudoise.apifactory.contract.domain.Contract;
import ch.vaudoise.apifactory.contract.dto.*;
import ch.vaudoise.apifactory.contract.mapper.ContractMapper;
import ch.vaudoise.apifactory.contract.repository.ContractRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/**
 * Default implementation of {@link ContractService}.
 * <p>
 * Enforces business rules (e.g., start/end date consistency, positive cost),
 * maintains update timestamps, and delegates database access to repositories.
 */
@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ClientRepository clientRepo;
    private final ContractRepository contractRepo;
    private final ContractMapper mapper;

    /** {@inheritDoc} */
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
    /** {@inheritDoc} */
    @Override
    @Transactional
    public List<Long> createBatch(Long clientId, List<ContractCreateDto> items) {
        Client client = clientRepo.findById(clientId)
                .orElseThrow(() -> new NotFoundException("Client " + clientId + " not found"));

        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            ContractCreateDto dto = items.get(i);

            if (dto.costAmount() == null || dto.costAmount().signum() <= 0) {
                throw new BadRequestException("[" + i + "] costAmount must be > 0");
            }

            LocalDate start = dto.startDate() != null ? dto.startDate() : LocalDate.now();
            if (dto.endDate() != null && dto.endDate().isBefore(start)) {
                throw new BadRequestException("[" + i + "] endDate must be >= startDate");
            }

            Contract c = new Contract();
            c.setClient(client);
            c.setCostAmount(dto.costAmount());
            c.setStartDate(start);
            c.setEndDate(dto.endDate());

            contractRepo.save(c);
            ids.add(c.getId());
        }
        return ids;
    }


    /** {@inheritDoc} */
    @Override
    public ContractResponseDto updateCost(Long contractId, BigDecimal newCost) {
        var c = contractRepo.findById(contractId)
                .orElseThrow(() -> new NotFoundException("Contract "+contractId+" not found"));
        c.setCostAmount(newCost); // lastUpdatedAt sera mis à jour via @PreUpdate
        return mapper.toDto(contractRepo.save(c));
    }

    /** {@inheritDoc} */
    @Override
    public Page<ContractResponseDto> listForClient(Long clientId, boolean activeOnly,
                                                   LocalDateTime updatedSince, Pageable p) {
        Page<Contract> page;
        if (activeOnly) {
            LocalDate today = LocalDate.now();
            page = (updatedSince != null)
                    ? contractRepo.findActiveByClientUpdatedSince(clientId, today, updatedSince, p)
                    : contractRepo.findActiveByClient(clientId, today, p);
        } else {
            // tout voir
            page = (updatedSince != null)
                    ? contractRepo.findByClientIdAndLastUpdatedAtAfter(clientId, updatedSince, p)
                    : contractRepo.findByClientId(clientId, p);
        }
        return page.map(mapper::toDto);
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal sumActive(Long clientId) {
        return contractRepo.sumActiveByClient(clientId, LocalDate.now());
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void delete(Long id) {
        if (!contractRepo.existsById(id)) {
            throw new NotFoundException("Contract " + id + " not found");
        }
        contractRepo.deleteById(id);
    }

}
