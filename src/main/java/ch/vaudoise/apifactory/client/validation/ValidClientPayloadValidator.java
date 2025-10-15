package ch.vaudoise.apifactory.client.validation;

import ch.vaudoise.apifactory.client.domain.ClientType;
import ch.vaudoise.apifactory.client.dto.ClientCreateDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
/**
 * Validator implementation for {@link ValidClientPayload}.
 * <p>
 * Adds field-level violations with meaningful messages and targets the correct property nodes
 * so that error responses can indicate the exact field (and index for batch inputs).
 */

public class ValidClientPayloadValidator implements ConstraintValidator<ValidClientPayload, ClientCreateDto> {

    /** This method valid the required field */
    @Override
    public boolean isValid(ClientCreateDto dto, ConstraintValidatorContext ctx) {
        if (dto == null || dto.type() == null) return true; // autres @NotNull gèrent ça

        boolean ok = true;
        ctx.disableDefaultConstraintViolation();

        if (dto.type() == ClientType.PERSON) {
            if (dto.birthdate() == null) {
                ctx.buildConstraintViolationWithTemplate("birthdate is required for PERSON")
                        .addPropertyNode("birthdate").addConstraintViolation();
                ok = false;
            }
        } else if (dto.type() == ClientType.COMPANY) {
            if (dto.companyIdentifier() == null || dto.companyIdentifier().isBlank()) {
                ctx.buildConstraintViolationWithTemplate("companyIdentifier is required for COMPANY")
                        .addPropertyNode("companyIdentifier").addConstraintViolation();
                ok = false;
            }
        }
        return ok;
    }
}
