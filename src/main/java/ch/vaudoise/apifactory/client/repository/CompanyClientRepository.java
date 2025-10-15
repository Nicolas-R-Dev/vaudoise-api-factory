package ch.vaudoise.apifactory.client.repository;

import ch.vaudoise.apifactory.client.domain.CompanyClient;
import org.springframework.data.jpa.repository.JpaRepository;
/** Spring Data repository for the {@code PersonClient}/{@code CompanyClient} subtype. */
public interface CompanyClientRepository extends JpaRepository<CompanyClient, Long> {

    boolean existsByCompanyIdentifier(String companyIdentifier);
}

