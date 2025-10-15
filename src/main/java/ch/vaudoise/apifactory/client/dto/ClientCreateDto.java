package ch.vaudoise.apifactory.client.dto;

import ch.vaudoise.apifactory.client.domain.ClientType;
import ch.vaudoise.apifactory.client.validation.ValidClientPayload;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * Request payload to create a client.
 * <p>
 * Business rules:
 * <ul>
 *   <li>PERSON requires {@code birthdate}</li>
 *   <li>COMPANY requires {@code companyIdentifier}</li>
 * </ul>
 * Field-level constraints are enforced via Jakarta Validation.
 */

@ValidClientPayload
public record ClientCreateDto(
        @NotNull ClientType type,
        @NotBlank String name,
        @Email @NotBlank String email,
        @Pattern(regexp = "^\\+?[0-9 .\\-]{7,20}$") @NotBlank String phone,

        /** Needed if type=PERSON*/
        @PastOrPresent LocalDate birthdate,
        /** Needed if type=COMPANY*/
        @Pattern(regexp = "^[A-Z]{3}-\\d{3}$") String companyIdentifier
) { }
