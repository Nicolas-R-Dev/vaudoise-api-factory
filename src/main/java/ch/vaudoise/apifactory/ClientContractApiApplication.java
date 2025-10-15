package ch.vaudoise.apifactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * Spring Boot application entry point.
 * <p>
 * Boots the Client-Contract API with a persistent H2 database and exposes
 * the H2 console for local development.
 */

@SpringBootApplication
public class ClientContractApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientContractApiApplication.class, args);
	}

}
