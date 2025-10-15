package ch.vaudoise.apifactory.client.repository;

import ch.vaudoise.apifactory.client.domain.CompanyClient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyClientRepository extends JpaRepository<CompanyClient, Long> { }
