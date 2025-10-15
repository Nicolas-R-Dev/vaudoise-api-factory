package ch.vaudoise.apifactory.client.service;

import ch.vaudoise.apifactory.client.domain.*;
import ch.vaudoise.apifactory.client.dto.*;
import ch.vaudoise.apifactory.client.mapper.ClientMapper;
import ch.vaudoise.apifactory.client.repository.*;
import ch.vaudoise.apifactory.common.exception.ConflictException;
import ch.vaudoise.apifactory.common.exception.NotFoundException;
import ch.vaudoise.apifactory.contract.repository.ContractRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Default implementation of {@link ClientService}.
 * <p>
 * Applies type-specific validations (PERSON/COMPANY), cross-aggregate operations
 * (closing/deleting contracts on client removal), and delegates persistence to repositories.
 */
@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepo;
    private final PersonClientRepository personRepo;
    private final CompanyClientRepository companyRepo;
    private final ContractRepository contractRepo;
    private final ClientMapper mapper;

    /** {@inheritDoc} */
    @Override
    public Long create(ClientCreateDto dto) {
        if (clientRepo.existsByEmail(dto.email())) {
            throw new ConflictException("email already exists");
        }
        if (dto.type() == ClientType.COMPANY) {
            String id = dto.companyIdentifier();
            if (companyRepo.existsByCompanyIdentifier(id)) {
                throw new ConflictException("companyIdentifier already exists");
            }
        }

        Client saved;
        if (dto.type() == ClientType.PERSON) {
            var p = new PersonClient();
            p.setType(ClientType.PERSON);
            p.setName(dto.name()); p.setEmail(dto.email()); p.setPhone(dto.phone());
            if (dto.birthdate() == null) throw new IllegalArgumentException("birthdate is required for PERSON");
            p.setBirthdate(dto.birthdate());
            saved = personRepo.save(p);
        } else {
            var c = new CompanyClient();
            c.setType(ClientType.COMPANY);
            c.setName(dto.name()); c.setEmail(dto.email()); c.setPhone(dto.phone());
            if (dto.companyIdentifier() == null) throw new IllegalArgumentException("companyIdentifier is required for COMPANY");
            c.setCompanyIdentifier(dto.companyIdentifier());
            saved = companyRepo.save(c);
        }
        return saved.getId();
    }

    /** {@inheritDoc} */
    @Override
    public ClientResponseDto get(Long id) {
        var client = clientRepo.findById(id).orElseThrow(() -> new NotFoundException("Client "+id+" not found"));
        return mapper.toDto(client);
    }

    /** {@inheritDoc} */
    @Override
    public ClientResponseDto update(Long id, ClientUpdateDto dto) {
        var client = clientRepo.findById(id).orElseThrow(() -> new NotFoundException("Client "+id+" not found"));
        client.setName(dto.name());
        client.setEmail(dto.email());
        client.setPhone(dto.phone());
        return mapper.toDto(clientRepo.save(client));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void delete(Long id) {
        if (!clientRepo.existsById(id)) {
            throw new NotFoundException("Client " + id + " not found");
        }
        var today = LocalDate.now();
        contractRepo.closeAllActiveByClient(id, today);
        contractRepo.deleteAllByClient(id);
        clientRepo.deleteById(id);
    }

}
