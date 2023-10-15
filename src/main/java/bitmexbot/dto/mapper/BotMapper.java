package bitmexbot.dto.mapper;

import bitmexbot.dto.BotDto;
import bitmexbot.entity.BotEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {BotDataMapper.class, BotOrderMapper.class})
public interface BotMapper {
    @Mappings({
            @Mapping(source = "botDataEntity", target = "botDataDto"),
            @Mapping(source = "botOrderEntities", target = "botOrderDtoSet")
    })
    BotDto toDto(BotEntity botEntity);

    List<BotDto> toDtoList(List<BotEntity> botEntityList);
}
