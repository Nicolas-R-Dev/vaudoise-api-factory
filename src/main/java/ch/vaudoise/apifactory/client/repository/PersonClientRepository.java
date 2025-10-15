package ch.vaudoise.apifactory.client.repository;

import ch.vaudoise.apifactory.client.domain.PersonClient;
import org.springframework.data.jpa.repository.JpaRepository;
/** Spring Data repository for the {@code PersonClient}/{@code CompanyClient} subtype. */
public interface PersonClientRepository extends JpaRepository<PersonClient, Long> { }
