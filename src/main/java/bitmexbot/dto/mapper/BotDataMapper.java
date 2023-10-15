package bitmexbot.dto.mapper;

import bitmexbot.dto.BotDataDto;
import bitmexbot.entity.BotDataEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BotDataMapper {
    BotDataDto toDto(BotDataEntity botDataEntity);
}
