package ch.vaudoise.apifactory.contract.repository;

import ch.vaudoise.apifactory.contract.domain.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    // Somme des contrats actifs d’un client (end_date null OU today < end_date)
    @Query("""
      SELECT COALESCE(SUM(c.costAmount), 0)
      FROM Contract c
      WHERE c.client.id = :clientId
        AND (c.endDate IS NULL OR :today < c.endDate)
    """)
    BigDecimal sumActiveByClient(@Param("clientId") Long clientId, @Param("today") LocalDate today);

    // Clôture en masse tous les contrats actifs d’un client
    @Modifying
    @Query("""
      UPDATE Contract c SET c.endDate = :today
      WHERE c.client.id = :clientId
        AND (c.endDate IS NULL OR :today < c.endDate)
    """)
    int closeAllActiveByClient(@Param("clientId") Long clientId, @Param("today") LocalDate today);

    // Listing filtrable par lastUpdatedAt (optionnel côté service)
    Page<Contract> findByClientIdAndLastUpdatedAtAfter(Long clientId, LocalDateTime updatedSince, Pageable p);

    Page<Contract> findByClientId(Long clientId, Pageable p);
}
