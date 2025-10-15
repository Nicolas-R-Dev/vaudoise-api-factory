package ch.vaudoise.apifactory.client.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


/**
 * Client subtype representing a natural person.
 * <p>
 * Adds a {@code birthdate} attribute required for PERSON clients.
 */


@Getter
@Setter
@Entity
public class PersonClient extends Client {

    @PastOrPresent
    @Column(nullable = false)
    private LocalDate birthdate; // immuable after the creation
}
