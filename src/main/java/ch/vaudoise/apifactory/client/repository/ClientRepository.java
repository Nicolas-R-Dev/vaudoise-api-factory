package ch.vaudoise.apifactory.client.repository;

import ch.vaudoise.apifactory.client.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
/**
 * Spring Data repository for {@code Client} entities.
 * <p>
 * Inherits standard CRUD operations and provides lookups by natural keys (e.g., email).
 */
public interface ClientRepository extends JpaRepository<Client, Long> {
    /** Finds a client by unique email. */
    Optional<Client> findByEmail(String email);
    boolean existsByEmail(String email);
}
