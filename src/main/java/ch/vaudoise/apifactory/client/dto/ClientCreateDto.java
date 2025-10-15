package ch.vaudoise.apifactory.client.dto;

import ch.vaudoise.apifactory.client.domain.ClientType;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record ClientCreateDto(
        @NotNull ClientType type,
        @NotBlank String name,
        @Email @NotBlank String email,
        @Pattern(regexp = "^\\+?[0-9 .\\-]{7,20}$") @NotBlank String phone,
        // needed if type=PERSON
        @PastOrPresent LocalDate birthdate,
        // needed if type=COMPANY
        @Pattern(regexp = "^[A-Z]{3}-\\d{3}$") String companyIdentifier
) { }
