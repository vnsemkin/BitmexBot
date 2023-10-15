package bitmexbot.dto.mapper;

import bitmexbot.dto.BotOrderDto;
import bitmexbot.entity.BotOrderEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BotOrderMapper {
    BotOrderDto toDto(BotOrderEntity botOrderEntity);
}
