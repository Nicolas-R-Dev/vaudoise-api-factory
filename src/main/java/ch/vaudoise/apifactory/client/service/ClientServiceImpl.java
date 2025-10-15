package ch.vaudoise.apifactory.client.service;

import ch.vaudoise.apifactory.client.domain.*;
import ch.vaudoise.apifactory.client.dto.*;
import ch.vaudoise.apifactory.client.mapper.ClientMapper;
import ch.vaudoise.apifactory.client.repository.*;
import ch.vaudoise.apifactory.common.exception.NotFoundException;
import ch.vaudoise.apifactory.contract.repository.ContractRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepo;
    private final PersonClientRepository personRepo;
    private final CompanyClientRepository companyRepo;
    private final ContractRepository contractRepo;
    private final ClientMapper mapper;

    @Override
    public Long create(ClientCreateDto dto) {
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

    @Override
    public ClientResponseDto get(Long id) {
        var client = clientRepo.findById(id).orElseThrow(() -> new NotFoundException("Client "+id+" not found"));
        return mapper.toDto(client);
    }

    @Override
    public ClientResponseDto update(Long id, ClientUpdateDto dto) {
        var client = clientRepo.findById(id).orElseThrow(() -> new NotFoundException("Client "+id+" not found"));
        client.setName(dto.name());
        client.setEmail(dto.email());
        client.setPhone(dto.phone());
        return mapper.toDto(clientRepo.save(client));
    }


    // This Methode Delete all the active contract and after delete the client
    @Override
    @Transactional
    public void delete(Long id) {
        var today = LocalDate.now();
        contractRepo.closeAllActiveByClient(id, today);
        clientRepo.deleteById(id);
    }
}
