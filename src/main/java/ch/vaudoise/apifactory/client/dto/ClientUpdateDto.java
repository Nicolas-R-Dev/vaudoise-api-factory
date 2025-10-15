package ch.vaudoise.apifactory.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;

/**
 * Request payload to update mutable client fields.
 * <p>
 * Immutable attributes (birthdate, companyIdentifier) are intentionally excluded.
 */

@JsonIgnoreProperties(ignoreUnknown = false)
public record ClientUpdateDto(
        @NotBlank String name,
        @Email @NotBlank String email,
        @Pattern(regexp = "^\\+?[0-9 .\\-]{7,20}$") @NotBlank String phone
) { }
