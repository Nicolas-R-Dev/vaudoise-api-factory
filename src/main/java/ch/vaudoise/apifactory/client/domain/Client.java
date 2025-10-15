package ch.vaudoise.apifactory.client.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;


/**
 * Base entity for clients (both PERSON and COMPANY).
 * <p>
 * Uses inheritance to map subtype-specific attributes into dedicated tables.
 * Includes audit fields ({@code createdAt}, {@code updatedAt}).
 */

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.JOINED) // une table par sous-classe
public abstract class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Discriminator for client type (PERSON or COMPANY). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClientType type;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Pattern(regexp = "^\\+?[0-9 .\\-]{7,20}$")
    @Column(nullable = false)
    private String phone;

    /** Audit: creation timestamp (ISO-8601). */
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    /** Audit: last modification timestamp (ISO-8601). */

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
