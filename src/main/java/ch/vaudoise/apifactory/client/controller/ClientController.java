package ch.vaudoise.apifactory.client.controller;

import ch.vaudoise.apifactory.client.dto.ClientCreateDto;
import ch.vaudoise.apifactory.client.dto.ClientResponseDto;
import ch.vaudoise.apifactory.client.dto.ClientUpdateDto;
import ch.vaudoise.apifactory.client.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService service;

    /** Create a client (PERSON or COMPANY) */
    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody ClientCreateDto dto) {
        Long id = service.create(dto);
        return ResponseEntity.created(URI.create("/api/clients/" + id)).build();
    }

    /** Read a client */
    @GetMapping("/{id}")
    public ClientResponseDto get(@PathVariable Long id) {
        return service.get(id);
    }

    /** Update mutable fields only (name, email, phone) */
    @PutMapping("/{id}")
    public ClientResponseDto update(@PathVariable Long id, @Valid @RequestBody ClientUpdateDto dto) {
        return service.update(id, dto);
    }

    /** Delete a client: close active contracts with endDate=today, after that delete client */
    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
