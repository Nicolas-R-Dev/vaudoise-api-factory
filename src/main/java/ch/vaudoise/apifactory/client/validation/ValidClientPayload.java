package ch.vaudoise.apifactory.client.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;
/**
 * Class-level constraint for {@code ClientCreateDto}.
 * <p>
 * Enforces conditional requirements:
 * <ul>
 *   <li>PERSON -> {@code birthdate} must be present</li>
 *   <li>COMPANY -> {@code companyIdentifier} must be present</li>
 * </ul>
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ValidClientPayloadValidator.class)
public @interface ValidClientPayload {
    String message() default "Invalid client payload";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
