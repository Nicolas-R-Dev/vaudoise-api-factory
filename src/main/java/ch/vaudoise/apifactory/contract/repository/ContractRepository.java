package ch.vaudoise.apifactory.contract.repository;

import ch.vaudoise.apifactory.contract.domain.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Spring Data repository for {@code Contract} entities.
 * <p>
 * Contains JPQL queries for active filters and time-based lookups to keep filtering at DB level.
 */
public interface ContractRepository extends JpaRepository<Contract, Long> {

    /**
     * Sums the {@code costAmount} of all active contracts for a client.
     * <p>
     * Active = {@code endDate} is null or strictly in the future.
     */
    @Query("""
      SELECT COALESCE(SUM(c.costAmount), 0)
      FROM Contract c
      WHERE c.client.id = :clientId
        AND (c.endDate IS NULL OR :today < c.endDate)
    """)
    BigDecimal sumActiveByClient(@Param("clientId") Long clientId, @Param("today") LocalDate today);

    /**
     * Closes all active contracts for a client by setting {@code endDate} to {@code :today}.
     * <p>
     * Returns the number of updated rows.
     */
    @Modifying
    @Query("""
      UPDATE Contract c SET c.endDate = :today
      WHERE c.client.id = :clientId
        AND (c.endDate IS NULL OR :today < c.endDate)
    """)
    int closeAllActiveByClient(@Param("clientId") Long clientId, @Param("today") LocalDate today);


    /**
     * Deletes all contracts for a client.
     * <p>
     * Typically called after closing contracts, prior to deleting the client to satisfy FK constraints.
     */
    // Supprime tous les contrats du client (après fermeture)
    @Modifying
    @Query("DELETE FROM Contract c WHERE c.client.id = :clientId")
    int deleteAllByClient(@Param("clientId") Long clientId);


    // Listing filtrable par lastUpdatedAt (optionnel côté service)
    Page<Contract> findByClientIdAndLastUpdatedAtAfter(Long clientId, LocalDateTime updatedSince, Pageable p);

    /** Finds active contracts for a client (endDate is null or in the future). */
    @Query("""
  SELECT c FROM Contract c
  WHERE c.client.id = :clientId
    AND (c.endDate IS NULL OR :today < c.endDate)
""")
    Page<Contract> findActiveByClient(@Param("clientId") Long clientId,
                                      @Param("today") LocalDate today,
                                      Pageable pageable);

    /** Finds active contracts updated after a given timestamp. */
    @Query("""
  SELECT c FROM Contract c
  WHERE c.client.id = :clientId
    AND (c.endDate IS NULL OR :today < c.endDate)
    AND c.lastUpdatedAt > :updatedSince
""")
    Page<Contract> findActiveByClientUpdatedSince(@Param("clientId") Long clientId,
                                                  @Param("today") LocalDate today,
                                                  @Param("updatedSince") LocalDateTime updatedSince,
                                                  Pageable pageable);

    Page<Contract> findByClientId(Long clientId, Pageable p);
}
