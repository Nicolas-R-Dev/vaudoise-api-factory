package ch.vaudoise.apifactory.client.repository;

import ch.vaudoise.apifactory.client.domain.PersonClient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonClientRepository extends JpaRepository<PersonClient, Long> { }
