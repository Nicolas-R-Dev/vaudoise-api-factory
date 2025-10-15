package ch.vaudoise.apifactory.client.service;

import ch.vaudoise.apifactory.client.dto.*;

public interface ClientService {
    Long create(ClientCreateDto dto);
    ClientResponseDto get(Long id);
    ClientResponseDto update(Long id, ClientUpdateDto dto);
    void delete(Long id);
}
