package ch.vaudoise.apifactory.contract.mapper;

import ch.vaudoise.apifactory.contract.domain.Contract;
import ch.vaudoise.apifactory.contract.dto.ContractResponseDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ContractMapper {
    @Mapping(target = "clientId", source = "client.id")
    ContractResponseDto toDto(Contract c);
}
