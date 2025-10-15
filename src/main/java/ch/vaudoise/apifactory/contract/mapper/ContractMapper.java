package ch.vaudoise.apifactory.contract.mapper;

import ch.vaudoise.apifactory.contract.domain.Contract;
import ch.vaudoise.apifactory.contract.dto.ContractResponseDto;
import org.mapstruct.*;
/**
 * Maps {@code Contract} entities to DTOs and vice versa where needed.
 * <p>
 * Includes {@code clientId} projection from the {@code Contract.client} relationship.
 */

@Mapper(componentModel = "spring")
public interface ContractMapper {
    @Mapping(target = "clientId", source = "client.id")
    ContractResponseDto toDto(Contract c);
}
