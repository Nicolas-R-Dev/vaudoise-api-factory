package ch.vaudoise.apifactory.contract.domain;

import ch.vaudoise.apifactory.client.domain.Client;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


/**
 * Contract entity linked to a {@link Client}.
 * <p>
 * {@code startDate} defaults to today's date if not provided; {@code endDate} may be null.
 * {@code lastUpdatedAt} is maintained automatically via lifecycle callbacks
 * and is used by the {@code updatedSince} filter.
 */


@Getter
@Setter
@Entity
@Table(
        name = "contract",
        indexes = {
                @Index(name = "idx_contract_client_enddate", columnList = "client_id, end_date"),
                @Index(name = "idx_contract_client_updated", columnList = "client_id, last_updated_at")
        }
)
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Le client lié à ce contrat (obligatoire)
     * - ManyToOne car plusieurs contrats peuvent appartenir à un même client
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    /**
     * Date de début du contrat
     * - Défaut : la date du jour si non renseignée
     */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate = LocalDate.now();

    /**
     * Date de fin du contrat
     * - Null = contrat actif
     */
    @Column(name = "end_date")
    private LocalDate endDate;

    /**
     * Montant du contrat
     * - Toujours > 0
     */
    @NotNull
    @DecimalMin("0.01")
    @Digits(integer = 12, fraction = 2)
    @Column(name = "cost_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal costAmount;

    /**
     * Date de dernière mise à jour interne
     * - Non exposée dans l’API
     * - Mise à jour automatique à chaque modification du coût
     */
    @Column(name = "last_updated_at", nullable = false)
    private LocalDateTime lastUpdatedAt = LocalDateTime.now();

    /**
     * Callback JPA pour mettre à jour la date à chaque update.
     */
    @PreUpdate
    void onUpdate() {
        this.lastUpdatedAt = LocalDateTime.now();
    }
}
