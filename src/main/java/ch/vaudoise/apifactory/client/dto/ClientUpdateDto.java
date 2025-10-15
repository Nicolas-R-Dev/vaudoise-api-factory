package ch.vaudoise.apifactory.client.dto;

import jakarta.validation.constraints.*;

public record ClientUpdateDto(
        @NotBlank String name,
        @Email @NotBlank String email,
        @Pattern(regexp = "^\\+?[0-9 .\\-]{7,20}$") @NotBlank String phone
) { }
