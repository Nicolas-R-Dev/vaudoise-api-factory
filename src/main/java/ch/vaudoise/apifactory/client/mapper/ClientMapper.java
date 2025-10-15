package ch.vaudoise.apifactory.client.mapper;

import ch.vaudoise.apifactory.client.domain.Client;
import ch.vaudoise.apifactory.client.domain.CompanyClient;
import ch.vaudoise.apifactory.client.domain.PersonClient;
import ch.vaudoise.apifactory.client.dto.ClientResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        imports = { PersonClient.class, CompanyClient.class })
public interface ClientMapper {

    @Mapping(target = "birthdate",
            expression = "java(client instanceof PersonClient pc ? pc.getBirthdate() : null)")
    @Mapping(target = "companyIdentifier",
            expression = "java(client instanceof CompanyClient cc ? cc.getCompanyIdentifier() : null)")
    ClientResponseDto toDto(Client client);
}
