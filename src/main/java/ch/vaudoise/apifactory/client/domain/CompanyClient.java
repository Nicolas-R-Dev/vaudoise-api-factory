package ch.vaudoise.apifactory.client.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;


/**
 * Client subtype representing a company.
 * <p>
 * Adds a {@code companyIdentifier} attribute required for COMPANY clients.
 */

@Getter
@Setter
@Entity
public class CompanyClient extends Client {

    @Pattern(regexp = "^[A-Z]{3}-\\d{3}$")
    @Column(nullable = false, unique = true)
    private String companyIdentifier; // immuable after the creation
}
